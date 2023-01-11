package edu.bluejack22_1.beefood.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClassRating(
    var id : String,
    var userId : String,
    var rating : Float
) {

    companion object {

        val db = Firebase.firestore

        suspend fun getMyRating(restaurantId: String) : Float{
            var ratingsSnapshot = ClassRestaurant.db.collection("restaurants").document(restaurantId).collection("ratings")
                .whereEqualTo("userId", ClassUser.getCurrentUser()?.id!!)
                .get().await()
            if(ratingsSnapshot.documents.size == 0) return -1f
            return ratingsSnapshot.documents.get(0).data?.get("rating").toString().toFloat()
        }

        suspend fun updateRestaurantReview(restaurantId: String){

            var ratingsSnapshot = ClassRestaurant.db.collection("restaurants").document(restaurantId).collection("ratings").get().await()
            var totalRating = 0f
            for(ratingSnapshot in ratingsSnapshot.documents){
                totalRating += ratingSnapshot.get("rating").toString().toFloat()
            }
            var meanRating = totalRating / ratingsSnapshot.documents.size

            ClassRestaurant.db.collection("restaurants").document(restaurantId).update(mapOf(
                "rating" to meanRating.toString()
            ))
        }

        suspend fun createRating(restaurantId : String, rating : Float){
            ClassRestaurant.db.collection("restaurants").document(restaurantId).collection("ratings").add(
                mapOf(
                    "userId" to ClassUser.getCurrentUser()?.id!!,
                    "rating" to rating.toString()
                )
            ).await()
            updateRestaurantReview(restaurantId)
        }
    }
}