#!/usr/bin/env python3
"""FRAM — Valide la syntaxe de chaque diagramme Mermaid dans docs/.

mkdocs build --strict ne détecte PAS les diagrammes Mermaid cassés : le
fenced block ```mermaid est juste transformé en <pre class="mermaid">, et
c'est mermaid.js qui parse/rend le diagramme côté client, dans le navigateur.
Une erreur de syntaxe Mermaid compile donc un site parfaitement "valide" du
point de vue de MkDocs, mais casse silencieusement l'affichage pour de vrai.

Extrait chaque bloc ```mermaid des fichiers docs/**/*.md et le fait valider
par `mmdc` (@mermaid-js/mermaid-cli), qui utilise le vrai moteur mermaid.js
dans un Chromium headless — donc les mêmes erreurs que verrait un visiteur.
"""

import glob
import re
import subprocess
import sys
import tempfile
from pathlib import Path

MERMAID_FENCE = re.compile(r"```mermaid\n(.*?)\n```", re.DOTALL)


def find_diagrams():
    for path in sorted(glob.glob("docs/**/*.md", recursive=True)):
        text = Path(path).read_text(encoding="utf-8")
        for i, match in enumerate(MERMAID_FENCE.finditer(text), start=1):
            yield path, i, match.group(1)


def main():
    diagrams = list(find_diagrams())
    if not diagrams:
        print("Aucun diagramme Mermaid trouvé dans docs/.")
        return 0

    failures = []
    with tempfile.TemporaryDirectory() as tmp:
        for path, index, source in diagrams:
            mmd_file = Path(tmp) / f"diagram.mmd"
            svg_file = Path(tmp) / f"diagram.svg"
            mmd_file.write_text(source, encoding="utf-8")

            result = subprocess.run(
                ["npx", "--yes", "@mermaid-js/mermaid-cli", "-i", str(mmd_file), "-o", str(svg_file)],
                capture_output=True,
                text=True,
            )

            label = f"{path} (diagramme #{index})"
            if result.returncode != 0:
                failures.append((label, result.stderr.strip()))
                print(f"❌ {label}")
            else:
                print(f"✅ {label}")

    print()
    if failures:
        print(f"::error::{len(failures)} diagramme(s) Mermaid invalide(s) sur {len(diagrams)}")
        for label, stderr in failures:
            print(f"\n--- {label} ---")
            print(stderr[-1500:])
        return 1

    print(f"✅ Les {len(diagrams)} diagrammes Mermaid sont valides.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
