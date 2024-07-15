package com.example.myapplication.mindMap

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class ConceptMapView(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {


    private var nodes: List<Node> = emptyList() // Populate with data from your server

    fun setNodes(nodes: List<Node>) {
        this.nodes = nodes
        removeAllViews() // Clear previous nodes
        for (node in nodes) {
            val nodeView = ConceptNodeView(context, null).apply {
                this.node = node // Assign data to the view
            }
            addView(nodeView)
        }
        requestLayout() // Trigger layout recalculation
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val nodeWidth = 100 // Customize node size
        val nodeHeight = 100
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(i * nodeWidth, 0, (i + 1) * nodeWidth, nodeHeight) // Basic horizontal layout
        }
    }

}