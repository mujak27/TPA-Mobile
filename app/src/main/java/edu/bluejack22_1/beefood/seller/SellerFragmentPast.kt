package edu.bluejack22_1.beefood.seller

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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SellerFragmentPast.newInstance] factory method to
 * create an instance of this fragment.
 */
class SellerFragmentPast : Fragment() {
    private lateinit var transactionRecycler : RecyclerView
    private var offset : Long = 0
    private var threshold : Long = 5
    private var isLoading = false
    private var isEnd = false
    var linearLayoutManager = LinearLayoutManager(activity)
    var transactionIds : ArrayList<String> = arrayListOf()


    private fun loadMore() {
        Log.d("layout visible index ", linearLayoutManager.findLastCompletelyVisibleItemPosition().toString())
        Log.d("rest size ", transactionIds.size.toString())
        if (!isLoading && !isEnd && linearLayoutManager.findLastCompletelyVisibleItemPosition() == transactionIds.size-1) {
            isLoading = true
            var lastId = ""
            if(transactionIds.size > 0) lastId = transactionIds.get(transactionIds.size-1)
            var newRestaurants = runBlocking { ClassTransaction.getPastTransactionIds(threshold, offset, lastId) }
            if(newRestaurants.size > 0){
                Log.d("inf scroll", "exists")
                if(newRestaurants.size < threshold){
                    isEnd = true;
                }
                transactionIds.addAll(newRestaurants)
                transactionRecycler.adapter = TransactionItemAdapter(transactionIds, true)
                offset += threshold
            }
            Log.d("inf scroll", "load more new offset " + offset.toString())
            isLoading = false
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_past, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val transactionIds = runBlocking { ClassTransaction.getTransactionIds()}
        Log.d("transactionids", transactionIds.toString())

        transactionRecycler = view.findViewById(R.id.recyclerViewTransactions)
        transactionRecycler.layoutManager = LinearLayoutManager(activity)
        transactionRecycler.setHasFixedSize(true)
        transactionRecycler.adapter = TransactionItemAdapter(transactionIds, true)
        loadMore()
        transactionRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadMore()
            }
        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SellerFragmentPast.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SellerFragmentPast().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}