package edu.bluejack22_1.beefood.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClassRating(
    var id : String,
    var userId : String,
    var rating : Float
) {

    companion object {

        val db = Firebase.firestore

        suspend fun createReview(restauratId : String, rating : Float){

        }
    }
}