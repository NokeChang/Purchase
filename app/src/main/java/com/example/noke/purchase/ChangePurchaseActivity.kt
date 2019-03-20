package com.example.noke.purchase

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.merchDao
import com.example.noke.purchase.MainActivity.Companion.purchaseDao
import kotlinx.android.synthetic.main.activity_update_purchase.*
import java.text.SimpleDateFormat
import java.util.*

class ChangePurchaseActivity : AppCompatActivity() {

    private var merchIds: Array<Int> = arrayOf(0)
    private var selectedMerchId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_purchase)
    }

    override fun onStart() {
        super.onStart()

        merchSpinner.adapter = ArrayAdapter<String>(this, R.layout.single_item, merchDao!!.getItems())
        merchIds = merchDao!!.getIds()
        merchSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMerchId = merchIds[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i("spinner","")
            }
        }

        var record = purchaseDao!!.getRecordById(this.intent.extras.getInt("id"))
        numberText.setText(record.number)
        selectedMerchId = record.mid
        merchSpinner.setSelection(searchItemPosition(selectedMerchId, merchIds))
        quantityText.setText(record.quantity.toString())
        dateText.setText(record.date)
        priceText.setText(record.price.toString())

        this.pickedDateDialog(dateText, record.date)

        saveButton.setOnClickListener {
            val purchase = Purchase()
            try {
                purchase.id = record.id
                purchase.number = numberText.text.toString()
                purchase.mid = selectedMerchId
                purchase.quantity = quantityText.text.toString().toInt()
                purchase.date = dateText.text.toString()
                purchase.price = priceText.text.toString().toInt()
                if (purchase.number == "") {
                    val message = getString(R.string.toast_input_again, getString(R.string.number))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    purchaseDao!!.update(purchase)
                    Toast.makeText(this, getString(R.string.toast_update_done), Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            }catch (e: NumberFormatException) {
                val message = getString(R.string.toast_input_again_2, getString(R.string.quantity), getString(R.string.price))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
