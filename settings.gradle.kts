rootProject.name = "egts-adapter"

include("library")
include("starter")

project(":library").name = "adapter-library"
project(":starter").name = "adapter-starter"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
