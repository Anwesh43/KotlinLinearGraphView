package ui.anwesome.com.kotlinlineargraphview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.lineargraphview.LinearGraphView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinearGraphView.create(this, arrayOf(300f,200f, 100f, 1000f, 400f, 600f, 700f, 500f, 900f))
    }
}
