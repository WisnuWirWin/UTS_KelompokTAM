package com.le.uts_tam.data.model.dataclass

data class ReportState(
    val labels: List<String> = emptyList(),
    val chartData: List<Float> = emptyList(),
    val items: List<ReportItem> = emptyList(),
    val totalEstimasi: String = "Rp 0"
)
