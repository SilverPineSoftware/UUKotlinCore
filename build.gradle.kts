extra["uu_namespace"] = "com.silverpine.uu.core"
extra["uu_publish_artifact_id"] = "uu-core-ktx"
extra["uu_publish_description"] = "Useful Utilities Core"
extra["uu_scm_module_name"] = "UUKotlinCore"

extra["uu_min_sdk"] = 26
extra["uu_target_sdk"] = 36
extra["uu_java_version"] = 17

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.uu.library) apply false
    alias(libs.plugins.uu.android.test) apply false
    alias(libs.plugins.uu.publish)
}
