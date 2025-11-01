plugins {
    `kotlin-dsl`
}

group = "ru.agrachev.build.logic.convention.plugins"
version = "unspecified"

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.build.gradle)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(libs.versions.javaVersion.map { it.toInt() }.get())
    }
}

gradlePlugin {
    plugins {
        register("room") {
            id = "ru.agrachev.convention.plugins.room"
            implementationClass = "RoomConventionPlugin"
        }
        register("koin") {
            id = "ru.agrachev.convention.plugins.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("test") {
            id = "ru.agrachev.convention.plugins.test"
            implementationClass = "TestConventionPlugin"
        }
        register("androidLibrary") {
            id = "ru.agrachev.convention.plugins.androidLibrary"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "ru.agrachev.convention.plugins.androidApplication"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "ru.agrachev.convention.plugins.kotlinLibrary"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
    }
}