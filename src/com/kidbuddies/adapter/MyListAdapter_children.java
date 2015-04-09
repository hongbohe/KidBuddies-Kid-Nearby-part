package com.kidbuddies.adapter;

import java.util.List;

import com.nyu.cs9033.kidbuddies.R;
import com.appkefu.lib.interfaces.KFIMInterfaces;
import com.kidbuddies.info.ActivityInfo;
import com.kidbuddies.info.ChildInfo;
import com.kidbuddies.model.Model;
import com.kidbuddies.utils.LoadImg;
import com.kidbuddies.utils.LoadImg.ImageDownloadCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyListAdapter_children extends BaseAdapter {

	private List<ChildInfo> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;

	public MyListAdapter_children(Context ctx, List<ChildInfo> list) {
		this.list = list;
		this.ctx = ctx;
		// 加载图片对象
		loadImgHeadImg = new LoadImg(ctx);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		final Holder hold;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, R.layout.mylistview_children_item, null);
			hold.ChildHead = (ImageView) arg1.findViewById(R.id.Item_ChildHead);
			hold.ChildName = (TextView) arg1.findViewById(R.id.Item_ChildName);
			hold.ChildAge = (TextView) arg1.findViewById(R.id.Item_ChildAge);
			hold.ChildHobbies = (TextView) arg1.findViewById(R.id.Item_ChildHobbies);
			hold.Detail = (TextView) arg1.findViewById(R.id.Item_ChildDetail);

			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		
		if (!list.get(arg0).getCage().equals("null")) 
		{
			hold.ChildAge.setText(list.get(arg0).getCage());
		}

		if (list.get(arg0).getCsex().equals("0")) {
			hold.ChildAge.setBackgroundResource(R.drawable.nearby_gender_female);
		} else if (list.get(arg0).getCsex().equals("1")) {
			hold.ChildAge.setBackgroundResource(R.drawable.nearby_gender_male);
		}

		if (!list.get(arg0).getCname().equals("null")) 
		{
			hold.ChildName.setText(list.get(arg0).getCname());
		}
		
		if (!list.get(arg0).getChobbles().equals("null")) 
		{
			hold.ChildHobbies.setText(list.get(arg0).getChobbles());
		}
		
		if (!list.get(arg0).getCdetail().equals("null")) 
		{
			hold.Detail.setText(list.get(arg0).getCdetail());
		}

		hold.ChildHead.setImageResource(R.drawable.default_users_avatar);
		if (list.get(arg0).getChead().equalsIgnoreCase("")) {
			hold.ChildHead.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.ChildHead
					.setTag(Model.USERHEADURL + list.get(arg0).getChead());
			Bitmap bitHead = loadImgHeadImg.loadImage(hold.ChildHead,
					Model.USERHEADURL + list.get(arg0).getChead(),
					new ImageDownloadCallBack() {
						@Override
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							if (arg0 >= list.size()) {
								if (hold.ChildHead
										.getTag()
										.equals(Model.USERHEADURL
												+ list.get(arg0 - 1).getChead())) {
									hold.ChildHead.setImageBitmap(bitmap);
								}
							} else {
								if (hold.ChildHead.getTag().equals(
										Model.USERHEADURL
												+ list.get(arg0).getChead())) {
									hold.ChildHead.setImageBitmap(bitmap);
								}
							}

						}
					});
			if (bitHead != null) {
				hold.ChildHead.setImageBitmap(bitHead);
			}
		}

		return arg1;
	}

	static class Holder {
		ImageView ChildHead;
		TextView ChildGender; 
		TextView ChildName;
		TextView ChildAge;
		TextView ChildHobbies;
		TextView Detail;
	}
}
