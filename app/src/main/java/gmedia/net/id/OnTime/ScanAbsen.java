package gmedia.net.id.OnTime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.Arrays;

import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.GetImei;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

import static android.content.Context.WIFI_SERVICE;

public class ScanAbsen {
	private Context context;
	private Proses proses;
	private String infoSSID = "", infoBSSID = "", infoIpPublic = "", tipe_scan, imei;
	private WifiManager manager;
	private DialogGagal dialogGagal;

	public ScanAbsen(final Context context, String tipe_scan) {
		this.context = context;
		this.tipe_scan = tipe_scan;
		proses = new Proses(context);
		imei = Arrays.toString(GetImei.getIMEI(context).toArray());
		proses.ShowDialog();
		ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", LinkURL.urlIpPublic, "", "", 3, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				infoIpPublic = Jsoup.parse(result).text();
				manager = (WifiManager) context.getSystemService(WIFI_SERVICE);
				WifiInfo info = manager.getConnectionInfo();
				infoSSID = info.getSSID();
				infoBSSID = info.getBSSID();
				Absen();
			}

			@Override
			public void onError(String result) {
				proses.DismissDialog();
			}
		});

	}

	private void Absen() {
		JSONArray jsonArray = new JSONArray(GetImei.getIMEI(context));
		final JSONObject jBody = new JSONObject();
		try {
			jBody.put("foto", "");
//			jBody.put("latitude", DashboardBaru.latitude);
//			jBody.put("longitude", DashboardBaru.longitude);
			jBody.put("tipe_scan", tipe_scan);
			jBody.put("imei", jsonArray);
			jBody.put("ip_public", infoIpPublic);
			jBody.put("mac_address", infoBSSID);
//			jBody.put("imei", imei);
			/*if (!infoIpPublic.equals("")) {
				jBody.put("ip_public", infoIpPublic);
			}
			if (!infoBSSID.equals("")) {
				jBody.put("mac_address", infoBSSID);
			}*/
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("error", e.getMessage());
		}
		ApiVolley request = new ApiVolley(context, jBody, "POST", LinkURL.ScanAbsen, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.popup_sukses);
						dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						RelativeLayout btnOk = (RelativeLayout) dialog.findViewById(R.id.btnOkPopupSukses);
						btnOk.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.dismiss();
								Intent intent = new Intent(context, MainActivityBaru.class);
								((Activity) context).startActivity(intent);
								((Activity) context).overridePendingTransition(R.anim.no_move, R.anim.fade_out_animation);
								((Activity) context).finish();

							}
						});
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
						final Handler handler = new Handler();
						final Runnable runnable = new Runnable() {
							@Override
							public void run() {
								if (dialog.isShowing()) {
									dialog.dismiss();
								}
							}
						};

						dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								handler.removeCallbacks(runnable);
								Intent intent = new Intent(context, MainActivityBaru.class);
								((Activity) context).startActivity(intent);
								((Activity) context).overridePendingTransition(R.anim.no_move, R.anim.fade_out_animation);
								((Activity) context).finish();

							}
						});
						handler.postDelayed(runnable, 3000);
					} else {
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
