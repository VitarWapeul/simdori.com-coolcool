package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.Locale;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * 서버에 회원 탈퇴 요청을 보낸다.
 * 네트워크 통신의 경우 별도의 쓰레드를 사용한다.  
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class Set_SecessionAsyncTask extends AsyncTask<String, Void, Integer>{
	// API
	private SharedPrefUtil pref = null;
	private JsonConnect json 	= null;
	private Locale locale 		= null;

	// Values
	private String id, pw = null;
	private String language = null;
	private int retry_request = 0;
	private JSONObject job, responseJSON = null;
	private String response, resultCode, resultMsg = "";

	// Views
	private Context context;
	private ProgressBar pb;

	/**
	 * @param context		: 호출한 액티비티
	 * @param pb			: 프로그레스바
	 * @param id			: 사용자 아이디
	 * @param pw			: 사용자 비밀번호
	 */
	public Set_SecessionAsyncTask(Context context, ProgressBar pb, String id, String pw) {
		this.context = context;
		this.pb = pb;
		this.id = id;
		this.pw = pw;

		// 재시도 횟수
		pref = new SharedPrefUtil(context);
		retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"Secession", 0);

		// 연결 정보
		json = new JsonConnect(context, AppConst.Delete_host);

		// 현재언어 가져오기
		locale = context.getResources().getConfiguration().locale;
		language =  locale.getLanguage();
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원탈퇴 현재언어는 " + language);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pb.setVisibility(View.VISIBLE);
	}

	/**
	 * <Request>
	 * uri			: http://simdori.com:3000/delete;
	 * id			: 사용자 아이디
	 * password 	: 사용자 비밀번호
	 * lang			: 현재 언어
	 * <Response>
	 * resultCode	: 1(성공)
	 * resultMsg	: 결과 메시지
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Integer doInBackground(String... arg0) {
		// 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
		if(retry_request > 2) {
			return AppConst.Network_Failed_RetryOver;
		} else {
			try {
				job = new JSONObject();
				job.put("id", id);
				job.put("password", pw);
				job.put("lang", language);
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Secession Data_Request = " + job.toString());

				// JSON 처리
				response = json.Connect(job);
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Secession Data_Response = " + response);

				if(response != null) {
					responseJSON = new JSONObject(response);
					resultCode = responseJSON.getString("resultCode");
					resultMsg = responseJSON.getString("resultMsg");
					if (resultCode.equals(AppConst.Network_ResultMSG_Success)) {
						return AppConst.Network_Success;
					} else {
						return AppConst.Network_Failed_Data;
					}
				} else {
					return AppConst.Network_Failed_Response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return AppConst.Network_Failed_Connection;
			}
		}
	}

	/**
	 * 연결 성공한 경우, 사용자 정보를 초기화하고 로그아웃한다.
	 * 나머지의 경우에는 알림창을 보여준다.
	 * @param status : 결과값
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer status) {
		super.onPostExecute(status);
		pb.setVisibility(View.GONE);

		switch (status) {
			case AppConst.Network_Success:
				try {
					// 로그아웃
					ToastUtils.ToastShow(context, resultMsg + "");
					if(AppUI.getModifyActivity()!=null) AppUI.getModifyActivity().onSusseionComplete();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원탈퇴 성공: " + resultMsg);
				} catch(Exception e) {
					e.printStackTrace();
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원탈퇴 처리 실패");

					// 서버 연결 재시도
					pref.put(SharedPrefUtil.RETRY_REQUEST+"Secession", retry_request+1);
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
						new Set_SecessionAsyncTask(context, pb, id, pw).execute();
					} else {
						new Set_SecessionAsyncTask(context, pb, id, pw).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					return;
				}
				break;

			case AppConst.Network_Failed_Data:
				ToastUtils.ToastShow(context, resultMsg + "");
				if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원탈퇴 실패: " + resultMsg);
				break;

			case AppConst.Network_Failed_Connection:
			case AppConst.Network_Failed_Response:
				// 재시도 횟수 증가
				pref.put(SharedPrefUtil.RETRY_REQUEST+"Secession", retry_request+1);
				if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원탈퇴 / 서버에서 결과값 획득실패: " + status);

				try {
					Thread.sleep(100);
					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원탈퇴 / 100ms 후 재연결");
				} catch (InterruptedException e) {
					// 다른 곳에서 알림 처리
					e.printStackTrace();
				}

				// 서버 연결 재시도
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					new Set_SecessionAsyncTask(context, pb, id, pw).execute();
				} else {
					new Set_SecessionAsyncTask(context, pb, id, pw).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				return;

			case AppConst.Network_Failed_RetryOver:
				// 재시도 횟수 초기화
				pref.put(SharedPrefUtil.RETRY_REQUEST+"Secession", 0);
				ToastUtils.ToastShow(context, context.getString(R.string.msg_mqtt_fetch_error));
				if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "회원탈퇴 / Retry Failed: " + retry_request);
				break;

			default:
				// 재시도 횟수 초기화
				pref.put(SharedPrefUtil.RETRY_REQUEST+"Secession", 0);
				ToastUtils.ToastShow(context, context.getString(R.string.msg_wall_fetch_error));
				if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원탈퇴 / 네트워크 연결 오류");
				break;
		}
	}
}
