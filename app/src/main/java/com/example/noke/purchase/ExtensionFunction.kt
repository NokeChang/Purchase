package com.example.noke.purchase

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun AppCompatActivity.confirmDialog(sureAction: (a: DialogInterface, b: Int) -> Unit) {

    var builder = AlertDialog.Builder(this)
    builder.setTitle(this.getString(R.string.notification))
    builder.setIcon(R.drawable.ic_warning_black_24dp)
    builder.setMessage(this.getString(R.string.are_you_sure))
    builder.setPositiveButton(this.getString(R.string.sure), sureAction)
    builder.setNegativeButton(this.getString(R.string.cancel), fun(dialog, _) {
        dialog.dismiss()
    })
    builder.setCancelable(true)
    builder.show()
}

fun <T> AppCompatActivity.pickedDateDialog(editText: EditText, date: T) {
    var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)
    var recordDate = when (date) {
        is String -> dateFormat.parse(date as String)
        else -> {
            var now = Date()
            editText.setText(dateFormat.format(now))
            now
        }
    }
    var year = SimpleDateFormat("yyyy", Locale.TAIWAN).format(recordDate).toString().toInt()
    var month = SimpleDateFormat("MM", Locale.TAIWAN).format(recordDate).toString().toInt()
    var day = SimpleDateFormat("dd", Locale.TAIWAN).format(recordDate).toString().toInt()
    var datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, yy, mm, dd
        ->
        var picked = if (mm < 9) {
            if (dd < 10) {
                "$yy-0${mm + 1}-0$dd"
            } else {
                "$yy-0${mm + 1}-$dd"
            }
        } else {
            if (dd < 10) {
                "$yy-${mm + 1}-0$dd"
            } else {
                "$yy-${mm + 1}-$dd"
            }
        }
        editText.setText(picked)
    }, year, month - 1, day)
    editText.setOnClickListener { _ ->
        datePicker.show()
    }
}

fun searchItemPosition(id: Int, ids: Array<Int>): Int {
    var position = 0
    for (j in 0 until ids.size) {
        if (ids[j] == id) {
            position = j
            break
        }
    }
    return position
}

fun getCurrentYearMonth(): String {
    var dateFormat = SimpleDateFormat("yyyy-MM", Locale.TAIWAN)
    var now = Date()
    return dateFormat.format(now)
}

class DateString(var s: String) {
    var year = ""
    var month = ""
    var day = ""

    init {
        parseDate(s)
    }

    private fun parseDate(s: String) {
        var separatorCount = 0
        var stringYear = StringBuilder("")
        var stringMonth = StringBuilder("")
        var stringDay = StringBuilder("")

        //if the first or last char of the input string is separator, alert
        if (s[0] == '/' || s.last() == '/' || s[0] == '-' || s.last() == '-' || s[0] == '.' || s.last() == '.') {
            Log.i("wrong", "the first or last char is . or / or -")
        } else {
            //at first, get separator count of the input string
            for (c in s) {
                if (c == '/' || c == '-' || c == '.') {
                    separatorCount++
                }
            }
            //if there is no separator, result in year
            //if there is one separator, result in year and month
            //if there is two separators, result in year and month and day
            when (separatorCount) {
                0 -> {
                    for (i in 0 until s.length) {
                        stringYear.append(s[i])
                    }
                    stringMonth.append("%%")
                    stringDay.append("%%")
                    Log.i("year", stringYear.toString())
                }
                1 -> {
                    var position = 0
                    for (i in 0 until s.length) {
                        if (s[i] == '/' || s[i] == '-' || s[i] == '.') {
                            position = i
                            break
                        }
                    }
                    for (i in 0 until position) {
                        stringYear.append(s[i])
                    }
                    for (i in (position + 1) until s.length) {
                        stringMonth.append(s[i])
                    }
                    stringDay.append("%%")
                    Log.i("month", stringYear.toString() + "  " + stringMonth.toString())
                }
                2 -> {
                    var position1 = 0
                    var position2 = 0
                    for (i in 0 until s.length) {
                        if (s[i] == '/' || s[i] == '-' || s[i] == '.') {
                            position1 = i
                            break
                        }
                    }
                    for (i in (position1 + 1) until s.length) {
                        if (s[i] == '/' || s[i] == '-' || s[i] == '.') {
                            position2 = i
                            break
                        }
                    }
                    if (position2 == position1 + 1) {
                        Log.i("wrong", "two separator adjacent")
                    } else {
                        for (i in 0 until position1) {
                            stringYear.append(s[i])
                        }
                        for (i in (position1 + 1) until position2) {
                            stringMonth.append(s[i])
                        }
                        for (i in (position2 + 1) until s.length) {
                            stringDay.append(s[i])
                        }
                        Log.i("day", stringYear.toString() + "  " + stringMonth.toString() + "  " + stringDay.toString())
                    }
                }
                else -> {
                    Log.i("wrong", "separator count wrong")
                }
            }
        }
        year = stringYear.toString()
        month = appendZero(stringMonth.toString())
        day = appendZero(stringDay.toString())
    }

    private fun appendZero(s: String): String {
        return when {
            s == "%%" || s == "" -> s
            s.toInt() < 10 -> "0${s.toInt()}"
            else -> s
        }
    }
}

fun getTrackingNumber(prefix: String): String {
    var now = Date()
    var year = SimpleDateFormat("yy", Locale.TAIWAN).format(now).toString()
    var month = SimpleDateFormat("MM", Locale.TAIWAN).format(now).toString()
    var day = SimpleDateFormat("dd", Locale.TAIWAN).format(now).toString()
    var hour = SimpleDateFormat("HH", Locale.TAIWAN).format(now).toString()
    var minute = SimpleDateFormat("mm", Locale.TAIWAN).format(now).toString()
    var second = SimpleDateFormat("ss", Locale.TAIWAN).format(now).toString()
    return "$prefix$year$month$day$hour$minute$second"
}

fun getBitmapFromFile(fileName: String): Bitmap? {
    return try {
        val file = File(NewMerchActivity.PHOTO_PATH, fileName)
        val fileInputStream = FileInputStream(file)
        val option = BitmapFactory.Options()
        option.inSampleSize = 2
        BitmapFactory.decodeStream(fileInputStream, null, option)
    } catch (e: Exception) {
//            Log.e("file error","file io wrong")
//            Log.e("error", Log.getStackTraceString(e))
        null
    }
}

fun AppCompatActivity.savePhotoFromImageView(imageView: ImageView, photoName: String) {
    try {
        var file = File(NewMerchActivity.PHOTO_PATH)
        if (!file.exists()) {
            if (file.mkdir()) {
                Toast.makeText(this, "Creating Directory Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Creating Directory Failed", Toast.LENGTH_SHORT).show()
            }
        }
        if (file.exists() && imageView.drawable is BitmapDrawable) {
            val bitmapDrawable = imageView.drawable as BitmapDrawable
            val photo = bitmapDrawable.bitmap
            val savePhoto = File(file, photoName)
            val fos = FileOutputStream(savePhoto)
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } else {
//                Toast.makeText(this,"File Not Found or No Photo", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
//            Log.e("error", Log.getStackTraceString(e))
    }
}