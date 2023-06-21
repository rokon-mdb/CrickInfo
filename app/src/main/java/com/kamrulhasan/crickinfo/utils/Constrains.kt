package com.kamrulhasan.crickinfo.utils

// one hour equal millis
const val oneHourMillis: Long = 3600000

const val MATCH_ID = "match_id"
const val PLAYER_ID = "player_id"
const val URL_KEY = "NEWS_URL_KEY"

const val VIEW_PAGER_COUNTER = 3

////  API url  \\\\
// Base api Link
const val BASE_URL = "https://cricket.sportmonks.com/api/v2.0/"
const val BASE_URL_NEWS = "https://newsapi.org/v2/"
const val DEFAULT_NEWS_PAGE = "https://www.cricbuzz.com/cricket-news/latest-news"

/// API Key
const val API_TOKEN = "dH8NTCnnxtGtW3ev2FSfqOaq4jcTWToNZ6r7AXW7RS8TWgekkd270L2jStLT"
const val API_TOKEN_LIVE = "hNf2oXFWaRWVINxXWzIZczPrbbH1db5WMoQ6osus2XhsK3Z5wI4D3Nsf8vTY"

const val API_KEY_NEWS = "2bd7895cc96e4a88bb0b58f85b4bca0d"

const val GET_CRICKET_NEWS_HOME =
    "everything?q=cricket&sortBy=publishedAt&pageSize=5&apiKey=$API_KEY_NEWS"
const val GET_CRICKET_NEWS =
    "everything?q=cricket&sortBy=publishedAt&pageSize=30&apiKey=$API_KEY_NEWS"

// specific api link
const val FIXTURES_END = "fixtures"
const val OFFICIALS_END = "officials"
const val COUNTRIES_END = "countries"
const val VENUES_END = "venues"
const val LEAGUES_END = "leagues"
const val TEAM_END = "teams"
const val PLAYER_END = "players"
const val LIVE_END = "livescores"

// Notification Channel constants
// Name of Notification Channel for notifications of background work
@JvmField
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"

@JvmField
val NOTIFICATION_TITLE: CharSequence = "Crick Info"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1


