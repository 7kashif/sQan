package com.scanner.sqan.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceModel(
    var deviceDocId: String="", //it will use firebase document id where this device is stored
    var deviceName: String = "",
    var deviceLocationId: String = "", //use location's firebase document id here
    var deviceInfo: String = "",
    var lastRepaired: String = "",
    var deviceReport: String = ""
) : Parcelable

