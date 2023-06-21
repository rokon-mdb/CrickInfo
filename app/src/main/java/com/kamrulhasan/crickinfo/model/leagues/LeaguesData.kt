package com.kamrulhasan.crickinfo.model.leagues

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leagues_table")
data class LeaguesData(
    val code: String?,
    val country_id: Int?,
    @PrimaryKey
    val id: Int,
    val image_path: String?,
    val name: String?,
    val resource: String?,
    val season_id: Int?,
    val type: String?,
    val updated_at: String?
)