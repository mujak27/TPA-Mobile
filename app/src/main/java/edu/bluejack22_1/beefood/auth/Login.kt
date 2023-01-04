package edu.bluejack22_1.beefood.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking


class Login : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var loginButton : Button
    lateinit var redirectRegisterButton : Button
    lateinit var redirectForgotButton : Button
    lateinit var errorText : TextView
    val Req_Code:Int=123 // get from


    val db = Firebase.firestore
    val auth = Firebase.auth

    fun onLogin(){
        if(auth.currentUser != null) auth.signOut()
        var email = findViewById<EditText>(R.id.input_email).text.toString()
        var pass = findViewById<EditText>(R.id.input_pass).text.toString()
        Log.d("name", email)

        if (ClassUser.loginUser(email, pass)){
            Log.d("login", "found")
            errorText.setText("found")
            updateUI()
        }else{
            Log.d("login", "not found")
            errorText.setText(R.string.not_found)
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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("447661352312-jr4uorr35qnuer6s2rlr244khom9584q.apps.googleusercontent.com")
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        signInButton.setOnClickListener{
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 123 )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 123) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.d("google", "request code match")

            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            Log.d("google login", "success")
            Log.d("google login account id", account.id.toString())
            Log.d("google login account name", account.displayName.toString())
            Log.d("google login account idtoken", account.idToken.toString())
//            ClassUser.googleLogin(account.email.toString())
//            Log
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("google login", "failed signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String){
        if(auth.currentUser != null) auth.signOut()
        Log.d("google login firebase auth", "called")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this){task->
            if(task.isSuccessful){
                Log.d("google login ", "signInWithCredential:success")
                val user = auth.currentUser
                if (user != null) {
                    if(runBlocking { ClassUser.isEmailExist(user.email!!)}){
                        Log.d("google login ", "Document already exists.")
                    }else{
                        Log.d("google login ", "create new")
                        runBlocking { ClassUser.registerUser(user.email.toString(), user.displayName.toString(), "anonim") }
                        val intent = Intent(this, ClassUser.redirectToHomeBasedOnUserRole())
                        startActivity(intent)
                    }
                }
                ClassUser.setUser( runBlocking { ClassUser.getUserByEmail(user?.email.toString()) })
                updateUI()
            }
            else{
                Log.d("google login", "failed")
            }

        }
    }

    fun updateUI(){
        val intent = Intent(this, ClassUser.redirectToHomeBasedOnUserRole())
        startActivity(intent)
    }
}

