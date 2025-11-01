import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.agrachev.build.logic.convention.plugins.configureBuildTypes
import ru.agrachev.build.logic.convention.plugins.configureCommonDefaultConfig
import ru.agrachev.build.logic.convention.plugins.configureCompileSdkVersion
import ru.agrachev.build.logic.convention.plugins.configureJavaCompileOptions
import ru.agrachev.build.logic.convention.plugins.configureJvmToolchainVersion
import ru.agrachev.build.logic.convention.plugins.getPluginId

@Suppress("unused")
class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.run {
            apply(getPluginId("android-application"))
            apply(getPluginId("kotlin-android"))
        }
        configureCompileSdkVersion()
        configureCommonDefaultConfig()
        configureBuildTypes(
            enableMinification = true,
        )
        configureJavaCompileOptions()
        configureJvmToolchainVersion()
    }
}
