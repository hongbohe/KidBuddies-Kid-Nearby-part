package com.kidbuddies.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nyu.cs9033.kidbuddies.R;
import com.appkefu.lib.interfaces.KFIMInterfaces;
import com.kidbuddies.info.FriendInfo;
import com.kidbuddies.info.UserInfo;
import com.kidbuddies.model.Model;
import com.kidbuddies.net.ThreadPoolUtils;
import com.kidbuddies.thread.HttpPostThread;
import com.kidbuddies.utils.LoadImg;
import com.kidbuddies.utils.LoadImg.ImageDownloadCallBack;

public class FriendAdapter extends BaseAdapter {
	
	private List<FriendInfo> friendList;
	private Context context;
	private LoadImg headImg;
	private int selectRow = 0;

	public FriendAdapter(Context context, List<FriendInfo> friendList) {
		this.context = context;
		this.friendList = friendList;
		headImg = new LoadImg(context);
	}

	@Override
	public int getCount() {
		return friendList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return friendList.get(arg0);
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
			
			arg1 = View.inflate(context, R.layout.item_friend, null);
			holder.friend_username = (TextView) arg1.findViewById(R.id.friend_username);
			holder.friend_gender = (TextView) arg1.findViewById(R.id.friend_gender);
			holder.friend_status = (TextView) arg1.findViewById(R.id.friend_status);
			holder.friend_img = (ImageView) arg1.findViewById(R.id.friend_img);
			holder.friend_operation = (Button) arg1.findViewById(R.id.friend_operation);
			
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		
		holder.friend_username.setText(friendList.get(arg0).getUname());
		holder.friend_gender.setBackgroundResource(R.drawable.nearby_gender_male);
		holder.friend_gender.setText("0");
		holder.friend_gender.setVisibility(View.VISIBLE);
		holder.friend_status.setVisibility(View.VISIBLE);
		holder.friend_img.setImageResource(R.drawable.default_users_avatar);
		holder.friend_operation
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				KFIMInterfaces.startChatWithUser(context,// 上下文Context
						Model.APPKEY + friendList.get(arg0).getUname(),// 对方用户名
						friendList.get(arg0).getUname());// 自定义会话窗口标题
			}
		});
		
		if (!friendList.get(arg0).getUage().equalsIgnoreCase("null")) {
			holder.friend_gender.setText(friendList.get(arg0).getUage());
			
			if (friendList.get(arg0).getUsex().equals("0")) {
				holder.friend_gender.setBackgroundResource(R.drawable.nearby_gender_female);
			} else {
				holder.friend_gender.setBackgroundResource(R.drawable.nearby_gender_male);
			}
		} else {
			holder.friend_gender.setVisibility(View.GONE);
		}
		
		if (!friendList.get(arg0).getUexplain().equalsIgnoreCase("null")) {
			holder.friend_status.setText(friendList.get(arg0).getUexplain());
		} else {
			holder.friend_status.setVisibility(View.GONE);
		}
		
		if (friendList.get(arg0).getUhead().equalsIgnoreCase("")) {
			holder.friend_img.setImageResource(R.drawable.default_users_avatar);
		} else {
			holder.friend_img.setTag(Model.USERHEADURL + friendList.get(arg0).getUhead());
			
			Bitmap bitHead = headImg.loadImage(holder.friend_img, Model.USERHEADURL + friendList.get(arg0).getUhead(),
				new ImageDownloadCallBack() {
					@Override
					public void onImageDownload(ImageView imageView, Bitmap bitmap) {
						if (holder.friend_img.getTag().equals(Model.USERHEADURL + friendList.get(arg0).getUhead())) {
							holder.friend_img.setImageBitmap(bitmap);
						}
					}
				});
			
			if (bitHead != null) {
				holder.friend_img.setImageBitmap(bitHead);
			}
		}

		return arg1;
	}

	static class Holder {
		TextView friend_username;
		TextView friend_gender;
		TextView friend_status;
		ImageView friend_img;
		Button friend_operation;
	}
}
