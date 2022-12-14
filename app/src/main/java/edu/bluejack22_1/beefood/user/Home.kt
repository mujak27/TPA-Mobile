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
    private var restaurantThreshold : Long = 6
    private var isLoading = false
    private var isEnd = false
    var linearLayoutManager = LinearLayoutManager(this)
    var restaurants = runBlocking { ClassRestaurant.getRestaurantWithBiggestRating(restaurantThreshold, restaurantOffset) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_home)
        loadingText = findViewById<TextView>(R.id.loadingText)
        restaurantsRecycler = findViewById(R.id.recyclerViewRestaurant)
        restaurantsRecycler.layoutManager = linearLayoutManager
        restaurantsRecycler.setHasFixedSize(true)

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
//        if (isLoading) {
//            isLoading = false
//            loadingText.setText("")
//        }
        if (!isLoading && !isEnd && linearLayoutManager.findLastCompletelyVisibleItemPosition() == restaurants.size-1) {
            isLoading = true
            Log.d("inf scroll", "load more")
            restaurantOffset += restaurantThreshold
            loadingText.setText("loading more")
            var newRestaurants = runBlocking { ClassRestaurant.getRestaurantWithBiggestRating(restaurantThreshold, restaurantOffset) }
            if(newRestaurants.size < restaurantThreshold){
                isEnd = true;
            }else{
                restaurants.addAll(newRestaurants)
            }
            isLoading = false
            loadingText.setText("")
        }
    }
}
