package com.example.workmanagerkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


import java.util.concurrent.TimeUnit

class WorkManagerActivity : AppCompatActivity() {
    var iv_1: ImageView? = null
    var iv_2: ImageView? = null
    var iv_3: ImageView? = null


    val jsonString: String = "[\n" +
            "  {\n" +
            "    \"albumId\": 1,\n" +
            "    \"id\": 1,\n" +
            "    \"title\": \"Eminem\",\n" +
            "    \"url\": \"https://i.pinimg.com/originals/c4/14/4f/c4144fba258c2f0b4735180fe5d9a03b.jpg\",\n" +
            "    \"thumbnailUrl\": \"https://via.placeholder.com/150/92c952\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"albumId\": 1,\n" +
            "    \"id\": 2,\n" +
            "    \"title\": \"MEME\",\n" +
            "    \"url\": \"https://pics.me.me/eminems-emotions-suprised-sad-happy-curious-annoyed-excited-shocked-bored-13877943.png\",\n" +
            "    \"thumbnailUrl\": \"https://via.placeholder.com/150/771796\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"albumId\": 1,\n" +
            "    \"id\": 3,\n" +
            "    \"title\": \"Eminem News\",\n" +
            "    \"url\": \"https://www.sohh.com/wp-content/uploads/Eminem.jpg\",\n" +
            "    \"thumbnailUrl\": \"https://via.placeholder.com/150/24f355\"\n" +
            "  }]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_manager)
        iv_1 = findViewById(R.id.iv_1)
        iv_2 = findViewById(R.id.iv_2)
        iv_3 = findViewById(R.id.iv_3)
        findViewById<Button>(R.id.btn_download).setOnClickListener {
            startWorker()
        }
    }
    private fun startWorker() {
        val data = Data.Builder()
            .putString("images", jsonString)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)

        val oneTimeRequest = OneTimeWorkRequest.Builder(ImageDownloadWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints.build())
            .addTag("demo")
            .build()

        Toast.makeText(this, "Starting worker", Toast.LENGTH_SHORT).show()

        WorkManager.getInstance(this)
            .enqueueUniqueWork("AndroidVille", ExistingWorkPolicy.KEEP, oneTimeRequest)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(imageDownloadedEvent: ImageDownloadedEvent) {
        val file = File("${imageDownloadedEvent.path}/${imageDownloadedEvent.name}")
        when (imageDownloadedEvent.id) {
            "1" -> Picasso.get().load(file).into(iv_1)
            "2" -> Picasso.get().load(file).into(iv_2)
            "3" -> Picasso.get().load(file).into(iv_3)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


}