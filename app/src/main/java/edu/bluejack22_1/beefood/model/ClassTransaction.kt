package edu.bluejack22_1.beefood.model

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack22_1.beefood.R
import kotlinx.coroutines.tasks.await

class ClassTransaction(
    var id : String,
    var data : String,
    var restaurantId : String,
    var customerId : String,
    var senderId : String,
    var status : String,
    var timestamp : String,
    var customerPictureLink : String,
    var senderPictureLink : String,
)  {

    companion object {

        val db = Firebase.firestore


        fun transactionFromSnapshot(transactionSnapshot : DocumentSnapshot) : ClassTransaction{
            return ClassTransaction(
                transactionSnapshot.id,
                transactionSnapshot.data?.get("data").toString(),
                transactionSnapshot.data?.get("restaurantId").toString(),
                transactionSnapshot.data?.get("customerId").toString(),
                transactionSnapshot.data?.get("senderId").toString(),
                transactionSnapshot.data?.get("status").toString(),
                transactionSnapshot.data?.get("timestamp").toString(),
                transactionSnapshot.data?.get("customerPictureLink").toString(),
                transactionSnapshot.data?.get("senderPictureLink").toString(),
            )
        }

        fun transactionFromHashmap(transactionHashmap : kotlin.collections.HashMap<String, *>) : ClassTransaction{
            Log.d("transactionhashmap", transactionHashmap.toString())
            return ClassTransaction(
                transactionHashmap.get("id").toString(),
                transactionHashmap.get("data").toString(),
                transactionHashmap.get("restaurantId").toString(),
                transactionHashmap.get("customerId").toString(),
                transactionHashmap.get("senderId").toString(),
                transactionHashmap.get("status").toString(),
                transactionHashmap.get("timestamp").toString(),
                transactionHashmap.get("customerPictureLink").toString(),
                transactionHashmap.get("senderPictureLink").toString(),
            )
        }


        suspend fun createTransaction(
            data : String,
            restaurantId : String,
            customerId : String,
            senderId : String,
            status : String
        ): String {
            val newTransaction = hashMapOf(
                "data" to data,
                "restaurantId" to restaurantId,
                "customerId" to customerId,
                "senderId" to senderId,
                "status" to status,
                "timestamp" to FieldValue.serverTimestamp(),
                "customerPictureLink" to "",
                "senderPictureLink" to ""
            )
            var ref = db
                .collection("transactions")
                .add(newTransaction)
                .await()
            return ref.id
        }


        suspend fun getTransactionIds(): ArrayList<String> {
            val userId = ClassUser.staticUser?.id.toString()
            val transactionsSnapshot = db
                .collection("transactions")
                .whereEqualTo("customerId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()                .await()
            var transactionIds : ArrayList<String> = arrayListOf()
            for(transactionSnapshot in transactionsSnapshot.documents){
                transactionIds.add(transactionSnapshot.id)
            }
            return transactionIds

        }


        fun getTransactionById(id :  String) =
            ClassUser.db.collection("transactions").document(id)

        suspend fun updateTransactionStatus(transactionId : String){
            var transaction = transactionFromHashmap(
                getTransactionById(transactionId).get().await().data  as HashMap<String, *>
            )
            var newStatus = ""
            if(transaction.status == "cooking"){
                newStatus = "sending"

//            }else if(transaction.status == "waiting"){
//                newStatus = "sending"

            }else if(transaction.status == "sending"){
                newStatus = "done"
            }
            if(newStatus == "") return // already done


            ClassUser.db.collection("transactions").document(transactionId)
                .update(mapOf(
                    "status" to newStatus
                ))
            lookForSender(transactionId)
        }

        suspend fun matchTransactionAndSender(transactionId: String, senderId: String){
            ClassUser.db.collection("transactions").document(transactionId)
                .update(mapOf(
                    "senderId" to senderId
                ))
            ClassUser.db.collection("users").document(senderId)
                .update(mapOf(
                    "status" to "busy"
                ))
        }

        suspend fun lookForSender(transactionId: String){
            var senderId = ClassUser.getIdleSender()
            Log.d("transaction sender", senderId)
            if(senderId == "") return
            matchTransactionAndSender(transactionId, senderId)

        }

//        SENDER

        suspend fun getActiveSenderTransaction() : ClassTransaction?{
            Log.d("get active transaction user id ", ClassUser.getCurrentUser()?.id!!)
            var ref = db
                .collection("transactions")
                .whereEqualTo("senderId", ClassUser.getCurrentUser()?.id!!)
                .whereEqualTo("status", "sending")
                .get()
                .await()
            Log.d("get active transaction size", ref.documents.size.toString())
            if(ref.documents.size == 0) return null
            return transactionFromSnapshot(ref.documents.get(0))
        }

        fun doneTransaction(transactionId: String, pictureLink : String){
            Log.d("transaction done model", transactionId)
            ClassUser.db.collection("transactions").document(transactionId)
                .update(mapOf(
                    "status" to "done",
                    "senderPictureLink" to pictureLink
                ))
        }

//        SELLER


        suspend fun getActiveTransactionIds(threshold : Long, offset : Long, lastId : String) : ArrayList<String>{
            Log.d("inf scroll", "get owned transactions: " + threshold + " " + offset + " " + lastId)
            var ownedRestaurantIds = ClassRestaurant.getOwnedRestaurantIds()
            Log.d("transaction restaurant ids", ownedRestaurantIds.toString())

            val userId = ClassUser.staticUser?.id.toString()
            var transactionQuery = db
                .collection("transactions")
                .limit(threshold)
                .whereNotEqualTo("status", "done")
                .whereIn("restaurantId", ownedRestaurantIds)
//                .orderBy("status")

            if(offset > 0){

                var cursor = db.collection("transactions").document(lastId).get().await()
                Log.d("inf scroll", cursor.toString())

                transactionQuery = transactionQuery
                    .startAfter(cursor)
            }

            var transactionsSnapshot = transactionQuery
                .get()
                .await()
            var transactionIds : ArrayList<String> = arrayListOf()
            for(transactionSnapshot in transactionsSnapshot.documents){
                Log.d("transaction rest id", transactionSnapshot.data?.get("restaurantId").toString())
//                if(ownedRestaurantIds.contains(transactionSnapshot.data?.get("restaurantId")))
                    transactionIds.add(transactionSnapshot.id)
            }
            return transactionIds
        }

        fun translateTransactionStatus(status : String, context : Context) : String{
            if(status.toLowerCase() == "done") return context.getString(R.string.done)
            if(status.toLowerCase() == "cooking") return context.getString(R.string.cooking)
            if(status.toLowerCase() == "sending") return context.getString(R.string.sending)
            return ""
        }

        suspend fun getPastTransactionIds(threshold : Long, offset : Long, lastId : String) : ArrayList<String>{
            Log.d("inf scroll", "get owned transactions: " + threshold + " " + offset + " " + lastId)
            var ownedRestaurantIds = ClassRestaurant.getOwnedRestaurantIds()
            Log.d("transaction restaurant ids", ownedRestaurantIds.toString())

            val userId = ClassUser.staticUser?.id.toString()
            var transactionQuery = db
                .collection("transactions")
                .limit(threshold)
                .whereEqualTo("status", "done")
                .whereIn("restaurantId", ownedRestaurantIds)
//                .orderBy("status")

            if(offset > 0){

                var cursor = db.collection("transactions").document(lastId).get().await()
                Log.d("inf scroll", cursor.toString())

                transactionQuery = transactionQuery
                    .startAfter(cursor)
            }

            var transactionsSnapshot = transactionQuery
                .get()
                .await()
            var transactionIds : ArrayList<String> = arrayListOf()
            for(transactionSnapshot in transactionsSnapshot.documents){
                Log.d("transaction rest id", transactionSnapshot.data?.get("restaurantId").toString())
//                if(ownedRestaurantIds.contains(transactionSnapshot.data?.get("restaurantId")))
                transactionIds.add(transactionSnapshot.id)
            }
            return transactionIds
        }
    }
}