// com/intel/medicine/ui/camera/CameraActivity.kt
package com.intel.medicine.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraScreen()
        }
    }
}

@Composable
fun CameraScreen() {
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
    var capturedImageUri by remember { mutableStateOf<String?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            Log.d("CameraActivity", "카메라 권한이 허용되었습니다.")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // isBackCamera가 변경될 때마다 카메라 재초기화
    LaunchedEffect(isBackCamera) {
        if (cameraInitialized && cameraProvider != null) {
            val provider = cameraProvider!!

            try {
                provider.unbindAll()

                val preview = Preview.Builder()
                    .build()

                val imageCaptureBuilder = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                imageCapture = imageCaptureBuilder.build()

                val cameraSelector = if (isBackCamera && provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }

                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                Log.d("CameraActivity", "카메라 재초기화 완료 - ${if (isBackCamera) "후면" else "전면"}")

            } catch (e: Exception) {
                Log.e("CameraActivity", "카메라 재초기화 실패", e)
            }
        }
    }

    // 사진 촬영 함수
    fun takePhoto() {
        val captureInstance = imageCapture
        if (captureInstance == null || isCapturing) {
            Log.w("CameraActivity", "ImageCapture가 초기화되지 않았거나 촬영 중입니다")
            return
        }

        isCapturing = true
        Log.d("CameraActivity", "사진 촬영 시작")

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
                    Log.e("CameraActivity", "사진 촬영 실패: ${exception.message}", exception)
                    isCapturing = false
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("CameraActivity", "사진 촬영 완료 - URI: ${output.savedUri}")
                    Log.d("CameraActivity", "갤러리 저장 대기 중...")
                    capturedImageUri = output.savedUri.toString()
                    isCapturing = false
                }
            }
        )
    }

    // 저장 완료 후 초기화
    fun resetCapture() {
        capturedImageUri = null
        Log.d("CameraActivity", "카메라 초기화 완료")
    }

    // 안내선 토글
    fun toggleGuideLines() {
        showGuideLines = !showGuideLines
        Log.d("CameraActivity", "안내선 토글: $showGuideLines")
    }

    if (hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val preview = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    previewView = preview

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val provider = cameraProviderFuture.get()
                            cameraProvider = provider
                            Log.d("CameraActivity", "카메라 프로바이더 가져오기 성공")

                            // 기존 바인딩 해제
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

                            // 카메라 선택 (후면 카메라 우선, 없으면 전면)
                            val cameraSelector = if (isBackCamera && provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }

                            // 카메라 바인딩 (analyzer 없이 안정성 향상)
                            val camera = provider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                previewBuilder,
                                imageCapture
                            )

                            cameraInitialized = true
                            Log.d("CameraActivity", "카메라 초기화 완료")

                        } catch (e: Exception) {
                            Log.e("CameraActivity", "카메라 초기화 실패", e)
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    preview
                },
                update = { previewView ->
                    Log.d("CameraActivity", "PreviewView 업데이트")
                }
            )

            // 원형 안내선 표시 - 조건부 렌더링 개선
            if (showGuideLines && cameraInitialized) {
                CircularCameraGuide(
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 촬영 후 정지된 이미지 표시 (오버레이)
            if (capturedImageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    // 이미지 표시
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(model = capturedImageUri),
                        contentDescription = "촬영된 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )

                    // 텍스트 오버레이
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(24.dp)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "사진이 촬영되었습니다.\n갤러리에 저장하려면 체크 버튼을 누르세요.",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }


            // 하단 컨트롤 - 촬영 완료 상태
            if (capturedImageUri != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 다시 촬영 버튼
                    IconButton(
                        onClick = { resetCapture() },
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color.Red.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "다시 촬영",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 갤러리 저장 완료 버튼
                    IconButton(
                        onClick = {
                            Log.d("CameraActivity", "갤러리 저장 완료")
                            // 이미 갤러리에 저장되어 있으므로 상태만 초기화
                            resetCapture()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color.Green.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "갤러리 저장 완료",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                // 기본 카메라 컨트롤
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 촬영 버튼만 중앙에 배치
                    IconButton(
                        onClick = {
                            Log.d("CameraActivity", "촬영 버튼 클릭")
                            takePhoto()
                        },
                        enabled = !isCapturing,
                        modifier = Modifier
                            .size(70.dp)
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
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    } else {
        // 권한이 없는 경우
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { launcher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("카메라 권한 허용")
            }
        }
    }

    // 메모리 정리
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

// 원형 가이드라인 Composable - 함수명 변경으로 충돌 방지
@Composable
fun CircularCameraGuide(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 원형 가이드라인
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                )
        )

        // 중앙 십자선 (선택사항)
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