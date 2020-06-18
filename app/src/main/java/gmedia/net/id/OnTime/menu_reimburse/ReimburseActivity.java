package gmedia.net.id.OnTime.menu_reimburse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.SessionManager;

public class ReimburseActivity extends AppCompatActivity {
    FloatingActionButton fbDetail;
    ImageView imgFilter, imgStartDate, imgEndDate;
    TextView tvStartDate, tvEndDate;
    String startDate, endDate;
    RecyclerView rvHistoryReimburse;
    List<ReimburseModel> reimburseModels = new ArrayList<>();
    ReimburseAdapter reimburseAdapter;
    int start =0, count =20;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimburse);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Riwayat Reimburse");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        sessionManager = new SessionManager(ReimburseActivity.this);
        initUi();
    }

    private void initUi(){
        fbDetail=  findViewById(R.id.fb_detail);
        fbDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReimburseActivity.this, AddReimburseActivity.class));
            }
        });

        rvHistoryReimburse = findViewById(R.id.rv_reimburse);

        imgFilter = findViewById(R.id.img_filter);
        imgStartDate = findViewById(R.id.img_start_date);
        imgEndDate = findViewById(R.id.img_end_date);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);

        imgStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar customDate = java.util.Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        customDate.set(java.util.Calendar.YEAR, year);
                        customDate.set(java.util.Calendar.MONTH, month);
                        customDate.set(java.util.Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                        tvStartDate.setText(sdFormat.format(customDate.getTime()));
                        tvStartDate.setAlpha(1);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        startDate = sdf.format(customDate.getTime());
                    }
                };
                new DatePickerDialog(ReimburseActivity.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });

        imgEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar customDate = java.util.Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        customDate.set(java.util.Calendar.YEAR, year);
                        customDate.set(java.util.Calendar.MONTH, month);
                        customDate.set(java.util.Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                        tvEndDate.setText(sdFormat.format(customDate.getTime()));
                        tvEndDate.setAlpha(1);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        endDate = sdf.format(customDate.getTime());
                    }
                };
                new DatePickerDialog(ReimburseActivity.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });
        setupListRiwayatReimburse();
        setupListScrollListenerRiwayatReimburse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start =0;
        count=20;
        loadHistoryReimburse("");
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start =0;
                count =20;
                reimburseAdapter.notifyDataSetChanged();
                loadHistoryReimburse("search");
            }
        });
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

    private void setupListRiwayatReimburse() {
        reimburseAdapter = new ReimburseAdapter(ReimburseActivity.this, reimburseModels);
        rvHistoryReimburse.setLayoutManager(new LinearLayoutManager(ReimburseActivity.this));
        rvHistoryReimburse.setAdapter(reimburseAdapter);
    }

    private void setupListScrollListenerRiwayatReimburse() {
        rvHistoryReimburse.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (! recyclerView.canScrollVertically(1)){
                    start += count;
                    loadHistoryReimburse("scroll");
                }
            }
        });
    }

    @SuppressLint("LogConditional")
    private void loadHistoryReimburse(final String type) {
        if(start == 0){
            reimburseModels.clear();
        }
        JSONObject params = new JSONObject();
        try {
            params.put("datestart",startDate);
            params.put("dateend",endDate);
            params.put("start",start);
            params.put("count",count);
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("ReimburseActivity","Params "+params);
        Log.d("ReimburseActivity",sessionManager.getKeyIdUser());
        new ApiVolley(ReimburseActivity.this, params, "POST", LinkURL.urlRiwayatReimburse, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("ReimburseActivity", "onsuccess "+result);
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONArray("response");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            reimburseModels.add(new ReimburseModel(
                                    isi.getString("id"),
                                    isi.getString("nama"),
                                    isi.getString("tgl_pembayaran"),
                                    isi.getString("foto_pembayaran"),
                                    isi.getString("nominal"),
                                    isi.getString("ket"),
                                    isi.getString("approval"),
                                    isi.getString("insert_at"),
                                    isi.getString("status")
                            ));
                        }
                        reimburseAdapter.notifyDataSetChanged();
                    }else{
                        if(type.equals("search")){
                            reimburseModels.clear();
                            reimburseAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ReimburseActivity.this, "Terjadi kesalahan dalam memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) {
                Log.d(">>>>>", result);
                Toast.makeText(ReimburseActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
