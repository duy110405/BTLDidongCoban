package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.didongbtl.R;

import java.util.List;

import DoiTuong.LichHoc;

public class LichhocAdapter extends RecyclerView.Adapter<LichhocAdapter.LichHocViewHolder> {
    private Context context;
    private List<LichHoc> list;

    public LichhocAdapter(Context context, List<LichHoc> list) {
        this.context = context;
        this.list = list;

    }
    public LichHocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_lophoc, parent, false);
        return new LichHocViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LichHocViewHolder holder, int position) {
        LichHoc item = list.get(position);

        holder.tvTenMon.setText(item.getTenMon());
        holder.tvTime.setText("Giờ bắt đầu: " + item.getGioBatDau());
        holder.tvHinhThucHoc.setText("Hình thức học: " + item.getHinhThucHoc());
        holder.tvRoom.setText(item.getPhongHoc());
    }
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
    static class LichHocViewHolder extends RecyclerView.ViewHolder {

        TextView tvTenMon, tvTime, tvHinhThucHoc, tvRoom;

        public LichHocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvTenMon);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvHinhThucHoc = itemView.findViewById(R.id.tvHinhThucHoc);
            tvRoom = itemView.findViewById(R.id.tvRoom);
        }
    }
}
