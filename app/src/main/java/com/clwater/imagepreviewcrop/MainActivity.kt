package com.clwater.imagepreviewcrop

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import com.clwater.library.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    val REQUEST_ALBUM = 1
    val REQUEST_CAMERA = 2
    val TAG = "__MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        button_choose_album.setOnClickListener {
            intoAlbum()
        }
        button_choose_camera.setOnClickListener {
            intoCamera()
        }

    }

    private fun intoAlbum() {
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(albumIntent, REQUEST_ALBUM)
    }


    lateinit var tempFile: File
    /**
     * 跳转到照相机
     */
    private fun intoCamera() {
        //创建拍照存储的图片文件
        tempFile = File(FileUtils.checkDirPath(Environment.getExternalStorageDirectory().path + "/tempImageFile/"), System.currentTimeMillis().toString() + ".jpg")
        Log.d(TAG, "tempFile: " + tempFile)

        //跳转到调用系统相机
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(this@MainActivity, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        }
        startActivityForResult(intent, REQUEST_CAMERA)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode: $requestCode")
        when (requestCode) {
            REQUEST_CAMERA -> {
                val intent = Intent()
                intent.setClass(this, CropImageAcivity::class.java)
                intent.data = Uri.fromFile(tempFile)
                startActivity(intent)
            }
            REQUEST_ALBUM -> {
                val intent = Intent()
                intent.setClass(this, CropImageAcivity::class.java)
                intent.data = data?.data
                startActivity(intent)
            }
//            REQUEST_CROP -> mPictureIb.setImageURI(Uri.fromFile(mImageFile))
        }
    }
}
