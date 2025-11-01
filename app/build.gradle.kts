plugins {
    alias(libs.plugins.conventions.android.application)
    alias(libs.plugins.conventions.koin)
    id("com.google.devtools.ksp")
}

android {
    namespace = "ru.agrachev.emojicalendar"

    defaultConfig {
        applicationId = "ru.agrachev.emojicalendar"
        targetSdk = libs.versions.compileSdk.map { it.toInt() }.get()
        versionCode = 1
        versionName = "1.0"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(project(":calendar-data"))
    implementation(project(":calendar-domain"))
    implementation(project(":calendar-presentation"))
}
