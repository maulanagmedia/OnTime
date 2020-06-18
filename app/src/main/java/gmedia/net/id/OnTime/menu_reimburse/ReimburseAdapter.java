package gmedia.net.id.OnTime.menu_reimburse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ConvertDate;

public class ReimburseAdapter extends RecyclerView.Adapter<ReimburseAdapter.ViewHolder>   {
    List<ReimburseModel> reimburseModels;
    Context context;

    public ReimburseAdapter(Context context, List<ReimburseModel> models){
        this.context =context;
        this.reimburseModels = models;
    }

    @NonNull
    @Override
    public ReimburseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reimburs, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SyntheticAccessor")
    @Override
    public void onBindViewHolder(@NonNull final ReimburseAdapter.ViewHolder holder, int position) {
        final ReimburseModel model = reimburseModels.get(position);
        holder.tvInsertAt.setText(ConvertDate.convert("yyyy-MM-dd", "dd MMMM yyyy", model.getInsert_at()));
        holder.tvNama.setText(model.getNama());
        holder.tvInsertAt.setText(ConvertDate.convert("yyyy-MM-dd", "dd MMMM yyyy", model.getTgl_pembayaran()));

        Integer nominal = Integer.parseInt(model.getNominal());
        holder.tvNominal.setText(String.format("Rp %,d", nominal));
//        if(model.approval.equals("1")){
//            holder.rlStatus.setVisibility(View.VISIBLE);
            if(model.status.equals("1")){
                holder.tvStatus.setText("DISETUJUI");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approve);
            }else  if(model.status.equals("2")){
                holder.tvStatus.setText("DITOLAK");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_reject);
            }else{
                holder.tvStatus.setText("PROSES");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            }
//        }else{
//            holder.rlStatus.setVisibility(View.GONE);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailReimburseActivity.class);
                intent.putExtra(DetailReimburseActivity.REIMBURSE_ITEM, new Gson().toJson(model));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reimburseModels.size();
    }

    @Override
    public long getItemId(int position) {
        long id = Long.parseLong(reimburseModels.get(position).getId());
        return id;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInsertAt, tvNama, tvTanggal, tvNominal, tvStatus;
        private RelativeLayout rlStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlStatus = itemView.findViewById(R.id.rl_status);
            tvInsertAt = itemView.findViewById(R.id.tv_insert_at);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvTanggal = itemView.findViewById(R.id.tv_tgl);
            tvNominal = itemView.findViewById(R.id.tv_nominal);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
