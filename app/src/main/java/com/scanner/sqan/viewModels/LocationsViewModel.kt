package com.scanner.sqan.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.scanner.sqan.Constants
import com.scanner.sqan.models.DeviceModel
import com.scanner.sqan.models.LocationModel
import com.scanner.sqan.models.Progress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LocationsViewModel : ViewModel() {
    private val _locationAddingProgress = MutableLiveData<Progress>()
    val locationAddingProgress: LiveData<Progress> get() = _locationAddingProgress
    private val _locationsDownloadingProgress = MutableLiveData<Progress>()
    val locationsDownloadingProgress: LiveData<Progress> get() = _locationsDownloadingProgress
    private val _locationsList = MutableLiveData<ArrayList<LocationModel>>()
    val locationsList: LiveData<ArrayList<LocationModel>> get() = _locationsList
    private val _registeredDevices = MutableLiveData<ArrayList<DeviceModel>>()
    val registeredDevices: LiveData<ArrayList<DeviceModel>> get() = _registeredDevices

    fun addNewLocation(
        locationName: String,
        locationInfo: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            _locationAddingProgress.postValue(Progress.Loading)
            if (locationInfo.isNotEmpty() && locationName.isNotEmpty()) {
                try {
                    val date = Date()
                    val formatter = SimpleDateFormat("yyMMddHHmmssZ", Locale.US)
                    val myDate: String = formatter.format(date)
                    val location = LocationModel(locationName, locationInfo, myDate)
                    Firebase.firestore.collection(Constants.LOCATIONS).add(location).await()
                    _locationAddingProgress.postValue(Progress.Done)
                } catch (e: Exception) {
                    _locationAddingProgress.postValue(Progress.Error(e.message.toString()))
                }
            } else if (locationName.isEmpty())
                _locationAddingProgress.postValue(Progress.Error("Please enter location name"))
            else
                _locationAddingProgress.postValue(Progress.Error("Please enter location info"))
        }
    }

    fun getAllLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            _locationsDownloadingProgress.postValue(Progress.Loading)
            try {
                Firebase.firestore.collection(Constants.LOCATIONS)
                    .addSnapshotListener { value, error ->
                        error?.let {
                            _locationsDownloadingProgress.postValue(Progress.Error(it.message.toString()))
                            return@addSnapshotListener
                        }
                        value?.let { locations ->
                            val list = ArrayList<LocationModel>()
                            for (item in locations) {
                                val loc = item.toObject<LocationModel>()
                                loc.fbDocumentId = item.id
                                list.add(loc)
                            }
                            _locationsList.postValue(list)
                            _locationsDownloadingProgress.postValue(Progress.Done)
                        }
                    }
            } catch (e: Exception) {
                _locationsDownloadingProgress.postValue(Progress.Error(e.message.toString()))
            }
        }
    }

    fun getAllRegisteredDevices() {
        CoroutineScope(Dispatchers.IO).launch {
            _locationsDownloadingProgress.postValue(Progress.Loading)
            try {
                Firebase.firestore.collection(Constants.DEVICES)
                    .addSnapshotListener { value, error ->
                        error?.let {
                            _locationsDownloadingProgress.postValue(Progress.Error(it.message.toString()))
                            return@addSnapshotListener
                        }
                        value?.let { locations ->
                            val list = ArrayList<DeviceModel>()
                            for (item in locations) {
                                val dev = item.toObject<DeviceModel>()
                                dev.deviceDocId = item.id //here we are setting the device id using firebase document id to uniquely identify it
                                list.add(dev)
                            }
                            _registeredDevices.postValue(list)
                            _locationsDownloadingProgress.postValue(Progress.Done)
                        }
                    }
            } catch (e: Exception) {
                _locationsDownloadingProgress.postValue(Progress.Error(e.message.toString()))
            }
        }
    }

}