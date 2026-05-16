plugins {
    alias(uuBuild.plugins.android.application) apply false
    alias(uuBuild.plugins.android.library) apply false
    alias(uuBuild.plugins.kotlin.android) apply false
    alias(uuBuild.plugins.nexus.publish)
    alias(uuBuild.plugins.kotlin.serialization)
    alias(uuBuild.plugins.uu.library) apply false
    alias(uuBuild.plugins.uu.android.test) apply false
    alias(uuBuild.plugins.uu.publish)
}
