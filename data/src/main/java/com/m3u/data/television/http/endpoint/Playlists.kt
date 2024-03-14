package com.m3u.data.television.http.endpoint

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.m3u.core.architecture.pref.Pref
import com.m3u.data.database.model.DataSource
import com.m3u.data.worker.SubscriptionWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class Playlists @Inject constructor(
    private val workManager: WorkManager,
    private val pref: Pref,
    @ApplicationContext private val context: Context
) : Endpoint {
    override fun apply(route: Route) {
        route.route("/playlists") {
            post("subscribe") {
                val dataSourceValue = call.queryParameters["data_source"]
                val dataSource = dataSourceValue?.let { DataSource.ofOrNull(it) }
                if (dataSource == null) {
                    call.respond(
                        DefRep(
                            result = false,
                            reason = "DataSource $dataSourceValue is unsupported."
                        )
                    )
                    return@post
                }

                val title = call.queryParameters["title"]
                val url = call.queryParameters["url"]
                val basicUrl = call.queryParameters["address"]
                val username = call.queryParameters["username"]
                val password = call.queryParameters["password"]
                val epg = call.queryParameters["epg"]

                if (title == null || url == null) {
                    call.respond(
                        DefRep(
                            result = false,
                            reason = "Both title and url are required."
                        )
                    )
                    return@post
                }
                workManager.cancelAllWorkByTag(url)
                workManager.cancelAllWorkByTag(title)

                val request = OneTimeWorkRequestBuilder<SubscriptionWorker>()
                    .setInputData(
                        workDataOf(
                            SubscriptionWorker.INPUT_STRING_TITLE to title,
                            SubscriptionWorker.INPUT_STRING_URL to url,
                            SubscriptionWorker.INPUT_STRING_BASIC_URL to basicUrl,
                            SubscriptionWorker.INPUT_STRING_USERNAME to username,
                            SubscriptionWorker.INPUT_STRING_PASSWORD to password,
                            SubscriptionWorker.INPUT_STRING_EPG to epg,
                            SubscriptionWorker.INPUT_STRING_DATA_SOURCE_VALUE to dataSource.value
                        )
                    )
                    .addTag(url)
                    .addTag(title)
                    .addTag(SubscriptionWorker.TAG)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
                workManager.enqueue(request)
                call.respond(
                    DefRep(result = true)
                )
            }
        }
    }
}