package com.vtol.quizbattleapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashViewModel: ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _hasUid = MutableLiveData<Boolean>()
    val hasUid: LiveData<Boolean> get() = _hasUid

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _hasUid.value = (user != null)
        Log.v("TOOL","AuthStateListener UID: ${user?.uid}")
    }

    init {
        // Set initial value immediately
        _hasUid.value = auth.currentUser != null
        Log.v("TOOL","Initial UID: ${auth.currentUser?.uid}")

        // Listen for future auth state changes
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
