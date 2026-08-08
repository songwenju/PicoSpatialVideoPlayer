pluginManagement {
    repositories {
        maven("https://maven.byted.org/repository/android_public")
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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.byted.org/repository/android_public")
        google()
        mavenCentral()
    }
}

rootProject.name = "DestinationVideo"
include(":app")
 
