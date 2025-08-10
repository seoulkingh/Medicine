// ui/camera/CameraGuideView.kt
package com.intel.medicine.ui.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CameraGuideView(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var animationProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            for (i in 0..100) {
                animationProgress = i / 100f
                delay(20)
            }
            for (i in 100 downTo 0) {
                animationProgress = i / 100f
                delay(20)
            }
        }
    }

    Box(
        modifier = modifier
            .size(300.dp)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = size.center
            val radius = minOf(size.width, size.height) / 2 * 0.8f

            val pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(20f, 10f),
                0f
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.6f + animationProgress * 0.4f),
                radius = radius,
                center = center,
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = pathEffect
                )
            )

            drawGuideLines(center, radius * 0.7f)
        }

        Surface(
            modifier = Modifier.size(8.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.8f)
        ) {}
    }
}

private fun DrawScope.drawGuideLines(center: Offset, radius: Float) {
    val lineColor = Color.White.copy(alpha = 0.3f)
    val strokeWidth = 1.dp.toPx()

    drawLine(
        color = lineColor,
        start = Offset(center.x - radius * 0.3f, center.y),
        end = Offset(center.x + radius * 0.3f, center.y),
        strokeWidth = strokeWidth
    )

    drawLine(
        color = lineColor,
        start = Offset(center.x, center.y - radius * 0.3f),
        end = Offset(center.x, center.y + radius * 0.3f),
        strokeWidth = strokeWidth
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CameraGuideViewPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(Color.Black)
            },
        contentAlignment = Alignment.Center
    ) {
        CameraGuideView()
    }
}