package gmedia.net.id.OnTime.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.se.omapi.Session;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateLocationService extends Service {

    private static Timer timer = new Timer();
    private Context ctx;
    private int lamaWaktu = 60 * 60 * 1000;
    private double longitude = 0, latitude = 0;
    private final String TAG = "lokasiservice";
    private Handler mHandler;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService() {
        timer.scheduleAtFixedRate(new mainTask(), 0, lamaWaktu);
    }

    private class mainTask extends TimerTask {

        public void run() {

            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }//Call looper.prepare()

            //toastHandler.sendEmptyMessage(0);
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            }

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            Log.d(TAG, "run: "+ String.valueOf(latitude));

            if(String.valueOf(latitude).isEmpty()){

                updateLocation();
            }

            //Looper.loop();
        }
    }

    private void updateLocation() {

        SessionManager session = new SessionManager(ctx);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nip" , session.getKeyIdUser());
            jBody.put("latitude" , String.valueOf(latitude));
            jBody.put("longitude" , String.valueOf(longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(ctx, new JSONObject(), "POST", LinkURL.updateLocation, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {

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

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };
}
