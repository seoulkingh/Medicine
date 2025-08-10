// ui/camera/CameraAnalyzer.kt
package com.intel.medicine.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.intel.medicine.ml.YoloModelHelper
import com.intel.medicine.util.BitmapUtils

class CameraAnalyzer(
    private val context: Context,
    private val onMedicineDetected: (String, Float) -> Unit
) : ImageAnalysis.Analyzer {

    private val yoloHelper = YoloModelHelper(context)
    private var lastAnalyzedTimestamp = 0L
    private val tag = "CameraAnalyzer"

    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= 1000) { // 1초마다 분석

            val bitmap = imageProxyToBitmap(image) ?: run {
                image.close()
                return
            }

            val result = yoloHelper.detectMedicine(bitmap)
            result?.let {
                Log.d(tag, "약물 인식: ${it.medicineName}, 신뢰도: ${it.confidence}")
                onMedicineDetected(it.medicineName, it.confidence)
            }

            lastAnalyzedTimestamp = currentTimestamp
        }

        image.close()
    }

    @OptIn(ExperimentalGetImage::class) private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val mediaImage: Image = image.image ?: return null
        val rotationDegrees = image.imageInfo.rotationDegrees
        return BitmapUtils.imageToBitmap(mediaImage, rotationDegrees)
    }

    fun close() {
        yoloHelper.close()
    }
}
