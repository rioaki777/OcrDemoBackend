package com.rioaki.ocrdemo.db

import com.rioaki.ocrdemo.model.OcrJob
import com.rioaki.ocrdemo.model.OcrJobStatus
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object OcrJobTable : IntIdTable("ocr_job") {
    val startDate = datetime("start_date")
    val fileName = varchar("file_name", 50)
    val status = varchar("status", 10)
}

class OcrJobDAO(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<OcrJobDAO>(OcrJobTable)

    var startDate by OcrJobTable.startDate
    var fileName by OcrJobTable.fileName
    var status by OcrJobTable.status
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToModel(dao: OcrJobDAO) = OcrJob(
    dao.id.value,
    dao.startDate,
    dao.fileName,
    OcrJobStatus.valueOf(dao.status)
)