package com.le.uts_tam.ui.screen.laporan

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Laporan(
    onBack: () -> Unit,
    onKasirClick: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onStokClick: () -> Unit = {},
    onLaporanClick: () -> Unit = {},
    viewModel: LaporanViewModel = viewModel()
) {
    val reportState by viewModel.reportData.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.clickable { onKasirClick() }) {
                        QuickActionButton(Icons.Default.ShoppingCart, "Kasir")
                    }
                    Box(modifier = Modifier.clickable { onRiwayatClick() }) {
                        QuickActionButton(Icons.Default.Email, "Riwayat")
                    }
                    Box(modifier = Modifier.clickable { onStokClick() }) {
                        QuickActionButton(Icons.AutoMirrored.Filled.List, "Stok")
                    }
                    Box(modifier = Modifier.clickable { onLaporanClick() }) {
                        QuickActionButton(Icons.Default.Edit, "Laporan")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "LAPORAN",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        letterSpacing = 1.sp
                    )
                }
                
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp)
            ) {
                val tabs = listOf("HARIAN", "MINGGUAN", "BULAN INI")
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { viewModel.setSelectedTab(tab) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PENDAPATAN $selectedTab",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "TAP UNTUK DETAIL",
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            InteractiveBarChart(
                                data = reportState.chartData,
                                labels = reportState.labels,
                                prefix = "Rp ",
                                suffix = "jt",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = "TANGGAL", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                        Text(text = "PENDAPATAN", modifier = Modifier.weight(1.5f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                        Text(text = "LABA BERSIH", modifier = Modifier.weight(1.5f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        items(reportState.items) { item ->
                            ReportRow(item)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL ESTIMASI",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = reportState.totalEstimasi.uppercase(),
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun InteractiveBarChart(
    data: List<Float>,
    labels: List<String>,
    prefix: String = "",
    suffix: String = "",
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val tooltipBg = MaterialTheme.colorScheme.inverseSurface
    val tooltipText = MaterialTheme.colorScheme.inverseOnSurface

    var selectedBarIndex by remember { mutableStateOf(-1) }
    val maxVal = if (data.isEmpty()) 1f else data.maxOrNull() ?: 1f

    var animationTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animationTriggered = true }
    val animationProgress by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "BarGrowth"
    )

    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(data) {
                        detectTapGestures(
                            onTap = { offset ->
                                val canvasWidth = size.width
                                val barCount = data.size
                                if (barCount == 0) return@detectTapGestures
                                val spacing = canvasWidth / (barCount * 2)
                                val barWidth = (canvasWidth - (spacing * (barCount + 1))) / barCount
                                
                                val index = ((offset.x - spacing / 2) / (barWidth + spacing)).toInt()
                                if (index in 0 until barCount) {
                                    selectedBarIndex = if (selectedBarIndex == index) -1 else index
                                } else {
                                    selectedBarIndex = -1
                                }
                            }
                        )
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barCount = data.size
                if (barCount == 0) return@Canvas
                val spacing = canvasWidth / (barCount * 2)
                val barWidth = (canvasWidth - (spacing * (barCount + 1))) / barCount

                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = canvasHeight - (canvasHeight / gridLines) * i
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                data.forEachIndexed { index, value ->
                    val x = spacing + index * (barWidth + spacing)
                    val barHeight = (canvasHeight * (value / maxVal)) * animationProgress
                    val isSelected = selectedBarIndex == index
                    
                    val brush = if (index == data.size - 1 || isSelected) {
                        Brush.verticalGradient(listOf(tertiaryColor, secondaryColor))
                    } else {
                        Brush.verticalGradient(listOf(primaryColor, primaryContainer))
                    }

                    drawRoundRect(
                        brush = brush,
                        topLeft = Offset(x, canvasHeight - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                        alpha = if (selectedBarIndex != -1 && !isSelected) 0.3f else 1f
                    )

                    if (isSelected || (index == data.size - 1 && selectedBarIndex == -1)) {
                        drawRoundRect(
                            color = tertiaryColor.copy(alpha = 0.2f),
                            topLeft = Offset(x - 4.dp.toPx(), canvasHeight - barHeight - 4.dp.toPx()),
                            size = Size(barWidth + 8.dp.toPx(), barHeight + 4.dp.toPx()),
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                            style = Fill
                        )
                    }
                }
            }

            if (selectedBarIndex != -1 && selectedBarIndex < data.size) {
                val valText = "$prefix${data[selectedBarIndex]}$suffix"
                Surface(
                    color = tooltipBg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = valText,
                        color = tooltipText,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { index, label ->
                val isSelected = selectedBarIndex == index
                Box(
                    modifier = Modifier.width(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected || (index == labels.size - 1 && selectedBarIndex == -1)) tertiaryColor else labelColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected || index == labels.size - 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (isSelected) 12.sp else 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReportRow(item: com.le.uts_tam.ui.screen.laporan.ReportItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (item.isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.date,
            modifier = Modifier.weight(1f),
            color = if (item.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.income,
            modifier = Modifier.weight(1.5f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.profit,
            modifier = Modifier.weight(1.5f),
            color = Color(0xFF4CAF50),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
