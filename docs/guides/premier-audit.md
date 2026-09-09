---
tags:
  - guides
  - audit
---

# Premier audit

Ce guide vous accompagne pour réaliser votre premier audit d'accessibilité avec FRAM.

## Choisir le niveau d'audit

| Type | Niveau | Durée estimée | Quand ? |
|---|---|---|---|
| **Audit simplifié** | `[A]` | ~2h par écran | Sprint review, PR review |
| **Audit complet** | `[AA]` | ~4h par écran | Release, mise en production |

!!! tip "Recommandation"
    Commencez toujours par un **audit simplifié [A]**. Il couvre les violations les plus critiques et est rapide à réaliser.

---

## Étape 1 — Préparer l'environnement

=== ":material-apple: iOS"

    1. Ouvrir l'app sur un **iPhone physique** (le simulateur ne supporte pas VoiceOver)
    2. Activer **VoiceOver** : *Réglages → Accessibilité → VoiceOver → ON*
    3. Ouvrir **Accessibility Inspector** dans Xcode : *Xcode → Open Developer Tool → Accessibility Inspector*

=== ":material-android: Android"

    1. Ouvrir l'app sur un **appareil physique** ou émulateur
    2. Activer **TalkBack** : *Paramètres → Accessibilité → TalkBack → ON*
    3. Installer **Accessibility Scanner** depuis le Play Store

---

## Étape 2 — Ouvrir la grille d'audit

1. Ouvrir `audit/grille-audit.html` dans un navigateur
2. Renseigner le **nom de l'application** et la **date**
3. Sélectionner le niveau : **Audit simplifié [A]** ou **Audit complet [AA]**

---

## Étape 3 — Tester avec le lecteur d'écran

Pour chaque écran de l'application, vérifier :

### Navigation (SKILL-10)

- [ ] VoiceOver/TalkBack annonce le **titre de l'écran** à l'arrivée
- [ ] L'ordre de lecture est **logique** (haut→bas, gauche→droite)
- [ ] Les modales **contraignent le focus** (on ne peut pas sortir)
- [ ] Le focus **revient au déclencheur** après fermeture d'une modale

### Images et icônes (SKILL-01)

- [ ] Les icônes **décoratives** sont ignorées par le lecteur d'écran
- [ ] Les icônes **informatives** ont un label pertinent
- [ ] Aucun label ne commence par "image de" ou "icône de"

### Composants interactifs (SKILL-05)

- [ ] Chaque **bouton** est annoncé avec son nom et son rôle
- [ ] Les **toggles** annoncent leur état (activé/désactivé)
- [ ] Le **double-tap** active correctement chaque élément

### Formulaires (SKILL-09)

- [ ] Chaque champ a un **label** annoncé par le lecteur d'écran
- [ ] Les champs **obligatoires** sont identifiés
- [ ] Les **messages d'erreur** sont annoncés automatiquement

---

## Étape 4 — Exporter le résultat

1. Dans la grille d'audit, cliquer **📥 Exporter JSON**
2. Le fichier JSON contient le score [A] et [AA] et chaque critère avec son statut
3. Archiver le rapport dans votre outil de gestion de projet

!!! success "Score cible"
    - **Audit simplifié [A]** : objectif ≥ 90%
    - **Audit complet [AA]** : objectif ≥ 75%

---

## Étape 5 — Corriger

Pour chaque violation, le skill correspondant fournit le **pattern de correction** :

1. Identifier le skill dans la grille (ex: critère 1.2 → SKILL-01)
2. Ouvrir la page du skill dans cette documentation
3. Copier le pattern ✅ Bon pour votre plateforme
4. Relancer le linting pour vérifier la correction
