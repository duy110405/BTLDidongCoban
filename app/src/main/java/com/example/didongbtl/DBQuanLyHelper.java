package com.example.didongbtl;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import DoiTuong.HocSinh;

public class DBQuanLyHelper extends SQLiteOpenHelper {

    // TÊN FILE PHẢI GIỐNG HỆT FILE TRONG assets
    private static final String DB_NAME = "QuanLySQLDiDong.db";
    private static final int DB_VERSION = 1;

    private Context context;
    private String dbPath;

    public DBQuanLyHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.dbPath = context.getApplicationInfo().dataDir + "/databases/";
        copyDatabaseIfNeeded();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không tạo gì ở đây vì DB lấy từ assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu muốn nâng cấp DB, có thể xóa rồi copy lại
    }

    // ====== COPY DB TỪ ASSETS NẾU CHƯA CÓ ======
    private void copyDatabaseIfNeeded() {
        try {
            File dbFile = new File(dbPath + DB_NAME);
            if (!dbFile.exists()) {
                File folder = new File(dbPath);
                if (!folder.exists()) folder.mkdirs();

                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open(DB_NAME);
                OutputStream os = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
                os.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy SQLiteDatabase để dùng
    public SQLiteDatabase getDatabase() {
        return SQLiteDatabase.openDatabase(
                dbPath + DB_NAME,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );
    }

    // ================== HÀM CHO HOCSINH ==================

    // Thêm học sinh (Đăng ký)
    public long insertHocSinh(HocSinh hs) {
        SQLiteDatabase db = getDatabase();
        ContentValues values = new ContentValues();
        values.put("HoTen", hs.getHoTen());
        values.put("Email", hs.getEmail());
        values.put("MaSV", hs.getMaSV());
        values.put("Truong", hs.getTruong());
        values.put("Nganh", hs.getNganh());
        values.put("MatKhau", hs.getMatKhau());

        long result = db.insert("HocSinh", null, values);
        db.close();
        return result;
    }

    // Lấy học sinh theo Email (check trùng)
    public HocSinh getHocSinhByEmail(String email) {
        SQLiteDatabase db = getDatabase();
        HocSinh hs = null;

        Cursor c = db.rawQuery(
                "SELECT * FROM HocSinh WHERE Email = ?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            hs = cursorToHocSinh(c);
        }
        c.close();
        db.close();
        return hs;
    }

    // Lấy học sinh theo Email + Mật khẩu (đăng nhập)
    public HocSinh getHocSinhByEmailPassword(String email, String password) {
        SQLiteDatabase db = getDatabase();
        HocSinh hs = null;

        Cursor c = db.rawQuery(
                "SELECT * FROM HocSinh WHERE Email = ? AND MatKhau = ?",
                new String[]{email, password}
        );

        if (c.moveToFirst()) {
            hs = cursorToHocSinh(c);
        }
        c.close();
        db.close();
        return hs;
    }

    private HocSinh cursorToHocSinh(Cursor c) {
        HocSinh hs = new HocSinh();
        hs.setId(c.getInt(c.getColumnIndexOrThrow("Id")));
        hs.setHoTen(c.getString(c.getColumnIndexOrThrow("HoTen")));
        hs.setEmail(c.getString(c.getColumnIndexOrThrow("Email")));
        hs.setMaSV(c.getString(c.getColumnIndexOrThrow("MaSV")));
        hs.setTruong(c.getString(c.getColumnIndexOrThrow("Truong")));
        hs.setNganh(c.getString(c.getColumnIndexOrThrow("Nganh")));
        hs.setMatKhau(c.getString(c.getColumnIndexOrThrow("MatKhau")));
        return hs;
    }
}
