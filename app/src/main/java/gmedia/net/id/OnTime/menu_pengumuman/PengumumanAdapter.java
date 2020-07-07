package gmedia.net.id.OnTime.menu_pengumuman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.OnTime.R;

class PengumumanAdapter  extends RecyclerView.Adapter<PengumumanAdapter.ViewHolder> {
    private List<ModelPengumuman> models = new ArrayList<>();
    private Activity context;
    private Fragment fragment;

    public PengumumanAdapter(Activity context, List<ModelPengumuman> models){
        this.models = models;
        this.context = context;
    }

    @NonNull
    @Override
    public PengumumanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pengumuman,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PengumumanAdapter.ViewHolder holder, int position) {
        final ModelPengumuman model = models.get(position);
        holder.tanggal.setText(model.getTanggal());
        holder.judul.setText(model.getJudul());
        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id",model.getId());
                fragment = new DetailPengumuman();
                fragment.setArguments(bundle);
                callFragment(fragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Boolean baru;
        private TextView tanggal, judul;
        private RelativeLayout background;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tanggal = itemView.findViewById(R.id.tanggalInfoGaji);
            judul = itemView.findViewById(R.id.judulInfoGaji);
            background= itemView.findViewById(R.id.bcg_lv_pengumuman);
        }
    }


    private void callFragment(Fragment fragment) {
        ((FragmentActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.zoom_in_detail_pengumuman, R.anim.no_move)
                .replace(R.id.replaceLayout, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}