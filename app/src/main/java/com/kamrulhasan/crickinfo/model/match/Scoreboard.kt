package com.kamrulhasan.crickinfo.model.match

data class Scoreboard(
    val bye: Int?,
    val fixture_id: Int?,
    val id: Int,
    val leg_bye: Int?,
    val noball_balls: Int?,
    val noball_runs: Int?,
    val overs: Double?,
    val penalty: Int?,
    val resource: String?,
    val scoreboard: String?,
    val team_id: Int?,
    val total: Int?,
    val type: String?,
    val updated_at: String?,
    val wickets: Int?,
    val wide: Int?
)