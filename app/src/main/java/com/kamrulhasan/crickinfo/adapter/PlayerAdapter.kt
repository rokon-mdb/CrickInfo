package com.kamrulhasan.crickinfo.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.model.custom.CustomPlayer
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.utils.PLAYER_ID
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel

class PlayerAdapter(
    private val player: List<CustomPlayer>,
    private val viewModel: CrickInfoViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PlayerAdapter.PlayerHolder>() {

    class PlayerHolder(binding: View) : RecyclerView.ViewHolder(binding.rootView) {
        val playerImage: ImageView = binding.findViewById(R.id.iv_player)
        val playerName: TextView = binding.findViewById(R.id.tv_player_name)
        val playerType: TextView = binding.findViewById(R.id.tv_player_position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val view = LayoutInflater.from(MyApplication.appContext)
            .inflate(R.layout.squad_item, parent, false)
        return PlayerHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerHolder, position: Int) {
        val playerItem = player[position]

        holder.playerImage.setImageResource(R.drawable.icon_image_24)
        holder.playerName.text = playerItem.name

        playerItem.country_id?.let {
            viewModel.readCountryById(it).observe(viewLifecycleOwner) { country ->
                holder.playerType.text = country
            }
        }
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
        return player.size
    }
}