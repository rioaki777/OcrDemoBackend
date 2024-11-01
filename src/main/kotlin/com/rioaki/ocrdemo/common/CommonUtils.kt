package com.rioaki.ocrdemo.common

import java.io.File
import java.io.InputStream

object CommonUtils {

    fun deployResourceFile(resourceFileName: String, outputDir: File){
        val resourceStream: InputStream = this::class.java.getResourceAsStream("/$resourceFileName") ?: return

        val targetFile = File(outputDir, resourceFileName)
        resourceStream.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

}