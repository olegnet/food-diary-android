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

package net.oleg.fd.barcode

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
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
                    for (barCode in barCodeList) {
                        val code = barCode.displayValue
                        val valueType = barCode.valueType
                        // Timber.d("type='$valueType' code='$code'")
                        if (valueType == Barcode.TYPE_PRODUCT) {
                            listener(code.toString())
                        }
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