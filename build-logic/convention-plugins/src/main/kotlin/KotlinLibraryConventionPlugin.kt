import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.agrachev.build.logic.convention.plugins.configureJava
import ru.agrachev.build.logic.convention.plugins.configureKotlin
import ru.agrachev.build.logic.convention.plugins.getPluginId

@Suppress("unused")
class KotlinLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply(getPluginId("jetbrains-kotlin-jvm"))
            target.run {
                configureJava()
                configureKotlin()
            }
        }
}
