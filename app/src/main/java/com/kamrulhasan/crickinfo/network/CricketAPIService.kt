package com.kamrulhasan.crickinfo.network

import com.kamrulhasan.crickinfo.model.country.Country
import com.kamrulhasan.crickinfo.model.news.News
import com.kamrulhasan.crickinfo.model.fixture.Fixtures
import com.kamrulhasan.crickinfo.model.leagues.Leagues
import com.kamrulhasan.crickinfo.model.match.Match
import com.kamrulhasan.crickinfo.model.officials.Officials
import com.kamrulhasan.crickinfo.model.player.Player
import com.kamrulhasan.crickinfo.model.player.Players
import com.kamrulhasan.crickinfo.model.squad.SquadTeams
import com.kamrulhasan.crickinfo.model.team.Teams
import com.kamrulhasan.crickinfo.model.venues.Venues
import com.kamrulhasan.crickinfo.utils.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

private val retrofit_news = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL_NEWS)
    .build()

interface CricketAPIService {

    @GET(FIXTURES_END)
    suspend fun getFixturesByDate(
        @Query("filter[starts_between]") dateRange: String,
        @Query("include") include: String = "runs",
        @Query("api_token") api_token: String = API_TOKEN
    ): Fixtures

    @GET(LIVE_END)
    suspend fun getLiveMatches(
        @Query("include") include: String = "runs",
        @Query("api_token") api_token: String = API_TOKEN_LIVE
    ): Call<Fixtures>

    @GET("$FIXTURES_END/{fixtures_id}")
    fun getMatchDetails(
        @Path("fixtures_id") fixtures_id: Int,
        @Query("include") include: String = "scoreboards,batting,bowling,lineup",
        @Query("api_token") api_token: String = API_TOKEN
    ): Call<Match>

    //get players
    @GET("$PLAYER_END/{player_id}")
    fun getPlayerById(
        @Path("player_id") player_id: Int,
        @Query("include") include: String = "career,teams",
        @Query("api_token") api_token: String = API_TOKEN
    ): Call<Players>

    //get players
    @GET("$PLAYER_END/{player_id}")
    fun getPlayerNameById(
        @Path("player_id") player_id: Int,
        @Query("api_token") api_token: String = API_TOKEN
    ): Call<Player>

    //// get squad
    @GET("$TEAM_END/{team_id}/squad/23")
    fun getPlayersByTeam(
        @Path("team_id") team_id: Int,
        @Query("api_token") api_token: String = API_TOKEN
    ): Call<SquadTeams>

    /// get teams
    @GET(TEAM_END)
    suspend fun getTeams(
        @Query("api_token") api_token: String = API_TOKEN
    ): Teams

    @GET(LEAGUES_END)
    suspend fun getLeagues(
        @Query("api_token") api_token: String = API_TOKEN
    ): Leagues

    @GET(OFFICIALS_END)
    suspend fun getOfficials(
        @Query("api_token") api_token: String = API_TOKEN
    ): Officials

    @GET(COUNTRIES_END)
    suspend fun getCountries(
        @Query("api_token") api_token: String = API_TOKEN
    ): Country

    @GET(VENUES_END)
    suspend fun getVenues(
        @Query("api_token") api_token: String = API_TOKEN
    ): Venues

    @GET(GET_CRICKET_NEWS)
    fun getCricketNews(): Call<News>

    @GET(GET_CRICKET_NEWS_HOME)
    fun getCricketNewsHome(): Call<News>
}

object CricketApi {
    val retrofitService: CricketAPIService by lazy { retrofit.create(CricketAPIService::class.java) }
    val news_retrofitService: CricketAPIService by lazy { retrofit_news.create(CricketAPIService::class.java) }
}
