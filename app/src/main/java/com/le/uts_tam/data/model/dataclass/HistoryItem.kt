package com.le.uts_tam.data.model.dataclass

import android.graphics.Color

data class HistoryItem(
    val trxId: String,
    val tgl: String,
    val bln: String,
    val jam: String,
    val customer: Customers,
    val vehicle: Vehicles,
    val layanan: String,
    val totalHarga: String,
    val status: String,
    val statusColor: Color,
    val kategori: String
)