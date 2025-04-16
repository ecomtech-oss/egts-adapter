
dependencies {
    // logging
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    // test
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockk)
}