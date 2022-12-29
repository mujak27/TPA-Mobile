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

    private lateinit var carts : ArrayList<ClassCart>
    private lateinit var photoFile : File
    private lateinit var currentPhotoPath : String


    private lateinit var restaurantId : String
    private lateinit var cartsRecycler : RecyclerView
    private lateinit var widgetTotalPrice: TextView
    private lateinit var widgetPickupData: EditText
    private lateinit var widgetCamera : Button


    private val CAMERA_REQUEST = 1888
    private val imageView: ImageView? = null
    private val MY_CAMERA_PERMISSION_CODE = 100

    fun addTotalPrice(price : Int){
        totalPrice += price
    }
    fun onCheckout(){
        val customerId = ClassUser.staticUser?.id.toString()
        val data = widgetPickupData.text.toString()
        var transactionId = runBlocking { ClassTransaction.createTransaction(data, restaurantId, customerId, "", "cooking") }

        val intent = Intent(this, TransactionDetail::class.java)
        intent.putExtra("transactionId", transactionId)
        this.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        totalPrice = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        restaurantId = intent.getStringExtra("restaurantId").toString()
        carts = intent.getSerializableExtra("carts") as ArrayList<ClassCart>

        Log.d("carts", carts.toString())

        cartsRecycler = findViewById(R.id.recyclerViewCarts)
        cartsRecycler.layoutManager = LinearLayoutManager(this)
        cartsRecycler.setHasFixedSize(true)
        cartsRecycler.adapter = CartItemAdapter(restaurantId, carts, ::addTotalPrice)

        widgetTotalPrice = findViewById(R.id.checkout_total_price)
        widgetTotalPrice.setText(totalPrice.toString())

        widgetPickupData = findViewById(R.id.input_pickup_data)

        findViewById<Button>(R.id.checkout_button).setOnClickListener{
            onCheckout()
        }


        widgetCamera.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
            }
        })
    }
    private fun takePicture(){
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val uri=FileProvider.getUriForFile(this,"", photoFile)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(pictureIntent, 1)
    }

    private fun createImageFile(): File {
        val timeStamp: String=SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File?=getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply{
            currentPhotoPath = absolutePath
        }
    }
}