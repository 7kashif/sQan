package com.scanner.sqan.viewModels

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.scanner.sqan.Constants
import com.scanner.sqan.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class DeviceViewModel : ViewModel() {
    private val uid = Firebase.auth.currentUser?.uid!!
    private val _deviceLoadingProgress = MutableLiveData<Progress>()
    val deviceLoadingProgress: LiveData<Progress> get() = _deviceLoadingProgress
    private val _deviceUpdatingProgress = MutableLiveData<Progress>()
    val deviceUpdatingProgress: LiveData<Progress> get() = _deviceUpdatingProgress
    private lateinit var currentDev: DeviceModel
    private lateinit var currentUser: UserModel
    private val _locationName= MutableLiveData<String>()
    val locationName: LiveData<String> get() = _locationName

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val query = Firebase.firestore.collection(Constants.USERS)
                    .whereEqualTo("userId",uid)
                    .get().await()
                query.forEach {
                    val user = it.toObject<UserModel>()
                    currentUser = user
                }
            }catch (e: Exception) {
                Log.e("deviceViewModel","${e.message}")
            }
        }
    }

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
                    getLocationName(dev.deviceLocationId)
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
                createLog()
                _deviceUpdatingProgress.postValue(Progress.Done)
            } catch (e: Exception) {
                _deviceUpdatingProgress.postValue(Progress.Error(e.message.toString()))
            }
        }
    }

    private fun createLog() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val date = Date()
                val formatter = SimpleDateFormat("MMM dd yyyy HH:mma", Locale.US)
                val myDate: String = formatter.format(date)
                val log= LogsModel(
                    deviceName = currentDev.deviceName,
                    userName = currentUser.userName,
                    locationName = _locationName.value!!,
                    time = myDate
                )
                Firebase.firestore.collection(Constants.LOGS).add(log).await()
            } catch (e: Exception) {
                Log.e("deviceViewModel","${e.message}")
            }
        }
    }

    private fun getLocationName(locationId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val doc = Firebase.firestore.collection(Constants.LOCATIONS).document(locationId).get().await()
            val loc = doc.toObject<LocationModel>()
            _locationName.postValue(loc?.locationName)
        }
    }

    fun clear() {
        _deviceLoadingProgress.postValue(Progress.Done)
    }
}