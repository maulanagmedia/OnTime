package gmedia.net.id.OnTime;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.OnTime.menu_approval_cuti.ApprovalCuti;
import gmedia.net.id.OnTime.menu_approval_ijin.ApprovalIjin;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.RuntimePermissionsActivity;

public class MainActivityBaru extends RuntimePermissionsActivity {
	private Fragment fragment = null;
	public static Boolean isHome = true;
	private static final int REQUEST_PERMISSIONS = 20;
	private String token = "", notifOnPlay = "", notifOnDead = "";
	public static int stateNotif = 0;
	private boolean dialogActive = false;
	private String version, latestVersion, link;
	private boolean updateRequired;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_baru);
		dialogActive = false;
		if (android.os.Build.VERSION.SDK_INT > 25) {
			statusCheck();
		}
		FirebaseApp.initializeApp(MainActivityBaru.this);
		FirebaseMessaging.getInstance().subscribeToTopic("ontime");
		token = FirebaseInstanceId.getInstance().getToken();
		try {
			Log.d("token", token);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("token", e.getMessage());
		}
		initPermission();
		if (fragment == null) {
			fragment = new DashboardBaru();
			callFragment(fragment);
		}
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			notifOnDead = bundle.getString("jenis", "");
			notifOnPlay = bundle.getString("notif", "");
			stateNotif = 1;
			/*if (notifOnPlay == null) {

			} else {
				stateNotif = 1;
				Intent intent = new Intent(this, Open_front_camera.class);
				startActivity(intent);
			}*/
		}
		/*if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		}*/
	}

	private void initPermission() {
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED /*||
				ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED*/) {

			super.requestAppPermissions(new String[]{
							Manifest.permission.CAMERA,
							Manifest.permission.INTERNET,
							Manifest.permission.READ_EXTERNAL_STORAGE,
							Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.ACCESS_COARSE_LOCATION,
							Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_WIFI_STATE,
							Manifest.permission.CHANGE_WIFI_STATE,
							Manifest.permission.READ_PHONE_STATE
//							Manifest.permission.RECEIVE_BOOT_COMPLETED
					},
					R.string.runtime_permissions_txt, REQUEST_PERMISSIONS);
		}
	}

	private void callFragment(Fragment fragment) {
		getSupportFragmentManager()
				.beginTransaction()
//                .setCustomAnimati ons(R.anim.fade_in, R.anim.fade_out_animation)
				.replace(R.id.changeLayout, fragment, fragment.getClass().getSimpleName())
				.addToBackStack(null)
				.commit();
	}


	@Override
	public void onBackPressed() {
		if (!isHome) {
			isHome = true;
			/*fragment = new DashboardBaru();
			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.anim.fade_out_animation, R.anim.fade_out_animation)
					.replace(R.id.changeLayout, fragment, fragment.getClass().getSimpleName())
					.addToBackStack(null)
					.commit();*/
		} else {
			preparePopUpExit();
		}
	}

	private void preparePopUpExit() {
		final Dialog dialogExit = new Dialog(MainActivityBaru.this);
		dialogExit.setContentView(R.layout.popup_exit);
		dialogExit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogExit.setCanceledOnTouchOutside(false);
		Button ya = (Button) dialogExit.findViewById(R.id.btnYa);
		Button tidak = (Button) dialogExit.findViewById(R.id.btnTidak);
		ya.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.putExtra("EXIT", true);
				startActivity(intent);
				finish();
				System.exit(0);

			}
		});
		tidak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialogExit.dismiss();
			}
		});
		dialogExit.show();
	}

	@Override
	public void onPermissionsGranted(int requestCode) {

	}

	/*@Override
	protected void onStart() {
		super.onStart();
		fragment = new DashboardBaru();
		callFragment(fragment);
	}*/

	@Override
	protected void onResume() {
		super.onResume();
		checkVersion();
		/*if (fragment == null) {
			fragment = new DashboardBaru();
			callFragment(fragment);
		} else {
			callFragment(fragment);
		}*/
		FirebaseApp.initializeApp(MainActivityBaru.this);
		if (stateNotif == 1) {
			stateNotif = 0;
			if (notifOnDead.equals("cuti") || notifOnPlay.equals("cuti")) {
				isHome = true;
				Intent intent = new Intent(MainActivityBaru.this, ApprovalCuti.class);
				startActivity(intent);
			} else if (notifOnDead.equals("ijin") || notifOnPlay.equals("ijin")) {
				isHome = true;
				Intent intent = new Intent(MainActivityBaru.this, ApprovalIjin.class);
				startActivity(intent);
			} else {
				isHome = true;
				Intent intent = new Intent(MainActivityBaru.this, MainActivityBaru.class);
				startActivity(intent);
			}
		}
		if (android.os.Build.VERSION.SDK_INT > 25) {
			statusCheck();
		}
	}

	private void checkVersion() {

		PackageInfo pInfo = null;
		version = "";

		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		version = pInfo.versionName;
//        getSupportActionBar().setSubtitle(getResources().getString(R.string.app_name) + " v "+ version);
		latestVersion = "";
		link = "";

		ApiVolley request = new ApiVolley(MainActivityBaru.this, new JSONObject(), "GET", LinkURL.upVersion, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				JSONObject responseAPI;
				try {
					responseAPI = new JSONObject(result);
					String status = responseAPI.getJSONObject("metadata").getString("status");
					if (status.equals("200")) {
						latestVersion = responseAPI.getJSONObject("response").getString("version");
						link = responseAPI.getJSONObject("response").getString("link");
						updateRequired = ((responseAPI.getJSONObject("response").getString("wajib")).equals("1")) ? true : false;
						if (!version.trim().equals(latestVersion.trim()) && link.length() > 0) {
							final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityBaru.this);
							if (updateRequired) {
								builder.setIcon(R.mipmap.ic_launcher)
										.setTitle("Update")
										.setMessage("Versi terbaru " + latestVersion + " telah tersedia, mohon download versi terbaru.")
										.setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
												startActivity(browserIntent);
											}
										})
										.setCancelable(false)
										.show();
							} else {
								builder.setIcon(R.mipmap.ic_launcher)
										.setTitle("Update")
										.setMessage("Versi terbaru " + latestVersion + " telah tersedia, mohon download versi terbaru.")
										.setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
												startActivity(browserIntent);
											}
										})
										.setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
											}
										}).show();
							}

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
			}
		});
	}

	public void statusCheck() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			buildAlertMessageNoGps();
		}
	}

	private void buildAlertMessageNoGps() {
		if (!dialogActive) {
			dialogActive = true;
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Mohon Hidupkan Akses Lokasi (GPS) Anda.")
					.setCancelable(false)
					.setPositiveButton("Hidupkan", new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					});
			final AlertDialog alert = builder.create();
			alert.show();

			alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialogInterface) {
					dialogActive = false;
				}
			});
		}
	}
}
