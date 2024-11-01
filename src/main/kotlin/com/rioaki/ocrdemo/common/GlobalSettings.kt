package com.rioaki.ocrdemo.common

import io.ktor.server.application.*

object GlobalSettings {
    private lateinit var environment: ApplicationEnvironment
    fun setup(env: ApplicationEnvironment) {
        environment = env
    }

    const val OCR_TRAIN_FILE: String = "eng.traineddata"
    const val OCR_PROG_FILE: String = "ocr_demo.py"
    val UPLOAD_DIR: String by lazy {
        environment.config.property("ktor.uploadDir").getString()
    }
    val PYTHON_DIR: String by lazy {
        environment.config.property("ktor.pythonDir").getString()
    }
}