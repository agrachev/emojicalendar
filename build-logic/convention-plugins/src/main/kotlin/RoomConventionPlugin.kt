import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.agrachev.build.logic.convention.plugins.libs

@Suppress("unused")
class RoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.google.devtools.ksp")

        dependencies {
            add(
                "implementation",
                libs.findLibrary("androidx-room-runtime").get(),
            )
            add(
                "ksp",
                libs.findLibrary("androidx-room-room-compiler").get(),
            )
            add(
                "implementation",
                libs.findLibrary("androidx-room-ktx").get(),
            )
            add(
                "testImplementation",
                libs.findLibrary("androidx-room-testing").get(),
            )
        }
    }
}
