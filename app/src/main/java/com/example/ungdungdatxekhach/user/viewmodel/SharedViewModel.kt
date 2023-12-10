package com.example.ungdungdatxekhach.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val dataToPass = MutableLiveData<String>()
}