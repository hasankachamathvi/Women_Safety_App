package com.example.yuwathi.models

import java.util.Date

data class Complaint(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val description: String = "",
    val category: ComplaintCategory = ComplaintCategory.OTHER,
    val urgency: UrgencyLevel = UrgencyLevel.MEDIUM,
    val status: ComplaintStatus = ComplaintStatus.PENDING,
    val timestamp: Date = Date(),
    val evidenceUrls: List<String> = emptyList(),
    val location: String = "",
    val resolvedBy: String = "",
    val resolvedDate: Date? = null,
    val adminNotes: String = ""
)

enum class ComplaintCategory {
    HARASSMENT,
    STALKING,
    ASSAULT,
    CYBERBULLYING,
    DOMESTIC_VIOLENCE,
    OTHER
}

enum class UrgencyLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class ComplaintStatus {
    PENDING,
    UNDER_REVIEW,
    RESOLVED,
    DISMISSED,
    ESCALATED
}
