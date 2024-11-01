package com.rioaki.ocrdemo.plugins

import com.rioaki.ocrdemo.common.GlobalSettings
import com.rioaki.ocrdemo.controller.OcrController
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import kotlinx.io.IOException
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        allowHost("localhost:3000")
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
    }

    val logger: Logger = LoggerFactory.getLogger("MyLogger")

    routing {
        staticResources("/resources", "static")

        get("/") {
            val title: String = "OCR DEMO"
            call.respond(ThymeleafContent("index", mapOf("title" to title)))
        }
    }

    val ocrController by inject<OcrController>()
    routing {
        route("/ocr") {
            get {
                call.respond(ocrController.getAllOcrJobs())
            }

            get("/byId/{id}") {
                val ocrJob = ocrController.getOcrJobById(call) ?: return@get
                call.respond(ocrJob)
            }

            staticFiles("/image", File(GlobalSettings.UPLOAD_DIR)){
                exclude { file -> !file.extension.contains("png") }
            }
            post {
                val multipartData = call.receiveMultipart()
                try {
                    val responseData = ocrController.doOCR(multipartData)

                    if (responseData != null){
                        call.respond(responseData)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Failed to ocr file.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest, "Invalid file.")
                }
            }

            post ("/clear") {
                // スケジューラで定期実行される
                // TODO: 古い画像を削除、OcrJobのステータス更新
            }
        }
    }
}
