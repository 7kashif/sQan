package com.scanner.sqan.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.scanner.sqan.models.Progress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    private val _progress = MutableLiveData<Progress>()
    val progress: LiveData<Progress> get() = _progress

    fun loginWithEmailAndPassword(
        email: String?,
        password: String?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            _progress.postValue(Progress.Loading)
            if (email != null && password != null && email.isNotEmpty() && password.isNotEmpty()) {
                try {
                    Firebase.auth.signInWithEmailAndPassword(email, password).await()
                    _progress.postValue(Progress.Done)
                } catch (e: Exception) {
                    _progress.postValue(Progress.Error(e.message.toString()))
                }
            } else if (password.isNullOrEmpty())
                _progress.postValue(Progress.Error("Please enter password."))
            else
                _progress.postValue(Progress.Error("Please enter email."))
        }
    }

}