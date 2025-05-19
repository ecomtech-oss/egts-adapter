rootProject.name = "egts-adapter"

include("library")
include("starter")

project(":library").name = "adapter-library"
project(":starter").name = "adapter-starter"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
