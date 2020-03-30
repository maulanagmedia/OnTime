package gmedia.net.id.OnTime.menu_gaji;

import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.DialogDataTidakDitemukan;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class MenuInfoGaji extends AppCompatActivity {

	private ImageView btnTgl;
	private TextView txtTgl;
	private Button btnKirim;
	private Spinner menuBulan, menuTahun;
	private String posisiMenuBulan = "", posisiMenuTahun = "";
	private String[] bulan = new String[]
			{
					"Pilih Bulan : ",
					"Januari",
					"Februari",
					"Maret",
					"April",
					"Mei",
					"Juni",
					"Juli",
					"Agustus",
					"September",
					"Oktober",
					"November",
					"Desember"
			};
	private String[] tahun = new String[]
			{
					"" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 3),
					"" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 2),
					"" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1),
					"" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					"" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1),
					"" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 2),
					"" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 3),
			};
	private Proses proses;
	private TextView gajiPokok, total;
	private LinearLayout layoutGajiPokok;
	private DialogGagal dialogGagal;
	private DialogDataTidakDitemukan dialogDataTidakDitemukan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_info_gaji);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Info Gaji");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setElevation(0);
		proses = new Proses(MenuInfoGaji.this);
		initUI();
		initAction();
//		if (android.os.Build.VERSION.SDK_INT >= 21) {
//			Window window = this.getWindow();
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			window.setStatusBarColor(this.getResources().getColor(R.color.color_main));
//		}

	}

	private void initUI() {
//		btnTgl = (ImageView) findViewById(R.id.btnTglInfoGaji);
//		txtTgl = (TextView) findViewById(R.id.txtTglInfoGaji);
		btnKirim = (Button) findViewById(R.id.btnKirimInfoGaji);
		menuBulan = (Spinner) findViewById(R.id.menuBulanInfoGaji);
		menuTahun = (Spinner) findViewById(R.id.menuTahunInfoGaji);
		gajiPokok = (TextView) findViewById(R.id.txtGajiPokokInfoGaji);
		total = (TextView) findViewById(R.id.txtTotalDiterimaInfoGaji);
		layoutGajiPokok = (LinearLayout) findViewById(R.id.layoutGajiPokokInfoGaji);
	}

	private void initAction() {
		ArrayAdapter<String> spinnerAdapterBulan = new ArrayAdapter<String>(MenuInfoGaji.this, R.layout.spinner_items, bulan) {
			@Override
			public boolean isEnabled(int position) {
				if (position == 0) {
					// Disable the first item from Spinner
					// First item will be use for hint
					return false;
				} else {
					return true;
				}
			}

			@Override
			public View getDropDownView(int position, View convertView,
										ViewGroup parent) {
				View view = super.getDropDownView(position, convertView, parent);
				TextView tv = (TextView) view;
				if (position == 0) {
					// Set the hint text color gray
					tv.setTextColor(Color.BLACK);
				} else {
					tv.setTextColor(Color.BLACK);
				}
				return view;
			}
		};
		spinnerAdapterBulan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		menuBulan.setAdapter(spinnerAdapterBulan);
		menuBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				posisiMenuBulan = String.valueOf(menuBulan.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		ArrayAdapter<String> spinnerAdapterTahun = new ArrayAdapter<String>(MenuInfoGaji.this, R.layout.spinner_items, tahun) {
			@Override
			public boolean isEnabled(int position) {
				if (position == 0) {
					// Disable the first item from Spinner
					// First item will be use for hint
					return true;
				} else {
					return true;
				}
			}

			@Override
			public View getDropDownView(int position, View convertView,
										ViewGroup parent) {
				View view = super.getDropDownView(position, convertView, parent);
				TextView tv = (TextView) view;
				if (position == 0) {
					// Set the hint text color gray
					tv.setTextColor(Color.BLACK);
				} else {
					tv.setTextColor(Color.BLACK);
				}
				return view;
			}
		};
		spinnerAdapterTahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		menuTahun.setAdapter(spinnerAdapterTahun);
		menuTahun.setSelection(3);
		menuTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				posisiMenuTahun = String.valueOf(menuTahun.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		/*btnTgl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final java.util.Calendar customDate = java.util.Calendar.getInstance();
				DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						customDate.set(java.util.Calendar.YEAR, year);
						customDate.set(java.util.Calendar.MONTH, month);
						customDate.set(java.util.Calendar.DATE, dayOfMonth);
						SimpleDateFormat sdFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
						txtTgl.setText(sdFormat.format(customDate.getTime()));
						txtTgl.setAlpha(1);
					}
				};
				new DatePickerDialog(MenuInfoGaji.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
			}
		});*/
		btnKirim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (posisiMenuBulan.equals("0")) {
					Toast.makeText(MenuInfoGaji.this, "Silahkan Pilih Bulan Terlebih Dahulu", Toast.LENGTH_LONG).show();
					return;
				} else {
					prepareDataInfoGaji();
				}
//				Toast.makeText(MenuInfoGaji.this, posisiMenuBulan + posisiMenuTahun, Toast.LENGTH_LONG).show();
				/*Intent intent = new Intent(MenuInfoGaji.this, MenuDetailInfoGaji.class);
				startActivity(intent);*/
			}
		});
	}

	private void prepareDataInfoGaji() {
		proses.ShowDialog();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("tgl", "");
			jBody.put("bln", posisiMenuBulan);
			jBody.put("thn", posisiMenuTahun);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(MenuInfoGaji.this, jBody, "POST", LinkURL.infoGaji, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONObject response = object.getJSONObject("response");
						layoutGajiPokok.setVisibility(View.VISIBLE);
						gajiPokok.setText(ChangeToRupiahFormat(response.getString("nominal")));
						total.setText(ChangeToRupiahFormat(response.getString("nominal")));
					} else if (status.equals("404")) {
						layoutGajiPokok.setVisibility(View.GONE);
						dialogDataTidakDitemukan = new DialogDataTidakDitemukan(MenuInfoGaji.this);
						dialogDataTidakDitemukan.ShowDialog();
					} else {
//						Toast.makeText(MenuInfoGaji.this, message, Toast.LENGTH_LONG).show();
						layoutGajiPokok.setVisibility(View.GONE);
						DialogGagal.message = message;
						dialogGagal = new DialogGagal(MenuInfoGaji.this);
						dialogGagal.ShowDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				proses.DismissDialog();
				Toast.makeText(MenuInfoGaji.this, "terjadi kesalahan", Toast.LENGTH_LONG).show();
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

	public Double parseNullDouble(String s) {
		double result = 0;
		if (s != null && s.length() > 0) {
			try {
				result = Double.parseDouble(s);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return result;
	}

	public String doubleToStringFull(Double number) {
		return String.format("%s", number).replace(",", ".");
	}

	public String ChangeToRupiahFormat(String number) {

		double value = parseNullDouble(number);

		NumberFormat format = NumberFormat.getCurrencyInstance();
		DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

		symbols.setCurrencySymbol("Rp ");
		((DecimalFormat) format).setDecimalFormatSymbols(symbols);
		format.setMaximumFractionDigits(0);

		String hasil = String.valueOf(format.format(value));

		return hasil;
	}
}
