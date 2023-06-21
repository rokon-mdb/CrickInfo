package com.kamrulhasan.crickinfo.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.kamrulhasan.crickinfo.model.fixture.FixturesData
import com.kamrulhasan.crickinfo.utils.*
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel
import java.util.*

private const val TAG = "FixtureAdapter"

class FixtureAdapter(
    private val fixtureList: List<FixturesData>,
    private val viewModel: CrickInfoViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<FixtureAdapter.FixturesHolder>() {

    class FixturesHolder(binding: View) : RecyclerView.ViewHolder(binding.rootView) {
        val team1: TextView = binding.findViewById(R.id.tv_team1)
        val team2: TextView = binding.findViewById(R.id.tv_team2)
        val date: TextView = binding.findViewById(R.id.tv_date)
        val matchType: TextView = binding.findViewById(R.id.tv_format)
        val notes: TextView = binding.findViewById(R.id.tv_match_notes)
        val status: TextView = binding.findViewById(R.id.tv_match_status)
        val scoreTeam1: TextView = binding.findViewById(R.id.tv_score_team1)
        val scoreTeam2: TextView = binding.findViewById(R.id.tv_score_team2)
        val wicketTeam1: TextView = binding.findViewById(R.id.tv_wicket_team1)
        val wicketTeam2: TextView = binding.findViewById(R.id.tv_wicket_team2)
        val teamIcon1: ImageView = binding.findViewById(R.id.iv_team1)
        val teamIcon2: ImageView = binding.findViewById(R.id.iv_team2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixturesHolder {
        val view = LayoutInflater.from(MyApplication.appContext)
            .inflate(R.layout.match_item, parent, false)
        return FixturesHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FixturesHolder, position: Int) {
        val fixturesItem = fixtureList[position]

        holder.date.text = fixturesItem.starting_at?.let { DateConverter.zoneToDate(it) }
        holder.matchType.text = fixturesItem.round

        if (fixturesItem.status == "NS") {

            holder.status.text = "Upcoming"

            fixturesItem.starting_at?.let {

                var countdownDuration =
                    DateConverter.stringToDateLong(it) - DateConverter.todayDateToLong()
                countdownDuration -= 15 * 60 * 1000

                Log.d(TAG, "onBindViewHolder: ms: $countdownDuration")

                if ((countdownDuration / oneHourMillis) < 100) {
                    MyNotification.scheduleNotification(
                        DateConverter.stringToDateLong(it),
                        "A Cricket Match will Start in 15 Min"
                    )
                }

                // countdown section
                when (countdownDuration.div(1000 * 60)) {
                    in 0..(48 * 60) -> {

                        val countdownTimer = object : CountDownTimer(countdownDuration, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                // Update the countdown text view with the remaining time
                                var secondsRemaining = millisUntilFinished / 1000
                                var minute = secondsRemaining / 60
                                secondsRemaining %= 60
                                val hour = minute / 60
                                minute %= 60
                                holder.status.text = "Upcoming"
                                holder.notes.text = "$hour:$minute:$secondsRemaining"
                            }

                            override fun onFinish() {
                                // Update the countdown text view with the final message
                                holder.notes.text = ""
                                holder.status.text = "Live"
                                viewModel.readUpcomingMatch()

                                Handler(Looper.getMainLooper()).postDelayed({
                                    viewModel.getRecentMatches()
                                    viewModel.getUpcomingMatches()

                                }, 5 * oneHourMillis)
                            }
                        }
                        countdownTimer.start()
                    }
                    in (-4 * 60)..0 -> {
                        holder.notes.text = ""
                        holder.status.text = "Live"
                    }
                    else -> {
                        val day = (countdownDuration / oneHourMillis) / 24
                        if (day > 2) {
                            holder.notes.text = "${day}days remaining"
                            holder.status.text = "Upcoming"
                        } else {
                            holder.status.text = ""
                        }
                    }
                }
            }
        } else {
            holder.notes.text = fixturesItem.note
            // run
            viewModel.readScoreById(fixturesItem.localteam_id, fixturesItem.id)
                .observe(viewLifecycleOwner) {
                    holder.scoreTeam1.text = if (it != null) {
                        "$it/"
                    } else {
                        "-/"
                    }
                }

            viewModel.readScoreById(fixturesItem.visitorteam_id, fixturesItem.id)
                .observe(viewLifecycleOwner) {
                    holder.scoreTeam2.text = if (it != null) {
                        "$it/"
                    } else {
                        "-/"
                    }
                }
            // wicket
            viewModel.readWicketById(fixturesItem.localteam_id, fixturesItem.id)
                .observe(viewLifecycleOwner) {
                    holder.wicketTeam1.text = it?.toString() ?: "-"
                }

            viewModel.readWicketById(fixturesItem.visitorteam_id, fixturesItem.id)
                .observe(viewLifecycleOwner) {
                    holder.wicketTeam2.text = it?.toString() ?: "-"
                }
        }

        viewModel.readTeamCode(fixturesItem.localteam_id)
            .observe(viewLifecycleOwner) {
                holder.team1.text = it
            }

        viewModel.readTeamUrl(fixturesItem.localteam_id)
            .observe(viewLifecycleOwner) {
                Glide
                    .with(holder.itemView.context)
                    .load(it)
                    .fitCenter()
                    .placeholder(R.drawable.icon_match)
                    .into(holder.teamIcon1)
            }

        viewModel.readTeamCode(fixturesItem.visitorteam_id)
            .observe(viewLifecycleOwner) {
                holder.team2.text = it
            }

        viewModel.readTeamUrl(fixturesItem.visitorteam_id)
            .observe(viewLifecycleOwner) {
                Glide
                    .with(holder.itemView.context)
                    .load(it)
                    .centerCrop()
                    .placeholder(R.drawable.icon_match)
                    .into(holder.teamIcon2)
            }

        // navigate to details
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(MATCH_ID, fixturesItem)
            holder.itemView.findNavController().navigate(R.id.matchDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return fixtureList.size
    }
}