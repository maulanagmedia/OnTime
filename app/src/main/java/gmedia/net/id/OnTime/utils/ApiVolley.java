package gmedia.net.id.OnTime.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import gmedia.net.id.OnTime.R;


/**
 * Created by Shin on 2/24/2017.
 */

public class ApiVolley {

    public static RequestQueue requestQueue;
    private SessionManager session;
    private Context context;

    public ApiVolley(final Context context, JSONObject jsonBody, String requestMethod, String REST_URL, final String successDialog, final String failDialog, final int showDialogFlag, final VolleyCallback callback) {

        /*
        context : Application context
        jsonBody : jsonBody (usually be used for POST and PUT)
        requestMethod : GET, POST, PUT, DELETE
        REST_URL : Rest API URL
        successDialog : custom Dialog when success call API
        failDialog : custom Dialog when failed call API
        showDialogFlag : 1 = show successDialog / failDialog with filter
        callback : return of the response
        */
        this.context = context;
        session = new SessionManager(context);
        final String requestBody = jsonBody.toString();

        int method = 0;

        switch (requestMethod.toUpperCase()) {

            case "GET":
                method = Request.Method.GET;
                break;
            case "POST":
                method = Request.Method.POST;
                break;
            case "PUT":
                method = Request.Method.PUT;
                break;
            case "DELETE":
                method = Request.Method.DELETE;
                break;
            default:
                method = Request.Method.GET;
                break;
        }

        //region initial of stringRequest
        StringRequest stringRequest = new StringRequest(method, REST_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                // Important Note : need to use try catch when parsing JSONObject, no need when parsing string
                try {
                    JSONObject responseAPI = new JSONObject(response);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");
                    responseAPI = null;

                    if (status != null) {
                        /*if (status.equals("0") && message.equals("Unauthorized")) {
                            Toast.makeText(context,"Token Anda Habis",Toast.LENGTH_LONG).show();
                            session.logoutUser();
                        }*/
                        callback.onSuccess(response);
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                    }
                    ShowCustomDialog(context, showDialogFlag, successDialog);
                } catch (Exception e) {

                    e.printStackTrace();
//                     Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    if (showDialogFlag == 3) {
                        callback.onSuccess(response);
                    } else {
                        Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = error.toString();
                if (error instanceof NetworkError) {
                    message = context.getResources().getString(R.string.msg_connection_issue);
                } else if (error instanceof ServerError) {
                    message = context.getResources().getString(R.string.msg_server_issue);
                } else if (error instanceof AuthFailureError) {
                    message = context.getResources().getString(R.string.msg_auth_issue);
                } else if (error instanceof ParseError) {
                    message = context.getResources().getString(R.string.msg_parsing_issue);
                } else if (error instanceof NoConnectionError) {
                    message = context.getResources().getString(R.string.msg_noinet_issue);
                } else if (error instanceof TimeoutError) {
                    message = context.getResources().getString(R.string.msg_timeout_issue);
                }
                ShowCustomDialog(context, showDialogFlag, failDialog);
                callback.onError(error.toString());
                return;
            }
        }) {

            // Request Header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Client-Service", "gmedia-client");
                headers.put("Auth-Key", "absensi-client");
                headers.put("Content-Type", "application/json");
                headers.put("User-Id", session.getKeyUserId());
                headers.put("Token", session.getKeyToken());
                headers.put("Id-Karyawan", session.getKeyIdKaryawan());
                headers.put("Id-Company", session.getKeyIdCompany());
                headers.put("Id-User", session.getKeyIdUser());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
        };

        trustAllCertivicate();

        //endregion
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        // retry when timeout
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
        requestQueue.getCache().clear();

    }

    // interface for call callback from response API
    public interface VolleyCallback {
        void onSuccess(String result);

        void onError(String result);
    }

    public void ShowCustomDialog(Context context, int flag, String message) {
        if (flag == 1) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    private void trustAllCertivicate() {

        updateAndroidSecurityProvider((Activity) context);

        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equalsIgnoreCase("reports.crashlytics.com") ||
                            hostname.equalsIgnoreCase("absenontime.com") ||
                            hostname.equalsIgnoreCase("api.ipify.org") ||
                            hostname.equalsIgnoreCase("erpsmg.gmedia.id") ||
                            hostname.equalsIgnoreCase("api.crashlytics.com") ||
                            hostname.equalsIgnoreCase("settings.crashlytics.com") ||
                            hostname.equalsIgnoreCase("clients4.google.com") ||
                            hostname.equalsIgnoreCase("www.facebook.com") ||
                            hostname.equalsIgnoreCase("www.instagram.com") ||
                            hostname.equalsIgnoreCase("lh1.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh2.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh3.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh4.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh5.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh6.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh7.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh8.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("lh9.googleusercontent.com") ||
                            hostname.equalsIgnoreCase("googleusercontent.com") ||
                            hostname.equalsIgnoreCase("fbcdn.net") ||
                            hostname.equalsIgnoreCase("scontent.xx.fbcdn.net") ||
                            hostname.equalsIgnoreCase("lookaside.facebook.com")) {
                        return true;
                    } else {
                        return false;
                    }
                }});

            // SSL Tipe TLS
            SSLContext context = SSLContext.getInstance("TLS");

            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(callingActivity);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            //Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
