package DoiTuong;

import java.lang.invoke.MutableCallSite;

public class NhiemVu {
    private int MaTask ;
    private String TenTask ;
    private String GioKetThucTask;
    private String NgayKetThucTask;
    private String MucDoUuTien;
    private String MoTa ;
    public NhiemVu(int MaTask ,String TenTask ,String GioKetThucTask ,String NgayKetThucTask ,String MucDoUuTien ,String Mota){
        this.MaTask = MaTask;
        this.TenTask = TenTask;
        this.GioKetThucTask = GioKetThucTask;
        this.NgayKetThucTask = NgayKetThucTask;
        this.MucDoUuTien = MucDoUuTien;
        this.MoTa = Mota;
    }
    public int getMaTask (){return MaTask;}
    public void setMaTask(int MaTask){this.MaTask=MaTask;}
    public String getTenTask(){return  TenTask;}
    public void setTenTask(String TenTask){this.TenTask=TenTask;}
    public String getGioKetThucTask(){return GioKetThucTask;}
    public void setGioKetThucTask(String NgayKetThucTask){this.NgayKetThucTask=NgayKetThucTask;}
    public String getNgayKetThucTask(){return NgayKetThucTask;}
    public void setNgayKetThucTask(String NgayKetThucTask){this.NgayKetThucTask=NgayKetThucTask;}
    public String getMucDoUuTien(){return MucDoUuTien;}
    public void setMucDoUuTien(String MucDoUuTien){this.MucDoUuTien = MucDoUuTien;}

    public String getMoTa() {
        return MoTa;
    }
    public void setMoTa(String moTa) {
        MoTa = moTa;
    }
}

