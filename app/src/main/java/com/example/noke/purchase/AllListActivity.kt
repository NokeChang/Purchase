package com.example.noke.purchase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.noke.purchase.MainActivity.Companion.clientDao
import com.example.noke.purchase.MainActivity.Companion.merchDao
import com.example.noke.purchase.MainActivity.Companion.orderDao
import com.example.noke.purchase.MainActivity.Companion.purchaseDao
import com.example.noke.purchase.MainActivity.Companion.comboDao
import com.example.noke.purchase.MainActivity.Companion.recordDatabase

import kotlinx.android.synthetic.main.activity_all_list.*
import kotlinx.android.synthetic.main.fragment_all_list.*
import java.io.File
import java.io.FileInputStream

class AllListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_list)
        setSupportActionBar(toolbar)
        container.adapter = SectionsPagerAdapter(supportFragmentManager)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_all_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }*/
        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return TableFragment.newInstance(position)
        }
        override fun getCount(): Int {
            return 7
        }
    }

    class TableFragment : Fragment() {

        class DataPackage<T>(var records: List<T>)

        var merchPackage: DataPackage<Merch>? = null
        var orderPackage: DataPackage<Order>? = null
        var clientPackage: DataPackage<Client>? = null
        var purchasePackage: DataPackage<Purchase>? = null
        var purchaseInfoPackage: DataPackage<ItemSum>? = null
        var orderInfoPackage: DataPackage<ItemSum>? = null
        var merchStockPackage: DataPackage<MerchStock>? = null

        var page: Int? = 0

        override fun onStart() {
            super.onStart()

            page = this.arguments?.getInt("position",0)
            merchPackage = DataPackage(merchDao!!.getAll())
            orderPackage = DataPackage(orderDao!!.getAll())
            clientPackage = DataPackage(clientDao!!.getAll())
            purchasePackage = DataPackage(purchaseDao!!.getAll())
            purchaseInfoPackage = DataPackage(comboDao!!.getPurchaseSum())
            orderInfoPackage = DataPackage(comboDao!!.getOrderSum())
            merchStockPackage = DataPackage(recordDatabase!!.getMerchStock("%%"))

            allListRecyclerView.layoutManager = LinearLayoutManager(this@TableFragment.context)
            allListRecyclerView.setHasFixedSize(true)
            allListRecyclerView.adapter = when(page) {
                2 -> ClientAdapter()
                0 -> OrderAdapter()
                1 -> MerchAdapter()
                3 -> PurchaseAdapter()
                4 -> PurchaseInfoAdapter()
                5 -> OrderInfoAdapter()
                6 -> MerchStockAdapter()
                else -> OrderAdapter()
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_all_list, container, false)
            return rootView
        }

        companion object {
            fun newInstance(position: Int): TableFragment {
                val fragment = TableFragment()
                val args = Bundle()
                args.putInt("position", position)
                fragment.arguments = args
                return fragment
            }
        }

        inner class ClientAdapter: RecyclerView.Adapter<ClientAdapter.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_client, parent, false)
                return ViewHolder(view)
            }
            override fun getItemCount(): Int {
                return clientPackage!!.records.size
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.id.text = clientPackage!!.records[position].id.toString()
                holder.name.text = clientPackage!!.records[position].name
                holder.locale.text = clientPackage!!.records[position].locale
            }
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                var id: TextView = view.findViewById(R.id.idTextView)
                var name: TextView = view.findViewById(R.id.nameTextView)
                var locale: TextView = view.findViewById(R.id.localeTextView)
            }
        }

        inner class OrderAdapter: RecyclerView.Adapter<OrderAdapter.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_order, parent, false)
                return ViewHolder(view)
            }
            override fun getItemCount(): Int {
                return orderPackage!!.records.size
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.id.text = orderPackage!!.records[position].id.toString()
                holder.number.text = orderPackage!!.records[position].number
                holder.mid.text = orderPackage!!.records[position].mid.toString()
                holder.cid.text = orderPackage!!.records[position].cid.toString()
                holder.qty.text = orderPackage!!.records[position].quantity.toString()
                holder.date.text = orderPackage!!.records[position].date
                holder.price.text = orderPackage!!.records[position].price.toString()
            }
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                var id: TextView = view.findViewById(R.id.idTextView)
                var number: TextView = view.findViewById(R.id.numberTextView)
                var mid: TextView = view.findViewById(R.id.midTextView)
                var cid: TextView = view.findViewById(R.id.cidTextView)
                var qty: TextView = view.findViewById(R.id.quantityTextView)
                var date: TextView = view.findViewById(R.id.dateTextView)
                var price: TextView = view.findViewById(R.id.priceTextView)
            }
        }

        inner class MerchAdapter: RecyclerView.Adapter<MerchAdapter.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_merch, parent, false)
                return ViewHolder(view)
            }
            override fun getItemCount(): Int {
                return merchPackage!!.records.size
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.id.text = merchPackage!!.records[position].id.toString()
                holder.item.text = merchPackage!!.records[position].item
                holder.photoName.text = merchPackage!!.records[position].photoName
                val bitmap = getBitmapFromFile(merchPackage!!.records[position].photoName)
                holder.photo.setImageBitmap(bitmap)
            }
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                var id: TextView = view.findViewById(R.id.idTextView)
                var item: TextView = view.findViewById(R.id.itemTextView)
                var photoName: TextView = view.findViewById(R.id.photoTextView)
                var photo: ImageView = view.findViewById(R.id.photoImageView)
            }
        }

        inner class PurchaseAdapter: RecyclerView.Adapter<PurchaseAdapter.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_purchase, parent, false)
                return ViewHolder(view)
            }
            override fun getItemCount(): Int {
                return purchasePackage!!.records.size
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.id.text = purchasePackage!!.records[position].id.toString()
                holder.number.text = purchasePackage!!.records[position].number
                holder.mid.text = purchasePackage!!.records[position].mid.toString()
                holder.qty.text = purchasePackage!!.records[position].quantity.toString()
                holder.date.text = purchasePackage!!.records[position].date
                holder.price.text = purchasePackage!!.records[position].price.toString()
            }
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                var id: TextView = view.findViewById(R.id.idTextView)
                var number: TextView = view.findViewById(R.id.numberTextView)
                var mid: TextView = view.findViewById(R.id.midTextView)
                var qty: TextView = view.findViewById(R.id.quantityTextView)
                var date: TextView = view.findViewById(R.id.dateTextView)
                var price: TextView = view.findViewById(R.id.priceTextView)
            }
        }

        inner class PurchaseInfoAdapter: RecyclerView.Adapter<PurchaseInfoAdapter.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_purchase_info, parent, false)
                return ViewHolder(view)
            }
            override fun getItemCount(): Int {
                return purchaseInfoPackage!!.records.size
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.item.text = purchaseInfoPackage!!.records[position].item
                holder.sum.text = purchaseInfoPackage!!.records[position].sum.toString()
            }
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                var item: TextView = view.findViewById(R.id.itemTextView)
                var sum: TextView = view.findViewById(R.id.purchaseSumTextView)
            }
        }

        inner class OrderInfoAdapter: RecyclerView.Adapter<OrderInfoAdapter.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_purchase_info, parent, false)
                return ViewHolder(view)
            }
            override fun getItemCount(): Int {
                return orderInfoPackage!!.records.size
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.item.text = orderInfoPackage!!.records[position].item
                holder.sum.text = orderInfoPackage!!.records[position].sum.toString()
            }
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                var item: TextView = view.findViewById(R.id.itemTextView)
                var sum: TextView = view.findViewById(R.id.purchaseSumTextView)
            }
        }

        inner class MerchStockAdapter: RecyclerView.Adapter<MerchStockAdapter.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_three, parent, false)
                return ViewHolder(view)
            }
            override fun getItemCount(): Int {
                return merchStockPackage!!.records.size
            }
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.item.text = merchStockPackage!!.records[position].item
                holder.sum1.text = merchStockPackage!!.records[position].sum1.toString()
                holder.sum2.text = merchStockPackage!!.records[position].sum2.toString()
                holder.stock.text = merchStockPackage!!.records[position].stock.toString()
            }
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                var item: TextView = view.findViewById(R.id.TextView1)
                var sum1: TextView = view.findViewById(R.id.TextView2)
                var sum2: TextView = view.findViewById(R.id.TextView3)
                var stock: TextView = view.findViewById(R.id.TextView4)
            }
        }

        private fun getBitmapFromFile(fileName: String): Bitmap?{
            return try {
                val file = File(Environment.getExternalStorageDirectory().toString() + "/" + NewMerchActivity.PHOTO_PATH, fileName)
                val fileInputStream = FileInputStream(file)
                BitmapFactory.decodeStream(fileInputStream)
            }catch(e: Exception){
                Log.e("file error","file io wrong")
//            Log.e("error", Log.getStackTraceString(e))
                null
            }
        }
    }
}
