package com.scanner.sqan.viewModels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.scanner.sqan.Constants
import com.scanner.sqan.models.DeviceModel
import com.scanner.sqan.models.Progress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DeviceViewModel : ViewModel() {
    private val _deviceLoadingProgress = MutableLiveData<Progress>()
    val deviceLoadingProgress: LiveData<Progress> get() = _deviceLoadingProgress
    private val _deviceUpdatingProgress = MutableLiveData<Progress>()
    val deviceUpdatingProgress: LiveData<Progress> get() = _deviceUpdatingProgress
    private lateinit var currentDev: DeviceModel


    fun getDeviceWithId(deviceId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _deviceLoadingProgress.postValue(Progress.Loading)
            try {
                val doc = Firebase.firestore
                    .collection(Constants.DEVICES)
                    .document(deviceId)
                    .get()
                    .await()
                val dev = doc.toObject<DeviceModel>()
                if (dev != null) {
                    dev.deviceDocId = deviceId
                    currentDev = dev
                    val bundle = Bundle().apply {
                        putParcelable("device", dev)
                    }
                    _deviceLoadingProgress.postValue(Progress.DeviceLoaded(bundle))
                }
            } catch (e: Exception) {
                _deviceLoadingProgress.postValue(Progress.Error(e.message.toString()))
            }
        }
    }

    fun updateDeviceInfo(
        repairDate: String,
        report: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            _deviceUpdatingProgress.postValue(Progress.Loading)
            try {
                val updateMap = mutableMapOf<String,Any>()
                updateMap["lastRepaired"] = repairDate
                updateMap["deviceReport"] = report
                Firebase.firestore
                    .collection(Constants.DEVICES)
                    .document(currentDev.deviceDocId)
                    .set(
                        updateMap, SetOptions.merge()
                    ).await()
                _deviceUpdatingProgress.postValue(Progress.Done)
            } catch (e: Exception) {
                _deviceUpdatingProgress.postValue(Progress.Error(e.message.toString()))
            }
        }
    }
}