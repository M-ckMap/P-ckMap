package com.example.guruapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guruapp.databinding.ActivitySearchBinding
import com.example.guruapp.databinding.ItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val adapter = RecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        // Handle Search Button click
        binding.searchBtn.setOnClickListener {
            val searchWord = binding.searchWord.text.toString()
            adapter.search(searchWord)
        }

        // Handle Home Button click
        binding.homeButton.setOnClickListener {
            onBackPressed()
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        private var missions: MutableList<MissionFragment.Mission> = mutableListOf()

        inner class ViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val missionData = missions[position]
            holder.binding.location.text = missionData.location
            holder.binding.mission.text = missionData.mission
        }

        override fun getItemCount(): Int {
            return missions.size
        }

        fun search(searchWord: String) {
            val user = FirebaseAuth.getInstance().currentUser
            val uid = user?.uid

            if (uid != null) {
                firestore.collection("missions").document(uid)
                    .collection("userMissions")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) {
                            Toast.makeText(this@SearchActivity, "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            return@addSnapshotListener
                        }

                        missions.clear()
                        for (document in querySnapshot!!) {
                            val missionData = document.toObject(MissionFragment.Mission::class.java)
                            if (missionData.location!!.contains(searchWord) || missionData.mission!!.contains(searchWord)) {
                                missions.add(missionData)
                            }
                        }
                        notifyDataSetChanged()
                    }
            } else {
                Toast.makeText(this@SearchActivity, "사용자가 인증되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
