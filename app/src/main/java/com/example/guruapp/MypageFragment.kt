package com.example.guruapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MypageFragment : Fragment(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var textViewUserEmail: TextView
    private lateinit var buttonLogout: Button
    private lateinit var textViewDelete: TextView
    private lateinit var recyclerView: RecyclerView // Add RecyclerView reference here

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
        loadBookmarkedMissions()

        // Initialize views
        textViewUserEmail = view.findViewById(R.id.textviewUserEmail)
        buttonLogout = view.findViewById(R.id.buttonLogout)
        textViewDelete = view.findViewById(R.id.textviewDelete)
        recyclerView = view.findViewById(R.id.recyclerView) // Initialize RecyclerView

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if user is logged in, if not, navigate to LoginActivity
        if (firebaseAuth.currentUser == null) {
            // Navigate to LoginActivity if user is not logged in
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Close the current activity
        } else {
            // User is logged in, update UI
            val user = firebaseAuth.currentUser
            textViewUserEmail.text = "즐거운 여행 되세요, \n${user?.email} 님"
        }

        // Set click listeners
        buttonLogout.setOnClickListener(this)
        textViewDelete.setOnClickListener(this)

        // Load bookmarked missions
        loadBookmarkedMissions()

        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonLogout -> {
                firebaseAuth.signOut()
                // Navigate to LoginActivity
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // Close the current activity
            }
            R.id.textviewDelete -> {
                // Show confirmation dialog
                AlertDialog.Builder(requireContext())
                    .setMessage("정말 계정을 삭제 할까요?")
                    .setCancelable(false)
                    .setPositiveButton("확인") { _, _ ->
                        val user = firebaseAuth.currentUser
                        user?.delete()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show()
                                // Navigate to MainActivity
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish() // Close the current activity
                            } else {
                                Toast.makeText(requireContext(), "계정 삭제 실패.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .setNegativeButton("취소") { _, _ ->
                        Toast.makeText(requireContext(), "취소", Toast.LENGTH_LONG).show()
                    }
                    .show()
            }
        }
    }

    private fun loadBookmarkedMissions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val bookmarkRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            .collection("bookmarkedMissions")

        bookmarkRef.get().addOnSuccessListener { result ->
            val missions = result.map { document ->
                val data = document.data
                MissionFragment.Mission(
                    id = document.id,
                    location = data["location"] as String,
                    mission = data["mission"] as String,
                    isBookmarked = true
                )
            }
            recyclerView.adapter = MissionAdapter(missions) { /* Handle bookmark click */ }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }.addOnFailureListener { e ->
            Log.e("MypageFragment", "Error loading bookmarked missions", e)
        }
    }
}
