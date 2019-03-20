package com.example.noke.purchase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.merchDao
import com.example.noke.purchase.MainActivity.Companion.clientDao
import com.example.noke.purchase.MainActivity.Companion.orderDao
import kotlinx.android.synthetic.main.activity_update_order.*
import java.util.*


class NewOrderActivity : AppCompatActivity() {

    private var merchIds: Array<Int> = arrayOf(0)
    private var clientIds: Array<Int> = arrayOf(0)
    private var selectedMerchId = 0
    private var selectedClientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_order)

        saveButton.setOnClickListener {
            saveOrder()
        }

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
        this.pickedDateDialog(dateText, Date())
        numberText.setText(getTrackingNumber("S"))
    }

    private fun saveOrder(){
        var order = Order()
        try {
            order.number = numberText.text.toString()
            order.mid = selectedMerchId
            order.cid = selectedClientId
            order.quantity = quantityText.text.toString().toInt()
            order.date = dateText.text.toString()
            order.price = priceText.text.toString().toInt()
            if (order.number == "") {
                val message = getString(R.string.toast_input_again, getString(R.string.number))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else {
                var insertResult = orderDao!!.insert(order)
                var insertMessage = when (insertResult) {
                    -1L -> getString(R.string.toast_add_failed)
                    else -> getString(R.string.toast_add_done)
                }
                Toast.makeText(this, insertMessage, Toast.LENGTH_SHORT).show()
                this.finish()
            }
        } catch (e: NumberFormatException) {
            val message = getString(R.string.toast_input_again_2, getString(R.string.quantity), getString(R.string.price))
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
