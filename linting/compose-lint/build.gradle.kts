// FRAM — Module Lint Accessibilité Mobile
// build.gradle.kts — Ajouter ce module au projet Android pour activer le linting a11y
//
// Installation :
//   1. Copier ce dossier compose-lint/ dans votre projet Android
//   2. Ajouter dans settings.gradle.kts :
//        include(":fram-a11y-lint")
//        project(":fram-a11y-lint").projectDir = file("path/to/compose-lint")
//   3. Ajouter dans le build.gradle.kts du module app :
//        dependencies { lintChecks(project(":fram-a11y-lint")) }

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // API Lint Android — fournit Detector, Issue, IssueRegistry, etc.
    compileOnly("com.android.tools.lint:lint-api:31.5.0")
    compileOnly("com.android.tools.lint:lint-checks:31.5.0")

    // Tests
    testImplementation("com.android.tools.lint:lint-tests:31.5.0")
    testImplementation("com.android.tools.lint:lint:31.5.0")
    testImplementation("junit:junit:4.13.2")
}

// Enregistrer le registry Lint pour la découverte automatique
tasks.jar {
    manifest {
        attributes(
            "Lint-Registry-v2" to "com.fram.lint.A11yLintRegistry"
        )
    }
}
