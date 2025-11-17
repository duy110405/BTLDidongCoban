package com.example.didongbtl;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class TaskReminderReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "task_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("taskId", -1);
        String tenTask = intent.getStringExtra("tenTask");

        // 1. Tạo notification channel cho Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nhắc nhiệm vụ",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo khi đến giờ làm nhiệm vụ");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // 2. Intent mở lại màn hình nhiệm vụ
        Intent openIntent = new Intent(context, activity_nhiemvu.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                taskId, // mỗi task 1 id
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // đổi icon nếu muốn
                .setContentTitle("Đến giờ làm nhiệm vụ")
                .setContentText(tenTask != null ? tenTask : "Đã tới thời gian nhắc")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // 4. CHECK QUYỀN TRƯỚC KHI GỬI (Android 13+ bắt buộc)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                // User chưa cấp / đã từ chối -> thôi, không notify, tránh crash / warning
                return;
            }
        }

        // 5. Gửi notification
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(taskId, builder.build());
    }
}
