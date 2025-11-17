package Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.didongbtl.R;
import com.example.didongbtl.TaskDatabaseHelper;

import java.util.List;
import java.util.Locale;

import DoiTuong.NhiemVu;

public class NhiemvuAdapter extends RecyclerView.Adapter<NhiemvuAdapter.NhiemVuViewHolder> {

    private final Context context;
    private final List<NhiemVu> list;
    private final TaskDatabaseHelper dbHelper;

    public NhiemvuAdapter(Context context, List<NhiemVu> list, TaskDatabaseHelper dbHelper) {
        this.context = context;
        this.list = list;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public NhiemVuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nhiemvu, parent, false);
        return new NhiemVuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NhiemVuViewHolder holder, int position) {
        NhiemVu item = list.get(position);
        if (item == null) return;

        holder.txtTennhiemvu.setText(item.getTenTask());
        holder.txtMota.setText(item.getMoTa());

        String han = "";
        if (item.getGioKetThucTask() != null && !item.getGioKetThucTask().isEmpty()) {
            han += item.getGioKetThucTask();
        }
        if (item.getNgayKetThucTask() != null && !item.getNgayKetThucTask().isEmpty()) {
            if (!han.isEmpty()) han += " - ";
            han += item.getNgayKetThucTask();
        }
        if (han.isEmpty()) han = "Không có hạn";
        holder.txtGiohethan.setText(han);

        // reset strike-through
        holder.cbNhiemvu.setOnCheckedChangeListener(null);
        holder.cbNhiemvu.setChecked(false);
        holder.txtTennhiemvu.setPaintFlags(
                holder.txtTennhiemvu.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
        );

        // tick -> gạch chữ cho vui
        holder.cbNhiemvu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.txtTennhiemvu.setPaintFlags(
                        holder.txtTennhiemvu.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                );
            } else {
                holder.txtTennhiemvu.setPaintFlags(
                        holder.txtTennhiemvu.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
                );
            }
        });

        // nút Xoá: xoá SQLite + xoá khỏi list
        holder.btnXoaNV.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                NhiemVu nv = list.get(pos);
                dbHelper.deleteTask(nv.getMaTask());
                list.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(context, "Đã xoá nhiệm vụ", Toast.LENGTH_SHORT).show();
            }
        });
        long totalMillis = dbHelper.getTotalTimeForTask(item.getMaTask());
        holder.txtTongThoiGian.setText("Tổng: " + formatDuration(totalMillis));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class NhiemVuViewHolder extends RecyclerView.ViewHolder {
        TextView txtTennhiemvu, txtMota, txtGiohethan, txtTongThoiGian;
        CheckBox cbNhiemvu;
        Button btnXoaNV;

        public NhiemVuViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTennhiemvu = itemView.findViewById(R.id.txtTennhiemvu);
            txtMota = itemView.findViewById(R.id.txtMota);
            txtGiohethan = itemView.findViewById(R.id.txtGiohethan);
            cbNhiemvu = itemView.findViewById(R.id.cbNhiemvu);
            btnXoaNV = itemView.findViewById(R.id.btnXoaNV);
            txtTongThoiGian = itemView.findViewById(R.id.txtTongThoiGian);
        }
    }
    private String formatDuration(long millis) {
        if (millis <= 0) return "00:00:00";
        long seconds = millis / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }

}
