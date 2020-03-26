package gmedia.net.id.OnTime.menu_pengajuan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gmedia.net.id.OnTime.menu_history_cuti.HistoryCuti;
import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.DialogSukses;
import gmedia.net.id.OnTime.utils.HideKeyboard;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class Fragment_cuti extends Fragment {
	private View view;
	private Context context;
	private ImageView btnTglMulai, btnTglAkhir;
	private TextView txtTglMulai, txtTglAkhir, txtTotalCuti;
	private RelativeLayout btnHistoryCuti;
	private EditText alasan;
	private Button btnKirim;
	private Proses proses;
	private String awal, akhir;
	private LinearLayout layoutUtama;
	private DialogSukses dialogSukses;
	private DialogGagal dialogGagal;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_cuti, container, false);
		context = getContext();
		proses = new Proses(context);
		dialogSukses = new DialogSukses(context);
		initUI();
		initAction();
		return view;
	}

	private void initUI() {
		btnTglMulai = (ImageView) view.findViewById(R.id.btnTglMulaiCuti);
		btnTglAkhir = (ImageView) view.findViewById(R.id.btnTglAkhirCuti);
		txtTglMulai = (TextView) view.findViewById(R.id.txtTglMulaiCuti);
		txtTglAkhir = (TextView) view.findViewById(R.id.txtTglAkhirCuti);
		btnHistoryCuti = (RelativeLayout) view.findViewById(R.id.btnHistoryCuti);
		alasan = (EditText) view.findViewById(R.id.ETalasanCuti);
		btnKirim = (Button) view.findViewById(R.id.btnKirimCuti);
		layoutUtama = (LinearLayout) view.findViewById(R.id.layoutCuti);
		txtTotalCuti = (TextView) view.findViewById(R.id.txtTotalCuti);
	}

	private void initDefaultDate() {
		Date c = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		String formattedDate = df.format(c);
		txtTglMulai.setText(formattedDate);
		txtTglAkhir.setText(formattedDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		awal = sdf.format(c);
		akhir = sdf.format(c);
	}

	private void initAction() {
		prepareDataSisaCuti();
		initDefaultDate();
		layoutUtama.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				HideKeyboard.hideSoftKeyboard(getActivity());
			}
		});
		btnHistoryCuti.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, HistoryCuti.class);
				((Activity) context).startActivity(intent);
			}
		});
		btnTglMulai.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final java.util.Calendar customDate = java.util.Calendar.getInstance();
				DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						customDate.set(java.util.Calendar.YEAR, year);
						customDate.set(java.util.Calendar.MONTH, month);
						customDate.set(java.util.Calendar.DATE, dayOfMonth);
						SimpleDateFormat sdFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
						txtTglMulai.setText(sdFormat.format(customDate.getTime()));
						txtTglMulai.setAlpha(1);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
						awal = sdf.format(customDate.getTime());
					}
				};
				new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
			}
		});
		btnTglAkhir.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final java.util.Calendar customDate = java.util.Calendar.getInstance();
				DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						customDate.set(java.util.Calendar.YEAR, year);
						customDate.set(java.util.Calendar.MONTH, month);
						customDate.set(java.util.Calendar.DATE, dayOfMonth);
						SimpleDateFormat sdFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
						txtTglAkhir.setText(sdFormat.format(customDate.getTime()));
						txtTglAkhir.setAlpha(1);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
						akhir = sdf.format(customDate.getTime());
					}
				};
				new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
			}
		});
		btnKirim.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (txtTglMulai.getText().toString().equals("")) {
					Toast.makeText(context, "Silahkan Pilih Tanggal Mulai", Toast.LENGTH_LONG).show();
					return;
				} else if (txtTglAkhir.getText().toString().equals("")) {
					Toast.makeText(context, "Silahkan Pilih Tanggal Akhir", Toast.LENGTH_LONG).show();
					return;
				} else if (alasan.getText().toString().equals("")) {
					alasan.setError("Mohon Di Isi");
					alasan.requestFocus();
					return;
				} else {
					prepareDataAddCuti();
				}
			}
		});
	}

	private void prepareDataSisaCuti() {
		proses.ShowDialog();
		ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", LinkURL.sisaCuti, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						JSONObject response = object.getJSONObject("response");
						txtTotalCuti.setText(response.getString("jumlah"));
					} else {
//						Toast.makeText(context, message, Toast.LENGTH_LONG).show();
						DialogGagal.message = message;
						dialogGagal = new DialogGagal(context);
						dialogGagal.ShowDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				proses.DismissDialog();
				Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void prepareDataAddCuti() {

		proses.ShowDialog();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("datestart", awal);
			jBody.put("dateend", akhir);
			jBody.put("alasan", alasan.getText().toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(context, jBody, "POST", LinkURL.addCuti, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {

				proses.DismissDialog();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
//						Toast.makeText(context, message, Toast.LENGTH_LONG).show();
						dialogSukses.ShowDialog();
						initDefaultDate();
						alasan.setText("");
					} else {
//						Toast.makeText(context, message, Toast.LENGTH_LONG).show();
						DialogGagal.message = message;
						dialogGagal = new DialogGagal(context);
						dialogGagal.ShowDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				proses.DismissDialog();
				Toast.makeText(context, result, Toast.LENGTH_LONG).show();
			}
		});
	}
}
