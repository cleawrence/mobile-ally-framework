// Compile les vrais fichiers skills/*/android-compose/patterns.kt (main) et
// skills/*/tests/android-tests.kt (androidTest) en place, sans les copier — voir
// la configuration des sourceSets plus bas.

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// linting/ci/android-content-check/app -> ../../../.. = racine du repo
val repoRoot = rootDir.parentFile.parentFile.parentFile
val skillsDir = File(repoRoot, "skills")

android {
    namespace = "com.fram.verify"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.fram.verify"
        minSdk = 24
        targetSdk = 37
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    val skillDirs: List<File> = skillsDir.listFiles { f -> f.isDirectory }?.toList() ?: emptyList()
    val patternsDirs = skillDirs.map { File(it, "android-compose") }.filter { it.exists() }
    // Le dossier tests/ contient aussi les *-ios-tests.swift, ignorés par le
    // compilateur Kotlin (seuls les .kt sont pris en compte).
    val testsDirs = skillDirs.map { File(it, "tests") }.filter { it.exists() }

    sourceSets {
        getByName("main") {
            kotlin.directories.addAll(patternsDirs.map { it.absolutePath })
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
        getByName("androidTest") {
            kotlin.directories.addAll(testsDirs.map { it.absolutePath })
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.activity:activity-compose:1.13.0")

    androidTestImplementation(platform("androidx.compose:compose-bom:2026.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
