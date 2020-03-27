package gmedia.net.id.OnTime.menu_utama;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.ScanAbsen;
import gmedia.net.id.OnTime.utils.GetLocation;
import gmedia.net.id.OnTime.utils.Proses;

public class AbsenMasuk extends Activity {
	private View view;
	private Context context;
	private TextView tanggal, jam, menit;
	private RelativeLayout btnCheckIn;
	private GetLocation getLocation;
	private String tipe_scan = "1";
	private Proses proses;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.absen_masuk);
		proses = new Proses(AbsenMasuk.this);
		if (android.os.Build.VERSION.SDK_INT >= 21) {
			Window window = AbsenMasuk.this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_blue));
		}
		/*if (DashboardBaru.latitude.equals("") || DashboardBaru.longitude.equals("")) {
			getLocation = new GetLocation();
			getLocation.GetLocation(AbsenMasuk.this);
		}*/
		initUI();
		initAction();
	}

	/*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.absen_masuk, viewGroup, false);
		context = getApplicationContext();
		proses = new Proses(context);
		if (DashboardBaru.latitude.equals("") || DashboardBaru.longitude.equals("")) {
			getLocation = new GetLocation();
			getLocation.GetLocation(context);
		}
		initUI();
		initAction();
		return view;
	}*/

	private void initUI() {
//		tanggal = (TextView) findViewById(R.id.txtDinoTanggalMasuk);
		jam = (TextView) findViewById(R.id.txtJamMasuk);
		menit = (TextView) findViewById(R.id.txtMenitMasuk);
		btnCheckIn = (RelativeLayout) findViewById(R.id.tombolCheckIn);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initAction() {
		final Handler handler = new Handler();
		final Runnable r = new Runnable() {
			public void run() {
				long date = System.currentTimeMillis();
				SimpleDateFormat formatTgl = new SimpleDateFormat("dd MMMM yyyy");
				SimpleDateFormat formatJam = new SimpleDateFormat("HH");
				SimpleDateFormat formatMenit = new SimpleDateFormat("mm");
//				tanggal.setText(formatTgl.format(date));
				jam.setText(formatJam.format(date));
				menit.setText(formatMenit.format(date));
				handler.postDelayed(this, 500);
			}
		};
		handler.postDelayed(r, 300);
		btnCheckIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ScanAbsen scanAbsen = new ScanAbsen(AbsenMasuk.this, tipe_scan);
				/*if (DashboardBaru.latitude.equals("") || DashboardBaru.longitude.equals("")) {
					getLocation = new GetLocation();
					getLocation.GetLocation(AbsenMasuk.this);
					Toast.makeText(context, "please try again", Toast.LENGTH_LONG).show();
				} else {
//                    prepareAbsenMasuk();
					ScanAbsen scanAbsen = new ScanAbsen(AbsenMasuk.this, tipe_scan);
//                    Toast.makeText(context, "" + DashboardBaru.latitude + " & " + DashboardBaru.longitude, Toast.LENGTH_LONG).show();
//                    Toast.makeText(context, "Berhasil", Toast.LENGTH_LONG).show();
				}*/
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.no_move, R.anim.fade_out_animation);
	}
}