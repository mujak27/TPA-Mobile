package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassTransaction
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class TransactionDetail : AppCompatActivity() {

    lateinit var widgetSlider : RangeSlider

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
                widgetTransactionDetailStatus.setText(snapshot.data?.get("status").toString())
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
        var senderName = "no sender yet"
        if(transaction.senderId != "") senderName = runBlocking { ClassUser.getUserById(transaction.senderId).name }

        findViewById<TextView>(R.id.transaction_detail_data).setText(transaction.data)
        findViewById<TextView>(R.id.transaction_detail_restaurant).setText(restaurant.name)
        findViewById<TextView>(R.id.transaction_detail_sender).setText(senderName)

    }
}