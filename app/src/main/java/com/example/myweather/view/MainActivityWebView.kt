package com.example.myweather.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myweather.databinding.ActivityMainWebviewBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class MainActivityWebView : AppCompatActivity() {

    lateinit var binding: ActivityMainWebviewBinding
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ok.setOnClickListener{
            val uri = URL(binding.url.text.toString())
            val urlConnection: HttpsURLConnection = (uri.openConnection() as HttpsURLConnection).apply {
                connectTimeout = 1000
                readTimeout = 1000
            }
            Thread{
                val buffer = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val result = toOneString(buffer)
                runOnUiThread{
                    binding.webView.settings.javaScriptEnabled = true
                    binding.webView.loadDataWithBaseURL(null, result, "text/html; utf-8", "utf-8", null)
                }
            }.start()

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun toOneString(bufferedReader: BufferedReader): String{
        return bufferedReader.lines().collect(Collectors.joining("\n"))
    }
}