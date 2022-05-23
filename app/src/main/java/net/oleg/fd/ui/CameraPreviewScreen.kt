package net.oleg.fd.ui

import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavHostController
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import net.oleg.fd.BuildConfig
import net.oleg.fd.MainActivity
import net.oleg.fd.R
import net.oleg.fd.barcode.BarCodeAnalyzer
import net.oleg.fd.viewmodel.FoodViewModel
import net.oleg.fd.viewmodel.FoodViewModelMock
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    navController: NavHostController,
    viewModel: FoodViewModel,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = Executors.newSingleThreadExecutor()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DisposableEffect(Unit) {
            onDispose {
                cameraExecutor.shutdown()
            }
        }

        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    setBackgroundColor(Color.TRANSPARENT)

                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                    post {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener(
                            {
                                bindView(
                                    navController,
                                    viewModel,
                                    cameraProviderFuture,
                                    cameraExecutor,
                                    lifecycleOwner,
                                    this
                                )
                            },
                            ContextCompat.getMainExecutor(context)
                        )
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(2.0f),   // TODO replace ic_view_port.xml
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(R.drawable.ic_view_port), contentDescription = null)
        }
    }
}

private fun bindView(
    navController: NavHostController,
    viewModel: FoodViewModel,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    cameraExecutor: ExecutorService,
    lifecycleOwner: LifecycleOwner,
    view: PreviewView,
) {
    val preview = androidx.camera.core.Preview.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
        .build()

    preview.setSurfaceProvider(view.surfaceProvider)

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val rotation = view.display.rotation

    val imageAnalyzer = ImageAnalysis.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
        .setTargetRotation(rotation)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    imageAnalyzer.setAnalyzer(cameraExecutor, BarCodeAnalyzer { barcode ->
        Timber.d("barcode: $barcode")

        // FIXME add tests for navigation
        // FIXME use navController instead of cameraReturnPath?
        val coroutineScope = lifecycleOwner.lifecycle.coroutineScope
        coroutineScope.launch {
            viewModel.setFoodBarcodeAndClear(barcode)
            val screen = viewModel.cameraReturnPath.value ?: Screen.EditFood
            when (screen) {
                Screen.AddToDailyList -> {
                    viewModel.setSelectedFoodItem(null)
                    viewModel.setSearchString(null)
                    viewModel.setSearchBarcode(barcode)
                    navController.navigate(screen)
                }
                Screen.Camera -> {
                    if (viewModel.getFood(barcode) != null) {
                        viewModel.setSelectedFoodItem(null)
                        viewModel.setSearchString(null)
                        viewModel.setSearchBarcode(barcode)
                        navController.navigate(Screen.AddToDailyList)
                    } else {
                        navController.navigate(Screen.EditFood)
                    }
                }
                else -> {
                    navController.navigate(screen)
                }
            }
        }
    })

    val cameraProvider = cameraProviderFuture.get()
    cameraProvider.unbindAll()

    val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)

    if (BuildConfig.DEBUG) {
        camera.cameraInfo.cameraState.observe(lifecycleOwner) { cameraState ->
            Timber.d("cameraState: type=${cameraState.type} error=${cameraState.error}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraPreviewDefaultPreview() {
    CameraPreview(NavHostController(MainActivity()), FoodViewModelMock)
}