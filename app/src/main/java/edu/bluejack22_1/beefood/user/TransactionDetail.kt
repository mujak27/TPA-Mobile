package edu.bluejack22_1.beefood.user

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.CartItemAdapter
import edu.bluejack22_1.beefood.model.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class TransactionDetail : AppCompatActivity() {

    lateinit var widgetSlider : RangeSlider
    private lateinit var cartsRecycler : RecyclerView

    var totalPrice = 0
    var isAlreadyRated = false

    fun addTotalPrice(price : Int){
        totalPrice = totalPrice + price
        Log.d("transaction add total price", price.toString() + " " + totalPrice.toString())
        findViewById<TextView>(R.id.transaction_detail_totalprice).setText(totalPrice.toString())

    }


    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            Log.d("upload file download image", "do in bg")
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
                Log.d("upload file display image", "success")
            }
            catch (e: Exception) {
                Log.e("upload file Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val transactionId = intent.getStringExtra("transactionId").toString()
        setContentView(R.layout.activity_transaction_detail)
        findViewById<TextView>(R.id.transaction_detail_id).setText(transactionId)
        var widgetTransactionDetailStatus = findViewById<TextView>(R.id.transaction_detail_status)

        val transactionRef = ClassTransaction.getTransactionById(transactionId)


        val transaction = runBlocking {
            ClassTransaction.transactionFromSnapshot(transactionRef.get().await())
        }
        val restaurant = runBlocking { ClassRestaurant.getRestaurantById(transaction.restaurantId) }
        var senderName = getString(R.string.no_sender_yet)
        if(transaction.senderId != "") senderName = runBlocking { ClassUser.getUserById(transaction.senderId)?.name!! }

        findViewById<TextView>(R.id.transaction_detail_data).setText(transaction.data)
        findViewById<TextView>(R.id.transaction_detail_restaurant).setText(restaurant.name)
        findViewById<TextView>(R.id.transaction_detail_sender).setText(senderName)

        if(!transaction.senderPictureLink.isNullOrBlank()){
            DownloadImageFromInternet(findViewById<ImageView>(R.id.transaction_detail_sender_pict)).execute(transaction.senderPictureLink)
        }

        var carts = runBlocking { ClassCart.getCartsFromTransaction(transactionId) }
        cartsRecycler = findViewById(R.id.recyclerViewCarts)
        cartsRecycler.layoutManager = LinearLayoutManager(this)
        cartsRecycler.setHasFixedSize(true)
        cartsRecycler.adapter = CartItemAdapter(transaction.restaurantId, carts, ::addTotalPrice)


        var rating = runBlocking { ClassRating.getMyRating(restaurant.id) }
        Log.d("rating", rating.toString())
        widgetSlider = findViewById(R.id.transaction_detail_rating)
        widgetSlider.visibility = View.GONE
        widgetSlider.setValueFrom(0f)
        widgetSlider.setValueTo(5f)
        widgetSlider.addOnChangeListener { slider, value, fromUser ->
            Log.d("slider change curr rating", rating.toString())
            if(rating == -1f){
                rating = value
                Log.d("slider change", value.toString())
                widgetSlider.isEnabled = false
                runBlocking {
                    ClassRating.createRating(restaurant.id, rating)
                }
            }
        }

//        widgetSlider.onFocusChangeListener = View.OnFocusChangeListener() { view, b ->
//
//            Log.d("slider change 1", rating.toString())
//            if(b){
//                Log.d("slider change 2", rating.toString())
//                rating = widgetSlider.values.get(0)
//                if(rating == -1f){
//                    Log.d("slider change 3", rating.toString())
//                }
//            }
//        }
        if (rating != -1f){
            isAlreadyRated = true
            widgetSlider.values = listOf(rating)
            widgetSlider.isEnabled = false
        }



        transactionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                widgetTransactionDetailStatus.setText(
                    ClassRestaurant.translateTransactionStatus(
                        snapshot.data?.get("status").toString(), this)
                )
                if(snapshot.data?.get("status").toString() == "done"){
                    widgetSlider.visibility = View.VISIBLE
                }
            } else {
                widgetTransactionDetailStatus.setText("not found")
            }
        }
    }
}