package edu.bluejack22_1.beefood.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Login : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var loginButton : Button
    lateinit var redirectRegisterButton : Button
    lateinit var redirectForgotButton : Button
    lateinit var errorText : TextView
    val Req_Code:Int=123 // get from
    val firebaseAuth= FirebaseAuth.getInstance()


    val db = Firebase.firestore

    fun onLogin(){
        var email = findViewById<EditText>(R.id.input_email).text.toString()
        var pass = findViewById<EditText>(R.id.input_pass).text.toString()
        Log.d("name", email)

        if (ClassUser.loginUser(email, pass)){
            Log.d("login", "found")
            errorText.setText("found")
            val intent = Intent(this, ClassUser.redirectToHomeBasedOnUserRole())
            startActivity(intent)
        }else{
            Log.d("login", "not found")
            errorText.setText("not found")
        }
    }

    fun onRedirectRegister(){
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }

    fun onRedirectForgot(){
        val intent = Intent(this, Forgot::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_auth_login)
        loginButton = findViewById<Button>(R.id.loginButton)
        findViewById<Button>(R.id.redirectRegisterButton).setOnClickListener{onRedirectRegister()}
        findViewById<Button>(R.id.redirectForgotButton).setOnClickListener{onRedirectForgot()}
        errorText = findViewById<Button>(R.id.error_text)
        loginButton.setOnClickListener{ onLogin()}
    }
}

