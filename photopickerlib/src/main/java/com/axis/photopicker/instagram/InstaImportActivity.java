package com.axis.photopicker.instagram;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.axis.photopicker.datastore.PhotoPickerDB;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.axis.photopicker.utils.BusProvider;
import com.axis.photopicker.utils.InstaLoginEvent;

public class InstaImportActivity extends Activity {
	private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
	public static final String APIURL = "https://api.instagram.com/v1";
	public static String CALLBACKURL = "http://www.4axissolutions.com";
//	public static String CLIENT_ID = "a0cf0c0f247d459b8c439bb0a009ed44";
//	public static String CLIENT_SECRET = "5e5ff3ee98b84e08bd07a0071573e94d";

	public static String CLIENT_ID = PhotoPickerDB.getInstance().getInstaApiKey();
	public static String CLIENT_SECRET = PhotoPickerDB.getInstance().getInstaApiSecret();

	String authURLString = AUTHURL
			+ "?client_id="
			+ CLIENT_ID
			+ "&redirect_uri="
			+ CALLBACKURL
			+ "&response_type=code&display=touch&scope=likes+comments+relationships";
	String tokenURLString = TOKENURL + "?client_id=" + CLIENT_ID
			+ "&client_secret=" + CLIENT_SECRET + "&redirect_uri="
			+ CALLBACKURL + "&grant_type=authorization_code";
	LinearLayout mainView;
	WebView wv;
	RequestQueue queue;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queue = Volley.newRequestQueue(this);
		mainView = new LinearLayout(this);
		wv = new WebView(this);
		mainView.addView(wv);
		setContentView(mainView);

		loadInstaLogin();
	}
	
	private static final int GALLERY_REQUEST = 45646;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == GALLERY_REQUEST) {
			setResult(GALLERY_REQUEST, data);
			finish();
		}else{
			setResult(RESULT_CANCELED , new Intent());
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		BusProvider.getInstance().post(new InstaLoginEvent(false, null));
	}




	private void loadInstaLogin() {
		wv.setVerticalScrollBarEnabled(true);
		wv.setHorizontalScrollBarEnabled(true);
		wv.setWebViewClient(new AuthWebViewClient());
		wv.getSettings().setJavaScriptEnabled(true);
		wv.loadUrl(authURLString);
	}

	private void saveInstaSession(InstaUser user) {
		SharedPreferences pref = getSharedPreferences(InstagramFragment.filename, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("INSTALOGIN", true);
		editor.putString("INSTATOKEN", user.getAccess_token());
		editor.putString("INSTAUNAME", user.getUser().getUsername());
		editor.putString("INSTAPROFILE", user.getUser().getProfile_picture().toString());
		//Toast.makeText(getApplicationContext(),  user.getUser().getProfile_picture().toString(), Toast.LENGTH_LONG).show();
		editor.commit();
	}

	private String getInstaSession() {
		SharedPreferences pref = getSharedPreferences(InstagramFragment.filename, 0);
		boolean isLoggedIn = pref.getBoolean("INSTALOGIN", false);
		if (isLoggedIn) {
			return pref.getString("INSTATOKEN", "");
		} else {
			return null;
		}
	}

	private void getAccessToken(final String request_token) {
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				tokenURLString, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Gson gson = new Gson();
						InstaUser instaUser = gson.fromJson(response,
								InstaUser.class);
						//String accessToken = instaUser.getAccess_token();
						saveInstaSession(instaUser);
						BusProvider.getInstance().post(new InstaLoginEvent(true, null));

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						//Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
						BusProvider.getInstance().post(new InstaLoginEvent(false, null));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("client_id", CLIENT_ID);
				params.put("client_secret", CLIENT_SECRET);
				params.put("grant_type", "authorization_code");
				params.put("redirect_uri", CALLBACKURL);
				params.put("code", request_token);
				return params;
			}
		};
		queue.add(stringRequest);
	}

	public class AuthWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(CALLBACKURL)) {
				System.out.println(url);
				String parts[] = url.split("=");
				try {
					String request_token = parts[1];
					getAccessToken(request_token);
					//BusProvider.getInstance().post(new InstaLoginEvent(true, getInstaSession()));
				} catch (Exception e) {
					e.printStackTrace();
					BusProvider.getInstance().post(new InstaLoginEvent(false, null));
				} finally {
					finish();

				}

				return true;
			}
			return false;
		}
	}
	

	class InstaUser {
		private String access_token;
		private User user;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		class User {
			private String id;
			private String username;
			private String fullname;
			private String profile_picture;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getUsername() {
				return username;
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public String getFullname() {
				return fullname;
			}

			public void setFullname(String fullname) {
				this.fullname = fullname;
			}

			public String getProfile_picture() {
				return profile_picture;
			}

			public void setProfile_picture(String profile_picture) {
				this.profile_picture = profile_picture;
			}

		}
	}
	
	
}
