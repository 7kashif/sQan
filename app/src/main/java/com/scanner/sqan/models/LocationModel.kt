package com.scanner.sqan.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    var locationName: String = "",
    var locationInfo: String = "",
    var timeStamp: String = "",
    var fbDocumentId: String = ""
):Parcelable

