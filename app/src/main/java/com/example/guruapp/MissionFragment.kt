package com.example.guruapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MissionFragment : Fragment() {

    data class Mission(
        val mission: String? = null,
        val location: String? = null
    )

    private lateinit var auth: FirebaseAuth
    private lateinit var missionButton: Button
    private lateinit var locationEdt: EditText
    private lateinit var missionEdt: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mission, container, false)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Initialize views
        missionButton = view.findViewById(R.id.missionButton)
        locationEdt = view.findViewById(R.id.locationEdt)
        missionEdt = view.findViewById(R.id.missionEdt)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set button click listener
        missionButton.setOnClickListener {
            addMissionToFirestore()
        }
    }

    private fun addMissionToFirestore() {
        // Access Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Get current user
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            // Create a new mission data map
            val missionData = hashMapOf(
                "location" to locationEdt.text.toString(),
                "mission" to missionEdt.text.toString()
            )

            // Add a new document with a generated ID to the "missions" collection
            db.collection("missions").document(uid)
                .collection("userMissions")
                .add(missionData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(context, "미션이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    // Clear EditTexts
                    locationEdt.text.clear()
                    missionEdt.text.clear()
                    // Hide keyboard
                    hideKeyboard()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "미션 등록에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "인증되지 않은 사용자입니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}