rootProject.name = "egts-adapter"

include("library")
include("starter")

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
