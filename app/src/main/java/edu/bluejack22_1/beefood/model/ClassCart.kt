package edu.bluejack22_1.beefood.model

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClassCart(
    var menuId : String,
    var quantity : Int
) : java.io.Serializable {



    companion object {

        val db = Firebase.firestore


        fun cartFromHashmap(cartHashmap : kotlin.collections.HashMap<String, *>) : ClassCart{
//            Log.d("cart hashmap", cartHashmap.toString())
            return ClassCart(
                cartHashmap.get("menuId").toString(),
                cartHashmap.get("quantity").toString().toInt(),
            )
        }



        fun insertCartToTransaction(transactionId : String, carts : ArrayList<ClassCart>){
            for (cart in carts){

                val newCart = hashMapOf(
                    "menuId" to cart.menuId,
                    "quantity" to cart.quantity,
                )
                var ref = ClassTransaction.db
                    .collection("transactions")
                    .document(transactionId)
                    .collection("carts")
                    .add(newCart)
            }
        }

        suspend fun getCartsFromTransaction(transactionId: String) : ArrayList<ClassCart>{
            var carts : ArrayList<ClassCart> = ArrayList()

            val cartsSnapshot = ClassTransaction.db
                .collection("transactions")
                .document(transactionId)
                .collection("carts")
                .get().await()
            for(cartSnapshot in cartsSnapshot.documents){
                carts.add(
                    cartFromHashmap(cartSnapshot.data as HashMap<String, *>)
                )
            }

            return carts
        }
    }

}