package com.kidbuddies.controller;

import java.util.ArrayList;
import java.util.List;

import com.nyu.cs9033.kidbuddies.R;
import com.appkefu.lib.interfaces.KFIMInterfaces;
import com.kidbuddies.adapter.DetailListAdapter;
import com.kidbuddies.adapter.MyListAdapter;
import com.kidbuddies.adapter.MyListAdapter_children;
import com.kidbuddies.info.ActivityInfo;
import com.kidbuddies.info.ChildInfo;
import com.kidbuddies.info.CommentsInfo;
import com.kidbuddies.info.UserInfo;
import com.kidbuddies.model.Model;
import com.kidbuddies.myview.MyDetailsListView;
import com.kidbuddies.net.ThreadPoolUtils;
import com.kidbuddies.thread.HttpGetThread;
import com.kidbuddies.utils.LoadImg;
import com.kidbuddies.utils.MyJson;
import com.kidbuddies.utils.LoadImg.ImageDownloadCallBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {
	private UserInfo info = null;
	private ImageView mUserRevise, mUserMore, mUserCamera;
	private LinearLayout mBrief, mQiushi, mChildren;
	private LinearLayout mUserBrief, mUserQiushi, mUserChildren;
	private RelativeLayout mBack, mSendMsgLayout;
	// private Boolean myflag = true;
	private TextView SendMessage, UserName, UserAge, UserHobbies, UserPlace,
			UserExplain, UserTime, UserBrand, userinfo, Uemail, uFacebook;
	private LoadImg loadImgHeadImg;
	private MyJson myJson = new MyJson();
	private List<ActivityInfo> list = new ArrayList<ActivityInfo>();
	private List<ChildInfo> list_children = new ArrayList<ChildInfo>();
	private MyListAdapter mAdapter = null;
	private MyListAdapter_children mAdapter_children = null;
	private Button ListBottem = null;
	private Button ListBottem_children = null;
	private int mStart = 0;
	private int mEnd = 5;
	private int mStart_children = 0;
	private int mEnd_children = 5;
	private String url = null;
	private boolean flag = true;
	private boolean flag_children = true;
	private boolean loadflag = false;
	private boolean listBottemFlag = true;
	private boolean listBottemFlag_children = true;
	private MyDetailsListView Detail_List;
	private LinearLayout Detail__progressBar;
	private RelativeLayout Detail_CommentsNum;

	private MyDetailsListView Detail_List_children;
	private LinearLayout Detail__progressBar_children;
	private RelativeLayout Detail_CommentsNum_children;

	private int showType = 1;
	private boolean isFriend = false;
	private boolean loadflag_child = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userinfo);
		Intent intent = getIntent();
		Bundle bund = intent.getBundleExtra("value");
		info = (UserInfo) bund.getSerializable("UserInfo");
		isFriend = intent.getBooleanExtra("isFriend", false);
		initView();
		createUserInfo();

		mAdapter_children = new MyListAdapter_children(UserInfoActivity.this,
				list_children);
		ListBottem_children = new Button(UserInfoActivity.this);
		ListBottem_children.setText("Click to load");
		ListBottem_children.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag_children && listBottemFlag_children) {
					url = Model.CHILDREN + "uid=" + info.getUserid()
							+ "&start=" + mStart_children + "&end=" + mEnd_children;
					ThreadPoolUtils.execute(new HttpGetThread(hand_children, url));
					listBottemFlag_children = false;
				} else if (!listBottemFlag_children)
					Toast.makeText(UserInfoActivity.this, "Load...", 1).show();
			}
		});
		Detail_List_children.addFooterView(ListBottem_children, null, false);
		ListBottem_children.setVisibility(View.GONE);
		Detail_List_children.setAdapter(mAdapter_children);

		mAdapter = new MyListAdapter(UserInfoActivity.this, list);
		ListBottem = new Button(UserInfoActivity.this);
		ListBottem.setText("Click to load");
		ListBottem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag && listBottemFlag) {
					url = Model.ACTIVITY + "uid=" + info.getUserid()
							+ "&start=" + mStart + "&end=" + mEnd;
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					listBottemFlag = false;
				} else if (!listBottemFlag)
					Toast.makeText(UserInfoActivity.this, "Load...", 1).show();
			}
		});
		Detail_List.addFooterView(ListBottem, null, false);
		ListBottem.setVisibility(View.GONE);
		Detail_List.setAdapter(mAdapter);

		// 获得当前用户的所有孩子
		String Parames = Model.CHILDREN + "uid=" + info.getUserid() + "&start="
				+ mStart_children + "&end=" + mEnd_children;
		ThreadPoolUtils.execute(new HttpGetThread(hand_children, Parames));

		// 获得发表过得活动
		String endParames = Model.ACTIVITY + "uid=" + info.getUserid()
				+ "&start=" + mStart + "&end=" + mEnd;
		ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));
		// 设置个人资料"NICKNAME"
		KFIMInterfaces.setVCardField(UserInfoActivity.this, "NICKNAME",
				info.getUname());
	}

	private void initView() {
		// TODO Auto-generated method stub
		mBrief = (LinearLayout) findViewById(R.id.Brief);
		mChildren = (LinearLayout) findViewById(R.id.Children);
		mQiushi = (LinearLayout) findViewById(R.id.Qiushi);
		mUserBrief = (LinearLayout) findViewById(R.id.UserBrief);
		mUserChildren = (LinearLayout) findViewById(R.id.UserChild);
		mUserQiushi = (LinearLayout) findViewById(R.id.UserQiushi);
		mBack = (RelativeLayout) findViewById(R.id.UserBack);
		mSendMsgLayout = (RelativeLayout) findViewById(R.id.sendmsg_layout);
		mUserRevise = (ImageView) findViewById(R.id.UserRevise);
		mUserMore = (ImageView) findViewById(R.id.img_UserMore);
		mUserCamera = (ImageView) findViewById(R.id.UserCamera);
		SendMessage = (TextView) findViewById(R.id.SendMessage);
		UserName = (TextView) findViewById(R.id.UserName);
		UserAge = (TextView) findViewById(R.id.UserAge);
		//UserHobbies = (TextView) findViewById(R.id.UserHobbies);
		UserPlace = (TextView) findViewById(R.id.UserPlace);
		Uemail = (TextView) findViewById(R.id.Email);
		UserExplain = (TextView) findViewById(R.id.UserExplain);
		UserTime = (TextView) findViewById(R.id.UserTime);
//		UserBrand = (TextView) findViewById(R.id.UserBrand);
		userinfo = (TextView) findViewById(R.id.userinfo);
		
		uFacebook = (TextView) findViewById(R.id.fb_account_name);

		Detail_List_children = (MyDetailsListView) findViewById(R.id.Detail_List_children);
		Detail__progressBar_children = (LinearLayout) findViewById(R.id.Detail__progressBar_children);
		Detail_CommentsNum_children = (RelativeLayout) findViewById(R.id.usernochildren);

		Detail_List = (MyDetailsListView) findViewById(R.id.Detail_List);
		Detail__progressBar = (LinearLayout) findViewById(R.id.Detail__progressBar);
		Detail_CommentsNum = (RelativeLayout) findViewById(R.id.usernoashamed);
		MyOnClick my = new MyOnClick();

//		Detail_List_children.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				Intent intent = new Intent(UserInfoActivity.this,
//						AshamedDetailActivity.class);
//				Bundle bund = new Bundle();
//				bund.putSerializable("AshamedInfo", list.get(arg2));
//				intent.putExtra("value", bund);
//				startActivity(intent);
//			}
//		});

		Detail_List.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(UserInfoActivity.this,
						ActivityDetailActivity.class);
				Bundle bund = new Bundle();
				bund.putSerializable("AshamedInfo", list.get(arg2));
				intent.putExtra("value", bund);
				startActivity(intent);
			}
		});
		mBrief.setOnClickListener(my);
		mChildren.setOnClickListener(my);
		mQiushi.setOnClickListener(my);
		mUserRevise.setOnClickListener(my);
		mUserMore.setOnClickListener(my);
		mBack.setOnClickListener(my);
		mUserCamera.setOnClickListener(my);
		SendMessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				KFIMInterfaces.startChatWithUser(UserInfoActivity.this,// 上下文Context
						Model.APPKEY + info.getUname(),// 对方用户名
						info.getUname());// 自定义会话窗口标题
			}
		});
		
		//暂时屏蔽发送信息功能
		mSendMsgLayout.setVisibility(View.GONE);
		//查看好友信息时，隐藏编辑、退出按钮
		if(!info.getUname().equals(Model.MYUSERINFO.getUname())){
			mUserRevise.setVisibility(View.GONE);
			mUserMore.setVisibility(View.GONE);
		}
		
//		else if(!isFriend){
//			//查看非好友的资料时，隐藏发送小纸条按钮、编辑、退出按钮
//			mSendMsgLayout.setVisibility(View.GONE);
//			mUserRevise.setVisibility(View.GONE);
//			mUserMore.setVisibility(View.GONE);
//		}else if(isFriend){
//			//查看好友的资料时，隐藏编辑、退出按钮
//			mUserRevise.setVisibility(View.GONE);
//			mUserMore.setVisibility(View.GONE);
//		}
		
	}

	class MyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int mId = v.getId();
			switch (mId) {
			case R.id.Brief:
				showType = 1;
				initCont(showType);
				break;
			case R.id.Children:
				showType = 2;
				initCont(showType);
				break;
			case R.id.Qiushi:
				showType = 3;
				initCont(showType);
				break;
			case R.id.UserBack:
				finish();
				break;
			case R.id.UserRevise:
				if (showType == 1) {
					Intent intent = new Intent(UserInfoActivity.this,
							UserInfoSettingActivity.class);
					startActivityForResult(intent, 1);
				} else if (showType == 2) {
					Intent intent2 = new Intent(UserInfoActivity.this,
							CreateChildInfoActivity.class);
					startActivityForResult(intent2, 1);
				}
				break;
			case R.id.UserCamera:
				// Intent intent = new Intent(UserInfoActivity.this,.class);
				// startActivity(intent);
				break;
			case R.id.img_UserMore:
				logout();
				break;
			}

		}

	}

	// 退出登录
	private void logout() {
		if (Model.MYUSERINFO != null) {
			if (info.getUname().equals(Model.MYUSERINFO.getUname())) {
				new AlertDialog.Builder(this)
						.setMessage("Log out?")
						.setPositiveButton("Confirm",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Model.MYUSERINFO = null;
										SharedPreferences sp = getSharedPreferences(
												"UserInfo", MODE_PRIVATE);
										Editor editor = sp.edit();
										editor.clear();
										editor.commit();
										// 退出登录
										KFIMInterfaces
												.Logout(UserInfoActivity.this);
										Intent intent = new Intent(
												UserInfoActivity.this,
												LoginActivity.class);
										startActivity(intent);
										finish();
									}
								}).setNegativeButton("Cancel", null).create()
						.show();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler hand_children = new Handler() {

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(UserInfoActivity.this, "Server error! Request failed!", 1).show();
				listBottemFlag_children = true;
			} else if (msg.what == 100) {
				Toast.makeText(UserInfoActivity.this, "Server no response", 1).show();
				listBottemFlag_children = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					List<ChildInfo> newList = myJson.getChildList(result);
					if (newList != null) {
						if (newList.size() == 5) {
							//Detail_List_children.setVisibility(View.VISIBLE);
							ListBottem_children.setVisibility(View.VISIBLE);
							mStart_children += 5;
							mEnd_children += 5;
						} else if (newList.size() == 0) {
							if (list_children.size() == 0)
								Detail_CommentsNum_children
										.setVisibility(View.VISIBLE);
							ListBottem_children.setVisibility(View.GONE);
							Toast.makeText(UserInfoActivity.this, "There is no more...", Toast.LENGTH_LONG).show();
							//Detail_List_children.setVisibility(View.GONE);
						} else {
							//Detail_List_children.setVisibility(View.VISIBLE);
							ListBottem_children.setVisibility(View.GONE);
						}
						if(!loadflag_child){
							list_children.removeAll(list_children);
						}
							
						for (ChildInfo info : newList) {
							list_children.add(info);
						}
						listBottemFlag_children = true;
						if(list_children.size() == 0){
							Detail_CommentsNum_children.setVisibility(View.VISIBLE);
						}else{
							Detail_CommentsNum_children.setVisibility(View.GONE);
						}
						
					} else {
						//Detail_List_children.setVisibility(View.GONE);
						Detail_CommentsNum_children.setVisibility(View.VISIBLE);
					}
				}

				Detail__progressBar_children.setVisibility(View.GONE);
				mAdapter_children.notifyDataSetChanged();
				loadflag_child = true;
			}
			mAdapter_children.notifyDataSetChanged();
			
		};

	};

	@SuppressLint("HandlerLeak")
	Handler hand = new Handler() {

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(UserInfoActivity.this, "Server error! Request failed!", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(UserInfoActivity.this, "Server no response", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					List<ActivityInfo> newList = myJson.getAshamedList(result);
					if (newList != null) {
						if (newList.size() == 5) {
							//Detail_List.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.VISIBLE);
							mStart += 5;
							mEnd += 5;
						} else if (newList.size() == 0) {
							if (list.size() == 0)
								Detail_CommentsNum.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
							Toast.makeText(UserInfoActivity.this, "There is no more...", Toast.LENGTH_LONG).show();
							//Detail_List.setVisibility(View.GONE);
						} else {
							//Detail_List.setVisibility(View.VISIBLE);
							ListBottem.setVisibility(View.GONE);
						}
						for (ActivityInfo info : newList) {
							list.add(info);
						}
						listBottemFlag = true;
					} else {
						Detail_List.setVisibility(View.GONE);
						Detail_CommentsNum.setVisibility(View.VISIBLE);
					}
				}
				Detail__progressBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
			}
			mAdapter.notifyDataSetChanged();
		};

	};

	private void initCont(int showType) {
		if (showType == 1) {
			mBrief.setBackgroundResource(R.drawable.cab_background_top_light);
			mChildren.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mQiushi.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mUserBrief.setVisibility(View.VISIBLE);
			mUserChildren.setVisibility(View.GONE);
			mUserQiushi.setVisibility(View.GONE);
		} else if (showType == 2) {
			mBrief.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mChildren
					.setBackgroundResource(R.drawable.cab_background_top_light);
			mQiushi.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mUserBrief.setVisibility(View.GONE);
			mUserChildren.setVisibility(View.VISIBLE);
			mUserQiushi.setVisibility(View.GONE);
		} else {
			mBrief.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mChildren.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mQiushi.setBackgroundResource(R.drawable.cab_background_top_light);
			mUserBrief.setVisibility(View.GONE);
			mUserChildren.setVisibility(View.GONE);
			mUserQiushi.setVisibility(View.VISIBLE);
		}
	}

	private void createUserInfo() {
		if (Model.MYUSERINFO != null) {
			if (info.getUname().equals(Model.MYUSERINFO.getUname()))
				mSendMsgLayout.setVisibility(View.GONE);
		} else {
			Intent intent = new Intent(UserInfoActivity.this,
					LoginActivity.class);
			startActivity(intent);
		}
		UserName.setText(info.getUname());
		if (!info.getUage().equals("null")) {
			UserAge.setText(info.getUage());
		}
		
		if (info.getUsex().equals("0")) {
			UserAge.setBackgroundResource(R.drawable.nearby_gender_female);
		} else if (info.getUsex().equals("1")) {
			UserAge.setBackgroundResource(R.drawable.nearby_gender_male);
		}
	
		if (!info.getUplace().equals("null")) {
			UserPlace.setText(info.getUplace());
		}
		
		if (!info.getUemail().equals("null")) {
			Uemail.setText(info.getUemail());
		}
		
		if (!info.getUexplain().equals("null")) {
			UserExplain.setText(info.getUexplain());
			userinfo.setText(info.getUexplain());
		}
		
		if (!info.getUfacebook().equals("null")) {
			uFacebook.setText(info.getUfacebook());
		}
		
		UserTime.setText(info.getUtime());
		if (!info.getUhead().equals("null")) {
			loadImgHeadImg = new LoadImg(UserInfoActivity.this);
			Bitmap bit = loadImgHeadImg.loadImage(mUserCamera,
					Model.USERHEADURL + info.getUhead(),
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
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 2) {
			info = Model.MYUSERINFO;
			createUserInfo();
		}else if(resultCode == 3){
			// 获得当前用户的所有孩子
			mStart_children = 0;
			mEnd_children = 5;
			loadflag_child = false;
			String Parames = Model.CHILDREN + "uid=" + info.getUserid() + "&start="
					+ mStart_children + "&end=" + mEnd_children;
			ThreadPoolUtils.execute(new HttpGetThread(hand_children, Parames));
		}
	}
	

}
