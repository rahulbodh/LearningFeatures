package com.example.myapplication.mindMap

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R

class MindMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mind_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val conceptMapView = findViewById<ConceptMapView>(R.id.conceptMapView)
        val nodes = listOf(
            Node(1, "Node 1"),
            Node(2, "Node 2"),
            Node(3, "Node 3"),
            Node(4, "Node 4"),
            Node(5, "Node 5"),
            Node(6, "Node 6"),
            Node(1, "Node 1"),
            Node(2, "Node 2"),
            Node(3, "Node 3"),
            Node(4, "Node 4"),
            Node(5, "Node 5"),
            Node(6, "Node 6")
        )
        conceptMapView.setNodes(nodes)
    }
}