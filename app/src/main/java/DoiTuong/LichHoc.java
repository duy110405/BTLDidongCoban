package DoiTuong;

public class LichHoc {
    private int maLich;
    private String tenMon;
    private String gioBatDau;
    private String phongHoc;
    private String hinhThucHoc;
    private String ngayHoc;
    public LichHoc(int maLich, String tenMon, String gioBatDau,
                        String phongHoc, String hinhThucHoc, String ngayHoc) {
        this.maLich = maLich;
        this.tenMon = tenMon;
        this.gioBatDau = gioBatDau;
        this.phongHoc = phongHoc;
        this.hinhThucHoc = hinhThucHoc;
        this.ngayHoc = ngayHoc;
    }

    public int getMaLich() { return maLich; }
    public String getTenMon() { return tenMon; }
    public String getGioBatDau() { return gioBatDau; }
    public String getPhongHoc() { return phongHoc; }
    public String getHinhThucHoc() { return hinhThucHoc; }
    public String getNgayHoc() { return ngayHoc; }
}
