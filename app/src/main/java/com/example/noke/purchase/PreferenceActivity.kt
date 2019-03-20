package com.example.noke.purchase

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_preference.*

class PreferenceActivity : AppCompatActivity() {

    companion object {
        const val KEY_LOW_STOCK = "low_stock"
        const val KEY_SEARCH_WORD_MERCH = "search_word_merch"
        const val KEY_SEARCH_WORD_ORDER = "search_word_order"
        const val KEY_SEARCH_WORD_CLIENT = "search_word_client"
        const val KEY_SEARCH_WORD_PURCHASE = "search_word_purchase"
        const val KEY_SEARCH_DATE_PURCHASE = "search_date_purchase"
        const val KEY_SEARCH_DATE_ORDER = "search_date_order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        var sharedPreference = this.getSharedPreferences("user", MODE_PRIVATE)
        lowStockText.setText(sharedPreference.getInt(KEY_LOW_STOCK, 0).toString())

        saveButton.setOnClickListener {
            var lowStock = lowStockText.text.toString().toInt()
            sharedPreference.edit()
                    .putInt(KEY_LOW_STOCK, lowStock)
                    .apply()
            this.finish()
        }
    }
}
