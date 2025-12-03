package com.example.coolor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import yuku.ambilwarna.AmbilWarnaDialog

class InputPage : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_input_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etExpression = findViewById<EditText>(R.id.etExpression)
        val btnGenerate = findViewById<Button>(R.id.btnGenerate)
        val firstColorView = findViewById<View>(R.id.firstColorView)
        val secondColorView = findViewById<View>(R.id.secondColorView)
        val tvColorOne = findViewById<TextView>(R.id.tvColorOne)
        val tvColorTwo = findViewById<TextView>(R.id.tvColorTwo)


        var firstColor = Color.WHITE // to convert to hex: String.format("#%06X", (0xFFFFFF and currentColor))
        var secondColor = Color.WHITE
        firstColorView.setBackgroundColor(firstColor)
        secondColorView.setBackgroundColor(secondColor)
        var firstColorHex = String.format("#%06X", (0xFFFFFF and firstColor))
        var secondColorHex = String.format("#%06X", (0xFFFFFF and secondColor))

        tvColorOne.text = firstColorHex
        tvColorTwo.text = secondColorHex

        firstColorView.setOnClickListener {
            val colorPicker = AmbilWarnaDialog(this, firstColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                    firstColor = color

                    firstColorView.setBackgroundColor(color)
                    firstColorHex = String.format("#%06X", (0xFFFFFF and color))
                    tvColorOne.text = firstColorHex


                }
            })
            colorPicker.show()
        }

        secondColorView.setOnClickListener {
            val colorPicker = AmbilWarnaDialog(this, secondColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {

                    secondColor = color

                    secondColorView.setBackgroundColor(color)
                    secondColorHex = String.format("#%06X", (0xFFFFFF and color))
                    tvColorTwo.text = secondColorHex


                }
            })
            colorPicker.show()
        }



        btnGenerate.setOnClickListener {
            var ch = false
            val str = etExpression.text.toString()
            try {
                Parser(str).parse()
            }catch (e: Exception){
                ch = true
            }
            if("From" !in etExpression.text.toString() && !ch)
                etExpression.setText("$str From $firstColorHex : $secondColorHex")
            startActivity(
                Intent(this, OutputPage::class.java)
                    .putExtra("firstColor", firstColor)
                    .putExtra("secondColor", secondColor)
                    .putExtra("inputString", str)
            )

        }
    }
}