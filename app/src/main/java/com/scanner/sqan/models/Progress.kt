package com.scanner.sqan.models

sealed class Progress {
    object Loading : Progress()
    object Done : Progress()
    data class Error(val error: String) : Progress()
}
