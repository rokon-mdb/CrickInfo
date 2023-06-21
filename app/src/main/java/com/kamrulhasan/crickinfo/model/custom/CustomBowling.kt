package com.kamrulhasan.crickinfo.model.custom

data class CustomBowling(
    val matches: Int,
    val runs: Int,
    val wicket: Int,
    val economy: Double,
    val avg: Double,
    val stickRate: Double,
    val fiveWicket: Int
)
