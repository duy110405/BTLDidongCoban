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

import DoiTuong.DiemMonHoc;


public class DiemmonAdapter extends RecyclerView.Adapter<DiemmonAdapter.DiemMonViewHolder> {
    private Context context;
    private List<DiemMonHoc> list;

    public DiemmonAdapter(Context context, List<DiemMonHoc> list) {
        this.context = context;
        this.list = list;
    }
    public DiemmonAdapter.DiemMonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_diemmonhoc, parent, false);
        return new DiemmonAdapter.DiemMonViewHolder(view);
    }
    public void onBindViewHolder(@NonNull DiemmonAdapter.DiemMonViewHolder holder, int position) {
        DiemMonHoc item = list.get(position);

        holder.tvTenmon.setText(item.getTenMon());
        holder.tvSotin.setText(String.valueOf(item.getSoTin()));
        holder.tvThangdiem10.setText(String.valueOf(item.getThangDiem10()));
        holder.tvThangdiem4.setText(String.valueOf(item.getThangDiem4()));
        holder.tvThangdiemchu.setText(item.getThangDiemChu());
        holder.tvGhichu.setText(item.getGhiChu());

    }
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
    static class DiemMonViewHolder extends RecyclerView.ViewHolder {

        TextView tvTenmon , tvSotin , tvThangdiem10 , tvThangdiem4 , tvThangdiemchu , tvGhichu ;

        public DiemMonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenmon = itemView.findViewById(R.id.tvTenmon);
            tvSotin = itemView.findViewById(R.id.tvSotin);
            tvThangdiem10 = itemView.findViewById(R.id.tvThangdiem10);
            tvThangdiem4 = itemView.findViewById(R.id.tvThangdiem4);
            tvThangdiemchu = itemView.findViewById(R.id.tvThangdiemchu);
            tvGhichu = itemView.findViewById(R.id.tvGhichu);

        }
    }
}
