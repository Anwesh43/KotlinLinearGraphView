package ui.anwesome.com.lineargraphview

/**
 * Created by anweshmishra on 08/03/18.
 */
import android.app.Activity
import android.content.*
import android.graphics.*
import android.view.*
import java.util.concurrent.ConcurrentLinkedQueue

class LinearGraphView(ctx : Context, var y_points : Array<Float>) : View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }
    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {
        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if(Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(scale)

            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }
    data class ContainerState(var n : Int, var j : Int = 0, var dir : Int = 1) {
        fun incrementCounter() {
            j += dir
            if(j == n || j == -1) {
                dir *= -1
                j+=dir
            }
        }
        fun executeCb(cb : (Int) -> Unit) {
            cb(j)
        }
    }
    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if(animated) {
                try {
                    updatecb()
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class Line(var x : Float, var y : Float, var prevX : Float, var prevY : Float) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawLine(prevX, prevY, prevX + (x - prevX) * state.scale, prevY + (y - prevY) * state.scale, paint)
        }
        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
    data class LinearGraph(var w: Float , var h : Float, var y_points: Array<Float>) {
        val lines : ConcurrentLinkedQueue<Line> = ConcurrentLinkedQueue()
        val state = ContainerState(y_points.size)
        init {
            var x = w/20
            var y = 19*w/20
            y_points.forEach {
                val gap = (9*w/10)/y_points.size
                lines.add(Line(x + gap, it, x, y))
                x += gap
                y = it
            }
        }
        fun draw(canvas : Canvas, paint : Paint) {
            lines.forEach {
                it.draw(canvas, paint)
            }
        }
        fun update(stopcb : (Float,Int) -> Unit) {
            state.executeCb {
                lines.at(it)?.update{ scale ->
                    state.incrementCounter()
                    state.executeCb {j ->
                        stopcb(scale,j)
                    }
                }
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            state.executeCb {
                lines.at(it)?.startUpdating {
                    startcb()
                }
            }
        }
    }
    data class Renderer(var view : LinearGraphView, var time : Int = 0) {
        val animator : Animator = Animator(view)
        var graph : LinearGraph ?= null
        fun render(canvas : Canvas, paint : Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                graph = LinearGraph(w, h, view.y_points)
                paint.color = Color.parseColor("#303F9F")
                paint.strokeWidth = canvas.width/50f
                paint.strokeCap = Paint.Cap.ROUND
            }
            canvas.drawColor(Color.parseColor("#212121"))
            graph?.draw(canvas, paint)
            time++
            animator.animate {
                graph?.update {scale,j ->
                    animator.stop()
                }
            }
        }
        fun handleTap() {
            graph?.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity : Activity, y_points : Array<Float>):LinearGraphView {
            val view = LinearGraphView(activity, y_points)
            activity.setContentView(view)
            return view
        }
    }
}
fun ConcurrentLinkedQueue<LinearGraphView.Line>.at(i : Int):LinearGraphView.Line? {
    var j = 0
    forEach {
        if(j == i) {
            return it
        }
        j++
    }
    return null
}