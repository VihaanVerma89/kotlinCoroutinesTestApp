package com.example.kotlincoroutinstestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

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

    private val channel = Channel<Any>()
    fun channelSendReceiveTest() {
        viewModelScope.launch(Dispatchers.IO) {
            for (i in 0..5) {
                channel.send(i)
                delay(1000)
            }
            channel.close()
        }

        viewModelScope.launch(Dispatchers.IO) {
            for (receive in channel) {
                println("received: $receive")
            }
            val numbersChannel = produceNumbers()
            val squaresChannel = squares(numbersChannel)

//            for (squares in squaresChannel) {
//                println(squares)
//            }
        }
    }

    fun CoroutineScope.squares(numbersChannel: ReceiveChannel<Int>) = produce {
        println("starting squares channel")
        send(1)
//        for (number in numbersChannel) {
//            send(number * number)
//        }
    }

    fun CoroutineScope.produceNumbers() = produce {
        println("starting numbers channel")
        var x = 1
        while (true) {
            println("sending number: $x")
            send(x++)
            delay(1 * 1000)
        }
    }

    fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
        for (x in 1..5) send(x * x)
    }

    fun flowTest() {
        viewModelScope.launch {
            val intFlow = getIntFlow()
            intFlow.collect {
                println("Flow value : $it")
            }
        }
    }

    fun simple(): Flow<Int> = flow {
        println("flow started")
        for (i in 1..3) {
            delay(100)
            println("emitting $i")
            emit(i)
        }
    }

    fun flowsAreCold() {
        viewModelScope.launch {
            println("calling simple function")
            val flow = simple()
            println("calling collect...")
            flow.collect {
                println("value : $it")
            }
            println("calling collect again ...")
            flow.collect {
                println("value : $it")
            }
        }
    }

    fun flowCancellation() {
        viewModelScope.launch(Dispatchers.IO) {
            val withTimeoutOrNull = withTimeoutOrNull(250)
            {
                val value = 100
                simple().collect {
                    println("value $it")
                }
                value
            }
            println("withTimeoutOrNull $withTimeoutOrNull")
        }
    }

    fun getIntFlow(): Flow<Int> {
        return flow {
            for (i in 1..10) {
                emit(i)
                delay(1 * 1000)
            }
        }
    }

    suspend fun performRequest(request: Int): String {
        delay(100)
        return "response $request"
    }

    fun flowMapOperator() {
        viewModelScope.launch(Dispatchers.IO) {
            (1..3)
                .asFlow()
                .map { request ->
                    performRequest(request)
                }
                .collect { response ->
                    println(response)
                }
        }
    }

    fun flowTransformOperator() {
        viewModelScope.launch(Dispatchers.IO) {
            (1..3)
                .asFlow()
                .transform { request ->
                    emit("Making request $request")
                    emit(performRequest(request))
                }
                .collect { response ->
                    println(response)
                }
        }
    }

    fun numbers(): Flow<Int> = flow {
        try {
            emit(1)
            emit(2)
            println("This line will not be executed")
            emit(3)
        } catch (e: Exception) {
            println("exception caught : ${e.message}")
        } finally {
            println("Finally in numbers")
        }
    }

    fun flowSizeLimitingOperator() {
        viewModelScope.launch(Dispatchers.IO) {
            numbers()
                .take(2)
                .collect { value ->
                    println("received value: $value")
                }
        }
    }

    fun flowTerminalOperator() {
        viewModelScope.launch(Dispatchers.IO) {
            val sum = (1..5).asFlow()
                .map { it * it }
                .reduce { accumulator, value ->
                    println("accumulator : $accumulator")
                    println("value: $value")
                    val sum = accumulator + value
                    println("sum in reduce: $sum")
                    sum
                }
            println("sum : $sum")
        }
    }

    fun flowsAreSequential() {
        viewModelScope.launch(Dispatchers.IO) {
            (1..5)
                .asFlow()
                .filter {
                    println("Filter $it")
                    it % 2 == 0
                }
                .map {
                    println("Map $it")
                    "string $it"
                }
                .collect {
                    println("Collect $it")
                }
        }
    }


    fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")


    fun simpleFlowOn(): Flow<Int> = flow {
        for (i in 1..3) {
            Thread.sleep(100)
            log("Emitting $i")
            emit(i)
        }
    }.flowOn(Dispatchers.IO)

    fun flowOnOperator() {
        viewModelScope.launch {
            simpleFlowOn().collect { value ->
                log("Collected $value")
            }
        }
    }

    fun flowWithoutBufferOperator() {
        viewModelScope.launch {
            val time = measureTimeMillis {
                simple().collect { value ->
                    delay(300)
                    println(value)
                }
            }
            println("Collected in $time ms")
        }

    }

    fun flowBufferOperator() {
        viewModelScope.launch {
            val time = measureTimeMillis {
                simple().buffer().collect { value ->
                    delay(300)
                    println(value)
                }
            }
            println("Collected in $time ms")
        }
    }

    fun flowConflateOperator() {
        viewModelScope.launch {
            val time = measureTimeMillis {
                simple().conflate().collect { value ->
                    delay(300)
                    println(value)
                }
            }
            println("Collected in $time ms")
        }
    }

    fun flowCollectLatest() {
        viewModelScope.launch {
            val time = measureTimeMillis {
                simple().collectLatest { value ->
                    println("Collecting $value")
                    delay(300)
                    println("Done $value")
                }
            }
            println("Collected in $time ms")
        }
    }

    fun flowZipOperator() {
        viewModelScope.launch {
            val numFlow = (1..3).asFlow()
            val strFlow = flowOf("one", "two", "three")

            numFlow.zip(strFlow) { a, b ->
                "$a->$b"
            }.collect {
                println(it)
            }
        }
    }

    fun flowException() {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                simple().collect { value ->
                    println(value)
                    check(value <= 1) {
                        "Collected $value"
                    }
                }
            } catch (e: Exception) {
                println("exception : ${e.message}")
            }

        }

    }

    fun flowExceptionDeclaratively() {

        viewModelScope.launch {

            simple().onEach { value ->
                check(value <= 1) {
                    "Collected $value"
                }
                println(value)
            }.catch { e ->
                println("Caught  $e message:${e.message}")
            }
                .collect {
                    println("collected $it")
                }

        }
    }


//    Cancellation and timeouts

    fun cancelCoroutineExecution() {
        val launch = viewModelScope.launch {

            val job = launch {
                repeat(1000)
                { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            }

            delay(1300)
            println("main: I'm tired of waiting!")
            job.cancel()
            job.join()
            println("main: Now I can quit.")
        }
    }

    fun cancellationIsCooperative() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (i < 5) { // computation loop, just wastes CPU
                    // print a message twice a second
                    if (System.currentTimeMillis() >= nextPrintTime) {
//                        println("job: I'm sleeping ${i++} ...")
                        println("job: I'm sleeping ${i} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }
    }

    fun makingComputationCodeCancellable() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val default = Dispatchers.Default
            println("default $default")
            val job = launch(Dispatchers.IO) {
                var nextPrintTime = startTime
                var i = 0
                while (isActive) { // cancellable computation loop
                    // print a message twice a second
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }
    }

    fun closingResourcesWithFinally() {
        viewModelScope.launch {

            val job = launch(Dispatchers.IO) {
                try {
                    repeat(1000) { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500L)
                    }
                } finally {
                    println("job: I'm running finally")
                }
            }

            delay(1300L)
            println("main: I'm tired of waiting")
            job.cancelAndJoin()
            println("main: Now I can quit...")
        }

    }

    fun runNonCancellableBlockSilentException() {

        viewModelScope.launch {
            val job = launch {

                try {
                    repeat(1000)
                    { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500L)
                    }
                } finally {
                    println("job: I'm in finally block")
                    delay(1000L)
                    println("job: I' was able to run in finally ? ")
                }

            }

            delay(1300L)
            println("main: I'm tired of waiting!")

            job.cancelAndJoin()
            println("main: Now I can quit.")

        }
    }

    fun runNonCancellableBlock() {

        viewModelScope.launch {
            val job = launch {
                try {
                    repeat(1000)
                    { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500L)
                    }
                } finally {
                    println("job: I'm in finally block")
                    withContext(NonCancellable)
                    {
                        delay(1000L)
                        println("job: I' was able to run in finally ? ")
                    }
                }
            }

            delay(1300L)
            println("main: I'm tired of waiting!")

            job.cancelAndJoin()
            println("main: Now I can quit.")

        }
    }


    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L)
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L)
        return 29
    }

    fun sequentialByDefault() {

    }

}

