package com.fabianfrank.tokrep.extension

import org.junit.jupiter.api.extension.*
import java.io.File

class TemporaryDir : BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private lateinit var testFolder: File

    private lateinit var testTempFolder: File

    override fun beforeAll(context: ExtensionContext?) {
        testFolder = File("./tokrep")
        testFolder.mkdir()

        testTempFolder = File("${testFolder.path}/temp")
        testTempFolder.mkdir()

        File("$testTempFolder/src").mkdir()
        File("$testTempFolder/src/inSrc").mkdir()
        File("$testTempFolder/src/inSrc/ininSrc").mkdir()
        File("$testTempFolder/target").mkdir()
    }

    override fun afterAll(context: ExtensionContext?) {
        testFolder.deleteRecursively()
    }

    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        return (parameterContext!!.parameter.type == File::class.java) && (parameterContext.index == 0)
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any {
        return testFolder
    }
}