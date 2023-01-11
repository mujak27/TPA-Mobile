package edu.bluejack22_1.beefood.user.fragments

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.RestaurantItemAdapter
import edu.bluejack22_1.beefood.helper.*
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.user.Profile
import edu.bluejack22_1.beefood.user.Search
import edu.bluejack22_1.beefood.user.Transactions
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CustomerFragmentHome : Fragment() {

    private lateinit var restaurantsRecycler : RecyclerView

    lateinit var loadingText : TextView

    private var restaurantOffset : Long = 0
    private var restaurantThreshold : Long = 5
    private var isLoading = false
    private var isEnd = false
    lateinit var linearLayoutManager : LinearLayoutManager
    var restaurants : ArrayList<ClassRestaurant> = arrayListOf()


    private fun loadMore() {
        if (!isLoading && !isEnd && linearLayoutManager.findLastCompletelyVisibleItemPosition() == restaurants.size-1) {
            isLoading = true
            var lastId = ""
            if(restaurants.size > 0) lastId = restaurants.get(restaurants.size-1).id
            var newRestaurants = runBlocking { ClassRestaurant.getRestaurantWithBiggestRating(restaurantThreshold, restaurantOffset, lastId) }
            if(newRestaurants.size > 0){
                if(newRestaurants.size < restaurantThreshold){
                    isEnd = true;
                }
                var lastSize = restaurants.size -1;
                restaurants.addAll(newRestaurants)
                restaurantsRecycler.adapter = RestaurantItemAdapter(restaurants)
                restaurantOffset += restaurantThreshold
                linearLayoutManager.scrollToPosition(lastSize-3)
            }
            isLoading = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(activity)
        loadingText = view.findViewById<TextView>(R.id.loadingText)
        restaurantsRecycler = view.findViewById(R.id.recyclerViewRestaurant)
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
        view.findViewById<Button>(R.id.searchButton).setOnClickListener{
            val intent = Intent(activity, Search::class.java)
            this.startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_customer_home, container, false)
        return view
    }


    private fun scheduleNotification()
    {
        Log.d("notif", "schedule called")
        val intent = Intent(activity?.applicationContext, Notification::class.java)
        val title = "beefood notification"
        val message = "order your food"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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
        val notificationManager = activity?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomerFragmentHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerFragmentHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}