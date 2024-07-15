package com.example.myapplication.mindMap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ConceptNodeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var node: Node? = null


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE // Customize node color
        style = Paint.Style.FILL
        strokeWidth = 2f
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f // Adjust text size
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        node?.let {
            val radius = 50f // Customize node radius
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
            val textX = width / 2f - textPaint.measureText(it.text) / 2f
            val textY = height / 2f + textPaint.textSize / 3f
            canvas.drawText(it.text, textX, textY, textPaint)
        }
    }
}