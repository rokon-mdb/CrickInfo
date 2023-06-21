package com.kamrulhasan.crickinfo.model.player

data class Team(
    val code: String,
    val country_id: Int,
    val id: Int,
    val image_path: String,
    val in_squad: InSquad,
    val name: String,
    val national_team: Boolean,
    val resource: String,
    val updated_at: String
)