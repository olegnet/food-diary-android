package net.oleg.fd.barcode

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber

class BarCodeAnalyzer(
    private val listener: (barcode: String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    private val executor = TaskExecutors.MAIN_THREAD

    // from ImageProxy docs:
    //  The returned image should not be closed by the application. Instead it should be closed by the ImageProxy
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        synchronized(this) {
            val image = imageProxy.image
            if (image == null) {
                // TODO send event to analytics and show something to the user
                Timber.e("analyze: imageProxy.image is null")
                imageProxy.close()
                return
            }

            val inputImage = InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees,
                imageProxy.imageInfo.sensorToBufferTransformMatrix
            )

            // val startTime = SystemClock.elapsedRealtime()
            val barCodeTaskList = scanner.process(inputImage)
            barCodeTaskList
                .addOnSuccessListener(executor) {
                    // Timber.d("latency: ${SystemClock.elapsedRealtime() - startTime}")
                    val barCodeList = barCodeTaskList.result
                    if (barCodeList.size > 0) {
                        val code = barCodeList[0].displayValue
                        // Timber.d("code: $code")
                        listener(code.toString())
                    }
                    imageProxy.close()
                }
                .addOnFailureListener(executor) {
                    Timber.e("Barcode detection failed!", it)
                    imageProxy.close()
                }
        }
    }
}