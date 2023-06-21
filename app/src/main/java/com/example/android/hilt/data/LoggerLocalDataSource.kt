/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.hilt.data

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Data manager class that handles data manipulation between the database and the UI.
 *
 * The +@Inject+ is telling +Hilt+ how to build new
 * instances of this class, when this class is a +Hilt+
 * dependency. This is also called +Hilt binding+.
 *
 * The +Singleton+ annotation (which is a scope annotation)
 * tells +Hilt+ application container to return the same
 * dependency instance for the same application container instance.
 * Note: for other Component Scopes read this: https://developer.android.com/training/dependency-injection/hilt-android#component-scopes
 *
 * The other problem that we have to soft ous is the transitive
 * dependency to +LogDao+. The +LogDao+ is an +Interface+, hence
 * it does have a +constructor+ to apply +@Inject+. In order to solve
 * this problem, we will use Hilt Modules, which is a special API/tool
 * that allows us to define _bindings_ i.e. a way for Hilt to know ho
 * to provide instances of types. Look at the package +di+ and the file
 * +DatabaseModule.kt+.
 */
class LoggerLocalDataSource @Inject constructor(private val logDao: LogDao) : LoggerDataSource {

    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun addLog(msg: String) {
        executorService.execute {
            logDao.insertAll(
                Log(
                    msg,
                    System.currentTimeMillis()
                )
            )
        }
    }

    override fun getAllLogs(callback: (List<Log>) -> Unit) {
        executorService.execute {
            val logs = logDao.getAll()
            mainThreadHandler.post { callback(logs) }
        }
    }

    override fun removeLogs() {
        executorService.execute {
            logDao.nukeTable()
        }
    }
}
