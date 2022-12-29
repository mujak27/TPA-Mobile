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
            Log.d("menu snapshot", menuSnapshot.toString())
            Log.d("menu snapshot data", menuSnapshot.data.toString())
            return ClassMenu(
                menuSnapshot.id,
                menuSnapshot.data?.get("name").toString(),
                menuSnapshot.data?.get("price").toString().toInt(),
                menuSnapshot.data?.get("imageLink").toString(),
                menuSnapshot.data?.get("desc").toString(),
            )
        }

        suspend fun getMenusFromRestaurantId(restaurantId : String) : ArrayList<ClassMenu>{

            val menusSnapshot = db.collection("restaurants").document(restaurantId).collection("menus").get().await()
            var menus : ArrayList<ClassMenu> = arrayListOf();
            for(menuSnapshot in menusSnapshot){
                menus.add(menuFromSnapshot(menuSnapshot));
            }

            return menus;
        }

        suspend fun getMenuById(restaurantId: String, menuId : String) : ClassMenu{
            Log.d("get menu restaurant", restaurantId + " " + menuId)
            val menuSnapshot = db.collection("restaurants").document(restaurantId).collection("menus").document(menuId).get().await()
            return menuFromSnapshot(menuSnapshot)
        }

    }
}


