package com.rioaki.ocrdemo.common

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Scheduler(private val task: Runnable) {
    private val executor = Executors.newScheduledThreadPool(1)

    fun scheduleExecution(delay: Long, timeUnit: TimeUnit) {
        val taskWrapper = Runnable {
            task.run()
        }

        executor.scheduleWithFixedDelay(taskWrapper, delay, delay, timeUnit)
    }


    fun stop() {
        executor.shutdown()

        try {
            executor.awaitTermination(1, TimeUnit.HOURS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}