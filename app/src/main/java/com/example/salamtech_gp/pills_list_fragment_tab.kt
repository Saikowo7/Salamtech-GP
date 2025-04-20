package com.example.salamtech_gp

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class pills_list_fragment_tab : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PillsAdapter
    private val pillList = mutableListOf<Pill>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        return inflater.inflate(R.layout.fragment_pills_list_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerViewPills)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PillsAdapter(pillList)
        recyclerView.adapter = adapter

        // Fetch pills from Firebase
        fetchPills()
    }

    private fun fetchPills() {
        val dbRef = FirebaseDatabase.getInstance().getReference("pills")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pillList.clear()
                for (pillSnap in snapshot.children) {
                    val pill = pillSnap.getValue(Pill::class.java)
                    if (pill != null) {
                        pillList.add(pill)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PillsFragment", "Error: ${error.message}")
            }
        })
    }
}
