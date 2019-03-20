package com.example.noke.purchase

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.merchDao
import com.example.noke.purchase.MainActivity.Companion.purchaseDao
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_DATE_PURCHASE
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_WORD_PURCHASE
import kotlinx.android.synthetic.main.activity_check_list.*
import kotlinx.android.synthetic.main.holder_purchase2.view.*


class PurchaseListActivity : AppCompatActivity() {

    class DataPackage<T>(var records: List<T> = listOf())
    var purchasePackage: DataPackage<PurchaseA>? = null
    var recordSelected = -1
    var searchWord = ""
    var searchDate = DateString("0")
    var sharedPreference: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list)
        setSupportActionBar(toolbar)
        recordSelected = -1
        sharedPreference = this.getSharedPreferences("user", MODE_PRIVATE)

        searchWord = "%%"
        purchasePackage = DataPackage()
        listRecyclerView.layoutManager = GridLayoutManager(this, 2)
        listRecyclerView.setHasFixedSize(true)
        listRecyclerView.adapter = ListAdapter()

        //change layout dynamically
        toolbar.title = this.getString(R.string.main_title_purchase)
        searchText.hint = this.getString(R.string.hint_search_item)
        searchText2.hint = this.getString(R.string.hint_search_date)

        searchText.setText(sharedPreference!!.getString(KEY_SEARCH_WORD_PURCHASE,""))
        searchText2.setText(sharedPreference!!.getString(KEY_SEARCH_DATE_PURCHASE,getCurrentYearMonth()))
        searchDate = DateString(searchText2.text.toString())

        deleteFab.setOnClickListener {
            confirmDialog( fun(_, _) {
                val purchase = Purchase()
                purchase.id = recordSelected
                purchaseDao!!.delete(purchase)
                Toast.makeText(this, getString(R.string.toast_delete_done), Toast.LENGTH_SHORT).show()
                updateList()
            })
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPreference!!.edit()
                .putString(KEY_SEARCH_WORD_PURCHASE, searchText.text.toString())
                .putString(KEY_SEARCH_DATE_PURCHASE, searchText2.text.toString())
                .apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_all_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            if(merchDao!!.getItems().isEmpty()){
                val message = getString(R.string.toast_create_advance, getString(R.string.merch))
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }else {
                val intent = Intent(this, NewPurchaseActivity::class.java)
                startActivity(intent)
            }
            return true
        }
        if(id == R.id.action_renew){
            updateList()
            searchText.clearFocus()
            searchText2.clearFocus()
            val imm = this.getSystemService(android.app.Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchText.windowToken, 0)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        recordSelected = -1
        searchWord = "%" + searchText.text.toString() + "%"
        if(searchText2.text.toString() == ""){
            searchDate.year = "%%"
            searchDate.month = "%%"
            searchDate.day = "%%"
        }else{
            searchDate = DateString(searchText2.text.toString())
//            Log.i("date string", "${searchDate.year}  ${searchDate.month}  ${searchDate.day}")
        }
        purchasePackage!!.records = purchaseDao!!.getRecordsBySearch(searchWord, searchDate.year, searchDate.month, searchDate.day)
        listRecyclerView.adapter.notifyDataSetChanged()
        deleteFab.hide()
    }

    inner class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_purchase2, parent, false)
            view.setOnClickListener {
                val r = it.idTextView.text.toString().toInt()
                if (recordSelected == r) {
                    val intent = Intent(this@PurchaseListActivity, ChangePurchaseActivity::class.java)
                    intent.putExtra("id", r)
                    startActivity(intent)
                } else {
                    recordSelected = r
                    notifyDataSetChanged()
                    deleteFab.show()
                }
            }
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return purchasePackage!!.records.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.id.text = purchasePackage!!.records[position].id.toString()
            holder.number.text = purchasePackage!!.records[position].number
            holder.item.text = purchasePackage!!.records[position].item
            holder.qty.text = purchasePackage!!.records[position].quantity.toString()
            holder.date.text = purchasePackage!!.records[position].date
            holder.price.text = purchasePackage!!.records[position].price.toString()
            if (holder.id.text.toString().toInt() == recordSelected) {
                holder.itemView.background = holder.itemView.resources.getDrawable(R.drawable.shape_selected, null)
                holder.numberTitle.visibility = View.VISIBLE
                holder.itemTitle.visibility = View.VISIBLE
                holder.qtyTitle.visibility = View.VISIBLE
                holder.dateTitle.visibility = View.VISIBLE
                holder.priceTitle.visibility = View.VISIBLE
            } else {
                holder.itemView.background = holder.itemView.resources.getDrawable(R.drawable.shape_unselected, null)
                holder.numberTitle.visibility = View.INVISIBLE
                holder.itemTitle.visibility = View.INVISIBLE
                holder.qtyTitle.visibility = View.INVISIBLE
                holder.dateTitle.visibility = View.INVISIBLE
                holder.priceTitle.visibility = View.INVISIBLE
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var id: TextView = view.findViewById(R.id.idTextView)
            var number: TextView = view.findViewById(R.id.numberTextView)
            var item: TextView = view.findViewById(R.id.itemTextView)
            var qty: TextView = view.findViewById(R.id.quantityTextView)
            var date: TextView = view.findViewById(R.id.dateTextView)
            var price: TextView = view.findViewById(R.id.priceTextView)
            var numberTitle: TextView = view.findViewById(R.id.numberTitleTextView)
            var itemTitle: TextView = view.findViewById(R.id.itemTitleTextView)
            var qtyTitle: TextView = view.findViewById(R.id.quantityTitleTextView)
            var dateTitle: TextView = view.findViewById(R.id.dateTitleTextView)
            var priceTitle: TextView = view.findViewById(R.id.priceTitleTextView)
        }
    }
}
