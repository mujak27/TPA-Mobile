package edu.bluejack22_1.beefood.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.model.ClassTransaction
import edu.bluejack22_1.beefood.user.TransactionDetail

class TransactionItemAdapter(
    private val transactionIds : ArrayList<String>,
) : RecyclerView.Adapter<TransactionItemAdapter.MyViewHolder>() {

    lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        this.context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transactionIds.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var widgetTransaction : FrameLayout
        var widgetId : TextView
        var widgetStatus : TextView
        init {
            widgetTransaction = itemView.findViewById(R.id.item_transaction)
            widgetId = itemView.findViewById(R.id.item_transaction_id)
            widgetStatus = itemView.findViewById<Button>(R.id.item_transaction_status)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currTransaction = ClassTransaction.getTransactionById(transactionIds.get(position))
        holder.widgetId.setText(currTransaction.id)

        currTransaction.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("snapshot data", snapshot.data.toString())
                holder.widgetStatus.setText(snapshot.data?.get("status").toString())
            } else {
                holder.widgetStatus.setText("not found")
            }
        }

        holder.widgetTransaction.setOnClickListener{
            val intent = Intent(this.context, TransactionDetail::class.java)
            intent.putExtra("transactionId", currTransaction.id)
            this.context.startActivity(intent)
        }
    }
}