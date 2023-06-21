package com.kamrulhasan.crickinfo.model.fixture

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "run")
data class Run(
    var fixture_id: Int?,
    @PrimaryKey
    var id: Int,
    var inning: Int?,
    var overs: Double?,
    var pp1: String?,
    @Ignore
    var pp2: Any?,
    @Ignore
    var pp3: Any?,
    var resource: String?,
    var score: Int?,
    var team_id: Int?,
    var updated_at: String?,
    var wickets: Int?
){
    constructor(): this(
        fixture_id = null,
    id = 0,
    inning = null,
    overs = null,
    pp1 = null,
    pp2 = null,
    pp3 = null,
    resource = null,
    score = null,
    team_id = null,
    updated_at = null,
    wickets = null
    )
}