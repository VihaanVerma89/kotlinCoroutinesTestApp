package com.example.kotlincoroutinstestapp

import com.example.kotlincoroutinstestapp.models.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.milliseconds

class MainRepo {

    suspend fun getData(): Result<Any> {
        return withContext(Dispatchers.IO)
        {
            println("print: getData started")
            println("print: adding delay of 3 secs")
            delay(3000)
            println("print: after delay of 3 secs")
            Result.success(Data("some value"))
        }
    }
}