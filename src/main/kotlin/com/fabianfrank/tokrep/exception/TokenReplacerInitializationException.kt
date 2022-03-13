package com.fabianfrank.tokrep.exception

import org.gradle.api.GradleException

class TokenReplacerInitializationException(message: String, cause: Throwable? = null) : GradleException(message, cause)