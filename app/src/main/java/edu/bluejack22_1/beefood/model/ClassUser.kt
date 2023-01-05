package edu.bluejack22_1.beefood.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.auth.Login
import edu.bluejack22_1.beefood.seller.SellerHome
import edu.bluejack22_1.beefood.sender.SenderHome
import edu.bluejack22_1.beefood.user.Home
import edu.bluejack22_1.beefood.user.SenderProfile
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
    var role : String,
    var pictureLink : String,
    var status : String,
    var isSending : Boolean
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
                userHashmap.get("pictureLink").toString(),
                userHashmap.get("status").toString(),
                userHashmap.get("isSending").toString().toBoolean(),
            )
        }


        fun setUser(user : ClassUser){
            this.staticUser = user
        }
        suspend fun getUserByEmail(email :  String) : ClassUser {

            Log.d("login get user by email", email)
            var userSnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get().await()
            var user = userFromHashmap(userSnapshot.documents.get(0).data as kotlin.collections.HashMap<String, *>)
            return user
        }

        suspend fun isEmailExist(email : String) : Boolean {
            var userSnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get().await()
            if(userSnapshot.documents.size == 0) return false
            return true
        }

        suspend fun getUserById(id : String): ClassUser{
            Log.d("login get user by id", id)
            var userSnapshot = ClassRestaurant.db.collection("users").document(id).get().await()
            var user = userFromHashmap(userSnapshot.data as kotlin.collections.HashMap<String, *>)
            return user
        }

        suspend fun registerUser(email : String, name : String, pass : String) : Boolean{
            Log.d("register email name", email + " " + name)
            var userId = UUID.randomUUID().toString()
            val user = ClassUser(userId, email, name, "", pass, "Customer", "", "", false)
            var valid = true
            db.collection("users").document(userId).set(user)
                .addOnSuccessListener {  }
                .addOnFailureListener { valid=false }
                .await()
            setUser(user)
            auth.createUserWithEmailAndPassword(email, pass).await()
            return valid
        }

        fun loginUser(email : String, pass : String) : Boolean{
            var valid = false
            Log.d("login user ", email + " " + pass)
            runBlocking {
                try {
                    var x = auth.signInWithEmailAndPassword(email, pass).await()
                    Log.d("user x", x.toString())
                    Log.d("user", x.user.toString())
                    valid = true
                    Log.d("login", "auth found")
                }catch (e : Exception){
                    Log.d("login", "auth not found")
                    valid = false
                }
            }
            runBlocking {
                var users = db.collection("users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("pass", pass)
                    .get().await()
                if(users.isEmpty){
                }else{
                    setUser(userFromHashmap(users.documents.get(0).data as HashMap<String, *>))
                    valid = true

                }
            }
            return valid
        }

        fun googleLogin(email: String){
            Log.d("google login auth", auth.currentUser?.email.toString())
            if(runBlocking { isEmailExist(email) } ){
                // email has registered before

            }else{
                // new account
            }
        }

        fun updateUser(name : String, desc : String, pictureLink: String){
            db.collection("users").document(staticUser?.id as String)
                .update(mapOf(
                    "name" to name,
                    "desc" to desc,
                    "pictureLink" to pictureLink
                ))
        }

        fun getCurrentUser() : ClassUser?{
            return staticUser;
        }

        fun redirectToHomeBasedOnUserRole() : Class<*>{
//            val authEmail = auth.currentUser?.email.toString()
//            if(authEmail!=""){
//                staticUser = runBlocking { getUserByEmail(authEmail) }
//            }
            if(staticUser == null) {
                Log.d("role", "null")
                return Login::class.java
            }
            if(staticUser!!.role == "Customer"){
                Log.d("role", "customer")
                return Home::class.java
            } else if(staticUser!!.role=="Sender"){
                Log.d("role","Sender")
                return SenderHome::class.java
            }else if(staticUser!!.role=="Seller"){
                Log.d("role","Seller")
                return SellerHome::class.java
            }
            else{
                Log.d("role", "else")

            }
            return Login::class.java
        }


        public fun sendForgotPasswordEmail(email: String) {
            auth.sendPasswordResetEmail(email)
        }

        //        SENDER

        fun translateStatus(status : String, context : Context) : String{
            if(status == "working") return context.getString(R.string.working)
            else if(status == "standby") return context.getString(R.string.standby)
            return ""
        }

        fun changeStatus(context: Context) : String{
            var newStatus = "working"
            if(staticUser?.status == "working") newStatus = "standby"
            db.collection("users").document(staticUser?.id as String)
                .update(mapOf(
                    "status" to newStatus
                ))
            return translateStatus(newStatus, context)
        }

        // SELLER
        suspend fun getIdleSender() : String{ // idle is working but is not sending any food right now
            var userSnapshot = ClassRestaurant.db.collection("users")
                .whereEqualTo("role", "sender")
                .whereEqualTo("isSending", false)
                .limit(1)
                .get().await()
            if(userSnapshot.documents.size == 0) return ""
            return userSnapshot.documents.get(0).id
        }

    }
}
