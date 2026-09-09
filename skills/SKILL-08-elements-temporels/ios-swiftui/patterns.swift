import SwiftUI
import AVKit

// =============================================================================
// SKILL-08 — Éléments Temporels — Patterns SwiftUI
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 8.1 à 8.6
// =============================================================================

// MARK: - 1. REDUCE MOTION [A] — Critère 8.6

struct ReduceMotionPatterns: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var isExpanded = false
    @State private var showCard = false

    var body: some View {
        VStack(spacing: 24) {
            motionSafeTransitionExample
            motionSafeCarouselButton
        }
    }

    // ✅ Bon — transition adaptée selon Reduce Motion
    var motionSafeTransitionExample: some View {
        VStack {
            if showCard {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.blue.opacity(0.2))
                    .frame(height: 80)
                    .overlay(Text("Contenu apparu").foregroundStyle(.primary))
                    // ✅ fade si reduceMotion, sinon slide + spring
                    .transition(reduceMotion
                        ? .opacity                                  // ✅ Sûr pour troubles vestibulaires
                        : .move(edge: .bottom).combined(with: .opacity) // Normal
                    )
            }

            Button(showCard ? "Masquer" : "Afficher") {
                withAnimation(motionSafeAnimation) {
                    showCard.toggle()
                }
            }
        }
    }

    // ✅ Bon — animation conditionnelle (spring vs linear fade)
    private var motionSafeAnimation: Animation {
        reduceMotion
            ? .linear(duration: 0.2)    // ✅ Fade rapide — pas de mouvement physique
            : .spring(response: 0.5, dampingFraction: 0.6) // Spring normal
    }

    // ❌ Mauvais — spring intense toujours actif — VIOLATION [A] 8.6
    var badSpringAlwaysActive: some View {
        Button("Ouvrir") {
            withAnimation(.spring(response: 0.3, dampingFraction: 0.2)) {
                // Rebond exagéré — pas de respect de Reduce Motion
                isExpanded.toggle()
            }
        }
        // VIOLATION [A] 8.6 : ne vérifie pas reduceMotion
    }

    // ✅ Fix — wrapper utilitaire motionSafe
    var motionSafeCarouselButton: some View {
        Button("Action") {
            motionSafeWithAnimation {
                isExpanded.toggle()
            }
        }
    }

    private func motionSafeWithAnimation(_ action: @escaping () -> Void) {
        withAnimation(reduceMotion ? .linear(duration: 0.15) : .spring()) {
            action()
        }
    }
}

// ✅ Bon — ViewModifier utilitaire Reduce Motion
struct MotionSafeTransitionModifier: ViewModifier {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    let normalTransition: AnyTransition

    func body(content: Content) -> some View {
        content.transition(reduceMotion ? .opacity : normalTransition)
    }
}

extension View {
    /// Applique une transition sûre : fade si Reduce Motion, sinon la transition normale
    func motionSafeTransition(_ normal: AnyTransition = .move(edge: .trailing).combined(with: .opacity)) -> some View {
        modifier(MotionSafeTransitionModifier(normalTransition: normal))
    }
}

// MARK: - 2. REDUCE MOTION — Lottie / GIF [A] — Critère 8.6

struct AnimatedContentPatterns: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    // ✅ Bon — Lottie/GIF remplacé par image statique si Reduce Motion
    var lottieOrStaticImage: some View {
        Group {
            if reduceMotion {
                // ✅ Image statique — aucun mouvement
                Image("celebration_static")
                    .resizable()
                    .scaledToFit()
                    .frame(height: 150)
                    .accessibilityLabel("Animation de célébration")
            } else {
                // Animation Lottie normale
                // LottieView(animation: .named("celebration")) // Lottie library
                Image("celebration_static") // placeholder
                    .resizable()
                    .scaledToFit()
                    .frame(height: 150)
                    .accessibilityLabel("Animation de célébration")
            }
        }
    }

    // ✅ Bon — animation de pulsation sûre (opacité uniquement)
    @State private var opacity: Double = 1.0
    var pulsatingDot: some View {
        Circle()
            .fill(Color.red)
            .frame(width: 12, height: 12)
            .opacity(opacity)
            .onAppear {
                if !reduceMotion {
                    // Pulsation uniquement si Reduce Motion désactivé
                    withAnimation(.easeInOut(duration: 1.0).repeatForever()) {
                        opacity = 0.3
                    }
                }
            }
            .accessibilityLabel("Notification active")
    }

    // ❌ Mauvais — parallaxe jamais désactivé — VIOLATION [A] 8.6
    var badParallax: some View {
        ScrollView {
            // Effet parallaxe sur la bannière hero — non conditionnel
            // GeometryReader { geometry in Image("hero").offset(y: geometry.frame(in: .global).minY * 0.5) }
            // VIOLATION [A] 8.6 : parallaxe sans respect de Reduce Motion
            Text("Contenu")
        }
    }

    var body: some View {
        VStack {
            lottieOrStaticImage
            pulsatingDot
        }
    }
}

// MARK: - 3. VIDEOPLAYER AVEC SOUS-TITRES [A] — Critère 8.2

struct AccessibleVideoPlayer: View {
    @State private var player: AVPlayer
    @State private var isPlaying = false
    @State private var showTranscript = false
    @State private var showAudioDescription = false

    init() {
        let url = URL(string: "https://example.com/video.mp4")!
        let playerItem = AVPlayerItem(url: url)
        self.player = AVPlayer(playerItem: playerItem)
    }

    var body: some View {
        VStack(spacing: 16) {

            // Lecteur vidéo natif avec contrôles intégrés
            VideoPlayer(player: player)
                .frame(height: 220)
                .accessibilityLabel("Lecteur vidéo : Présentation du projet")
                // [A] VideoPlayer inclut nativement les contrôles play/pause/scrubbing
                // Les sous-titres système (CC activés dans Réglages) s'affichent automatiquement

            // Contrôles d'accessibilité supplémentaires
            HStack(spacing: 16) {

                // ✅ Bouton Play/Pause avec label adaptatif [A]
                Button {
                    isPlaying.toggle()
                    isPlaying ? player.play() : player.pause()
                } label: {
                    Label(
                        isPlaying ? "Mettre en pause" : "Lire",
                        systemImage: isPlaying ? "pause.fill" : "play.fill"
                    )
                }
                .accessibilityLabel(isPlaying ? "Mettre la vidéo en pause" : "Lire la vidéo")
                // ✅ Label change selon l'état [A] 8.5

                // ✅ Bouton audiodescription [AA]
                Button {
                    toggleAudioDescription()
                } label: {
                    Label(
                        showAudioDescription ? "AD activée" : "Audiodescription",
                        systemImage: "ear"
                    )
                }
                .accessibilityLabel(
                    showAudioDescription
                        ? "Désactiver l'audiodescription"
                        : "Activer l'audiodescription"
                )

                // ✅ Bouton transcription [A]
                Button {
                    showTranscript = true
                } label: {
                    Label("Transcription", systemImage: "text.alignleft")
                }
                .accessibilityLabel("Lire la transcription de la vidéo")
                .accessibilityHint("Ouvre un document texte avec tout le contenu parlé de la vidéo")
            }
        }
        .sheet(isPresented: $showTranscript) {
            TranscriptView(
                title: "Présentation du projet",
                transcript: """
                [00:00] Bonjour et bienvenue dans cette présentation.
                [00:04] [musique d'introduction]
                [00:08] Aujourd'hui, nous allons voir comment améliorer l'accessibilité mobile.
                [00:15] Marie (Cheffe de projet) : Voici les trois axes principaux.
                [00:20] [graphique affiché à l'écran montrant les métriques]
                """
            )
        }
    }

    private func toggleAudioDescription() {
        guard let asset = player.currentItem?.asset else { return }
        Task {
            if let group = try? await asset.loadMediaSelectionGroup(for: .audible) {
                let adOptions = AVMediaSelectionGroup.mediaSelectionOptions(
                    from: group.options,
                    withMediaCharacteristics: [.describesVideoForAccessibility]
                )
                if let adTrack = adOptions.first {
                    player.currentItem?.select(adTrack, in: group)
                    showAudioDescription = true
                }
            }
        }
    }
}

// MARK: - 4. TRANSCRIPTION [A] — Critère 8.1

struct TranscriptView: View {
    let title: String
    let transcript: String
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            ScrollView {
                Text(transcript)
                    .font(.body)
                    .padding()
                    // ✅ Texte natif — taille ajustable via Dynamic Type
                    // ✅ VoiceOver peut lire la transcription entière
            }
            .navigationTitle("Transcription : \(title)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Fermer") { dismiss() }
                        .accessibilityLabel("Fermer la transcription")
                }
            }
        }
    }
}

// MARK: - 5. LECTEUR AUDIO AVEC TRANSCRIPTION [A] — Critère 8.1

struct AccessibleAudioPlayer: View {
    @State private var isPlaying = false
    @State private var progress: Double = 0.3
    @State private var showTranscript = false
    let duration: TimeInterval = 1845  // 30:45

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {

            Text("Episode 42 — L'accessibilité mobile en 2026")
                .font(.headline)
                .accessibilityAddTraits(.isHeader)

            Text("Durée : 30 min 45 sec")
                .font(.caption)
                .foregroundStyle(.secondary)

            // Barre de progression
            ProgressView(value: progress)
                .accessibilityLabel("Progression de lecture")
                .accessibilityValue("\(Int(progress * 100))%")

            // Contrôles
            HStack {
                Button {
                    isPlaying.toggle()
                } label: {
                    Image(systemName: isPlaying ? "pause.circle.fill" : "play.circle.fill")
                        .font(.title)
                }
                .accessibilityLabel(isPlaying ? "Mettre en pause le podcast" : "Lire le podcast")
                // ✅ Label adaptatif [A] 8.5

                Button {
                    // Reculer de 15 secondes
                } label: {
                    Image(systemName: "gobackward.15")
                }
                .accessibilityLabel("Reculer de 15 secondes")

                Button {
                    // Avancer de 30 secondes
                } label: {
                    Image(systemName: "goforward.30")
                }
                .accessibilityLabel("Avancer de 30 secondes")

                Spacer()

                // ✅ Bouton transcription obligatoire [A] 8.1
                Button {
                    showTranscript = true
                } label: {
                    Label("Transcription", systemImage: "text.alignleft")
                        .font(.caption)
                }
                .accessibilityLabel("Lire la transcription de l'épisode")
                .accessibilityHint("Ouvre le texte complet du podcast")
            }
        }
        .padding()
        .background(Color(UIColor.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .sheet(isPresented: $showTranscript) {
            TranscriptView(
                title: "Episode 42",
                transcript: "Présentateur : Bienvenue dans ce podcast sur l'accessibilité mobile..."
            )
        }
    }
}

// MARK: - 6. CAROUSEL AVEC AUTO-PLAY [A] — Critère 8.5

struct AccessibleAutoPlayCarousel: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var currentIndex = 0
    @State private var isAutoPlaying = true

    let slides = [
        ("Offre du jour", "50% sur tous les articles"),
        ("Nouveautés", "Découvrez notre collection printemps"),
        ("Livraison gratuite", "Pour toute commande > 50€"),
    ]

    // Timer auto-play
    let timer = Timer.publish(every: 5, on: .main, in: .common).autoconnect()

    var body: some View {
        VStack(spacing: 12) {

            // Slides
            TabView(selection: $currentIndex) {
                ForEach(0..<slides.count, id: \.self) { index in
                    VStack {
                        Text(slides[index].0).font(.headline)
                        Text(slides[index].1).font(.subheadline)
                    }
                    .frame(maxWidth: .infinity, maxHeight: 120)
                    .background(Color.blue.opacity(0.15))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .tag(index)
                    .accessibilityLabel("\(slides[index].0) : \(slides[index].1). Slide \(index + 1) sur \(slides.count)")
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .always))
            .frame(height: 120)

            // ✅ Bouton Pause obligatoire [A] 8.5
            HStack {
                Button {
                    isAutoPlaying.toggle()
                } label: {
                    Label(
                        isAutoPlaying ? "Mettre en pause le défilement" : "Reprendre le défilement",
                        systemImage: isAutoPlaying ? "pause.fill" : "play.fill"
                    )
                    .font(.caption)
                }
                .accessibilityLabel(
                    isAutoPlaying
                        ? "Mettre en pause le défilement automatique du carousel"
                        : "Reprendre le défilement automatique du carousel"
                )
                .buttonStyle(.bordered)

                Text("\(currentIndex + 1) / \(slides.count)")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                    .accessibilityHidden(true)
            }
        }
        .onReceive(timer) { _ in
            guard isAutoPlaying else { return }
            withAnimation(reduceMotion ? .linear(duration: 0.15) : .easeInOut) {
                // ✅ Reduce Motion appliqué aux transitions du carousel
                currentIndex = (currentIndex + 1) % slides.count
            }
        }
    }
}

// MARK: - 7. CONTENU CLIGNOTANT [A] — WCAG 2.3.1

// ✅ Bon — indicateur de chargement non-clignotant
struct SafeLoadingIndicator: View {
    var body: some View {
        VStack {
            // ✅ ProgressView iOS = rotation continue, pas de clignotement
            ProgressView()
                .accessibilityLabel("Chargement en cours")
                .progressViewStyle(.circular)

            // ✅ Barre de progression linéaire — pas de clignotement
            ProgressView(value: 0.6)
                .accessibilityLabel("Chargement : 60%")
        }
    }
}

// ❌ Mauvais — animation clignotante rapide — VIOLATION WCAG 2.3.1
// Ne pas implémenter un composant qui clignote > 3 fois par seconde :
// withAnimation(.easeInOut(duration: 0.1).repeatForever()) { opacity = opacity == 1 ? 0 : 1 }
// Fréquence 1/0.1 = 10 flashs/seconde → risque épilepsie photosensible
