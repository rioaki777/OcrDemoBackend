package com.rioaki.ocrdemo.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

enum class OcrJobStatus {
    OK, NG, INPROGRESS
}

@Serializable
data class OcrJob(
    val id: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDate: LocalDateTime,
    val fileName: String,
    val status: OcrJobStatus
)