package com.fabianfrank.tokrep.internal

internal const val extensionName = "tokrepConfiguration"

internal const val groupName = "tokrep"

internal const val taskTokRep = "tokrepExecute"

//-----------------------------------------------------------------------------
// Default values

internal const val enabledDefault: Boolean = true

internal val srcDirsDefault: Array<out String> = arrayOf("./src/main/webapp")

internal val targetDirsDefault: Array<out String> = arrayOf("./tokenReplacer")

internal const val patternDefault: String = "\${%s}"
