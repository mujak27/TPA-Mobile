package edu.bluejack22_1.beefood.user

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


    }
}
