package com.rioaki.ocrdemo.controller

import com.rioaki.ocrdemo.model.OcrJob
import com.rioaki.ocrdemo.model.OcrJobStatus
import com.rioaki.ocrdemo.service.OcrService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import java.io.File

class OcrController (private val ocrService: OcrService) {

    suspend fun doOCR(multipartData: MultiPartData): OcrJob? {
        val imageFile: File = ocrService.saveImage(multipartData) ?: return null

        val ocrJob: OcrJob = ocrService.addOcrJob(imageFile.name)

        // ocrは非同期で行い、結果を先に返す
        CoroutineScope(Dispatchers.IO).launch {
            val ocrResult: Boolean  = ocrService.runOCR(imageFile)
            ocrService.updateOcrjobStatus(ocrJob.id, if (ocrResult) OcrJobStatus.OK else OcrJobStatus.NG)
        }

        return ocrJob
    }

    suspend fun getAllOcrJobs(): List<OcrJob> {
        return ocrService.allOcrJobs()
    }

    suspend fun getOcrJobById(call: RoutingCall): OcrJob? {
        val idStr = call.parameters["id"]
        val id: Int? = idStr?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest)
            return null
        }

        val ocrJob = ocrService.ocrJobById(id)
        if (ocrJob == null) {
            call.respond(HttpStatusCode.NotFound)
        }

        return ocrJob
    }


}