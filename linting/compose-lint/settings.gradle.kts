// Ce settings.gradle.kts ne sert qu'à builder ce module de façon autonome (CI, tests
// locaux). Il n'est PAS nécessaire — ni recommandé — de le copier lorsqu'on inclut ce
// module comme sous-projet d'une vraie application Android (voir build.gradle.kts) :
// la version du plugin Kotlin doit alors venir du projet consommateur.
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.0.21"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "fram-a11y-lint"
