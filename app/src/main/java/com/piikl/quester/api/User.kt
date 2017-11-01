package com.piikl.quester.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class User {

    var id: Long = 0

    var name: String = ""

    var password: String = ""

    @JsonIgnoreProperties("creator")
    var campaigns: List<Campaign>? = null
}
