package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.RestaurantItemAdapter
import edu.bluejack22_1.beefood.model.ClassRestaurant
import kotlinx.coroutines.runBlocking

class Search : AppCompatActivity() {

    lateinit var widgetInputSearch : EditText

    private lateinit var restaurantsRecycler : RecyclerView


    private var restaurantOffset : Long = 0
    private val restaurantThreshold : Long = 5
    private var isLoading = false
    private var isEnd = false
    var linearLayoutManager = LinearLayoutManager(this)
    var restaurants : ArrayList<ClassRestaurant> = arrayListOf()

    fun onChange(){
        Log.d("load more", "on change detected")
        restaurantOffset = 0
        isEnd = false
        isLoading = false
        restaurants = arrayListOf()
        loadMore(false)
//        var searchText = widgetInputSearch.text.toString();
//        var restaurants = runBlocking { ClassRestaurant.getRestaurantByName(searchText, ) }
//        Log.d("search restaurant", restaurants.toString())
    }


    private fun loadMore(mustEndScreen : Boolean) {

        var searchText = widgetInputSearch.text.toString();
        if (!isLoading && !isEnd && (!mustEndScreen || linearLayoutManager.findLastCompletelyVisibleItemPosition() == restaurants.size-1) ) {
            isLoading = true
            var lastId = ""
            if(restaurants.size > 0) lastId = restaurants.get(restaurants.size-1).id
            var newRestaurants = runBlocking { ClassRestaurant.getRestaurantByName(searchText, restaurantThreshold, restaurantOffset, lastId) }
            if(newRestaurants.size > 0){
                if(newRestaurants.size < restaurantThreshold) isEnd = true;
                var lastSize = restaurants.size - 1
                restaurants = newRestaurants
                restaurantsRecycler.adapter = RestaurantItemAdapter(restaurants)
                restaurantOffset += restaurantThreshold
                linearLayoutManager.scrollToPosition(lastSize-3)
            }
            Log.d("inf scroll", "load more new offset " + restaurantOffset.toString())
            isLoading = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        restaurants = arrayListOf()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        restaurantsRecycler = findViewById(R.id.recyclerViewRestaurant)
        restaurantsRecycler.layoutManager = linearLayoutManager
        restaurantsRecycler.setHasFixedSize(true)

        widgetInputSearch = findViewById(R.id.input_search)
        widgetInputSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                onChange()
            }
        })

        loadMore(false)
        restaurantsRecycler.adapter = RestaurantItemAdapter(restaurants)
        restaurantsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadMore(true)
            }
        })



    }
}