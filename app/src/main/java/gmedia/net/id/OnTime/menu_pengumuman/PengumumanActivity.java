package gmedia.net.id.OnTime.menu_pengumuman;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.DialogDataTidakDitemukan;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class PengumumanActivity extends AppCompatActivity {

    private RecyclerView rvPengumuman;
    private ArrayList<ModelPengumuman> dataInfoGaji;
    private PengumumanAdapter adapter;

    private Proses proses;
    public static String id = "";
    private DialogGagal dialogGagal;
    private DialogDataTidakDitemukan dialogDataTidakDitemukan;
    private RelativeLayout listPengumuman;
    private TextView textKosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengumuman);

        proses = new Proses(PengumumanActivity.this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Pengumuman");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#18C1FE"));
        actionBar.setBackgroundDrawable(colorDrawable);

        initUI();

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorNew));
        }
    }

    private void initUI() {
        rvPengumuman = findViewById(R.id.rv_pengumuman);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PengumumanActivity.this);
        rvPengumuman.setLayoutManager(layoutManager);
        listPengumuman = (RelativeLayout) findViewById(R.id.listPengumuman);
        textKosong = (TextView) findViewById(R.id.txtKosong);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAction();
    }

    private void initAction() {
        proses.ShowDialog();
        ApiVolley request = new ApiVolley(PengumumanActivity.this, new JSONObject(), "POST", LinkURL.viewPengumuman, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("PengumumanActivity","onSuccess "+result);
                proses.DismissDialog();
                dataInfoGaji = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray response = object.getJSONArray("response");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject isi = response.getJSONObject(i);
                            dataInfoGaji.add(new ModelPengumuman(
                                    isi.getString("id"),
                                    isi.getString("tgl"),
                                    isi.getString("judul")
                            ));
                        }
                        adapter = new PengumumanAdapter(PengumumanActivity.this, dataInfoGaji);
                        rvPengumuman.setAdapter(adapter);
                        listPengumuman.setVisibility(View.VISIBLE);
                        textKosong.setVisibility(View.GONE);
                    } else if (status.equals("404")) {
                        listPengumuman.setVisibility(View.GONE);
                        textKosong.setVisibility(View.VISIBLE);
                        dialogDataTidakDitemukan = new DialogDataTidakDitemukan(PengumumanActivity.this);
                        dialogDataTidakDitemukan.ShowDialog();
                    } else {
//                        Toast.makeText(MenuPengumuman.this, message, Toast.LENGTH_LONG).show();
                        listPengumuman.setVisibility(View.GONE);
                        textKosong.setVisibility(View.VISIBLE);
                        DialogGagal.message = message;
                        dialogGagal = new DialogGagal(PengumumanActivity.this);
                        dialogGagal.ShowDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String result) {
                proses.DismissDialog();
                Toast.makeText(PengumumanActivity.this, "terjadi kesalahan", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}