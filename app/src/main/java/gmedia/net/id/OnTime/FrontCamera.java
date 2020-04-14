package gmedia.net.id.OnTime;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.EncodeBitmapToString;
import gmedia.net.id.OnTime.utils.GetImei;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;


public class FrontCamera {
	private static Context context;

	public static byte[] dataBaru;
	private static File saveDirectory;
	private static String folderName = "MyCameraApp";
	private static String filePathURI;
	private static Proses proses;

	public FrontCamera(Context context) {
		this.context = context;
		proses = new Proses(context);
	}

	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = openFrontFacingCamera();
		} catch (Exception e) {
		}
		return c;
	}

	public static Camera openFrontFacingCamera() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					Log.e("error camera", "Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}
		return cam;
	}

	public static Camera.PictureCallback mPicture = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			//input to gallery
			/*File pictureFile = getOutputMediaFile();
			if (pictureFile == null) {
				Log.d("TEST", "Error creating media file, check storage permissions");
				Open_front_camera.safeToTakePicture = true;
				return;
			}*/
			int rotasi = 0;
			/*Camera camera2 = camera;
			Camera.Parameters x = camera2.getParameters();
			Camera.Size pictureSize = x.getPreviewSize();
			if (pictureSize.height < pictureSize.width) {
				rotasi = 180;
			} else {
				rotasi = 180;
			}*/
			if (Preview.rotasiLayar == 0) {
				rotasi = 270;
			} else if (Preview.rotasiLayar == 1) {
				rotasi = 0;
			} else if (Preview.rotasiLayar == 3) {
				rotasi = 180;
			}
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//bitmap asli
			Matrix matrix = new Matrix();
			matrix.postRotate(rotasi);
			Long tsLong = System.currentTimeMillis()/1000;
			String ts = tsLong.toString();
			Uri uri = getImageUri(context, bitmap);
			String namaFile = ts+".JPG";
			/*Cursor returnCursor =
					((Activity) context).getApplication().getContentResolver().query(getImageUri(context, bitmap),
							null, null, null, null);*/
			String pathfile = copyFileFromUri(context, uri, namaFile, matrix);
			try {
				/*ExifInterface exif = new ExifInterface(pathfile);
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
				}*/
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
				if (Preview.rotasiLayar == 0) {
					paint.setTextSize(16);
				} else {
					paint.setTextSize(20);
				}
//                canvas.drawPaint(paint);
				SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - HH:mm:ss");
				String currentDateandTime = sdf.format(new Date());
				canvas.drawText(currentDateandTime, (widthWatermark / 2) + tambahanWidthWatermark, heightWatermark - 20, paint);
				ByteArrayOutputStream blob = new ByteArrayOutputStream();
				mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob);
				dataBaru = blob.toByteArray();
				mutableBitmap.recycle();
				try {
					FileOutputStream fos = new FileOutputStream(new File(pathfile));
					fos.write(dataBaru);
//					Open_front_camera.dataBaru = new byte[dataBaru.length];
//					System.arraycopy(dataBaru, 0, Open_front_camera.dataBaru, 0, dataBaru.length);
//					dataNew(dataBaru);
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.d("kamera", e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					Log.d("kamera", e.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("kamera", e.getMessage());
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

			Open_front_camera.afterSnapCamera = true;
			if (Open_front_camera.afterSnapCamera) {
				Absen();
			}
			/*Open_front_camera.backgroundBtnTakePicture.setVisibility(View.GONE);
			Open_front_camera.layoutBerhasilAbsen.setVisibility(View.VISIBLE);
			Open_front_camera.layoutBerhasilAbsen.startAnimation(Open_front_camera.animation);*/
			Log.d("TEST", "File created");
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
			Open_front_camera.safeToTakePicture = true;

		}
	};

	public static void Absen() {
		proses.ShowDialog();
		if (Open_front_camera.menuFrom.equals("sampai tujuan")) {
			Open_front_camera.tipe_scan = "7";
			Bitmap bitmapBaru;
			bitmapBaru = BitmapFactory.decodeByteArray(FrontCamera.dataBaru, 0, FrontCamera.dataBaru.length);
			JSONArray jsonArray = new JSONArray(GetImei.getIMEI(context));
			final JSONObject jBody = new JSONObject();
			try {
				jBody.put("foto", EncodeBitmapToString.convert(bitmapBaru));
				jBody.put("latitude", Open_front_camera.latitude);
				jBody.put("longitude", Open_front_camera.longitude);
				jBody.put("tipe_scan", Open_front_camera.tipe_scan);
				jBody.put("imei", jsonArray);
				jBody.put("ip_public", Open_front_camera.infoIpPublic);
				jBody.put("mac_address", Open_front_camera.infoBSSID);
//				jBody.put("imei",imei);
				/*if (!infoIpPublic.equals("")) {
					jBody.put("ip_public", infoIpPublic);
				}
				if (!infoBSSID.equals("")) {
					jBody.put("mac_address", infoBSSID);
				}*/
			} catch (JSONException e) {
				e.printStackTrace();
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

							//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
							final Dialog dialog = new Dialog(context);
							dialog.setContentView(R.layout.popup_sukses);
							dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
							RelativeLayout btnOk = (RelativeLayout) dialog.findViewById(R.id.btnOkPopupSukses);
							btnOk.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									dialog.dismiss();
									Open_front_camera.backgroundBtnTakePicture.setVisibility(View.GONE);
									Open_front_camera.layoutBerhasilAbsen.setVisibility(View.VISIBLE);
									Open_front_camera.layoutBerhasilAbsen.startAnimation(Open_front_camera.animation);

								}
							});
							dialog.setCanceledOnTouchOutside(false);
							dialog.show();

						} else {
							//Munculkan pesan gagal
							//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.popup_gagal_custom);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            TextView messageError = (TextView) dialog.findViewById(R.id.txtMessageError);
                            TextView tvPesan = (TextView) dialog.findViewById(R.id.tv_pesan);
                            tvPesan.setText("Ok");
                            messageError.setText(message);
                            RelativeLayout btnUlangi = (RelativeLayout) dialog.findViewById(R.id.btnUlangiLagiPopupGagalCustom);
                            btnUlangi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Open_front_camera.backgroundBtnTakePicture.setVisibility(View.GONE);
                                    Open_front_camera.layoutGagalAbsen.setVisibility(View.VISIBLE);
                                    Open_front_camera.layoutGagalAbsen.startAnimation(Open_front_camera.animation);
                                }
                            });
							dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
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

//                    Toast.makeText(Open_front_camera.this, "" + latitude + " & " + longitude, Toast.LENGTH_LONG).show();
//			Toast.makeText(Open_front_camera.this, "Berhasil", Toast.LENGTH_LONG).show();
			Log.d("location", "" + Open_front_camera.latitude + " & " + Open_front_camera.longitude);
			/*Intent intent = new Intent(context, MainActivityBaru.class);
			((Activity) context).startActivity(intent);
			((Activity) context).finish();*/

		} else if (Open_front_camera.menuFrom.equals("pindah tujuan")) {
			Open_front_camera.tipe_scan = "8";
//                    Bitmap bitmapPindahTujuan = BitmapFactory.decodeByteArray(FrontCamera.dataBaru, 0, FrontCamera.dataBaru.length);
			Bitmap bitmapBaru;
			bitmapBaru = BitmapFactory.decodeByteArray(FrontCamera.dataBaru, 0, FrontCamera.dataBaru.length);
			JSONArray jsonArray = new JSONArray(GetImei.getIMEI(context));
			final JSONObject jBody = new JSONObject();
			try {
				jBody.put("foto", EncodeBitmapToString.convert(bitmapBaru));
				jBody.put("latitude", Open_front_camera.latitude);
				jBody.put("longitude", Open_front_camera.longitude);
				jBody.put("tipe_scan", Open_front_camera.tipe_scan);
				jBody.put("imei", jsonArray);
				jBody.put("ip_public", Open_front_camera.infoIpPublic);
				jBody.put("mac_address", Open_front_camera.infoBSSID);
			} catch (JSONException e) {
				e.printStackTrace();
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

							//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
							final Dialog dialog = new Dialog(context);
							dialog.setContentView(R.layout.popup_sukses);
							dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
							RelativeLayout btnOk = (RelativeLayout) dialog.findViewById(R.id.btnOkPopupSukses);
							btnOk.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									dialog.dismiss();
									Open_front_camera.backgroundBtnTakePicture.setVisibility(View.GONE);
									Open_front_camera.layoutBerhasilAbsen.setVisibility(View.VISIBLE);
									Open_front_camera.layoutBerhasilAbsen.startAnimation(Open_front_camera.animation);

								}
							});
							dialog.setCanceledOnTouchOutside(false);
							dialog.show();
						} else {
                            //Munculkan pesan gagal
                            //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.popup_gagal_custom);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            TextView messageError = (TextView) dialog.findViewById(R.id.txtMessageError);
                            TextView tvPesan = (TextView) dialog.findViewById(R.id.tv_pesan);
                            tvPesan.setText("Ok");
                            messageError.setText(message);
                            RelativeLayout btnUlangi = (RelativeLayout) dialog.findViewById(R.id.btnUlangiLagiPopupGagalCustom);
                            btnUlangi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Open_front_camera.backgroundBtnTakePicture.setVisibility(View.GONE);
							        Open_front_camera.layoutGagalAbsen.setVisibility(View.VISIBLE);
							        Open_front_camera.layoutGagalAbsen.startAnimation(Open_front_camera.animation);
                                }
                            });
                            dialog.show();
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
//                    Toast.makeText(Open_front_camera.this, "" + latitude + " & " + longitude, Toast.LENGTH_LONG).show();
//			Toast.makeText(Open_front_camera.this, "Berhasil", Toast.LENGTH_LONG).show();
			Log.d("location", "" + Open_front_camera.latitude + " & " + Open_front_camera.longitude);
			/*Intent intent = new Intent(context, MainActivityBaru.class);
			((Activity) context).startActivity(intent);
			((Activity) context).finish();*/
		}
	}

	public static byte[] dataNew(byte[] data) {
		return dataBaru = data;
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

	private static String copyFileFromUri(Context context, Uri fileUri, String namaFile, Matrix matrix) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		String extension = namaFile.substring(namaFile.lastIndexOf("."));
		FileOutputStream out = null;
		String hasil = "";

		try {
			ContentResolver content = context.getContentResolver();
			inputStream = content.openInputStream(fileUri);
			File root = Environment.getExternalStorageDirectory();
			if (root == null) {
				//Log.d(TAG, "Failed to get root");
			}
			// create a directory
			saveDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + folderName + File.separator);
			// create direcotory if it doesn't exists
			saveDirectory.mkdirs();
			final int time = (int) (new Date().getTime() / 1000);
			extension = extension.toLowerCase();
			Bitmap bm2 = null;
			if (extension.equals(".jpeg") || extension.equals(".jpg") || extension.equals(".png") || extension.equals(".bmp")) {

				outputStream = new FileOutputStream(saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
				bm2 = BitmapFactory.decodeStream(inputStream);
				int scale = 80;

				int imageHeight = bm2.getHeight();
				int imageWidth = bm2.getWidth();

				int newWidth = 0;
				int newHeight = 0;

				if (imageHeight > imageWidth) {

					newWidth = 640;
					newHeight = newWidth * imageHeight / imageWidth;
				} else {

					newHeight = 640;
					newWidth = newHeight * imageWidth / imageHeight;
				}

				bm2 = Bitmap.createScaledBitmap(bm2, newWidth, newHeight, false);

				if (matrix != null) {

					bm2 = Bitmap.createBitmap(bm2, 0, 0, bm2.getWidth(), bm2.getHeight(), matrix, true);
				}

				bm2.compress(Bitmap.CompressFormat.JPEG, scale, outputStream);

				File file = new File(saveDirectory, time + namaFile);
				//Log.i(TAG, "" + file);
				if (file.exists())
					file.delete();
				try {
					FileOutputStream outstreamBitmap = new FileOutputStream(file);
					bm2.compress(Bitmap.CompressFormat.JPEG, scale, outstreamBitmap);
					outstreamBitmap.flush();
					outstreamBitmap.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {

				outputStream = new FileOutputStream(saveDirectory.getAbsoluteFile() + File.separator + time + namaFile); // filename.png, .mp3, .mp4 ...
				if (outputStream != null) {
					Log.e("kamera", "Output Stream Opened successfully");
				}

				byte[] buffer = new byte[1000];
				int bytesRead = 0;
				while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
					outputStream.write(buffer, 0, buffer.length);
				}
			}

			filePathURI = Environment.getExternalStorageDirectory() + File.separator + folderName + File.separator + time + namaFile;
			hasil = filePathURI;
			/*if (bm2 != null) {

				listPhoto.add(new PhotoModel(filePathURI, "", ImageUtils.convert(bm2)));
				adapterPhoto.notifyDataSetChanged();
			}*/

			//new UploadFileToServer().execute();
		} catch (Exception e) {
			Log.e("kamera", "Exception occurred " + e.getMessage());
		} finally {

		}
		return hasil;
	}

	public static Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
				matrix, true);
	}

	public static Uri getImageUri(Context context, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		Long tsLong = System.currentTimeMillis()/1000;
		String ts = tsLong.toString();
		String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, ts, null);
		return Uri.parse(path);
	}
}
