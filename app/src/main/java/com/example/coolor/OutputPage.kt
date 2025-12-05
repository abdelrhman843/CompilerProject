package com.example.coolor

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class OutputPage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_output_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun generateColor(firstColor: Int, secondColor: Int, numberOfColors: Int): MutableList<Int> {
            var rGab = (secondColor.red - firstColor.red).toDouble() / (numberOfColors - 1)
            var gGab = (secondColor.green - firstColor.green).toDouble() / (numberOfColors - 1)
            var bGab = (secondColor.blue - firstColor.blue).toDouble() / (numberOfColors - 1)

            val colors = mutableListOf<Int>()
            var r = 0.0
            var b = 0.0
            var g = 0.0
            colors.add(firstColor)
            for(i in 1..<numberOfColors){
                r = firstColor.red + i * rGab
                g = firstColor.green + i * gGab
                b = firstColor.blue + i * bGab
                colors.add(Color.rgb(r.toInt(), g.toInt(), b.toInt()))
            }
            colors.add(secondColor)

            return colors
        }
        val views = mutableListOf<View>(
            findViewById<View>(R.id.viewOne),
            findViewById<View>(R.id.viewTwo),
            findViewById<View>(R.id.viewThree),
            findViewById<View>(R.id.viewFour),
            findViewById<View>(R.id.viewFive),
            findViewById<View>(R.id.viewSix),
            findViewById<View>(R.id.viewSeven),
            findViewById<View>(R.id.viewEight),
            findViewById<View>(R.id.viewNine)
        )

        val textViews = mutableListOf<TextView>(
            findViewById<TextView>(R.id.tvOne),
            findViewById<TextView>(R.id.tvTwo),
            findViewById<TextView>(R.id.tvThree),
            findViewById<TextView>(R.id.tvFour),
            findViewById<TextView>(R.id.tvFive),
            findViewById<TextView>(R.id.tvSix),
            findViewById<TextView>(R.id.tvSeven),
            findViewById<TextView>(R.id.tvEight),
            findViewById<TextView>(R.id.tvNine)
        )



        val firstColor = intent.getIntExtra("firstColor", -1)
        val secondColor = intent.getIntExtra("secondColor", -1)
        val inputString = intent.getStringExtra("inputString")!!
        val tvResult = findViewById<TextView>(R.id.tvResult)
        var numberOfColors = 0
        val firstHexColor = String.format("#%06X", (0xFFFFFF and firstColor))
        val secondHexColor = String.format("#%06X", (0xFFFFFF and secondColor))

        try {
            val result = Parser(inputString).parse()
            tvResult.text = "Successfully Parsed:\nCommand: ${result.command}\n" +
                    " Number of Colors: ${result.numberOfColors}\nFirst Color: $firstHexColor\nLast Color: $secondHexColor"
            numberOfColors = result.numberOfColors
        } catch (e: Exception){
            tvResult.text = "Parsing Failed:\n ${e.message}"
        }

        val cardView3 = findViewById<CardView>(R.id.cardView3)
        val cardView2 = findViewById<CardView>(R.id.cardView2)
        val cardView = findViewById<CardView>(R.id.cardView)

        cardView.alpha = 1F
        if(numberOfColors > 3){
            cardView2.alpha = 1F
        }
        if(numberOfColors > 6){
            cardView3.alpha = 1F
        }


        val colors = generateColor(firstColor, secondColor, numberOfColors)


        for (i in 0..<numberOfColors){
            views[i].setBackgroundColor(colors[i])
            textViews[i].text = String.format("#%06X", (0xFFFFFF and colors[i]))
        }



    }
}