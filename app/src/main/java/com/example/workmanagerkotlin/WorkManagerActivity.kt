package com.example.workmanagerkotlin

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.workmanagerkotlin.DownLoadFileWorkManager.*


import java.util.concurrent.TimeUnit

class WorkManagerActivity : AppCompatActivity(), View.OnClickListener {

    private var periodaticworkmanager: Button? = null

    /**
     * Runtime permissions object init to check storage persmissions
     */
    var runtimePermission: RunTimePermission = RunTimePermission(this)

    /**
     *  Workmanager global instance to enqueue tasks & get update
     */
    val workManager = WorkManager.getInstance()
    var btnStartDownloadWork: Button? = null
    var llProgress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_manager)
        btnStartDownloadWork = findViewById(R.id.btnStartDownloadWork)
        periodaticworkmanager = findViewById<Button>(R.id.periodaticworkmanager)
        llProgress = findViewById(R.id.progressBar)
        //init clicklistener for both buttons
        btnStartDownloadWork?.setOnClickListener(this@WorkManagerActivity)
        periodaticworkmanager?.setOnClickListener(this@WorkManagerActivity)
    }

    /**
     * TODO When use clicl on buttons it will call below methods
     *
     * @param view clicked item view(or id)
     */
    override fun onClick(view: View) {

        when (view.id) {
            R.id.btnStartDownloadWork -> {
                runtimePermission.requestPermission(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    object : RunTimePermission.PermissionCallback {
                        override fun onGranted() {

                            StartOneTimeWorkManager()
                        }

                        override fun onDenied() {
                            //show message if not allow storage permission
                        }
                    })
            }

            R.id.periodaticworkmanager -> {
                runtimePermission.requestPermission(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    object : RunTimePermission.PermissionCallback {
                        override fun onGranted() {

                            StartPeriodicWorkManager()
                        }

                        override fun onDenied() {
                            //show message if not allow storage permission
                        }

                    })

            }
        }
    }


    private fun StartOneTimeWorkManager() {

        val constraints = androidx.work.Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val task = OneTimeWorkRequest.Builder(DownLoadFileWorkManager::class.java).setConstraints(constraints).build()
        workManager.enqueue(task)

        workManager.getWorkInfoByIdLiveData(task.id)
            .observe(this@WorkManagerActivity, Observer {
                it?.let {

                    if (it.state == WorkInfo.State.RUNNING) {
                        loaderShow(true)

                    }else
                        if (it.state.isFinished) {

                            Toast.makeText(this@WorkManagerActivity, "Hecho", Toast.LENGTH_SHORT).show()
                            loaderShow(false)
                        }
                }
            })
    }

    // Every periodic [PERIODIC_INTERVAL] interval work execute
    private fun StartPeriodicWorkManager() {
        loaderShow(true)
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DownLoadFileWorkManager::class.java,
            PERIODIC_INTERVAL,
            TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        workManager.enqueue(periodicWorkRequest)


        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
            .observe(this@WorkManagerActivity, Observer {
                it?.let {
                    if (it.state == WorkInfo.State.ENQUEUED) {

                        loaderShow(false)
                        Toast.makeText(this@WorkManagerActivity, "Hecho", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    /**
     * Loader visibility
     */
    private fun loaderShow(flag: Boolean) {
        when (flag) {
            true -> llProgress?.visibility = View.VISIBLE
            false -> llProgress?.visibility = View.GONE
        }
    }

    /**
     * Request permission result pass to RuntimePermission.kt
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISION_REQUEST)
            runtimePermission.onRequestPermissionsResult(grantResults)

    }
}