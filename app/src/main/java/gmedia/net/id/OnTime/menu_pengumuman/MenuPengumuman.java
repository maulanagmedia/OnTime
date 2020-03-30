package gmedia.net.id.OnTime.menu_pengumuman;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

public class MenuPengumuman extends AppCompatActivity {

	private ListView listView;
	private ArrayList<ModelPengumuman> dataInfoGaji;
	private ListAdapterPengumuman adapter;
	private String tanggal[] =
			{
					"07 September 2019",
					"08 September 2019",
					"09 September 2019",
					"10 September 2019",
					"07 September 2019",
					"08 September 2019"
			};
	private String judul[] =
			{
					"Info BPJS",
					"libur seluruh karyawan",
					"Cuti seluruh karyawan",
					"Info BPJS",
					"libur seluruh karyawan",
					"Cuti seluruh karyawan"
			};
	private Proses proses;
	public static String id = "";
	private DialogGagal dialogGagal;
	private DialogDataTidakDitemukan dialogDataTidakDitemukan;
	private RelativeLayout listPengumuman;
	private TextView textKosong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_pengumuman);
		proses = new Proses(MenuPengumuman.this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Pengumuman");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setElevation(0);
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#18C1FE"));
		actionBar.setBackgroundDrawable(colorDrawable);

		initUI();
		initAction();

		if (android.os.Build.VERSION.SDK_INT >= 21) {
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.colorNew));
		}
	}

	private void initUI() {
		listView = (ListView) findViewById(R.id.lvViewPengumuman);
		listPengumuman = (RelativeLayout) findViewById(R.id.listPengumuman);
		textKosong = (TextView) findViewById(R.id.txtKosong);
	}

	private void initAction() {
//        dataInfoGaji = prepareDataInfoGaji();
		proses.ShowDialog();
		ApiVolley request = new ApiVolley(MenuPengumuman.this, new JSONObject(), "POST", LinkURL.listPengumuman, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
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
						listView.setAdapter(null);
						adapter = new ListAdapterPengumuman(MenuPengumuman.this, dataInfoGaji);
						listView.setAdapter(adapter);
						listPengumuman.setVisibility(View.VISIBLE);
						textKosong.setVisibility(View.GONE);
					} else if (status.equals("404")) {
						listPengumuman.setVisibility(View.GONE);
						textKosong.setVisibility(View.VISIBLE);
						dialogDataTidakDitemukan = new DialogDataTidakDitemukan(MenuPengumuman.this);
						dialogDataTidakDitemukan.ShowDialog();
					} else {
//                        Toast.makeText(MenuPengumuman.this, message, Toast.LENGTH_LONG).show();
						listPengumuman.setVisibility(View.GONE);
						textKosong.setVisibility(View.VISIBLE);
						DialogGagal.message = message;
						dialogGagal = new DialogGagal(MenuPengumuman.this);
						dialogGagal.ShowDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onError(String result) {
				proses.DismissDialog();
				Toast.makeText(MenuPengumuman.this, "terjadi kesalahan", Toast.LENGTH_LONG).show();
			}
		});

	}

   /* private ArrayList<ModelPengumuman> prepareDataInfoGaji() {
        ArrayList<ModelPengumuman> rvData = new ArrayList<>();
        for (int i = 0; i < tanggal.length; i++) {
            ModelPengumuman isiInfoGaji = new ModelPengumuman();
            isiInfoGaji.setTglAwal(tanggal[i]);
            isiInfoGaji.setJudul(judul[i]);
            rvData.add(isiInfoGaji);
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
