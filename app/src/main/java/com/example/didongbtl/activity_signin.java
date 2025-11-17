package com.example.didongbtl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import DoiTuong.HocSinh;

public class activity_signin extends AppCompatActivity {

    Button btnlogin ;
    TextView txtSignup ;
    EditText edtEmail, edtMatkhau;

    DBQuanLyHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        dbHelper   = new DBQuanLyHelper(this);

        btnlogin   = findViewById(R.id.btnLogin);
        txtSignup  = findViewById(R.id.txtSignup);
        edtEmail   = findViewById(R.id.editTextText);
        edtMatkhau = findViewById(R.id.edtMatkhau);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_signin.this, activity_signup.class));
            }
        });
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String pass  = edtMatkhau.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email không được để trống");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtMatkhau.setError("Mật khẩu không được để trống");
            return;
        }

        // Lấy học sinh từ DB
        HocSinh hs = dbHelper.getHocSinhByEmailPassword(email, pass);


        if (hs == null) {
            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            return;
        }
        // ✅ Lưu ID học sinh vào SESSION
        getSharedPreferences("SESSION", MODE_PRIVATE)
                .edit()
                .putInt("ID_HOC_SINH", hs.getId())          // dùng getId() mà bạn đã set trong cursorToHocSinh
                .putString("TEN_HOC_SINH", hs.getHoTen())   // optional, để chào tên ở trang chủ
                .apply();


        SessionManager.saveUserId(this, hs.getId());

        // Đăng nhập thành công: sang Trang chủ, truyền kèm HọcSinh
        Intent intent = new Intent(activity_signin.this, activity_trangchu.class);
        intent.putExtra("hoc_sinh", hs);
        startActivity(intent);
        finish();
}
}
