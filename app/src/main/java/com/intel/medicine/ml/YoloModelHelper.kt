// com/intel/medicine/ml/YoloModelHelper.kt
package com.intel.medicine.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.Detection

class YoloModelHelper(context: Context) {
    private val detector: ObjectDetector

    init {
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

        detector = ObjectDetector.createFromFileAndOptions(
            context,
            "model.tflite",  // assets 폴더에 위치
            options
        )
    }

    fun detectObjects(bitmap: Bitmap): List<Detection> {
        val tensorImage = TensorImage.fromBitmap(bitmap)
        return detector.detect(tensorImage)
    }
}
