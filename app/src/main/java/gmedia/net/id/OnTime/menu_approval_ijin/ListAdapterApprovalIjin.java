package gmedia.net.id.OnTime.menu_approval_ijin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.utils.ApiVolley;
import gmedia.net.id.OnTime.utils.ConvertDate;
import gmedia.net.id.OnTime.utils.LinkURL;
import gmedia.net.id.OnTime.utils.Proses;

public class ListAdapterApprovalIjin extends ArrayAdapter {
	private Context context;
	private List<ModelApprovalIjin> list;
	private String status = "0";
	private Proses proses;

	public ListAdapterApprovalIjin(Context context, List<ModelApprovalIjin> list) {
		super(context, R.layout.view_lv_approval_ijin, list);
		this.context = context;
		this.list = list;
		proses = new Proses(context);
	}

	private static class ViewHolder {
		private TextView nama, tanggal, alasan;
		private ImageView proses;
		private String id;
	}

	public void addMoreData(List<ModelApprovalIjin> moreData) {
		list.addAll(moreData);
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
			convertView = inflater.inflate(R.layout.view_lv_approval_ijin, null);
			holder.nama = (TextView) convertView.findViewById(R.id.txtNamaApprovalIjin);
			holder.tanggal = (TextView) convertView.findViewById(R.id.txtTanggalApprovalIjin);
			holder.alasan = (TextView) convertView.findViewById(R.id.txtKeteranganApprovalIjin);
			holder.proses = (ImageView) convertView.findViewById(R.id.prosesApprovalIjin);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ModelApprovalIjin modelApprovalIjin = list.get(position);
		holder.id = modelApprovalIjin.getId();
		holder.nama.setText(modelApprovalIjin.getNama());
		holder.tanggal.setText(ConvertDate.convert("yyyy-MM-dd", "dd MMM yyyy", modelApprovalIjin.getTgl()));
		holder.alasan.setText(modelApprovalIjin.getAlasan());
		final ViewHolder finalHolder = holder;
		holder.proses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.popup_approval_yes_no);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				Button btnApprove = (Button) dialog.findViewById(R.id.btnYaApprovalCuti);
				btnApprove.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						status = "2";
						if (!status.equals("0")) {
							prepareApprovalIjin();
						}
						dialog.dismiss();
					}
				});
				Button btnReject = (Button) dialog.findViewById(R.id.btnTidakApprovalCuti);
				btnReject.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						status = "4";
						if (!status.equals("0")) {
							prepareApprovalIjin();
						}
						dialog.dismiss();
					}
				});
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}

			private void prepareApprovalIjin() {
				proses.ShowDialog();
				JSONObject jBody = new JSONObject();
				try {
					jBody.put("id", finalHolder.id);
					jBody.put("status", status);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ApiVolley request = new ApiVolley(context, jBody, "POST", LinkURL.approvalIjin, "", "", 0, new ApiVolley.VolleyCallback() {
					@Override
					public void onSuccess(String result) {
						proses.DismissDialog();
						try {
							JSONObject object = new JSONObject(result);
							String status = object.getJSONObject("metadata").getString("status");
							String message = object.getJSONObject("metadata").getString("message");
							if (status.equals("200")) {
								list.remove(list.get(position));
								notifyDataSetChanged();
								Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
		});
		return convertView;
	}
}
