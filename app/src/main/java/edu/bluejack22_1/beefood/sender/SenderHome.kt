package edu.bluejack22_1.beefood.sender

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.helper.classStorage
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassTransaction
import edu.bluejack22_1.beefood.model.ClassUser
import edu.bluejack22_1.beefood.user.SenderProfile
import kotlinx.coroutines.runBlocking

class SenderHome : AppCompatActivity() {

    lateinit var imageView : ImageView
    lateinit var widgetStatus : TextView
    lateinit var widgetLayout : LinearLayout
    lateinit var widgetError : TextView
    var isSetImage = false

    fun onChangeStatus(){
        widgetStatus.text = ClassUser.changeStatus(this)
    }

    fun onSelectPhoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){
            var pic : Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            isSetImage = true
            imageView.setImageBitmap(pic)
        }
    }

    fun onDoneTransaction(transactionId : String){
        if(!isSetImage) {
            Log.d("transaction done", "image not set")
            widgetError.setText(getString(R.string.please_input_photo))
            return
        }
        var pictureLink = runBlocking { classStorage.uploadPhoto(imageView).toString() }
        ClassTransaction.doneTransaction(transactionId, pictureLink)
        widgetLayout.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sender_home)

        widgetError = findViewById(R.id.sender_home_transaction_error)
        imageView =  findViewById(R.id.pickup_photo)
        widgetLayout = findViewById(R.id.sender_home_layout_transaction_detail)
        widgetStatus = findViewById(R.id.sender_home_status)
        widgetStatus.text = ClassUser.translateStatus(ClassUser.staticUser?.status!!, this)

        findViewById<Button>(R.id.sender_home_change_status).setOnClickListener {
            onChangeStatus()
        }
        findViewById<Button>(R.id.redirect_profile_sender).setOnClickListener{
            val intent = Intent(this, SenderProfile::class.java)
            this.startActivity(intent)
        }
        var currentTransaction = runBlocking { ClassTransaction.getActiveSenderTransaction() }
        if(currentTransaction != null){
            val restaurant = runBlocking { ClassRestaurant.getRestaurantById(currentTransaction.restaurantId) }

            findViewById<TextView>(R.id.transaction_detail_id).setText(currentTransaction.id)
            findViewById<TextView>(R.id.transaction_detail_id).setText(ClassRestaurant.translateTransactionStatus(currentTransaction.status, this))
            findViewById<TextView>(R.id.transaction_detail_data).setText(currentTransaction.data)
            findViewById<TextView>(R.id.transaction_detail_restaurant).setText(restaurant.name)
            widgetLayout.visibility = View.VISIBLE

            findViewById<Button>(R.id.button_select_photo).setOnClickListener {
                onSelectPhoto()
            }

            findViewById<Button>(R.id.sender_home_button_done).setOnClickListener {
                onDoneTransaction(currentTransaction.id)
            }

        }

    }
}
