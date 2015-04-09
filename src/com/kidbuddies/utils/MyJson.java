package com.kidbuddies.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kidbuddies.info.ActivityInfo;
import com.kidbuddies.info.ActivityUsersInfo;
import com.kidbuddies.info.ChildInfo;
import com.kidbuddies.info.CommentsInfo;
import com.kidbuddies.info.FriendInfo;
import com.kidbuddies.info.InvitationInfo;
import com.kidbuddies.info.NearByKid;
import com.kidbuddies.info.UserInfo;

/**
 * Json字符串解析工具类
 * 
 * @author Linxuan jiang
 */

public class MyJson {

	// 解析活动方法
	public List<ActivityInfo> getAshamedList(String value) {
		List<ActivityInfo> list = null;
		try {
			JSONArray jay = new JSONArray(value);
			list = new ArrayList<ActivityInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				ActivityInfo info = new ActivityInfo();
				info.setQid(job.getString("QID"));
				info.setUid(job.getString("UID"));
				info.setTid(job.getString("TID"));
//				info.setQimg(job.getString("QIMG"));
//				info.setQvalue(job.getString("QVALUE"));
//				info.setQlike(job.getString("QLIKE"));
//				info.setQunlike(job.getString("QUNLIKE"));
//				info.setQshare(job.getString("QSHARE"));
				info.setUname(job.getString("UNAME"));
				info.setUhead(job.getString("UHEAD"));
				
				info.setTitle(job.getString("TITLE"));
				info.setStartdate(job.getString("STARTDATE"));
				info.setStarttime(job.getString("STARTTIME"));
				info.setEnddate(job.getString("ENDDATE"));
				info.setEndtime(job.getString("ENDTIME"));
				info.setSpot(job.getString("SPOT"));
				info.setMaxnum(job.getString("MAXNUM"));
				info.setDetail(job.getString("DETAIL"));
				info.setCommentcount(job.getString("COMMENTCOUNT"));
				info.setJoincount(job.getString("JOINCOUNT"));
				
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}
		
	// 解析参加活动人方法
	public List<ActivityUsersInfo> getActivityUsersList(String value) {
		List<ActivityUsersInfo> list = null;
		try {
			JSONArray jay = new JSONArray(value);
			list = new ArrayList<ActivityUsersInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				ActivityUsersInfo info = new ActivityUsersInfo();
				info.setId(job.getString("ID"));
				info.setAid(job.getString("AID"));
				info.setUid(job.getString("UID"));
				info.setCreatetime(job.getString("CREATETIME"));
				info.setUsername("USERNAME");
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}
	

	// 解析评论的方法
	public List<CommentsInfo> getAhamedCommentsList(String value) {
		List<CommentsInfo> list = null;
		try {
			JSONArray jay = new JSONArray(value);
			list = new ArrayList<CommentsInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				CommentsInfo info = new CommentsInfo();
				info.setCid(job.getString("CID"));
				info.setCvalue(job.getString("CVALUE"));
				info.setQid(job.getString("QID"));
				info.setUid(job.getString("UID"));
				info.setCtime(job.getString("CTIME"));
				info.setUname(job.getString("UNAME"));
				info.setUhead(job.getString("UHEAD"));
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}

	// 解析用户的方法
	public List<UserInfo> getNearUserList(String result) {
		List<UserInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<UserInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				UserInfo info = new UserInfo();
				info.setUserid(job.getString("USERID"));
				info.setUname(job.getString("UNAME"));
				info.setUhead(job.getString("UHEAD"));
				info.setUage(job.getString("UAGE"));
				info.setUhobbles(job.getString("UHOBBIES"));
				info.setUplace(job.getString("UPLACE"));
				info.setUexplain(job.getString("UEXPLAIN"));
				info.setUtime(job.getString("UTIME"));
				info.setUbrand(job.getString("UBRAND"));
				info.setUsex(job.getString("USEX"));
				info.setUemail(job.getString("UEMAIL"));
				info.setUfacebook(job.getString("UFACEBOOK"));
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}

	// 解析孩子的方法
	public List<ChildInfo> getChildList(String result) {
		List<ChildInfo> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<ChildInfo>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				ChildInfo info = new ChildInfo();
				info.setChildid(job.getString("CHILDID"));
				info.setUserid(job.getString("USERID"));
				info.setCname(job.getString("CNAME"));
				info.setChead(job.getString("CHEAD"));
				info.setCage(job.getString("CAGE"));
				info.setChobbles(job.getString("CHOBBIES"));
				info.setCdetail(job.getString("CDETAIL"));
				info.setCsex(job.getString("CSEX"));
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}
	
	
	// 解析用户的方法
	public List<NearByKid> getNearKidList(String result) {
		List<NearByKid> list = null;
		try {
			JSONArray jay = new JSONArray(result);
			list = new ArrayList<NearByKid>();
			for (int i = 0; i < jay.length(); i++) {
				JSONObject job = jay.getJSONObject(i);
				NearByKid info = new NearByKid();
				info.setUserName(job.getString("UNAME"));
				info.setChildAge(Integer.parseInt(job.getString("CAGE")));
				int sex = Integer.parseInt(job.getString("CSEX"));
				info.setChildSex(sex == 0 ? "girl":"boy");
				info.setLatitude(job.getDouble("ULAT"));
				info.setLongitude(job.getDouble("ULNG"));
				info.setUserID(job.getString("USERID"));
				list.add(info);
			}
		} catch (JSONException e) {
		}
		return list;
	}
	
	// 解析加好友请求的方法
		public List<InvitationInfo> getInvitationList(String result) {
			List<InvitationInfo> list = null;
			try {
				JSONArray jay = new JSONArray(result);
				list = new ArrayList<InvitationInfo>();
				for (int i = 0; i < jay.length(); i++) {
					JSONObject job = jay.getJSONObject(i);
					InvitationInfo info = new InvitationInfo();
					info.setId(job.getString("ID"));
					info.setSenduid(job.getString("SENDUID"));
					info.setReceiveuid(job.getString("RECEIVEUID"));
					info.setInvitetime(job.getString("INVITETIME"));
					info.setResponsetime(job.getString("RESPONSETIME"));
					info.setStatus(job.getString("STATUS"));
					
					info.setUserid(job.getString("USERID"));
					info.setUname(job.getString("UNAME"));
					info.setUhead(job.getString("UHEAD"));
					info.setUage(job.getString("UAGE"));
					info.setUhobbles(job.getString("UHOBBIES"));
					info.setUplace(job.getString("UPLACE"));
					info.setUexplain(job.getString("UEXPLAIN"));
					info.setUtime(job.getString("UTIME"));
					info.setUbrand(job.getString("UBRAND"));
					info.setUsex(job.getString("USEX"));
					info.setUemail(job.getString("UEMAIL"));
					info.setUfacebook(job.getString("UFACEBOOK"));
					list.add(info);
				}
			} catch (JSONException e) {
			}
			return list;
		}
		
		
		// 从邀请信息中获取邀请发出者的个人信息
		public UserInfo getUserInfoFromInvitationInfo(InvitationInfo invitationInfo) {
			UserInfo info = new UserInfo();
			try {
				info.setUserid(invitationInfo.getUserid());
				info.setUname(invitationInfo.getUname());
				info.setUhead(invitationInfo.getUhead());
				info.setUage(invitationInfo.getUage());
				info.setUhobbles(invitationInfo.getUhobbles());
				info.setUplace(invitationInfo.getUplace());
				info.setUexplain(invitationInfo.getUexplain());
				info.setUtime(invitationInfo.getUtime());
				info.setUbrand(invitationInfo.getUbrand());
				info.setUsex(invitationInfo.getUsex());
				info.setUemail(invitationInfo.getUemail());
				info.setUfacebook(invitationInfo.getUfacebook());
				
			} catch (Exception e) {
			}
			return info;
		}



		
		// 解析好友的方法
		public List<FriendInfo> getFriendList(String result) {
			List<FriendInfo> list = null;
			try {
				JSONArray jay = new JSONArray(result);
				list = new ArrayList<FriendInfo>();
				for (int i = 0; i < jay.length(); i++) {
					JSONObject job = jay.getJSONObject(i);
					FriendInfo info = new FriendInfo();
					info.setId(job.getString("ID"));
					info.setUid(job.getString("UID"));
					info.setFid(job.getString("FID"));
					info.setCreatetime(job.getString("CREATETIME"));
					
					info.setUserid(job.getString("USERID"));
					info.setUname(job.getString("UNAME"));
					info.setUhead(job.getString("UHEAD"));
					info.setUage(job.getString("UAGE"));
					info.setUhobbles(job.getString("UHOBBIES"));
					info.setUplace(job.getString("UPLACE"));
					info.setUexplain(job.getString("UEXPLAIN"));
					info.setUtime(job.getString("UTIME"));
					info.setUbrand(job.getString("UBRAND"));
					info.setUsex(job.getString("USEX"));
					info.setUemail(job.getString("UEMAIL"));
					info.setUfacebook(job.getString("UFACEBOOK"));
					list.add(info);
				}
			} catch (JSONException e) {
			}
			return list;
		}
		
		// 从我的好友信息中获取好友个人信息
		public UserInfo getUserInfoFromFriendInfo(FriendInfo friendInfo) {
			UserInfo info = new UserInfo();
			try {
				info.setUserid(friendInfo.getUserid());
				info.setUname(friendInfo.getUname());
				info.setUhead(friendInfo.getUhead());
				info.setUage(friendInfo.getUage());
				info.setUhobbles(friendInfo.getUhobbles());
				info.setUplace(friendInfo.getUplace());
				info.setUexplain(friendInfo.getUexplain());
				info.setUtime(friendInfo.getUtime());
				info.setUbrand(friendInfo.getUbrand());
				info.setUsex(friendInfo.getUsex());
				info.setUemail(friendInfo.getUemail());
				info.setUfacebook(friendInfo.getUfacebook());
				
			} catch (Exception e) {
			}
			return info;
		}

}
