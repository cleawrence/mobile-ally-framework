import SwiftUI

// =============================================================================
// SKILL-09 — Formulaires — Patterns SwiftUI
// FRAM : Framework Référence Accessibilité Mobile — v1.0
// Critères RAAM 1.1 : 9.1 à 9.6
// =============================================================================

// MARK: - 1. TEXTFIELD AVEC LABEL [A] — Critères 9.1, 9.2

struct TextFieldLabelPatterns: View {
    @State private var firstName = ""
    @State private var email = ""

    // ✅ Bon — Label visible et persistant avec VStack
    var goodLabeledField: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Prénom")
                .font(.caption)
                .foregroundStyle(.secondary)
            TextField("ex. Marie", text: $firstName)
                .accessibilityLabel("Prénom")
                // VoiceOver avant saisie : "Prénom, champ de saisie, vide"
                // VoiceOver pendant saisie : "Prénom, Marie"
        }
        .padding()
    }

    // ✅ Bon — Form natif SwiftUI (label intégré, visible et persistant)
    var nativeFormField: some View {
        Form {
            LabeledContent("Prénom") {
                TextField("ex. Marie", text: $firstName)
            }
            // VoiceOver : "Prénom, champ de saisie" — label persistant
        }
    }

    // ❌ Mauvais — Placeholder seul (disparaît à la saisie) — VIOLATION [A] 9.1
    var badPlaceholderOnly: some View {
        TextField("Prénom", text: $firstName)
        // VoiceOver avant saisie : "Prénom, champ de saisie"
        // VoiceOver après saisie : "Marie, champ de saisie" ← le label a disparu !
        // L'utilisateur ne sait plus à quoi correspond ce champ
    }

    // ❌ Mauvais — Label non pertinent — VIOLATION [A] 9.2
    var badGenericLabel: some View {
        VStack {
            Text("Champ 1")
            TextField("Saisir", text: $email)
        }
        // VoiceOver : "Champ 1, saisir" — incompréhensible
    }

    // ✅ Bon — accessibilityLabel consolidé pour champ avec icône
    var fieldWithLeadingIcon: some View {
        HStack {
            Image(systemName: "envelope")
                .accessibilityHidden(true) // [A] icône décorative
            TextField("Adresse email", text: $email)
                .accessibilityLabel("Adresse email")
                .keyboardType(.emailAddress)
        }
        // VoiceOver : "Adresse email, champ de saisie" (icône ignorée)
    }

    var body: some View {
        VStack(spacing: 16) {
            goodLabeledField
            nativeFormField
            badPlaceholderOnly
            badGenericLabel
            fieldWithLeadingIcon
        }
    }
}

// MARK: - 2. CHAMPS OBLIGATOIRES [A] — Critère 9.3

struct RequiredFieldPatterns: View {
    @State private var lastName = ""
    @State private var email = ""
    @State private var phone = ""

    // ✅ Bon — mention "obligatoire" dans le label accessible
    var requiredFieldInLabel: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 4) {
                Text("Nom de famille")
                Text("*")
                    .foregroundStyle(.red)
                    .accessibilityHidden(true) // Astérisque visuel seulement
            }
            TextField("Nom de famille", text: $lastName)
                .accessibilityLabel("Nom de famille, obligatoire")
                // VoiceOver : "Nom de famille, obligatoire, champ de saisie"
        }
        .padding()
    }

    // ✅ Bon — légende globale expliquant l'astérisque
    var globalLegendPattern: some View {
        VStack(alignment: .leading) {
            Text("Les champs marqués * sont obligatoires")
                .font(.caption)
                .foregroundStyle(.secondary)

            VStack(alignment: .leading, spacing: 12) {
                requiredFieldInLabel
                // Autres champs...
            }
        }
    }

    // ❌ Mauvais — astérisque seul, sans explication — VIOLATION [A] 9.3
    var badAsteriskOnly: some View {
        VStack(alignment: .leading) {
            HStack {
                Text("Email")
                Text("*").foregroundStyle(.red) // L'astérisque est lu par VoiceOver !
                // VoiceOver : "Email astérisque, champ de saisie" — confus
            }
            TextField("Email", text: $email)
        }
    }

    // ✅ Bon — astérisque masqué, obligation dans le label
    var correctAsteriskPattern: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 2) {
                Text("Email")
                Text("*").foregroundStyle(.red).accessibilityHidden(true)
            }
            TextField("Email", text: $email)
                .accessibilityLabel("Email, champ obligatoire")
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            globalLegendPattern
            badAsteriskOnly
            correctAsteriskPattern
        }
    }
}

// MARK: - 3. MESSAGES D'ERREUR [A] — Critère 9.4

struct ErrorMessagePatterns: View {
    @State private var email = ""
    @State private var emailError: String? = nil
    @State private var phone = ""
    @State private var phoneError: String? = nil

    // ✅ Bon — erreur textuelle visible + annonce VoiceOver
    var goodEmailField: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Adresse email")
                .font(.caption)
                .foregroundStyle(.secondary)

            TextField("ex. prenom@domaine.fr", text: $email)
                .keyboardType(.emailAddress)
                // Intégrer l'erreur dans accessibilityValue
                .accessibilityLabel("Adresse email" + (emailError != nil ? ", en erreur" : ""))
                .accessibilityValue(email.isEmpty ? "vide" : email)
                .accessibilityHint(emailError ?? "Saisir votre adresse email")
                .padding(8)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(emailError != nil ? Color.red : Color.gray.opacity(0.5), lineWidth: 1)
                )

            // ✅ Message d'erreur texte visible (pas seulement la bordure rouge)
            if let error = emailError {
                HStack(spacing: 4) {
                    Image(systemName: "exclamationmark.circle.fill")
                        .foregroundStyle(.red)
                        .accessibilityHidden(true)
                    Text(error)
                        .font(.caption)
                        .foregroundStyle(.red)
                }
                .accessibilityHidden(true) // L'info est déjà dans accessibilityHint du TextField
            }
        }
        .onChange(of: emailError) { _, newError in
            // ✅ Annonce vocale quand l'erreur apparaît
            if let error = newError {
                UIAccessibility.post(
                    notification: .announcement,
                    argument: "Erreur dans le champ Email : \(error)"
                )
            }
        }
    }

    // ❌ Mauvais — erreur seulement visuelle — VIOLATION [A] 9.4
    var badVisualOnlyError: some View {
        TextField("Email", text: $email)
            .padding(8)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(emailError != nil ? Color.red : Color.gray, lineWidth: 2)
                // Uniquement une bordure rouge → inaccessible aux utilisateurs aveugles
                // ET aux utilisateurs daltoniens
            )
        // VIOLATION [A] 9.4 : l'info d'erreur est portée uniquement par la couleur
    }

    // ❌ Mauvais — message d'erreur générique — VIOLATION [A] 9.4
    var badGenericErrorMessage: some View {
        VStack {
            TextField("Email", text: $email)
            if emailError != nil {
                Text("Erreur") // Trop vague — que doit corriger l'utilisateur ?
                    .foregroundStyle(.red)
            }
        }
    }

    // ✅ Bon — liste d'erreurs après soumission avec focus
    @FocusState private var isFirstErrorFocused: Bool

    var errorSummaryPattern: some View {
        VStack(alignment: .leading, spacing: 16) {
            if emailError != nil || phoneError != nil {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Veuillez corriger les erreurs suivantes :")
                        .font(.headline)
                        .foregroundStyle(.red)
                        // ✅ Ce titre reçoit le focus après soumission
                        .focused($isFirstErrorFocused)
                        .accessibilityAddTraits(.isHeader)

                    if let error = emailError {
                        Text("• Email : \(error)")
                    }
                    if let error = phoneError {
                        Text("• Téléphone : \(error)")
                    }
                }
                .padding()
                .background(Color.red.opacity(0.1))
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            goodEmailField
            badVisualOnlyError
            badGenericErrorMessage
            errorSummaryPattern
        }
    }
}

// MARK: - 4. AIDE À LA SAISIE [A] — Critère 9.5

struct InputHintPatterns: View {
    @State private var username = ""
    @State private var password = ""
    @State private var birthDate = ""
    @State private var iban = ""

    // ✅ Bon — hint décrivant le format attendu
    var usernameWithHint: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Nom d'utilisateur")
            TextField("ex. marie_dupont", text: $username)
                .accessibilityHint("Entre 5 et 20 caractères, lettres, chiffres et underscore uniquement")
                // VoiceOver : "Nom d'utilisateur, champ de saisie" (label)
                // Puis balayage vers le haut → lit le hint
        }
    }

    // ✅ Bon — texte d'aide visible + hint accessibilité (pas redondant)
    var fieldWithVisibleHelp: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Mot de passe")
            SecureField("Mot de passe", text: $password)
                .accessibilityHint("Minimum 8 caractères, une majuscule, un chiffre")
            Text("Minimum 8 caractères, une majuscule, un chiffre")
                .font(.caption)
                .foregroundStyle(.secondary)
                .accessibilityHidden(true) // Déjà dans le hint — éviter la double lecture
        }
    }

    // ✅ Bon — placeholder d'exemple de format (complète le label, ne le remplace pas)
    var dateFieldWithFormat: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Date de naissance")
            TextField("JJ/MM/AAAA", text: $birthDate)
                .accessibilityLabel("Date de naissance")
                .accessibilityHint("Format jour/mois/année, exemple : 24/07/1990")
                .keyboardType(.numberPad)
        }
    }

    // ✅ Bon — champ IBAN avec aide et format
    var ibanFieldWithHelp: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("IBAN")
            TextField("FR76 0000 0000 0000 0000 0000 000", text: $iban)
                .accessibilityLabel("IBAN")
                .accessibilityHint("Code bancaire international. Exemple : FR76 3000 1007 9400 0000 0000 000")
                .textContentType(.none)
                .keyboardType(.asciiCapable)
                .autocorrectionDisabled()
                .textInputAutocapitalization(.characters)
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            usernameWithHint
            fieldWithVisibleHelp
            dateFieldWithFormat
            ibanFieldWithHelp
        }
    }
}

// MARK: - 5. SECUREFIELD — MOT DE PASSE [A]

struct PasswordFieldPattern: View {
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var isVisible = false

    // ✅ Bon — SecureField avec bouton afficher/masquer accessible
    var accessiblePasswordField: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Mot de passe")

            HStack {
                if isVisible {
                    TextField("Mot de passe", text: $password)
                        .accessibilityLabel("Mot de passe")
                        .textContentType(.password)
                } else {
                    SecureField("Mot de passe", text: $password)
                        .accessibilityLabel("Mot de passe")
                        .textContentType(.password)
                }

                Button(action: { isVisible.toggle() }) {
                    Image(systemName: isVisible ? "eye.slash" : "eye")
                        .foregroundStyle(.secondary)
                }
                // ✅ Label du bouton change selon l'état
                .accessibilityLabel(isVisible ? "Masquer le mot de passe" : "Afficher le mot de passe")
                // VoiceOver : "Afficher le mot de passe, bouton"
                // Après tap : "Masquer le mot de passe, bouton"
            }
            .padding(8)
            .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.gray.opacity(0.5)))

            Text("Minimum 8 caractères, une majuscule, un chiffre")
                .font(.caption)
                .foregroundStyle(.secondary)
        }
    }

    // ✅ Bon — confirmation de mot de passe avec label distinct
    var confirmPasswordField: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Confirmer le mot de passe")
            SecureField("Confirmer le mot de passe", text: $confirmPassword)
                .accessibilityLabel("Confirmer le mot de passe")
                .textContentType(.password)
                // Label DISTINCT de "Mot de passe" pour éviter la confusion [A] 9.2
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            accessiblePasswordField
            confirmPasswordField
        }
    }
}

// MARK: - 6. PICKER / SELECT / DATEPICKER [A] — Critère 9.1

struct PickerPatterns: View {
    @State private var selectedCountry = "France"
    @State private var selectedDelivery = "Standard"
    @State private var selectedDate = Date()

    let countries = ["France", "Belgique", "Suisse", "Canada"]
    let deliveries = ["Standard (3-5 jours)", "Express (24h)", "Retrait en magasin"]

    // ✅ Bon — Picker avec label décrivant la sélection
    var countryPicker: some View {
        VStack(alignment: .leading) {
            Text("Pays de livraison")
            Picker("Pays de livraison", selection: $selectedCountry) {
                ForEach(countries, id: \.self) { Text($0).tag($0) }
            }
            // pickerStyle par défaut → VoiceOver : "Pays de livraison, France, menu déroulant"
        }
    }

    // ✅ Bon — Picker dans Form (navigation-style)
    var formPicker: some View {
        Form {
            Section("Expédition") {
                Picker("Mode de livraison", selection: $selectedDelivery) {
                    ForEach(deliveries, id: \.self) { Text($0).tag($0) }
                }
                // VoiceOver : "Mode de livraison, Standard (3-5 jours)"
            }
        }
    }

    // ✅ Bon — DatePicker avec label accessible
    var datePicker: some View {
        VStack(alignment: .leading) {
            Text("Date de livraison souhaitée")
            DatePicker(
                "Date de livraison souhaitée",
                selection: $selectedDate,
                in: Date()...,
                displayedComponents: .date
            )
            .datePickerStyle(.compact)
            // VoiceOver : "Date de livraison souhaitée, 24 juillet 2026, sélecteur de date"
            .labelsHidden() // si label visible déjà dans le VStack
            .accessibilityLabel("Date de livraison souhaitée")
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            countryPicker
            formPicker
            datePicker
        }
    }
}

// MARK: - 7. SAISIE AUTOMATIQUE [AA] — Critère 9.6

struct AutocompletePatterns: View {
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var email = ""
    @State private var phone = ""
    @State private var address = ""
    @State private var postalCode = ""
    @State private var city = ""
    @State private var password = ""
    @State private var newPassword = ""
    @State private var website = ""
    @State private var creditCard = ""

    // ✅ Tableau complet des textContentType et keyboardType recommandés [AA]
    var autocompleteForm: some View {
        Form {
            Section("Identité") {
                // Prénom
                TextField("Prénom", text: $firstName)
                    .textContentType(.givenName)        // [AA] autocomplete prénom
                    .keyboardType(.default)

                // Nom de famille
                TextField("Nom", text: $lastName)
                    .textContentType(.familyName)       // [AA] autocomplete nom
                    .keyboardType(.default)
            }

            Section("Coordonnées") {
                // Email
                TextField("Email", text: $email)
                    .textContentType(.emailAddress)     // [AA] autocomplete email
                    .keyboardType(.emailAddress)        // [AA] clavier email (avec @)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)

                // Téléphone
                TextField("Téléphone", text: $phone)
                    .textContentType(.telephoneNumber)  // [AA] autocomplete téléphone
                    .keyboardType(.phonePad)            // [AA] clavier numérique téléphone

                // Site web
                TextField("Site web", text: $website)
                    .textContentType(.URL)              // [AA] autocomplete URL
                    .keyboardType(.URL)                 // [AA] clavier URL (avec .)
            }

            Section("Adresse") {
                // Adresse ligne 1
                TextField("Rue et numéro", text: $address)
                    .textContentType(.streetAddressLine1) // [AA]
                    .keyboardType(.default)

                // Code postal
                TextField("Code postal", text: $postalCode)
                    .textContentType(.postalCode)       // [AA]
                    .keyboardType(.numberPad)           // [AA] clavier numérique

                // Ville
                TextField("Ville", text: $city)
                    .textContentType(.addressCity)      // [AA]
                    .keyboardType(.default)
            }

            Section("Sécurité") {
                // Mot de passe existant
                SecureField("Mot de passe actuel", text: $password)
                    .textContentType(.password)         // [AA] → propose le trousseau iOS

                // Nouveau mot de passe
                SecureField("Nouveau mot de passe", text: $newPassword)
                    .textContentType(.newPassword)      // [AA] → iOS génère un mot de passe fort
            }
        }
    }

    var body: some View {
        autocompleteForm
    }
}

// MARK: - 8. REGROUPEMENT DE CHAMPS [A] — Critère 9.1

struct FieldGroupingPatterns: View {
    @State private var street = ""
    @State private var city = ""
    @State private var postalCode = ""
    @State private var country = "France"

    // ✅ Bon — Section avec en-tête comme label de groupe
    var addressGroup: some View {
        Form {
            Section {
                TextField("Numéro et rue", text: $street)
                    .textContentType(.streetAddressLine1)
                TextField("Ville", text: $city)
                    .textContentType(.addressCity)
                TextField("Code postal", text: $postalCode)
                    .textContentType(.postalCode)
                    .keyboardType(.numberPad)
                Picker("Pays", selection: $country) {
                    Text("France").tag("France")
                    Text("Belgique").tag("Belgique")
                }
            } header: {
                Text("Adresse de facturation")
                    .accessibilityAddTraits(.isHeader)
                    // VoiceOver annonce "Adresse de facturation, en-tête" avant les champs
            }
        }
    }

    var body: some View {
        addressGroup
    }
}

// MARK: - 9. FORMULAIRE DE CONNEXION COMPLET [A + AA]

struct AccessibleLoginForm: View {
    @State private var email = ""
    @State private var password = ""
    @State private var isPasswordVisible = false
    @State private var emailError: String? = nil
    @State private var passwordError: String? = nil
    @State private var isSubmitting = false
    @FocusState private var focusedField: LoginField?

    enum LoginField { case email, password }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text("Connexion")
                    .font(.largeTitle.bold())
                    .accessibilityAddTraits(.isHeader)

                // Email
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 2) {
                        Text("Email")
                        Text("*").foregroundStyle(.red).accessibilityHidden(true)
                    }
                    TextField("ex. prenom@domaine.fr", text: $email)
                        .accessibilityLabel("Email, champ obligatoire")
                        .accessibilityHint(emailError ?? "")
                        .textContentType(.emailAddress)                 // [AA]
                        .keyboardType(.emailAddress)                    // [AA]
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .focused($focusedField, equals: .email)
                        .padding(10)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(emailError != nil ? Color.red : Color.gray.opacity(0.5))
                        )

                    if let error = emailError {
                        HStack {
                            Image(systemName: "exclamationmark.circle.fill")
                                .foregroundStyle(.red).accessibilityHidden(true)
                            Text(error).font(.caption).foregroundStyle(.red)
                        }
                        .accessibilityHidden(true) // Géré dans accessibilityHint
                    }
                }

                // Mot de passe
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 2) {
                        Text("Mot de passe")
                        Text("*").foregroundStyle(.red).accessibilityHidden(true)
                    }
                    HStack {
                        Group {
                            if isPasswordVisible {
                                TextField("Mot de passe", text: $password)
                            } else {
                                SecureField("Mot de passe", text: $password)
                            }
                        }
                        .accessibilityLabel("Mot de passe, champ obligatoire")
                        .textContentType(.password)                      // [AA]
                        .focused($focusedField, equals: .password)

                        Button {
                            isPasswordVisible.toggle()
                        } label: {
                            Image(systemName: isPasswordVisible ? "eye.slash" : "eye")
                        }
                        .accessibilityLabel(isPasswordVisible ? "Masquer le mot de passe" : "Afficher le mot de passe")
                    }
                    .padding(10)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(passwordError != nil ? Color.red : Color.gray.opacity(0.5))
                    )

                    if let error = passwordError {
                        Text(error).font(.caption).foregroundStyle(.red)
                            .accessibilityHidden(true)
                    }
                }

                // Bouton de soumission
                Button {
                    validateAndSubmit()
                } label: {
                    HStack {
                        if isSubmitting {
                            ProgressView().tint(.white)
                        }
                        Text("Se connecter")
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(isFormValid ? Color.blue : Color.gray)
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .disabled(!isFormValid)
                // VoiceOver : "Se connecter, estompé, bouton" si formulaire incomplet
            }
            .padding()
        }
    }

    var isFormValid: Bool { !email.isEmpty && !password.isEmpty }

    func validateAndSubmit() {
        emailError = nil
        passwordError = nil

        if email.isEmpty {
            emailError = "L'adresse email est obligatoire"
        } else if !email.contains("@") {
            emailError = "L'adresse email doit contenir un @. Exemple : prenom@domaine.fr"
        }

        if password.isEmpty {
            passwordError = "Le mot de passe est obligatoire"
        }

        // ✅ Annoncer les erreurs et replacer le focus
        if emailError != nil {
            UIAccessibility.post(
                notification: .announcement,
                argument: "Erreur dans le formulaire : \(emailError!)"
            )
            focusedField = .email
        } else if passwordError != nil {
            UIAccessibility.post(
                notification: .announcement,
                argument: "Erreur : \(passwordError!)"
            )
            focusedField = .password
        } else {
            isSubmitting = true
            // Appel réseau...
        }
    }
}
