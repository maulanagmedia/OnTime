package gmedia.net.id.OnTime;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.OnTime.menu_approval_cuti.ApprovalCuti;
import gmedia.net.id.OnTime.menu_approval_ijin.ApprovalIjin;
import gmedia.net.id.OnTime.menu_data.MenuData;
import gmedia.net.id.OnTime.menu_gaji.MenuInfoGaji;
import gmedia.net.id.OnTime.menu_keterlambatan.Keterlambatan;
import gmedia.net.id.OnTime.menu_pengajuan.MenuPengajuan;
import gmedia.net.id.OnTime.menu_pengumuman.MenuPengumuman;
import gmedia.net.id.OnTime.menu_reimburse.ReimburseActivity;
import gmedia.net.id.OnTime.menu_scanlog.Scanlog;
import gmedia.net.id.OnTime.menu_utama.AbsenMasuk;
import gmedia.net.id.OnTime.menu_utama.AbsenPulang;
import gmedia.net.id.OnTime.menu_utama.BerhentiLembur;
import gmedia.net.id.OnTime.menu_utama.Istirahat;
import gmedia.net.id.OnTime.menu_utama.KembaliBekerja;
import gmedia.net.id.OnTime.menu_utama.MulaiLembur;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.CircleTransform;
import gmedia.net.id.OnTime.utils.DialogGagal;
import gmedia.net.id.OnTime.utils.DialogSukses;
import gmedia.net.id.OnTime.utils.GetLocation;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;
import gmedia.net.id.OnTime.utils.SessionManager;
import gmedia.net.id.OnTime.utils.UpdateLocationService;

public class DashboardBaru extends Fragment {
	private View view;
	private RelativeLayout btnMenu, btnGantiPassword, menuAbsenMasuk, menuIstirahat, menuMulaiLembur, menuSampaiTujuan, menuAbsenPulang, menuKembaliKerja, menuBerhentiLembur, menuPindahTujuan;
	private Fragment fragment;
	private Context context;
	private GetLocation getLocation;
	public static String latitude = "", longitude = "", token = "";
	public static Boolean isSampaiTujuan = false, isPindahTujuan = false;
	private ImageView fotoProfle;
	private TextView namaProfile, nikProfile;
	private Proses proses;
	private SessionManager session;
	private EditText passLama, passBaru, rePassBaru;
	private Boolean klikToVisiblePassLama = true;
	private Boolean klikToVisiblePassBaru = true;
	private Boolean klikToVisibleRePassBaru = true;
	private DialogSukses dialogSukses;
	private DialogGagal dialogGagal;
	private final  String TAG = "Dashborad";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.dashboard_baru, viewGroup, false);
		context = getContext();
		proses = new Proses(context);
		session = new SessionManager(context);
		dialogSukses = new DialogSukses(context);

		FirebaseApp.initializeApp(context);
		FirebaseMessaging.getInstance().subscribeToTopic("ontime");
		token = FirebaseInstanceId.getInstance().getToken();
		try {
			Log.d("token", token);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("token", e.getMessage());
		}
		if (android.os.Build.VERSION.SDK_INT >= 21) {
			Window window = this.getActivity().getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.color_main));
		}

		/*if (latitude.equals("") || longitude.equals("")) {
			getLocation = new GetLocation();
			getLocation.GetLocation(context);
		}*/
		initUI();
		initAction();
		return view;
	}

	private void initUI() {
		fotoProfle = (ImageView) view.findViewById(R.id.fotoProfile);
		namaProfile = (TextView) view.findViewById(R.id.namaProfile);
		nikProfile = (TextView) view.findViewById(R.id.nikProfile);
		btnMenu = (RelativeLayout) view.findViewById(R.id.menuDashboardBaru);
		btnGantiPassword = (RelativeLayout) view.findViewById(R.id.menuGantiPassword);
		menuAbsenMasuk = (RelativeLayout) view.findViewById(R.id.menuAbsenMasukBaru);
		menuIstirahat = (RelativeLayout) view.findViewById(R.id.menuIstirahatBaru);
		menuMulaiLembur = (RelativeLayout) view.findViewById(R.id.menuMulaiLemburBaru);
		menuSampaiTujuan = (RelativeLayout) view.findViewById(R.id.tombolSampaiTujuan);
		menuAbsenPulang = (RelativeLayout) view.findViewById(R.id.menuAbsenKeluarBaru);
		menuKembaliKerja = (RelativeLayout) view.findViewById(R.id.menuKembaliKerjaBaru);
		menuBerhentiLembur = (RelativeLayout) view.findViewById(R.id.menuBerhentiLemburBaru);
		menuPindahTujuan = (RelativeLayout) view.findViewById(R.id.tombolPindahTujuan);
	}

	private void initAction() {
		prepareDataProfle();
		btnMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final Dialog dialogMenu = new Dialog(context);
				dialogMenu.getWindow().getAttributes().windowAnimations = R.style.animasiDialog;
				dialogMenu.setContentView(R.layout.popup_menu);
				dialogMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				ImageView btnClose = (ImageView) dialogMenu.findViewById(R.id.closePopupMenu);
				btnClose.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialogMenu.dismiss();
					}
				});
				LinearLayout menuData = (LinearLayout) dialogMenu.findViewById(R.id.menuData);
				menuData.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(context, MenuData.class);
						((Activity) context).startActivity(intent);
						dialogMenu.dismiss();
					}
				});
				LinearLayout menuPengumuman = (LinearLayout) dialogMenu.findViewById(R.id.menuPengumuman);
				menuPengumuman.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(context, MenuPengumuman.class);
						((Activity) context).startActivity(intent);
						dialogMenu.dismiss();
					}
				});
				LinearLayout menuApprovalCuti = (LinearLayout) dialogMenu.findViewById(R.id.menuApprovalCuti);
				if (session.getKeyApprovalCuti().equals("1")) {
					menuApprovalCuti.setVisibility(View.VISIBLE);
					menuApprovalCuti.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(context, ApprovalCuti.class);
							((Activity) context).startActivity(intent);
							dialogMenu.dismiss();
						}
					});
				}
				LinearLayout menuApprovalIjin = (LinearLayout) dialogMenu.findViewById(R.id.menuApprovalIjin);
				if (session.getKeyApprovalIjin().equals("1")) {
					menuApprovalIjin.setVisibility(View.VISIBLE);
					menuApprovalIjin.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(context, ApprovalIjin.class);
							((Activity) context).startActivity(intent);
							dialogMenu.dismiss();
						}
					});
				}
				LinearLayout menuPengajuan = (LinearLayout) dialogMenu.findViewById(R.id.menuPengajuan);
				menuPengajuan.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(context, MenuPengajuan.class);
						((Activity) context).startActivity(intent);
						dialogMenu.dismiss();
					}
				});

				LinearLayout menuInfoGaji = (LinearLayout) dialogMenu.findViewById(R.id.menuInfoGaji);
				menuInfoGaji.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(context, MenuInfoGaji.class);
						((Activity) context).startActivity(intent);
						dialogMenu.dismiss();
					}
				});
				LinearLayout menuScanlog = (LinearLayout) dialogMenu.findViewById(R.id.menuScanlog);
				menuScanlog.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(context, Scanlog.class);
						((Activity) context).startActivity(intent);
						dialogMenu.dismiss();
					}
				});
				LinearLayout menuKeterlambatan = (LinearLayout) dialogMenu.findViewById(R.id.menuKeterlambatan);
				menuKeterlambatan.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(context, Keterlambatan.class);
						((Activity) context).startActivity(intent);
						dialogMenu.dismiss();
					}
				});
				LinearLayout menuReimbusement = dialogMenu.findViewById(R.id.menuReimbusement);
				menuReimbusement.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, ReimburseActivity.class);
						((Activity) context).startActivity(intent);
						dialogMenu.dismiss();
					}
				});
				TextView logout = (TextView) dialogMenu.findViewById(R.id.logout);
				logout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						session.logoutUser();
					}
				});
				dialogMenu.setCanceledOnTouchOutside(false);
				dialogMenu.show();
				Window window = dialogMenu.getWindow();
				window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
				if (Build.VERSION.SDK_INT >= 21) {
					window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
					window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						window.setStatusBarColor(dialogMenu.getContext().getColor(R.color.color_main));
					}
				}
			}
		});
		btnGantiPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.popup_ganti_password);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				final ImageView visiblePassLama = dialog.findViewById(R.id.visiblePassLama);
				final ImageView visiblePassBaru = dialog.findViewById(R.id.visiblePassBaru);
				final ImageView visibleRePassBaru = dialog.findViewById(R.id.visibleRePassBaru);
				passLama = dialog.findViewById(R.id.passLama);
				passBaru = dialog.findViewById(R.id.passBaru);
				rePassBaru = dialog.findViewById(R.id.reTypePassBaru);
				visiblePassLama.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (klikToVisiblePassLama) {
							visiblePassLama.setImageDrawable(getResources().getDrawable(R.drawable.visible));
							passLama.setInputType(InputType.TYPE_CLASS_TEXT);
							klikToVisiblePassLama = false;
						} else {
							visiblePassLama.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
							passLama.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
							passLama.setTransformationMethod(PasswordTransformationMethod.getInstance());
							klikToVisiblePassLama = true;
						}
					}
				});
				visiblePassBaru.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (klikToVisiblePassBaru) {
							visiblePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.visible));
							passBaru.setInputType(InputType.TYPE_CLASS_TEXT);
							klikToVisiblePassBaru = false;
						} else {
							visiblePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
							passBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
							passBaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
							klikToVisiblePassBaru = true;
						}
					}
				});
				visibleRePassBaru.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (klikToVisibleRePassBaru) {
							visibleRePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.visible));
							rePassBaru.setInputType(InputType.TYPE_CLASS_TEXT);
							klikToVisibleRePassBaru = false;
						} else {
							visibleRePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
							rePassBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
							rePassBaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
							klikToVisibleRePassBaru = true;
						}
					}
				});
				RelativeLayout OK = dialog.findViewById(R.id.tombolOKgantiPassword);
				OK.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						// validasi
						if (passLama.getText().toString().isEmpty()) {
							passLama.setError("Password lama harap diisi");
							passLama.requestFocus();
							return;
						} else {
							passLama.setError(null);
						}

						if (passBaru.getText().toString().isEmpty()) {
							passBaru.setError("Password baru harap diisi");
							passBaru.requestFocus();
							return;
						} else {
							passBaru.setError(null);
						}

						if (rePassBaru.getText().toString().isEmpty()) {
							rePassBaru.setError("Password baru ulang harap diisi");
							rePassBaru.requestFocus();
							return;
						} else {
							rePassBaru.setError(null);
						}
						if (!rePassBaru.getText().toString().equals(passBaru.getText().toString())) {
							rePassBaru.setError("Password ulang tidak sama");
							rePassBaru.requestFocus();
							return;
						} else {
							rePassBaru.setError(null);
						}
						prepareDataGantiPassword();
						dialog.dismiss();
					}
				});
				RelativeLayout cancel = dialog.findViewById(R.id.tombolcancelGantiPassword);
				cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		});
		menuAbsenMasuk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                /*MainActivityBaru.isHome = false;
                fragment = new AbsenMasuk();
                callFragment(fragment);*/
				Intent intent = new Intent(context, AbsenMasuk.class);
				((Activity) context).startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.no_move);
			}
		});

		menuIstirahat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*MainActivityBaru.isHome = false;
				fragment = new Istirahat();
				callFragment(fragment);*/
				Intent intent = new Intent(context, Istirahat.class);
				((Activity) context).startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.no_move);
			}
		});
		menuMulaiLembur.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*MainActivityBaru.isHome = false;
				fragment = new MulaiLembur();
				callFragment(fragment);*/
				Intent intent = new Intent(context, MulaiLembur.class);
				((Activity) context).startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.no_move);
			}
		});
		menuAbsenPulang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*MainActivityBaru.isHome = false;
				fragment = new AbsenPulang();
				callFragment(fragment);*/
				Intent intent = new Intent(context, AbsenPulang.class);
				((Activity) context).startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.no_move);
			}
		});
		menuKembaliKerja.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*MainActivityBaru.isHome = false;
				fragment = new KembaliBekerja();
				callFragment(fragment);*/
				Intent intent = new Intent(context, KembaliBekerja.class);
				((Activity) context).startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.no_move);
			}
		});
		menuBerhentiLembur.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*MainActivityBaru.isHome = false;
				fragment = new BerhentiLembur();
				callFragment(fragment);*/
				Intent intent = new Intent(context, BerhentiLembur.class);
				((Activity) context).startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.no_move);
			}
		});
		menuSampaiTujuan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isSampaiTujuan = true;
				isPindahTujuan = false;
				Intent intent = new Intent(context, Open_front_camera.class);
				intent.putExtra("absen", "sampai tujuan");
				((Activity) context).startActivity(intent);
			}
		});
		menuPindahTujuan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isPindahTujuan = true;
				isSampaiTujuan = false;
				Intent intent = new Intent(context, Open_front_camera.class);
				intent.putExtra("absen", "pindah tujuan");
				((Activity) context).startActivity(intent);
			}
		});
	}

	private void prepareDataProfle() {
		proses.ShowDialog();
		final JSONObject jBody = new JSONObject();
		try {
			jBody.put("fcm_id", token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(context, jBody, "POST", LinkURL.Profile, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				try {
					JSONObject object = new JSONObject(result);
					Log.d(TAG,"onsuccess "+result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						String foto = object.getJSONObject("response").getString("foto");
						if (foto.equals("")) {
							fotoProfle.setImageResource(R.drawable.user);
						} else {
							Picasso.get().load(object.getJSONObject("response").getString("foto"))
									.resize(512, 512)
									.centerCrop()
									.placeholder(R.drawable.user)
									.error(R.drawable.user)
									.transform(new CircleTransform())
									.into(fotoProfle);
						}
						namaProfile.setText(object.getJSONObject("response").getString("nama"));
						nikProfile.setText(object.getJSONObject("response").getString("nik"));
					} else if (status.equals("401")) {
						session.logoutUser();
					} else {
						Toast.makeText(context, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String result) {
				proses.DismissDialog();
				Toast.makeText(context, "terjadi kesalahan", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void prepareDataGantiPassword() {
		proses.ShowDialog();
		final JSONObject jBody = new JSONObject();
		try {
			jBody.put("password_lama", passLama.getText());
			jBody.put("password_baru", passBaru.getText());
			jBody.put("re_password", rePassBaru.getText());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ApiVolley request = new ApiVolley(context, jBody, "POST", LinkURL.gantiPassword, "", "", 0, new ApiVolley.VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				proses.DismissDialog();
				try {
					JSONObject object = new JSONObject(result);
					String status = object.getJSONObject("metadata").getString("status");
					String message = object.getJSONObject("metadata").getString("message");
					if (status.equals("200")) {
						dialogSukses.ShowDialog();
//						Toast.makeText(context, message, Toast.LENGTH_LONG).show();
					} else {
						DialogGagal.message = message;
						dialogGagal = new DialogGagal(context);
						dialogGagal.ShowDialog();
//						Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
	}

	private void callFragment(Fragment fragment) {
		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.fade_in, R.anim.no_move)
				.replace(R.id.changeLayout, fragment, fragment.getClass().getSimpleName())
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		prepareDataProfle();
	}
}
