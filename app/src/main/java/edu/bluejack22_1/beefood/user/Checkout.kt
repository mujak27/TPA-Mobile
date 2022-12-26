package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassCart

class Checkout : AppCompatActivity() {

    private lateinit var carts : ArrayList<ClassCart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val carts = intent.getSerializableExtra("carts") as? ArrayList<ClassCart>

        Log.d("carts", carts.toString())



    }
}