import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.agrachev.build.logic.convention.plugins.isAndroidProject
import ru.agrachev.build.logic.convention.plugins.libs

@Suppress("unused")
class TestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) =
        with(target) {
            dependencies {
                add(
                    "testImplementation",
                    libs.findBundle("test-common").get(),
                )
                if (isAndroidProject) {
                    add(
                        "androidTestImplementation",
                        libs.findBundle("test-common-android").get(),
                    )
                }
            }
        }
}
