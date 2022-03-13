package com.fabianfrank.tokrep.internal

import com.fabianfrank.tokrep.exception.TokenReplacerInitializationException
import com.fabianfrank.tokrep.exception.TokenReplacerRuntimeException
import org.gradle.api.DefaultTask
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.charset.Charset

open class TokRepTask : DefaultTask() {

    //-------------------------------------------------------------------------
    // Test(s)

    @get:Internal
    val enabled: Property<Boolean> = project.objects.property(Boolean::class.java)

    @get:Internal
    val srcDirs: Property<Array<out String>> = project.objects.property(Array<out String>::class.java)

    @get:Internal
    val targetDirs: Property<Array<out String>> = project.objects.property(Array<out String>::class.java)

    @get:Internal
    val pattern: Property<String> = project.objects.property(String::class.java)

    @get:Internal
    val tokens: MapProperty<String, String> = project.objects.mapProperty(String::class.java, String::class.java)

    @get:Internal
    val debug: Property<Boolean> = project.objects.property(Boolean::class.java)

    //-------------------------------------------------------------------------
    // Constructor(s)

    init {
        group = groupName
        outputs.upToDateWhen { false }

    }

    @TaskAction
    fun execute() {
        if (enabled.get()) {
            println("Executing TokRepTask")

            validateProperties()

            if (debug.get()) printProperties()

            executeTask()
        }
    }

    //-------------------------------------------------------------------------
    // Convenience function(s)

    private fun executeTask() {
        for (fileIteration in srcDirs.get()) {
            val file = File(fileIteration)

            if (file.isDirectory) {
                file.walk().forEach {
                    replaceFile(it)
                }
            } else if (file.isFile) {
                replaceFile(file)
            } else if (file.isHidden) {
                println("Warning: Hidden file is beeing edited")
            } else {
                throw TokenReplacerRuntimeException("Couldn't handle file ${file.path}")
            }
        }
    }

    private fun replaceFile(file: File) {
        if (file.isFile) {
            val tokens = this.tokens.get()
            val pattern = this.pattern.get()

            val fileText = file.readText()
            var replacedString = fileText

            if (fileText.isNotEmpty() && fileText.isNotBlank()) {
                tokens.forEach {
                    replacedString = replacedString.replace(String.format(pattern, it.key), it.value)
                }
            }

            addFileToTarget(file.name, replacedString)
        }
    }

    private fun addFileToTarget(fileName: String, fileText: String) {
        val targetDirectories = this.targetDirs.get();

        for (targetDirectory in targetDirectories) {
            val target = File(targetDirectory)

            if (target.isDirectory) {
                File("${target.path}/$fileName").run {
                    writeText(fileText)
                }
            } else {
                throw TokenReplacerRuntimeException("Target $targetDirectory isn't a directory")
            }
        }
    }

    /**
     * Checks required properties
     */
    private fun validateProperties() {
        if (!tokens.isPresent) {
            throw TokenReplacerInitializationException("Required parameter 'tokens' is not set in configuration (type Array<out String>)")
        }

        if (srcDirs.get().isEmpty()) {
            throw TokenReplacerInitializationException("SrcDirs array is empty")
        }

        if (targetDirs.get().isEmpty()) {
            throw TokenReplacerInitializationException("TargetDirs array is empty")
        }

        if (pattern.get().isEmpty() || pattern.get().isBlank()) {
            throw TokenReplacerInitializationException("Pattern is empty or blank")
        }

        if (debug.get()) println("Properties validated")
    }


    private fun printProperties() {
        println(String.format("\nPrinting properties" +
                "\n-------------------------------------------------------------------------------------------------" +
                "\nenabled: %b\nsrcDirs: %s\ntargetDirs: %s\npatter: %s\ntokens: %s\n",
            enabled.get(),
            srcDirs.get().size,
            targetDirs.get().size,
            pattern.get(),
            tokens.get().size
            )
        )
    }
}