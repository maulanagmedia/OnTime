package gmedia.net.id.OnTime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import gmedia.net.id.OnTime.utils.CustomMapView;
import gmedia.net.id.OnTime.utils.GetImei;
import gmedia.net.id.OnTime.utils.GetLocationAndShowMap;
import gmedia.net.id.OnTime.utils.Proses;

public class Open_front_camera extends AppCompatActivity {
	private FrameLayout frameLayout;
	public static RelativeLayout btnOpenCamera, layoutBerhasilAbsen, layoutGagalAbsen;
	public static ImageView ivRefresh;
	private Preview preview;
	private Camera camera;
	private FrontCamera frontCamera;
	public static boolean afterSnapCamera = false;
	public static boolean safeToTakePicture = false;
	public static Animation animation;
	private GetLocationAndShowMap getLocation;
	public static String latitude = "", longitude = "";
	public static String menuFrom = "";
	public static String tipe_scan = "";
	private Proses proses;
	private Bitmap bitmap;
	private byte[] dataBaru;
	public static String infoSSID = "", infoBSSID = "", infoIpPublic = "", imei;
	private WifiManager manager;
	public static CustomMapView backgroundBtnTakePicture;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//No title bar is set for the activity
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Full screen is set for the Window
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.open_front_camera);
		proses = new Proses(Open_front_camera.this);
		imei = Arrays.toString(GetImei.getIMEI(Open_front_camera.this).toArray());

		initUI();
		savedInstanceState = getIntent().getExtras();
		if (savedInstanceState != null) {
			if (savedInstanceState.getString("absen") != null) {
				menuFrom = savedInstanceState.getString("absen");
				if (menuFrom.equals("sampai tujuan")) {
					btnOpenCamera.setBackgroundDrawable(ContextCompat.getDrawable(Open_front_camera.this, R.drawable.bcg_btn_sampai_tujuan));
				} else if (menuFrom.equals("pindah tujuan")) {
					btnOpenCamera.setBackgroundDrawable(ContextCompat.getDrawable(Open_front_camera.this, R.drawable.bcg_btn_pindah_tujuan));
				}
			}
		}
		Log.d("menuFrom", menuFrom);
		if (latitude.equals("") || longitude.equals("")) {
			getLocation = new GetLocationAndShowMap();
			getLocation.GetLocation(Open_front_camera.this, backgroundBtnTakePicture);
		}
		frontCamera = new FrontCamera(Open_front_camera.this);
		camera = frontCamera.getCameraInstance();
		preview = new Preview(Open_front_camera.this, camera);
		animation = AnimationUtils.loadAnimation(Open_front_camera.this, R.anim.up_from_bottom);
		frameLayout.addView(preview);

		initAction();
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(Open_front_camera.this, MainActivityBaru.class);
				startActivity(intent);
				finish();
				/*proses.ShowDialog();
				ApiVolley request = new ApiVolley(Open_front_camera.this, new JSONObject(), "GET", LinkURL.urlIpPublic, "", "", 3, new ApiVolley.VolleyCallback() {
					@Override
					public void onSuccess(String result) {
						infoIpPublic = Jsoup.parse(result).text();
						manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
						WifiInfo info = manager.getConnectionInfo();
						infoSSID = info.getSSID();
						infoBSSID = info.getBSSID();

					}

					@Override
					public void onError(String result) {

					}
				});*/
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

        /*if (afterSnapCamera) {
            Intent intent = new Intent(Open_front_camera.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
	}

	private void initUI() {
		frameLayout = (FrameLayout) findViewById(R.id.previewCamera);
		btnOpenCamera = (RelativeLayout) findViewById(R.id.btnClick);
		layoutBerhasilAbsen = (RelativeLayout) findViewById(R.id.layoutBerhasilAbsen);
		layoutGagalAbsen = (RelativeLayout) findViewById(R.id.layoutGagalAbsen);
		backgroundBtnTakePicture = (CustomMapView) findViewById(R.id.backgroundBtnTakePicture);
		backgroundBtnTakePicture.onCreate(null);
		backgroundBtnTakePicture.onResume();
		ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
		getLocation = new GetLocationAndShowMap();
		getLocation.GetLocation(Open_front_camera.this, backgroundBtnTakePicture);
//		getLocation.setPointMap();
	}

	private void initAction() {

		btnOpenCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (safeToTakePicture) {
					if (longitude.equals("") || latitude.equals("")) {
						getLocation = new GetLocationAndShowMap();
						getLocation.GetLocation(Open_front_camera.this, backgroundBtnTakePicture);
						Toast.makeText(Open_front_camera.this, "Please Check Your GPS and Try Again", Toast.LENGTH_LONG).show();
						return;
					} else {
						getLocation = new GetLocationAndShowMap();
						getLocation.GetLocation(Open_front_camera.this, backgroundBtnTakePicture);
						Log.d("click", "clicked");
						camera.takePicture(null, null, FrontCamera.mPicture);
						//btnOpenCamera.setClickable(false);

					}
				}
				/*if (afterSnapCamera) {
					Absen();
				}*/
			}
		});

		ivRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				getLocation = new GetLocationAndShowMap();
				getLocation.GetLocation(Open_front_camera.this, backgroundBtnTakePicture);
			}
		});
	}

	/*public void Absen() {
		proses.ShowDialog();
		if (menuFrom.equals("sampai tujuan")) {
			tipe_scan = "7";
			Bitmap bitmapBaru;
			bitmapBaru = BitmapFactory.decodeByteArray(FrontCamera.dataBaru, 0, FrontCamera.dataBaru.length);
			JSONArray jsonArray = new JSONArray(GetImei.getIMEI(Open_front_camera.this));
			final JSONObject jBody = new JSONObject();
			try {
				jBody.put("foto", EncodeBitmapToString.convert(bitmapBaru));
				jBody.put("latitude", latitude);
				jBody.put("longitude", longitude);
				jBody.put("tipe_scan", tipe_scan);
				jBody.put("imei", jsonArray);
				jBody.put("ip_public", infoIpPublic);
				jBody.put("mac_address", infoBSSID);
//				jBody.put("imei",imei);
				*//*if (!infoIpPublic.equals("")) {
					jBody.put("ip_public", infoIpPublic);
				}
				if (!infoBSSID.equals("")) {
					jBody.put("mac_address", infoBSSID);
				}*//*
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ApiVolley request = new ApiVolley(Open_front_camera.this, jBody, "POST", LinkURL.ScanAbsen, "", "", 0, new ApiVolley.VolleyCallback() {
				@Override
				public void onSuccess(String result) {
					proses.DismissDialog();
					try {
						JSONObject object = new JSONObject(result);
						String status = object.getJSONObject("metadata").getString("status");
						String message = object.getJSONObject("metadata").getString("message");
						if (status.equals("200")) {
							backgroundBtnTakePicture.setVisibility(View.GONE);
							layoutBerhasilAbsen.setVisibility(View.VISIBLE);
							layoutBerhasilAbsen.startAnimation(Open_front_camera.animation);
							Toast.makeText(Open_front_camera.this, message, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(Open_front_camera.this, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String result) {
					proses.DismissDialog();
					Toast.makeText(Open_front_camera.this, "terjadi kesalahan", Toast.LENGTH_LONG).show();
				}
			});

//                    Toast.makeText(Open_front_camera.this, "" + latitude + " & " + longitude, Toast.LENGTH_LONG).show();
//			Toast.makeText(Open_front_camera.this, "Berhasil", Toast.LENGTH_LONG).show();
			Log.d("location", "" + latitude + " & " + longitude);
			Intent intent = new Intent(Open_front_camera.this, MainActivityBaru.class);
			startActivity(intent);
			finish();

		} else if (menuFrom.equals("pindah tujuan")) {
			tipe_scan = "8";
//                    Bitmap bitmapPindahTujuan = BitmapFactory.decodeByteArray(FrontCamera.dataBaru, 0, FrontCamera.dataBaru.length);
			Bitmap bitmapBaru;
			bitmapBaru = BitmapFactory.decodeByteArray(FrontCamera.dataBaru, 0, FrontCamera.dataBaru.length);
			JSONArray jsonArray = new JSONArray(GetImei.getIMEI(Open_front_camera.this));
			final JSONObject jBody = new JSONObject();
			try {
				jBody.put("foto", EncodeBitmapToString.convert(bitmapBaru));
				jBody.put("latitude", latitude);
				jBody.put("longitude", longitude);
				jBody.put("tipe_scan", tipe_scan);
				jBody.put("imei", jsonArray);
				jBody.put("ip_public", infoIpPublic);
				jBody.put("mac_address", infoBSSID);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ApiVolley request = new ApiVolley(Open_front_camera.this, jBody, "POST", LinkURL.ScanAbsen, "", "", 0, new ApiVolley.VolleyCallback() {
				@Override
				public void onSuccess(String result) {
					proses.DismissDialog();
					try {
						JSONObject object = new JSONObject(result);
						String status = object.getJSONObject("metadata").getString("status");
						String message = object.getJSONObject("metadata").getString("message");
						if (status.equals("200")) {
							Toast.makeText(Open_front_camera.this, message, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(Open_front_camera.this, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String result) {
					proses.DismissDialog();
					Toast.makeText(Open_front_camera.this, "terjadi kesalahan", Toast.LENGTH_LONG).show();
				}
			});
//                    Toast.makeText(Open_front_camera.this, "" + latitude + " & " + longitude, Toast.LENGTH_LONG).show();
//			Toast.makeText(Open_front_camera.this, "Berhasil", Toast.LENGTH_LONG).show();
			Log.d("location", "" + latitude + " & " + longitude);
			Intent intent = new Intent(Open_front_camera.this, MainActivityBaru.class);
			startActivity(intent);
			finish();
		}
	}*/


	@Override
	protected void onRestart() {
		super.onRestart();
		Intent intent = new Intent(Open_front_camera.this, MainActivityBaru.class);
		startActivity(intent);
		finish();
	}


	public static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}
}
/*exif.setAttribute(ExifInterface.TAG_ORIENTATION, "3");
				exif.saveAttributes();*/

//coba keluaran image setelah rotate
			/*Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			ByteArrayOutputStream blob = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
			dataBaru = blob.toByteArray();
//			mutableBitmap.recycle();
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(pictureFile);
				fos.write(dataBaru);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
//
			/*int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Double widthBaru = (double) 0;
			Double heightBaru = (double) 0;
			if (width > height) {
//				heightBaru = (double) 480;
				heightBaru = (double) 640 / width * height;
				widthBaru = (double) 640;
			} else if (width < height) {
				widthBaru = (double) 640 / height * width;
				heightBaru = (double) 640;
			} else if (width == height) {
				widthBaru = (double) 640;
				heightBaru = (double) 640;
			}
			Log.d("WidthHeight", String.valueOf(widthBaru) + " " + String.valueOf(heightBaru));
			bitmap = Bitmap.createScaledBitmap(bitmap, Integer.valueOf(widthBaru.intValue()), Integer.valueOf(heightBaru.intValue()), true);//resize bitmap
			int widthWatermark = bitmap.getWidth();
			int tambahanWidthWatermark = (widthWatermark / 6) - 30;
			int heightWatermark = bitmap.getHeight();
			Log.d("width & height", "" + widthWatermark + " " + heightWatermark);
			Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                Bitmap mutableBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
			Canvas canvas = new Canvas(mutableBitmap);
			canvas.drawBitmap(bitmap, 0, 0, null);
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#FFFFFF"));
			paint.setStyle(Paint.Style.FILL);
//                paint.setColor(Color.BLACK);
			paint.setTextSize(20);
//                canvas.drawPaint(paint);
			SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - HH:mm:ss");
			String currentDateandTime = sdf.format(new Date());
			canvas.drawText(currentDateandTime, (widthWatermark / 2) + tambahanWidthWatermark, heightWatermark - 20, paint);
			ByteArrayOutputStream blob = new ByteArrayOutputStream();
			mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
			dataBaru = blob.toByteArray();
			mutableBitmap.recycle();*/

//support import to gallery
//			FileOutputStream fos = null;
			/*try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(dataBaru);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/


                /*final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.loading);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if (Open_front_camera.latitude.equals("") || Open_front_camera.longitude.equals("")) {
                    dialog.dismiss();
                    Toast.makeText(context, "" + Open_front_camera.latitude + " & " + Open_front_camera.longitude, Toast.LENGTH_LONG).show();
                    Log.d("location", "" + Open_front_camera.latitude + " & " + Open_front_camera.longitude);
                    return;
                } else {
                    dialog.dismiss();
                    Log.d("location", "" + latitude + " & " + longitude);
                    Toast.makeText(context, "" + latitude + " & " + longitude, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    ((Activity) context).startActivity(intent);
                    ((Activity) context).finish();
                }*/
                /*MainActivity.btnClick.setClickable(false);
                MainActivity.btnClick.setVisibility(View.GONE);
                MainActivity.preview.setVisibility(View.GONE);
                MainActivity.btnClick.startAnimation(MainActivity.animation);
                MainActivity.preview.startAnimation(MainActivity.animation);
                camera.release();
                MainActivity.layoutUtama.setVisibility(View.VISIBLE);
                MainActivity.btn.setVisibility(View.VISIBLE);
                MainActivity.btn.setClickable(true);*/
//                GetLocation getLocation = new GetLocation();
//                getLocation.updateAllLocation();
/*public Camera.PictureCallback mPicture = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			//input to gallery
			try {
				File pictureFile = getOutputMediaFile();
//				File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
//				File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Pictures" + File.separator + "MyCameraApp");
				// This location works best if you want the created images to be shared
				// between applications and persist after your app has been uninstalled.

				// Create the storage directory if it does not exist
				*//*if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						return;
					}
				}
				// Create a media file name
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

				File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
				if (mediaFile == null) {
					Log.d("TEST", "Error creating media file, check storage permissions");
					Open_front_camera.safeToTakePicture = true;
					return;
				}*//*
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//bitmap asli
			*//*Cursor returnCursor =
					((Activity) context).getApplication().getContentResolver().query(getImageUri(context, bitmap),
							null, null, null, null);*//*
				Matrix matrix = new Matrix();
				ExifInterface exif = new ExifInterface(pictureFile.toString());
				int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				float rotationInDegrees = 0;
				switch (rotation) {

					case ExifInterface.ORIENTATION_ROTATE_90:
						rotationInDegrees = 90;
//							bitmap = rotateImage(bitmap, 90);
						break;

					case ExifInterface.ORIENTATION_ROTATE_180:
						rotationInDegrees = 180;
//							bitmap = rotateImage(bitmap, 180);
						break;

					case ExifInterface.ORIENTATION_ROTATE_270:
						rotationInDegrees = 270;
//							bitmap = rotateImage(bitmap, 270);
						break;
				}
//				int rotationInDegrees = iv.exifToDegrees(rotation);
				if (rotation != 0f) {
					matrix.postRotate(rotationInDegrees);
				}
//			matrix.postRotate(270);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				Double widthBaru = (double) 0;
				Double heightBaru = (double) 0;
				if (width > height) {
//				heightBaru = (double) 480;
					heightBaru = (double) 640 / width * height;
					widthBaru = (double) 640;
				} else if (width < height) {
					widthBaru = (double) 640 / height * width;
					heightBaru = (double) 640;
				} else if (width == height) {
					widthBaru = (double) 640;
					heightBaru = (double) 640;
				}
				Log.d("WidthHeight", String.valueOf(widthBaru) + " " + String.valueOf(heightBaru));
				bitmap = Bitmap.createScaledBitmap(bitmap, Integer.valueOf(widthBaru.intValue()), Integer.valueOf(heightBaru.intValue()), true);//resize bitmap
				int widthWatermark = bitmap.getWidth();
				int tambahanWidthWatermark = (widthWatermark / 6) - 30;
				int heightWatermark = bitmap.getHeight();
				Log.d("width & height", "" + widthWatermark + " " + heightWatermark);
				Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                Bitmap mutableBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
				Canvas canvas = new Canvas(mutableBitmap);
				canvas.drawBitmap(bitmap, 0, 0, null);
				Paint paint = new Paint();
				paint.setColor(Color.parseColor("#FFFFFF"));
				paint.setStyle(Paint.Style.FILL);
//                paint.setColor(Color.BLACK);
				paint.setTextSize(20);
//                canvas.drawPaint(paint);
				SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - HH:mm:ss");
				String currentDateandTime = sdf.format(new Date());
				canvas.drawText(currentDateandTime, (widthWatermark / 2) + tambahanWidthWatermark, heightWatermark - 20, paint);
				ByteArrayOutputStream blob = new ByteArrayOutputStream();
				mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
				dataBaru = blob.toByteArray();
				mutableBitmap.recycle();
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(dataBaru);
					*//*Open_front_camera.dataBaru = new byte[dataBaru.length];
					System.arraycopy(dataBaru, 0, Open_front_camera.dataBaru, 0, dataBaru.length);*//*
//					dataNew(dataBaru);
				fos.close();
				afterSnapCamera = true;
				backgroundBtnTakePicture.setVisibility(View.GONE);
				layoutBerhasilAbsen.setVisibility(View.VISIBLE);
				layoutBerhasilAbsen.startAnimation(animation);
				Log.d("TEST", "File created");
				safeToTakePicture = true;

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("kamera", e.getMessage());
			}
		}
	};*/