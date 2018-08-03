package com.clwater.imagepreviewcrop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.clwater.library.CropView
import com.clwater.library.FileUtils
import kotlinx.android.synthetic.main.activity_cropimage.*

class CropImageAcivity : AppCompatActivity() {
    val TAG = "__CropImageAcivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropimage)

        init()
    }

    private fun init() {

//        val path = FileUtils.getRealFilePathFromUri(this, intent.dat)
        imageview_crop.setImageURI(intent.data)
        cropView.cropType = CropView.CropType.Circle
        cropView.strokePading = 10f
//        imageview_crop.setImageResource(date)
    }




}