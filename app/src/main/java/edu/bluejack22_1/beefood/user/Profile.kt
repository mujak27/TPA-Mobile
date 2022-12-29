package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassUser

class Profile : AppCompatActivity() {

    fun onUpdate(){
        var name = findViewById<EditText>(R.id.input_name).text.toString()
        var desc = findViewById<EditText>(R.id.input_desc).text.toString()
        ClassUser.updateUser(name, desc)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var user = ClassUser.getCurrentUser()

        findViewById<EditText>(R.id.input_name).setText(user?.name)
        findViewById<EditText>(R.id.input_desc).setText(user?.desc)

        findViewById<Button>(R.id.button_update).setOnClickListener {
            onUpdate()
        }


    }
}