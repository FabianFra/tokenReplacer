package com.fabianfrank.tokrep.internal

open class TokRepExtension {

    /**
     * Optional parameter enabled.
     *
     * Set the enabled state of the plugin.
     *
     * Default value: "true"
     */
    var enabled: Boolean? = true

    /**
     * Optional parameter srcDirs.
     *
     * Array of directory paths which are evaluated.
     *
     * Default value: "src/main/webapp"
     */
    var srcDirs: Array<out String>? = arrayOf("src/main/webapp")

    /**
     * Optional parameter targetDirs.
     *
     * Array of directory paths where the result will be added.
     *
     * Default value: "./tokRep"
     */
    var targetDirs: Array<out String>? = arrayOf("./tokenReplacer")

    /**
     * Optional parameter pattern.
     *
     * Pattern which is used to replace content.
     *
     * Default value: "\${%s}"
     */
    var pattern: String? = "\${%s}"

    /**
     * Required parameter tokens.
     *
     * The array includes tokens which are looked for.
     */
    var tokens: Map<String, String>? = null

    /**
     * Optional parameter debug.
     *
     * If set to true, debug information will be included.
     */
    var debug: Boolean? = false
}