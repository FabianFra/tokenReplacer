package com.fabianfrank.tokrep

import com.fabianfrank.tokrep.extension.TemporaryDir
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

@ExtendWith(TemporaryDir::class)
class PluginTest {

    //-------------------------------------------------------------------------
    // Test(s)

    @Test
    fun `Apply plugin, add configuration and execute task - should print 'Executing TokRepTask'`(tempDir: File) {
        println("Testing 'Apply plugin, add configuration and execute task - should print 'Executing TokRepTask'")

        File(tempDir, "build.gradle.kts").run {
            writeText("""
                plugins {
                    id("java")
                    id("com.fabianfrank.tokrep") version "0.0.1"
                }
                
                tokrepConfiguration {
                    debug = true
                    tokens = mapOf("test" to "123")
                }
                
                tasks.tokrepExecute {
                }
            """.trimIndent())
        }

        val buildResult = GradleRunner.create()
            .withProjectDir(tempDir)
            .withPluginClasspath()
            .withArguments("tokrepExecute")
            .build()

        assert(buildResult.output.contains("Properties validated"))
    }

    @Test
    fun `Disable plugin - Shouldn't throw exception and succeed`(tempDir: File) {
        println("Testing 'Disable plugin - Shouldn't throw exception and succeed'")

        File(tempDir, "build.gradle.kts").run {
            writeText("""
                plugins {
                    id("java")
                    id("com.fabianfrank.tokrep") version "0.0.1"
                }
                
                tokrepConfiguration {
                    enabled = false
                }
                
                tasks.tokrepExecute {
                }
            """.trimIndent())
        }

        val buildResult = GradleRunner.create()
            .withProjectDir(tempDir)
            .withPluginClasspath()
            .withArguments("tokrepExecute")
            .build()

        assert(buildResult.task(":tokrepExecute")!!.outcome.equals(TaskOutcome.SUCCESS))
    }

    @Test
    fun `Adds two different files with tokens - Expect tokens are replaced`(tempDir: File) {
        println("Testing 'Test'")

        File(tempDir, "build.gradle.kts").run {
            writeText("""
                plugins {
                    id("java")
                    id("com.fabianfrank.tokrep") version "0.0.1"
                }
                
                tokrepConfiguration {
                    debug = true
                    
                    srcDirs = arrayOf("./tokrep/temp/src")
                    targetDirs = arrayOf("./tokrep/temp/target")
                    
                    tokens = mapOf(
                        "com.fabianfrank.tokrep.version" to "0.0.1",
                        "com.fabianfrank.tokrep.date" to "13.03.2022"
                    )
                }
                
                tasks.tokrepExecute {
                }
            """.trimIndent())
        }

        val textFile = File(tempDir, "/temp/src/test.txt").run {
            writeText("" +
                    "This is a simple text file which is used to test the plugin com.fabianfrank.tokrep\n" +
                    "The current versions is \${com.fabianfrank.tokrep.version}\n" +
                    "Date \${com.fabianfrank.tokrep.date}\n" +
                    "".trim())
        }

        val xmlFile = File(tempDir, "/temp/src/test.xml").run {
            writeText("" +
                    "<property>\${com.fabianfrank.tokrep.version}</property>\n" +
                    "<property>\${com.fabianfrank.tokrep.date}</property>\n" +
                    "".trim())
        }

        val buildResult = GradleRunner.create()
            .withProjectDir(tempDir)
            .withPluginClasspath()
            .withArguments("tokrepExecute")
            .build()

        println(buildResult.output)

        val textFileResult = File(tempDir, "/temp/target/test.txt")
        val xmlFileResult = File(tempDir, "/temp/target/test.xml")

        assert(textFileResult.isFile && textFileResult.extension == "txt")
        assert(xmlFileResult.isFile && xmlFileResult.extension == "xml")

        assert(textFileResult.readText().contains("0.0.1") && textFileResult.readText().contains("13.03.2022"))
        assert(xmlFileResult.readText().contains("0.0.1") && xmlFileResult.readText().contains("13.03.2022"))
    }

    @Test
    fun `Adds files in nested directories - Expect tokens are replaced`(tempDir: File) {
        println("Testing 'Test'")

        File(tempDir, "build.gradle.kts").run {
            writeText("""
                plugins {
                    id("java")
                    id("com.fabianfrank.tokrep") version "0.0.1"
                }
                
                tokrepConfiguration {
                    debug = true
                    
                    srcDirs = arrayOf("./tokrep/temp/src")
                    targetDirs = arrayOf("./tokrep/temp/target")
                    
                    tokens = mapOf(
                        "com.fabianfrank.tokrep.version" to "0.0.1",
                        "com.fabianfrank.tokrep.date" to "13.03.2022"
                    )
                }
                
                tasks.tokrepExecute {
                }
            """.trimIndent())
        }

        val textFile = File(tempDir, "/temp/src/test.txt").run {
            writeText("" +
                    "This is a simple text file which is used to test the plugin com.fabianfrank.tokrep\n" +
                    "The current versions is \${com.fabianfrank.tokrep.version}\n" +
                    "Date \${com.fabianfrank.tokrep.date}\n" +
                    "".trim())
        }

        val xmlFile = File(tempDir, "/temp/src/insrc/nestedTest.xml").run {
            writeText("" +
                    "<property>\${com.fabianfrank.tokrep.version}</property>\n" +
                    "<property>\${com.fabianfrank.tokrep.date}</property>\n" +
                    "".trim())
        }

        val buildResult = GradleRunner.create()
            .withProjectDir(tempDir)
            .withPluginClasspath()
            .withArguments("tokrepExecute")
            .build()

        println(buildResult.output)

        val textFileResult = File(tempDir, "/temp/target/test.txt")
        val xmlFileResult = File(tempDir, "/temp/target/nestedTest.xml")

        assert(textFileResult.isFile && textFileResult.extension == "txt")
        assert(xmlFileResult.isFile && xmlFileResult.extension == "xml")

        assert(textFileResult.readText().contains("0.0.1") && textFileResult.readText().contains("13.03.2022"))
        assert(xmlFileResult.readText().contains("0.0.1") && xmlFileResult.readText().contains("13.03.2022"))
    }

    //-------------------------------------------------------------------------
    // Convenience function(s)

    private fun printBuildResult(result: BuildResult) {
        println("\n\nBuild Result\n" +
                "--------------------------------------------------------------------------------\n" +
                "${result.output}\n" +
                "--------------------------------------------------------------------------------\n\n"
        )
    }
}