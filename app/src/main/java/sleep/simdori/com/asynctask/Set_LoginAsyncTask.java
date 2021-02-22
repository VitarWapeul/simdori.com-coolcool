package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.item.GetDeviceList;
import sleep.simdori.com.item.GetMessageList;
import sleep.simdori.com.util.DatabaseHandler;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.RangeCheck;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * 서버에 로그인 요청한다.
 * 네트워크 통신의 경우 별도의 쓰레드를 사용한다.  
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class Set_LoginAsyncTask extends AsyncTask<String, Void, Integer> {
	// API
	private DatabaseHandler db 	= null;
	private SharedPrefUtil pref = null;
	private JsonConnect json 	= null;
	private Locale locale 		= null;

	// Values
	private String user_id, user_pw, unique_ID = null;
	private int loginType = 0;
	private String language	= null;
	private int retry_request = 0;
	private JSONObject job, responseJSON, listJSON, shareJSON = null;
	private JSONArray device_list, share_list = null;
	private String response, resultCode, resultMsg = "";
	private String email = "", phone = "", list, share = "";
	private String device_ID , device_Mac, device_Name="", socket_name ="", fw_builddate="";
	private int device_status, sharing_status, socket, gpio_status = 0;
	private int socket_image, socket_active = AppConst.device_acitve_icon;
	private String sleep_time, wakeup_time, device_sleep_time="", device_wakeup_time="";
	private String sleep_day, wakeup_day, device_sleepday, device_wakeupday;
	private int master_plug, plug_auth, blocker_auth = AppConst.PlugBlocker_NoAuth, current_auth, voice_alarm_auth, temperature_auth, gas_auth, version = 0;
	private double latitue, lontitude;
	private String sharing_id, shared_id, sharing_mac = "";
	private int sharing_popup = 0;
	private GetDeviceList device, socket_list = null;
	private GetMessageList message_list = null;
//	private ArrayList<String> arr_device, device_arr = null;
	
	// Views
	private Context context;
	private ProgressBar pb;
	
	/**
	 * @param context		: 호출한 액티비티
	 * @param pb			: 프로그레스바
	 * @param id			: 사용자 입력 아이디
	 * @param pw			: 사용자 입력 비밀번호
	 * @param pw			: 로그인 타입 (0: 일반 로그인, 1: 페이스북 로그인)
	 */
	public Set_LoginAsyncTask(Context context, ProgressBar pb, String id, String pw, int loginType) {
		this.context = context;
		this.pb = pb;
		this.user_id = id;
		this.user_pw = pw;
		this.loginType = loginType;

		// API
		db = new DatabaseHandler(context);
		json = new JsonConnect(context, AppConst.Login_host);

		/*// 장치 필터링 관리
		arr_device = db.getDeviceMac();
		String device_pre = pref.getValue(SharedPrefUtil.DEVICE_REGISTER+user_id, "");
		String[] devices1 = MainActivity.convertStringToArray(device_pre);
		device_arr = new ArrayList<String>(Arrays.asList(devices1));*/

		// 고유ID 가져오기
		pref = new SharedPrefUtil(context);
		unique_ID = pref.getValue(SharedPrefUtil.UNIQUE_ID, "");
		// 재시도 횟수
		retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"LOGIN", 0);

		// 현재언어 가져오기
		locale = context.getResources().getConfiguration().locale;
		language = locale.getLanguage();
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 현재언어는 " + language);
	}

	public Set_LoginAsyncTask(Context context, String id, String pw) {
		this.context = context;
		this.user_id = id;
		this.user_pw = pw;
		this.loginType = AppConst.LoginType_Default;

		// API
		db = new DatabaseHandler(context);
		json = new JsonConnect(context, AppConst.Login_host);

		/*// 장치 필터링 관리
		arr_device = db.getDeviceMac();
		String device_pre = pref.getValue(SharedPrefUtil.DEVICE_REGISTER+user_id, "");
		String[] devices1 = MainActivity.convertStringToArray(device_pre);
		device_arr = new ArrayList<String>(Arrays.asList(devices1));*/

		// 고유ID 가져오기
		pref = new SharedPrefUtil(context);
		unique_ID = pref.getValue(SharedPrefUtil.UNIQUE_ID, "");
		// 재시도 횟수
		retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"LOGIN", 0);

		// 현재언어 가져오기
		locale = context.getResources().getConfiguration().locale;
		language = locale.getLanguage();
		if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 현재언어는 " + language);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (pb != null) pb.setVisibility(View.VISIBLE);
	}
	
	/** 
	 * <Request>
	 * uri			: http://simdori.com:3000/login;
	 * id			: 사용자 아이디
	 * password		: 사용자 비밀번호
	 * unique_id 	: 기기 고유 아이디
	 * device_info	: 장치 정보
	 * lang			: 현재 언어
	 * <Response>
	 * resultCode	: 1(성공)
	 * resultMsg	: 결과 메시지
	 * email		: 사용자 이메일
	 * list	: "device_mac"		- 장치 맥주소
	 * list	: "device_name"		- 장치 이름
	 * list	: "status"			- 공유 상태
	 * list	: "socket"			- 소켓 번호	
	 * list	: "socket_image"	- 소켓 이미지
	 * list	: "socket_name"		- 소켓 이름
	 * list	: "gpio_status"		- 소켓 상태
	 * list	: "wakeup_time"		- 켜지는 시간
	 * list	: "sleep_time"		- 꺼지는 시간
	 * list	: "device_status"	- 장치 상태
	 * @see AsyncTask#doInBackground(Object[])
	 */
	@Override
	protected Integer doInBackground(String... arg0) {
		// 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
		if (retry_request > AppConst.Network_Retry_Baseline) {
			return AppConst.Network_Failed_RetryOver;
		} else {
			try {
				job = new JSONObject();
				job.put("id", user_id);
				job.put("password", user_pw);
				job.put("unique_id", unique_ID);
				job.put("device_info", "a");
				job.put("loginType", loginType);
				job.put("lang", language);
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / Data_Request = " + job.toString());
				 
				// Json Data 처리

				response = json.Connect(job);
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / Data_Response = " + response);
				
				// 결과값 저장
				if(response != null) {
					responseJSON = new JSONObject(response);
					resultCode = responseJSON.getString("resultCode");
					resultMsg = responseJSON.getString("resultMsg");
					if (resultCode.equals(AppConst.Network_ResultMSG_Success)) {
						email = responseJSON.getString("email");
						if(responseJSON.has("phone")) phone = responseJSON.getString("phone");
						list = responseJSON.getString("device_list").toString();
						share = responseJSON.getString("share_list").toString();
					} else {
						return AppConst.Network_Failed_Data;
					}

					// 3. 장치목록과 공유팝업 저장
					if(!list.equals("noData") && !list.equals("") && !share.equals("") && !share.equals("noShare")) {
						device_list = responseJSON.getJSONArray("device_list");
						share_list = responseJSON.getJSONArray("share_list");
						if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 장치 목록 수: " + device_list.length() + " , 공유팝업 수: " + share_list.length());
					
					// 4. 공유팝업만 저장
					} else if (list.equals("noData") && !share.equals("") && !share.equals("noShare")) {
						share_list = responseJSON.getJSONArray("share_list");
						if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 장치 목록 수: NoData");
					
//					// 5. 장치목록만 저장
//					} else if (!list.equals("noData") && !list.equals("") && share.equals("noShare") ) {
//						device_list = responseJSON.getJSONArray("device_list");
//						if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 장치 목록 수: " + device_list.length() + " ,NoShare ");
					} else {
						// 처리할 장치 및 공유팝업이 없으므로 처리X	
					}

					// 20170624 yjjeon : 요일설정 없는 지나간 시간예약 해제 [[
//					if(list != null) {
//						JSONArray cancelList = responseJSON.getJSONArray("device_list");
//						if(cancelList != null && cancelList.length() > 0 ){
//							for(int i=0; i<cancelList.length(); i++) {
//
//								JSONObject socketJSON = cancelList.getJSONObject(i);
//
//								// 디바이스 아니고 소켓
//								String wakeup_time = socketJSON.getString("wakeup_time");
//								String sleep_time = socketJSON.getString("sleep_time");
//
//								// 디바이스
//								String device_wakeup_time = socketJSON.getString("device_wakeup_time");
//								String device_sleep_time = socketJSON.getString("device_sleep_time");
//
//								String wakeup_day = socketJSON.getString("wakeup_day");
//								String sleep_day = socketJSON.getString("sleep_day");
//								String device_wakeupday = socketJSON.getString("device_wakeupday");
//								String device_sleepday = socketJSON.getString("device_sleepday");
//
//								//device_wakeupday, device_sleepday 값이 있고, 요일설정이 있다면, 진행 x
//								if(!TextUtils.isEmpty(device_wakeupday) && device_wakeupday.contains("1") && !TextUtils.isEmpty(device_sleepday) && device_sleepday.contains("1")){
//									continue;
//								}
//
//								//wakeup_day, sleep_day 값이 있고, 요일설정이 있다면, 진행 x
//								if(!TextUtils.isEmpty(wakeup_day) && wakeup_day.contains("1") && !TextUtils.isEmpty(sleep_day) && sleep_day.contains("1")){
//									continue;
//								}
//
//
//								// device_wakeupday 값이 있고, 요일설정 안되어 있다면 예약날자가 넘어갔는지 체크한다(디바이스)
//								if(isDatePassed(device_wakeupday)){
//									//디바이스는 나중에...(소켓먼저)
//								}
//
//								// device_sleep_time 값이 있고, 요일설정 안되어 있다면 예약날자가 넘어갔는지 체크한다(디바이스)
//								if(isDatePassed(device_sleep_time)){
//									//디바이스는 나중에...(소켓먼저)
//								}
//
//
//								// wakeup_day 값이 있고, 요일설정 안되어 있다면 예약날자가 넘어갔는지 체크한다(소켓)
//								if(isDatePassed(wakeup_day)){
//
//								}
//
//
//								// sleep_day 값이 있고, 요일설정 안되어 있다면 예약날자가 넘어갔는지 체크한다(소켓)
//								if(isDatePassed(sleep_day)){
//
//								}
//
//
//							} //end for
//						} // end list size
//					}
					// ]] 20170624 yjjeon : 요일설정 없는 지나간 시간예약 해제


				} else {
					return AppConst.Network_Failed_Response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return AppConst.Network_Failed_Connection;
			}
			return AppConst.Network_Success;
		}
	}


	private boolean isDatePassed(String date){

		try {
			String temp_date = date;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			//SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date ddd = sdf.parse(temp_date);

			// 현재
			long now = System.currentTimeMillis();
			long elapsed = now - ddd.getTime();

			// ddd.getTime() 이 마이너스 일때, 예약 설정이 안되어 있는것 (0000-00-00 00:00:00 => -62170156800000)
			if(ddd.getTime() < 0) {
				return false;
			}

			// 예약 날짜가 현재 날짜보다 적을겨우
			if(elapsed < 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 예약 날짜가 지나가지 않은 경우
		return false;
	}
		
	/**
	 * 연결 성공한 경우, 로그인 상태를 저장하고 장치 목록을 초기화한다. 
	 * 나머지의 경우에는 알림창을 보여준다. 
	 * @param status : 결과값
	 * @see AsyncTask#onPostExecute(Object)
	 */
	@Override
	protected void onPostExecute(Integer status) {
		super.onPostExecute(status);
		if (pb != null) pb.setVisibility(View.GONE);
		
		// 1. 장치목록 초기화
//		db.deleteDB(0);
//		db.deleteMessageDB();
		
		switch (status) {
			case AppConst.Network_Success:
				// 2. 이메일 형식이 아닌 경우
				if (!RangeCheck.Email(context, email)) {
					// 재시도 횟수 증가
					pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
						new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
					} else {
						new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					return;
					
				// 2-1. 전화번호 형식이 아닌 경우
				} else if (!phone.equals("") && !RangeCheck.Phone(context, phone)) {
					// 재시도 횟수 증가
					pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
						new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
					} else {
						new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					return;
					
					
				// 3. 장치목록과 공유팝업 저장
				} else if (device_list != null && share_list != null) {
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 장치목록과 공유팝업 추가 중");
					
					// 3-1. 공유팝업 체크
					for(int i=0; i<share_list.length(); i++){
						// 3-1-1. 공유팝업 정보 가져오기
						try {
							shareJSON = share_list.getJSONObject(i);
							sharing_id = shareJSON.getString("sharing_id");
							shared_id = shareJSON.getString("shared_id");
							sharing_mac = shareJSON.getString("device_mac");
							sharing_popup = shareJSON.getInt("status");
							message_list = new GetMessageList(sharing_id, shared_id, sharing_mac, sharing_popup, user_id);
							if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 공유팝업: " + sharing_popup);
						} catch (Exception e) {
							e.printStackTrace();
							if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 공유팝업 오류");
							
							// 3-1-2. 로그인 재시도
							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
							} else {
								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
							return;
						}
						
						// 3-1-3. 오류 발견 시 재시도
						if(sharing_id.equals("") || shared_id.equals("") || 
								!RangeCheck.DeviceMac(context, sharing_mac) || !RangeCheck.Sharing_Popup(context, sharing_popup)) {
							// 로그인 재시도
							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
							} else {
								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
							return;
							
						// 3-1-4. 오류가 없는 경우 팝업여부 저장
						} else {
							pref.put(SharedPrefUtil.SHARING_ID, sharing_id);
							pref.put(SharedPrefUtil.SHARED_ID, shared_id);
							pref.put(SharedPrefUtil.SHARING_MAC, sharing_mac);
							pref.put(SharedPrefUtil.SHARING_POPUP, sharing_popup);
							db.addMessage(message_list);
							if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 공유팝업 저장: " + sharing_popup);
						}
					}
					
//					// 3-2. 장치목록 추가
//					for(int i=0; i<device_list.length(); i++){
//						// 3-2-1. 장치리스트 저장
//						try {
//							listJSON = device_list.getJSONObject(i);
//							device_Mac = listJSON.getString("device_mac");
//							device_Name = listJSON.getString("device_name");
//							device_status = listJSON.getInt("device_status");
//							sharing_status = listJSON.getInt("status");
//
//							socket = listJSON.getInt("socket");
//							socket_name = listJSON.getString("socket_name");
//							gpio_status = listJSON.getInt("gpio_status");
//							socket_image = listJSON.getInt("socket_image");
//							if(listJSON.has("socket_active")) socket_active = listJSON.getInt("socket_active");
//
//							sleep_time = listJSON.getString("sleep_time");
//							wakeup_time = listJSON.getString("wakeup_time");
//							sleep_day = listJSON.getString("sleep_day");				// 소켓 꺼지는 요일
//							wakeup_day = listJSON.getString("wakeup_day");				// 소켓 켜지는 요일
//							if(listJSON.has("device_sleep_time")) device_sleep_time = listJSON.getString("device_sleep_time");
//							if(listJSON.has("device_wakeup_time")) device_wakeup_time = listJSON.getString("device_wakeup_time");
//							if(listJSON.has("device_sleepday")) device_sleepday = listJSON.getString("device_sleepday");		// 장치 꺼지는 요일
//							if(listJSON.has("device_wakeupday")) device_wakeupday = listJSON.getString("device_wakeupday");	// 장치 켜지는 요일
//							if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / DeviceMac: " + device_Mac);
//							if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / WakeUp_Day: " + device_wakeupday);
//							if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / Sleep_Day: " + device_sleepday);
//
//							master_plug = listJSON.getInt("master_plug");
//							plug_auth = listJSON.getInt("plug_auth");
//							if(listJSON.has("blocking_auth")) blocker_auth = listJSON.getInt("blocking_auth");
//							current_auth = listJSON.getInt("current_auth");
//							voice_alarm_auth = listJSON.getInt("voice_alarm_auth");
//							temperature_auth = listJSON.getInt("temperature_auth");
//							gas_auth = listJSON.getInt("gas_auth");
//							latitue = listJSON.getDouble("x");
//							lontitude = listJSON.getDouble("y");
//							version = listJSON.getInt("version");
//							if(listJSON.has("fw_date")) fw_builddate = listJSON.getString("fw_date");
//
//						} catch(Exception e) {
//							e.printStackTrace();
//							if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 장치목록 오류");
//
//							// 3-2-2. 로그인 재시도
//							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
//							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
//							} else {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//							}
//							return;
//						}
//
//						// 3-2-3. 오류 발견 시 재시도
//						if(!RangeCheck.DeviceMac(context, device_Mac) || !RangeCheck.DeviceName(context, device_Name) ||
//							!RangeCheck.DeviceStatus(context, device_status) || !RangeCheck.SharingStatus(context, sharing_status) ||
//							!RangeCheck.Socket(context, socket) || !RangeCheck.SocketImg(context, socket_image) ||
//							!RangeCheck.SocketName(context, socket_name) || !RangeCheck.GPIOStatus(context, gpio_status) ||
//							!RangeCheck.Time(context, sleep_time) || !RangeCheck.Time(context, wakeup_time) ||
//							!RangeCheck.Auth(context, plug_auth) || !RangeCheck.Plug(context, master_plug) ||
//							!RangeCheck.Auth(context, current_auth) || !RangeCheck.Auth(context, voice_alarm_auth) ||
//							!RangeCheck.Auth(context, temperature_auth) || !RangeCheck.Auth(context, gas_auth)){
//							// 로그인 재시도
//							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
//							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
//							} else {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//							}
//							return;
//
//						// 3-2-4. 오류가 없는 경우 장치 추가
//						} else {
//							// 장치 추가
//							if (!device_Mac.equals(device_ID)){
//								device = new GetDeviceList();
//								device.setKeyDeviceMac(device_Mac);
//								device.setKeyDeviceName(device_Name);
//								device.setKeyDeviceStatus(device_status);
//								device.setKeyStatus(sharing_status);
//
//								device.setKeySocketImg(R.drawable.backup_button);
//								device.setKeySocketActive(R.drawable.backup_button);
//
//								device.setKeyDevice_WakeUp(device_wakeup_time);
//								device.setKeyDevice_Sleep(device_sleep_time);
//								device.setKeyDevice_WakeUpDay(device_wakeupday);
//								device.setKeyDevice_SleepDay(device_sleepday);
//
//								device.setKeyMasterPlug(master_plug);
//								device.setKeyPlugControl(plug_auth);
//								device.setKeyPlugBlocker(blocker_auth);
//								device.setKeyStanByPower(current_auth);
//								device.setKeyVoiceAlert(voice_alarm_auth);
//								device.setKeyFireAlert(temperature_auth);
//								device.setKeyGasAlert(gas_auth);
//								device.setKeyLatitude(latitue);
//								device.setKeyLongtitude(lontitude);
//								device.setKeyVersion(version);
//								device.setKeyFWtime(fw_builddate);
//								db.addDevice(device);
//								device_ID = device_Mac;
//
//								// 펌웨어 버전 체크
//								if(fw_builddate.equals(AppConst.Device_FWupdate_Status)) pref.put(SharedPrefUtil.FWUPDATE_STATUS, true);
//
//								// 장치상태 플래그 처리 - OFFLINE
//								if(device_status>AppConst.OFF) {
//									pref.put(SharedPrefUtil.DEVICE_STATUS+device_Mac, AppConst.DEVICE_ONLINE);
//								} else {
//									pref.put(SharedPrefUtil.DEVICE_STATUS+device_Mac, AppConst.DEVICE_OFFLINE);
//								}
//								if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 장치목록과 공유팝업 추가 중 - 장치상태: " + device_status);
//
//								/*// 장치 필터링 관리
//								if(AppUI.getMainActivity()!=null && !arr_device.contains(device_Mac)) {
//									device_arr.remove(device_Mac);
//									if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - Set_Device_List_AsyncTask() / 장치목록 추가 중 - 장치 필터링: " + device_Mac);
//								}*/
//							} else {
//								// 장치 추가할 필요가 없으므로 알림X
//							}
//
//							// 소켓 추가
//							socket_list = new GetDeviceList();
//							socket_list.setKeyDeviceMac(device_Mac);
//							socket_list.setKeyDeviceName(device_Name);
//							socket_list.setKeyDeviceStatus(device_status);
//							socket_list.setKeyStatus(sharing_status);
//							socket_list.setKeySocket(socket);
//							socket_list.setKeySocketName(socket_name);
//							socket_list.setKeyGPIO(gpio_status);
//							socket_list.setKeySocketImg(socket_image);
//							socket_list.setKeySocketActive(socket_active);
//							socket_list.setKeyWakeUp(wakeup_time);
//							socket_list.setKeySleep(sleep_time);
//							socket_list.setKeyWakeUpDay(wakeup_day);
//							socket_list.setKeySleepDay(sleep_day);
//							socket_list.setKeyMasterPlug(master_plug);
//							socket_list.setKeyPlugControl(plug_auth);
//							socket_list.setKeyPlugBlocker(blocker_auth);
//							socket_list.setKeyStanByPower(current_auth);
//							socket_list.setKeyVoiceAlert(voice_alarm_auth);
//							socket_list.setKeyFireAlert(temperature_auth);
//							socket_list.setKeyGasAlert(gas_auth);
//							socket_list.setKeyLatitude(latitue);
//							socket_list.setKeyLongtitude(lontitude);
//							socket_list.setKeyVersion(version);
//							socket_list.setKeyFWtime(fw_builddate);
//							db.addDevice(socket_list);
//						}
//					}
				
//				// 4. 장치목록만 저장
//				} else if (device_list != null) {
//					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 장치목록 추가 중");
//
//					// 4-1. 장치목록 추가
//					for(int i=0; i<device_list.length(); i++){
//						// 4-1-1. 장치리스트 저장
//						try {
//							listJSON = device_list.getJSONObject(i);
//							device_Mac = listJSON.getString("device_mac");
//							device_Name = listJSON.getString("device_name");
//							device_status = listJSON.getInt("device_status");
//							sharing_status = listJSON.getInt("status");
//
//							socket = listJSON.getInt("socket");
//							socket_name = listJSON.getString("socket_name");
//							gpio_status = listJSON.getInt("gpio_status");
//							socket_image = listJSON.getInt("socket_image");
//							if(listJSON.has("socket_active")) socket_active = listJSON.getInt("socket_active");
//
//							sleep_time = listJSON.getString("sleep_time");
//							wakeup_time = listJSON.getString("wakeup_time");
//							sleep_day = listJSON.getString("sleep_day");				// 소켓 꺼지는 요일
//							wakeup_day = listJSON.getString("wakeup_day");				// 소켓 켜지는 요일
//							if(listJSON.has("device_sleep_time")) device_sleep_time = listJSON.getString("device_sleep_time");
//							if(listJSON.has("device_wakeup_time")) device_wakeup_time = listJSON.getString("device_wakeup_time");
//							if(listJSON.has("device_sleepday")) device_sleepday = listJSON.getString("device_sleepday");		// 장치 꺼지는 요일
//							if(listJSON.has("device_wakeupday")) device_wakeupday = listJSON.getString("device_wakeupday");	// 장치 켜지는 요일
//
//							master_plug = listJSON.getInt("master_plug");
//							plug_auth = listJSON.getInt("plug_auth");
//							if(listJSON.has("blocking_auth")) blocker_auth = listJSON.getInt("blocking_auth");
//
//							current_auth = listJSON.getInt("current_auth");
//							voice_alarm_auth = listJSON.getInt("voice_alarm_auth");
//							temperature_auth = listJSON.getInt("temperature_auth");
//							gas_auth = listJSON.getInt("gas_auth");
//							latitue = listJSON.getDouble("x");
//							lontitude = listJSON.getDouble("y");
//							version = listJSON.getInt("version");
//							if(listJSON.has("fw_date")) fw_builddate = listJSON.getString("fw_date");
//
//						} catch(Exception e) {
//							e.printStackTrace();
//							if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 장치추가 오류");
//
//							// 4-1-2. 로그인 재시도
//							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
//							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
//							} else {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//							}
//							return;
//						}
//
//						// 4-1-3. 오류 발견 시 재시도
//						if(!RangeCheck.DeviceMac(context, device_Mac) || !RangeCheck.DeviceName(context, device_Name) ||
//							!RangeCheck.DeviceStatus(context, device_status) || !RangeCheck.SharingStatus(context, sharing_status) ||
//							!RangeCheck.Socket(context, socket) || !RangeCheck.SocketImg(context, socket_image) ||
//							!RangeCheck.SocketName(context, socket_name) || !RangeCheck.GPIOStatus(context, gpio_status) ||
//							!RangeCheck.Time(context, sleep_time) || !RangeCheck.Time(context, wakeup_time) ||
//							!RangeCheck.Auth(context, plug_auth) || !RangeCheck.Plug(context, master_plug) ||
//							!RangeCheck.Auth(context, current_auth) || !RangeCheck.Auth(context, voice_alarm_auth) ||
//							!RangeCheck.Auth(context, temperature_auth) || !RangeCheck.Auth(context, gas_auth)){
//							// 로그인 재시도
//							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
//							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
//							} else {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//							}
//							return;
//
//						// 4-1-4. 오류가 없는 경우 장치 추가
//						} else {
//							// 장치 추가
//							if (!device_Mac.equals(device_ID)){
//								device = new GetDeviceList();
//								device.setKeyDeviceMac(device_Mac);
//								device.setKeyDeviceName(device_Name);
//								device.setKeyDeviceStatus(device_status);
//								device.setKeyStatus(sharing_status);
//
//								device.setKeySocketImg(R.drawable.backup_button);
//								device.setKeySocketActive(R.drawable.backup_button);
//
//								device.setKeyDevice_WakeUp(device_wakeup_time);
//								device.setKeyDevice_Sleep(device_sleep_time);
//								device.setKeyDevice_WakeUpDay(device_wakeupday);
//								device.setKeyDevice_SleepDay(device_sleepday);
//
//								device.setKeyMasterPlug(master_plug);
//								device.setKeyPlugControl(plug_auth);
//								device.setKeyPlugBlocker(blocker_auth);
//
//								device.setKeyStanByPower(current_auth);
//								device.setKeyVoiceAlert(voice_alarm_auth);
//								device.setKeyFireAlert(temperature_auth);
//								device.setKeyGasAlert(gas_auth);
//								device.setKeyLatitude(latitue);
//								device.setKeyLongtitude(lontitude);
//								device.setKeyVersion(version);
//								device.setKeyFWtime(fw_builddate);
//
//								db.addDevice(device);
//								device_ID = device_Mac;
//
//
//
//
//
//
//
//
//								// 펌웨어 버전 체크
//								if(fw_builddate.equals(AppConst.Device_FWupdate_Status)) pref.put(SharedPrefUtil.FWUPDATE_STATUS, true);
//
//								// 장치상태 플래그 처리 - OFFLINE
//								if(device_status>AppConst.OFF) {
//									pref.put(SharedPrefUtil.DEVICE_STATUS+device_Mac, AppConst.DEVICE_ONLINE);
//								} else {
//									pref.put(SharedPrefUtil.DEVICE_STATUS+device_Mac, AppConst.DEVICE_OFFLINE);
//								}
//								if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / 장치목록 추가 중 - 장치상태: " + device_status);
//
//								/*// 장치 필터링 관리
//								if(AppUI.getMainActivity()!=null && !arr_device.contains(device_Mac)) {
//									device_arr.remove(device_Mac);
//									if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - Set_Device_List_AsyncTask() / 장치목록 추가 중 - 장치 필터링: " + device_Mac);
//								}*/
//							} else {
//								// 장치 추가할 필요가 없으므로 알림X
//							}
//
//							// 소켓 추가
//							socket_list = new GetDeviceList();
//							socket_list.setKeyDeviceMac(device_Mac);
//							socket_list.setKeyDeviceName(device_Name);
//							socket_list.setKeyDeviceStatus(device_status);
//							socket_list.setKeyStatus(sharing_status);
//
//							socket_list.setKeySocket(socket);
//							socket_list.setKeySocketName(socket_name);
//							socket_list.setKeyGPIO(gpio_status);
//							socket_list.setKeySocketImg(socket_image);
//							socket_list.setKeySocketActive(socket_active);
//
//							socket_list.setKeyWakeUp(wakeup_time);
//							socket_list.setKeySleep(sleep_time);
//							socket_list.setKeyWakeUpDay(wakeup_day);
//							socket_list.setKeySleepDay(sleep_day);
//
//							socket_list.setKeyMasterPlug(master_plug);
//							socket_list.setKeyPlugControl(plug_auth);
//							socket_list.setKeyPlugBlocker(blocker_auth);
//
//							socket_list.setKeyStanByPower(current_auth);
//							socket_list.setKeyVoiceAlert(voice_alarm_auth);
//							socket_list.setKeyFireAlert(temperature_auth);
//							socket_list.setKeyGasAlert(gas_auth);
//							socket_list.setKeyLatitude(latitue);
//							socket_list.setKeyLongtitude(lontitude);
//							socket_list.setKeyVersion(version);
//							socket_list.setKeyFWtime(fw_builddate);
//							db.addDevice(socket_list);
//
//
//
//
//						}
//					}
//
//				// 5. 공유팝업만 저장
//				} else if (share_list != null) {
//					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 공유팝업 추가 중");
//
//					// 5-1. 공유팝업 저장
//					for(int i=0; i<share_list.length(); i++){
//						// 5-1-1. 공유팝업 정보 가져오기
//						try {
//							shareJSON = share_list.getJSONObject(i);
//							sharing_id = shareJSON.getString("sharing_id");
//							shared_id = shareJSON.getString("shared_id");
//							sharing_mac = shareJSON.getString("device_mac");
//							sharing_popup = shareJSON.getInt("status");
//							message_list = new GetMessageList(sharing_id, shared_id, sharing_mac, sharing_popup, user_id);
//							if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - 공유 팝업: " + sharing_popup);
//						} catch (Exception e) {
//							e.printStackTrace();
//							if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 공유팝업 오류");
//
//							// 5-1-2. 로그인 재시도 횟수 증가
//							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
//							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
//							} else {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//							}
//							return;
//						}
//
//						// 5-1-3. 오류 발견 시 재시도
//						if(sharing_id.equals("") || shared_id.equals("") ||
//								!RangeCheck.DeviceMac(context, sharing_mac) || !RangeCheck.Sharing_Popup(context, sharing_popup)) {
//							// 로그인 재시도
//							pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
//							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
//							} else {
//								new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//							}
//							return;
//
//						// 5-1-4. 오류가 없는 경우 팝업여부 저장
//						} else {
//							pref.put(SharedPrefUtil.SHARING_ID, sharing_id);
//							pref.put(SharedPrefUtil.SHARED_ID, shared_id);
//							pref.put(SharedPrefUtil.SHARING_MAC, sharing_mac);
//							pref.put(SharedPrefUtil.SHARING_POPUP, sharing_popup);
//							db.addMessage(message_list);
//							if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 공유 팝업 저장: " + sharing_popup);
//						}
//					}
				} else {
					// 장치목록이 없으므로 알림X
				}
				
				// 기본 화면으로 이동
				if(AppUI.getLoginActivity()!=null) AppUI.getLoginActivity().onLoginStatus(user_id, user_pw, email, phone);
				ToastUtils.ToastShow(context, resultMsg + "");
				break;

			case AppConst.Network_Failed_Data:
				ToastUtils.ToastShow(context, resultMsg + "");
				if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 연결 실패: " + resultMsg);
				break;
			
			case AppConst.Network_Failed_Response:
			case AppConst.Network_Failed_Connection:
				// 재시도 횟수 증가
				pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", retry_request+1);
				if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 서버에서 결과값 획득실패: " + status);
				
				try {
					Thread.sleep(100);
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 100ms 후 재연결");
				} catch (InterruptedException e) {
					// 다른 곳에서 알림 처리
					e.printStackTrace();
				}
				
				// 로그인 재시도
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).execute();
				} else {
					new sleep.simdori.com.asynctask.Set_LoginAsyncTask(context, pb, user_id, user_pw, loginType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				return;
				
			case AppConst.Network_Failed_RetryOver:
				// 로그인 재시도 횟수 초기화
				pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", AppConst.QUEUE_ZERO);
				ToastUtils.ToastShow(context, context.getString(R.string.msg_mqtt_fetch_error));
				if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / Retry Failed: " + retry_request);
				break; 	
				
			default:
				// 재시도 횟수 초기화
				pref.put(SharedPrefUtil.RETRY_REQUEST+"LOGIN", AppConst.QUEUE_ZERO);
				ToastUtils.ToastShow(context, context.getString(R.string.msg_wall_fetch_error));
				if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "LoginActivity - Set_LoginAsynctask / 네트워크 연결 오류");
				break;
		}
	}
}
