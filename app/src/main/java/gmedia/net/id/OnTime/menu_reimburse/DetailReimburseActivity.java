package gmedia.net.id.OnTime.menu_reimburse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.ConvertDate;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.DialogSukses;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class DetailReimburseActivity extends AppCompatActivity {

    TextView tvNama, tvTglPembayaran, tvNominal, tvKeterangan;
    ImageView imgBuktiPembayaran;
    Button btnTerima, btnTolak;
    RelativeLayout rlButton;
    private Gson gson = new Gson();
    public static final String REIMBURSE_ITEM = "reimburse_item";
    Dialog dialog;
    private Proses proses;
    private DialogGagal dialogGagal;
    private DialogSukses dialogSukses;
    ScrollView svDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reimburse);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detail Reimburse");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        proses = new Proses(DetailReimburseActivity.this);
        dialogSukses = new DialogSukses(DetailReimburseActivity.this,"");
        initUi();
    }

    private void initUi(){
        String reimburse_item = getIntent().getStringExtra(REIMBURSE_ITEM);
        final ReimburseModel item = gson.fromJson(reimburse_item, ReimburseModel.class);

        tvNama = findViewById(R.id.tv_nama);
        tvTglPembayaran = findViewById(R.id.tv_tgl_bayar);
        tvNominal = findViewById(R.id.tv_nominal);
        tvKeterangan = findViewById(R.id.tv_ket);
        imgBuktiPembayaran = findViewById(R.id.img_bukti);
        rlButton = findViewById(R.id.rl_button);
        svDetail= findViewById(R.id.sv_detail);
        svDetail.setVerticalScrollBarEnabled(false);
        svDetail.setHorizontalScrollBarEnabled(false);

        tvNama.setText(item.getNama());
        tvTglPembayaran.setText(ConvertDate.convert("yyyy-MM-dd", "dd MMMM yyyy", item.getTgl_pembayaran()));
        tvKeterangan.setText(item.getKet());

        Integer nominal = Integer.parseInt(item.nominal);
        tvNominal.setText(String.format("Rp %,d", nominal));

        Glide
                .with(DetailReimburseActivity.this)
                .load(item.getFoto())
                .placeholder(R.drawable.gallery)
                .into(imgBuktiPembayaran);
        if(item.approval.equals("1")){
            if(item.status.equals("0")){
                btnTerima = findViewById(R.id.btn_terima);
                btnTolak = findViewById(R.id.btn_tolak);

                btnTerima.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = new Dialog(DetailReimburseActivity.this);
                        dialog.setContentView(R.layout.popup_reimburse);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(true);
                        Button btnTerima = dialog.findViewById(R.id.btn_ya);
                        Button btnTolak = dialog.findViewById(R.id.btn_tidak);
                        TextView tvMessage = dialog.findViewById(R.id.tv_message);
                        tvMessage.setText("Anda yakin ingin menerima reimburse ini ?");
                        btnTerima.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                approve_reimbers(item.id,"1");
                            }
                        });

                        btnTolak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });

                btnTolak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = new Dialog(DetailReimburseActivity.this);
                        dialog.setContentView(R.layout.popup_reimburse);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(true);
                        Button btnTerima = dialog.findViewById(R.id.btn_ya);
                        Button btnTolak = dialog.findViewById(R.id.btn_tidak);
                        TextView tvMessage = dialog.findViewById(R.id.tv_message);
                        tvMessage.setText("Anda yakin ingin menolak reimburse ini ?");
                        btnTerima.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                approve_reimbers(item.id,"2");
                            }
                        });

                        btnTolak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            }else{
                rlButton.setVisibility(View.GONE);
            }
        }else{
            rlButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void approve_reimbers(String id_reimburse,String status){
        proses.ShowDialog();
        JSONObject params = new JSONObject();
        try {
            params.put("id_reimburse",id_reimburse);
            params.put("status_apv",status);
        }catch (Exception e){
            e.printStackTrace();
        }
        new ApiVolley(DetailReimburseActivity.this, params, "POST", LinkURL.urlPostApproval, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        dialogSukses.ShowDialog();
                    } else {
                        DialogGagal.message = message;
                        dialogGagal = new DialogGagal(DetailReimburseActivity.this);
                        dialogGagal.ShowDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Log.d(">>>>>", result);
                Toasty.error(DetailReimburseActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}
