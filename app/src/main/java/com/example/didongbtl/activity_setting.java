 package com.example.didongbtl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.badge.BadgeUtils;

 public class activity_setting extends AppCompatActivity {

    ImageButton btnQuaylai ;
    LinearLayout layoutlogout ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        // ========================================== gọi intent chuyển trang ====================================//
        btnQuaylai = findViewById(R.id.btnQuaylai);
        layoutlogout = findViewById(R.id.layoutLogout);
        btnQuaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_setting.this , activity_trangchu.class);
                startActivity(intent);
                finish();
            }
        });
        layoutlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_setting.this , activity_signin.class);
            }
        });

    }
}