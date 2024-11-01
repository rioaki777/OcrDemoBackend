package com.rioaki.ocrdemo

import com.rioaki.ocrdemo.plugins.configureRouting
import io.ktor.server.application.*
import com.rioaki.ocrdemo.common.CommonUtils
import com.rioaki.ocrdemo.common.GlobalSettings
import com.rioaki.ocrdemo.common.Scheduler
import com.rioaki.ocrdemo.plugins.configureDatabases
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.server.thymeleaf.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.plugin.Koin
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.io.File
import java.util.concurrent.TimeUnit

fun Application.module() {
    GlobalSettings.setup(environment)

    setupPostProcess()
    setupKoin()
    setupThymeleaf()
    configureDatabases()
    configureRouting()

    // アップロードされたサーバファイルを削除するスケジューラ
    val client = HttpClient {}
    val scheduler = Scheduler {
        launch(Dispatchers.IO) {
            client.post("http://localhost:8080/ocr/clear")
        }
    }
    scheduler.scheduleExecution(1, TimeUnit.HOURS)
    monitor.subscribe(ApplicationStopped) {
        scheduler.stop()
    }
}

fun Application.setupKoin() {
    install(Koin){
        modules(Module.koinModules)
    }
}

fun Application.setupPostProcess() {
    launch {
        // 出力ディレクトリが存在しない場合は作成
        val targetDir = File(GlobalSettings.UPLOAD_DIR)
        if (!targetDir.exists()) targetDir.mkdir()

        // リソースファイルを展開
        CommonUtils.deployResourceFile(GlobalSettings.OCR_PROG_FILE, targetDir)
        CommonUtils.deployResourceFile(GlobalSettings.OCR_TRAIN_FILE, targetDir)
    }
}

fun Application.setupThymeleaf() {
    install(Thymeleaf) {
        setTemplateResolver((if (developmentMode) {
            FileTemplateResolver().apply {
                cacheManager = null
                prefix = "src/main/resources/templates/"
            }
        } else {
            ClassLoaderTemplateResolver().apply {
                prefix = "templates/"
            }
        }).apply {
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
}

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}