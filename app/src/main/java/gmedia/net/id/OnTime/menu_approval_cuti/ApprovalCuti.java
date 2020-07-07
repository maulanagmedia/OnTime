package gmedia.net.id.OnTime.menu_approval_cuti;

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
import java.util.List;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.DialogDataTidakDitemukan;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class ApprovalCuti extends AppCompatActivity {
	private ListAdapterApprovalCuti adapter;
	private ListView listView;
	private List<ModelApprovalCuti> list;
	private List<ModelApprovalCuti> moreList;
	private Proses proses;
	private boolean isLoading = false;
	private int startIndex = 0;
	private int count = 5;
	private View footerList;
	private DialogGagal dialogGagal;
	private DialogDataTidakDitemukan dialogDataTidakDitemukan;
	private LinearLayout tableApprovalCuti;
	private TextView txtKosong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approval_cuti);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Approval Cuti");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setElevation(0);
		proses = new Proses(ApprovalCuti.this);
		LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerList = li.inflate(R.layout.footer_list, null);
		initUI();
		initAction();
	}

	private void initUI() {
		txtKosong = (TextView) findViewById(R.id.txtKosongAppCuti);
		listView = (ListView) findViewById(R.id.lvApprovalCuti);
		tableApprovalCuti = (LinearLayout) findViewById(R.id.tableApprovalCuti);
	}

	private void initAction() {
		prepareDataApprovalCuti();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startIndex = 0;
		count = 5;
		prepareDataApprovalCuti();
	}

	private void prepareDataApprovalCuti() {
		proses.ShowDialog();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("id", "");
			jBody.put("start", String.valueOf(startIndex));
			jBody.put("count", String.valueOf(count));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(ApprovalCuti.this, jBody, "POST", LinkURL.listApprovalCuti, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				isLoading = false;
				proses.DismissDialog();
				list = new ArrayList<>();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {
							JSONObject isi = response.getJSONObject(i);
							list.add(new ModelApprovalCuti(
									isi.getString("nama"),
									isi.getString("awal"),
									isi.getString("akhir"),
									isi.getString("alasan"),
									isi.getString("id")
							));
						}
						txtKosong.setVisibility(View.GONE);
						listView.setAdapter(null);
						adapter = new ListAdapterApprovalCuti(ApprovalCuti.this, list);
						listView.setAdapter(adapter);
						tableApprovalCuti.setVisibility(View.VISIBLE);
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
						tableApprovalCuti.setVisibility(View.GONE);
						dialogDataTidakDitemukan = new DialogDataTidakDitemukan(ApprovalCuti.this);
						dialogDataTidakDitemukan.ShowDialog();
					} else {
//						Toast.makeText(ApprovalCuti.this, message, Toast.LENGTH_LONG).show();
						tableApprovalCuti.setVisibility(View.GONE);
						DialogGagal.message = message;
						dialogGagal = new DialogGagal(ApprovalCuti.this);
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
				Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void getMoreData() {
		isLoading = true;
//		moreList=new ArrayList<>();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("id", "");
			jBody.put("start", String.valueOf(startIndex));
			jBody.put("count", String.valueOf(count));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(ApprovalCuti.this, jBody, "POST", LinkURL.listApprovalCuti, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				moreList = new ArrayList<>();
				listView.removeFooterView(footerList);
				JSONObject object = null;
				try {
					object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONArray response = object.getJSONArray("response");
						for (int i = 0; i < response.length(); i++) {
							JSONObject isi = response.getJSONObject(i);
							list.add(new ModelApprovalCuti(
									isi.getString("nama"),
									isi.getString("awal"),
									isi.getString("akhir"),
									isi.getString("keterangan"),
									isi.getString("id")

							));
						}
						isLoading = false;
						if (adapter != null) adapter.addMoreData(moreList);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				proses.DismissDialog();
				Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
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
