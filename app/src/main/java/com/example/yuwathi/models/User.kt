package com.example.yuwathi.models

import java.util.Date

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val role: UserRole = UserRole.STANDARD_USER,
    val registrationDate: Date = Date(),
    val lastActive: Date = Date(),
    val warningCount: Int = 0,
    val banReason: String = ""
)

enum class UserStatus {
    ACTIVE,
    SUSPENDED,
    BANNED
}

enum class UserRole {
    STANDARD_USER,
    MODERATOR,
    TRUSTED_MEMBER,
    ADMIN
}
