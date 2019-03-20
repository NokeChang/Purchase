package com.example.noke.purchase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.clientDao
import com.example.noke.purchase.MainActivity.Companion.merchDao
import com.example.noke.purchase.MainActivity.Companion.orderDao
import kotlinx.android.synthetic.main.activity_update_order.*

class ChangeOrderActivity : AppCompatActivity() {

    private var merchIds: Array<Int> = arrayOf(0)
    private var clientIds: Array<Int> = arrayOf(0)
    private var selectedMerchId = 0
    private var selectedClientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_order)
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

        clientSpinner.adapter = ArrayAdapter<String>(this, R.layout.single_item, clientDao!!.getNames())
        clientIds = clientDao!!.getIds()
        clientSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedClientId = clientIds[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i("spinner","")
            }
        }

        var record = orderDao!!.getRecordById(this.intent.extras.getInt("id"))
        numberText.setText(record.number)
        selectedMerchId = record.mid
        merchSpinner.setSelection(searchItemPosition(selectedMerchId, merchIds))
        selectedClientId = record.cid
        clientSpinner.setSelection(searchItemPosition(selectedClientId, clientIds))
        quantityText.setText(record.quantity.toString())
        dateText.setText(record.date)
        priceText.setText(record.price.toString())

        this.pickedDateDialog(dateText, record.date)

        saveButton.setOnClickListener {
            val order = Order()
            try {
                order.id = record.id
                order.number = numberText.text.toString()
                order.mid = selectedMerchId
                order.cid = selectedClientId
                order.quantity = quantityText.text.toString().toInt()
                order.date = dateText.text.toString()
                order.price = priceText.text.toString().toInt()
                if (order.number == "") {
                    val message = getString(R.string.toast_input_again, getString(R.string.number))
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }else {
                    orderDao!!.update(order)
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
