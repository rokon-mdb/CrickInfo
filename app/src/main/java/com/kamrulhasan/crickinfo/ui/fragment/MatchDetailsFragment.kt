package com.kamrulhasan.crickinfo.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.adapter.BattingAdapter
import com.kamrulhasan.crickinfo.adapter.BowlingAdapter
import com.kamrulhasan.crickinfo.adapter.LineupAdapter
import com.kamrulhasan.crickinfo.model.fixture.FixturesData
import com.kamrulhasan.crickinfo.model.match.Batting
import com.kamrulhasan.crickinfo.model.match.Lineup
import com.kamrulhasan.crickinfo.model.match.Bowling
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel
import com.kamrulhasan.crickinfo.databinding.FragmentMatchDetailsBinding
import com.kamrulhasan.crickinfo.network.NetworkConnection
import com.kamrulhasan.crickinfo.utils.DateConverter
import com.kamrulhasan.crickinfo.utils.MATCH_ID
import com.kamrulhasan.crickinfo.utils.oneHourMillis

private const val TAG = "MatchDetailsFragment"

class MatchDetailsFragment : Fragment() {

    private var _binding: FragmentMatchDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CrickInfoViewModel
    private lateinit var viewOwner: LifecycleOwner

    private var match: FixturesData? = null
    private var localTeam = 0
    private var visitorTeam = 1
    private var team1Code = ""
    private var team2Code = ""
    private var firstInning = 0
    private var flagMom = false

    private val lineupLocal = mutableListOf<Lineup>()
    private val lineupVisitor = mutableListOf<Lineup>()

    private val battingTeam1 = mutableListOf<Batting>()
    private val bowlingTeam1 = mutableListOf<Bowling>()
    private val battingTeam2 = mutableListOf<Batting>()
    private val bowlingTeam2 = mutableListOf<Bowling>()

    private var team1Extra = 0
    private var team2Extra = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            match = it.getParcelable(MATCH_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMatchDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CrickInfoViewModel::class.java]
        viewOwner = viewLifecycleOwner

        sectionVisibility()

        match?.let { it ->

            localTeam = it.localteam_id
            visitorTeam = it.visitorteam_id

            firstInning =
                if ((localTeam == it.toss_won_team_id && it.elected == "batting") ||
                    (visitorTeam == it.toss_won_team_id && it.elected == "bowling")
                ) {
                    /// 1st innings local team batting
                    0

                } else {
                    /// 1st innings local team bowling
                    1
                }

            /*
            * ##############################
            *      Match Primary Info
            * ##############################
            * */

            // League
            it.league_id?.let { it1 ->
                viewModel.readLeaguesById(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvLeague.text = it
                    }
            }

            // read local team code
            viewModel.readTeamCode(it.localteam_id)
                .observe(viewLifecycleOwner) { it1 ->
                    if (it1 != null && it1 != "") {
                        binding.tvTeam1.text = it1
                        binding.tvLineupTeam1.text = it1
                        if (it.toss_won_team_id != null) {
                            team1Code = it1
                        }
                    }
                }

            // read visitor team code
            viewModel.readTeamCode(it.visitorteam_id)
                .observe(viewLifecycleOwner) { it1 ->
                    if (it1 != null && it1 != "") {
                        binding.tvTeam2.text = it1
                        binding.tvLineupTeam2.text = it1
                        if (it.toss_won_team_id != null) {
                            team2Code = it1
                        }
                    }
                }

            // read local team image url
            viewModel.readTeamUrl(it.localteam_id)
                .observe(viewLifecycleOwner) {
                    Glide
                        .with(requireContext())
                        .load(it)
                        .fitCenter()
                        .placeholder(R.drawable.icon_match)
                        .into(binding.ivTeam1)
                }

            // read visitor team image url
            viewModel.readTeamUrl(it.visitorteam_id)
                .observe(viewLifecycleOwner) {
                    Glide
                        .with(requireContext())
                        .load(it)
                        .fitCenter()
                        .placeholder(R.drawable.icon_match)
                        .into(binding.ivTeam2)
                }

            // run of local team
            viewModel.readScoreById(it.localteam_id, it.id)
                .observe(viewLifecycleOwner) {
                    binding.tvScoreTeam1.text = if (it != null) {
                        "$it/"
                    } else {
                        "-/"
                    }
                }

            // run of visitor team
            viewModel.readScoreById(it.visitorteam_id, it.id)
                .observe(viewLifecycleOwner) {
                    binding.tvScoreTeam2.text = if (it != null) {
                        "$it/"
                    } else {
                        "-/"
                    }
                }

            // wicket of local team
            viewModel.readWicketById(it.localteam_id, it.id)
                .observe(viewLifecycleOwner) {
                    binding.tvWicketTeam1.text = it?.toString() ?: "-"
                }

            // wicket of visitor team
            viewModel.readWicketById(it.visitorteam_id, it.id)
                .observe(viewLifecycleOwner) {
                    binding.tvWicketTeam2.text = it?.toString() ?: "-"
                }

            // over played local team
            viewModel.readOverById(it.localteam_id, it.id)
                .observe(viewLifecycleOwner) {
                    binding.tvOverTeam1.text = if (it != null) {
                        "($it)"
                    } else {
                        "(-)"
                    }
                }

            // over played visitor team
            viewModel.readOverById(it.visitorteam_id, it.id)
                .observe(viewLifecycleOwner) {
                    binding.tvOverTeam2.text = if (it != null) {
                        "($it)"
                    } else {
                        "(-)"
                    }
                }

            /*
            * ###################################
            *       Match Details Information
            * ###################################
            * */

            //binding.tvDate.text = it.starting_at?.let { DateConverter.zoneToDate(it) }
            binding.tvMatchDate.text = it.starting_at?.let {
                activity?.getString(R.string.info_show, DateConverter.zoneToDate(it))
            }
            binding.tvMatchTime.text = it.starting_at?.let {
                activity?.getString(R.string.info_show, DateConverter.zoneToTime(it))
            }
            // match notes
            binding.tvMatchNotes.text = if (it.note == "") {
                activity?.getString(R.string.info_show, "NA")
            } else {
                activity?.getString(R.string.info_show, it.note)
            }

            //Match Status
            binding.tvStatus.text = activity?.getString(R.string.info_show, it.status)

            if (it.status == "Finished") {
                binding.tvStatus.setTextColor(resources.getColor(R.color.red))
            } else if (it.status == "NS") {

                binding.tvStatus.text = activity?.getString(R.string.info_show, "Upcoming")
                binding.tvStatus.setTextColor(resources.getColor(R.color.green_dark))

                it.starting_at?.let {

                    val countdownDuration =
                        DateConverter.stringToDateLong(it) - DateConverter.todayDateToLong()

                    Log.d(TAG, "onBindViewHolder: ms: $countdownDuration")
                    val countMinute = countdownDuration.div(1000 * 60)

                    if (countMinute in 1..48 * 60) {
                        val countdownTimer = object : CountDownTimer(countdownDuration, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                // Update the countdown text view with the remaining time
                                var secondsRemaining = millisUntilFinished / 1000
                                var minute = secondsRemaining / 60
                                secondsRemaining %= 60
                                val hour = minute / 60
                                minute %= 60
                                binding.tvStatus.text =
                                    activity?.getString(R.string.info_show, "Upcoming")
                                binding.tvTimeCountDown.text = "$hour:$minute:$secondsRemaining"
                            }

                            override fun onFinish() {
                                // Update the countdown text view with the final message
                                binding.tvStatus.text = "Live"
                                binding.tvTimeCountDown.text = "Live"
                                viewModel.readUpcomingMatch()
                            }
                        }
                        countdownTimer.start()
                    } else if (countMinute in (-4 * oneHourMillis)..0) {
                        binding.tvTimeCountDown.text = "Live"
                    } else {
                        val day = (countdownDuration / oneHourMillis) / 24
                        if (day > 2) {
                            binding.tvDayCount.visibility = View.VISIBLE
                            binding.tvDayCount.text = "${day}days remaining"
                        } else {
                            binding.tvDayCount.visibility = View.GONE
                        }
                    }
                }
            }

            //round of match
            binding.tvRound.text = activity?.getString(R.string.info_show, it.round)
            // match toss winner elected
            binding.tvElected.text = if (it.elected == "" || it.elected.isNullOrEmpty()) {
                activity?.getString(R.string.info_show, "NA")
            } else {
                activity?.getString(R.string.info_show, it.elected)
            }

            it.venue_id?.let { it1 ->
                //venue
                viewModel.readVenuesNameById(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvVenues.text = activity?.getString(R.string.info_show, it)
                    }
                //venues city
                viewModel.readVenuesCityById(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvVenuesCity.text = activity?.getString(R.string.info_show, it)
                    }
            }

            //won team
            it.winner_team_id?.let { it1 ->
                viewModel.readTeamCode(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvMatchWonTeam.text = activity?.getString(R.string.info_show, it)
                    }
            }

            // Man of Match
            it.man_of_match_id?.let { player_id ->
                viewModel.readPlayerNameById(player_id).observe(viewLifecycleOwner) { it1 ->
                    Log.d(TAG, "onViewCreated: $it1")
                    if (!it1.isNullOrEmpty()) {
                        flagMom = true
                        binding.tvManOfTheMatch.text = activity?.getString(R.string.info_show, it1)
                    }
                }
            }

            //toss won team
            it.toss_won_team_id?.let { it1 ->
                viewModel.readTeamCode(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvTossWonTeam.text = activity?.getString(R.string.info_show, it)
                    }
            }

            /*
            * ##############################
            *         Match Umpire
            * ##############################
            * */
            /// match Umpire Info
            it.first_umpire_id?.let { it1 ->
                viewModel.readOfficialsById(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvFirstUmpire.text = activity?.getString(R.string.info_show, it)
                    }
            }

            it.second_umpire_id?.let { it1 ->
                viewModel.readOfficialsById(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvSecondUmpire.text = activity?.getString(R.string.info_show, it)
                    }
            }

            it.tv_umpire_id?.let { it1 ->
                viewModel.readOfficialsById(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvTvUmpire.text = activity?.getString(R.string.info_show, it)
                    }
            }

            it.referee_id?.let { it1 ->
                viewModel.readOfficialsById(it1)
                    .observe(viewLifecycleOwner) {
                        binding.tvReferee.text = activity?.getString(R.string.info_show, it)
                    }
            }

            /*
            * ##############################
            *         Match Details
            * ##############################
            * */

            //// Match details calling with network observe
            NetworkConnection().observe(viewLifecycleOwner) { network ->
                if (viewModel.matchDetails.value == null && network) {

                    viewModel.getMatchDetails(it.id)

                    // Man of Match
                    if (!flagMom) {
                        it.man_of_match_id?.let { it1 ->
                            viewModel.getPlayerNameById(it1)
                            viewModel.playerName.observe(viewLifecycleOwner) {
                                Log.d(TAG, "onViewCreated: $it")
                                binding.tvManOfTheMatch.text =
                                    activity?.getString(R.string.info_show, it)
                            }
                        }
                    }

                    // read team image url
                    viewModel.readTeamUrl(it.localteam_id)
                        .observe(viewLifecycleOwner) {
                            Glide
                                .with(requireContext())
                                .load(it)
                                .fitCenter()
                                .placeholder(R.drawable.icon_match)
                                .into(binding.ivTeam1)
                        }

                    viewModel.readTeamUrl(it.visitorteam_id)
                        .observe(viewLifecycleOwner) {
                            Glide
                                .with(requireContext())
                                .load(it)
                                .fitCenter()
                                .placeholder(R.drawable.icon_match)
                                .into(binding.ivTeam2)
                        }

                    viewModel.getMatchDetails(it.id)
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (team1Code.isNotEmpty() && team2Code.isNotEmpty()) {
                    binding.tvBattingTeam.text = team1Code
                    binding.tvBowlingTeam.text = team2Code
                } else {
                    binding.tvBattingTeam.text = activity?.getString(R.string.pending)
                    binding.tvBowlingTeam.text = activity?.getString(R.string.pending)
                }

            }, 1000)

            // match details
            viewModel.matchDetails.observe(viewLifecycleOwner) { it1 ->

                /*
                * #################################################
                *         Match Innings Data Calculation
                * #################################################
                * */

                battingTeam1.clear()
                battingTeam2.clear()
                bowlingTeam1.clear()
                bowlingTeam2.clear()

                /// make different batting list for two team
                it1?.batting?.forEach { batting ->
                    if (batting.team_id == localTeam) {
                        battingTeam1.add(batting)
                    } else {
                        battingTeam2.add(batting)
                    }
                }

                /////// calculating extra runs for two team

                it1?.scoreboards?.forEach { score ->
                    if (score.team_id == localTeam && score.type == "extra") {
                        team1Extra =
                            (score.wide ?: 0) + (score.noball_runs ?: 0) + (score.noball_balls ?: 0)
                        +(score.bye ?: 0) + (score.leg_bye ?: 0) + (score.penalty ?: 0)
                    } else if (score.team_id == visitorTeam && score.type == "extra") {
                        team2Extra =
                            (score.wide ?: 0) + (score.noball_runs ?: 0) + (score.noball_balls ?: 0)
                        +(score.bye ?: 0) + (score.leg_bye ?: 0) + (score.penalty ?: 0)
                    }
                }

                /// make different bowling list for two team
                it1?.bowling?.forEach { bowling ->
                    if (bowling.team_id == localTeam) {
                        bowlingTeam1.add(bowling)
                    } else {
                        bowlingTeam2.add(bowling)
                    }
                }

                /*
                * ##############################################
                *         Match First Inning Data showing
                * ##############################################
                * */

                if (firstInning == 0) {
                    localTeamBatting()
                } else {
                    visitorTeamBatting()
                }

                /*
                * ##############################
                *         Match Lineup
                * ##############################
                * */

                lineupLocal.clear()
                lineupVisitor.clear()
                Log.d(TAG, "onViewCreated: lineup: ${it1?.lineup?.size}")

                it1?.lineup?.forEach {
                    if (it.lineup?.team_id == localTeam) {
                        lineupLocal.add(it)
                    } else {
                        lineupVisitor.add(it)
                    }
                }
                // local team lineup
                if (lineupLocal.isNotEmpty()) {
                    binding.tvNa.visibility = View.GONE
                    binding.recyclerViewTeamSquad.adapter = LineupAdapter(lineupLocal)
                } else {
                    binding.tvNa.visibility = View.VISIBLE
                }
            }

            /*
            * ##############################
            *     scorecard for team1
            * ##############################
            * */

            binding.tvCardInning1.setOnClickListener {

                if (firstInning == 0) {
                    localTeamBatting()
                } else {
                    visitorTeamBatting()

                }

                /// change selected item view
                selectedItem(binding.tvCardInning1, binding.tvCardInning2)
            }

            /*
            * ##############################
            *     scorecard for team2
            * ##############################
            * */
            binding.tvCardInning2.setOnClickListener {

                if (firstInning == 0) {
                    visitorTeamBatting()
                } else {
                    localTeamBatting()
                }

                /// change selected item view
                selectedItem(binding.tvCardInning2, binding.tvCardInning1)
            }

            /*
            * ##############################
            *     Lineup for team1
            * ##############################
            * */
            binding.tvLineupTeam1.setOnClickListener {
                if (lineupLocal.isNotEmpty()) {
                    binding.tvNa.visibility = View.GONE
                    binding.recyclerViewTeamSquad.adapter = LineupAdapter(lineupLocal)
                } else {
                    binding.tvNa.visibility = View.VISIBLE
                }
                /// change selected item view
                selectedItem(binding.tvLineupTeam1, binding.tvLineupTeam2)
            }

            if (lineupLocal.isNotEmpty()) {
                binding.tvNa.visibility = View.GONE
            } else {
                binding.tvNa.visibility = View.VISIBLE
            }

            /*
            * ##############################
            *     Lineup for team2
            * ##############################
            * */
            binding.tvLineupTeam2.setOnClickListener {
                if (lineupLocal.isNotEmpty()) {
                    binding.tvNa.visibility = View.GONE
                    binding.recyclerViewTeamSquad.adapter = LineupAdapter(lineupVisitor)
                } else {
                    binding.tvNa.visibility = View.VISIBLE
                }
                /// change selected item view
                selectedItem(binding.tvLineupTeam2, binding.tvLineupTeam1)
            }
        }
    }

    /*
    * #######################################
    *    Function for change Selected View
    * #######################################
    * */
    private fun selectedItem(selected: TextView, nonSelected: TextView) {
        // text background change
        selected.setBackgroundResource(R.drawable.selected)
        nonSelected.setBackgroundResource(R.drawable.non_selected)

        // text color change
        selected.setTextColor(resources.getColor(R.color.white))
        nonSelected.setTextColor(resources.getColor(R.color.black))

        // text size change
        selected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        nonSelected.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    }

    /*
    * #########################################
    * second team bating and first team bowling
    * #########################################
    * */

    private fun visitorTeamBatting() {

        if (team1Code.isNotEmpty() && team2Code.isNotEmpty()) {
            binding.tvBattingTeam.text = team2Code
            binding.tvBowlingTeam.text = team1Code
        } else {
            binding.tvBattingTeam.text = resources.getString(R.string.pending)
            binding.tvBowlingTeam.text = resources.getString(R.string.pending)
        }

        binding.recyclerViewBattingScorecard.adapter =
            BattingAdapter(battingTeam2, lineupVisitor, viewModel, viewLifecycleOwner)

        val extra = "Extra: ( $team2Extra )"
        binding.tvTeamExtra.text = extra

        binding.recyclerViewBowlingScorecard.adapter =
            BowlingAdapter(bowlingTeam1, lineupLocal, viewModel, viewLifecycleOwner)
    }

    /*
    * #########################################
    * first team bating and second team bowling
    * #########################################
    * */
    private fun localTeamBatting() {
        if (team1Code.isNotEmpty() && team2Code.isNotEmpty()) {
            binding.tvBattingTeam.text = team1Code
            binding.tvBowlingTeam.text = team2Code
        } else {
            binding.tvBattingTeam.text = resources.getString(R.string.pending)
            binding.tvBowlingTeam.text = resources.getString(R.string.pending)
        }

        binding.recyclerViewBattingScorecard.adapter =
            BattingAdapter(battingTeam1, lineupVisitor, viewModel, viewLifecycleOwner)

        val extra = "Extra: ( $team1Extra )"
        binding.tvTeamExtra.text = extra

        binding.recyclerViewBowlingScorecard.adapter =
            BowlingAdapter(bowlingTeam2, lineupVisitor, viewModel, viewLifecycleOwner)
    }

    /*
    * #########################################
    *      visibility of selected section
    * #########################################
    * */
    private fun sectionVisibility() {
        /// default visibility
        binding.layoutMatchInfo.visibility = View.VISIBLE
        binding.layoutScorecard.visibility = View.GONE
        binding.layoutTeamSquad.visibility = View.GONE
        binding.layoutMatchUmpire.visibility = View.GONE

        binding.tvMatchInfo.setOnClickListener {
            binding.layoutMatchInfo.visibility = View.VISIBLE
            binding.layoutScorecard.visibility = View.GONE
            binding.layoutTeamSquad.visibility = View.GONE
            binding.layoutMatchUmpire.visibility = View.GONE
        }

        binding.tvTeamSquad.setOnClickListener {
            binding.layoutMatchInfo.visibility = View.GONE
            binding.layoutScorecard.visibility = View.GONE
            binding.layoutTeamSquad.visibility = View.VISIBLE
            binding.layoutMatchUmpire.visibility = View.GONE
        }

        binding.tvTeamScoreCard.setOnClickListener {
            binding.layoutMatchInfo.visibility = View.GONE
            binding.layoutScorecard.visibility = View.VISIBLE
            binding.layoutTeamSquad.visibility = View.GONE
            binding.layoutMatchUmpire.visibility = View.GONE
        }

        binding.tvMatchUmpire.setOnClickListener {
            binding.layoutMatchInfo.visibility = View.GONE
            binding.layoutScorecard.visibility = View.GONE
            binding.layoutTeamSquad.visibility = View.GONE
            binding.layoutMatchUmpire.visibility = View.VISIBLE
        }
    }
}