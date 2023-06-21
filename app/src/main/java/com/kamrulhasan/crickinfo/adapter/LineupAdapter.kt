package com.kamrulhasan.crickinfo.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.model.match.Lineup
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.utils.PLAYER_ID

private const val TAG = "LineupAdapter"

class LineupAdapter(
    private val playerData: List<Lineup>
) : RecyclerView.Adapter<LineupAdapter.LineupHolder>() {

    class LineupHolder(binding: View) : RecyclerView.ViewHolder(binding.rootView) {
        val playerImage: ImageView = binding.findViewById(R.id.iv_player)
        val playerName: TextView = binding.findViewById(R.id.tv_player_name)
        val playerType: TextView = binding.findViewById(R.id.tv_player_position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineupHolder {
        val view = LayoutInflater.from(MyApplication.appContext)
            .inflate(R.layout.squad_item, parent, false)
        return LineupHolder(view)
    }

    override fun onBindViewHolder(holder: LineupHolder, position: Int) {
        val playerItem = playerData[position]

        Log.d(TAG, "onBindViewHolder: ${playerItem.firstname}")

        holder.playerName.text =
            if (playerItem.lineup?.captain == true) {
                "${playerItem.fullname} (C)"
            } else {
                playerItem.fullname
            }

        holder.playerType.text = playerItem.position?.name
        Glide
            .with(holder.itemView.context)
            .load(playerItem.image_path)
            .fitCenter()
            .placeholder(R.drawable.icon_match)
            .into(holder.playerImage)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(PLAYER_ID, playerItem.id)
            holder.itemView.findNavController().navigate(R.id.playerDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${playerData.size}")
        return playerData.size
    }
}