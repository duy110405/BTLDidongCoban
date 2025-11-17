package DoiTuong;

import java.io.Serializable;

public class HocSinh implements Serializable {
    private int id;
    private String hoTen;
    private String email;
    private String maSV;
    private String truong;
    private String nganh;
    private String matKhau;

    public HocSinh() {}

    public HocSinh(String hoTen, String email, String maSV,
                   String truong, String nganh, String matKhau) {
        this.hoTen = hoTen;
        this.email = email;
        this.maSV = maSV;
        this.truong = truong;
        this.nganh = nganh;
        this.matKhau = matKhau;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getTruong() { return truong; }
    public void setTruong(String truong) { this.truong = truong; }

    public String getNganh() { return nganh; }
    public void setNganh(String nganh) { this.nganh = nganh; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
}
