package com.example.noke.purchase

import android.database.sqlite.SQLiteConstraintException
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.clientDao
import kotlinx.android.synthetic.main.activity_update_client.*

class ChangeClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_client)
    }

    override fun onStart() {
        super.onStart()
        var record = clientDao!!.getRecordById(this.intent.extras.getInt("id"))
        nameText.setText(record.name)
        localeText.setText(record.locale)

        saveButton.setOnClickListener {
            val client = Client()
            client.id = record.id
            try {
                client.name = nameText.text.toString()
                client.locale = localeText.text.toString()
                if (client.name == "") {
                    val message = getString(R.string.toast_input_again, getString(R.string.name))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }else {
                    clientDao!!.update(client)
                    Toast.makeText(this, getString(R.string.toast_update_done), Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            } catch (e: SQLiteConstraintException){
                val message = getString(R.string.toast_cannot_repeat, getString(R.string.name))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
