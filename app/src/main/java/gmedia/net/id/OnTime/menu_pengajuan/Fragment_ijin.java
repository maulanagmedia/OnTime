package gmedia.net.id.OnTime.menu_pengajuan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.menu_history_ijin.HistoryIjin;
import gmedia.net.id.OnTime.menu_reimburse.DetailReimburseActivity;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.DialogSukses;
import gmedia.net.id.OnTime.utils.HideKeyboard;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class Fragment_ijin extends Fragment {
	private View view;
	private Context context;
	private ImageView btnTgl, btnJam;
	private TextView txtTglIjin, txtJamIjin;
	private Spinner dropdown;
	private int posisiDropdown;
	private String tanggal;
	private EditText alasan;
	private Button btnProses;
	private LinearLayout layoutUtama;
	private Proses proses;
	private RelativeLayout historyIjin;
	private DialogSukses dialogSukses;
	private DialogGagal dialogGagal;
	Dialog dialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_ijin, container, false);
		context = getContext();
		proses = new Proses(context);
		dialogSukses = new DialogSukses(context);
		initUI();
		initAction();
		return view;
	}

	private void initUI() {
		btnTgl = (ImageView) view.findViewById(R.id.btnTglIjin);
		btnJam = (ImageView) view.findViewById(R.id.btnJamIjin);
		txtTglIjin = (TextView) view.findViewById(R.id.txtTglIjin);
		txtJamIjin = (TextView) view.findViewById(R.id.txtJamIjin);
		alasan = (EditText) view.findViewById(R.id.ETalasanIjin);
		btnProses = (Button) view.findViewById(R.id.btnKirimIjin);
		layoutUtama = (LinearLayout) view.findViewById(R.id.layoutIjin);
		historyIjin = (RelativeLayout) view.findViewById(R.id.historyIjin);
//        dropdown = (Spinner) view.findViewById(R.id.dropdown);
	}

	private void initDefaultDate() {
		Date c = Calendar.getInstance().getTime();
		SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		String formattedDate = df.format(c);
		txtTglIjin.setText(formattedDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		tanggal = sdf.format(c);
	}

	private void initAction() {
		initDefaultDate();
		layoutUtama.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				HideKeyboard.hideSoftKeyboard(getActivity());
			}
		});
		btnTgl.setOnClickListener(new View.OnClickListener() {
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
						txtTglIjin.setText(sdFormat.format(customDate.getTime()));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
						tanggal = sdf.format(customDate.getTime());
					}
				};
				new DatePickerDialog(context, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
			}
		});
		btnJam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
				int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
				int minute = mcurrentTime.get(java.util.Calendar.MINUTE);
				TimePickerDialog mTimePicker;
				mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
						String jam = String.valueOf(selectedHour);
						String menit = String.valueOf(selectedMinute);
						jam = jam.length() < 2 ? "0" + jam : jam;
						menit = menit.length() < 2 ? "0" + menit : menit;
						txtJamIjin.setText(jam + ":" + menit);
//                        textTimePixer.setAlpha(1);
					}
				}, hour, minute, true);//Yes 24 hour time
				mTimePicker.setTitle("Select Time");
				mTimePicker.show();
			}
		});
		btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (txtTglIjin.getText().toString().equals("")) {
					Toast.makeText(context, "Silahkan pilih tanggal dahulu", Toast.LENGTH_LONG).show();
					return;
				} else if (txtJamIjin.getText().toString().equals("")) {
					Toast.makeText(context, "Silahkan pilih jam dahulu", Toast.LENGTH_LONG).show();
					return;
				} else if (alasan.getText().toString().equals("")) {
					alasan.setError("Silahkan isi terlebih dahulu");
					alasan.requestFocus();
					return;
				} else {
					dialog = new Dialog(context);
					dialog.setContentView(R.layout.popup_reimburse);
					dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					dialog.setCanceledOnTouchOutside(true);
					Button btnTerima = dialog.findViewById(R.id.btn_ya);
					Button btnTolak = dialog.findViewById(R.id.btn_tidak);
					TextView tvMessage = dialog.findViewById(R.id.tv_message);
					tvMessage.setText("Anda yakin ingin mengajukan ijin ?");
					btnTerima.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							prepareDataAddIjin();
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
			}
		});
		historyIjin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, HistoryIjin.class);
				((Activity) context).startActivity(intent);
			}
		});
       /* dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posisiDropdown = parent.getSelectedItemPosition();
                if (position==1){

                }
                else if (position==2){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        String[] items = new String[]{"Jenis Ijin: ", "Ijin Pulang Awal", "Ijin Keluar Kantor"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    dropdown.setAlpha(0.5f);
                    return false;
                } else {
                    dropdown.setAlpha(1);
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);*/
	}

	private void prepareDataAddIjin() {
		proses.ShowDialog();
		JSONObject jBody = new JSONObject();
		try {
			jBody.put("tgl", tanggal);
			jBody.put("jam", txtJamIjin.getText().toString());
			jBody.put("alasan", alasan.getText().toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(context, jBody, "POST", LinkURL.addIjin, "", "", 0, new ApiVolley.VolleyCallback() {
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
				Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
			}
		});
//        Toast.makeText(context, "berhasil", Toast.LENGTH_LONG).show();
	}
}
