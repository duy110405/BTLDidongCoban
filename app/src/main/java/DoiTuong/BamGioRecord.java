package DoiTuong;

public class BamGioRecord {
    private int id;
    private int maTask;
    private String tenTask;
    private long thoiGianMillis;
    private String createdAt;

    public BamGioRecord(int id, int maTask, String tenTask, long thoiGianMillis, String createdAt) {
        this.id = id;
        this.maTask = maTask;
        this.tenTask = tenTask;
        this.thoiGianMillis = thoiGianMillis;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getMaTask() { return maTask; }
    public String getTenTask() { return tenTask; }
    public long getThoiGianMillis() { return thoiGianMillis; }
    public String getCreatedAt() { return createdAt; }
}
