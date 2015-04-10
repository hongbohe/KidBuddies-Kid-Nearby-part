package com.kidbuddies.info;

public class NearByKid {
	public String userName;
	public int childAge;
	public String childSex;
	public double latitude;
	public double longitude;
	public String userID;

	public NearByKid() {

	}

	public NearByKid(String userName, int childAge, String childSex,
			double latitude, double longitude, String userID) {
		super();
		this.userName = userName;
		this.childAge = childAge;
		this.childSex = childSex;
		this.latitude = latitude;
		this.longitude = longitude;
		this.userID = userID;
	}

	


	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public int getChildAge() {
		return childAge;
	}

	public String getChildSex() {
		return childSex;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setChildAge(int childAge) {
		this.childAge = childAge;
	}

	public void setChildSex(String childSex) {
		this.childSex = childSex;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
