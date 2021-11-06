package com.example.kotlincoroutinstestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repo = MainRepo()
    fun getData() {
        viewModelScope.launch {
            println("print: 1")
            val data = repo.getData()
            println(data)
            println("print: 3")
        }
        println("print: 2")
    }

    fun getDataInDispatcherLaunch() {
        viewModelScope.launch(Dispatchers.IO) {
            println("print: 1")
            val data = repo.getData()
            println(data)
            println("print: 3")
        }
        println("print: 2")
        var i = 0
        i++
    }
}