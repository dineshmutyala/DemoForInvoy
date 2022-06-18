package com.dinesh.demoforinvoy.core.scheduler

import io.reactivex.rxjava3.core.Scheduler

interface SchedulerProvider {
    fun ui(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler

    fun single(): Scheduler
}