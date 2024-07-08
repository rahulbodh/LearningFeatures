package com.example.myapplication.youtubeVideoPlayer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class VideoAdapter(private val context: Context, private val videoList: List<VideoItem>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = videoList[position]
        holder.bind(videoItem)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val webView: WebView = itemView.findViewById(R.id.videoWebView)

        @SuppressLint("SetJavaScriptEnabled")
        fun bind(videoItem: VideoItem) {
            val videoStr = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"${videoItem.videoUrl}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"
            webView.webViewClient = WebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.loadDataWithBaseURL(null, videoStr, "text/html", "utf-8", null)
        }
    }
}
