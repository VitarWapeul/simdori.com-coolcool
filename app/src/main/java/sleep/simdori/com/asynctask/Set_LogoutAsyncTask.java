package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.R;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

public class Set_LogoutAsyncTask extends AsyncTask {

    // API
    private SharedPrefUtil pref = null;
    private JsonConnect json 	= null;

    // Values
    private String user_id, unique_ID, email = null;
    private int retry_request = 0;
    private String language	= "ko";
    private JSONObject job, responseJSON = null;
    private String response, resultCode, resultMsg = "";

    // Views
    private Context context;


    /**
     * @param context		: 호출한 액티비티
     * @param id			: 사용자 입력 아이디
     */
    public Set_LogoutAsyncTask(Context context, String id, String email) {
        this.context = context;
        this.user_id = id;
        this.email = email;

        // API
        json = new JsonConnect(context, AppConst.Logout_host);

        // 고유ID 가져오기
        pref = new SharedPrefUtil(context);
        unique_ID = pref.getValue(SharedPrefUtil.UNIQUE_ID, "");
        // 재시도 횟수
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"LOGOUT", 0);

    }


    @Override
    protected Object doInBackground(Object[] objects) {
        // 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
        if (retry_request > AppConst.Network_Retry_Baseline) {
            return AppConst.Network_Failed_RetryOver;
        } else {
            try {
                job = new JSONObject();
                job.put("id", user_id);
                job.put("email", email);
                job.put("lang", language);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "SettingFragment - Set_LogoutAsyncTask() / Data_Request = " + job.toString());

                // Json Data 처리

                response = json.Connect(job);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "LoginActivity - Set_LoginAsyncTask() / Data_Response = " + response);

                // 결과값 저장
                if(response != null) {
                    responseJSON = new JSONObject(response);
                    resultCode = responseJSON.getString("resultCode");
                    resultMsg = responseJSON.getString("resultMsg");
                    if (resultCode.equals(AppConst.Network_ResultMSG_Success)) {
                        ToastUtils.ToastShow(context, R.string.user_logout_noti);
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
            return AppConst.Network_Success;
        }
    }
}
