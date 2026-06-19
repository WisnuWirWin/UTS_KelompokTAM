package com.le.uts_tam.ui.screen.kasir

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun QRScanner(
    onScan: (String) -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner: BarcodeScanner = remember { BarcodeScanning.getClient() }

    var isProcessed by remember { mutableStateOf(value = false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

                cameraProviderFuture.addListener(
                    {
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalysis.setAnalyzer(executor) { imageProxy: ImageProxy ->
                                if (!isProcessed) {
                                    processImageProxy(scanner, imageProxy) { code ->
                                        if (!isProcessed) {
                                            isProcessed = true
                                            onScan(code)
                                        }
                                    }
                                } else {
                                    imageProxy.close()
                                }
                            }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            Log.e("QRScanner", "Camera initialization failed", e)
                            onClose()
                        }
                    }, 
                    ContextCompat.getMainExecutor(ctx)
                )

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // QR Scanner Overlay
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.99f)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val boxSize = 250.dp.toPx()
            val left = (canvasWidth - boxSize) / 2
            val top = (canvasHeight - boxSize) / 2
            
            drawRect(color = Color.Black.copy(alpha = 0.7f))
            
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(boxSize, boxSize),
                cornerRadius = CornerRadius(20.dp.toPx()),
                blendMode = BlendMode.Clear
            )

            drawRoundRect(
                color = Color(0xFFFF5722),
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(boxSize, boxSize),
                cornerRadius = CornerRadius(20.dp.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(top = 40.dp, start = 20.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Arahkan kamera ke QR Code barang",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Barcode/QR akan terdeteksi otomatis",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
private fun processImageProxy(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onScan: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes: List<Barcode> ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { value ->
                        onScan(value)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QRScanner", "Scan failure", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
