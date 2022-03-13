package com.fabianfrank.tokrep

import com.fabianfrank.tokrep.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project

open class TokRepPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        // Extension access
        val extension = target.extensions.create(extensionName, TokRepExtension::class.java)

        // Parameters
        val enabled: Boolean by lazy {
            extension.enabled ?: enabledDefault
        }

        val srcDirs: Array<out String> by lazy {
            extension.srcDirs ?: srcDirsDefault
        }

        val targetDirs: Array<out String> by lazy {
            extension.targetDirs ?: targetDirsDefault
        }

        val pattern: String by lazy {
            extension.pattern ?: patternDefault
        }

        val tokens: Map<String, String>? by lazy {
            extension.tokens
        }

        val debug : Boolean by lazy {
            extension.debug ?: false
        }

        target.tasks.register(taskTokRep, TokRepTask::class.java).configure { task ->
            task.enabled.set(target.provider {
                enabled
            })

            task.srcDirs.set(target.provider {
                srcDirs
            })

            task.targetDirs.set(target.provider {
                targetDirs
            })

            task.pattern.set(target.provider {
                pattern
            })

            task.tokens.set(target.provider {
                tokens
            })

            task.debug.set(target.provider {
                debug
            })
        }
    }
}