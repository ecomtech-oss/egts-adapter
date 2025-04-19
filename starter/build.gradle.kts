dependencies {
    kapt(libs.spring.boot.configuration.processor)

    implementation(libs.spring.boot.framework)
    implementation(libs.spring.boot.autoconfigure)
    implementation(libs.spring.boot.configuration.processor)
    api(project(":adapter-library"))

    testImplementation(libs.spring.boot.starter.test)
}