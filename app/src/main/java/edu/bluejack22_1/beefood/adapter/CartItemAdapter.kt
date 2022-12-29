package edu.bluejack22_1.beefood.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassCart
import edu.bluejack22_1.beefood.model.ClassMenu
import kotlinx.coroutines.runBlocking

class CartItemAdapter(
    private val restaurantId : String,
    private val carts : ArrayList<ClassCart>,
    private val addTotalPrice : (price : Int)->Unit,
) : RecyclerView.Adapter<CartItemAdapter.MyViewHolder>() {

    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        for (cart in carts){
            Log.d("cart item", cart.toString())
            Log.d("cart item", cart.menuId)
        }
        this.context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var name : TextView
        var price : TextView
        var quantity : TextView
        init {
            name = itemView.findViewById(R.id.cart_name)
            price = itemView.findViewById<Button>(R.id.cart_price)
            quantity = itemView.findViewById<Button>(R.id.cart_quantity)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currCart = carts.get(position)
        var menu = runBlocking { ClassMenu.getMenuById(restaurantId, currCart.menuId)}
        Log.d("cart adapter menu", menu.name + " " + menu.price.toString())
        Log.d("widget quantity", holder.price.toString())
        holder.name.setText(menu.name)
        holder.price.setText(menu.price.toString())
        holder.quantity.setText(currCart.quantity.toString())
        addTotalPrice(menu.price * currCart.quantity)
    }
}