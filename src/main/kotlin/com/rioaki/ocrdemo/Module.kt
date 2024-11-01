package com.rioaki.ocrdemo

import com.rioaki.ocrdemo.controller.OcrController
import com.rioaki.ocrdemo.model.PostgresOcrJobRepository
import com.rioaki.ocrdemo.service.OcrService
import org.koin.dsl.module

object Module {
    val koinModules = module {
        single{ PostgresOcrJobRepository() }
        single{ OcrService(get()) }
        single{ OcrController(get()) }
    }
}