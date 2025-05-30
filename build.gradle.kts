import com.vanniktech.maven.publish.SonatypeHost

description = "Library and Spring Boot starter for EGTS packets encoding-decoding"

plugins {
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlin)
    jacoco
    alias(libs.plugins.publish)
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "jacoco")

    apply(plugin = "com.vanniktech.maven.publish")

    group = "tech.ecom.egts"

    tasks {
        withType<JavaCompile> {
            targetCompatibility = "17"
            sourceCompatibility = "17"
        }

        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "17"
            }
        }

        withType<org.jetbrains.kotlin.gradle.tasks.KaptGenerateStubs> {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    tasks.register("testCoverageReport") {
        dependsOn("test", "jacocoTestReport")

        doLast {
            val jacocoReportFile = layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile
            if (!jacocoReportFile.exists()) {
                println("JaCoCo report file not found. Run tests first.")
                return@doLast
            }

            val doc = with(javax.xml.parsers.DocumentBuilderFactory.newInstance()) {
                setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                newDocumentBuilder().parse(jacocoReportFile)
            }

            val countersAttributes = doc.getElementsByTagName("counter").let { nodeList ->
                (0 until nodeList.length).map { nodeList.item(it) }
            }.map { it.attributes }

            var branchTotal = 0; var branchMissed = 0
            var lineTotal = 0; var lineMissed = 0
            var complexityTotal = 0; var complexityMissed = 0

            countersAttributes.onEach { attributes ->

                val type = attributes.getNamedItem("type").nodeValue
                val missed = attributes.getNamedItem("missed").nodeValue.toInt()
                val covered = attributes.getNamedItem("covered").nodeValue.toInt()

                when (type) {
                    "BRANCH" -> {
                        branchMissed = missed
                        branchTotal = missed + covered
                    }
                    "LINE" -> {
                        lineMissed = missed
                        lineTotal = missed + covered
                    }
                    "COMPLEXITY" -> {
                        complexityMissed = missed
                        complexityTotal = missed + covered
                    }
                }
            }

            val branchPercentage = if (branchTotal > 0) (branchTotal - branchMissed) * 100 / branchTotal else 0
            val linePercentage = if (lineTotal > 0) (lineTotal - lineMissed) * 100 / lineTotal else 0
            val complexityPercentage = if (complexityTotal > 0) (complexityTotal - complexityMissed) * 100 / complexityTotal else 0

            println("Branches coverage percentage: ${branchPercentage}%")
            println("Lines coverage percentage: ${linePercentage}%")
            println("Complexity: ${complexityMissed} missed of ${complexityTotal}. Coverage percentage: ${complexityPercentage}%")
        }
    }

    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        finalizedBy(tasks.named("jacocoTestReport"))
        tasks.named("jacocoTestReport").get().finalizedBy(tasks.named("testCoverageReport"))
    }

    mavenPublishing {
        coordinates(group.toString(), project.name, project.version.toString())
        pom {
            name.set("EGTS Adapter: ${project.name}")
            description.set(project.description ?: "EGTS encoding/decoding utilities")
            url.set("https://github.com/ecomtech-oss/egts-adapter")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    name.set("Ignat Nushtaev")
                    email.set("inushtaev@ecom.tech")
                    properties.set(mapOf("telegram" to "@ignat_nushtaev"))
                }
            }
            scm {
                connection.set("scm:git:https://github.com/ecomtech-oss/egts-adapter.git")
                developerConnection.set("scm:git:ssh://github.com/ecomtech-oss/egts-adapter.git")
                url.set("https://github.com/ecomtech-oss/egts-adapter")
            }
        }
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()
    }
}
