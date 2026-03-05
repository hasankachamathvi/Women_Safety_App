package com.example.yuwathi.models

import java.util.Date

data class SafetyTip(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val category: TipCategory = TipCategory.GENERAL,
    val tags: List<String> = emptyList(),
    val isPublished: Boolean = false,
    val scheduledDate: Date? = null,
    val createdDate: Date = Date(),
    val createdBy: String = "",
    val engagementCount: Int = 0,
    val imageUrl: String = ""
)

enum class TipCategory {
    ONLINE_SAFETY,
    PHYSICAL_SECURITY,
    EMERGENCY_RESPONSE,
    SELF_DEFENSE,
    TRAVEL_SAFETY,
    GENERAL
}
