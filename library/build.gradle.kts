plugins {
    id("com.android.library")
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
    alias(uuBuild.plugins.kotlin.serialization)
    alias(uuBuild.plugins.uu.library)
    alias(uuBuild.plugins.uu.android.test)
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.security.crypto)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.uu.test.ktx)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.uu.test.ktx)
    androidTestImplementation(libs.uu.test.instrumented.ktx)
    androidTestImplementation(libs.kotlin.test)
}

android {
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}
