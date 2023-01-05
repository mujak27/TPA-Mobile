package edu.bluejack22_1.beefood.user

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.RestaurantItemAdapter
import edu.bluejack22_1.beefood.helper.*
import edu.bluejack22_1.beefood.model.ClassRestaurant
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class Home : AppCompatActivity() {

    private lateinit var restaurantsRecycler : RecyclerView

    lateinit var loadingText : TextView

    private var restaurantOffset : Long = 0
    private var restaurantThreshold : Long = 5
    private var isLoading = false
    private var isEnd = false
    var linearLayoutManager = LinearLayoutManager(this)
    var restaurants : ArrayList<ClassRestaurant> = arrayListOf()


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_home)
        loadingText = findViewById<TextView>(R.id.loadingText)
        restaurantsRecycler = findViewById(R.id.recyclerViewRestaurant)
        restaurantsRecycler.layoutManager = linearLayoutManager
        restaurantsRecycler.setHasFixedSize(true)
        createNotificationChannel()
        scheduleNotification()

        loadMore()
        restaurantsRecycler.adapter = RestaurantItemAdapter(restaurants)
        restaurantsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadMore()
            }
        })
        findViewById<Button>(R.id.transactions).setOnClickListener{
            val intent = Intent(this, Transactions::class.java)
            this.startActivity(intent)
        }
        findViewById<Button>(R.id.searchButton).setOnClickListener{
            val intent = Intent(this, Search::class.java)
            this.startActivity(intent)
        }
        findViewById<Button>(R.id.buttonProfile).setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            this.startActivity(intent)
        }

    }


    private fun scheduleNotification()
    {
        Log.d("notif", "schedule called")
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "beefood notification"
        val message = "order your food"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var date = Calendar.getInstance()
        Log.d("notif", "timestamp before " + date.timeInMillis.toString())
        var timestamp = date.timeInMillis + kotlin.random.Random.nextInt(10, 20) * 3600 * 1000
//        date.set(2023, 1, 2, 15, 0, 1)
        Log.d("notif", "notif will be called at " + timestamp.toString())
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timestamp,
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
