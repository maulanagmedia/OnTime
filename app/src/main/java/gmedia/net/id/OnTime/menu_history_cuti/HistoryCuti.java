package gmedia.net.id.OnTime.menu_history_cuti;

import android.content.Context;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class HistoryCuti extends AppCompatActivity {
	private ListAdapterHistoryCuti adapter;
	private ArrayList<ModelHistoryCuti> historyCuti;
	private ArrayList<ModelHistoryCuti> moreHistoryCuti;
	private ListView listView;
	TextView tvKosong;
	private String tglAwal[] =
			{
					"02/11/2018",
					"08/04/2019",
					"02/11/2018"
			};
	private String tglAkhir[] =
			{
					"04/11/2018",
					"12/04/2019",
					"04/11/2018"
			};
	private String alasan[] =
			{
					"sakit",
					"acara keluarga",
					"sakit"
			};
	private String status[] =
			{
					"setuju",
					"tidak setuju",
					"setuju"
			};
	private Proses proses;
	private boolean isLoading = false;
	private int startIndex = 0;
	private int count = 10;
	private View footerList;
	private DialogGagal dialogGagal;
	private DialogDataTidakDitemukan dialogDataTidakDitemukan;
	private LinearLayout table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_cuti);
		proses = new Proses(HistoryCuti.this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("History Cuti");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setElevation(0);
		initUI();
		initAction();
	}

	private void initUI() {
		listView = (ListView) findViewById(R.id.listViewHistoryCuti);
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerList = li.inflate(R.layout.footer_list, null);
		table = (LinearLayout) findViewById(R.id.tableHistoryCuti);
		tvKosong = findViewById(R.id.tv_kosong);
	}

	private void initAction() {
		isLoading = true;
		proses.ShowDialog();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("id", "");
			jBody.put("start", String.valueOf(startIndex));
			jBody.put("count", String.valueOf(count));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(HistoryCuti.this, jBody, "POST", LinkURL.historyCuti, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				isLoading = false;
				proses.DismissDialog();
				historyCuti = new ArrayList<>();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {
							JSONObject isi = response.getJSONObject(i);
							historyCuti.add(new ModelHistoryCuti(
									isi.getString("awal"),
									isi.getString("akhir"),
									isi.getString("alasan"),
									isi.getString("keterangan")
							));
						}
						listView.setAdapter(null);
						adapter = new ListAdapterHistoryCuti(HistoryCuti.this, historyCuti);
						listView.setAdapter(adapter);
						table.setVisibility(View.VISIBLE);
						tvKosong.setVisibility(View.GONE);
						listView.setOnScrollListener(new AbsListView.OnScrollListener() {
							@Override
							public void onScrollStateChanged(AbsListView absListView, int i) {

							}

							@Override
							public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
								if (view.getLastVisiblePosition() == totalItemCount - 1 && listView.getCount() > (count - 1) && !isLoading) {
									isLoading = true;
									listView.addFooterView(footerList);
									startIndex += count;
//                                        startIndex = 0;
									getMoreData();
									//Log.i(TAG, "onScroll: last ");
								}
							}
						});
					} else if (status.equals("404")) {
						table.setVisibility(View.GONE);
						tvKosong.setVisibility(View.VISIBLE);
						dialogDataTidakDitemukan = new DialogDataTidakDitemukan(HistoryCuti.this);
						dialogDataTidakDitemukan.ShowDialog();
					} else {
//						Toast.makeText(HistoryCuti.this, message, Toast.LENGTH_LONG).show();
						table.setVisibility(View.GONE);
						tvKosong.setVisibility(View.VISIBLE);
						DialogGagal.message = message;
						dialogGagal = new DialogGagal(HistoryCuti.this);
						dialogGagal.ShowDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				isLoading = false;
				proses.DismissDialog();
				Toast.makeText(HistoryCuti.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
//        historyCuti = prepareDataHistoryCuti();

	}

	private void getMoreData() {
		isLoading = true;
		moreHistoryCuti = new ArrayList<>();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("id", "");
			jBody.put("start", String.valueOf(startIndex));
			jBody.put("count", String.valueOf(count));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(HistoryCuti.this, jBody, "POST", LinkURL.viewCuti, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				moreHistoryCuti = new ArrayList<>();
				listView.removeFooterView(footerList);
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {
							JSONObject isi = response.getJSONObject(i);
							moreHistoryCuti.add(new ModelHistoryCuti(
									isi.getString("awal"),
									isi.getString("akhir"),
									isi.getString("alasan"),
									isi.getString("keterangan")
							));
						}
						isLoading = false;
						listView.removeFooterView(footerList);
						if (adapter != null) adapter.addMoreData(moreHistoryCuti);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				isLoading = false;
				proses.DismissDialog();
				Toast.makeText(HistoryCuti.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

    /*private ArrayList<ModelHistoryCuti> prepareDataHistoryCuti() {
        ArrayList<ModelHistoryCuti> rvData = new ArrayList<>();
        for (int i = 0; i < tglAwal.length; i++) {
            ModelHistoryCuti isiHistoryCuti = new ModelHistoryCuti();
            isiHistoryCuti.setTglAwal(tglAwal[i]);
            isiHistoryCuti.setTglAkhir(tglAkhir[i]);
            isiHistoryCuti.setAlasan(alasan[i]);
            isiHistoryCuti.setStatus(status[i]);
            rvData.add(isiHistoryCuti);
        }
        return rvData;
    }*/

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
