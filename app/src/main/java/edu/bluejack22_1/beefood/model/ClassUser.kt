package edu.bluejack22_1.beefood.model

import android.util.Log
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
        var pass : String,
        var role : String
    ) {

    companion object {
        val db = Firebase.firestore
        var staticUser : ClassUser? = null

        fun userFromHashmap(userHashmap : kotlin.collections.HashMap<String, *>) : ClassUser{
            Log.d("userhashmap", userHashmap.toString())
            return ClassUser(
                userHashmap.get("id").toString(),
                userHashmap.get("email").toString(),
                userHashmap.get("name").toString(),
                userHashmap.get("pass").toString(),
                userHashmap.get("role").toString()
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

        suspend fun registerUser(email : String, name : String, pass : String) : Boolean{
            var userId = UUID.randomUUID().toString()
            val user = ClassUser(userId, email, name, pass, "Customer")
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
    }
}



//.addOnSuccessListener { result ->
//    if(result.isEmpty){
//        return false
//    }else{
//        Log.d("loginResult", "found")
////                    val intent = Intent(this, Register::class.java)
////                    startActivity(intent)
//    }
//}
//.addOnFailureListener { exception ->
//    Log.w("error", "Error getting documents.", exception)
//    errorText.setText("error occured: " + exception)
//}