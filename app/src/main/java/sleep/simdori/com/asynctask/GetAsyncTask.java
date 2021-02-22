package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.Locale;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;


public class GetAsyncTask extends AsyncTask<String, Void, Integer> {

    // API
    private SharedPrefUtil pref = null;
    private JsonConnect json	= null;
    private Locale locale		= null;

    // Values
    private String id, pw, email, phone = null;
    private int loginType = 0;
    private String language = null;
    private JSONObject job, responseJSON = null;
    private int retry_request = 0;
    private String response, resultCode, resultMsg = "";

    // Views
    private Context context;
    private ProgressBar pb;

    /**
     * @param context		: 호출한 액티비티
     * @param pb			: 프로그레스바
     * @param id			: 사용자 입력 아이디
     * @param email			: 사용자 입력 이메일
     * @param pw			: 사용자 입력 비밀번호
     */
    public GetAsyncTask(Context context, ProgressBar pb, String id, String email, String pw, String phone) {
        this.context = context;
        this.pb = pb;
        this.id = id;
        this.pw = pw;
        this.email = email;
        this.phone = phone;

        // 재시도 횟수
        pref = new SharedPrefUtil(context);
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"Register", 0);

        // 연결 정보
        json = new JsonConnect(context, AppConst.Signup_host);

//        // 현재언어 가져오기
//        locale = context.getResources().getConfiguration().locale;
//        language =  locale.getLanguage();
//        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "회원가입 현재언어는 " + language);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        // 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
        if(retry_request > AppConst.Network_Retry_Baseline) {
            return AppConst.Network_Failed_RetryOver;
        } else {
            try {
                job = new JSONObject();
                job.put("id", id);
                job.put("email", email);
                job.put("phone", phone);
                job.put("password", pw);
                job.put("lang", language);
                job.put("loginType", loginType);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Singup_Data Request = " + job.toString());

                // JSON 처리
                response = json.Connect(job);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Singup_Data Response = " + response);

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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(pb!=null) pb.setVisibility(View.VISIBLE);
    }
}
