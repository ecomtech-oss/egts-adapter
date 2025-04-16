description = "Library and Sping Boot starter for EGTS packets encoding-decoding"

plugins {
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlin)
    `maven-publish`
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")

    tasks.register("sourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {

                artifactId = "egts-adapter-${project.name}"
                artifact(tasks.named("sourcesJar").get())
                from(components["java"])
                pom {
                    name.set("EGTS Adapter: ${project.name}")
                    description.set(project.description ?: "EGTS encoding/decoding utilities")
                    url.set("https://https://github.com/ecomtech-oss/egts-adapter")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                }
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        finalizedBy(tasks.named("jacocoTestReport"))
    }

}
