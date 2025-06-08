pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${rootProject.extensions.findByName("androidGradlePlugin") ?: "8.2.2"}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extensions.findByName("kotlinAndroid") ?: "1.9.22"}")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "SnakeGameAndroid"
include(":app")
