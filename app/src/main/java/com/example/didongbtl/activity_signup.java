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

public class activity_signup extends AppCompatActivity {

    Button btnSignup ;
    TextView txtSignin ;
    EditText edtEmail, edtMatkhau, edtLaimatkhau;

    DBQuanLyHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        dbHelper = new DBQuanLyHelper(this);

        btnSignup     = findViewById(R.id.btnSignup);
        txtSignin     = findViewById(R.id.txtSignin);
        edtEmail      = findViewById(R.id.edtEmail);
        edtMatkhau    = findViewById(R.id.edtMatkhau);
        edtLaimatkhau = findViewById(R.id.edtLaimatkhau);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignup();
            }
        });

        txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_signup.this, activity_signin.class));
            }
        });
    }

    private void handleSignup() {
        String email  = edtEmail.getText().toString().trim();
        String pass   = edtMatkhau.getText().toString().trim();
        String rePass = edtLaimatkhau.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email không được để trống");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtMatkhau.setError("Mật khẩu không được để trống");
            return;
        }
        if (pass.length() < 8) {
            edtMatkhau.setError("Mật khẩu phải ít nhất 8 ký tự");
            return;
        }
        if (!pass.equals(rePass)) {
            edtLaimatkhau.setError("Mật khẩu nhập lại không khớp");
            return;
        }

        // Check email đã tồn tại chưa
        if (dbHelper.getHocSinhByEmail(email) != null) {
            Toast.makeText(this, "Email này đã được đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạm gán cứng, sau có thể thêm EditText Họ tên, Mã SV...
        HocSinh hs = new HocSinh(
                "Nguyễn Văn A",         // HoTen
                email,
                "SV123456789",          // MaSV
                "Đại học Mở Hà Nội",    // Truong
                "Công nghệ thông tin",  // Nganh
                pass
        );

        long id = dbHelper.insertHocSinh(hs);
        if (id > 0) {
            Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(activity_signup.this, activity_signin.class));
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại, thử lại sau", Toast.LENGTH_SHORT).show();
        }
    }
}
