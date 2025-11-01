import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.agrachev.build.logic.convention.plugins.isAndroidProject
import ru.agrachev.build.logic.convention.plugins.libs

@Suppress("unused")
class KoinConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) =
        with(target) {
            dependencies {
                add(
                    "implementation",
                    dependencies.platform(
                        libs.findLibrary("koin-bom").get()
                    ),
                )
                add(
                    "implementation",
                    libs.findLibrary("koin-core").get(),
                )
                add(
                    "testImplementation",
                    libs.findLibrary("koin-test").get(),
                )
                if (isAndroidProject) {
                    add(
                        "implementation",
                        libs.findLibrary("koin-android").get(),
                    )
                }
            }
        }
}
