package com.project.sleepwell.utils.alarm

import android.os.Parcel
import android.os.Parcelable

data class AlarmInterval(
    var hour: Int,
    var minute: Int,
    val message: String = "Scheduled Notification"
) : Parcelable {

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(hour)
        dest.writeInt(minute)
        dest.writeString(message)
    }

    companion object CREATOR : Parcelable.Creator<AlarmInterval> {
        override fun createFromParcel(parcel: Parcel): AlarmInterval {
            val hour = parcel.readInt()
            val minute = parcel.readInt()
            val message = parcel.readString() ?: "Scheduled Notification"
            return AlarmInterval(hour, minute, message)
        }

        override fun newArray(size: Int): Array<AlarmInterval?> {
            return arrayOfNulls(size)
        }
    }
}
