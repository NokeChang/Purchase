package com.example.noke.purchase

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.merchDao
import kotlinx.android.synthetic.main.activity_update_merch.*

class NewMerchActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_REQUEST_CODE = 0
        const val NO_PHOTO_FILE_NAME = "NoPhoto"
        var PHOTO_PATH = Environment.getExternalStorageDirectory().toString() + "/com.example.noke.purchase"
//        this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)  //private Dir in /sdcard/Android/data/com.example.noke.purchase/files

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_merch)

        saveButton.setOnClickListener {
            saveData()
        }
        photoImageView.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val photoFromCamera = data.extras.get("data") as Bitmap
                    photoImageView.setImageBitmap(photoFromCamera)
                } else {
//                    Toast.makeText(this, "No Photo", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData() {
        var merch = Merch()
        var photoName = ""
        try {
            merch.item = itemText.text.toString()
            merch.remark = remarkText.text.toString()
            if (merch.item == "") {
                val message = getString(R.string.toast_input_again, getString(R.string.item))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                if (photoImageView.drawable is BitmapDrawable) {
                    photoName = "${getTrackingNumber("pho")}.jpeg"
                    merch.photoName = photoName
                } else {
                    merch.photoName = NO_PHOTO_FILE_NAME
                }
                var insertResult = merchDao!!.insert(merch)
                var insertMessage = if (insertResult != -1L) {
                    savePhotoFromImageView(photoImageView, photoName)
                    getString(R.string.toast_add_done)
                } else {
                    getString(R.string.toast_add_failed)
                }
                Toast.makeText(this, insertMessage, Toast.LENGTH_SHORT).show()
                this.finish()
            }
        } catch (e: SQLiteConstraintException) {
            val message = getString(R.string.toast_cannot_repeat, getString(R.string.item))
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("error", Log.getStackTraceString(e))
        }
    }
}
