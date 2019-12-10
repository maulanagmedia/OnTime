package gmedia.net.id.OnTime.menu_history_cuti;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ConvertDate;

/**
 * Created by Bayu on 08/12/2017.
 */

public class ListAdapterHistoryCuti extends ArrayAdapter {
    private Context context;
    private List<ModelHistoryCuti> absensi;


    public ListAdapterHistoryCuti(Context context, List<ModelHistoryCuti> absensi) {
        super(context, R.layout.lv_data_history_cuti, absensi);
        this.context = context;
        this.absensi = absensi;
    }

    public void addMoreData(List<ModelHistoryCuti> moreData) {
        absensi.addAll(moreData);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private TextView tglAwal, tglAkhir, alasan, status;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        int hasil = 0;
        if (position % 2 == 1) {
            hasil = 0;
        } else {
            hasil = 1;
        }
        return hasil;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        int tipeViewList = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
            convertView = inflater.inflate(R.layout.lv_data_history_cuti, null);
            holder.tglAwal = (TextView) convertView.findViewById(R.id.tanggalAwalHistoryCuti);
            holder.tglAkhir = (TextView) convertView.findViewById(R.id.tanggalAkHistoryCuti);
            holder.alasan = (TextView) convertView.findViewById(R.id.alasanHistoryCuti);
            holder.status = (TextView) convertView.findViewById(R.id.statusHistoryCuti);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ModelHistoryCuti absen = absensi.get(position);
        String awal = ConvertDate.convert("yyyy-MM-dd","dd-MM-yyyy",absen.getTglAwal());
        String akhir = ConvertDate.convert("yyyy-MM-dd","dd-MM-yyyy",absen.getTglAkhir());
        holder.tglAwal.setText(awal);
        holder.tglAkhir.setText(akhir);
        holder.alasan.setText(absen.getAlasan());
        holder.status.setText(absen.getStatus());
        if (tipeViewList == 0) {
            LinearLayout layoutHistoryCuti = convertView.findViewById(R.id.layoutHistoryCuti);
            layoutHistoryCuti.setBackgroundColor(Color.parseColor("#F6F6F6"));
//            layoutHistoryCuti.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            /*RelativeLayout a = convertView.findViewById(R.id.layouttanggalabsen);
            RelativeLayout c = convertView.findViewById(R.id.layoutjamkeluarabsen);
            RelativeLayout d = convertView.findViewById(R.id.layoutjamtelat);
            a.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            c.setBackgroundColor(Color.parseColor("#FFE5E6E8"));
            d.setBackgroundColor(Color.parseColor("#FFE5E6E8"));*/
        }
        /*TextView texta = convertView.findViewById(R.id.tanggalabsen);
        String textb = texta.getText().toString();
        if (textb.contains("Sab")) {
            texta.setTextColor(Color.parseColor("#FF0000"));
        } else if (textb.contains("Min")) {
            texta.setTextColor(Color.parseColor("#FF0000"));
        } else {
            texta.setTextColor(context.getResources().getColor(R.color.textbiasa));
        }*/
        return convertView;
    }
}
