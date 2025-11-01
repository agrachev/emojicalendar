plugins {
    alias(libs.plugins.conventions.kotlin.library)
    alias(libs.plugins.conventions.koin)
    alias(libs.plugins.conventions.test)
}

dependencies {
    implementation(libs.threeten.extra)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlinx.coroutines.test)
}
