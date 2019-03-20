package com.example.noke.purchase

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.merchDao
import kotlinx.android.synthetic.main.activity_update_merch.*

class ChangeMerchActivity : AppCompatActivity() {
    private var hasPhotoIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_merch)
    }

    override fun onStart() {
        super.onStart()
        var record = merchDao!!.getRecordById(this.intent.extras.getInt("id"))
        itemText.setText(record.item)
        remarkText.setText(record.remark)
        if(!hasPhotoIn && record.photoName != NewMerchActivity.NO_PHOTO_FILE_NAME) {
            var bitmap = getBitmapFromFile(record.photoName)
            if(bitmap != null) {
                photoImageView.setImageBitmap(getBitmapFromFile(record.photoName))
            }
        }

        saveButton.setOnClickListener {
            val merch = Merch()
            merch.id = record.id
            try {
                merch.item = itemText.text.toString()
                merch.remark = remarkText.text.toString()
                if (merch.item == "") {
                    val message = getString(R.string.toast_input_again, getString(R.string.item))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    if (photoImageView.drawable is BitmapDrawable) {
                        merch.photoName = if (record.photoName == NewMerchActivity.NO_PHOTO_FILE_NAME) {
                            "${getTrackingNumber("pho")}.jpeg"
                        } else {
                            record.photoName
                        }
                    } else {
                        merch.photoName = NewMerchActivity.NO_PHOTO_FILE_NAME
                    }
                    merchDao!!.update(merch)
                    if (hasPhotoIn) {
                        savePhotoFromImageView(photoImageView, merch.photoName)
                    }
                    Toast.makeText(this, getString(R.string.toast_update_done), Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            } catch (e: SQLiteConstraintException){
                val message = getString(R.string.toast_cannot_repeat, getString(R.string.item))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        photoImageView.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(callCameraIntent, NewMerchActivity.CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            NewMerchActivity.CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val photoFromCamera = data.extras.get("data") as Bitmap
                    photoImageView.setImageBitmap(photoFromCamera)
                    hasPhotoIn = true
                } else {
//                    Toast.makeText(this, "No Photo", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "Something Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
