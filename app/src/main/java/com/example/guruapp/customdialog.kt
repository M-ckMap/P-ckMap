package com.example.guruapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.guruapp.databinding.DialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class customdialog(private val context: AppCompatActivity) :DialogFragment() {

    data class Mission(
        val id: String = "",
        val location: String = "",
        val mission: String = "",
        val isBookmarked: Boolean = false
    ) {
        fun toMap(): Map<String, Any> {
            return mapOf(
                "id" to id,
                "location" to location,
                "mission" to mission,
                "isBookmarked" to isBookmarked
            )
        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var missionButton: Button
    private lateinit var locationEdt: EditText
    private lateinit var missionEdt: EditText
    private lateinit var cancelButton: Button

    private var _binding: DialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        // Initialize views
        missionButton = view.findViewById(R.id.missionButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        locationEdt = view.findViewById(R.id.locationEdt)
        missionEdt = view.findViewById(R.id.missionEdt)

        // Set button click listeners
        binding.cancelButton.setOnClickListener {
            // Clear EditTexts
            locationEdt.text.clear()
            missionEdt.text.clear()
            dismiss()
        }
        binding.missionButton.setOnClickListener {
            addMissionToFirestore()
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "미션 등록에 실패하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "인증되지 않은 사용자입니다.", Toast.LENGTH_SHORT).show()
        }
    }


}