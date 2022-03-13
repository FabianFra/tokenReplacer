package com.fabianfrank.tokrep.internal

import com.fabianfrank.tokrep.exception.TokenReplacerInitializationException
import com.fabianfrank.tokrep.exception.TokenReplacerRuntimeException
import org.gradle.api.DefaultTask
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

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


    private lateinit var tempPath: String

    private lateinit var tempDirectory: File

    //-------------------------------------------------------------------------
    // Constructor(s)

    init {
        group = groupName
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun execute() {
        if (enabled.get()) {
            validateProperties()

            if (debug.get()) printProperties()

            executeTask()
        }
    }


    //-------------------------------------------------------------------------
    // Convenience function(s)

    /**
     * Holds task process logic which is applied to given source directories.
     */
    private fun executeTask() {
        tempPath = "./token-rep-${getRandomString(23)}"

        for (filePath in srcDirs.get()) {
            val file = File(filePath)

            if (file.exists()) {
                prepareTempDirectory()

                copyToTempDirectory(file)

                evaluateTempDirectory()

                copyToTargetDirectories()

                deleteTempDirectory()
            }
        }
    }

    /**
     * Prepares temporary directory
     */
    private fun prepareTempDirectory() {
        tempDirectory = File(tempPath)

        if (tempDirectory.exists()) {
            tempDirectory.deleteRecursively()
        } else {
            tempDirectory.mkdir()
        }
    }

    /**
     * Deletes temporary directory
     */
    private fun deleteTempDirectory() {
        tempDirectory.deleteRecursively()
    }

    /**
     * Copies passed file into the temporary directory recursively.
     */
    private fun copyToTempDirectory(file: File) {

        if (tempDirectory.exists() && file.exists()) {
            file.copyRecursively(
                target = tempDirectory,
                overwrite = true
            )
        } else {
            throw TokenReplacerRuntimeException(String.format("Copy to temp directory failed  " +
                    "TempDirectory exists: ${tempDirectory.exists()}, file exists: ${file.exists()}"))
        }
    }

    /**
     * Evaluates the temporary directory. Iterated recursively through directory and applies token replacement logic on
     * each file-
     */
    private fun evaluateTempDirectory() {
        tempDirectory.walk(FileWalkDirection.TOP_DOWN).forEach {
            replaceTokensInFile(it)
        }
    }

    /**
     * Replaces tokens in passed file.
     *
     */
    private fun replaceTokensInFile(file: File) {
        if (file.exists()) {
            if (file.isFile) {
                val pattern = this.pattern.get()
                val tokens = this.tokens.get()

                var currentText = file.readText()

                for (token in tokens) {
                    currentText = currentText.replace(String.format(pattern, token.key), token.value)
                }

                file.writeText(currentText)
            }
        } else {
            throw TokenReplacerRuntimeException("File does not exist.")
        }
    }

    /**
     * Copies the content of the temporary directory into the target directories
     */
    private fun copyToTargetDirectories() {
        if (tempDirectory.exists()) {
            val targets = this.targetDirs.get()

            for (target in targets) {
                val targetFile = File(target)

                if (targetFile.exists() && targetFile.isDirectory) {
                    tempDirectory.copyRecursively(
                        target = targetFile,
                        overwrite = true
                    )
                } else {
                    throw TokenReplacerRuntimeException("Target $target is not a directory or does not exist")
                }
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

    private fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}