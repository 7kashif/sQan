package com.scanner.sqan.models

data class LogsModel(
    var docId: String="", //firebase doc id
    var deviceName: String="",
    val userName: String = "",
    val locationName: String = "",
    val time: String = ""
)
