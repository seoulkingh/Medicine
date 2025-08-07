// com/intel/medicine/ui/camera/CameraAnalyzer.kt
package com.intel.medicine.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.intel.medicine.ml.YoloModelHelper
import java.nio.ByteBuffer

class CameraAnalyzer(private val context: Context) : ImageAnalysis.Analyzer {
    private val yoloHelper = YoloModelHelper(context)

    override fun analyze(image: ImageProxy) {
        val bitmap = imageProxyToBitmap(image) ?: run {
            image.close()
            return
        }

        val results = yoloHelper.detectObjects(bitmap)
        for (result in results) {
            val category = result.categories.firstOrNull()
            Log.d("YOLO", "인식: ${category?.label}, 확률: ${category?.score}")
        }

        image.close()
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val mediaImage: Image = image.image ?: return null
        val rotationDegrees = image.imageInfo.rotationDegrees
        return BitmapUtils.imageToBitmap(mediaImage, rotationDegrees)
    }
}
