package com.kamrulhasan.crickinfo.model.country

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_table")
data class CountryData(
    val continent_id: Int?,
    @PrimaryKey
    val id: Int,
    val image_path: String?,
    val name: String?,
    val resource: String?,
    val updated_at: String?
)