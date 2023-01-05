package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.CartItemAdapter
import edu.bluejack22_1.beefood.model.ClassCart
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassTransaction
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class TransactionDetail : AppCompatActivity() {

    lateinit var widgetSlider : RangeSlider
    private lateinit var cartsRecycler : RecyclerView

    var totalPrice = 0

    fun addTotalPrice(price : Int){
        totalPrice = totalPrice + price
        Log.d("transaction add total price", price.toString() + " " + totalPrice.toString())
        findViewById<TextView>(R.id.transaction_detail_totalprice).setText(totalPrice.toString())

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val transactionId = intent.getStringExtra("transactionId").toString()
        setContentView(R.layout.activity_transaction_detail)
        findViewById<TextView>(R.id.transaction_detail_id).setText(transactionId)
        var widgetTransactionDetailStatus = findViewById<TextView>(R.id.transaction_detail_status)


        widgetSlider = findViewById<RangeSlider>(R.id.transaction_detail_rating)
        widgetSlider.visibility = View.GONE
        widgetSlider.setValueFrom(0f)
        widgetSlider.setValueTo(5f)
        widgetSlider.addOnChangeListener { slider, value, fromUser ->
            Log.d("slider change", value.toString())
        }

        val transactionRef = ClassTransaction.getTransactionById(transactionId)

        transactionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                widgetTransactionDetailStatus.setText(
                    ClassRestaurant.translateTransactionStatus(
                        snapshot.data?.get("status").toString(), this)
                    )
                if(snapshot.data?.get("status").toString() == "completed"){
                    widgetSlider.visibility = View.VISIBLE
                }
            } else {
                widgetTransactionDetailStatus.setText("not found")
            }
        }

        val transaction = runBlocking {
            ClassTransaction.transactionFromSnapshot(transactionRef.get().await())
        }
        val restaurant = runBlocking { ClassRestaurant.getRestaurantById(transaction.restaurantId) }
        var senderName = getString(R.string.no_sender_yet)
        if(transaction.senderId != "") senderName = runBlocking { ClassUser.getUserById(transaction.senderId)?.name!! }

        findViewById<TextView>(R.id.transaction_detail_data).setText(transaction.data)
        findViewById<TextView>(R.id.transaction_detail_restaurant).setText(restaurant.name)
        findViewById<TextView>(R.id.transaction_detail_sender).setText(senderName)



        var carts = runBlocking { ClassCart.getCartsFromTransaction(transactionId) }
        cartsRecycler = findViewById(R.id.recyclerViewCarts)
        cartsRecycler.layoutManager = LinearLayoutManager(this)
        cartsRecycler.setHasFixedSize(true)
        cartsRecycler.adapter = CartItemAdapter(transaction.restaurantId, carts, ::addTotalPrice)



    }
}