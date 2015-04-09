package com.kidbuddies.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nyu.cs9033.kidbuddies.R;
import com.kidbuddies.adapter.RadiusAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kidbuddies.fragment.NearFragment.NearFragmentCallBack;
import com.kidbuddies.info.NearByKid;
import com.kidbuddies.info.UserInfo;
import com.kidbuddies.model.Model;
import com.kidbuddies.net.ThreadPoolUtils;
import com.kidbuddies.thread.HttpPostThread;
import com.kidbuddies.utils.MyJson;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

public class MapMainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, OnClickListener {

	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	private static final String DEFAULT = "N/A";
	private LinearLayout mapBack;
	GoogleMap mMap;

	@SuppressWarnings("unused")
	private static final double JALGAON_LAT = 40.694131,
			JALGAON_LNG = -73.986927;
	private List<Marker> markerList = new ArrayList<Marker>();
	private List<NearByKid> nearKidList = new ArrayList<NearByKid>();
	private static final float DEFAULTZOOM = 15;
	LocationClient mLocationClient;
	Marker marker;
	private MyJson myJson = new MyJson();
	// Circle shape;
	private String locality;
    Spinner spinnerRadius;
//	private ArrayAdapter<String> adapter = null;
//	private static final String[] searchRadius = {"1", "2", "3"};
	private double radius = 1.00d;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (servicesOK()) {
			setContentView(R.layout.activity_mapmain);
			spinnerRadius = (Spinner)findViewById(R.id.spinnerradius);
			mapBack = (LinearLayout) findViewById(R.id.Map_Back);
			mapBack.setOnClickListener(new OnClickListener() 
	         {

	            @Override
	            public void onClick(View v)
	            {
					Intent intenttoMain = new Intent(MapMainActivity.this,MainActivity.class);
					startActivity(intenttoMain);

	            }
	        });

//			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,searchRadius);
			
//			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
//			spinnerRadius.setAdapter(adapter);
//			spinnerRadius.setVisibility(View.VISIBLE);
//			spinnerRadius.setOnItemSelectedListener(new OnItemSelectedListener(){



		/*		@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
				//	result.setText("Select search radius: "+((TextView)arg1).getText());
					radius = Double.parseDouble(arg0.getItemAtPosition(arg2).toString());
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});*/

			final List<String> radiusList = new ArrayList<String>();
			radiusList.add("1");
			radiusList.add("5");
			radiusList.add("10");
			radiusList.add("Radius (miles)");// set hint here .
			
			spinnerRadius.setAdapter(new RadiusAdapter(getApplicationContext(), radiusList));
			spinnerRadius.setVisibility(View.VISIBLE);
			spinnerRadius.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if(arg0.getItemAtPosition(arg2).toString().length()<=2){
						radius = Double.parseDouble(arg0.getItemAtPosition(arg2).toString());
					}else{
						radius = 1.0;
					}
					
						
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
					
				}
			});
			
			spinnerRadius.setSelection(radiusList.size()-1);
			
			
			
			if (initMap()) {
				Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT)
						.show();
				gotoLocation(JALGAON_LAT, JALGAON_LNG, DEFAULTZOOM);
				mMap.setMyLocationEnabled(true);
				mLocationClient = new LocationClient(this, this, this);
				mLocationClient.connect();
			} else {
				Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			setContentView(R.layout.activity_nomapservice);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mapmain, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mapTypeNone:
			mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			break;
		case R.id.mapTypeNormal:
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.mapTypeSatellite:
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.mapTypeTerrain:
			mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case R.id.mapTypeHybrid:
			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case R.id.gotoCurrentLocation:
			gotoCurrentLocation();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean servicesOK() {
		int isAvailables = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (isAvailables == ConnectionResult.SUCCESS) {
			return true;
		} else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailables)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailables,
					this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		} else {
			Toast.makeText(this, "Google service not available",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private boolean initMap() {
		if (mMap == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mMap = mapFrag.getMap();

			if (mMap != null) {
				mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

					@Override
					public View getInfoWindow(Marker arg0) {
						return null;
					}

					@Override
					public View getInfoContents(Marker marker) {
						View v = getLayoutInflater().inflate(
								R.layout.mapinfo_window, null);
						TextView tvLocality = (TextView) v
								.findViewById(R.id.tv_locality);
						TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
						TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
						TextView tvSnippet = (TextView) v
								.findViewById(R.id.tv_snippet);

						LatLng ll = marker.getPosition();
						tvLocality.setText(marker.getTitle());
						String sniptTmp = null;
						sniptTmp = marker.getSnippet();

						if (sniptTmp != null) {
							String[] split = sniptTmp.split(" ");
							tvLat.setText("childAge: " + split[0]);
							tvLng.setText("childSex: " + split[1]);
						} else {
							tvLat.setText("latitude: " + ll.latitude);
							tvLng.setText("longitude: " + ll.longitude);
						}
						// tvSnippet.setText();
						return v;
					}
				});

				mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

					@Override
					public void onMapLongClick(LatLng ll) {
						Geocoder gc = new Geocoder(MapMainActivity.this);
						List<Address> list = null;

						try {
							list = gc.getFromLocation(ll.latitude,
									ll.longitude, 1);
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}

						Address add = list.get(0);
						MapMainActivity.this.setMarker(add.getLocality(),
								add.getCountryName(), ll.latitude, ll.longitude);

					}
				});

				mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker marker) {
						String msg = marker.getTitle() + " ("
								+ marker.getPosition().latitude + ","
								+ marker.getPosition().longitude + ")";
						Toast.makeText(MapMainActivity.this, msg,
								Toast.LENGTH_LONG).show();
						return false;
					}
				});
				
				mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			        @Override
			        public void onInfoWindowClick(final Marker marker) {
			        	

			        	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
			        	        MapMainActivity.this);

			        	// Setting Dialog Title
			        	alertDialog2.setTitle("Add friend");

			        	// Setting Dialog Message
			        	alertDialog2.setMessage("Add "+marker.getTitle()+ " as your friend?");

			        	// Setting Icon to Dialog
			        	alertDialog2.setIcon(R.drawable.icon_album);

			        	// Setting Positive "Yes" Btn
			        	alertDialog2.setPositiveButton("YES",
			        	        new DialogInterface.OnClickListener() {
			        	            public void onClick(DialogInterface dialog, int which) {
			        	                // Write your code here to execute after dialog
			        	            	String uid2 = marker.getSnippet().split(" ")[2];
			        	            	String uid1 = Model.MYUSERINFO.getUserid();
			        	            	
			        	        	/*	String Json = "{\"uid1\":\"" + uid1 + "\"," 
			        	        				+ "\"uid2\":\"" + uid2 + "\"}";
			        	        		ThreadPoolUtils
			        	        				.execute(new HttpPostThread(hand, Model.ADD_FRIEND_LIST, Json));*/
			        
			        	                Toast.makeText(getApplicationContext(),
			        	                        "You clicked on YES", Toast.LENGTH_SHORT)
			        	                        .show();
			        	            }
			        	        });
			        	// Setting Negative "NO" Btn
			        	alertDialog2.setNegativeButton("NO",
			        	        new DialogInterface.OnClickListener() {
			        	            public void onClick(DialogInterface dialog, int which) {
			        	                // Write your code here to execute after dialog
			        	                Toast.makeText(getApplicationContext(),
			        	                        "You clicked on NO", Toast.LENGTH_SHORT)
			        	                        .show();
			        	                dialog.cancel();
			        	            }
			        	        });

			        	// Showing Alert Dialog
			        	alertDialog2.show();

			        }
			    });

				Handler hand = new Handler() {
					public void handleMessage(android.os.Message msg) {
						super.handleMessage(msg);
						
					};
				};				

	        	
				mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

					@Override
					public void onMarkerDragStart(Marker arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMarkerDragEnd(Marker marker) {
						Geocoder gc = new Geocoder(MapMainActivity.this);
						List<Address> list = null;
						LatLng ll = marker.getPosition();
						try {
							list = gc.getFromLocation(ll.latitude,
									ll.longitude, 1);
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}

						Address add = list.get(0);
						marker.setTitle(add.getLocality());
						marker.setSnippet(add.getCountryName());
						marker.showInfoWindow();
					}

					@Override
					public void onMarkerDrag(Marker arg0) {
						// TODO Auto-generated method stub

					}
				});

			}
		}
		return (mMap != null);
	}

	private void gotoLocation(double lat, double lng) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
		mMap.moveCamera(update);
	}

	private void gotoLocation(double lat, double lng, float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.moveCamera(update);
	}

	public void geoLocate(View v) throws IOException {
		hideSoftKeyboard(v);

		EditText et = (EditText) findViewById(R.id.editText1);
		String location = et.getText().toString();

		Geocoder gc = new Geocoder(this);
		List<Address> list = gc.getFromLocationName(location, 1);
		Address add = list.get(0);
		locality = add.getLocality();
	//	Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

		double lat = add.getLatitude();
		double lng = add.getLongitude();

		gotoLocation(lat, lng, DEFAULTZOOM);
		if (marker != null) {
			marker.remove();
		}
		MarkerOptions options = new MarkerOptions().title(locality).position(
				new LatLng(lat, lng));
		marker = mMap.addMarker(options);
		// for(int i = 0; i < 3; ++i){
		// Marker markerTmp =
		// }
		retriveNearbyList(String.valueOf(lat), String.valueOf(lng),
				String.valueOf(radius));

	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		MapStateManager mgr = new MapStateManager(this);
		mgr.saveMapState(mMap);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MapStateManager mgr = new MapStateManager(this);
		CameraPosition position = mgr.getSavedCameraPosition();
		if (position != null) {
			CameraUpdate update = CameraUpdateFactory
					.newCameraPosition(position);
			mMap.moveCamera(update);
			// This is part of the answer to the code challenge
			mMap.setMapType(mgr.getSavedMapType());
		}
	}

	protected void gotoCurrentLocation() {
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation == null) {
			Toast.makeText(this, "Current location isn't available",
					Toast.LENGTH_SHORT).show();
		} else {
			LatLng ll = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,
					DEFAULTZOOM);
			mMap.animateCamera(update);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Toast.makeText(this, "Connected to location service",
				Toast.LENGTH_SHORT).show();
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(5000);
		request.getFastestInterval();
		mLocationClient.requestLocationUpdates(request, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	private double distance(double lat, double lng, double Latitude,
			double Longitude) {

		double earthRadius = 3958.75; // in miles, change to 6371 for kilometer
										// output

		double dLat = Math.toRadians(Latitude - lat);
		double dLng = Math.toRadians(Longitude - lng);

		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);

		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat))
				* Math.cos(Math.toRadians(Latitude));

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		double dist = earthRadius * c;

		return dist; // output distance, in MILES
	}

	private void setMarker(String locality, String country, double lat,
			double lng) {
		LatLng ll = new LatLng(lat, lng);

		MarkerOptions options = new MarkerOptions()
				.title(locality)
				.position(new LatLng(lat, lng))
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_mapmarker))
				// .icon(BitmapDescriptorFactory.defaultMarker())
				.anchor(.5f, .5f).draggable(true);
		if (country.length() > 0) {
			options.snippet(country);
		}

		if (marker != null) {
			removeEverything();
		}

		marker = mMap.addMarker(options);
	}

	private void removeEverything() {
		marker.remove();
		marker = null;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
	}

	private void retriveNearbyList(String latitude, String longitude,
			String radius) {
		// latitude longitude function(address);
		String Json = "{\"latitude\":\"" + latitude + "\","
				+ "\"longitude\":\"" + longitude + "\"," + "\"radius\":\""
				+ radius + "\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand,
				Model.RETRIEVE_NEARBY_LIST, Json));
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(MapMainActivity.this,
						"Server error! Request failed!", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(MapMainActivity.this, "Server no response", 1)
						.show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result == null) {
					Toast.makeText(MapMainActivity.this,
							"Server no response, please check network", 1)
							.show();
					return;
				}
				// handle result json myJson.method(); addList
				nearKidList = myJson.getNearKidList(result);
				
				if (nearKidList != null && !nearKidList.isEmpty()){
					
				
				for (Marker oldMarker : markerList) {
					oldMarker.remove();
				}
				markerList.clear();
				for (NearByKid kid : nearKidList) {
					String snippet = kid.getChildAge() + " "
							+ kid.getChildSex()+" "+kid.getUserID();
					MarkerOptions options = new MarkerOptions()
							.title(locality)
							.position(
									new LatLng(kid.getLatitude(), kid
											.getLongitude()))
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
							.title(kid.getUserName()).snippet(snippet);
					Marker markerNew = mMap.addMarker(options);
					markerNew.showInfoWindow();
					markerList.add(markerNew);
				}
				
				}else{
				//	Toast.makeText(MapMainActivity.this, "No kid is found nearby", Toast.LENGTH_LONG).show();
				}

			//	Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				Log.e("NearbyKid", result);

			}
		};
	};



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}