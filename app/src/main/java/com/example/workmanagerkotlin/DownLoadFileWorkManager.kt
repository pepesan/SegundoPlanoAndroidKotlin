package com.example.workmanagerkotlin

import android.content.Context
import android.os.Environment
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL

val PERMISION_REQUEST = 100
val PERIODIC_INTERVAL = 15L
val URLFILE = "https://images.pexels.com/photos/730344/" +
        "pexels-photo-730344.jpeg?" +
        "auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"

class DownLoadFileWorkManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    /**
     * Workmanager worker thread which do processing
     * in background, so it will not impact to main thread or UI
     *
     */
    override fun doWork(): ListenableWorker.Result {
        try {
            val url = URL(URLFILE)
            val conection = url.openConnection()
            conection.connect()
            // getting file length

            // input stream to read file - with 8k buffer
            val input = BufferedInputStream(url.openStream(), 8192)

            // Output stream to write file
            val output = FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/myphoto.jpeg")

            val data = ByteArray(1024)

            var count: Int? = 0

            while ({ count = input.read(data);count }() != -1) {
                output.write(data, 0, count!!)
            }

            // flushing output
            output.flush()

            // closing streams
            output.close()
            input.close()

        } catch (e: Exception) {
            return Result.retry()
        }

        return Result.success()
    }


}