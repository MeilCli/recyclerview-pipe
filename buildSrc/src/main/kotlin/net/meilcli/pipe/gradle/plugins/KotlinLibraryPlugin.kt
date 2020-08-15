package net.meilcli.pipe.gradle.plugins

import net.meilcli.pipe.gradle.dependencies.Junit5
import net.meilcli.pipe.gradle.dependencies.Kotlin
import net.meilcli.pipe.gradle.extensions.implementation
import net.meilcli.pipe.gradle.extensions.testImplementation
import net.meilcli.pipe.gradle.extensions.testRuntimeOnly
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies

class KotlinLibraryPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.named("test", Test::class.java).configure {
            @Suppress("UnstableApiUsage")
            useJUnitPlatform()
        }

        project.dependencies {
            implementation(Kotlin.stdlib)

            testImplementation(Junit5.api)
            testRuntimeOnly(Junit5.engine)
        }
    }
}