package com.le.uts_tam.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    private val localeID = Locale("id", "ID")
    
    private val currencyFormatter = NumberFormat.getCurrencyInstance(localeID).apply {
        maximumFractionDigits = 0
    }

    private val numberFormatter = NumberFormat.getInstance(localeID)

    fun formatCurrency(amount: Long): String {
        return currencyFormatter.format(amount)
    }

    fun formatNumber(number: Long): String {
        return numberFormatter.format(number)
    }

    fun formatShortAmount(amount: Long): String {
        return when {
            amount >= 1_000_000 -> "Rp %.1fJT".format(amount / 1_000_000.0)
            amount >= 1_000 -> "Rp ${amount / 1_000}K"
            else -> "Rp $amount"
        }
    }

    fun getMonthName(monthNum: String): String {
        return when (monthNum) {
            "01" -> "JAN"; "02" -> "FEB"; "03" -> "MAR"; "04" -> "APR"
            "05" -> "MEI"; "06" -> "JUN"; "07" -> "JUL"; "08" -> "AGU"
            "09" -> "SEP"; "10" -> "OKT"; "11" -> "NOV"; "12" -> "DES"
            else -> "..."
        }
    }

    fun formatDate(date: Date, pattern: String = "dd-MM-yyyy"): String {
        return SimpleDateFormat(pattern, localeID).format(date)
    }

    fun cleanNumber(input: String): String {
        return input.replace(Regex("[^0-9]"), "")
    }
}
