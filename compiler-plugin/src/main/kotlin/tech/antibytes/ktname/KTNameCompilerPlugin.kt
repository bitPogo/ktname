/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.ktname

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import tech.antibytes.ktname.KTNameContract.COMPILE_TASK_ANDROID_SUFFIX
import tech.antibytes.ktname.KTNameContract.COMPILE_TASK_JS
import tech.antibytes.ktname.KTNameContract.COMPILE_TASK_PREFIX
import tech.antibytes.ktname.KTNameContract.USE_ANDROID
import tech.antibytes.ktname.KTNameContract.USE_JS
import tech.antibytes.ktname.config.MainConfig

class KTNameCompilerPlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.create("ktname", KTNameExtension::class.java)
    }

    private val KotlinCompilation<*>.ktnameExtension: KTNameExtension
        get() = target.project.extensions.getByType(KTNameExtension::class.java)

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>,
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val ktNameExtension = kotlinCompilation.ktnameExtension
        return project.provider {
            listOf(
                SubpluginOption(
                    key = USE_JS,
                    value = ktNameExtension.enableForJsTests.toString(),
                ),
                SubpluginOption(
                    key = USE_ANDROID,
                    value = ktNameExtension.enableForInstrumentedAndroidTests.toString(),
                ),
            )
        }
    }

    override fun getCompilerPluginId(): String = MainConfig.pluginId

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = MainConfig.group,
            artifactId = MainConfig.artifactId,
            version = MainConfig.version,
        )
    }

    private fun KotlinCompilation<*>.isApplicableForJs(): Boolean {
        return ktnameExtension.enableForJsTests && compileKotlinTaskName == COMPILE_TASK_JS
    }

    private fun String.isInstrumentedAndroidTarget(): Boolean {
        return startsWith(COMPILE_TASK_PREFIX) && endsWith(COMPILE_TASK_ANDROID_SUFFIX)
    }

    private fun KotlinCompilation<*>.isApplicableForAndroid(): Boolean {
        return ktnameExtension.enableForInstrumentedAndroidTests && compileKotlinTaskName.isInstrumentedAndroidTarget()
    }

    override fun isApplicable(
        kotlinCompilation: KotlinCompilation<*>,
    ): Boolean = kotlinCompilation.isApplicableForJs() || kotlinCompilation.isApplicableForAndroid()
}
