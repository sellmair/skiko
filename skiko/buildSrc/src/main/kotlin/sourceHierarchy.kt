@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate

val SkikoProjectContext.jvmMainSourceSet get() = kotlin.sourceSets.getByName("jvmMain")

val SkikoProjectContext.jvmTestSourceSet get() = kotlin.sourceSets.getByName("jvmTest")

val SkikoProjectContext.awtMainSourceSet get() = if (project.supportAwt) kotlin.sourceSets.getByName("awtMain") else null

val SkikoProjectContext.androidMainSourceSet get() = if (project.supportAndroid) kotlin.sourceSets.getByName("androidMain") else null

val SkikoProjectContext.webTestSourceSet get() = if (project.supportWeb) kotlin.sourceSets.getByName("webTest") else null

val SkikoProjectContext.wasmJsTest get() = if (project.supportWeb) kotlin.sourceSets.getByName("wasmJsTest") else null

val skikoSourceSetHierarchyTemplate = KotlinHierarchyTemplate {
    common {
        group("jvm") {
            withAndroidTarget()
            withJvm()
        }

        group("web") {
            withJs()
            withWasmJs()
        }

        group("native") {
            group("linux") {
                withLinux()
            }

            group("darwin") {
                group("uikit") {
                    group("tvos")
                    group("ios") {
                        withIos()
                    }
                }

                group("macos") {
                    withMacos()
                }

                group("tvos") {
                    withTvos()
                }
            }
        }

        group("nativeJs") {
            group("web")
            group("native")
        }
    }
}
