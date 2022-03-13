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