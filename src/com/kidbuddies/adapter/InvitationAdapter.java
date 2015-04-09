package com.kidbuddies.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.kidbuddies.R;
import com.appkefu.lib.interfaces.KFIMInterfaces;
import com.kidbuddies.info.InvitationInfo;
import com.kidbuddies.info.UserInfo;
import com.kidbuddies.model.Model;
import com.kidbuddies.net.ThreadPoolUtils;
import com.kidbuddies.thread.HttpPostThread;
import com.kidbuddies.utils.LoadImg;
import com.kidbuddies.utils.LoadImg.ImageDownloadCallBack;

public class InvitationAdapter extends BaseAdapter {
	
	private List<InvitationInfo> invitationList;
	private Context context;
	private LoadImg headImg;
	private int selectRow = 0;

	public InvitationAdapter(Context context, List<InvitationInfo> invitationList) {
		this.context = context;
		this.invitationList = invitationList;
		headImg = new LoadImg(context);
	}

	@Override
	public int getCount() {
		return invitationList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return invitationList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		final Holder holder;
		
		if (arg1 == null) {
			holder = new Holder();
			
			arg1 = View.inflate(context, R.layout.item_invitation, null);
			holder.invitation_username = (TextView) arg1.findViewById(R.id.invitation_username);
			holder.invitation_gender = (TextView) arg1.findViewById(R.id.invitation_gender);
			holder.invitation_status = (TextView) arg1.findViewById(R.id.invitation_status);
			holder.invitation_img = (ImageView) arg1.findViewById(R.id.invitation_img);
			holder.invitation_operation = (Button) arg1.findViewById(R.id.invitation_operation);
			
			
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		
		holder.invitation_username.setText(invitationList.get(arg0).getUname());
		holder.invitation_gender.setBackgroundResource(R.drawable.nearby_gender_male);
		holder.invitation_gender.setText("0");
		holder.invitation_gender.setVisibility(View.VISIBLE);
		holder.invitation_status.setVisibility(View.VISIBLE);
		holder.invitation_img.setImageResource(R.drawable.default_users_avatar);
		holder.invitation_operation
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						// builder.setIcon(R.drawable.icon);
						builder.setTitle("How do you wanna handle this request?");
						builder.setPositiveButton("Accept",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// 这里添加点击确定后的逻辑
										//showDialog("你选择了确定");
										selectRow = arg0;
										String form = "{\"command\":\"" + "respond invitation" + "\"," 
												+ "\"uid\":\"" + Model.MYUSERINFO.getUserid() + "\","
												+ "\"fid\":\"" + invitationList.get(arg0).getUserid() + "\","
												+ "\"id\":\"" + invitationList.get(arg0).getId() + "\","
												+ "\"status\":\"" + "1" + "\"}";
										ThreadPoolUtils.execute(new HttpPostThread(hand, Model.FRIEND, form)); 
										
									}
								});
						builder.setNegativeButton("Refuse",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// 这里添加点击确定后的逻辑
										//showDialog("你选择了取消");
										selectRow = arg0;
										String form = "{\"command\":\"" + "respond invitation" + "\"," 
												+ "\"uid\":\"" + Model.MYUSERINFO.getUserid() + "\","
												+ "\"fid\":\"" + invitationList.get(arg0).getUserid() + "\","
												+ "\"id\":\"" + invitationList.get(arg0).getId() + "\","
												+ "\"status\":\"" + "2" + "\"}";
										ThreadPoolUtils.execute(new HttpPostThread(hand, Model.FRIEND, form));
										
									}
								});
						builder.create().show();
					}
				});
		
		if (!invitationList.get(arg0).getUage().equalsIgnoreCase("null")) {
			holder.invitation_gender.setText(invitationList.get(arg0).getUage());
			
			if (invitationList.get(arg0).getUsex().equals("0")) {
				holder.invitation_gender.setBackgroundResource(R.drawable.nearby_gender_female);
			} else {
				holder.invitation_gender.setBackgroundResource(R.drawable.nearby_gender_male);
			}
		} else {
			holder.invitation_gender.setVisibility(View.GONE);
		}
		
		if (!invitationList.get(arg0).getUexplain().equalsIgnoreCase("null")) {
			holder.invitation_status.setText(invitationList.get(arg0).getUexplain());
		} else {
			holder.invitation_status.setVisibility(View.GONE);
		}
		
		if (invitationList.get(arg0).getUhead().equalsIgnoreCase("")) {
			holder.invitation_img.setImageResource(R.drawable.default_users_avatar);
		} else {
			holder.invitation_img.setTag(Model.USERHEADURL + invitationList.get(arg0).getUhead());
			
			Bitmap bitHead = headImg.loadImage(holder.invitation_img, Model.USERHEADURL + invitationList.get(arg0).getUhead(),
				new ImageDownloadCallBack() {
					@Override
					public void onImageDownload(ImageView imageView, Bitmap bitmap) {
						if (holder.invitation_img.getTag().equals(Model.USERHEADURL + invitationList.get(arg0).getUhead())) {
							holder.invitation_img.setImageBitmap(bitmap);
						}
					}
				});
			
			if (bitHead != null) {
				holder.invitation_img.setImageBitmap(bitHead);
			}
		}

		return arg1;
	}
	
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			
			if (msg.what == 404) {
				Toast.makeText(context, "Server IP not found.", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 100) {
				Toast.makeText(context, "Communication failed.", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				
				if (result != null && result.equals("ok")) {
					invitationList.remove(selectRow);
					notifyDataSetChanged();
					Toast.makeText(context, "Successful Operation!", Toast.LENGTH_SHORT).show();
				}
			}
			
		};
	};

	static class Holder {
		TextView invitation_username;
		TextView invitation_gender;
		TextView invitation_status;
		ImageView invitation_img;
		Button invitation_operation;
	}
}
