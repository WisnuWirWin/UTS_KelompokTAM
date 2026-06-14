package com.le.uts_tam.data.model.dataclass

data class ReportItem(
    val date: String,
    val income: String,
    val profit: String,
    val isToday: Boolean = false
)
