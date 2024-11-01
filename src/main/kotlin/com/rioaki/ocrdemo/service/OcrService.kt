package com.rioaki.ocrdemo.service

import com.rioaki.ocrdemo.common.GlobalSettings
import com.rioaki.ocrdemo.model.OcrJob
import com.rioaki.ocrdemo.model.OcrJobRepository
import com.rioaki.ocrdemo.model.OcrJobStatus
import com.rioaki.ocrdemo.model.PostgresOcrJobRepository
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Paths
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OcrService (private val ocrJobRepository: PostgresOcrJobRepository) {
    private val logger: Logger = LoggerFactory.getLogger(OcrService::class.java)

    suspend fun allOcrJobs(): List<OcrJob> {
        return ocrJobRepository.allOcrJobs()
    }

    suspend fun ocrJobById(id: Int): OcrJob? {
        return ocrJobRepository.ocrJobById(id)
    }

    suspend fun addOcrJob(fileName: String): OcrJob {
        return ocrJobRepository.addOcrJob(fileName)
    }

    suspend fun updateOcrjobStatus(id: Int, status: OcrJobStatus) {
        return ocrJobRepository.updateOcrJobStatusById(id, status)
    }

    suspend fun saveImage(multipartData: MultiPartData): File? {
        var imageFile: File? = null

        multipartData.forEachPart { partData ->
            if (partData !is PartData.FileItem) return@forEachPart
            if (partData.originalFileName == null) return@forEachPart

            imageFile = Paths.get(GlobalSettings.UPLOAD_DIR, partData.originalFileName).toFile()

            // ファイルを書き込む
            val channel = partData.provider()
            channel.copyAndClose(imageFile!!.writeChannel())

            partData.dispose()
        }

        return imageFile
    }

    suspend fun runOCR(imageFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            val ocrProgFile = File(GlobalSettings.UPLOAD_DIR, GlobalSettings.OCR_PROG_FILE)
            val command = listOf(GlobalSettings.PYTHON_DIR, ocrProgFile.absolutePath, imageFile.absolutePath)

            val processBuilder = ProcessBuilder(command)
            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()
            val output = StringBuilder()

            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.lines().forEach { line ->
                    output.appendLine(line)
                }
            }

            process.waitFor()
            if (process.exitValue() != 0) logger.debug(output.toString())
            (process.exitValue() == 0)
        }
    }

}