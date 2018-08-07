package com.clwater.library

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout

class CropLyaout : RelativeLayout {

    lateinit var imageView: ImageView
    lateinit var cropView: CropView

    var cropType = CropView.CropType.Circle
    var imageURI: Uri? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CropLayout)
        val cropTypeInt = typedArray.getInt(R.styleable.CropLayout_cropType, 1)

        Log.d("gzb", "cropTypeInt: $cropTypeInt")


        when (cropTypeInt) {
            1 -> cropType = CropView.CropType.Circle
            2 -> cropType = CropView.CropType.Squre
            3 -> cropType = CropView.CropType.Diamond
            4 -> cropType = CropView.CropType.Other
        }


        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        initView()
    }


    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.croplayout, this, true)
        imageView = findViewById(R.id.crop_imageview)
        cropView = findViewById(R.id.crop_cropView)

        initCropView()
        initImageView()
    }

    private fun initImageView(){

    }

    private fun setImageView(){
        imageView.setImageURI(imageURI)
    }


    private fun initCropView() {
        cropView.cropType = cropType
    }

    fun initImageURI(data: Uri?) {
        imageURI = data
        setImageView()

    }


}