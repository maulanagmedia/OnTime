package gmedia.net.id.OnTime.menu_utama;

import android.app.Activity;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.ScanAbsen;
import gmedia.net.id.OnTime.utils.GetLocation;

public class KembaliBekerja extends Activity {
    private View view;
//    private Context context;
    private TextView tanggal, jam, menit;
    private GetLocation getLocation;
    private RelativeLayout btnCheckOut;
    private String tipe_scan = "4";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kembali_bekerja);
		if (android.os.Build.VERSION.SDK_INT >= 21) {
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_red));
		}
		/*if (DashboardBaru.latitude.equals("") || DashboardBaru.longitude.equals("")) {
			getLocation = new GetLocation();
			getLocation.GetLocation(KembaliBekerja.this);
		}*/
		initUI();
		initAction();
	}

	/*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.kembali_bekerja, viewGroup, false);
        context = getContext();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbarkeluar));
        }
        if (DashboardBaru.latitude.equals("") || DashboardBaru.longitude.equals("")) {
            getLocation = new GetLocation();
            getLocation.GetLocation(context);
        }
        initUI();
        initAction();
        return view;
    }*/

    private void initUI() {
//        tanggal = (TextView) findViewById(R.id.txtDinoTanggalKembaliKerja);
        jam = (TextView) findViewById(R.id.txtJamKembaliKerja);
        menit = (TextView) findViewById(R.id.txtMenitKembaliKerja);
        btnCheckOut = (RelativeLayout) findViewById(R.id.tombolCheckOutKembaliKerja);
    }

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
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				ScanAbsen scanAbsen = new ScanAbsen(KembaliBekerja.this,tipe_scan);
                /*if (DashboardBaru.latitude.equals("") || DashboardBaru.longitude.equals("")) {
                    getLocation = new GetLocation();
                    getLocation.GetLocation(KembaliBekerja.this);
                    Toast.makeText(KembaliBekerja.this, "please try again", Toast.LENGTH_LONG).show();
                } else {
                    ScanAbsen scanAbsen = new ScanAbsen(KembaliBekerja.this,tipe_scan);
//                    Toast.makeText(context, "" + DashboardBaru.latitude + " & " + DashboardBaru.longitude, Toast.LENGTH_LONG).show();
//                    Toast.makeText(context,"Berhasil",Toast.LENGTH_LONG).show();
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
