package com.example.yuwathi.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.yuwathi.R
import com.example.yuwathi.models.DashboardStats
import com.example.yuwathi.models.ServerStatus

class AdminDashboardActivity : AppCompatActivity() {

    // UI Components
    private lateinit var tvTotalUsers: TextView
    private lateinit var tvActiveUsers: TextView
    private lateinit var tvNewUsers: TextView
    private lateinit var tvPendingComplaints: TextView
    private lateinit var tvResolvedComplaints: TextView
    private lateinit var tvCriticalComplaints: TextView
    private lateinit var tvPublishedTips: TextView
    private lateinit var tvTipEngagement: TextView
    private lateinit var tvServerStatus: TextView
    private lateinit var ivServerStatus: ImageView
    private lateinit var tvLastBackup: TextView

    private lateinit var cardComplaints: CardView
    private lateinit var cardUsers: CardView
    private lateinit var cardSafetyTips: CardView
    private lateinit var cardReports: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        initializeViews()
        setupClickListeners()
        loadDashboardData()
    }

    private fun initializeViews() {
        // Stats TextViews
        tvTotalUsers = findViewById(R.id.tv_total_users)
        tvActiveUsers = findViewById(R.id.tv_active_users)
        tvNewUsers = findViewById(R.id.tv_new_users)
        tvPendingComplaints = findViewById(R.id.tv_pending_complaints)
        tvResolvedComplaints = findViewById(R.id.tv_resolved_complaints)
        tvCriticalComplaints = findViewById(R.id.tv_critical_complaints)
        tvPublishedTips = findViewById(R.id.tv_published_tips)
        tvTipEngagement = findViewById(R.id.tv_tip_engagement)
        tvServerStatus = findViewById(R.id.tv_server_status)
        ivServerStatus = findViewById(R.id.iv_server_status)
        tvLastBackup = findViewById(R.id.tv_last_backup)

        // Navigation Cards
        cardComplaints = findViewById(R.id.card_complaints)
        cardUsers = findViewById(R.id.card_users)
        cardSafetyTips = findViewById(R.id.card_safety_tips)
        cardReports = findViewById(R.id.card_reports)
    }

    private fun setupClickListeners() {
        cardComplaints.setOnClickListener {
            startActivity(Intent(this, AdminComplaintsActivity::class.java))
        }

        cardUsers.setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }

        cardSafetyTips.setOnClickListener {
            startActivity(Intent(this, AdminSafetyTipsActivity::class.java))
        }

        cardReports.setOnClickListener {
            startActivity(Intent(this, AdminReportsActivity::class.java))
        }
    }

    private fun loadDashboardData() {
        // TODO: Replace with actual API call or database query
        val stats = getDummyStats()
        updateUI(stats)
    }

    private fun updateUI(stats: DashboardStats) {
        // Update user stats
        tvTotalUsers.text = stats.totalUsers.toString()
        tvActiveUsers.text = "${stats.activeUsers} Active"
        tvNewUsers.text = "+${stats.newUsersToday} Today"

        // Update complaint stats
        tvPendingComplaints.text = stats.pendingComplaints.toString()
        tvResolvedComplaints.text = "${stats.resolvedComplaints} Resolved"
        tvCriticalComplaints.text = "${stats.criticalComplaints} Critical"

        // Update safety tips stats
        tvPublishedTips.text = stats.publishedTips.toString()
        tvTipEngagement.text = "${stats.tipEngagementToday} Today"

        // Update server status
        updateServerStatus(stats.serverStatus)
        tvLastBackup.text = "Last backup: ${stats.lastBackupTime}"
    }

    private fun updateServerStatus(status: ServerStatus) {
        when (status) {
            ServerStatus.ONLINE -> {
                tvServerStatus.text = "Online"
                tvServerStatus.setTextColor(getColor(R.color.status_online))
                ivServerStatus.setColorFilter(getColor(R.color.status_online))
            }
            ServerStatus.MAINTENANCE -> {
                tvServerStatus.text = "Maintenance"
                tvServerStatus.setTextColor(getColor(R.color.status_warning))
                ivServerStatus.setColorFilter(getColor(R.color.status_warning))
            }
            ServerStatus.OFFLINE -> {
                tvServerStatus.text = "Offline"
                tvServerStatus.setTextColor(getColor(R.color.status_error))
                ivServerStatus.setColorFilter(getColor(R.color.status_error))
            }
        }
    }

    private fun getDummyStats(): DashboardStats {
        return DashboardStats(
            totalUsers = 1247,
            activeUsers = 324,
            newUsersToday = 15,
            pendingComplaints = 8,
            resolvedComplaints = 156,
            criticalComplaints = 2,
            publishedTips = 42,
            tipEngagementToday = 89,
            serverStatus = ServerStatus.ONLINE,
            lastBackupTime = "2 hours ago"
        )
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        loadDashboardData()
    }
}
