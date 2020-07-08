package gmedia.net.id.OnTime.menu_approval_cuti;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class ListAdapterApprovalCuti extends ArrayAdapter {
	private Context context;
	private List<ModelApprovalCuti> listApprovalCuti;
	private Proses proses;
	private String status = "0";

	public ListAdapterApprovalCuti(Context context, List<ModelApprovalCuti> listApprovalCuti) {
		super(context, R.layout.view_lv_approval_cuti, listApprovalCuti);
		this.context = context;
		this.listApprovalCuti = listApprovalCuti;
		proses = new Proses(context);
	}

	private static class ViewHolder {
		private TextView nama, tanggal, alasan;
		private ImageView proses;
		private String id;
	}

	public void addMoreData(List<ModelApprovalCuti> moreData) {
		listApprovalCuti.addAll(moreData);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
			convertView = inflater.inflate(R.layout.view_lv_approval_cuti, null);
			holder.nama = (TextView) convertView.findViewById(R.id.txtNamaApprovalCuti);
			holder.tanggal = (TextView) convertView.findViewById(R.id.txtTanggalApprovalCuti);
			holder.alasan = (TextView) convertView.findViewById(R.id.txtKeteranganApprovalCuti);
			holder.proses = (ImageView) convertView.findViewById(R.id.prosesApprovalCuti);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ModelApprovalCuti modelApprovalCuti = listApprovalCuti.get(position);
		holder.id = modelApprovalCuti.getId();
		holder.nama.setText(modelApprovalCuti.getNama());
//		holder.tanggal.setText(modelApprovalCuti.getTglAwal()+" - "+modelApprovalCuti.getTglAkhir());
		holder.tanggal.setText(ConvertDate.convert("yyyy-MM-dd", "dd MMM yyyy", modelApprovalCuti.getTglAwal())
				+ " - " + ConvertDate.convert("yyyy-MM-dd", "dd MMM yyyy", modelApprovalCuti.getTglAkhir()));
		holder.alasan.setText(modelApprovalCuti.getAlasan());
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
							prepareApprovalCuti();
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
							prepareApprovalCuti();
						}
						dialog.dismiss();
					}
				});
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}

			private void prepareApprovalCuti() {
				proses.ShowDialog();
				JSONObject jBody = new JSONObject();
				try {
					jBody.put("id", finalHolder.id);
					jBody.put("status", status);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ApiVolley request = new ApiVolley(context, jBody, "POST", LinkURL.approvalCuti, "", "", 0, new ApiVolley.VolleyCallback() {
					@Override
					public void onSuccess(String result) {
						proses.DismissDialog();
						try {
							JSONObject object = new JSONObject(result);
							String status = object.getJSONObject("metadata").getString("status");
							String message = object.getJSONObject("metadata").getString("message");
							if (status.equals("200")) {
								listApprovalCuti.remove(listApprovalCuti.get(position));
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
