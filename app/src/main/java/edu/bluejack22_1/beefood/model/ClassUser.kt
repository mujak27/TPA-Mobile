package edu.bluejack22_1.beefood.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beefood.auth.Login
import edu.bluejack22_1.beefood.user.Home
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

class ClassUser (
        var id : String,
        var email : String,
        var name : String,
        var desc : String,
        var pass : String,
        var role : String
    ) {

    companion object {
        val db = Firebase.firestore
        val auth = Firebase.auth
        public var staticUser : ClassUser? = null

        fun userFromHashmap(userHashmap : kotlin.collections.HashMap<String, *>) : ClassUser{
            Log.d("userhashmap", userHashmap.toString())
            return ClassUser(
                userHashmap.get("id").toString(),
                userHashmap.get("email").toString(),
                userHashmap.get("name").toString(),
                userHashmap.get("desc").toString(),
                userHashmap.get("pass").toString(),
                userHashmap.get("role").toString(),
            )
        }


        fun setUser(user : ClassUser){
            this.staticUser = user
        }

        fun getUserByEmail(email :  String) =
            db.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()

        suspend fun isEmailExist(email : String) : Boolean {
            val users = getUserByEmail(email).await()
            return !users.isEmpty
        }

        suspend fun getUserById(id : String): ClassUser{
            var userSnapshot = ClassRestaurant.db.collection("users").document(id).get().await()
            var user = userFromHashmap(userSnapshot.data as kotlin.collections.HashMap<String, *>)
            return user
        }


        suspend fun registerUser(email : String, name : String, pass : String) : Boolean{
            var userId = UUID.randomUUID().toString()
            val user = ClassUser(userId, email, name, "", pass, "Customer")
            var valid = true
            db.collection("users").document(userId).set(user)
                .addOnSuccessListener {  }
                .addOnFailureListener { valid=false }
                .await()
            setUser(user)
            return valid
        }

        fun loginUser(email : String, pass : String) : Boolean{
            var valid = false
            runBlocking {
                var users = db.collection("users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("pass", pass)
                    .get().await()
                valid = !users.isEmpty
                if(users.isEmpty){
                    Log.d("loginResult", "not found")
                }else{
                    Log.d("loginResult", "found")
                    setUser(userFromHashmap(users.documents.get(0).data as HashMap<String, *>))

                }
            }
            Log.d("valid", if (valid) "true" else "false")
            return valid
        }

        fun updateUser(name : String, desc : String){
            db.collection("users").document(staticUser?.id as String)
                .update(mapOf(
                    "name" to name,
                    "desc" to desc
                ))
        }

        fun getCurrentUser() : ClassUser?{
            return staticUser;
        }

        fun redirectToHomeBasedOnUserRole() : Class<*>{
            if(staticUser == null) {
                Log.d("role", "null")
                return Login::class.java
            }
            if(staticUser!!.role == "Customer"){
                Log.d("role", "customer")
                return Home::class.java
            }
            else{
                Log.d("role", "else")

            }
            return Login::class.java
        }


        public fun sendForgotPasswordEmail(email: String, context : Context) {

            var resetId = UUID.randomUUID().toString()
            db.collection("resets").document(resetId).set(
                hashMapOf(
                    "email" to email
                )
            )

            var resetLink = "http://www.beefood-app.com/reset/" + resetId

            /*ACTION_SEND action to launch an email client installed on your Android device.*/
            val mIntent = Intent(Intent.ACTION_SEND)
            /*To send an email you need to specify mailto: as URI using setData() method
            and data type will be to text/plain using setType() method*/
            mIntent.data = Uri.parse("mailto:")
            mIntent.type = "text/plain"
            // put recipient email in intent
            /* recipient is put as array because you may wanna send email to multiple emails
               so enter comma(,) separated emails, it will be stored in array*/
            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            //put the Subject in the intent
            mIntent.putExtra(Intent.EXTRA_SUBJECT, "reset password")
            //put the message in the intent
            mIntent.putExtra(Intent.EXTRA_TEXT, "resetLink")


            try {
                //start email intent
                context.startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
            }
            catch (e: Exception){
                //if any thing goes wrong for example no email client application or any exception
                //get and show exception message
                Log.d("error", e.toString())
            }

        }
    }
}