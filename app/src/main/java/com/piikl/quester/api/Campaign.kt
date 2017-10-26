package com.piikl.quester.api

class Campaign {

    var id: Long = 0

    var name: String = ""

    var quests: List<Quest>? = null

    var creator: User? = null
}