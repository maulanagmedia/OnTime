package gmedia.net.id.OnTime.menu_reimburse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class ApprovalReimburs extends AppCompatActivity {

    private RecyclerView rvHistoryReimburse;
    private List<ReimburseModel> reimburseModels = new ArrayList<>();
    private ReimburseAdapter reimburseAdapter;
    private int start = 0, count = 20;
    private SessionManager sessionManager;
    private LinearLayoutManager linearLayoutManager;
    private Activity activity;
    private String statusFilter = "";
    private RadioGroup rgFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_reimburs);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("List Approval Reimburse");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        activity = this;
        sessionManager = new SessionManager(activity);
        linearLayoutManager = new LinearLayoutManager(activity);
        initUi();
    }

    private void initUi(){

        rvHistoryReimburse = findViewById(R.id.rv_reimburse);
        rgFilter = (RadioGroup) findViewById(R.id.rg_filter);

        setupListRiwayatReimburse();
        setupListScrollListenerRiwayatReimburse();

        rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.rb_all){

                    statusFilter = "";
                }else{
                    statusFilter = "0";
                }

                start = 0;
                count = 20;
                loadHistoryReimburse("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        start = 0;
        count = 20;
        loadHistoryReimburse("");
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
        reimburseAdapter = new ReimburseAdapter(activity, reimburseModels, "1");
        rvHistoryReimburse.setLayoutManager(linearLayoutManager);
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
            params.put("status", statusFilter);
            params.put("start", start);
            params.put("count", count);
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("ReimburseActivity","Params "+params);
        Log.d("ReimburseActivity",sessionManager.getKeyIdUser());
        new ApiVolley(activity, params, "POST", LinkURL.urlListApprovalReimburs, "", "", 0, new ApiVolley.VolleyCallback() {
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

                    }else{

                        if(type.equals("search")){
                            reimburseModels.clear();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Terjadi kesalahan dalam memuat data", Toast.LENGTH_SHORT).show();
                }

                reimburseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {
                Log.d(">>>>>", result);
                Toast.makeText(activity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}