package com.example.noke.purchase

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.example.noke.purchase.MainActivity.Companion.clientDao
import com.example.noke.purchase.MainActivity.Companion.recordDatabase
import com.example.noke.purchase.PreferenceActivity.Companion.KEY_SEARCH_WORD_CLIENT
import kotlinx.android.synthetic.main.activity_check_list.*
import kotlinx.android.synthetic.main.holder_client2.view.*

class ClientListActivity : AppCompatActivity() {

    class DataPackage<T>(var records: List<T> = listOf())
    var clientSpentPackage: DataPackage<ClientSpent>? = null
    var recordSelected = -1
    var searchWord = "%%"
    var sharedPreference: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list)
        setSupportActionBar(toolbar)
        recordSelected = -1
        sharedPreference = this.getSharedPreferences("user", MODE_PRIVATE)

        clientSpentPackage = DataPackage()
        listRecyclerView.layoutManager = GridLayoutManager(this,2)
        listRecyclerView.setHasFixedSize(true)
        listRecyclerView.adapter = ListAdapter()

        //change layout dynamically
        toolbar.title = this.getString(R.string.main_title_client)
        searchText.hint = this.getString(R.string.hint_search_name)
        searchText2.visibility = View.GONE

        searchText.setText(sharedPreference!!.getString(KEY_SEARCH_WORD_CLIENT,""))

        deleteFab.setOnClickListener {
            confirmDialog(fun(_, _) {
                val client = Client()
                client.id = recordSelected
                clientDao!!.delete(client)
                Toast.makeText(this, getString(R.string.toast_delete_done), Toast.LENGTH_SHORT).show()
                updateList()
            })
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPreference!!.edit()
                .putString(KEY_SEARCH_WORD_CLIENT, searchText.text.toString())
                .apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_all_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            val intent = Intent(this, NewClientActivity::class.java)
            startActivity(intent)
            return true
        }
        if(id == R.id.action_renew){
            updateList()
            searchText.clearFocus()
            var imm = this.getSystemService(android.app.Activity.INPUT_METHOD_SERVICE) as InputMethodManager
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
        clientSpentPackage!!.records = recordDatabase!!.getClientSpent(searchWord)
        listRecyclerView.adapter.notifyDataSetChanged()
        deleteFab.hide()
    }

    inner class ListAdapter: RecyclerView.Adapter<ListAdapter.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.holder_client2, parent, false)
            view.setOnClickListener {
                var r = it.idTextView.text.toString().toInt()
                if (recordSelected == r) {
                    val intent = Intent(this@ClientListActivity, ChangeClientActivity::class.java)
                    intent.putExtra("id",r)
                    startActivity(intent)
                } else {
                    recordSelected = r
                    notifyDataSetChanged()
                    deleteFab.show()
                }
            }
            return ViewHolder(view)
        }

        override fun getItemCount(): Int{
            return clientSpentPackage!!.records.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.id.text = clientSpentPackage!!.records[position].id.toString()
            holder.name.text = clientSpentPackage!!.records[position].name
            holder.locale.text = clientSpentPackage!!.records[position].locale
            holder.spent.text = clientSpentPackage!!.records[position].spent.toString()

            if (holder.id.text.toString().toInt() == recordSelected) {
                holder.itemView.background = holder.itemView.resources.getDrawable(R.drawable.shape_selected, null)
                holder.nameTitle.visibility = View.VISIBLE
                holder.localeTitle.visibility = View.VISIBLE
                holder.spentTitle.visibility = View.VISIBLE
            } else {
                holder.itemView.background = holder.itemView.resources.getDrawable(R.drawable.shape_unselected, null)
                holder.nameTitle.visibility = View.INVISIBLE
                holder.localeTitle.visibility = View.INVISIBLE
                holder.spentTitle.visibility = View.INVISIBLE
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            var id: TextView = view.findViewById(R.id.idTextView)
            var name: TextView = view.findViewById(R.id.nameTextView)
            var locale: TextView = view.findViewById(R.id.localeTextView)
            var spent: TextView = view.findViewById(R.id.spentTextView)
            var nameTitle: TextView = view.findViewById(R.id.nameTitleTextView)
            var localeTitle: TextView = view.findViewById(R.id.localeTitleTextView)
            var spentTitle: TextView = view.findViewById(R.id.spentTitleTextView)
        }
    }
}
