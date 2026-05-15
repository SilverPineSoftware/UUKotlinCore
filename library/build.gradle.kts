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
    implementation(uuBuild.androidx.annotation)
    implementation(uuBuild.androidx.appcompat)
    implementation(uuBuild.kotlinx.serialization.json)
    implementation(libs.androidx.security.crypto)

    testImplementation(platform(uuBuild.junit.bom))
    testImplementation(uuBuild.junit.jupiter)
    testImplementation(uuBuild.junit.jupiter.api)
    testImplementation(uuBuild.junit.jupiter.engine)
    testImplementation(uuBuild.junit.jupiter.params)
    testRuntimeOnly(uuBuild.junit.platform.launcher)
    testImplementation(uuBuild.mockito.junit.jupiter)
    testImplementation(uuBuild.mockito.core)
    testImplementation(uuBuild.mockito.inline)
    testImplementation(uuBuild.mockito.kotlin)

    testImplementation(libs.uu.test.ktx)

    androidTestImplementation(uuBuild.androidx.junit)
    androidTestImplementation(uuBuild.androidx.espresso.core)
    androidTestImplementation(uuBuild.kotlin.test)

    androidTestImplementation(libs.uu.test.ktx)
    androidTestImplementation(libs.uu.test.instrumented.ktx)
}

android {
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}
