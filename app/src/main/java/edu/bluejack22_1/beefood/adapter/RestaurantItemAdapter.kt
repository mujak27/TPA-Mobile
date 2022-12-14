package edu.bluejack22_1.beefood.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassUser

class RestaurantItemAdapter (private val restaurants : ArrayList<ClassRestaurant>) : RecyclerView.Adapter<RestaurantItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return restaurants.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var title : TextView
        var container : FrameLayout

        init {
            title = itemView.findViewById(R.id.item_restaurant_title)
            container = itemView.findViewById(R.id.item_restaurant)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.setText(restaurants.get(position).name)
        holder.container.setOnClickListener{
//            val intent = Intent(this, ClassUser.redirectToHomeBasedOnUserRole())
//            ActivityCompat.startActivityForResult(intent)
        }
    }

}