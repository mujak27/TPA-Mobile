package edu.bluejack22_1.beefood.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.helper.classStorage
import edu.bluejack22_1.beefood.model.ClassMenu
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking

class MenuItemAdapter(
    private val restaurantId : String,
    private val menus : ArrayList<ClassMenu>,
    private val onAddCart : (id : String)->Unit,
    private val onRemoveCart : (id : String)->Unit,
    private val onSelectPhoto : (position : Int, id : String, imageView : ImageView)->Unit,
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
        var id : String

        var title : TextView
        var addButton : Button
        var removeButton : Button

        var widgetEditName : EditText
        var widgetEditPrice : EditText
        var imageView : ImageView
        var widgetSelectPhotoButton : Button
        var widgetEditMenuButton : Button

        var layoutSeller : LinearLayout
        var layoutCustomer : LinearLayout

        init {
            id = ""
            title = itemView.findViewById(R.id.item_menu_title)
            addButton = itemView.findViewById<Button>(R.id.item_menu_add)
            removeButton = itemView.findViewById<Button>(R.id.item_menu_remove)
            layoutSeller = itemView.findViewById(R.id.menu_edit)
            layoutCustomer = itemView.findViewById(R.id.menu_view)
            widgetSelectPhotoButton = itemView.findViewById<Button>(R.id.button_select_photo)
            widgetEditMenuButton = itemView.findViewById<Button>(R.id.edit_menu_button)

            imageView = itemView.findViewById(R.id.menu_photo)
            widgetEditName = itemView.findViewById(R.id.edit_menu_name)
            widgetEditPrice = itemView.findViewById(R.id.edit_menu_price)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currMenu = menus.get(position)
        Log.d("update menu name", currMenu.name)
        holder.id = currMenu.id
        holder.title.setText(currMenu.name)
        holder.addButton.setOnClickListener{
            onAddCart(currMenu.id)
        }
        holder.removeButton.setOnClickListener{
            onRemoveCart(currMenu.id)
        }


        holder.widgetSelectPhotoButton.setOnClickListener {
            onSelectPhoto(position, currMenu.id, holder.imageView)
        }
        holder.widgetEditMenuButton.setOnClickListener {
            onUpdate(holder)
        }

        Log.d("update menu price", currMenu.price.toString())
        holder.widgetEditName.setText(currMenu.name)
        holder.widgetEditPrice.setText(currMenu.price.toString())
        val role = ClassUser.getCurrentUser()?.role!!
        Log.d("transaction check role", role)
        if(role == "Seller"){
            holder.layoutCustomer.visibility = View.GONE
        }else{
            holder.layoutSeller.visibility = View.GONE
        }
    }

    fun onUpdate(holder : MyViewHolder){
        var name = holder.widgetEditName.text.toString()
        var price = holder.widgetEditPrice.text.toString().toInt()
        var pictureLink = runBlocking { classStorage.uploadPhoto(holder.imageView).toString() }
        ClassMenu.updateMenu(restaurantId, holder.id, name, price, pictureLink)
    }


}
