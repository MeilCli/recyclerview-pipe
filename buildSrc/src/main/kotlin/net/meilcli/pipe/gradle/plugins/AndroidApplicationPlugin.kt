package net.meilcli.pipe.gradle.plugins

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.ProguardFiles
import net.meilcli.pipe.gradle.dependencies.Android
import net.meilcli.pipe.gradle.dependencies.Junit4
import net.meilcli.pipe.gradle.dependencies.Kotlin
import net.meilcli.pipe.gradle.extensions.androidTestImplementation
import net.meilcli.pipe.gradle.extensions.applyLintSetting
import net.meilcli.pipe.gradle.extensions.implementation
import net.meilcli.pipe.gradle.extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.findByName("android") as BaseExtension
        extension.compileSdkVersion(29)

        extension.defaultConfig {
            minSdkVersion(15)
            targetSdkVersion(29)
            applicationId = "net.meilcli.pipe.sample"
            versionCode = 1
            versionName = "1.0"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        val defaultProguard =
            ProguardFiles.getDefaultProguardFile("proguard-android-optimize.txt", project.layout)
        (extension.buildTypes.findByName("release")
            ?: extension.buildTypes.create("release")).apply {
            isMinifyEnabled = false
            proguardFiles(defaultProguard, "proguard-rules.pro")
        }

        project.applyLintSetting()

        extension.sourceSets.all {
            java.srcDir("src/${name}/kotlin")
        }
        project.dependencies {
            implementation(Kotlin.stdlib)
            implementation(Android.appCompat)

            testImplementation(Junit4.junit)

            androidTestImplementation(Android.espresso)
            androidTestImplementation(Android.junit)
        }
    }
}