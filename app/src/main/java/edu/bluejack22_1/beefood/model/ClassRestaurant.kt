package edu.bluejack22_1.beefood.model

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClassRestaurant(
    var id : String,
    var name : String,
    var ownerId : String,
    var rating : Float,
    var desc : String
) {

    companion object{

        val db = Firebase.firestore

        fun restaurantFromHashmap(restaurantHashmap : kotlin.collections.HashMap<String, *>) : ClassRestaurant{
            Log.d("userhashmap", restaurantHashmap.toString())
            return ClassRestaurant(
                restaurantHashmap.get("id").toString(),
                restaurantHashmap.get("name").toString(),
                restaurantHashmap.get("ownerId").toString(),
                restaurantHashmap.get("rating").toString().toFloat(),
                restaurantHashmap.get("desc").toString(),
            )
        }

        suspend fun getRestaurantWithBiggestRating(limit : Long, offset : Long) : ArrayList<ClassRestaurant>{

            Log.d("rest debug", "1")
            var restaurantsSnapshot = db.collection("restaurants")
                .orderBy("rating", Query.Direction.DESCENDING)
//                .startAt(offset)
//                .limit(limit)
                .get()
                .await()

            Log.d("rest debug", "2")

            var restaurants : ArrayList<ClassRestaurant> = ArrayList()
            for(restaurantSnapshot in restaurantsSnapshot){
                restaurants.add(
                    restaurantFromHashmap(restaurantSnapshot.data as HashMap<String, *>)
                )
                Log.d("rest debug", "3")
                Log.d("rest name", restaurantFromHashmap(restaurantSnapshot.data as HashMap<String, *>).name)
            }

            Log.d("rest debug", "4")

            return restaurants

        }
    }
}


