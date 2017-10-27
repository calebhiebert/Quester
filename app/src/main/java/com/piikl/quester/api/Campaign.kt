package com.piikl.quester.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class Campaign {

    var id: Long = 0

    var name: String? = null

    @JsonIgnoreProperties("campaign")
    var quests: List<Quest>? = null

    @JsonIgnoreProperties("campaigns")
    var creator: User? = null
}