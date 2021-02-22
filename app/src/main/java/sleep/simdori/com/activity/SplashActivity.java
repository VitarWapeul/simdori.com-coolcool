package sleep.simdori.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.asynctask.Set_Splash_LoginAsyncTask;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * 앱 로딩 시 특정 이미지를 보여주고,
 * 암호화 여부를 체크하고 장치 고유ID를 저장하며,
 * 자동로그인을 처리한다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class SplashActivity extends Activity {
	// API
	private SharedPrefUtil pref = null;
	private Intent intent = null;
	private AsyncTask<String, Void, Integer> mAsyncTask_Login = null;

	// MQTT
	private InputStream is_ca, is_crt, is_key = null;
	private FileOutputStream fos_ca, fos_crt, fos_key = null;
	private File outDir, file_ca, file_crt, file_key = null;

	// Values
	private String user_id, user_pw, unique_ID 	= null;
	private int login_status = 0;
	private boolean aes_status, mqtt_ssl_status = false;

	// Views
	private Activity mActivity;
	private ProgressBar pb;
	private ImageView bg;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppUI.setSplashActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppUI.setSplashActivity(null);
	}

	@Override
	protected void onDestroy() {
		// 실행되고 있는 AsyncTask 종료
		if(mAsyncTask_Login != null) mAsyncTask_Login.cancel(true);

		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		AppUI.setSplashActivity(this);
		mActivity = this;

		// 에니메이션 처리
		initViews();

		// 프로젝트 키 값 얻기
		getProjectKey();

		// 사용자 정보 가져오기
		pref = new SharedPrefUtil(mActivity);
		user_id = pref.getValue(SharedPrefUtil.USER_ID, "");
		user_pw = pref.getValue(SharedPrefUtil.USER_PASSWORD, "");
		// 기기 정보 가져오기
		unique_ID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		pref.put(SharedPrefUtil.UNIQUE_ID, unique_ID);
		pref.put(SharedPrefUtil.WIFI_CONNECTION, false);
		// 화재알람 여부
		pref.put(SharedPrefUtil.ALERT_FIRE_STATUS, true);

		// 로그인 상태 체크 (로그아웃: 0/ 로그인: 1/ 자동로그인: 2)
		login_status = pref.getValue(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Logout);

		// 센서 처리 유무 - True
		pref.put(SharedPrefUtil.SENSOR_STATUS, true);
		// 응답 처리 여부 - True
		pref.put(SharedPrefUtil.RESPONSE_PROCESSING, true);
		// AES 암호화 여부 - True
		pref.put(SharedPrefUtil.AES_STATUS, true);

		// MQTT 암호화 여부 - True
		pref.put(SharedPrefUtil.MQTT_SSL, true);
		// MQTT 암호화 여부 - Azure
		//pref.put(SharedPrefUtil.MQTT_SSL, false);

		// 암호화 처리
		aes_status = pref.getValue(SharedPrefUtil.AES_STATUS, false);
		mqtt_ssl_status = pref.getValue(SharedPrefUtil.MQTT_SSL, false);
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - 로그인 여부: " + login_status + " / AES 암호화: " + aes_status + " / MQTT SSL 암호화: " + mqtt_ssl_status);
		if(mqtt_ssl_status) {
			// 파일 경로 저장하기
			outDir = new File(AppConst.MQTT_FilePath); // "/data/sleep.simdori.com/");
			boolean status = outDir.mkdirs();
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - 폴더 경로: " + outDir.getAbsolutePath() + " / 폴더 여부: " + status);

			// 인증서 파일 이동하기
			try {
				is_ca = getAssets().open("all-ca.crt");
				int size = is_ca.available();
				byte[] buffer = new byte[size];
				file_ca = new File(outDir + "/" + "all-ca.crt");
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - CA File 경로: " + file_ca.getAbsolutePath());

				fos_ca = new FileOutputStream(file_ca);
				for (int c=is_ca.read(buffer); c!=-1; c=is_ca.read(buffer)){
					fos_ca.write(buffer, 0, c);
			    }
			    is_ca.close();
			    fos_ca.close();

			    is_crt = getAssets().open("client.crt");
				size = is_crt.available();
				buffer = new byte[size];
				file_crt = new File(outDir + "/" + "client.crt");
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - Clinet File 경로: " + file_crt.getAbsolutePath());

				fos_crt = new FileOutputStream(file_crt);
				for (int c=is_crt.read(buffer); c!=-1; c=is_crt.read(buffer)){
					fos_crt.write(buffer, 0, c);
			    }
				is_crt.close();
			    fos_crt.close();

			    is_key = getAssets().open("client.key");
				size = is_key.available();
				buffer = new byte[size];
				file_key = new File(outDir + "/" + "client.key");
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - KEY File 경로: " + file_key.getAbsolutePath());

				fos_key = new FileOutputStream(file_key);
				for (int c=is_key.read(buffer); c!=-1; c=is_key.read(buffer)){
					fos_key.write(buffer, 0, c);
			    }
				is_key.close();
				fos_key.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// SSL 암호화가 필요없으므로 처리X
		}

		// 스플래시 이미지를 보여주기 위해서, 1000ms 후에 다음화면으로 넘어감
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				startApp();
			}
		}, AppConst.SPLASH_DISPLAY_LENGTH);
	}

	/**
	 * 뷰 생성 및 초기화
	 */
	private void initViews() {
		// 배경화면
		AnimationSet set = new AnimationSet(true);
		set.setInterpolator(new AccelerateInterpolator());

		// 페이드인 효과
		AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
		fadeInAnimation.setDuration(1500);
		set.addAnimation(fadeInAnimation);
		//fadeInAnimation.setFillAfter(true);

		// 페이드 아웃 효과
		AlphaAnimation fadeOutAnimation = new AlphaAnimation(1, 1);
		fadeOutAnimation.setDuration(700);
		set.addAnimation(fadeOutAnimation);
		//fadeOutAnimation.setFillAfter(true);

		// 에니메이션 적용
		bg.setAnimation(fadeInAnimation);
		bg.startAnimation(fadeInAnimation);
		//bg.setAnimation(fadeOutAnimation);
		//bg.startAnimation(fadeOutAnimation);
	}

	/**
	 * 페이스북 연동을 위해 프로젝트 키 값을 가져온다.
	 * @param 	None
	 * @throws 	None
	 * @return 	None
	 */
	private void getProjectKey() {
		try {
		    PackageInfo info = getPackageManager().getPackageInfo("sleep.simdori.com", PackageManager.GET_SIGNATURES);
		    for (Signature signature : info.signatures) {
		        MessageDigest md = MessageDigest.getInstance("SHA");
		        md.update(signature.toByteArray());
		        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		    }
		} catch (PackageManager.NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {	}
	}

	/**
	 * 로그인 상태
	 */
	enum LOGIN_STATUS {
		LOGOUT,
		LOGIN,
		AUTO_LOGIN,
		FACEBOOK_LOGIN;
	}

	/**
	 * 로그인 상태에 따라 로그인화면으로 이동할지 로그인 연결할 지 결정한다.
	 * <Case문 조건 및 결과>
	 * LOGOUT : 로그인 화면으로 이동
	 * LOGIN  : 로그인 화면으로 이동
	 * AUTO_LOGIN : 로그인 연결
	 * @param 	None
	 * @throws 	None
	 * @return 	None
	 */
	public void startApp() {
		switch(LOGIN_STATUS.values()[login_status]) {
			case AUTO_LOGIN:
				// 로그인 연결
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					mAsyncTask_Login = new Set_Splash_LoginAsyncTask(mActivity, pb, user_id, user_pw).execute();
				} else {
					mAsyncTask_Login = new Set_Splash_LoginAsyncTask(mActivity, pb, user_id, user_pw).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				break;

			case LOGOUT:
			case LOGIN:
			case FACEBOOK_LOGIN:
			default:
				onLoginFailed();
				break;
		}
	}

	/**
	 * 로그인 연결되었을 때, 장치목록 초기화 여부를 저장하고 기본화면으로 이동한다.
	 * @param 	login_id: 아이디
	 * @param 	login_pw: 비밀번호
	 * @param 	user_email : 이메일
	 * @param 	user_phone : 전화번호
	 * @throws 	None
	 * @return 	None
	 */
	public void onLoginStatus(String login_id, String login_pw, String user_email, String user_phone) {
		// 1. MQTTservice 시작
		try {
			startService(new Intent(mActivity, MQTTservice.class));
			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "SplashActivity - MQTT Service를 시작하였습니다!!!");
		} catch (Exception e) {
			// 서버에 연결하지 못하였습니다. 네트워크를 확인하거나 잠시 후 다시 시도하십시오.
			ToastUtils.ToastShow(mActivity, getString(R.string.msg_mqtt_fetch_error));
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "SplashActivity - MQTT Service 시작 실패!!!");
			e.printStackTrace();
		}

		// 1-1. MQTT 재연결 여부 저장
		if(login_id.equals(user_id)) {
			pref.put(SharedPrefUtil.MQTT_Connection, true);
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - MQTT 동일한 아이디입니다. ");
		} else {
			pref.put(SharedPrefUtil.MQTT_Connection, false);
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SplashActivity - MQTT 다른 아이디입니다. ");
		}

		// 2. 로그인 정보 저장
		pref.put(SharedPrefUtil.USER_ID, login_id);
		pref.put(SharedPrefUtil.USER_PASSWORD, login_pw);
		pref.put(SharedPrefUtil.USER_EMAIL, user_email);
		pref.put(SharedPrefUtil.USER_PHONE, user_phone);

		// 3. 장치목록 로딩 초기화
		pref.put(SharedPrefUtil.RELOADING_LIST, false);
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "SplashActivity - 자동 로그인되었습니다.");

		// 4. 기본화면으로 이동
		intent = new Intent(mActivity, LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	/**
	 * 로그인 연결이 실패하는 경우, 로그인 화면으로 이동한다.
	 * @param 	None
	 * @throws 	None
	 * @return 	None
	 */
	public void onLoginFailed() {
		// 1. 로그인 여부 초기화
		pref.put(SharedPrefUtil.LOGIN_STATUS, 0);
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "SplashActivity - 로그인 화면으로 이동합니다.");

		// 2. 로그인 화면으로 이동
		intent = new Intent(mActivity, LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
}
