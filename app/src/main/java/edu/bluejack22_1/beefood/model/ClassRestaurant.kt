package edu.bluejack22_1.beefood.model

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beefood.R
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


            var restaurantsSnapshot = restaurantQuery
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

        fun translateTransactionStatus(status : String, context : Context) : String{
            if(status == "cooking") return context.getString(R.string.cooking)
            else if(status == "sending") return context.getString(R.string.sending)
            else if(status == "done") return context.getString(R.string.done)
            return ""
        }

//        SELLER

        suspend fun getOwnedRestaurantIds() : ArrayList<String>{

            Log.d("transaction get restaurant ids", ClassUser.getCurrentUser()?.id!!)
            var restaurantsSnapshot = db.collection("restaurants")
                .whereEqualTo("ownerId", ClassUser.getCurrentUser()?.id!!)
                .get()
                .await()

            var restaurantIds : ArrayList<String> = ArrayList()
            for(restaurantSnapshot in restaurantsSnapshot.documents){
                Log.d("transaction restaurant ", restaurantSnapshot.id)
                restaurantIds.add(
                    restaurantSnapshot.id
                )
            }
            return restaurantIds
        }

        suspend fun getOwnedRestaurants(threshold : Long, offset : Long, lastId : String) : ArrayList<ClassRestaurant>{
            Log.d("inf scroll", "get owned restaurants: " + threshold + " " + offset + " " + lastId)

            var restaurantQuery = db.collection("restaurants")
                .whereEqualTo("ownerId", ClassUser.getCurrentUser()?.id!!)

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


        suspend fun getOwnedRestaurantsByName(name : String, threshold : Long, offset : Long) : ArrayList<ClassRestaurant>{

            Log.d("inf scroll", "getRestaurantbyname : " + threshold + " " + offset)
//
            var restaurantQuery = db.collection("restaurants")
                .orderBy("name")


            var restaurantsSnapshot = restaurantQuery
                .whereEqualTo("ownerId", ClassUser.getCurrentUser()?.id!!)
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

    }
}


