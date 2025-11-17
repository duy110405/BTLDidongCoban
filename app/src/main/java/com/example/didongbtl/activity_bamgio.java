package com.example.didongbtl;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapter.BamgioAdapter;
import DoiTuong.BamGioRecord;
import DoiTuong.NhiemVu;

public class activity_bamgio extends AppCompatActivity {

    // HEADER + NAV
    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting, btnQuaylai;

    // BẤM GIỜ
    private TextView tvTimerDisplay;
    private Button btnStart, btnPause, btnReset, btnThemviec;
    private RecyclerView rvListcongviec;

    private boolean isRunning = false;
    private long startTime = 0L;     // thời điểm bắt đầu (ms)
    private long elapsedTime = 0L;   // thời gian đã trôi (ms)

    private final Handler handler = new Handler();
    private Runnable updateTimerRunnable;

    // SQLITE
    private TaskDatabaseHelper dbHelper;
    private final List<BamGioRecord> bamGioList = new ArrayList<>();
    private BamgioAdapter bamGioAdapter;

    // User hiện tại
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bamgio);

        // Lấy ID học sinh đang đăng nhập
        currentUserId = SessionManager.getUserId(this);
        if (currentUserId == -1) {
            // Chưa đăng nhập -> đá về Signin
            Intent intent = new Intent(activity_bamgio.this, activity_signin.class);
            startActivity(intent);
            finish();
            return;
        }

        // Ánh xạ view
        mapViews();

        // DB helper
        dbHelper = new TaskDatabaseHelper(this);

        // RecyclerView: lịch sử bấm giờ
        bamGioAdapter = new BamgioAdapter(bamGioList);
        rvListcongviec.setLayoutManager(new LinearLayoutManager(this));
        rvListcongviec.setAdapter(bamGioAdapter);
        rvListcongviec.setNestedScrollingEnabled(false);

        // Load lịch sử từ DB (chỉ của currentUserId)
        loadTimeLogsFromDb();

        // Timer runnable: cập nhật mỗi 500ms
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                elapsedTime = now - startTime;
                tvTimerDisplay.setText(formatDuration(elapsedTime));
                if (isRunning) {
                    handler.postDelayed(this, 500);
                }
            }
        };

        // Sự kiện nút bấm giờ
        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnReset.setOnClickListener(v -> resetTimer());
        btnThemviec.setOnClickListener(v -> saveCurrentTimeToTask());

        // Nav & header
        setupNavigation();
    }

    private void mapViews() {
        // Header
        btnQuaylai = findViewById(R.id.btnQuaylai);
        btnSetting = findViewById(R.id.btnSetting);

        // Bottom nav
        navHome = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);

        // Timer & list
        tvTimerDisplay = findViewById(R.id.tv_timer_display);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnReset = findViewById(R.id.btnReset);
        btnThemviec = findViewById(R.id.btnThemviec);
        rvListcongviec = findViewById(R.id.rvListcongviec);

        // Đồng hồ mặc định
        tvTimerDisplay.setText("00:00:00");
    }

    private void setupNavigation() {
        btnQuaylai.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bamgio.this, activity_trangchu.class);
            startActivity(intent);
            finish();
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bamgio.this, activity_setting.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bamgio.this, activity_trangchu.class);
            startActivity(intent);
        });

        navLichhoc.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bamgio.this, activity_lichhoc.class);
            startActivity(intent);
        });

        navNhiemvu.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bamgio.this, activity_nhiemvu.class);
            startActivity(intent);
        });

        navBamgio.setOnClickListener(v -> {
            // đang ở màn này rồi, không làm gì
        });
    }

    // ==================== TIMER ====================

    private void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime; // tiếp tục từ chỗ dừng
            isRunning = true;
            handler.post(updateTimerRunnable);
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(updateTimerRunnable);
        }
    }

    private void resetTimer() {
        isRunning = false;
        handler.removeCallbacks(updateTimerRunnable);
        elapsedTime = 0L;
        tvTimerDisplay.setText("00:00:00");
    }

    private String formatDuration(long millis) {
        if (millis <= 0) return "00:00:00";
        long seconds = millis / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }

    // ==================== LƯU LOG BẤM GIỜ ====================

    private void saveCurrentTimeToTask() {
        if (elapsedTime <= 0) {
            Toast.makeText(this, "Chưa có thời gian để lưu!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<NhiemVu> tasks;
        try {
            // 🔥 chỉ lấy nhiệm vụ của học sinh hiện tại
            tasks = dbHelper.getTasksForUser(currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Lỗi đọc bảng NhiemVu: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (tasks == null || tasks.isEmpty()) {
            Toast.makeText(this, "Chưa có nhiệm vụ nào. Hãy tạo nhiệm vụ trước.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo mảng tên & id
        String[] tenTasks = new String[tasks.size()];
        int[] ids = new int[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            tenTasks[i] = tasks.get(i).getTenTask();
            ids[i] = tasks.get(i).getMaTask();
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn nhiệm vụ để lưu thời gian")
                .setItems(tenTasks, (dialog, which) -> {
                    int maTask = ids[which];
                    String tenTask = tenTasks[which];

                    String createdAt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(new Date());

                    try {
                        // Lưu log vào bảng BangBamGio
                        dbHelper.insertTimeLog(maTask, elapsedTime, createdAt);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,
                                "Lỗi lưu lịch sử bấm giờ: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Thêm vào list hiển thị
                    bamGioList.add(0, new BamGioRecord(
                            0, maTask, tenTask, elapsedTime, createdAt
                    ));
                    bamGioAdapter.notifyItemInserted(0);
                    rvListcongviec.scrollToPosition(0);

                    Toast.makeText(this,
                            "Đã lưu " + formatDuration(elapsedTime) +
                                    " cho nhiệm vụ: " + tenTask,
                            Toast.LENGTH_SHORT).show();

                    resetTimer();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==================== LOAD LỊCH SỬ TỪ DB ====================

    private void loadTimeLogsFromDb() {
        bamGioList.clear();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            // Nếu bảng BangBamGio chưa tồn tại, tạo luôn cho chắc (trong onCreate đã có rồi, nhưng không sao)
            db.execSQL("CREATE TABLE IF NOT EXISTS BangBamGio (" +
                    "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "MaTask INTEGER NOT NULL, " +
                    "ThoiGianMillis INTEGER NOT NULL, " +
                    "CreatedAt TEXT)");

            String sql = "SELECT g.Id, g.MaTask, t.TenTask, g.ThoiGianMillis, g.CreatedAt " +
                    "FROM BangBamGio g " +
                    "JOIN NhiemVu t ON g.MaTask = t.MaTask " +
                    "WHERE t.MaHocSinh = ? " +
                    "ORDER BY g.Id DESC";

            cursor = db.rawQuery(sql, new String[]{String.valueOf(currentUserId)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    int maTask = cursor.getInt(1);
                    String tenTask = cursor.getString(2);
                    long thoiGianMillis = cursor.getLong(3);
                    String createdAt = cursor.getString(4);

                    bamGioList.add(new BamGioRecord(id, maTask, tenTask, thoiGianMillis, createdAt));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Lỗi load lịch sử bấm giờ: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
            bamGioAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTimerRunnable);
        isRunning = false;
    }
}
