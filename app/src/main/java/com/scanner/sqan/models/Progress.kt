package com.scanner.sqan.models

import android.os.Bundle

sealed class Progress {
    object Loading : Progress()
    object Done : Progress()
    data class Error(val error: String) : Progress()
    data class DeviceLoaded(val bundle: Bundle) : Progress()
}
