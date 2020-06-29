package com.skichrome.portfolio.util

import android.os.Environment.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// =================================
//              Methods
// =================================

// --- External storage State methods --- //

fun canWriteExternalStorage(): Boolean
{
    val state = getExternalStorageState()
    return state == MEDIA_MOUNTED
}

fun canReadExternalStorage(): Boolean
{
    val state = getExternalStorageState()
    return state == MEDIA_MOUNTED || state == MEDIA_MOUNTED_READ_ONLY
}

// --- File IO operations --- //

@Throws(IOException::class)
fun File.createOrGetJpegFile(folderName: String, fileName: String): File
{
    val fileFolder = File(this, folderName).apply {
        if (!exists())
            mkdirs()
    }
    val timeStamp = SimpleDateFormat("HH:mm:ss_dd-MM-yyyy", Locale.getDefault()).format(Date())
    return File(fileFolder, "[$timeStamp]-$fileName.jpg")
}

@Throws(IOException::class)
fun File.createOrGetFile(folderName: String, fileName: String): File
{
    val fileFolder = File(this, folderName).apply {
        if (!exists())
            mkdirs()
    }
    return File(fileFolder, fileName)
}