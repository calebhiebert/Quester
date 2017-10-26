package com.piikl.quester.api

import java.util.*

class Quest() {

    var id: Long = 0

    lateinit var name: String

    lateinit var updated: Date

    lateinit var created: Date

    var unlockedBy: List<Quest>? = null

    lateinit var unlockMode: UnlockMode

    lateinit var status: Status

    var unlocks: Quest? = null

    var locationObtained: String? = null

    var questGiver: String? = null

    lateinit var details: String

    lateinit var campaign: Campaign

    enum class Status {
        COMPLETE, INCOMPLETE, IN_PROGRESS, LOCKED, HIDDEN
    }

    enum class UnlockMode {
        ALL, ANY
    }
}