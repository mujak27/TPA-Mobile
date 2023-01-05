package edu.bluejack22_1.beefood.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    lateinit var checkoutButton : FloatingActionButton

    var carts : ArrayList<ClassCart> = ArrayList()

    fun onAddCart(id : String){
        var cartItem = carts.firstOrNull{it.menuId == id}
        if(cartItem == null){
            carts.add(ClassCart(id, 1))
        }else{
            cartItem.quantity++;
        }
        var check = carts.first{it.menuId == id}

    }

    fun onRemoveCart(id : String){
        var cartItem = carts.firstOrNull{it.menuId == id}
        if(cartItem == null) return;
        if(cartItem.quantity == 1){
            carts.remove(cartItem)
        }else{
            cartItem.quantity--;
        }
    }

    fun onCheckout(){
        if(carts.size == 0) return
        val intent = Intent(this, Checkout::class.java)
        intent.putExtra("restaurantId", restaurantId)
        intent.putExtra("carts", carts)
        this.startActivity(intent)
    }



    private var offset : Long = 0
    private var threshold : Long = 5
    private var isLoading = false
    private var isEnd = false
    var linearLayoutManager = LinearLayoutManager(this)
    var menus : ArrayList<ClassMenu> = arrayListOf()

    var totalPrice = 0


    private fun loadMore() {
        Log.d("layout visible index ", linearLayoutManager.findLastCompletelyVisibleItemPosition().toString())
        Log.d("rest size ", menus.size.toString())
        if (!isLoading && !isEnd && linearLayoutManager.findLastCompletelyVisibleItemPosition() == menus.size-1) {
            isLoading = true
            var lastId = ""
            if(menus.size > 0) lastId = menus.get(menus.size-1).id
            var newRestaurants = runBlocking { ClassMenu.getMenusFromRestaurantIdWithOffset(restaurantId, threshold, offset, lastId) }
            if(newRestaurants.size > 0){
                Log.d("inf scroll", "exists")
                if(newRestaurants.size < threshold){
                    isEnd = true;
                }
                menus.addAll(newRestaurants)
                menusRecycler.adapter = MenuItemAdapter(menus, ::onAddCart, ::onRemoveCart)
                offset += threshold
            }
            Log.d("inf scroll", "load more new offset " + offset.toString())
            isLoading = false
        }
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

        checkoutButton = findViewById(R.id.buttonCheckout)
        checkoutButton.setOnClickListener{onCheckout()}


        loadMore()
        menusRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadMore()
            }
        })



    }
}