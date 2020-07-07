package gmedia.net.id.OnTime.menu_reimburse;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.MediaRouteButton;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.menu_keterlambatan.Keterlambatan;
import gmedia.net.id.OnTime.menu_keterlambatan.ListAdapterKeterlambatan;
import gmedia.net.id.OnTime.menu_keterlambatan.ModelKeterlambatan;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.DialogDataTidakDitemukan;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.DialogSukses;
import gmedia.net.id.OnTime.utils.ImageUtils;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class AddReimburseActivity extends AppCompatActivity {

    ImageView imgTglBayar, imgBukti, imgSearchImage;
    EditText edtNominal, edtKet;
    TextView tvTglBayar;
    String tglBayar;
    private int imageRequestCode = 100;
    private ImageUtils imageUtils = new ImageUtils();
    Bitmap b,decoded;
    private Proses proses;
    private DialogGagal dialogGagal;
    private DialogSukses dialogSukses;
    Button btnKirimReimburse;
    int bitmap_size = 100; // range 1 - 100
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reimburse);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Reimburse");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);

        activity = this;
        tglBayar= "";

        initUi();
    }

    private void initUi(){
        proses = new Proses(AddReimburseActivity.this);
        dialogSukses = new DialogSukses(AddReimburseActivity.this,"");
        imgTglBayar = findViewById(R.id.img_tgl_bayar);
        imgBukti = findViewById(R.id.img_bukti);
        imgSearchImage = findViewById(R.id.img_search_image);
        tvTglBayar = findViewById(R.id.tv_tgl_bayar);
        edtNominal = findViewById(R.id.edt_nominal);
        edtKet = findViewById(R.id.edt_keterangan);
        btnKirimReimburse = findViewById(R.id.btnKirimReimburse);
//        b = ((BitmapDrawable) imgBukti.getDrawable()).getBitmap();

        imgTglBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final java.util.Calendar customDate = java.util.Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        customDate.set(java.util.Calendar.YEAR, year);
                        customDate.set(java.util.Calendar.MONTH, month);
                        customDate.set(java.util.Calendar.DATE, dayOfMonth);
                        SimpleDateFormat sdFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                        tvTglBayar.setText(sdFormat.format(customDate.getTime()));
                        tvTglBayar.setAlpha(1);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        tglBayar = sdf.format(customDate.getTime());
                    }
                };
                new DatePickerDialog(AddReimburseActivity.this, date, customDate.get(java.util.Calendar.YEAR), customDate.get(java.util.Calendar.MONTH), customDate.get(java.util.Calendar.DATE)).show();
            }
        });

        imgSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(imageRequestCode)                                    //Request code for activity results
                        .setCount(1)                                                         //Number of images to restict selection count
                        .setFrontfacing(false)                                               //Front Facing camera on start
                        .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
                        .setPath("/ontime/images");                                             //Custom Path For Image Storage
                Pix.start(AddReimburseActivity.this, options);
            }
        });

        btnKirimReimburse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyimpan?")
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(validasiFormReimburs()){
                                    prepareDataRemburse();
                                }
                            }
                        })
                        .show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            if(returnValue.size() > 0) {

                File f = new File(returnValue.get(0));
                b = new BitmapDrawable(AddReimburseActivity.this.getResources(), f.getAbsolutePath()).getBitmap();
                imageUtils.LoadOriginalRealImage((ImageUtils.getImageUri(AddReimburseActivity.this, b)).toString(), imgBukti);
            }
        }
    }

    private Boolean validasiFormReimburs(){
        if(tglBayar.equals("")){
            Toasty.error(AddReimburseActivity.this, "Masukkan tanggal pembayaran.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        if(b == null){
            Toasty.error(AddReimburseActivity.this, "Masukkan foto bukti pembayaran.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        if (edtNominal.getText().toString().matches("")){
            Toasty.error(AddReimburseActivity.this, "Masukkan nominal pembayaran.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        if (edtKet.getText().toString().matches("")){
            Toasty.error(AddReimburseActivity.this, "Masukkan keterangan.", Toast.LENGTH_SHORT, true).show();
            return false;
        }
        return true;
    }

    @SuppressLint("LogConditional")
    private void prepareDataRemburse() {
        proses.ShowDialog();
        JSONObject jBody = new JSONObject();
        String image= "";
        if(imgBukti.getDrawable() == null){
            image="";
        }else{
            image =ImageUtils.convert(b);
        }

        try {
            jBody.put("tgl_pembayaran", tglBayar);
            jBody.put("foto_pembayaran",image);
            jBody.put("nominal",edtNominal.getText().toString());
            jBody.put("ket",edtKet.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("AddReimburseActivity",String.valueOf(jBody));
        ApiVolley request = new ApiVolley(AddReimburseActivity.this, jBody, "POST", LinkURL.urlPostReimburse, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("AddReimburseActivity",result);
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        dialogSukses.ShowDialog();
                    } else {
                        DialogGagal.message = message;
                        dialogGagal = new DialogGagal(AddReimburseActivity.this);
                        dialogGagal.ShowDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                proses.DismissDialog();
                Toasty.error(AddReimburseActivity.this, "Terjadi Kesalahan.", Toast.LENGTH_SHORT, true).show();
            }
        });
    }


}
