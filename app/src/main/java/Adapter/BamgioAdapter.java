package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.didongbtl.R;

import java.util.List;
import java.util.Locale;

import DoiTuong.BamGioRecord;

public class BamgioAdapter extends RecyclerView.Adapter<BamgioAdapter.BamGioViewHolder> {

    private final List<BamGioRecord> list;

    public BamgioAdapter(List<BamGioRecord> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BamGioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bamgio, parent, false);
        return new BamGioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BamGioViewHolder holder, int position) {
        BamGioRecord item = list.get(position);
        if (item == null) return;

        holder.txtTenTask.setText(item.getTenTask());
        holder.txtTime.setText(formatDuration(item.getThoiGianMillis()));
        holder.txtCreatedAt.setText(item.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class BamGioViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenTask, txtTime, txtCreatedAt;

        public BamGioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTenTask = itemView.findViewById(R.id.txtTenTask);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", m, s);
        }
    }
}
