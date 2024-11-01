package com.rioaki.ocrdemo.model

interface OcrJobRepository {
    suspend fun allOcrJobs(): List<OcrJob>
    suspend fun ocrJobById(id: Int): OcrJob?
    suspend fun updateOcrJobStatusById(id: Int, status: OcrJobStatus)
    suspend fun addOcrJob(fileName: String): OcrJob
    suspend fun removeOcrJob(id: Int): Boolean
}