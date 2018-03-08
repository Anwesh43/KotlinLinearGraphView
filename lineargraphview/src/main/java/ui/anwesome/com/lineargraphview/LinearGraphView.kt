package ui.anwesome.com.lineargraphview

/**
 * Created by anweshmishra on 08/03/18.
 */
import android.content.*
import android.graphics.*
import android.view.*

class LinearGraphView(ctx : Context, var y_points : Array<Float>) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas : Canvas) {

    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}