package com.piikl.quester.api

class SearchUser {
    var id: Long = 0

    var name: String = ""

    var password: String = ""

    var isPartOfCampaign: Boolean? = null

    var isOwnerOfCampaign: Boolean? = null

    var campaigns: List<Campaign>? = null
}