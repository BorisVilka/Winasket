package win.winbasket.ball.winline.game


import android.R.attr
import android.R.attr.max
import android.R.attr.min
import android.R.attr.x
import android.R.attr.y
import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Math.max
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


class GameView(val ctx: Context, val attributeSet: AttributeSet): SurfaceView(ctx,attributeSet) {


    var millis = 0
    var paused = true
    private var paintB: Paint = Paint(Paint.DITHER_FLAG)
    private var paintT: Paint = Paint(Paint.DITHER_FLAG).apply {
        textSize = 120f
        color = Color.WHITE
        typeface = ctx.resources.getFont(R.font.font)
    }
    var destroy = false
    private var listener: EndListener? = null
    val updateThread = Thread {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if(!destroy ) {
                    if(!paused) millis++
                    update.run()
                }
            }
        }, 500, 16)
    }

    var bx = 0f
    var by = 0f

    var width1 = 0f


    var boom = BitmapFactory.decodeResource(ctx.resources,R.drawable.boom)
    var basket = BitmapFactory.decodeResource(ctx.resources,ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getInt("basket",R.drawable.basket))
    var drag = BitmapFactory.decodeResource(ctx.resources,R.drawable.drag)

    var images = arrayOf(
        BitmapFactory.decodeResource(ctx.resources,ctx.getSharedPreferences("prefs",Context.MODE_PRIVATE).getInt("ball",R.drawable.ball1)),
        BitmapFactory.decodeResource(ctx.resources,R.drawable.money),
        BitmapFactory.decodeResource(ctx.resources,R.drawable.bomb)
    )

    init {
        boom = Bitmap.createScaledBitmap(boom,boom.width/2,boom.height/2,true)
        drag = Bitmap.createScaledBitmap(drag,drag.width/2,drag.height/2,true)
        for(i in images.indices) images[i] = Bitmap.createScaledBitmap(images[i],images[i].width/4,images[i].height/4,true)
        basket = Bitmap.createScaledBitmap(basket,basket.width/2,basket.height/2,true)
        holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                val canvas = holder.lockCanvas()
                if(canvas!=null) {
                    width1 = canvas.width.toFloat()
                    bx = canvas.width/2f-basket.width/2f
                    by = canvas.height-basket.height*1.5f
                    draw(canvas)
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                destroy = true
                updateThread.interrupt()
            }

        })

        updateThread.start()
    }

    var started = false
    var x1 = 0f
    var y1 = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!paused) {
            when(event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    if(isEnd) paused = false
                    x1 = event!!.x
                    y1 = event!!.y
                    if(x1>=bx && x1<=bx+basket.width && y1>=by && y1<by+basket.height) {
                        started = true

                    }
                }
                MotionEvent.ACTION_UP -> {
                    started = false
                }
                MotionEvent.ACTION_MOVE -> {
                    if(started) {
                        x1 = event!!.x
                       // y1 = event!!.y
                        bx =x1-basket.width/2
                       // by =y1-basket.height/2
                        bx = max(bx,35f)
                        bx = min(bx,width1-basket.width)
                    }
                }
            }
        }
        return true
    }
    var tmp = false
    var time = 0
    var delta = 0f

    /*override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!paused) {
            when(event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    if(bx>event.x) {
                        delta = -8f
                    } else if(event.x>bx+basket.width) delta = 8f
                }
                MotionEvent.ACTION_UP -> {
                    delta = 0f
                }
                MotionEvent.ACTION_MOVE -> {
                    if(bx>event.x) {
                        delta = -8f
                    } else if(event.x>bx+basket.width) delta = 8f
                }
            }
        }
        return true
    }*/


    val random = Random()
    var isEnd = false
    var health = 3
    var gold = 0
    var balls = 0
    var list = mutableListOf<Model>()


    val update = Runnable{
        try {
            val canvas = holder.lockCanvas()
            if(!paused) {
              //  bx += delta
              //  bx = max(0f,bx)
              //  bx = min(bx,canvas.width-basket.width.toFloat())
                if(millis >= time + 15 && tmp) tmp = false
                while(list.size<8) {
                    list.add(Model(
                        random.nextInt(canvas.width-images[0].width).toFloat(),
                        -1*random.nextInt(canvas.height).toFloat(),
                        random.nextInt(images.size)
                    ))
                }
                var i = 0
                while(i<list.size) {
                    val j = list[i]
                    j.y+=5
                    if(
                        bx<=j.x && j.x+images[j.ind].width<=bx+basket.width
                        &&
                        j.y>=by
                    ) {
                        when(j.ind) {
                            0 -> balls++
                            1 -> gold++
                            2 -> {
                                health--
                                time = millis
                                tmp = true
                            }
                        }
                        list.removeAt(i)
                    } else i++
                }
                canvas.drawPaint(Paint().apply {
                    shader = LinearGradient(0f,0f,0.toFloat(),canvas.height.toFloat(), ctx.getColor(R.color.grey),Color.BLACK,Shader.TileMode.MIRROR)
                })
                for(j in list) canvas.drawBitmap(images[j.ind],j.x,j.y,paintB)
                canvas.drawBitmap(basket,bx,by,paintB)
                if(tmp) {
                    val s = "BOOM"
                    canvas.drawText(s,canvas.width/2-paintT.measureText(s)/2f,canvas.height/2f,paintT)
                }
                val tmp = 60-millis/50
                if(tmp<=0 || health<=0) isEnd = true
                if (isEnd) {
                    paused = true
                    if (listener != null) listener!!.end()
                }
            } else {
                canvas.drawPaint(Paint().apply {
                    shader = LinearGradient(0f,0f,0.toFloat(),canvas.height.toFloat(), ctx.getColor(R.color.grey),Color.BLACK,Shader.TileMode.MIRROR)
                })
                canvas.drawBitmap(basket,bx,by,paintB)
            }
            if(isEnd) canvas.drawBitmap(boom,bx-boom.width/2f,by-boom.height/2f,paintB)
            holder.unlockCanvasAndPost(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        listener!!.score(0)
    }

     fun setEndListener(list: EndListener) {
        this.listener = list
    }
    fun togglePause() {
        paused = !paused

    }
   companion object {
        data class Model(var x: Float, var y: Float,var ind: Int)
        interface EndListener {
            fun end();
            fun score(score: Int);
        }

    }
}