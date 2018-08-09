package com.clwater.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout

class CropLyaout : RelativeLayout {

    enum class PointAction {
        DRAG,
        ZOOM,
        NONE
    }


    lateinit var imageView: CropImageView
    lateinit var cropView: CropView

    var cropType = CropView.CropType.Circle

    var start = PointF()
    var zoomStartPointF = PointF()
    var zoomStart: Float = 0f
    var mode = PointAction.NONE
    var selectSize = 0f

    var strokePading = 0f

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CropLayout)
        val cropTypeInt = typedArray.getInt(R.styleable.CropLayout_cropType, 1)

        strokePading = 50f

        when (cropTypeInt) {
            1 -> cropType = CropView.CropType.Circle
            2 -> cropType = CropView.CropType.Squre
            3 -> cropType = CropView.CropType.Diamond
            4 -> cropType = CropView.CropType.Other
        }
        initView()
    }

    private fun initCropView() {
        cropView.cropType = cropType
        cropView.strokePading = strokePading
    }


    private fun initView() {
        imageView = CropImageView(context)
        imageView.scaleType = ImageView.ScaleType.MATRIX
        cropView = CropView(context)
        val layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(imageView, layoutParams)
        this.addView(cropView, layoutParams)

        initCropView()
//        initImageView()
    }

    fun initImageURI(data: Uri) {
        imageView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                initImageView(data)
                imageView.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })
    }

    val imageMatrix = Matrix()

    private fun initImageView(data: Uri) {
        selectSize = cropView.width - ViewUtils.dip2px(context, strokePading).toFloat() * 2

        //获取路径
        val path = FileUtils.getRealFilePathFromUri(context, data)
        //获取裁剪后图片
        var bitmap = ViewUtils.decodeSampledBitmap(path, 720, 1280)


        //调整竖屏拍摄旋转问题
        val rotation = ViewUtils.getExifOrientation(path) //获取旋转角度
        val rotaitonMatrix = Matrix()
        rotaitonMatrix.setRotate(rotation.toFloat())
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotaitonMatrix, true)

        var scale: Float

        if (bitmap.width >= bitmap.height) {
            scale = selectSize / bitmap.width
            val heightScale = selectSize / (bitmap.height * scale)
            if (heightScale > 1) {
                scale *= heightScale
            }
        } else {
            scale = selectSize / bitmap.height
            val widthScale = selectSize / (bitmap.width * scale)
            if (widthScale > 1) {
                scale *= widthScale
            }
        }

        imageMatrix.postScale(scale, scale)

        imageMatrix.postTranslate(imageView.width / 2f - bitmap.width * scale / 2,
                imageView.height / 2f - bitmap.height * scale / 2)


        imageView.imageMatrix = imageMatrix
        imageView.setImageBitmap(bitmap)

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mode = PointAction.DRAG
                start.set(event.x, event.y)
            }
            MotionEvent.ACTION_POINTER_2_DOWN -> {
                zoomStart = pointDistance(event)
                if (zoomStart > 10f) {
                    mode = PointAction.ZOOM
                    zoomStartPointF.set(pointCenter(event))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (mode) {
                    PointAction.DRAG -> {
                        val dx = event.x - start.x
                        val dy = event.y - start.y
                        start.set(event.x, event.y)

                        imageMatrix.postTranslate(dx, dy)
                        imageView.imageMatrix = imageMatrix
                    }
                    PointAction.ZOOM -> {
                        val zoomMove = pointDistance(event)
                        val scale = zoomMove / zoomStart
                        imageMatrix.postScale(scale, scale, zoomStartPointF.x, zoomStartPointF.y)
                        imageView.imageMatrix = imageMatrix
                        zoomStart = zoomMove

                    }
                    PointAction.NONE -> {
                    }
                }

            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = PointAction.NONE
            }
        }
        return true
    }

    private fun pointCenter(event: MotionEvent): PointF? {
        val pointF = PointF()
        pointF.set((event.getX(0) + event.getX(1)) / 2f,
                (event.getY(0) + event.getY(1)) / 2f)
        return pointF

    }


    fun pointDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }
}