package com.piikl.quester.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.piikl.quester.R
import com.piikl.quester.api.Quest.Status.*
import java.util.*

class Quest() {

    var id: Long = 0

    var name: String? = null
    var updated: Date? = null
    var created: Date? = null

    var unlockedBy: List<Quest>? = null

    var unlockMode: UnlockMode? = null

    var status: Status? = null

    var unlocks: List<Quest>? = null

    var locationObtained: String? = null

    var questGiver: String? = null

    var details: String? = null

    @JsonIgnoreProperties("quests")
    var campaign: Campaign? = null

    enum class Status {
        COMPLETE, INCOMPLETE, IN_PROGRESS, LOCKED, HIDDEN
    }

    enum class UnlockMode {
        ALL, ANY;

        override fun toString(): String {
            return super.toString().toLowerCase()
        }
    }

    @JsonIgnore
    fun getIconDrawble(): Int? {
        return when(status) {
            INCOMPLETE -> R.drawable.ic_flag_o
            IN_PROGRESS -> R.drawable.ic_flag
            LOCKED -> R.drawable.ic_lock
            HIDDEN -> R.drawable.ic_hidden
            COMPLETE -> R.drawable.ic_check_circle
            else -> null
        }
    }
}