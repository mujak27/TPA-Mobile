
package edu.bluejack22_1.beefood.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking


class Register : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var registerButton : Button
    lateinit var redirectLoginButton : Button
    lateinit var errorText : TextView




    suspend fun onRegister(){
        var name = findViewById<EditText>(R.id.input_name).text.toString()
        var email = findViewById<EditText>(R.id.input_email).text.toString()
        var pass = findViewById<EditText>(R.id.input_pass).text.toString()

        Log.d("register name email kt", name + ' ' + email)

        if(name.length == 0 || email.length == 0 || pass.length == 0){
            errorText.setText("all data must be filled")
            return
        }

        if(ClassUser.isEmailExist(name)){
            errorText.setText(R.string.email_taken)
            return
        }

        Log.d("register", "valid new user")

        if(ClassUser.registerUser(email, name, pass)){
            val intent = Intent(this, ClassUser.redirectToHomeBasedOnUserRole())
            startActivity(intent)
        }else{
            errorText.setText("error")
        }

    }

    fun onRedirectLogin(){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_auth_register)

        registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener{ runBlocking { onRegister() }}

        redirectLoginButton = findViewById<Button>(R.id.redirectLoginButton)
        redirectLoginButton.setOnClickListener{onRedirectLogin()}

        errorText = findViewById<Button>(R.id.error_text)
    }
}
