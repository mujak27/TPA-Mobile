package edu.bluejack22_1.beefood.user

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.helper.classStorage
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking
import java.net.URL


class Profile : AppCompatActivity() {

    lateinit var imageView : ImageView
    var isImageChanged = false

    fun onUpdate(){
        var name = findViewById<EditText>(R.id.input_name).text.toString()
        var desc = findViewById<EditText>(R.id.input_desc).text.toString()
        var pictureLink = ClassUser.getCurrentUser()?.pictureLink!!
        if(isImageChanged){
            pictureLink = runBlocking { classStorage.uploadPhoto(imageView).toString() }
        }
        ClassUser.updateUser(name, desc, pictureLink)
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
            isImageChanged = true
            Log.d("upload file on activ result", "req code 101")
            var pic : Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            Log.d("upload file bitmap", pic.toString())
            imageView.setImageBitmap(pic)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var user = ClassUser.getCurrentUser()

        imageView = findViewById(R.id.profile_photo)

        findViewById<EditText>(R.id.input_name).setText(user?.name)
        findViewById<EditText>(R.id.input_desc).setText(user?.desc)
        Log.d("upload file current user", user?.pictureLink!!)
        if(!user?.pictureLink.isNullOrBlank() && user?.pictureLink != "null" && user?.pictureLink.toString() != ""){
            val newurl = URL(user?.pictureLink)
            val bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
            imageView.setImageBitmap(bitmap)
        }

        findViewById<Button>(R.id.button_select_photo).setOnClickListener {
            onSelectPhoto()
        }

        findViewById<Button>(R.id.button_update).setOnClickListener {
            onUpdate()
        }

        if(ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),  100)
        }else{
            Log.d("upload file camera", "granted")
        }


    }
}
