package com.example.noke.purchase

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.merchDao
import com.example.noke.purchase.MainActivity.Companion.recordDatabase
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_LOW_STOCK
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_WORD_MERCH
import kotlinx.android.synthetic.main.activity_check_list.*
import kotlinx.android.synthetic.main.holder_merch2.view.*
import java.io.File

class MerchListActivity : AppCompatActivity() {

    class DataPackage<T>(var records: List<T> = listOf())
    var merchStockPackage: DataPackage<MerchStock>? = null
    var recordSelected = -1
    var searchWord = "%%"
    var sharedPreference: SharedPreferences? = null
    var lowStock = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list)
        setSupportActionBar(toolbar)

        recordSelected = -1
        sharedPreference = this.getSharedPreferences("user", MODE_PRIVATE)
        lowStock = sharedPreference!!.getInt(KEY_LOW_STOCK, 0)

        merchStockPackage = DataPackage()
        listRecyclerView.layoutManager = GridLayoutManager(this, 2)
        listRecyclerView.setHasFixedSize(true)
        listRecyclerView.adapter = ListAdapter()

        //change layout dynamically
        toolbar.title = this.getString(R.string.main_title_merch)
        searchText.hint = this.getString(R.string.hint_search_item)
        searchText2.visibility = View.GONE

        searchText.setText(sharedPreference!!.getString(KEY_SEARCH_WORD_MERCH,""))

        deleteFab.setOnClickListener {
            confirmDialog(fun(_, _) {
                val file = File(NewMerchActivity.PHOTO_PATH, merchDao!!.getPhotoName(recordSelected))
                file.delete()
                val merch = Merch()
                merch.id = recordSelected
                merchDao!!.delete(merch)
                Toast.makeText(this, getString(R.string.toast_delete_done), Toast.LENGTH_SHORT).show()
                updateList()
            })
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPreference!!.edit()
                .putString(KEY_SEARCH_WORD_MERCH, searchText.text.toString())
                .apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_all_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            val intent = Intent(this, NewMerchActivity::class.java)
            startActivity(intent)
            return true
        }
        if (id == R.id.action_renew) {
            updateList()
            searchText.clearFocus()
            var imm = this.getSystemService(android.app.Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchText.windowToken, 0)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if(v!!.idTextView.text.toString().toInt() == recordSelected) {
            menuInflater.inflate(R.menu.menu_merch, menu)
            menu!!.getItem(0).title = "複製[" + v.itemTextView.text.toString() + "]的備註"
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        var clipboard = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        when(id){
            R.id.merchInfo -> {
                var clip = ClipData.newPlainText("copy", merchDao!!.getRemarkById(recordSelected))
                clipboard.primaryClip = clip
                Toast.makeText(this, "複製完成", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        recordSelected = -1
        searchWord = "%" + searchText.text.toString() + "%"
        merchStockPackage!!.records = recordDatabase!!.getMerchStock(searchWord)
        listRecyclerView.adapter.notifyDataSetChanged()
        deleteFab.hide()
    }

    inner class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_merch2, parent, false)
            view.setOnClickListener {
                val r = it.idTextView.text.toString().toInt()
                if (recordSelected == r) {
                    val intent = Intent(this@MerchListActivity, ChangeMerchActivity::class.java)
                    intent.putExtra("id", r)
                    startActivity(intent)
                } else {
                    recordSelected = r
                    notifyDataSetChanged()
                    deleteFab.show()
                }
            }
            this@MerchListActivity.registerForContextMenu(view)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return merchStockPackage!!.records.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.id.text = merchStockPackage!!.records[position].id.toString()
            holder.item.text = merchStockPackage!!.records[position].item
            holder.purchaseQty.text = merchStockPackage!!.records[position].sum1.toString()
            holder.orderQty.text = merchStockPackage!!.records[position].sum2.toString()
            holder.stockQty.text = merchStockPackage!!.records[position].stock.toString()
            holder.outcome.text = merchStockPackage!!.records[position].outcome.toString()
            holder.income.text = merchStockPackage!!.records[position].income.toString()
            holder.surplus.text = merchStockPackage!!.records[position].surplus.toString()
            holder.photoName.text = merchStockPackage!!.records[position].photoName
            val bitmap = getBitmapFromFile(merchStockPackage!!.records[position].photoName)
            holder.photo.setImageBitmap(bitmap)
            if (holder.id.text.toString().toInt() == recordSelected) {
                holder.itemView.background = holder.itemView.resources.getDrawable(R.drawable.shape_selected, null)
                holder.itemTitle.visibility = View.VISIBLE
                holder.purchaseQtyTitle.visibility = View.VISIBLE
                holder.orderQtyTitle.visibility = View.VISIBLE
                holder.stockQtyTitle.visibility = View.VISIBLE
                holder.outcomeTitle.visibility = View.VISIBLE
                holder.incomeTitle.visibility = View.VISIBLE
                holder.surplusTitle.visibility = View.VISIBLE
                holder.purchaseQty.visibility = View.VISIBLE
                holder.orderQty.visibility = View.VISIBLE
                holder.outcome.visibility = View.VISIBLE
                holder.income.visibility = View.VISIBLE
            } else {
                if (holder.stockQty.text.toString().toInt() < lowStock) {
                    holder.itemView.background = holder.itemView.resources.getDrawable(R.drawable.shape_alarm, null)
                } else {
                    holder.itemView.background = holder.itemView.resources.getDrawable(R.drawable.shape_unselected, null)
                }
                holder.itemTitle.visibility = View.INVISIBLE
                holder.purchaseQtyTitle.visibility = View.GONE
                holder.orderQtyTitle.visibility = View.GONE
                holder.stockQtyTitle.visibility = View.INVISIBLE
                holder.outcomeTitle.visibility = View.GONE
                holder.incomeTitle.visibility = View.GONE
                holder.surplusTitle.visibility = View.INVISIBLE
                holder.purchaseQty.visibility = View.GONE
                holder.orderQty.visibility = View.GONE
                holder.outcome.visibility = View.GONE
                holder.income.visibility = View.GONE
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var id: TextView = view.findViewById(R.id.idTextView)
            var photoName: TextView = view.findViewById(R.id.photoTextView)
            var photo: ImageView = view.findViewById(R.id.photoImageView)
            var item: TextView = view.findViewById(R.id.itemTextView)
            var purchaseQty: TextView = view.findViewById(R.id.purchaseQtyTextView)
            var orderQty: TextView = view.findViewById(R.id.orderQtyTextView)
            var stockQty: TextView = view.findViewById(R.id.stockQtyTextView)
            var outcome: TextView = view.findViewById(R.id.outcomeTextView)
            var income: TextView = view.findViewById(R.id.incomeTextView)
            var surplus: TextView = view.findViewById(R.id.surplusTextView)
            var itemTitle: TextView = view.findViewById(R.id.itemTitleTextView)
            var purchaseQtyTitle: TextView = view.findViewById(R.id.purchaseQtyTitleTextView)
            var orderQtyTitle: TextView = view.findViewById(R.id.orderQtyTitleTextView)
            var stockQtyTitle: TextView = view.findViewById(R.id.stockQtyTitleTextView)
            var outcomeTitle: TextView = view.findViewById(R.id.outcomeTitleTextView)
            var incomeTitle: TextView = view.findViewById(R.id.incomeTitleTextView)
            var surplusTitle: TextView = view.findViewById(R.id.surplusTitleTextView)
        }
    }
}
