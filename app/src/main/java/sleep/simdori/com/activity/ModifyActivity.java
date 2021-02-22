package sleep.simdori.com.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.data.OAuthLoginState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.asynctask.Set_ModifyAsyncTask;
import sleep.simdori.com.asynctask.Set_SecessionAsyncTask;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.DatabaseHandler;
import sleep.simdori.com.util.Network;
import sleep.simdori.com.util.PopupUtils;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * 사용자 정보 중 이메일, 비밀번호를 변경한다. 
 *
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class ModifyActivity extends Activity {
	// API
//	private DatabaseHandler db 	= null;
	private SharedPrefUtil pref = null;
	private Pattern p_email, p_phone = null;
	private Matcher m_email, m_phone = null;
	private Intent intent = null;
	private AsyncTask<String, Void, Integer> mAsyncTask_Modify, mAsyncTask_Secession = null;

	// MQTT
	private IntentFilter intentFilter 		= null;
	private StatusReceiver statusReceiver 	= null;

	// Network
	private IntentFilter intentFilter_con 				= null;
	private ConnectivityReceiver connectivityReceiver 	= null;

	// Values
	private String modify_id, modify_pw, modify_pw2, modify_email, modify_phone = "";
	private String user_ID, unique_ID, topic, message, userTopic, valueType = null;
	private String[] messages = null;
	private int login_status = 0;

	// Views
	private Activity mActivity;
	private ProgressBar pb;
	private RelativeLayout touch_layout, top_back;
	private LinearLayout modify_mailinput, modify_phoneinput, modify_pwinput, modify_pwcheck;
	private TextView modify_tv_id;
	private EditText modify_mail_hint, modify_phone_hint, modify_pw_input, modify_pw_check;
	private Button modify_btn_confirm, modify_btn_cancel, modify_btn_secession, btn_dialog;

	/**
	 * 네트워크 상태체크 브로드캐스트 리시버 등록
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		AppUI.setModifyActivity(this);

		// 1. MQTT 브로드캐스트 리시버 등록
		registerReceiver(statusReceiver, intentFilter);
		// 2. 네트워크 브로드캐스트 리시버 등록
		registerReceiver(connectivityReceiver, intentFilter_con);
	}

	/**
	 * 네트워크 상태체크 브로드캐스트 리시버 해제
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		AppUI.setModifyActivity(null);

		// MQTT 브로드캐스트 리시버 해제
		unregisterReceiver(statusReceiver);
		// 네트워크 브로드캐스트 리시버 해제
		unregisterReceiver(connectivityReceiver);
	}

	/**
	 * BroadcastReceiver - MQTT 처리
	 */
	public class StatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent i) {
			topic = i.getStringExtra(MQTTservice.TOPIC);
			message = i.getStringExtra(MQTTservice.MESSAGE);
			if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - StatusReceiver() / MQTT - Topic= " + topic + " ,Message= " + message);

			// 0. MQTT 토픽과 메시지 처리
			try {
				messages = message.split("/");
				valueType = messages[2];
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - StatusReceiver() / MQTT - ValueType=" + valueType);
			} catch (Exception e) {
				// MQTT 토픽을 처리하지 못하였습니다.
				e.printStackTrace();
				PopupUtils.Notify_Exception(mActivity, getString(R.string.dialog_MQTT_FAIL));
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - StatusReceiver() / MQTT - 토픽 처리 오류");
			}

			// 1. 로그아웃 - 고유ID로 장치를 특정하여 로그아웃 처리
			if (userTopic.equals(topic) && unique_ID.equals(valueType)) {
				pref.put(SharedPrefUtil.LOGOUT_MQTT, true);
				onLogout();
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - StatusReceiver() / MQTT - 동시로그인 로그아웃 처리");
			} else {
				// 설정된 토픽만 걸러냈기 때문에 알림X
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - StatusReceiver() / MQTT - 기타 토픽 처리X");
			}
		}
	}

	/**
	 * BroadcastReceiver로부터 수신한 네트워크 변경상태에 따라,
	 * 프로그레스바를 작동시키고 사용자에게 알림을 준다.
	 * @see android.content.BroadcastReceiver
	 */
	public class ConnectivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 네트워크 상태가 변경된 경우
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				// 네트워트 연결실패 시, 토스트로 알리기
				if (!Network.getConnectivityStatus(mActivity)) {
					// 1. 네트워크 연결상태 저장
					pref.put(SharedPrefUtil.NETWORK_CONNECTION, false);

					// 2. 네트워크 연결이 원활하지 않습니다.잠시 후 다시 시도하십시오.
					ToastUtils.ToastShow(mActivity, R.string.msg_wall_fetch_error);
					if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - ConnectivityReceiver() / 네트워크 연결 실패!!!!!!!!!!!");

					// 3. 프로그레스바 시작
					Progressbar_Strat();
				} else {
					// 장치상태 체크가 꺼져 있는 경우
					if(!pref.getValue(SharedPrefUtil.NETWORK_CONNECTION, false)) {
						// 4. 프로그레스바 프로그레스바 종료
						Progressbar_Close();
						if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - ConnectivityReceiver() / 네트워크 연결 성공");
					} else {
						// 네트워크 연결이 되어 있으므로 알림X
					}

					// 5. 네트워크 연결상태 변경
					pref.put(SharedPrefUtil.NETWORK_CONNECTION, true);
				}
			} else {
				// 네트워트 상태가 변경이 된 것이 아니므로 알림X
			}
		}
	}

	/**
	 * 프로그레스바를 작동시킨다.
	 * @return None
	 */
	public void Progressbar_Strat() {
		if(pb!=null) {
			pb.setVisibility(View.VISIBLE);
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "ModifyActivity - ConnectivityReceiver() / PB 시작");
		} else {
			// 뷰와 관련되어 알림X
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "ModifyActivity - ConnectivityReceiver() / PB 시작 실패!");
		}
	}

	/**
	 * 프로그레스바를 정지시킨다.
	 * @return None
	 */
	public void Progressbar_Close() {
		if(pb!=null) {
			pb.setVisibility(View.GONE);
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "ModifyActivity - ConnectivityReceiver() / PB 종료");
		} else {
			// 뷰와 관련되어 알림X
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "ModifyActivity - ConnectivityReceiver() / PB 종료 실패!!!");
		}
	}

	/**
	 * 로그인 상태를 로그아웃으로 변경하고, 로그인화면으로 이동한다.
	 * @return 	None
	 */
	public void onLogout() {
		// 로그인 여부 초기화
		pref.put(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Logout);
		pref.put(SharedPrefUtil.LOGOUT_POPUP, true);

		// 로그인 화면으로 이동
		intent = new Intent(mActivity, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	protected void onDestroy() {
		// 실행중인 팝업 닫기
		if(PopupUtils.getNotity()!=null) PopupUtils.getNotity().dismiss();
		if(pb!=null) pb.setVisibility(View.GONE);

		// 실행되고 있는 AsyncTask 종료
		if(mAsyncTask_Modify != null) mAsyncTask_Modify.cancel(true);
		if(mAsyncTask_Secession != null) mAsyncTask_Secession.cancel(true);

		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

//		// 홈화면으로 이동
//		intent = new Intent(mActivity, HomeActivity.class);
//		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify);
		mActivity = this;

		// 로그인 상태 가져오기
//		db = new DatabaseHandler(this);
		pref = new SharedPrefUtil(this);
		login_status = pref.getValue(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Logout);
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - 로그인 상태: " + login_status);

		// 사용자 정보 가져오기
		modify_id = pref.getValue(SharedPrefUtil.USER_ID, modify_id);
		modify_pw = pref.getValue(SharedPrefUtil.USER_PASSWORD, modify_pw);
		modify_email = pref.getValue(SharedPrefUtil.USER_EMAIL, modify_email);
		modify_phone = pref.getValue(SharedPrefUtil.USER_PHONE, modify_phone);
		user_ID = pref.getValue(SharedPrefUtil.USER_ID, "");
		unique_ID = pref.getValue(SharedPrefUtil.UNIQUE_ID, "");
		userTopic = AppConst.Topic + user_ID + "/";
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "ModifyActivity - 사용자ID = " + user_ID + " / 고유ID = " + unique_ID + " / 이메일 = " + modify_email + " / 핸드폰 = " + modify_phone);

		initViews();
		eventListener();
	}

	/**
	 * Views 생성
	 */
	private void initViews(){
		// 진행바
		pb = (ProgressBar)findViewById(R.id.pb);
		// 이전화면
		top_back = (RelativeLayout) findViewById(R.id.top_back);
		// 키보드 감추기
		touch_layout = (RelativeLayout) findViewById(R.id.touch_layout);

		// MQTT
		intentFilter = new IntentFilter();
		intentFilter.addAction("sleep.simdori.com.MQTT.PushReceived");
		statusReceiver = new StatusReceiver();

		// Network
		intentFilter_con = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		connectivityReceiver = new ConnectivityReceiver();
		registerReceiver(connectivityReceiver, intentFilter_con, null, null);

		modify_mailinput = (LinearLayout) findViewById(R.id.modify_mail);
		modify_phoneinput = (LinearLayout) findViewById(R.id.modify_phone);
		modify_pwinput = (LinearLayout) findViewById(R.id.modify_pw);
		modify_pwcheck = (LinearLayout) findViewById(R.id.modify_pw_check);

		// 현재 ID 보여주기
		modify_tv_id = (TextView)findViewById(R.id.modify_id_tv);
		if (modify_id!=null && !modify_id.equals("")) modify_tv_id.setText(modify_id);

		// 이메일 변경
		modify_mail_hint = (EditText)findViewById(R.id.modify_mail_edit);
		if (modify_email!=null && !modify_email.equals("")) {
			modify_mail_hint.setText(modify_email);
			modify_mail_hint.setSelection(modify_mail_hint.length());
		} else {
			// 뷰와 관련되어 알림X
		}
		// 전화번호 변경
		modify_phone_hint = (EditText)findViewById(R.id.modify_phone_edit);
		if (modify_phone!=null && !modify_phone.equals("")) modify_phone_hint.setText(modify_phone);

		// 비밀번호 변경
		modify_pw_input = (EditText) findViewById(R.id.modify_pw_input_edit);
		if (modify_pw_input!=null && !modify_pw_input.equals("")) modify_pw_input.setText(modify_pw);

		// 비밀번호 확인
		modify_pw_check = (EditText) findViewById(R.id.modify_pw_check_edit);
		if(login_status == AppConst.LoginStatus_Facebook) {
			modify_pw_check.setText(modify_pw);
			modify_mail_hint.setFocusable(false);
			modify_phone_hint.setFocusable(false);
			modify_pw_input.setFocusable(false);
			modify_pw_check.setFocusable(false);
			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Focus - False / " + login_status);
		} else {
			modify_mail_hint.setFocusable(true);
			modify_phone_hint.setFocusable(true);
			modify_pw_input.setFocusable(true);
			modify_pw_check.setFocusable(true);
			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Focus - True / " + login_status);
		}

		// 키보드 감추기
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInputFromWindow(modify_pw_input.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);

		// 취소
		modify_btn_cancel = (Button) findViewById(R.id.modify_btn_cancel);
		// 확인
		modify_btn_confirm = (Button) findViewById(R.id.modify_btn_confirm);
		// 탈퇴
		modify_btn_secession = (Button) findViewById(R.id.modify_btn_secession);
	}

	/**
	 * Events 생성
	 */
	private void eventListener(){
		// 이전화면
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// 키보드 감추기
		touch_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(touch_layout.getWindowToken(), 0);
			}
		});

		// 이메일 입력 시 포커스 주기
		modify_mail_hint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					modify_mail_hint.setHint(null);
					modify_phone_hint.setHint(R.string.Optional_Field);
					modify_pw_input.setHint(R.string.Required_Field);
					modify_pw_check.setHint(R.string.Required_Field);
					modify_mailinput.setBackgroundResource(R.drawable.round_edit_white3);
					modify_phoneinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_pwinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_pwcheck.setBackgroundResource(R.drawable.round_view_white3);
				}
			}
		});

		// 전화번호 입력 시 포커스 주기
		modify_phone_hint.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					modify_mail_hint.setHint(R.string.Required_Field);
					modify_phone_hint.setHint(null);
					modify_pw_input.setHint(R.string.Required_Field);
					modify_pw_check.setHint(R.string.Required_Field);
					modify_mailinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_phoneinput.setBackgroundResource(R.drawable.round_edit_white3);
					modify_pwinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_pwcheck.setBackgroundResource(R.drawable.round_view_white3);
				}
			}
		});

		// 비밀번호 입력 시 포커스 주기
		modify_pw_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					modify_mail_hint.setHint(R.string.Required_Field);
					modify_phone_hint.setHint(R.string.Optional_Field);
					modify_pw_input.setHint(null);
					modify_pw_check.setHint(R.string.Required_Field);
					modify_mailinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_phoneinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_pwinput.setBackgroundResource(R.drawable.round_edit_white3);
					modify_pwcheck.setBackgroundResource(R.drawable.round_view_white3);
				}
			}
		});

		// 비밀번호 재입력 시 포커스 주기
		modify_pw_check.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					modify_mail_hint.setHint(R.string.Required_Field);
					modify_phone_hint.setHint(R.string.Optional_Field);
					modify_pw_input.setHint(R.string.Required_Field);
					modify_pw_check.setHint(null);
					modify_mailinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_phoneinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_pwinput.setBackgroundResource(R.drawable.round_view_white3);
					modify_pwcheck.setBackgroundResource(R.drawable.round_edit_white3);
				}
			}
		});

		// 취소
		modify_btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// 확인
		modify_btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Email 형식만 입력 가능
				String email = modify_mail_hint.getText().toString().trim();
				String phone = modify_phone_hint.getText().toString().trim();
				p_email = Pattern.compile("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
				m_email = p_email.matcher(email);
				// 전화번호 형식만 입력 가능
				p_phone = Pattern.compile("^s|^([0-9]{2,3})-?([0-9]{3,4})-?([0-9]{4})$"); //("^\d{3}-\d{3,4}-\d{4}$");
				m_phone = p_phone.matcher(phone);

				// 비밀번호 입력값
				String pw_input = modify_pw_input.getText().toString().trim();
				String pw_check = modify_pw_check.getText().toString().trim();
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "정보수정 Email:" + email + ", Password: " + pw_input);

				// Email 입력되지 않은 경우
				if(email.equals("")) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_email_null));
					modify_mail_hint.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - Email 입력 오류");

					// Email 형식이 아닌 경우
				} else if (!m_email.find()) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_email_not_form));
					modify_mail_hint.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - Email 형식 오류");

					// 핸드폰 형식이 아닌 경우
				} else if (!phone.equals("") && !m_phone.find()) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_phone_null));
					modify_phone_hint.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - 핸드폰 형식 오류");

					// 비밀번호가 입력되지 않은 경우
				} else if (pw_input.equals("")) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_pw_null));
					modify_pw_input.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - 비밀번호 입력 오류");

					// 비밀번호가 재입력되지 않은 경우
				} else if (pw_check.equals("")) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_pw_check));
					modify_pw_check.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - 비밀번호 재입력 오류");

					// 비밀번호가 서로 다른 경우
				} else if (!pw_input.equals(pw_check)) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_pw_not_same));
					modify_pw_check.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - 비밀번호 확인 오류");

				} else {
					// 수정하기
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 연결 시도!!!");
					if(Build.VERSION.SDK_INT < AppConst.HONEYCOMB) {
						mAsyncTask_Modify = new Set_ModifyAsyncTask(mActivity, pb, modify_id, email, pw_input, phone).execute();
					} else {
						mAsyncTask_Modify = new Set_ModifyAsyncTask(mActivity, pb, modify_id, email, pw_input, phone).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
			}
		});

		// 탈퇴
		modify_btn_secession.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 비밀번호 입력값
				modify_pw = modify_pw_input.getText().toString().trim();
				modify_pw2 = modify_pw_check.getText().toString().trim();

				// 비밀번호가 입력되지 않은 경우
				if (modify_pw.equals("")) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_pw_null));
					modify_pw_input.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - 비밀번호 입력 오류");

					// 비밀번호가 재입력되지 않은 경우
				} else if (modify_pw2.equals("")) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_pw_check));
					modify_pw_check.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - 비밀번호 재입력 오류");

					// 비밀번호가 서로 다른 경우
				} else if (!modify_pw.equals(modify_pw2)) {
					PopupUtils.Notify_Exception(mActivity, getString(R.string.toast_pw_not_same));
					modify_pw_check.requestFocus();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - 비밀번호 확인 오류");

					// 회원 탈퇴
				} else {
					btn_dialog = PopupUtils.Notify(mActivity, getString(R.string.modify_Secession_popup), R.string.Btn_confirm);
					btn_dialog.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(Build.VERSION.SDK_INT < AppConst.HONEYCOMB){
								mAsyncTask_Secession = new Set_SecessionAsyncTask(mActivity, pb, modify_id, modify_pw).execute();
							} else {
								mAsyncTask_Secession = new Set_SecessionAsyncTask(mActivity, pb, modify_id, modify_pw).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
							PopupUtils.getNotity().dismiss();
						}
					});
				}
			}
		});
	}

	/**
	 * 개인정보수정 성공하는 경우, 키보드를 감춘다.
	 * @return 	None
	 */
	public void onModifyComplete(){
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(modify_pw_input.getWindowToken(), 0);
	}

	/**
	 * 회원 탈퇴하는 경우, 관련 정보를 지우고 로그인화면으로 이동한다.
	 * @return 	None
	 */
	public void onSusseionComplete() {
		// 1. 로그인 여부 초기화
		pref.put(SharedPrefUtil.LOGIN_STATUS, AppConst.LoginStatus_Logout);
		pref.put(SharedPrefUtil.USER_ID, "");
		pref.put(SharedPrefUtil.USER_PASSWORD, "");
		pref.put(SharedPrefUtil.LOGOUT_POPUP, true);

		// 2. 그룹 및 장치정보 지우기
//		db.deleteAllGroup(user_ID);
//		pref.put(SharedPrefUtil.DEVICE_REGISTER+user_ID, "");

		// 3. MQTTservice 시작
		try {
			stopService(new Intent(mActivity, MQTTservice.class));
			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "정보수정 - MQTT Service를 중지하였습니다!!!");
		} catch (Exception e) {
			// 서버에 연결하지 못하였습니다. 네트워크를 확인하거나 잠시 후 다시 시도하십시오.
			ToastUtils.ToastShow(mActivity, R.string.msg_mqtt_fetch_error);
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "정보수정 - MQTT Service 중지 실패!!!");
			e.printStackTrace();
		}

		// 4. 로그인 화면으로 이동
		intent = new Intent(mActivity, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}