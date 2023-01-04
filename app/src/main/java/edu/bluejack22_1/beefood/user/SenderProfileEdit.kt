package edu.bluejack22_1.beefood.user

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.helper.classStorage
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking
import java.net.URL

class SenderProfileEdit : AppCompatActivity() {

    val user= ClassUser.getCurrentUser()
    var image: Bitmap? = null
    val url = user?.pictureLink.toString()

    fun setInitValue(){
        val name : EditText = findViewById(R.id.edit_profile_name)
        val desc : EditText = findViewById(R.id.edit_profile_desc)
        var pL = findViewById<EditText>(R.id.edit_picLink)
        name.setText(user?.name.toString())
        desc.setText(user?.desc.toString())
        pL.setText(user?.pictureLink.toString())
        val img : ImageView = findViewById<ImageView>(R.id.edit_profile_pic)
        DownloadImageFromInternet(findViewById(R.id.edit_profile_pic)).execute(url)
    }
    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(user?.pictureLink.toString()).openStream()
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

    lateinit var imageView : ImageView
    var isImageChanged = false


    fun onUpdate(){
        var name = findViewById<EditText>(R.id.edit_profile_name).text.toString()
        var desc = findViewById<EditText>(R.id.edit_profile_desc).text.toString()
        var pL = findViewById<EditText>(R.id.edit_picLink).text.toString()
        var pictureLink = ClassUser.getCurrentUser()?.pictureLink!!
        if(isImageChanged){
            pictureLink = runBlocking { classStorage.uploadPhoto(imageView).toString() }
        }
        ClassUser.updateUser(name, desc, pictureLink)
    }
    fun onSelectPhoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){
            isImageChanged = true
            Log.d("upload file on activ result", "req code 101")
            var pic : Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            Log.d("upload file bitmap", pic.toString())
            var img : ImageView = findViewById<ImageView>(R.id.edit_profile_pic)
            img.setImageBitmap(pic)
        }
    }
//    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender_profile_edit)
        setInitValue()
        var err : TextView = findViewById(R.id.error_edit)
        err.setText("")


//        imageView = findViewById(R.id.edit_profile_pic)
//        if(!user?.pictureLink.isNullOrBlank() && user?.pictureLink != "null" && user?.pictureLink.toString() != ""){
//            val newurl = URL(user?.pictureLink)
//            val bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
//            img.setImageBitmap(bitmap)
//        }
        findViewById<Button>(R.id.edit_upload_picture).setOnClickListener {
            onSelectPhoto()
        }
        findViewById<Button>(R.id.sender_edit_profile_button).setOnClickListener {


            var name = findViewById<EditText>(R.id.edit_profile_name).text.toString()
            var desc = findViewById<EditText>(R.id.edit_profile_desc).text.toString()
            var pL = findViewById<EditText>(R.id.edit_picLink).text.toString()
            if(name != "" && desc != ""){
                var pictureLink = ClassUser.getCurrentUser()?.pictureLink!!
                ClassUser.updateUser(name, desc, pL)
                ClassUser.staticUser?.pictureLink = pL
                ClassUser.staticUser?.desc = desc
                ClassUser.staticUser?.name = name

                val intent = Intent(this,SenderProfile::class.java)
                this.startActivity(intent)
            }else{
                err.setText("Fill desc aand name")
            }

        }
    }
}
