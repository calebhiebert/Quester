package com.piikl.quester.api

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class Campaign() : Parcelable {

    var id: Long = 0

    var name: String? = null

    @JsonIgnoreProperties("campaign")
    var quests: List<Quest>? = null

    @JsonIgnoreProperties("campaigns")
    var creator: User? = null

    @JsonIgnoreProperties("participatingIn")
    var participants: MutableList<User>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        quests = parcel.createTypedArrayList(Quest)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeTypedList(quests)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Campaign> {
        override fun createFromParcel(parcel: Parcel): Campaign {
            return Campaign(parcel)
        }

        override fun newArray(size: Int): Array<Campaign?> {
            return arrayOfNulls(size)
        }
    }

}