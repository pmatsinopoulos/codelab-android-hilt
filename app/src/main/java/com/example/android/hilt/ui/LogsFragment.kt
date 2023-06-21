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

package com.example.android.hilt.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.android.hilt.R
import com.example.android.hilt.data.Log
import com.example.android.hilt.data.LoggerLocalDataSource
import com.example.android.hilt.util.DateFormatter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment that displays the database logs.
 *
 * +logger+ and +dateFormatter+ are two dependencies that
 * come from the +serviceLocator+ of the main application instance
 * i.e. +context.applicationContext as LogApplication+
 *
 * We use +@AndroidEntryPoint+ to ask +Hilt+ to inject these
 * dependencies and manage their lifecycle. This will create
 * a dependencies container that follows the Android
 * LogsFragment (Fragment) lifecycle.
 */
@AndroidEntryPoint
class LogsFragment : Fragment() {

    /**
     * This is how we can reference fields that are injected by Hilt.
     * But how Hilt knows how to provide instances of these classes?
     * i.e. of +LoggerLocalDataSource+ and +DateFormatter+. Look at these
     * classes source code to find out how.
     *
     * Note that this is called _field injection_.
     *
     * However, the +logger: LoggerLocalDataSource+ is a special
     * case because for the same instance of +serviceLocator+
     * the +serviceLocator.loggerLocalDataSource+ is the same
     * instance. It's not a new instance. This means that the
     * dependency instance is _scoped to the application container+, i.e.
     * the dependency instance is _scoped to the serviceLocator+.
     * How can we do that with +Hilt+?
     * We use _scoping annotations_. See the +LoggerLocalDataSource+
     * annotation +@Singleton+.
     * Note: for other Component Scopes read this: https://developer.android.com/training/dependency-injection/hilt-android#component-scopes
     */
    @Inject lateinit var logger: LoggerLocalDataSource
    @Inject lateinit var dateFormatter: DateFormatter

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
        }
    }

    /**
     * We remove the code below, because it is not needed anymore
     */
    /* removed thanks to Hilt
    override fun onAttach(context: Context) {
        super.onAttach(context)

        populateFields(context)
    }

    private fun populateFields(context: Context) {
        logger = (context.applicationContext as LogApplication).serviceLocator.loggerLocalDataSource
        dateFormatter =
            (context.applicationContext as LogApplication).serviceLocator.provideDateFormatter()
    }
    */
    override fun onResume() {
        super.onResume()

        logger.getAllLogs { logs ->
            recyclerView.adapter =
                LogsViewAdapter(
                    logs,
                    dateFormatter
                )
        }
    }
}

/**
 * RecyclerView adapter for the logs list.
 */
private class LogsViewAdapter(
    private val logsDataSet: List<Log>,
    private val daterFormatter: DateFormatter
) : RecyclerView.Adapter<LogsViewAdapter.LogsViewHolder>() {

    class LogsViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsViewHolder {
        return LogsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.text_row_item, parent, false) as TextView
        )
    }

    override fun getItemCount(): Int {
        return logsDataSet.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        val log = logsDataSet[position]
        holder.textView.text = "${log.msg}\n\t${daterFormatter.formatDate(log.timestamp)}"
    }
}
