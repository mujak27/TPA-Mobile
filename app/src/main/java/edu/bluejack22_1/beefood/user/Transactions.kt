package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.MenuItemAdapter
import edu.bluejack22_1.beefood.adapter.TransactionItemAdapter
import edu.bluejack22_1.beefood.model.ClassTransaction
import kotlinx.coroutines.runBlocking

class Transactions : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
    }
}