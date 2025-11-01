import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.agrachev.build.logic.convention.plugins.configureBuildTypes
import ru.agrachev.build.logic.convention.plugins.configureCommonDefaultConfig
import ru.agrachev.build.logic.convention.plugins.configureCompileSdkVersion
import ru.agrachev.build.logic.convention.plugins.configureConsumerProGuardFiles
import ru.agrachev.build.logic.convention.plugins.configureJavaCompileOptions
import ru.agrachev.build.logic.convention.plugins.configureJvmToolchainVersion
import ru.agrachev.build.logic.convention.plugins.getPluginId

@Suppress("unused")
class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.run {
            apply(getPluginId("android-library"))
            apply(getPluginId("kotlin-android"))
        }
        configureCompileSdkVersion()
        configureCommonDefaultConfig()
        configureConsumerProGuardFiles()
        configureJvmToolchainVersion()
        configureJavaCompileOptions()
        configureBuildTypes(
            enableMinification = false,
        )
    }
}
