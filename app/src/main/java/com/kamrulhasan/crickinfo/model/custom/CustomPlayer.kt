package com.kamrulhasan.crickinfo.model.custom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_table")
data class CustomPlayer(
    @PrimaryKey
    val id: Int,
    val image_path: String?,
    val name: String?,
    val country_id: Int?
    )