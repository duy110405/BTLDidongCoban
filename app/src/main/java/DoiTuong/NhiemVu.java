package DoiTuong;

public class NhiemVu {
    private int MaTask;
    private String TenTask;
    private String GioKetThucTask;
    private String NgayKetThucTask;
    private String MucDoUuTien;
    private String MoTa;
    private int MaHocSinh; // 🔥 để biết nhiệm vụ thuộc học sinh nào

    public NhiemVu(int MaTask,
                   String TenTask,
                   String GioKetThucTask,
                   String NgayKetThucTask,
                   String MucDoUuTien,
                   String Mota) {
        this.MaTask = MaTask;
        this.TenTask = TenTask;
        this.GioKetThucTask = GioKetThucTask;
        this.NgayKetThucTask = NgayKetThucTask;
        this.MucDoUuTien = MucDoUuTien;
        this.MoTa = Mota;
    }

    // Có thể thêm constructor full nếu cần
    public NhiemVu(int MaTask,
                   String TenTask,
                   String GioKetThucTask,
                   String NgayKetThucTask,
                   String MucDoUuTien,
                   String Mota,
                   int MaHocSinh) {
        this(MaTask, TenTask, GioKetThucTask, NgayKetThucTask, MucDoUuTien, Mota);
        this.MaHocSinh = MaHocSinh;
    }

    public int getMaTask() {
        return MaTask;
    }

    public void setMaTask(int MaTask) {
        this.MaTask = MaTask;
    }

    public String getTenTask() {
        return TenTask;
    }

    public void setTenTask(String TenTask) {
        this.TenTask = TenTask;
    }

    public String getGioKetThucTask() {
        return GioKetThucTask;
    }

    public void setGioKetThucTask(String GioKetThucTask) {
        this.GioKetThucTask = GioKetThucTask;
    }

    public String getNgayKetThucTask() {
        return NgayKetThucTask;
    }

    public void setNgayKetThucTask(String NgayKetThucTask) {
        this.NgayKetThucTask = NgayKetThucTask;
    }

    public String getMucDoUuTien() {
        return MucDoUuTien;
    }

    public void setMucDoUuTien(String MucDoUuTien) {
        this.MucDoUuTien = MucDoUuTien;
    }

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        MoTa = moTa;
    }

    public int getMaHocSinh() {
        return MaHocSinh;
    }

    public void setMaHocSinh(int maHocSinh) {
        MaHocSinh = maHocSinh;
    }
}
