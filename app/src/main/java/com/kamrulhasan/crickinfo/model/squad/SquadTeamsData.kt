package com.kamrulhasan.crickinfo.model.squad

data class SquadTeamsData(
    val code: String?,
    val country_id: Int?,
    val id: Int,
    val image_path: String?,
    val name: String?,
    val national_team: Boolean?,
    val resource: String?,
    val squad: List<Squad>?,
    val updated_at: String?
)