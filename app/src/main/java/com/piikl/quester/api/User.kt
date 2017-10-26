package com.piikl.quester.api

class User {

    var id: Long = 0

    var name: String = ""

    var password: String = ""

    lateinit var campaigns: List<Campaign>
}
