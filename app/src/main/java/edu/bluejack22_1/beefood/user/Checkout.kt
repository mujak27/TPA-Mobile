package edu.bluejack22_1.beefood.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.CartItemAdapter
import edu.bluejack22_1.beefood.model.ClassCart
import edu.bluejack22_1.beefood.model.ClassTransaction
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class Checkout : AppCompatActivity() {
    var totalPrice = 0

    lateinit var imageView : ImageView
    private lateinit var carts : ArrayList<ClassCart>
    private lateinit var photoFile : File
    private lateinit var currentPhotoPath : String


    private lateinit var restaurantId : String
    private lateinit var cartsRecycler : RecyclerView
    private lateinit var widgetTotalPrice: TextView
    private lateinit var widgetPickupData: EditText
    private lateinit var widgetCamera : Button



    fun addTotalPrice(price : Int){
        totalPrice += price

        widgetTotalPrice = findViewById(R.id.checkout_total_price)
        Log.d("transaction checkout total price", totalPrice.toString())
        widgetTotalPrice.setText(totalPrice.toString())
    }
    fun onCheckout(){
        val customerId = ClassUser.staticUser?.id.toString()
        val data = widgetPickupData.text.toString()
        var transactionId = runBlocking { ClassTransaction.createTransaction(data, restaurantId, customerId, "", "cooking") }
        ClassCart.insertCartToTransaction(transactionId, carts)

        val intent = Intent(this, TransactionDetail::class.java)
        intent.putExtra("transactionId", transactionId)
        this.startActivity(intent)
    }


    fun onSelectPhoto(){

//        imageView.visibility = View.GONE
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        chooseImage.launch(intent)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){
            Log.d("upload file on activ result", "req code 101")
            var pic : Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            Log.d("upload file bitmap", pic.toString())
            imageView.setImageBitmap(pic)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        totalPrice = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        imageView = findViewById(R.id.pickup_photo)
        restaurantId = intent.getStringExtra("restaurantId").toString()
        carts = intent.getSerializableExtra("carts") as ArrayList<ClassCart>

        cartsRecycler = findViewById(R.id.recyclerViewCarts)
        cartsRecycler.layoutManager = LinearLayoutManager(this)
        cartsRecycler.setHasFixedSize(true)
        cartsRecycler.adapter = CartItemAdapter(restaurantId, carts, ::addTotalPrice)


        widgetPickupData = findViewById(R.id.input_pickup_data)

        findViewById<Button>(R.id.checkout_button).setOnClickListener{
            onCheckout()
        }

        findViewById<Button>(R.id.button_select_photo).setOnClickListener {
            onSelectPhoto()
        }
    }

}