plugins {
    alias(libs.plugins.conventions.android.library)
    alias(libs.plugins.conventions.room)
    alias(libs.plugins.conventions.koin)
    alias(libs.plugins.conventions.test)
}

android {
    namespace = "ru.agrachev.calendar.data"
}

dependencies {
    implementation(project(":calendar-domain"))
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
