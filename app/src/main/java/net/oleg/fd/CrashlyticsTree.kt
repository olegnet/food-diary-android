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