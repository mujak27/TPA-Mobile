package edu.bluejack22_1.beefood.user

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.RestaurantItemAdapter
import edu.bluejack22_1.beefood.model.ClassRestaurant
import kotlinx.coroutines.runBlocking

class Home : AppCompatActivity() {

    private lateinit var restaurantsRecycler : RecyclerView
    private lateinit var newArrList: List<ClassRestaurant>


    lateinit var loadingText : TextView

    private var restaurantOffset : Long = 0
    private var restaurantThreshold : Long = 5
    private var isLoading = false
    private var isEnd = false
    var linearLayoutManager = LinearLayoutManager(this)
    var restaurants : ArrayList<ClassRestaurant> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_home)
        loadingText = findViewById<TextView>(R.id.loadingText)
        restaurantsRecycler = findViewById(R.id.recyclerViewRestaurant)
        restaurantsRecycler.layoutManager = linearLayoutManager
        restaurantsRecycler.setHasFixedSize(true)

        loadMore()
        restaurantsRecycler.adapter = RestaurantItemAdapter(restaurants)
        restaurantsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadMore()
            }
        })
    }
    private fun loadMore() {
        Log.d("layout visible index ", linearLayoutManager.findLastCompletelyVisibleItemPosition().toString())
        Log.d("rest size ", restaurants.size.toString())
        if (!isLoading && !isEnd && linearLayoutManager.findLastCompletelyVisibleItemPosition() == restaurants.size-1) {
            isLoading = true
            loadingText.setText("loading more")
            var lastId = ""
            if(restaurants.size > 0) lastId = restaurants.get(restaurants.size-1).id
            var newRestaurants = runBlocking { ClassRestaurant.getRestaurantWithBiggestRating(restaurantThreshold, restaurantOffset, lastId) }
            if(newRestaurants.size > 0){
                Log.d("inf scroll", "exists")
                if(newRestaurants.size < restaurantThreshold){
                    isEnd = true;
                }
                restaurants.addAll(newRestaurants)
                restaurantsRecycler.adapter = RestaurantItemAdapter(restaurants)
                restaurantOffset += restaurantThreshold
            }
            Log.d("inf scroll", "load more new offset " + restaurantOffset.toString())
            isLoading = false
            loadingText.setText("")
        }
    }
}
