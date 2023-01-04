package edu.bluejack22_1.beefood.user

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassUser

class SenderProfileEdit : AppCompatActivity() {

    val user= ClassUser.getCurrentUser()
    var image: Bitmap? = null
    val url = user?.pictureLink.toString()

    fun setInitValue(){
        val name : EditText = findViewById(R.id.edit_profile_name)
        val desc : EditText = findViewById(R.id.edit_profile_desc)

        name.setText(user?.name.toString())
        desc.setText(user?.desc.toString())


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender_profile_edit)
    }
}
