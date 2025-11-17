package com.example.didongbtl;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapter.NhiemvuAdapter;
import DoiTuong.NhiemVu;

public class activity_nhiemvu extends AppCompatActivity {

    private static final int REQ_NOTI_PERMISSION = 2001;
    private static final int REQUEST_ADD_TASK = 1001;

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting, btnQuaylai;
    private Button btnThemnhiemvu;
    private RecyclerView rvNhiemvu;

    private TaskDatabaseHelper dbHelper;
    private List<NhiemVu> nhiemVuList;
    private NhiemvuAdapter adapter;

    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nhiemvu);

        // Hỏi quyền thông báo (Android 13+)
        askNotificationPermission();

        // Lấy user đang đăng nhập
        currentUserId = SessionManager.getUserId(this);
        if (currentUserId == -1) {
            Intent intent = new Intent(activity_nhiemvu.this, activity_signin.class);
            startActivity(intent);
            finish();
            return;
        }

        // Ánh xạ view
        btnThemnhiemvu = findViewById(R.id.btnThemnhiemvu);
        rvNhiemvu = findViewById(R.id.rvNhiemvu);

        navHome = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);
        btnQuaylai = findViewById(R.id.btnQuaylai);

        // SQLite helper
        dbHelper = new TaskDatabaseHelper(this);

        // RecyclerView
        nhiemVuList = new ArrayList<>();
        adapter = new NhiemvuAdapter(this, nhiemVuList, dbHelper);
        rvNhiemvu.setLayoutManager(new LinearLayoutManager(this));
        rvNhiemvu.setAdapter(adapter);
        rvNhiemvu.setNestedScrollingEnabled(false);

        loadTasksFromDb();

        // Mở màn thêm nhiệm vụ
        btnThemnhiemvu.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_addnhiemvu.class);
            startActivityForResult(intent, REQUEST_ADD_TASK);
        });

        // Header & bottom menu
        btnQuaylai.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_trangchu.class);
            startActivity(intent);
            finish();
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_setting.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_trangchu.class);
            startActivity(intent);
        });

        navLichhoc.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_lichhoc.class);
            startActivity(intent);
        });

        navBamgio.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_bamgio.class);
            startActivity(intent);
        });

        navNhiemvu.setOnClickListener(v -> {
            // đang ở màn này rồi, không làm gì
        });

        // Đảm bảo DB được tạo
        TaskDatabaseHelper helper = new TaskDatabaseHelper(this);
        helper.getWritableDatabase();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTI_PERMISSION
                );
            }
        }
    }

    private void loadTasksFromDb() {
        nhiemVuList.clear();
        // chỉ lấy nhiệm vụ của currentUserId
        nhiemVuList.addAll(dbHelper.getTasksForUser(currentUserId));
        adapter.notifyDataSetChanged();
    }

    // ================== NHẮC NHIỆM VỤ ==================
    private void scheduleTaskReminder(
            int taskId,
            String tenTask,
            String ngayHetHan,
            String gioHetHan
    ) {
        try {
            if (ngayHetHan == null || gioHetHan == null
                    || ngayHetHan.trim().isEmpty()
                    || gioHetHan.trim().isEmpty()) {
                return; // không có ngày/giờ thì thôi, không đặt nhắc
            }

            // Ví dụ: ngayHetHan = "20/11/2025", gioHetHan = "13:00"
            String timeStr = ngayHetHan + " " + gioHetHan;

            // PHẢI TRÙNG VỚI FORMAT Ở activity_addnhiemvu
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = sdf.parse(timeStr);
            if (date == null) return;

            long triggerAtMillis = date.getTime();
            if (triggerAtMillis <= System.currentTimeMillis()) {
                // quá khứ rồi thì không đặt
                return;
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            Intent intent = new Intent(this, TaskReminderReceiver.class);
            intent.putExtra("taskId", taskId);
            intent.putExtra("tenTask", tenTask);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    taskId, // mỗi nhiệm vụ 1 requestCode riêng
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // ❌ KHÔNG DÙNG setExactAndAllowWhileIdle (đòi quyền SCHEDULE_EXACT_ALARM)
            // ✅ Dùng set() là đủ cho bài tập
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );

            Toast.makeText(
                    this,
                    "Đã đặt nhắc nhiệm vụ lúc " + timeStr,
                    Toast.LENGTH_SHORT
            ).show();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(
                    this,
                    "Không đọc được thời gian nhắc nhiệm vụ",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_TASK && resultCode == RESULT_OK && data != null) {
            String ten = data.getStringExtra("tenTask");
            String moTa = data.getStringExtra("moTa");
            String gioHetHan = data.getStringExtra("gioHetHan");
            String ngayHetHan = data.getStringExtra("ngayHetHan");
            String mucDo = data.getStringExtra("mucDo");

            if (ten != null && !ten.trim().isEmpty()) {
                long newId = dbHelper.insertTask(
                        ten,
                        gioHetHan != null ? gioHetHan : "",
                        ngayHetHan != null ? ngayHetHan : "",
                        mucDo != null ? mucDo : "",
                        moTa != null ? moTa : "",
                        currentUserId
                );
                if (newId > 0) {
                    // Đặt nhắc nhiệm vụ cho bản ghi vừa thêm
                    scheduleTaskReminder(
                            (int) newId,
                            ten,
                            ngayHetHan,
                            gioHetHan
                    );
                }
                loadTasksFromDb();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromDb();
    }
}
