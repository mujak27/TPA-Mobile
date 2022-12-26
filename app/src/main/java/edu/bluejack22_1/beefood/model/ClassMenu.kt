package edu.bluejack22_1.beefood.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClassMenu(
    var id : String,
    var name : String,
    var price : Int,
    var imageLink : String,
    var desc : String
) {

    companion object{

        val db = Firebase.firestore

        fun restaurantFromHashmap(restaurantHashmap : kotlin.collections.HashMap<String, *>) : ClassRestaurant{
            Log.d("restauranthashmap", restaurantHashmap.toString())
            return ClassRestaurant(
                restaurantHashmap.get("id").toString(),
                restaurantHashmap.get("name").toString(),
                restaurantHashmap.get("ownerId").toString(),
                restaurantHashmap.get("rating").toString().toFloat(),
                restaurantHashmap.get("desc").toString(),
            )
        }

        fun menuFromSnapshot(menuSnapshot : DocumentSnapshot) : ClassMenu{
            return ClassMenu(
                menuSnapshot.id,
                menuSnapshot.data?.get("name").toString(),
                menuSnapshot.data?.get("price").toString().toInt(),
                menuSnapshot.data?.get("imageLink").toString(),
                menuSnapshot.data?.get("desc").toString(),
            )
        }

//        suspend fun getMenuById(id : String): ClassRestaurant{
//
//            var restaurantSnapshot = db.collection("restaurants").document(id).get().await()
//            var restaurant = restaurantFromSnapshot(restaurantSnapshot)
//            return restaurant
//        }

        suspend fun getMenusFromRestaurantId(restaurantId : String) : ArrayList<ClassMenu>{

            val menusSnapshot = db.collection("restaurants").document(restaurantId).collection("menus").get().await()
            var menus : ArrayList<ClassMenu> = arrayListOf();
            for(menuSnapshot in menusSnapshot){
                menus.add(menuFromSnapshot(menuSnapshot));
            }

            return menus;
        }

    }
}


