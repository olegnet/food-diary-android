/*
 * Copyright 2022-2024 Oleg Okhotnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oleg.fd

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        // Don't skip any Crashlytics in DEBUG if we already here (must filter DEBUG in MainApplication)
        if (!BuildConfig.DEBUG &&
            (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO)
        ) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "CrashlyticsTree.log(): skipped")
            }
            return
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "CrashlyticsTree.log(priority=$priority tag=$tag message=$message t=${t?.message})")
        }

        Firebase.crashlytics.apply {
            setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
            setCustomKey(CRASHLYTICS_KEY_TAG, tag ?: "")
            setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)

            when (t) {
                null -> recordException(Exception(message))
                else -> recordException(t)
            }
        }
    }

    companion object {
        private val TAG = CrashlyticsTree::class.simpleName

        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}