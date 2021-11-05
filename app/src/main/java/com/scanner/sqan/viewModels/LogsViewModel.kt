package com.scanner.sqan.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.scanner.sqan.Constants
import com.scanner.sqan.models.LogsModel
import com.scanner.sqan.models.Progress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogsViewModel : ViewModel() {
    private val _progress = MutableLiveData<Progress>()
    val progress: LiveData<Progress> get() = _progress
    private val _logsList = MutableLiveData<ArrayList<LogsModel>>()
    val logsList: LiveData<ArrayList<LogsModel>> get() = _logsList

    init {
        getAllLogs()
    }

    private fun getAllLogs() {
        CoroutineScope(Dispatchers.IO).launch {
            _progress.postValue(Progress.Loading)
           try {
               Firebase.firestore.collection(Constants.LOGS)
                   .addSnapshotListener { value, error ->
                       error?.let {
                           _progress.postValue(Progress.Error(it.message.toString()))
                           return@addSnapshotListener
                       }
                       value?.let { logs ->
                           val list = arrayListOf<LogsModel>()
                           logs.forEach {
                               val log = it.toObject<LogsModel>()
                               log.docId = it.id
                               list.add(log)
                           }
                           _logsList.postValue(list)
                           _progress.postValue(Progress.Done)
                       }
                   }
           } catch (e: Exception) {
               _progress.postValue(Progress.Error(e.message.toString()))
           }
        }
    }

}