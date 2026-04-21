pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Compose Compiler Gradle plugin required for Kotlin 2.x + Compose
        id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // MapLibre Android SDK repository
        maven("https://maplibre.org/maven/")
    }
}

rootProject.name = "Emergency App"
include(":app")
