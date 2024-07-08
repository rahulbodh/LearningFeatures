package com.example.myapplication.modelBottomSheet

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R

class BottomSheetMainActivity : AppCompatActivity() {
    private lateinit var button : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bottom_sheet_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        button = findViewById(R.id.button)
        button.setOnClickListener {
            showModelBottomSheet()
        }
    }

    private fun showModelBottomSheet() {

        val modelBottomSheet = ModelBottomSheet()
        modelBottomSheet.show(supportFragmentManager, "BottomSheet" )
    }
}