package edu.bluejack22_1.beefood.sender

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassTransaction
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking

class SenderHome : AppCompatActivity() {

    lateinit var widgetStatus : TextView
    lateinit var widgetLayout : LinearLayout

    fun onChangeStatus(){
        widgetStatus.text = ClassUser.changeStatus(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sender_home)

        widgetLayout = findViewById(R.id.sender_home_layout_transaction_detail)
        widgetStatus = findViewById(R.id.sender_home_status)
        widgetStatus.text = ClassUser.translateStatus(ClassUser.staticUser?.status!!, this)

        findViewById<Button>(R.id.sender_home_change_status).setOnClickListener {
            onChangeStatus()
        }

        var currentTransaction = runBlocking { ClassTransaction.getActiveSenderTransaction() }
        if(currentTransaction != null){
            val restaurant = runBlocking { ClassRestaurant.getRestaurantById(currentTransaction.restaurantId) }

            findViewById<TextView>(R.id.transaction_detail_id).setText(currentTransaction.id)
            findViewById<TextView>(R.id.transaction_detail_id).setText(ClassRestaurant.translateTransactionStatus(currentTransaction.status, this))
            findViewById<TextView>(R.id.transaction_detail_data).setText(currentTransaction.data)
            findViewById<TextView>(R.id.transaction_detail_restaurant).setText(restaurant.name)
            widgetLayout.visibility = View.VISIBLE
        }

    }
}