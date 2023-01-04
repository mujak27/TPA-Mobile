package edu.bluejack22_1.beefood.user

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassUser
import java.net.URL
import java.util.concurrent.Executors


class SenderProfile : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding

    val user=ClassUser.getCurrentUser()
    var image: Bitmap? = null
    val url = user?.pictureLink.toString()

    fun setTextProfile(){
        val name : TextView = findViewById<TextView>(R.id.profile_sender_name)
        val desc : TextView = findViewById<TextView>(R.id.profile_sender_desc)

        name.setText(user?.name.toString())
        desc.setText(user?.desc.toString())
        val img : ImageView = findViewById<ImageView>(R.id.sender_profile_pic)
//        val executor = Executors.newSingleThreadExecutor()
//        val inp = URL(url).openStream()
//        val bMap = BitmapFactory.decodeStream(inp)
//        img.setImageBitmap(bMap)
        DownloadImageFromInternet(findViewById(R.id.sender_profile_pic)).execute(url)
    }
    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
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
        setContentView(R.layout.activity_sender_profile)
        setTextProfile()
        findViewById<Button>(R.id.redirect_edit).setOnClickListener{
            val intent = Intent(this, SenderProfileEdit::class.java)
            this.startActivity(intent)
        }

    }
}
