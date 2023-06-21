package com.kamrulhasan.crickinfo.repository

import androidx.lifecycle.LiveData
import com.kamrulhasan.crickinfo.database.CricketDao
import com.kamrulhasan.crickinfo.model.country.CountryData
import com.kamrulhasan.crickinfo.model.custom.CustomPlayer
import com.kamrulhasan.crickinfo.model.fixture.FixturesData
import com.kamrulhasan.crickinfo.model.fixture.Run
import com.kamrulhasan.crickinfo.model.leagues.LeaguesData
import com.kamrulhasan.crickinfo.model.officials.OfficialsData
import com.kamrulhasan.crickinfo.model.team.TeamsData
import com.kamrulhasan.crickinfo.model.venues.VenuesData

class CrickInfoRepository(private val cricketDao: CricketDao) {

    val readAllPlayers: LiveData<List<CustomPlayer>?> = cricketDao.readAllPlayers()

    fun readUpcomingFixtures(todayDate: String, lastDate: String): LiveData<List<FixturesData>?> {
        return cricketDao.readUpcomingFixtures(todayDate, lastDate)
    }

    // read recent matches
    fun readRecentFixtures(todayDate: String, passedDate: String): LiveData<List<FixturesData>?> {
        return cricketDao.readRecentFixtures(todayDate, passedDate)
    }

    // read recent matches short list
    fun readUpcomingShort(
        todayDate: String,
        passedDate: String,
        limit: Int
    ): LiveData<List<FixturesData>?> {
        return cricketDao.readUpcomingFixturesSort(todayDate, passedDate, limit)
    }

    suspend fun addFixturesData(fixturesData: FixturesData) {
        cricketDao.addFixtures(fixturesData)
    }

    suspend fun addRun(run: Run) {
        cricketDao.addRun(run)
    }

    // read team info

    fun readTeamCodeById(id: Int): LiveData<String> {
        return cricketDao.readTeamCodeById(id)
    }

    fun readTeamIconById(id: Int): LiveData<String> {
        return cricketDao.readTeamIconById(id)
    }

    fun readPlayerNameById(id: Int): LiveData<String> {
        return cricketDao.readPlayerNameById(id)
    }

    fun readPlayerImageUrlById(id: Int): LiveData<String> {
        return cricketDao.readPlayerImageUrlById(id)
    }

    fun readPlayerCountryById(id: Int): LiveData<Int> {
        return cricketDao.readPlayerCountryById(id)
    }

    // get run
    fun readTeamScoreById(team_id: Int, fixture_id: Int): LiveData<Int> {
        return cricketDao.readTeamScoreById(team_id, fixture_id)
    }

    fun readTeamWicketById(team_id: Int, fixture_id: Int): LiveData<Int> {
        return cricketDao.readTeamWicketById(team_id, fixture_id)
    }

    fun readTeamOverById(team_id: Int, fixture_id: Int): LiveData<Double> {
        return cricketDao.readTeamOverById(team_id, fixture_id)
    }

    fun readOfficialsById(id: Int): LiveData<String> {
        return cricketDao.readUmpireNameById(id)
    }

    fun readLeaguesById(id: Int): LiveData<String> {
        return cricketDao.readLeaguesById(id)
    }

    //country name by id
    fun readCountryById(id: Int): LiveData<String> {
        return cricketDao.readCountryById(id)
    }

    //venues name by id
    fun readVenuesNameById(id: Int): LiveData<String> {
        return cricketDao.readVenuesNameById(id)
    }

    //venues city by id
    fun readVenuesCityById(id: Int): LiveData<String> {
        return cricketDao.readVenuesCityById(id)
    }

    suspend fun addTeams(teamsData: TeamsData) {
        cricketDao.addTeam(teamsData)
    }

    suspend fun addOfficials(officialsData: OfficialsData) {
        cricketDao.addOfficials(officialsData)
    }

    suspend fun addLeagues(leaguesData: LeaguesData) {
        cricketDao.addLeagues(leaguesData)
    }

    suspend fun addVenues(venuesData: VenuesData) {
        cricketDao.addVenues(venuesData)
    }

    suspend fun addCountries(countryData: CountryData) {
        cricketDao.addCountries(countryData)
    }

    suspend fun addPlayer(player: CustomPlayer) {
        cricketDao.addPlayer(player)
    }

    suspend fun deleteFixtures(date: String) {
        cricketDao.deleteOldFixtures(date)
    }
}