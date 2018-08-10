package com.clwater.library

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


class CropView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    val showPaint = Paint()
    val strokePaint = Paint()

    var backgroundLayerColor: Long

    var strokeColor: Long
    var strokeWidth: Float
    var cropType: CropType

    var strokePading: Float
    var customizePath: Path

    enum class CropType {
        Circle,
        Squre,
        Diamond,
        Other
    }


    init {


        backgroundLayerColor = 0xA8000000
        strokeColor = 0xFFFFFFFF
        strokeWidth = 1f
        strokePading = 0f

        showPaint.isAntiAlias = true
        showPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)


        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = strokeColor.toInt()
        strokePaint.strokeWidth = strokeWidth
        strokePaint.isAntiAlias = true

        cropType = CropType.Circle

        customizePath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        drawBackgroundLayer(canvas)
    }

    private fun drawBackgroundLayer(canvas: Canvas) {
        canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        //设置背景
        canvas.drawColor(backgroundLayerColor.toInt())

        drawSelect(canvas)

        canvas.restore()
    }

    private fun drawSelect(canvas: Canvas) {
        val pixelPadding = ViewUtils.dip2px(this.context, strokePading).toFloat()
        when (cropType) {
            CropType.Circle -> {
                canvas.drawCircle(width / 2f, height / 2f, width / 2f - pixelPadding, showPaint)
                canvas.drawCircle(width / 2f, height / 2f, width / 2f - pixelPadding, strokePaint)
            }
            CropType.Squre -> {
                val squreSize = width
                canvas.drawRect(pixelPadding, height / 2f - squreSize / 2f + pixelPadding,
                        width - pixelPadding, height / 2f + squreSize / 2f - pixelPadding,
                        showPaint)
                canvas.drawRect(pixelPadding, height / 2f - squreSize / 2f + pixelPadding,
                        width - pixelPadding, height / 2f + squreSize / 2f - pixelPadding,
                        strokePaint)
            }
            CropType.Diamond -> {
                val linePath = Path()

                linePath.moveTo(0f + pixelPadding, height / 2f)
                linePath.lineTo(width / 2f, height / 2f - width / 2f + pixelPadding)
                linePath.lineTo(width.toFloat() - pixelPadding, height / 2f)
                linePath.lineTo(width / 2f, height / 2f + width / 2f - pixelPadding)

                canvas.drawPath(linePath, showPaint)
                canvas.drawPath(linePath, strokePaint)
            }
            CropType.Other -> {
                canvas.drawPath(customizePath, showPaint)
                canvas.drawPath(customizePath, strokePaint)
            }
        }
    }

    fun getCropRect(): Rect {
        val rect = Rect()
        rect.left = (this.width / 2 - (width / 2f - ViewUtils.dip2px(this.context, strokePading))).toInt()
        rect.right = (this.width / 2 + (width / 2f - ViewUtils.dip2px(this.context, strokePading))).toInt()
        rect.top = (this.height / 2 - (width / 2f - ViewUtils.dip2px(this.context, strokePading))).toInt()
        rect.bottom = (this.height / 2 + (width / 2f - ViewUtils.dip2px(this.context, strokePading))).toInt()
        return rect
    }

}