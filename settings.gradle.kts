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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://artifact.bytedance.com/repository/Volcengine") {
            name = "VolcengineMaven"
            content {
                includeGroupByRegex("com\\.pico\\.spatial.*")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "PicoSpatialVideoPlayer"
include(":app")
 
