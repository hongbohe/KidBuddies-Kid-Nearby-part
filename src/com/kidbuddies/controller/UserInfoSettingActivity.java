package com.kidbuddies.controller;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.kidbuddies.R;
import com.kidbuddies.controller.PhotoAct.IMGCallBack1;
import com.kidbuddies.info.UserInfo;
import com.kidbuddies.model.Model;
import com.kidbuddies.net.ThreadPoolUtils;
import com.kidbuddies.thread.HttpPostThread;
import com.kidbuddies.utils.LoadImg;
import com.kidbuddies.utils.MyJson;
import com.kidbuddies.utils.LoadImg.ImageDownloadCallBack;

public class UserInfoSettingActivity extends Activity {
	private ImageView mBack,mUserCamera;
	private TextView mGender;
	private EditText mAge, mAddress, mStatus, mEmail, mFacebook;
	private Button mSave;
	private MyJson myJson = new MyJson();
	private LoadImg loadImgHeadImg;
	private String data = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userinfo_setting);
		initview();
		initText();
	}

	private void initview() {
		// TODO Auto-generated method stub
		mBack = (ImageView) findViewById(R.id.setting_back);
		mUserCamera = (ImageView) findViewById(R.id.UserCamera);
		mGender = (TextView) findViewById(R.id.gender_text);
		mAge = (EditText) findViewById(R.id.age_Edittext);
		mAddress = (EditText) findViewById(R.id.address_Edittext);
		mEmail = (EditText) findViewById(R.id.email_Edittext);
		mStatus = (EditText) findViewById(R.id.status_Edittext);
		mFacebook = (EditText) findViewById(R.id.fb_account_name);
		
		mSave = (Button) findViewById(R.id.save_button);
		
		MyOnClick my = new MyOnClick();
		mBack.setOnClickListener(my);
		mUserCamera.setOnClickListener(my);
		mGender.setOnClickListener(my);
		mSave.setOnClickListener(my);
		
		PhotoAct.setIMGcallback(new IMGCallBack1() {

			@Override
			public void callback(String data) {
				UserInfoSettingActivity.this.data = data;
				byte[] bitmapArray = Base64.decode(UserInfoSettingActivity.this.data, Base64.DEFAULT);  
				Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
				try {
					mUserCamera.setImageBitmap(bitmap);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		});

	}
	
	private void initText() {
		// TODO Auto-generated method stub
		String gender = Model.MYUSERINFO.getUsex();
		if(gender.equals("1"))
		{
			mGender.setText("MALE");
		}else
		{
			mGender.setText("FEMALE");
		}
		
		if(!Model.MYUSERINFO.getUage().equals("null"))
		{
			mAge.setText(Model.MYUSERINFO.getUage());
		}
		
		if(!Model.MYUSERINFO.getUplace().equals("null"))
		{
			mAddress.setText(Model.MYUSERINFO.getUplace());
		}

		if(!Model.MYUSERINFO.getUemail().equals("null"))
		{
			mEmail.setText(Model.MYUSERINFO.getUemail());
		}
		
		if(!Model.MYUSERINFO.getUexplain().equals("null"))
		{
			mStatus.setText(Model.MYUSERINFO.getUexplain());
		}
		
		if(!Model.MYUSERINFO.getUfacebook().equals("null"))
		{
			mFacebook.setText(Model.MYUSERINFO.getUfacebook());
		}
		
		if (!Model.MYUSERINFO.getUhead().equals("null")) {
			loadImgHeadImg = new LoadImg(UserInfoSettingActivity.this);
			Bitmap bit = loadImgHeadImg.loadImage(mUserCamera,
					Model.USERHEADURL + Model.MYUSERINFO.getUhead(),
					new ImageDownloadCallBack() {
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							mUserCamera.setImageBitmap(bitmap);
						}
					});
			if (bit != null) {
				mUserCamera.setImageBitmap(bit);
			}
		}
		
	}

	class MyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int mId = v.getId();
			switch (mId) {
			case R.id.setting_back:
				UserInfoSettingActivity.this.finish();
				break;
			case R.id.UserCamera:
				Intent intent2 = new Intent(UserInfoSettingActivity.this, PhotoAct.class);
				startActivity(intent2);
				break;
			case R.id.gender_text:
				Builder builder = new Builder(UserInfoSettingActivity.this);
//				builder.setTitle("please select gender");
				final String[] items = new String[] { "FEMALE", "MALE" };
				DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg1, int arg0) {
						// TODO Auto-generated method stub
						if (arg0 == DialogInterface.BUTTON_POSITIVE) {
							arg1.cancel();
						}
						switch (arg0) {
						case 0:
							mGender.setText("FEMALE");
							break;
						case 1:
							mGender.setText("MALE");
							break;
						}
					}
				};
				builder.setItems(items, dialog);
				builder.setPositiveButton("Cancel", dialog);
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				break;
			case R.id.save_button:
				try {
					saveMeth();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("SaveMeth() error: ",e.getMessage());
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}

	}
	
	private void saveMeth() throws IOException {
		String uid = Model.MYUSERINFO.getUserid();
		mGender = (TextView) findViewById(R.id.gender_text);
		mAge = (EditText) findViewById(R.id.age_Edittext);
		mAddress = (EditText) findViewById(R.id.address_Edittext);
		mEmail = (EditText) findViewById(R.id.email_Edittext);
		mStatus = (EditText) findViewById(R.id.status_Edittext);
		
		String kimg = "";// activity图片
		if (!data.equalsIgnoreCase("")) {
			kimg = System.currentTimeMillis() + ".png";// activity图片
		}
		
		
		String gender = mGender.getText().toString().equals("male") ? "1" : "0";
		String age = mAge.getText().toString();
		String address = mAddress.getText().toString();
		String email = mEmail.getText().toString();
		String status = mStatus.getText().toString();
		String facebook = mFacebook.getText().toString();
		
		String latitude = null;
		String longitude = null;
		
		Geocoder gc = new Geocoder(this);
		List<Address> list = gc.getFromLocationName(address, 1);
		Address add = list.get(0);
		String locality = add.getLocality();
		Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

		double lat = add.getLatitude();
		double lng = add.getLongitude();
		latitude = String.valueOf(lat);
		longitude = String.valueOf(lng);
		//////////////////////////////////////////////
		
		String Json = "{\"uid\":\"" + uid + "\"," 
				+ "\"kimg\":\"" + kimg + "\","
				+ "\"gender\":\"" + gender + "\","
				+ "\"age\":\"" + age + "\","
				+ "\"address\":\"" + address + "\","
				+ "\"latitude\":\"" + latitude + "\","
				+ "\"longitude\":\"" + longitude + "\","
				+ "\"email\":\"" + email + "\","
				+ "\"facebook\":\"" + facebook + "\","
				+ "\"status\":\"" + status + "\"}";
		ThreadPoolUtils
				.execute(new HttpPostThread(hand, Model.USERINFOSETTING, Json,data));
		
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					Toast.makeText(UserInfoSettingActivity.this, "Successfully saved!", 1)
							.show();
					
					//重新取得当前登陆用户的用户信息
					List<UserInfo> newList = myJson.getNearUserList(result);
					if (newList != null) {
						Model.MYUSERINFO = newList.get(0);
						
						// 调用SharedPreferences保存本程序软件相关参数
						SharedPreferences sp = UserInfoSettingActivity.this
								.getSharedPreferences("UserInfo", MODE_PRIVATE);
						Log.e("SharedPreferencesOld",
								sp.getString("UserInfoJson", "none"));
						SharedPreferences.Editor mSettinsEd = sp.edit();
						mSettinsEd.putString("UserInfoJson", result);
						// 提交保存
						mSettinsEd.commit();
					}
					
					Intent intent = new Intent();
					setResult(2, intent);
					UserInfoSettingActivity.this.finish();
				}
			}
		};
	};
	
	
}
