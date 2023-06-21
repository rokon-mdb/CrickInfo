package com.kamrulhasan.crickinfo.model.fixture

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "fixtures_data")
data class FixturesData(
    var draw_noresult: String?,
    var elected: String?,
    var first_umpire_id: Int?,
    var follow_on: Boolean?,
    @PrimaryKey
    var id: Int,
    @Ignore
    var last_period: @RawValue Any?,
    var league_id: Int?,
    var live: Boolean?,
    @Ignore
    var localteam_dl_data: @RawValue LocalteamDlData?,
    var localteam_id: Int,
    var man_of_match_id: Int?,
    var man_of_series_id: Int?,
    var note: String?,
    var referee_id: Int?,
    var resource: String?,
    var round: String?,
    var rpc_overs: String?,
    var rpc_target: String?,
    @Ignore
    var runs: @RawValue List<Run>?,
    var season_id: Int?,
    var second_umpire_id: Int?,
    var stage_id: Int?,
    var starting_at: String?,
    var status: String?,
    var super_over: Boolean?,
    var toss_won_team_id: Int?,
    var total_overs_played: Int?,
    var tv_umpire_id: Int?,
    var type: String?,
    var venue_id: Int?,
    @Ignore
    var visitorteam_dl_data: @RawValue VisitorteamDlData?,
    var visitorteam_id: Int,
    @Ignore
    var weather_report: @RawValue List<Any>?,
    var winner_team_id: Int?
):Parcelable{
    constructor(): this(
        draw_noresult= null,
        elected = null,
        first_umpire_id = null,
        follow_on = null,
        id = 0,
        last_period = null,
        league_id = null,
        live = null,
        localteam_dl_data = null,
        localteam_id = 0,
        man_of_match_id = null,
        man_of_series_id = null,
        note = null,
        referee_id = null,
        resource = null,
        round = null,
        rpc_overs = null,
        rpc_target = null,
        runs = null,
        season_id = null,
        second_umpire_id = null,
        stage_id = null,
        starting_at = null,
        status = null,
        super_over = null,
        toss_won_team_id = null,
        total_overs_played = null,
        tv_umpire_id = null,
        type = null,
        venue_id = null,
        visitorteam_dl_data = null,
        visitorteam_id = 0,
        weather_report = null,
        winner_team_id = null
    )
}