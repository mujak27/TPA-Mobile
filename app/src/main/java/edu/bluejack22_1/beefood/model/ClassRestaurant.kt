package edu.bluejack22_1.beefood.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
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
            Log.d("restauranthashmap", restaurantHashmap.toString())
            return ClassRestaurant(
                restaurantHashmap.get("id").toString(),
                restaurantHashmap.get("name").toString(),
                restaurantHashmap.get("ownerId").toString(),
                restaurantHashmap.get("rating").toString().toFloat(),
                restaurantHashmap.get("desc").toString(),
            )
        }

        fun restaurantFromSnapshot(restaurantSnapshot : DocumentSnapshot) : ClassRestaurant{
            return ClassRestaurant(
                restaurantSnapshot.id,
                restaurantSnapshot.data?.get("name").toString(),
                restaurantSnapshot.data?.get("ownerId").toString(),
                restaurantSnapshot.data?.get("rating").toString().toFloat(),
                restaurantSnapshot.data?.get("desc").toString(),
            )
        }

        suspend fun getRestaurantByName(name : String, threshold : Long, offset : Long, lastId : String) : ArrayList<ClassRestaurant>{

            Log.d("inf scroll", "getRestaurantbyname : " + threshold + " " + offset + " " + lastId)

            var restaurantQuery = db.collection("restaurants")
                .orderBy("name")

//            if(offset > 0){
//
//                var cursor = db.collection("restaurants").document(lastId).get().await()
//                Log.d("inf scroll cursor", cursor.toString())
//
//                restaurantQuery = restaurantQuery
//                    .startAfter(cursor)
//            }

            var restaurantsSnapshot = restaurantQuery
//                .limit(threshold)
                .limit(threshold+offset)
                .startAt(name)
                .endAt(name + '\uf8ff')
                .get()
                .await()

            var restaurants : ArrayList<ClassRestaurant> = ArrayList()
            for(restaurantSnapshot in restaurantsSnapshot){
                Log.d("restaurant", restaurantSnapshot.toString())
                restaurants.add(
                    restaurantFromSnapshot(restaurantSnapshot)
                )
            }
            return restaurants
        }

        suspend fun getRestaurantById(id : String): ClassRestaurant{
            var restaurantSnapshot = db.collection("restaurants").document(id).get().await()
            var restaurant = restaurantFromSnapshot(restaurantSnapshot)
            return restaurant
        }

        suspend fun getRestaurantWithBiggestRating(threshold : Long, offset : Long, lastId : String) : ArrayList<ClassRestaurant>{
            Log.d("inf scroll", "getRestaurantWithBiggestRating: " + threshold + " " + offset + " " + lastId)

            var restaurantQuery = db.collection("restaurants")
                .orderBy("rating", Query.Direction.DESCENDING)

            if(offset > 0){

                var cursor = db.collection("restaurants").document(lastId).get().await()
                Log.d("inf scroll", cursor.toString())

                restaurantQuery = restaurantQuery
                    .startAfter(cursor)
            }

            var restaurantsSnapshot = restaurantQuery
                .limit(threshold)
                .get()
                .await()

            var restaurants : ArrayList<ClassRestaurant> = ArrayList()
            for(restaurantSnapshot in restaurantsSnapshot){
                restaurants.add(
                    restaurantFromSnapshot(restaurantSnapshot)
                )
            }
            return restaurants

        }
    }
}


