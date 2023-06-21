package com.kamrulhasan.crickinfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.model.match.Batting
import com.kamrulhasan.crickinfo.model.match.Lineup
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel

class BattingAdapter(
    private val batting: List<Batting>,
    private val battingLineup: List<Lineup>,
    private val viewModel: CrickInfoViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<BattingAdapter.BattingHolder>() {

    class BattingHolder(binding: View) : RecyclerView.ViewHolder(binding.rootView) {

        val playerName: TextView = binding.findViewById(R.id.tv_player_name)
        val runs: TextView = binding.findViewById(R.id.tv_runs)
        val balls: TextView = binding.findViewById(R.id.tv_balls)
        val fours: TextView = binding.findViewById(R.id.tv_four_s)
        val sixes: TextView = binding.findViewById(R.id.tv_six_s)
        val stickRate: TextView = binding.findViewById(R.id.tv_stick_rate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BattingHolder {
        val view = LayoutInflater.from(MyApplication.appContext)
            .inflate(R.layout.batting_item, parent, false)
        return BattingHolder(view)
    }

    override fun onBindViewHolder(holder: BattingHolder, position: Int) {
        val battingItem = batting[position]

        battingItem.player_id.let { it1 ->

            viewModel.getPlayerNameById(it1)
            var flag = false

            battingLineup.forEach {
                if (it1 == it.id) {
                    flag = true
                    holder.playerName.text = it.fullname
                }
            }

            if (!flag) {
                viewModel.readPlayerNameById(it1).observe(viewLifecycleOwner) { name ->
                    if (!name.isNullOrEmpty()) {
                        if (battingItem.active == true) {
                            val playerName = "$name*"
                            holder.playerName.text = playerName
                        } else {
                            holder.playerName.text = name
                        }
                    }
                }
            }
        }

        holder.runs.text = battingItem.score.toString()
        holder.balls.text = battingItem.ball.toString()
        holder.fours.text = battingItem.four_x.toString()
        holder.sixes.text = battingItem.six_x.toString()
        holder.stickRate.text = battingItem.rate.toString()

    }

    override fun getItemCount(): Int {
        return batting.size
    }
}
