package gmedia.net.id.OnTime.menu_history_ijin;

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

public class HistoryIjin extends AppCompatActivity {
	private ListAdapterHistoryIjin adapter;
	private ArrayList<ModelHistoryIjin> historyIjin;
	private ArrayList<ModelHistoryIjin> moreHistoryIjin;
	private ListView listView;
	private Proses proses;
	private boolean isLoading = false;
	private int startIndex = 0;
	private int count = 10;
	private View footerList;
	private DialogDataTidakDitemukan dialogDataTidakDitemukan;
	private DialogGagal dialogGagal;
	private LinearLayout tableHistoryIjin;
	TextView tvKosong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_ijin);
		proses = new Proses(HistoryIjin.this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("History Ijin");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setElevation(0);
		initUI();
		initAction();
	}

	private void initUI() {
		listView = (ListView) findViewById(R.id.listViewHistoryIjin);
		tvKosong = findViewById(R.id.tv_kosong);
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerList = li.inflate(R.layout.footer_list, null);
		tableHistoryIjin=(LinearLayout)findViewById(R.id.tableHistoryIjin);
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
		ApiVolley request = new ApiVolley(HistoryIjin.this, jBody, "POST", LinkURL.historyIjin, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				isLoading = false;
				proses.DismissDialog();
				historyIjin = new ArrayList<>();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {
							JSONObject isi = response.getJSONObject(i);
							historyIjin.add(new ModelHistoryIjin(
									isi.getString("tgl"),
									isi.getString("jam"),
									isi.getString("alasan"),
									isi.getString("keterangan")
							));
						}
						listView.setAdapter(null);
						adapter = new ListAdapterHistoryIjin(HistoryIjin.this, historyIjin);
						listView.setAdapter(adapter);
						tableHistoryIjin.setVisibility(View.VISIBLE);
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
									getMoreData();
									//Log.i(TAG, "onScroll: last ");
								}
							}
						});
					}else if (status.equals("404")) {
						tableHistoryIjin.setVisibility(View.GONE);
						tvKosong.setVisibility(View.VISIBLE);
						dialogDataTidakDitemukan = new DialogDataTidakDitemukan(HistoryIjin.this);
						dialogDataTidakDitemukan.ShowDialog();
					} else {
//						Toast.makeText(HistoryCuti.this, message, Toast.LENGTH_LONG).show();
						tableHistoryIjin.setVisibility(View.GONE);
						DialogGagal.message = message;
						tvKosong.setVisibility(View.VISIBLE);
						dialogGagal = new DialogGagal(HistoryIjin.this);
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
				Toast.makeText(HistoryIjin.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void getMoreData() {
		isLoading = true;
		moreHistoryIjin = new ArrayList<>();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("id", "");
			jBody.put("start", String.valueOf(startIndex));
			jBody.put("count", String.valueOf(count));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(HistoryIjin.this, jBody, "POST", LinkURL.viewIjin, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				moreHistoryIjin = new ArrayList<>();
				listView.removeFooterView(footerList);
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {
							JSONObject isi = response.getJSONObject(i);
							moreHistoryIjin.add(new ModelHistoryIjin(
									isi.getString("tgl"),
									isi.getString("jam"),
									isi.getString("alasan"),
									isi.getString("keterangan")
							));
						}
						isLoading = false;
						listView.removeFooterView(footerList);
						if (adapter != null) adapter.addMoreData(moreHistoryIjin);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				isLoading = false;
				proses.DismissDialog();
				Toast.makeText(HistoryIjin.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
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
