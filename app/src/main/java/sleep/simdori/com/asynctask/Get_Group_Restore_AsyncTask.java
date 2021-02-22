package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.item.GetGroupList;
import sleep.simdori.com.util.DatabaseHandler;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * 서버에 장치추가 요청을 보낸다.
 * 네트워크 통신의 경우 별도의 쓰레드를 사용한다.  
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class Get_Group_Restore_AsyncTask extends AsyncTask<String, Void, Integer> {
	// API
	private DatabaseHandler db = null;
	private SharedPrefUtil pref = null;
	private JsonConnect json 	= null;
	private Locale locale 		= null;

	// Values
	private GetGroupList group;
	private String group_name = "";
	private String user_ID, group_name2, group_device, group_device2 = null;
	private String group_sleeptime= AppConst.Setting_NoTime, group_wakeuptime = AppConst.Setting_NoTime;
	private ArrayList<String> arr_device, group_array = null;
	private String[] group_devices = null;
	private String language	= null;
	private int retry_request = 0;
	private JSONObject job, responseJSON, listJSON = null;
	private JSONArray device_list = null;
	private String response, resultCode, resultMsg = "";
	
	// Views
	private Context context;
	private ProgressBar pb;
	
	/**
	 * @param context		: 호출한 액티비티
	 * @param pb			: 프로그레스바
	 * @param user_id		: 사용자 ID
	 * @param device		: 공유받은 장치 정보
	 */
	public Get_Group_Restore_AsyncTask(Context context, ProgressBar pb, String user_ID) {
		this.context = context;
		this.pb = pb;
		this.user_ID = user_ID;
		
		// API
		db = new DatabaseHandler(context);
		json = new JsonConnect(context, AppConst.Device_GetGroup_host);
		
		// 재시도 횟수
		pref = new SharedPrefUtil(context);
		retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"GetGroup", 0);
		
		// 현재언어 가져오기
		locale = context.getResources().getConfiguration().locale;
		language =  locale.getLanguage();
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 현재언어는 " + language);
	}	

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pb.setVisibility(View.VISIBLE);
	}
	
	/** 
	 * <Request>
	 * uri			: http://simdori:3000/acceptUseIOT;
	 * id			: 사용자 ID
	 * device_mac 	: 공유받은 장치 MAC주소
	 * sharing_id 	: 상대방의 ID
	 * lang			: 현재 언어
	 * <Response>
	 * resultCode	: 1(성공)
	 * resultMsg	: 결과 메시지
	 * @see AsyncTask#doInBackground(Object[])
	 */
	@Override
	protected Integer doInBackground(String... arg0) {
		// 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
		if(retry_request > AppConst.Network_Retry_Baseline) {
			return AppConst.Network_Failed_RetryOver;
		} else {
			try {
				job = new JSONObject();
				job.put("id", user_ID);
				job.put("lang", language);
				//job.put("unique_id",xxxxxx );
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / Data_Request = " + job.toString());
				 
				// JSON 처리 
				response = json.Connect(job);
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / Data_Response = " + response);
				
				if (response != null) {
					responseJSON = new JSONObject(response);
					resultCode = responseJSON.getString("resultCode");
					resultMsg = responseJSON.getString("resultMsg");
					if(resultCode.equals(AppConst.Network_ResultMSG_Success)) {
						device_list = responseJSON.getJSONArray("data");
						if(device_list != null) {
							if(device_list.length()>AppConst.QUEUE_ZERO) db.deleteGroupDB();
							arr_device = db.getDeviceMac();
							
							for(int i=0; i<device_list.length(); i++){
								// 1. 장치리스트 저장
								try {
									listJSON = device_list.getJSONObject(i);
									group_name2 = listJSON.getString("group_name");
									group_device2 = listJSON.getString("group_device_mac");

									// 20170513 yjjeon : group_device2(group_device_mac) 없어도 그룹을 보여준다
//									if(!arr_device.contains(group_device2)) continue;
									
									if(listJSON.has("group_sleeptime")) group_sleeptime = listJSON.getString("group_sleeptime");
									if(listJSON.has("group_wakeuptime")) group_wakeuptime = listJSON.getString("group_wakeuptime");
									if(group_name2!=null && !group_name.equals(group_name2)) {
										group_name = group_name2;
			
										group_array = new ArrayList<String>();
										group_array.add(group_device2);
										
										group = new GetGroupList(group_name2, group_device2, AppConst.DEVICE_OFFLINE, 
												group_sleeptime, group_wakeuptime, AppConst.Setting_NoDay, AppConst.Setting_NoDay,  
												AppConst.Group_AI_NoSetting, AppConst.Group_AI_NoSetting, user_ID);
										db.addGroup(group);
										if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 그룹목록 추가 - 이름: " + group_name2
												+ " / 장치: " + group_device2);
									} else {
										group_array.add(group_device2);
										group_devices = (String[]) group_array.toArray(new String[group_array.size()]);
										group_device = convertArrayToString(group_devices);
										
										group.setKeyGroupDevices(group_device);
										int update = db.updateGroup2(group);
										if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 그룹목록 업데이트 - 이름: " + group_name
												+ " / 장치: " + group_device + "업데이트 여부: " + update);
									}
								} catch(Exception e) {
									e.printStackTrace();
									if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 그룹목록 추가 오류");
									
									return AppConst.Network_Failed_Data;
								}
							}
							return AppConst.Network_Success;
						} else {
							// 아래에서 처리
						}
					} else {
						if(resultMsg.equals(AppConst.Network_ResultMSG2_NO_DATA_GROUP)) return AppConst.Network_Failed_Setting;
					}
				} else {
					return AppConst.Network_Failed_Response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return AppConst.Network_Failed_Connection;
			}
			return AppConst.Network_Failed_Data;
		}
	}
	
	/**
	 * 연결 성공한 경우, 장치를 추가한다. 
	 * 나머지의 경우에는 알림창을 보여준다. 
	 * @param status : 결과값
	 * @see AsyncTask#onPostExecute(Object)
	 */
	@Override
	protected void onPostExecute(Integer status) {
		super.onPostExecute(status);
		pb.setVisibility(View.GONE);
		
		switch (status) {
			case AppConst.Network_Success:
//				if(AppUI.getHomeActivity()!=null) AppUI.getHomeActivity().refreshSocket();
//				if(AppUI.getGroupListActivity() != null) AppUI.getGroupListActivity().refreshGroup();
//				ToastUtils.ToastShow(context, R.string.group_Save_Restore_Success);
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 서버 연결 성공!!");
				break;
			
			case AppConst.Network_Failed_Setting:
				ToastUtils.ToastShow(context, R.string.group_Save_Restore_NoGroup);
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 그룹 없음: " + resultMsg);
				break;
			
			case AppConst.Network_Failed_RetryOver:
				// 재시도 횟수 초기화
				pref.put(SharedPrefUtil.RETRY_REQUEST+"GetGroup", 0);
				ToastUtils.ToastShow(context, context.getString(R.string.msg_mqtt_fetch_error));
				if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 서버 연결 실패: " + resultMsg);
				break;	
				
			case AppConst.Network_Failed_Data:
			case AppConst.Network_Failed_Response:
			case AppConst.Network_Failed_Connection:
			default:
				// 재시도 횟수 증가
				pref.put(SharedPrefUtil.RETRY_REQUEST+"GetGroup", retry_request+1);
				if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 서버에서 결과값 획득실패: " + resultMsg);
				
				try {
					Thread.sleep(100);
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - Get_Group_Restore_AsyncTask / 100ms 후 재연결");
				} catch (InterruptedException e) {
					// 다른 곳에서 알림 처리
					e.printStackTrace();
				}
				
				// 연결 재시도!!
				if(Build.VERSION.SDK_INT < AppConst.HONEYCOMB) {
					new sleep.simdori.com.asynctask.Get_Group_Restore_AsyncTask(context, pb, user_ID).execute();
				} else {
					new sleep.simdori.com.asynctask.Get_Group_Restore_AsyncTask(context, pb, user_ID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				return;
		}
	}
	
	/**
	 * 입력받은 배열을 문자로 변환한다.  
	 * @param 	array : 배열
	 * @throws 	None
	 * @return 	String : 문자값
	 */
	public static String convertArrayToString(String[] array){
	    String str = "";
	    String strSeparator = "__,__";
	    for (int i = 0;i<array.length; i++) {
	        str = str+array[i];
	        // Do not append comma at the end of last element
	        if(i<array.length-1){
	            str = str+strSeparator;
	        }
	    }
	    return str;
	}
	
	/**
	 * 입력받은 문자를 배열로 변환한다.  
	 * @param 	str : 문자값
	 * @throws 	None
	 * @return 	String[] : 배열
	 */
	public static String[] convertStringToArray(String str){
		String strSeparator = "__,__";
		String[] arr = str.split(strSeparator);
	    return arr;
	}
}
