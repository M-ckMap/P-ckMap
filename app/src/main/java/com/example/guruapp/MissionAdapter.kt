package com.example.guruapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.guruapp.databinding.ItemBinding

// MissionAdapter.kt
class MissionAdapter(
    private val missions: List<MissionFragment.Mission>,
    private val onBookmarkClick: (MissionFragment.Mission) -> Unit
) : RecyclerView.Adapter<MissionAdapter.MissionViewHolder>() {

    inner class MissionViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mission: MissionFragment.Mission) {
            binding.location.text = mission.location
            binding.mission.text = mission.mission
            binding.bookmarkButton.setImageResource(
                if (mission.isBookmarked) R.drawable.baseline_check_box_24_filled
                else R.drawable.baseline_check_box_24
            )
            binding.bookmarkButton.setOnClickListener {
                Log.d("MissionAdapter", "Bookmark button clicked for mission ID: ${mission.id}")
                onBookmarkClick(mission)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(missions[position])
    }

    override fun getItemCount() = missions.size
}
