// ui/camera/CameraActivity.kt
package com.intel.medicine.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.intel.medicine.data.model.DetectionResult
import com.intel.medicine.ml.YoloModelHelper
import com.intel.medicine.ui.result.ResultActivity
import com.intel.medicine.util.BitmapUtils
import com.intel.medicine.util.showToast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    private var cameraAnalyzer: CameraAnalyzer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CameraScreen(
                    onBackClick = { finish() },
                    onPhotoTaken = { detectionResult ->
                        // 결과 액티비티로 이동
                        val intent = Intent(this@CameraActivity, ResultActivity::class.java).apply {
                            putExtra("detection_result", detectionResult)
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAnalyzer?.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onBackClick: () -> Unit = {},
    onPhotoTaken: (DetectionResult) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var cameraInitialized by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isBackCamera by remember { mutableStateOf(true) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var showGuideLines by remember { mutableStateOf(true) }
    var isCapturing by remember { mutableStateOf(false) }
    var detectedMedicine by remember { mutableStateOf<String?>(null) }
    var detectionConfidence by remember { mutableStateOf(0f) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            (context as ComponentActivity).showToast("카메라 권한이 필요합니다")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    fun takePhoto() {
        val captureInstance = imageCapture
        if (captureInstance == null || isCapturing) {
            return
        }

        isCapturing = true

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.KOREA)
            .format(System.currentTimeMillis())

        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        captureInstance.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraActivity", "사진 촬영 실패: ${exception.message}")
                    isCapturing = false
                    (context as ComponentActivity).showToast("사진 촬영에 실패했습니다")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("CameraActivity", "사진 촬영 완료")
                    isCapturing = false

                    // AI 모델로 약물 인식 수행 (샘플 데이터)
                    val yoloHelper = YoloModelHelper(context)
                    val sampleResult = DetectionResult(
                        medicineName = "타이레놀정 500mg",
                        confidence = 0.95f,
                        category = "약",
                        manufacturer = "한국얀센",
                        mainIngredient = "아세트아미노펜",
                        description = "해열진통제"
                    )

                    onPhotoTaken(sampleResult)
                }
            }
        )
    }

    if (hasCameraPermission) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("약물 촬영") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showGuideLines = !showGuideLines }) {
                            Icon(
                                if (showGuideLines) Icons.Default.GridOff else Icons.Default.GridOn,
                                contentDescription = "가이드라인 토글"
                            )
                        }
                        IconButton(onClick = { isBackCamera = !isBackCamera }) {
                            Icon(Icons.Default.Flip, contentDescription = "카메라 전환")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val preview = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val provider = cameraProviderFuture.get()
                                cameraProvider = provider

                                provider.unbindAll()

                                val previewBuilder = Preview.Builder()
                                    .build()
                                    .also {
                                        it.setSurfaceProvider(preview.surfaceProvider)
                                    }

                                val imageCaptureBuilder = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                imageCapture = imageCaptureBuilder.build()

                                val analyzer = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(cameraExecutor, CameraAnalyzer(ctx) { medicine, confidence ->
                                            detectedMedicine = medicine
                                            detectionConfidence = confidence
                                        })
                                    }

                                val cameraSelector = if (isBackCamera) {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                }

                                val camera = provider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    previewBuilder,
                                    imageCapture,
                                    analyzer
                                )

                                cameraInitialized = true

                            } catch (e: Exception) {
                                Log.e("CameraActivity", "카메라 초기화 실패", e)
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        preview
                    }
                )

                // 가이드라인 표시
                if (showGuideLines && cameraInitialized) {
                    CircularCameraGuide(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 인식 결과 표시
                detectedMedicine?.let { medicine ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "약물 인식됨",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                medicine,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "신뢰도: ${(detectionConfidence * 100).toInt()}%",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // 촬영 버튼
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { takePhoto() },
                        enabled = !isCapturing,
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = if (isCapturing) Color.Gray else Color.White,
                                shape = CircleShape
                            )
                            .border(4.dp, Color.Black, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "사진 촬영",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "약물을 원 안에 맞춰 촬영하세요",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Camera,
                    contentDescription = "카메라",
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("카메라 권한이 필요합니다")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { launcher.launch(Manifest.permission.CAMERA) }
                ) {
                    Text("권한 허용")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@Composable
fun CircularCameraGuide(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(20.dp, 2.dp)
                .background(Color.White.copy(alpha = 0.6f))
        )
        Box(
            modifier = Modifier
                .size(2.dp, 20.dp)
                .background(Color.White.copy(alpha = 0.6f))
        )
    }
}