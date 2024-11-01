package com.rioaki.ocrdemo.model

import com.rioaki.ocrdemo.db.OcrJobDAO
import com.rioaki.ocrdemo.db.OcrJobTable
import com.rioaki.ocrdemo.db.daoToModel
import com.rioaki.ocrdemo.db.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.time.LocalDateTime

class PostgresOcrJobRepository : OcrJobRepository {
    override suspend fun allOcrJobs(): List<OcrJob> = suspendTransaction {
        OcrJobDAO.all().map(::daoToModel)
    }

    override suspend fun ocrJobById(id: Int): OcrJob? = suspendTransaction {
        OcrJobDAO
            .find { (OcrJobTable.id eq id) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun updateOcrJobStatusById(id: Int, status: OcrJobStatus): Unit = suspendTransaction {
        OcrJobDAO
            .find { (OcrJobTable.id eq id) }
            .single().apply {
                this.status = status.toString()
            }
    }

    override suspend fun addOcrJob(fileName: String): OcrJob  = suspendTransaction {
        val dao = OcrJobDAO.new {
            this.startDate = LocalDateTime.now()
            this.fileName = fileName
            this.status = OcrJobStatus.INPROGRESS.toString()
        }
        daoToModel(dao)
    }

    override suspend fun removeOcrJob(id: Int): Boolean = suspendTransaction {
        val rowsDeleted = OcrJobTable.deleteWhere {
            OcrJobTable.id eq id
        }
        rowsDeleted == 1
    }
}