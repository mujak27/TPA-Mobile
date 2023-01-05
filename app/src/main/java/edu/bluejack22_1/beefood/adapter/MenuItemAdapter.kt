package edu.bluejack22_1.beefood.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassMenu
import edu.bluejack22_1.beefood.model.ClassUser

class MenuItemAdapter(
    private val menus : ArrayList<ClassMenu>,
    private val onAddCart : (id : String)->Unit,
    private val onRemoveCart : (id : String)->Unit,
) : RecyclerView.Adapter<MenuItemAdapter.MyViewHolder>() {

    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var title : TextView
        var addButton : Button
        var removeButton : Button
        init {
            title = itemView.findViewById(R.id.item_menu_title)
            addButton = itemView.findViewById<Button>(R.id.item_menu_add)
            removeButton = itemView.findViewById<Button>(R.id.item_menu_remove)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currRestaurant = menus.get(position)
        holder.title.setText(currRestaurant.name)
        holder.addButton.setOnClickListener{
            onAddCart(currRestaurant.id)
        }
        holder.removeButton.setOnClickListener{
            onRemoveCart(currRestaurant.id)
        }

        val role = ClassUser.getCurrentUser()?.role!!
        Log.d("transaction check role", role)
        if(role == "Seller"){
            holder.addButton.visibility = View.GONE
            holder.removeButton.visibility = View.GONE

        }
    }

}
