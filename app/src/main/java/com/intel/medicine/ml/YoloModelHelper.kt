// ml/YoloModelHelper.kt
package com.intel.medicine.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.intel.medicine.data.model.DetectionResult
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.Detection

class YoloModelHelper(context: Context) {

    private var detector: ObjectDetector? = null
    private val tag = "YoloModelHelper"

    // 샘플 약물 데이터베이스 (실제로는 서버에서 가져오거나 로컬 DB 사용)
    private val medicineDatabase = mapOf(
        "tylenol" to DetectionResult(
            medicineName = "타이레놀정 500mg",
            confidence = 0.95f,
            category = "약",
            manufacturer = "한국얀센",
            mainIngredient = "아세트아미노펜",
            description = "해열진통제"
        ),
        "advil" to DetectionResult(
            medicineName = "애드빌정",
            confidence = 0.92f,
            category = "약",
            manufacturer = "화이자제약",
            mainIngredient = "이부프로펜",
            description = "소염진통제"
        ),
        "aspirin" to DetectionResult(
            medicineName = "아스피린정 100mg",
            confidence = 0.88f,
            category = "약",
            manufacturer = "바이엘코리아",
            mainIngredient = "아스피린",
            description = "항혈전제"
        ),
        "norvasc" to DetectionResult(
            medicineName = "노바스크정 5mg",
            confidence = 0.90f,
            category = "약",
            manufacturer = "화이자제약",
            mainIngredient = "암로디핀베실산염",
            description = "고혈압 치료제"
        ),
        "centrum" to DetectionResult(
            medicineName = "센트롬 종합비타민",
            confidence = 0.93f,
            category = "영양제",
            manufacturer = "화이자컨슈머헬스케어",
            mainIngredient = "종합비타민",
            description = "종합 비타민 미네랄"
        ),
        "vitamin_d" to DetectionResult(
            medicineName = "비타민D 1000IU",
            confidence = 0.87f,
            category = "영양제",
            manufacturer = "종근당",
            mainIngredient = "콜레칼시페롤",
            description = "비타민D 보충제"
        )
    )

    init {
        try {
            val options = ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(5)
                .setScoreThreshold(0.3f)
                .build()

            detector = ObjectDetector.createFromFileAndOptions(
                context,
                "model.tflite",  // assets 폴더에 위치해야 함
                options
            )
            Log.d(tag, "YOLO 모델 초기화 성공")
        } catch (e: Exception) {
            Log.e(tag, "YOLO 모델 초기화 실패: ${e.message}")
            e.printStackTrace()
        }
    }

    fun detectMedicine(bitmap: Bitmap): DetectionResult? {
        return try {
            val detections = detectObjects(bitmap)

            if (detections.isNotEmpty()) {
                val bestDetection = detections.maxByOrNull {
                    it.categories.firstOrNull()?.score ?: 0f
                }

                bestDetection?.let { detection ->
                    val label = detection.categories.firstOrNull()?.label?.lowercase()
                    val confidence = detection.categories.firstOrNull()?.score ?: 0f

                    Log.d(tag, "인식된 라벨: $label, 신뢰도: $confidence")

                    // 라벨에 따라 해당 약물 정보 반환
                    val medicineInfo = when {
                        label?.contains("tylenol") == true -> medicineDatabase["tylenol"]
                        label?.contains("advil") == true -> medicineDatabase["advil"]
                        label?.contains("aspirin") == true -> medicineDatabase["aspirin"]
                        label?.contains("norvasc") == true -> medicineDatabase["norvasc"]
                        label?.contains("centrum") == true -> medicineDatabase["centrum"]
                        label?.contains("vitamin") == true -> medicineDatabase["vitamin_d"]
                        else -> {
                            // 기본값으로 첫 번째 약물 정보 반환 (데모용)
                            medicineDatabase["tylenol"]
                        }
                    }

                    // 실제 신뢰도로 업데이트
                    medicineInfo?.copy(confidence = confidence)
                }
            } else {
                Log.d(tag, "약물이 인식되지 않았습니다.")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "약물 인식 중 오류 발생: ${e.message}")
            e.printStackTrace()

            // 오류 발생 시 샘플 데이터 반환 (데모용)
            medicineDatabase["tylenol"]
        }
    }

    private fun detectObjects(bitmap: Bitmap): List<Detection> {
        return try {
            detector?.let { objectDetector ->
                val tensorImage = TensorImage.fromBitmap(bitmap)
                val results = objectDetector.detect(tensorImage)

                Log.d(tag, "객체 감지 결과: ${results.size}개 발견")

                results.forEach { detection ->
                    val category = detection.categories.firstOrNull()
                    Log.d(tag, "감지된 객체: ${category?.label}, 신뢰도: ${category?.score}")
                }

                results
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(tag, "객체 감지 중 오류 발생: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    fun isModelReady(): Boolean {
        return detector != null
    }

    fun close() {
        try {
            detector?.close()
            detector = null
            Log.d(tag, "모델 리소스 해제 완료")
        } catch (e: Exception) {
            Log.e(tag, "모델 리소스 해제 중 오류: ${e.message}")
        }
    }
}