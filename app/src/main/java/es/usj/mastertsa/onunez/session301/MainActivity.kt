package es.usj.mastertsa.onunez.session301

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import es.usj.mastertsa.onunez.session301.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnTouchListener {
    var cx = 100f
    var cy = 100f
    lateinit var customCanvas : View
    lateinit var bindings : ActivityMainBinding

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        cx = event!!.x
        cy = event.y
        customCanvas.invalidate()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        customCanvas = CustomCanvas(this)
        customCanvas.setOnTouchListener(this)
        bindings.canvasLayout.addView(customCanvas)
    }
}

class CustomCanvas(context : Context) : View(context) {
    private val brush : Paint = Paint()
    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawRGB(255,255,0)
        brush.apply {
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }
        brush.setARGB(255,255,0, 0 )
        val ctx = (context as MainActivity)
        canvas.drawCircle(ctx.cx, ctx.cy, 20f, brush)
    }
}