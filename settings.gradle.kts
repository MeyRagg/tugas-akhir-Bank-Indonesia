pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Make sure this is present
        maven { url = uri("https://www.jitpack.io") } // Alternative URL if needed
    }
}

rootProject.name = "Bismillahirahmanirahim Tugas Akhir Lancar No Problem"
include(":app")