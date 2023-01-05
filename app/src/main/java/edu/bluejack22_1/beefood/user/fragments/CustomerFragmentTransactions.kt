package edu.bluejack22_1.beefood.user.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.TransactionItemAdapter
import edu.bluejack22_1.beefood.model.ClassTransaction
import kotlinx.coroutines.runBlocking

class CustomerFragmentTransactions : Fragment() {
    private lateinit var transactionRecycler : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionIds = runBlocking { ClassTransaction.getTransactionIds()}
        Log.d("transactionids", transactionIds.toString())

        transactionRecycler = view.findViewById(R.id.recyclerViewTransactions)
        transactionRecycler.layoutManager = LinearLayoutManager(activity)
        transactionRecycler.setHasFixedSize(true)
        transactionRecycler.adapter = TransactionItemAdapter(transactionIds, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomerFragmentTransactions.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerFragmentTransactions().apply {
                arguments = Bundle().apply {
                }
            }
    }
}