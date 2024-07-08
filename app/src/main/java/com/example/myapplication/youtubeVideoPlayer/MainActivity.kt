package com.example.myapplication.youtubeVideoPlayer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL, false)

        val videoList = listOf(
            VideoItem("https://youtu.be/Rjnm-y902UU?si=vSQcW0jvab_lNBhM"), // PSY - GANGNAM STYLE
            VideoItem("https://www.youtube.com/embed/dQw4w9WgXcQ"), // Rick Astley - Never Gonna Give You Up
            VideoItem("https://www.youtube.com/embed/3JZ_D3ELwOQ")  // Charlie Puth - We Don't Talk Anymore
        )

        adapter = VideoAdapter(this, videoList)
        recyclerView.adapter = adapter
    }
}
