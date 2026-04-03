package com.example.yuwathi.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.yuwathi.R;

public class AdminReportsActivity extends BaseAdminActivity {

    private TextView tvUserGrowth, tvComplaintTrends, tvResponseTime, tvTopLocations;
    private CardView cardUserStats, cardComplaintStats, cardLocationStats, cardTimeStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reports and Analytics"); // Fixed '&'
        }

        setupAdminBackButton();
        initializeViews();
        loadReportData();
    }

    private void initializeViews() {
        tvUserGrowth = findViewById(R.id.tv_user_growth);
        tvComplaintTrends = findViewById(R.id.tv_complaint_trends);
        tvResponseTime = findViewById(R.id.tv_response_time);
        tvTopLocations = findViewById(R.id.tv_top_locations);

        cardUserStats = findViewById(R.id.card_user_stats);
        cardComplaintStats = findViewById(R.id.card_complaint_stats);
        cardLocationStats = findViewById(R.id.card_location_stats);
        cardTimeStats = findViewById(R.id.card_time_stats);
    }

    private void loadReportData() {
        tvUserGrowth.setText("↑ 15% this month\n142 total users");
        tvComplaintTrends.setText("28 new complaints\n75% resolved");
        tvResponseTime.setText("Avg: 2.5 hours\nMedian: 1.8 hours");
        tvTopLocations.setText("1. Colombo (45%)\n2. Kandy (22%)\n3. Galle (18%)");
    }

    @Override
    public boolean onSupportNavigateUp() {
        navigateToDashboard();
        return true;
    }

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_admin_reports;
    }
}
