package com.example.didongbtl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import DoiTuong.NhiemVu;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NhiemVu.db";
    // TĂNG VERSION để onUpgrade chạy, tạo lại bảng có MaHocSinh
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "NhiemVu";
    public static final String COL_ID   = "MaTask";
    public static final String COL_TEN  = "TenTask";
    public static final String COL_GIO  = "GioKetThucTask";
    public static final String COL_NGAY = "NgayKetThucTask";
    public static final String COL_MUCDO = "MucDoUuTien";
    public static final String COL_MOTA  = "MoTa";
    public static final String COL_USER  = "MaHocSinh";   // 🔥 khóa ngoại tới HocSinh.Id

    public static final String TABLE_BAMGIO = "BangBamGio";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng nhiệm vụ – có MaHocSinh
        String sqlNhiemVu = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TEN  + " TEXT NOT NULL, " +
                COL_GIO  + " TEXT, " +
                COL_NGAY + " TEXT, " +
                COL_MUCDO+ " TEXT, " +
                COL_MOTA + " TEXT, " +
                COL_USER + " INTEGER NOT NULL" +
                ")";
        db.execSQL(sqlNhiemVu);

        // Bảng lưu lịch sử bấm giờ theo nhiệm vụ
        String sqlBangBamGio = "CREATE TABLE IF NOT EXISTS " + TABLE_BAMGIO + " (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaTask INTEGER NOT NULL, " +
                "ThoiGianMillis INTEGER NOT NULL, " +
                "CreatedAt TEXT, " +
                "FOREIGN KEY (MaTask) REFERENCES " + TABLE_NAME + "(" + COL_ID + ") " +
                "ON DELETE CASCADE" +
                ")";
        db.execSQL(sqlBangBamGio);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Đơn giản: xoá và tạo lại (đang dev nên ok)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAMGIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ================== NHIỆM VỤ ==================

    // Thêm nhiệm vụ CHO 1 HỌC SINH
    public long insertTask(String ten,
                           String gio,
                           String ngay,
                           String mucDo,
                           String moTa,
                           int maHocSinh) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEN, ten);
        values.put(COL_GIO, gio);
        values.put(COL_NGAY, ngay);
        values.put(COL_MUCDO, mucDo);
        values.put(COL_MOTA, moTa);
        values.put(COL_USER, maHocSinh);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Lấy nhiệm vụ THEO 1 HỌC SINH
    public List<NhiemVu> getTasksForUser(int maHocSinh) {
        List<NhiemVu> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COL_USER + "=?",
                new String[]{String.valueOf(maHocSinh)},
                null,
                null,
                COL_ID + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEN));
                String gio = cursor.getString(cursor.getColumnIndexOrThrow(COL_GIO));
                String ngay = cursor.getString(cursor.getColumnIndexOrThrow(COL_NGAY));
                String mucDo = cursor.getString(cursor.getColumnIndexOrThrow(COL_MUCDO));
                String moTa = cursor.getString(cursor.getColumnIndexOrThrow(COL_MOTA));

                NhiemVu nv = new NhiemVu(id, ten, gio, ngay, mucDo, moTa);
                nv.setMaHocSinh(maHocSinh);
                list.add(nv);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    // (Giữ lại getAllTasks nếu muốn debug hoặc dùng chung)
    public List<NhiemVu> getAllTasks() {
        List<NhiemVu> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEN));
                String gio = cursor.getString(cursor.getColumnIndexOrThrow(COL_GIO));
                String ngay = cursor.getString(cursor.getColumnIndexOrThrow(COL_NGAY));
                String mucDo = cursor.getString(cursor.getColumnIndexOrThrow(COL_MUCDO));
                String moTa = cursor.getString(cursor.getColumnIndexOrThrow(COL_MOTA));

                list.add(new NhiemVu(id, ten, gio, ngay, mucDo, moTa));
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    // Xoá nhiệm vụ theo id
    public void deleteTask(int maTask) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(maTask)});
        db.close();
    }

    // ================== Bảng BangBamGio: Lưu lịch sử bấm giờ ==================

    public long insertTimeLog(int maTask, long thoiGianMillis, String createdAt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MaTask", maTask);
        values.put("ThoiGianMillis", thoiGianMillis);
        values.put("CreatedAt", createdAt);
        long id = db.insert(TABLE_BAMGIO, null, values);
        db.close();
        return id;
    }

    // Tổng thời gian (ms) đã bấm cho 1 nhiệm vụ
    public long getTotalTimeForTask(int maTask) {
        SQLiteDatabase db = getReadableDatabase();
        long total = 0;

        Cursor cursor = db.rawQuery(
                "SELECT SUM(ThoiGianMillis) FROM " + TABLE_BAMGIO + " WHERE MaTask = ?",
                new String[]{String.valueOf(maTask)}
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getLong(0);
            }
            cursor.close();
        }
        db.close();
        return total;
    }
}
