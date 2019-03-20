package com.example.noke.purchase

import android.database.sqlite.SQLiteConstraintException
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.clientDao
import kotlinx.android.synthetic.main.activity_update_client.*

class NewClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_client)

        saveButton.setOnClickListener {
            var client = Client()
            try {
                client.name = nameText.text.toString()
                client.locale = localeText.text.toString()
                if (client.name == "") {
                    val message = getString(R.string.toast_input_again, getString(R.string.name))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    var insertResult = clientDao!!.insert(client)
                    var insertMessage = when (insertResult) {
                        -1L -> getString(R.string.toast_add_failed)
                        else -> getString(R.string.toast_add_done)
                    }
                    Toast.makeText(this, insertMessage, Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            } catch (e: SQLiteConstraintException){
                val message = getString(R.string.toast_cannot_repeat, getString(R.string.name))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
