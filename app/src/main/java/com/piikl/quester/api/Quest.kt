package com.piikl.quester.api

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.piikl.quester.R
import com.piikl.quester.api.Quest.Status.*
import java.util.*

class Quest() : Parcelable {

    var id: Long = 0

    var name: String? = null
    var updated: Date? = null
    var created: Date? = null

    var unlockedBy: MutableList<Quest>? = null

    var unlockMode: UnlockMode? = null

    var status: Status? = null

    var unlocks: List<Quest>? = null

    var locationObtained: String? = null

    var questGiver: String? = null

    var details: String? = null

    @JsonIgnoreProperties("quests")
    var campaign: Campaign? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        unlocks = parcel.createTypedArrayList(CREATOR)
        locationObtained = parcel.readString()
        questGiver = parcel.readString()
        details = parcel.readString()
        campaign = parcel.readParcelable(Campaign::class.java.classLoader)
        unlockedBy = parcel.createTypedArrayList(CREATOR)
        status = Status.valueOf(parcel.readString())
        unlockMode = UnlockMode.valueOf(parcel.readString())
    }

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

    fun getUnlockedByIds() : MutableList<Long> {
        val list = mutableListOf<Long>()
        unlockedBy?.forEach { list.add(it.id) }
        return list
    }

    override fun equals(other: Any?): Boolean {
        return other is Quest && other.id == this.id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeTypedList(unlocks)
        parcel.writeString(locationObtained)
        parcel.writeString(questGiver)
        parcel.writeString(details)
        parcel.writeParcelable(campaign, flags)
        parcel.writeTypedList(unlockedBy)
        parcel.writeString(status!!.name)
        parcel.writeString(unlockMode!!.name)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quest> {
        override fun createFromParcel(parcel: Parcel): Quest {
            return Quest(parcel)
        }

        override fun newArray(size: Int): Array<Quest?> {
            return arrayOfNulls(size)
        }
    }
}