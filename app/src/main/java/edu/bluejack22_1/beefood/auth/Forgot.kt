package edu.bluejack22_1.beefood.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking

fun onForgot(){

}

class Forgot : AppCompatActivity() {

    lateinit var widgetForgotButton : Button
    lateinit var widgetEmail : EditText
    lateinit var widgetErrorText : TextView

    fun onRedirectLogin(){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun onForgot(){
        val email = widgetEmail.text.toString()
        val isEmailExist = runBlocking { ClassUser.isEmailExist(email) }
        if(!isEmailExist){
            widgetErrorText.setText("email not found")
            return
        }
        widgetErrorText.setText("found")
        ClassUser.sendForgotPasswordEmail(email, this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_forgot)

        widgetForgotButton = findViewById(R.id.forgotButton)
        widgetEmail = findViewById(R.id.input_email)
        widgetErrorText = findViewById(R.id.error_text)
        findViewById<Button>(R.id.redirectLoginButton).setOnClickListener { onRedirectLogin() }

        widgetForgotButton.setOnClickListener{onForgot()}

    }
}