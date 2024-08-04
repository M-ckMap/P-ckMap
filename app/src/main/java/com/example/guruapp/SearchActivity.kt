package com.example.guruapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guruapp.databinding.ActivitySearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val missions: MutableList<MissionFragment.Mission> = mutableListOf()
    private val adapter = MissionAdapter(missions) { mission ->
        toggleBookmark(mission)
    }

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
            search(searchWord)
        }

        // Handle Home Button click
        binding.homeButton.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun search(searchWord: String) {
        firestore.collectionGroup("userMissions")
            .get()
            .addOnSuccessListener { querySnapshot ->
                missions.clear()
                for (document in querySnapshot.documents) {
                    val missionData = document.toObject(MissionFragment.Mission::class.java)
                    if (missionData != null &&
                        (missionData.location!!.contains(searchWord) || missionData.mission!!.contains(
                            searchWord
                        ))
                    ) {
                        missions.add(missionData)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleBookmark(mission: MissionFragment.Mission) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        val bookmarkRef = firestore.collection("users").document(userId)
            .collection("bookmarkedMissions").document(mission.id)

        bookmarkRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Remove bookmark
                bookmarkRef.delete().addOnSuccessListener {
                    Log.d("SearchActivity", "Bookmark removed for mission ID: ${mission.id}")
                    Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show()
                    // Notify MypageFragment to update RecyclerView
                    notifyBookmarkChange()
                }.addOnFailureListener { e ->
                    Log.e("SearchActivity", "Error removing bookmark", e)
                    Toast.makeText(this, "Error removing bookmark", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Add bookmark
                bookmarkRef.set(mission.toMap()).addOnSuccessListener {
                    Log.d("SearchActivity", "Bookmark added for mission ID: ${mission.id}")
                    Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
                    // Notify MypageFragment to update RecyclerView
                    notifyBookmarkChange()
                }.addOnFailureListener { e ->
                    Log.e("SearchActivity", "Error adding bookmark", e)
                    Toast.makeText(this, "Error adding bookmark", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            Log.e("SearchActivity", "Error checking bookmark status", e)
            Toast.makeText(this, "Error checking bookmark status", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notifyBookmarkChange() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("BOOKMARK_CHANGE"))
    }
}