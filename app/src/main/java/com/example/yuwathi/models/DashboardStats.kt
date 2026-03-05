package com.example.yuwathi.models

data class DashboardStats(
    val totalUsers: Int = 0,
    val activeUsers: Int = 0,
    val newUsersToday: Int = 0,
    val pendingComplaints: Int = 0,
    val resolvedComplaints: Int = 0,
    val criticalComplaints: Int = 0,
    val publishedTips: Int = 0,
    val tipEngagementToday: Int = 0,
    val serverStatus: ServerStatus = ServerStatus.ONLINE,
    val lastBackupTime: String = ""
)

enum class ServerStatus {
    ONLINE,
    MAINTENANCE,
    OFFLINE
}
