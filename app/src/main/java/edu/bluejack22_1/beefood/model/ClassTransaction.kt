package edu.bluejack22_1.beefood.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ClassTransaction(
    var id : String,
    var data : String,
    var restaurantId : String,
    var customerId : String,
    var senderId : String,
    var status : String,
    var timestamp : String
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
    }
}