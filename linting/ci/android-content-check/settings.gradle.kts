// FRAM — Harnais de compilation pour les skills Android
//
// Ne fait PAS partie du framework livré aux consommateurs : sert uniquement à
// compiler les vrais skills/*/android-compose/patterns.kt et
// skills/*/tests/android-tests.kt contre un vrai SDK Compose en CI, afin de
// détecter les régressions de contenu (imports manquants, API inexistantes,
// détecteurs de test cassés, etc.) — voir app/build.gradle.kts pour le mapping
// des source sets vers skills/ (aucune copie de fichier n'est faite).

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "fram-android-content-check"
include(":app")
