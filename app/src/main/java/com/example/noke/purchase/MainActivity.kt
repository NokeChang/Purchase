package com.example.noke.purchase

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_LOW_STOCK
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_DATE_ORDER
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_DATE_PURCHASE
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_WORD_CLIENT
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_WORD_MERCH
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_WORD_ORDER
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_WORD_PURCHASE
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    class DataPackage(var hasLowStock: Boolean = false)
    private var dataPackage = DataPackage()
    private var sharedPreference: SharedPreferences? = null
    private var lowStock = 0

    companion object {
        var recordDatabase: RecordDatabase? = null
        var merchDao: MerchDao? = null
        var orderDao: OrderDao? = null
        var clientDao: ClientDao? = null
        var purchaseDao: PurchaseDao? = null
        var comboDao: ComboDao? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        popPermissionDialog()
        sharedPreference = this.getSharedPreferences("user", MODE_PRIVATE)

        recordDatabase = RecordDatabase.getInstance(this)
        recordDatabase!!.createView()
        merchDao = recordDatabase!!.getMerchDao()
        orderDao = recordDatabase!!.getOrderDao()
        clientDao = recordDatabase!!.getClientDao()
        purchaseDao = recordDatabase!!.getPurchaseDao()
        comboDao = recordDatabase!!.getComboDao()

        val callback = fun(i: Int) {
            val page = when (i) {
                0 -> MerchListActivity::class.java
                1 -> ClientListActivity::class.java
                2 -> PurchaseListActivity::class.java
                3 -> OrderListActivity::class.java
//                4 -> AllListActivity::class.java
                else -> null
            }
            val intent = Intent(this, page)
            startActivity(intent)
        }

        menuRecyclerView.layoutManager = GridLayoutManager(this, 3)
        menuRecyclerView.setHasFixedSize(true)
        menuRecyclerView.adapter = MenuAdapter(callback, dataPackage)
    }

    override fun onResume() {
        super.onResume()
        lowStock = sharedPreference!!.getInt(KEY_LOW_STOCK, 0)
        dataPackage.hasLowStock = recordDatabase!!.hasLowStock(lowStock)
        menuRecyclerView.adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreference!!.edit()
                .remove(KEY_SEARCH_WORD_MERCH)
                .remove(KEY_SEARCH_WORD_CLIENT)
                .remove(KEY_SEARCH_WORD_ORDER)
                .remove(KEY_SEARCH_WORD_PURCHASE)
                .remove(KEY_SEARCH_DATE_PURCHASE)
                .remove(KEY_SEARCH_DATE_ORDER)
                .apply()
    }

    override fun onBackPressed() {
        this.confirmDialog(fun(_,_){
            super.onBackPressed()
        })
    }

    private fun popPermissionDialog() {
        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, PreferenceActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class MenuAdapter(private val callback: (Int) -> Unit, private val dataPackage: DataPackage) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
        private var titles = arrayOf("","","","") //, "資料庫")
        private var draws = arrayOf(R.drawable.merch, R.drawable.client, R.drawable.purchase, R.drawable.order)
        private var images: Array<Bitmap>? = null
        private var alertImage: Bitmap? = null

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)

            titles = arrayOf(recyclerView.context.getString(R.string.main_title_merch),
                    recyclerView.context.getString(R.string.main_title_client),
                    recyclerView.context.getString(R.string.main_title_purchase),
                    recyclerView.context.getString(R.string.main_title_order))

            val option = BitmapFactory.Options()
            option.inSampleSize = 2
            images = arrayOf(BitmapFactory.decodeResource(recyclerView.resources, draws[0], option),
                    BitmapFactory.decodeResource(recyclerView.resources, draws[1], option),
                    BitmapFactory.decodeResource(recyclerView.resources, draws[2], option),
                    BitmapFactory.decodeResource(recyclerView.resources, draws[3], option))
            alertImage = BitmapFactory.decodeResource(recyclerView.resources, R.drawable.alert)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.i("view holder","creating")
            val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_menu, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return 4
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.i("binder", "called")
            holder.title.text = titles[position]
            holder.image.setImageBitmap(images!![position]) //lag
            holder.itemView.setOnClickListener {
                callback(position)
            }
            if (position == 0) {
                if (dataPackage.hasLowStock) {
                    holder.image.setImageBitmap(alertImage)
                } else {
                    holder.image.setImageBitmap(images!![0])
                }
            }
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var title: TextView = view.findViewById(R.id.textView)
            var image: ImageView = view.findViewById(R.id.imageView)
        }
    }
}
