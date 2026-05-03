package com.arestov.playlistmaker.ui.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.arestov.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val playBitmap: Bitmap
    private val pauseBitmap: Bitmap
    private val imageRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isPlaying = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView)
        val playResId = typedArray.getResourceId(R.styleable.PlaybackButtonView_playImage, 0)
        val pauseResId = typedArray.getResourceId(R.styleable.PlaybackButtonView_pauseImage, 0)
        typedArray.recycle()

        playBitmap = AppCompatResources.getDrawable(context, playResId)!!.toBitmap()
        pauseBitmap = AppCompatResources.getDrawable(context, pauseResId)!!.toBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        canvas.drawBitmap(bitmap, null, imageRect, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                isPlaying = !isPlaying
                invalidate()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setIsPlaying(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }
}