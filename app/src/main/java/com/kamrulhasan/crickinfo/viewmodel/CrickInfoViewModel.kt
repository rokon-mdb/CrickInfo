package com.kamrulhasan.crickinfo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kamrulhasan.crickinfo.database.CrickInfoDatabase
import com.kamrulhasan.crickinfo.model.country.CountryData
import com.kamrulhasan.crickinfo.model.custom.CustomPlayer
import com.kamrulhasan.crickinfo.model.news.Article
import com.kamrulhasan.crickinfo.model.fixture.FixturesData
import com.kamrulhasan.crickinfo.model.leagues.LeaguesData
import com.kamrulhasan.crickinfo.model.match.MatchData
import com.kamrulhasan.crickinfo.model.officials.OfficialsData
import com.kamrulhasan.crickinfo.model.player.PlayersData
import com.kamrulhasan.crickinfo.model.team.TeamsData
import com.kamrulhasan.crickinfo.model.venues.VenuesData
import com.kamrulhasan.crickinfo.network.CricketApi
import com.kamrulhasan.crickinfo.repository.CrickInfoRepository
import com.kamrulhasan.crickinfo.utils.DateConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await

private const val TAG = "CrickInfoViewModel"

class CrickInfoViewModel(application: Application) : AndroidViewModel(application) {

    var upcomingMatch: LiveData<List<FixturesData>?>
    var recentMatch: LiveData<List<FixturesData>?>
    var shortList: LiveData<List<FixturesData>?>
    var playerList: LiveData<List<CustomPlayer>?>

    private var _news: MutableLiveData<List<Article>?> = MutableLiveData<List<Article>?>()
    val news: LiveData<List<Article>?> = _news

    private var _matchDetails: MutableLiveData<MatchData?> = MutableLiveData<MatchData?>()
    val matchDetails: LiveData<MatchData?> = _matchDetails

    private var _liveMatches: MutableLiveData<List<FixturesData>?> =
        MutableLiveData<List<FixturesData>?>()
    val liveMatches: LiveData<List<FixturesData>?> = _liveMatches

    private var _player = MutableLiveData<PlayersData?>()
    val player: LiveData<PlayersData?> = _player

    private var _playerName = MutableLiveData<String?>()
    val playerName: LiveData<String?> = _playerName

    private val repository: CrickInfoRepository

    init {

        repository = CrickInfoRepository(
            CrickInfoDatabase.getDatabase(application)
                .cricketDao()
        )

        recentMatch = repository.readRecentFixtures(
            DateConverter.todayDateForRecentTimeZone(),
            DateConverter.passedTwoMonth()
        )

        shortList = repository.readUpcomingShort(
            DateConverter.customDateForLiveTimeZone("-05"),
            DateConverter.upcomingTwoWeek(),
            3
        )

        upcomingMatch = repository.readUpcomingFixtures(
            DateConverter.todayDateWithTimeZone(),
            DateConverter.upcomingTwoMonth()
        )

        playerList = repository.readAllPlayers
    }

    //// Api call from main activity
    fun apiCallOnce() {
        getUpcomingMatches()
        getRecentMatches()
        getLeaguesData()
        getOfficialsData()
        getTeamsData()
        getCountries()
        getVenues()
        deleteFixtures(DateConverter.passedTwoMonth())
    }

    /// Player
    /// read all players
    fun readAllPlayers() {
        playerList = repository.readAllPlayers
    }

    // player name
    fun readPlayerNameById(id: Int): LiveData<String> {
        return repository.readPlayerNameById(id)
    }

    fun readPlayerCountryById(id: Int): LiveData<Int> {
        return repository.readPlayerCountryById(id)
    }

    fun readPlayerImageUrlById(id: Int): LiveData<String> {
        return repository.readPlayerImageUrlById(id)
    }

    /// Team
    ///  read team code
    fun readTeamCode(id: Int): LiveData<String> {
        return repository.readTeamCodeById(id)
    }

    // team url string
    fun readTeamUrl(id: Int): LiveData<String> {
        return repository.readTeamIconById(id)
    }

    /// Runs
    /// read run
    fun readScoreById(team_id: Int, fixture_id: Int): LiveData<Int> {
        return repository.readTeamScoreById(team_id, fixture_id)
    }

    fun readWicketById(team_id: Int, fixture_id: Int): LiveData<Int> {
        return repository.readTeamWicketById(team_id, fixture_id)
    }

    fun readOverById(team_id: Int, fixture_id: Int): LiveData<Double> {
        return repository.readTeamOverById(team_id, fixture_id)
    }

    /// Officials
    // Officials Name
    fun readOfficialsById(id: Int): LiveData<String> {
        return repository.readOfficialsById(id)
    }

    /// Leagues
    // Leagues Name
    fun readLeaguesById(id: Int): LiveData<String> {
        return repository.readLeaguesById(id)
    }

    /// Country
    // Country Name
    fun readCountryById(id: Int): LiveData<String> {
        return repository.readCountryById(id)
    }

    //// Venues
    // Venues Name
    fun readVenuesNameById(id: Int): LiveData<String> {
        return repository.readVenuesNameById(id)
    }

    // Venues City
    fun readVenuesCityById(id: Int): LiveData<String> {
        return repository.readVenuesCityById(id)
    }

    //// Fixtures
    // read recent matches short list
    fun readUpcomingMatchShortList(limit: Int) {
        shortList = repository.readUpcomingShort(
            DateConverter.todayDateWithTimeZone(),
            DateConverter.upcomingTwoMonth(),
            limit
        )
        Log.d(TAG, "readRecentMatchShortList: ${shortList.value?.size}")
    }

    /// read upcoming matches
    fun readUpcomingMatch() {
        upcomingMatch = repository.readUpcomingFixtures(
            DateConverter.todayDateWithTimeZone(),
            DateConverter.upcomingTwoMonth()
        )
    }

    private fun readRecentMatch() {
        try {
            upcomingMatch = repository.readRecentFixtures(
                DateConverter.todayDateForRecentTimeZone(),
                DateConverter.passedTwoMonth()
            )

        } catch (e: Exception) {
            Log.e(TAG, "readRecentMatches: $e")
        }
    }

    /*
    * ###################################################
    *                Fixtures Api Call
    * ####################################################
    */
    // get upcoming match from api
    fun getUpcomingMatches() {
        val firstDate = DateConverter.todayDateWithTimeZone()
        val lastDate = DateConverter.upcomingTwoMonth()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateRange = "$firstDate,$lastDate"
                val upcomingTemp = CricketApi.retrofitService.getFixturesByDate(dateRange).data
                Log.d(TAG, "getUpcomingMatches: ${upcomingTemp?.size}")

                upcomingTemp?.forEach {
                    repository.addFixturesData(it)
                }
            } catch (e: Exception) {
                Log.e(TAG, "getUpcomingMatches: $e")
            }
            readUpcomingMatch()
        }
    }

    // get recent matches from api
    fun getRecentMatches() {
        val todayDate = DateConverter.todayDateForRecentTimeZone()
        val passedDate = DateConverter.passedTwoMonth()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateRange = "$passedDate,$todayDate"
                val recentTemp = CricketApi.retrofitService.getFixturesByDate(dateRange).data
                Log.d(TAG, "getRecentMatches: ${recentTemp?.size}")

                recentTemp?.forEach {
                    repository.addFixturesData(it)

                    val runs = it.runs
                    if (runs != null && runs.isNotEmpty()) {
                        if (runs.size == 2) {
                            repository.addRun(runs[0])
                            repository.addRun(runs[1])
                        } else if (runs.size == 1) {
                            repository.addRun(runs[0])
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getRecentMatches: $e")
            }
            readRecentMatch()
        }
    }

    // get match details
    fun getMatchDetails(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val matchTemp = CricketApi.retrofitService.getMatchDetails(id).await()
                _matchDetails.postValue(matchTemp.data)
                Log.d(TAG, "getMatchDetails: ${matchTemp.data}")
            } catch (e: Exception) {
                Log.e(TAG, "getMatchDetails: $e")
            }
        }
    }

    // delete match details from database
    private fun deleteFixtures(date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteFixtures(date)
            } catch (e: Exception) {
                Log.e(TAG, "getMatchDetails: $e")
            }
        }
    }

    /*
    * ###################################################
    *                Players Api Call
    * ####################################################
    */
    // ok///get [player] by id
    fun getPlayerById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val playerTemp = CricketApi.retrofitService.getPlayerById(id).await()
                Log.d(TAG, "getPlayerById: MOM: $playerTemp")
                if (playerTemp.data != null) {
                    _player.postValue(playerTemp.data)
                    val temp = playerTemp.data
                    val player = CustomPlayer(
                        temp.id,
                        temp.image_path,
                        temp.fullname,
                        temp.country_id
                    )
                    repository.addPlayer(player)
                } else {
                    _player.value = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "getPlayers: $e")
            }
        }
    }

    // get [player] by id
    fun getPlayerNameById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val playerTemp = CricketApi.retrofitService.getPlayerNameById(id).await()
                Log.d(TAG, "getPlayerById: $playerTemp")
                if (playerTemp.data != null) {
                    _playerName.postValue(playerTemp.data.fullname)
                    val temp = playerTemp.data
                    val player = CustomPlayer(
                        temp.id,
                        temp.image_path,
                        temp.fullname,
                        temp.country_id,
                    )
                    repository.addPlayer(player)
                } else {
                    _playerName.value = "NA"
                }
            } catch (e: Exception) {
                Log.e(TAG, "getPlayer: $e")
            }
        }
    }

    //Team Squad
    private fun getSquadByTeamId(team_id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "getSquadByTeamId: before")
                val playersCall = CricketApi.retrofitService.getPlayersByTeam(team_id).await()
                val players = playersCall.data?.squad
                Log.d(TAG, "getSquadByTeamId: squad: $players")

                players?.forEach {
                    val player = CustomPlayer(
                        it.id,
                        it.image_path,
                        it.fullname,
                        it.country_id
                    )
                    repository.addPlayer(player)
                }

            } catch (e: Exception) {
                Log.e(TAG, "getTeamSquad: $e")
            }
        }
    }

    /*
    * ###################################################
    *                Teams Api Call
    * ####################################################
    */
    // add team data into database
    private fun getTeamsData() {

        viewModelScope.launch(Dispatchers.IO) {
            var teamsList: List<TeamsData> = listOf()
            try {
                teamsList = CricketApi.retrofitService.getTeams().data
            } catch (e: Exception) {
                Log.e(TAG, "getTeamsData: $e")
            }

            teamsList.forEach {
                repository.addTeams(it)
                // add team squad
                if (playerList.value.isNullOrEmpty()) {
                    getSquadByTeamId(it.id)
                    Log.d(TAG, "getTeamsData: Team squad")
                }
            }
        }
    }

    /*
    * ###################################################
    *                Officials Api Call
    * ####################################################
    */
    // add officials data into database
    private fun getOfficialsData() {

        viewModelScope.launch(Dispatchers.IO) {
            var officialsList: List<OfficialsData>? = listOf()
            try {
                officialsList = CricketApi.retrofitService.getOfficials().data
            } catch (e: Exception) {
                Log.e(TAG, "getTeamsData: $e")
            }
            officialsList?.forEach {
                repository.addOfficials(it)
            }
        }
    }

    /*
    * ###################################################
    *                Leagues Api Call
    * ####################################################
    */
    // add Leagues data into database
    private fun getLeaguesData() {
        viewModelScope.launch(Dispatchers.IO) {
            var leaguesList: List<LeaguesData>? = listOf()
            try {
                leaguesList = CricketApi.retrofitService.getLeagues().data
            } catch (e: Exception) {
                Log.e(TAG, "getTeamsData: $e")
            }

            /// add in database
            leaguesList?.forEach {
                repository.addLeagues(it)
            }
        }
    }

    /*
    * ###################################################
    *               Country Api Call
    * ####################################################
    */
    private fun getCountries() {
        viewModelScope.launch(Dispatchers.IO) {
            var countriesList: List<CountryData>? = listOf()
            try {
                countriesList = CricketApi.retrofitService.getCountries().data
            } catch (e: Exception) {
                Log.e(TAG, "getTeamsData: $e")
            }
            countriesList?.forEach {
                repository.addCountries(it)
            }
        }
    }

    /*
    * ###################################################
    *                Venues Api Call
    * ####################################################
    */
    private fun getVenues() {
        viewModelScope.launch(Dispatchers.IO) {
            var seasonsList: List<VenuesData>? = listOf()
            try {
                seasonsList = CricketApi.retrofitService.getVenues().data
            } catch (e: Exception) {
                Log.e(TAG, "getTeamsData: $e")
            }

            // add Venues data into database
            seasonsList?.forEach {
                repository.addVenues(it)
            }
        }
    }

    /*
    * #######################################################
    *                Cricket News Api Call
    * #######################################################
    */
    // get all cricket news
    fun getNewsArticle() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val articles = CricketApi.news_retrofitService.getCricketNews().await().articles
                _news.postValue(articles)

            } catch (e: Exception) {
                Log.e("TAG", "getNewsArticle: $e")
            }
        }
    }

    // get top 5 cricket news
    fun getNewsArticleHome() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val articles = CricketApi.news_retrofitService.getCricketNewsHome().await().articles
                _news.postValue(articles)

            } catch (e: Exception) {
                Log.e("TAG", "getNewsArticleHome: $e")
            }
        }
    }

    /*
    * ###################################################
    *                Live match Api Call
    * ####################################################
    */
    // get live matches
    fun getLiveMatches() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val matches = CricketApi.news_retrofitService.getLiveMatches().await()
                _liveMatches.postValue(matches.data)

            } catch (e: Exception) {
                Log.e("TAG", "getNewsArticle: $e")
            }
        }
    }
}