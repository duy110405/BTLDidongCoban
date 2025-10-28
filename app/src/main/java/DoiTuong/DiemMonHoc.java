package DoiTuong;

public class DiemMonHoc {
    private String tenMon , thangDiemChu , ghiChu;
    private int soTin ;
    private double thangDiem10 , thangDiem4 ;

    // Constructor
    public DiemMonHoc(String tenMon , int soTin, double thangDiem10, double thangDiem4, String thangDiemChu, String ghiChu){
        this.tenMon = tenMon ;
        this.soTin = soTin ;
        this.thangDiem10 = thangDiem10 ;
        this.thangDiem4 = thangDiem4 ;
        this.thangDiemChu = thangDiemChu ;
        this.ghiChu = ghiChu;
    }
    // getter
    public String getTenMon() { return tenMon; }
    public int getSoTin() { return soTin; }
    public double getThangDiem10() { return thangDiem10; }
    public double getThangDiem4() { return thangDiem4; }
    public String getThangDiemChu() { return thangDiemChu; }
    public String getGhiChu() { return ghiChu; }

  // không dùng set vì chỉ cần hiển thị dữ liệu thôi
}
