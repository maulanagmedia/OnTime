package gmedia.net.id.OnTime.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import gmedia.net.id.OnTime.MainActivityBaru;
import gmedia.net.id.OnTime.R;

public class DialogSukses {
	private Context context;
	private Dialog dialog;

	public DialogSukses(final Context context) {
		this.context = context;
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.popup_sukses);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		RelativeLayout btnOk = (RelativeLayout) dialog.findViewById(R.id.btnOkPopupSukses);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}

	public DialogSukses(final Context context, String end) {
		this.context = context;
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.popup_sukses);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		RelativeLayout btnOk = (RelativeLayout) dialog.findViewById(R.id.btnOkPopupSukses);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				((Activity) context).finish();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}

	public void ShowDialog() {
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
			}
		});
		handler.postDelayed(runnable, 3000);
	}

	public void DismissDialog() {
		dialog.dismiss();
	}
}
