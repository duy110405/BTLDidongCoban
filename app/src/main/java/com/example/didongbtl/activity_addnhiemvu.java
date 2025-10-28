package com.example.didongbtl;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class activity_addnhiemvu extends AppCompatActivity {

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting , btnQuaylai;
    private TextView txtChonngay , txtChongio ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addnhiemvu);

     txtChonngay = findViewById(R.id.txtChonngay);
     txtChongio = findViewById(R.id.txtChongio);

        // =================================================   Gọi inent chuyển trang cho menu , header ========================================== //
        navHome = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);
        btnQuaylai = findViewById(R.id.btnQuaylai);

        btnQuaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(activity_addnhiemvu.this , activity_nhiemvu.class);
                finish();
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_addnhiemvu.this , activity_setting.class) ;
                startActivity(intent);
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_addnhiemvu.this, activity_trangchu.class);
                startActivity(intent);
            }
        });
        navLichhoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_addnhiemvu.this, activity_lichhoc.class);
                startActivity(intent);
            }
        });
        navBamgio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_addnhiemvu.this, activity_bamgio.class);
                startActivity(intent);
            }
        });
        navNhiemvu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_addnhiemvu.this, activity_nhiemvu.class);
                startActivity(intent);
            }
        });
        // ==================================================================================================

        //  Thiết lập sự kiện click mở Dialog
        txtChonngay.setOnClickListener(v -> showDatePickerDialog());
        txtChongio.setOnClickListener(v -> showTimePickerDialog());

    }

    // ==================================================================== Mở dialog chọn ngày =============================================== //
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Logic sau khi người dùng chọn ngày
                    // selectedMonth là 0-based, nên cần cộng 1
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    // Cập nhật TextView với icon và ngày đã chọn
                    txtChonngay.setText( " " + selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }

    // ==================================================================== Mở dialog chọn giờ =============================================== //
    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Tạo TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Logic sau khi người dùng chọn giờ
                    String selectedTime = String.format(Locale.getDefault(),
                            "%02d:%02d",
                            selectedHour, selectedMinute);
                    // Cập nhật TextView với icon và giờ đã chọn
                    txtChongio.setText(" " + selectedTime);
                },
                hour, minute,
                true); // 'true' để sử dụng định dạng 24 giờ, 'false' cho AM/PM
        timePickerDialog.show();
    }
}