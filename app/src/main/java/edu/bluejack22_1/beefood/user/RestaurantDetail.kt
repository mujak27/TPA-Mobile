package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.MenuItemAdapter
import edu.bluejack22_1.beefood.adapter.RestaurantItemAdapter
import edu.bluejack22_1.beefood.model.ClassCart
import edu.bluejack22_1.beefood.model.ClassMenu
import edu.bluejack22_1.beefood.model.ClassRestaurant
import kotlinx.coroutines.runBlocking

class RestaurantDetail : AppCompatActivity() {

    lateinit var restaurantId : String
    private lateinit var menusRecycler : RecyclerView

    lateinit var restaurantTitle: TextView

    var carts : ArrayList<ClassCart> = ArrayList()

    fun onAddCart(id : String){
        Log.d("cart", "add " + id)
    }

    fun onRemoveCart(id : String){
        Log.d("cart", "remove " + id)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        restaurantTitle = findViewById(R.id.restaurantTitle)

        restaurantId = intent.getStringExtra("restaurantId").toString()
        var restaurant = runBlocking { ClassRestaurant.getRestaurantById(restaurantId)}
        restaurantTitle.setText(restaurant.name)

        var menus = runBlocking { ClassMenu.getMenusFromRestaurantId(restaurantId)}


        menusRecycler = findViewById(R.id.recyclerViewMenus)
        menusRecycler.layoutManager = LinearLayoutManager(this)
        menusRecycler.setHasFixedSize(true)
        menusRecycler.adapter = MenuItemAdapter(menus, ::onAddCart, ::onRemoveCart)


    }
}