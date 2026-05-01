package ru.agrachev.build.logic.convention.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginInstantiationException
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

private const val JAVA_VERSION_ALIAS = "javaVersion"
private const val COMPILE_SDK_ALIAS = "compileSdk"
private const val MIN_SDK_ALIAS = "minSdk"

internal inline val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>()
        .named("libs")

internal inline val Project.commonExtension: CommonExtension
    get() = applicationExtension
        ?: libraryExtension
        ?: throw PluginInstantiationException("Can only be applied on an android Application or Library")

internal inline val Project.applicationExtension: ApplicationExtension?
    get() = extensions.findByType<ApplicationExtension>()

internal inline val Project.libraryExtension: LibraryExtension?
    get() = extensions.findByType<LibraryExtension>()

internal inline val Project.isAndroidProject
    get() = ANDROID_PLUGINS.any { plugins.findPlugin(it) != null }

internal fun Project.valueOf(alias: String) =
    libs.findVersion(alias).map { it.requiredVersion.toInt() }.get()

internal fun Project.configureCompileSdkVersion() =
    commonExtension.run {
        compileSdk {
            version = release(valueOf(COMPILE_SDK_ALIAS))
        }
    }

internal fun Project.configureCommonDefaultConfig() =
    commonExtension.run {
        defaultConfig.apply {
            minSdk = valueOf(MIN_SDK_ALIAS)
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }


internal fun Project.configureConsumerProGuardFiles() =
    libraryExtension?.let {
        it.defaultConfig {
            consumerProguardFiles("consumer-rules.pro")
        }
    } ?: throw PluginInstantiationException("Can only be applied on an library")

internal fun Project.configureBuildTypes(
    enableMinification: Boolean = false
) = commonExtension.run {
    buildTypes.run {
        forEach {
            getByName(it.name) {
                if ("debug" !in name) {
                    isMinifyEnabled = enableMinification
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }
}

internal fun Project.configureJvmToolchainVersion() =
    kotlinExtension.run {
        jvmToolchain(valueOf(JAVA_VERSION_ALIAS))
    }

internal fun Project.configureJava() =
    extensions.findByType<JavaPluginExtension>()?.let {
        val javaVersion = JavaVersion.toVersion(valueOf(JAVA_VERSION_ALIAS))
        it.sourceCompatibility = javaVersion
        it.targetCompatibility = javaVersion
    } ?: throw (PluginInstantiationException("JavaPluginExtension not found"))

internal fun Project.configureKotlin() =
    extensions.findByType<KotlinJvmProjectExtension>()?.let {
        it.compilerOptions {
            configureJvmToolchainVersion()
        }
    } ?: throw (PluginInstantiationException("KotlinJvmProjectExtension not found"))

internal fun Project.configureJavaCompileOptions() =
    commonExtension.run {
        compileOptions.apply {
            val javaVersion = JavaVersion.toVersion(valueOf(JAVA_VERSION_ALIAS))
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }

internal fun Project.getPluginId(name: String) =
    this.libs.findPlugin(name).map { provider ->
        provider.get().pluginId
    }.get()

private val ANDROID_PLUGINS = listOf(
    "com.android.library",
    "com.android.application",
)
