package com.kidbuddies.controller;

import com.nyu.cs9033.kidbuddies.R;
import com.kidbuddies.controller.PhotoAct.IMGCallBack1;
import com.kidbuddies.model.Model;
import com.kidbuddies.net.ThreadPoolUtils;
import com.kidbuddies.thread.HttpPostThread;
import com.kidbuddies.utils.AlertDialogManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchPeopleActivity extends Activity implements OnClickListener {
	private ImageView mAddBack;
	private EditText mSearchUname;
	private Button mAddBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_people);
		initView();
	}

	private void initView() {
		mAddBack = (ImageView) findViewById(R.id.add_back);
		mSearchUname = (EditText) findViewById(R.id.edit_search_username);
		mAddBtn = (Button) findViewById(R.id.btn_add_friend);
		
		mAddBack.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_back:
			finish();
			break;
		case R.id.btn_add_friend:
			addFriend();
			break;
		default:
			break;
		}
	}
	
	public void addFriend(){
		String receiveusername = mSearchUname.getText().toString().trim();
		if(receiveusername.equals("")){
			AlertDialogManager.showAlert(this, "ALERT", "please enter username!", "OK");
			return;
		}
		String form = "{\"command\":\"" + "send invitation" + "\"," 
				+ "\"senduid\":\"" + Model.MYUSERINFO.getUserid() + "\","
				+ "\"receiveusername\":\"" + receiveusername + "\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand, Model.FRIEND, form)); 
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			
			if (msg.what == 200) {
				String result = (String) msg.obj;
				
				if (result != null && result.equals("ok")) {
					Toast.makeText(SearchPeopleActivity.this, "Successfully send invitation!", 1).show();
					SearchPeopleActivity.this.finish();
				}else if(result != null && result.equals("not exist username")){
					Toast.makeText(SearchPeopleActivity.this, "not exist username!", 1).show();
				}else if(result != null && result.equals("exist friend")){
					Toast.makeText(SearchPeopleActivity.this, "exist friend!", 1).show();
				}else if(result != null && result.equals("exist invitation")){
					Toast.makeText(SearchPeopleActivity.this, "exist invitation!", 1).show();
				}
				
//				Intent intent = new Intent();
//				setResult(3, intent);
//				SearchPeopleActivity.this.finish();
			}
		}
	};
}
