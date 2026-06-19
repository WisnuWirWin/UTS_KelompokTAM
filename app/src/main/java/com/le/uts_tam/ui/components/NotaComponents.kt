package com.le.uts_tam.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.le.uts_tam.utils.QRUtils

@Composable
fun DashedDivider(
    color: Color = Color.Black,
    thickness: Float = 2f
) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}

@Composable
fun ReceiptText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    fontSize: Int = 12
) {
    Text(
        text = text,
        modifier = modifier,
        color = Color.Black,
        fontSize = fontSize.sp,
        fontFamily = FontFamily.Monospace,
        textAlign = textAlign
    )
}

@Composable
fun QRVisualizer(
    seed: String,
    sizeDp: Int = 100
) {
    val qrBitmap = remember(seed) {
        QRUtils.generateQRCode(seed, 300)
    }

    qrBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .size(sizeDp.dp)
                .background(Color.White)
                .padding(4.dp)
        )
    }
}
