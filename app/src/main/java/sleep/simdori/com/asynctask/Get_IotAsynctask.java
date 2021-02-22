package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Locale;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;

/**
 * 서버에 user의 unique_id를 요청한다.
 * 네트워크 통신의 경우 별도의 쓰레드를 사용한다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class Get_IotAsynctask extends AsyncTask<String, Void, Integer> {
    // API
    private SharedPrefUtil pref = null;
    private JsonConnect json 	= null;
    private Locale locale 		= null;

    // Values
    private String id, language = null;
    private int retry_request = 0;
    private JSONObject job, responseJSON = null;
    private String response, resultCode, resultMsg = "";
    private String device_mac = "";

    // Views
    private Context context;

    /**
     * @param context		: 호출한 액티비티
     * @param id			: 사용자 입력 아이디
     */
    public Get_IotAsynctask(Context context, String id) {
        this.context = context;
        this.id = id;

        // 재시도 횟수
        pref = new SharedPrefUtil(context);
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"getDeviceMac", 0);

        // 연결 정보
        json = new JsonConnect(context, AppConst.getDeviceMac_host);

        // 현재언어 가져오기
        locale = context.getResources().getConfiguration().locale;
        language =  locale.getLanguage();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "getDeviceMac 현재언어는 " + language);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * <Request>
     * uri			: http://simdori.com:3000/getDeviceMac;
     * id		    : 사용자 입력 아이디
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
                job.put("id", id);
                job.put("lang", language);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "getDeviceMac Data_Request = " + job.toString());

                // Json Data 처리
                response = json.Connect(job);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "getDeviceMac Data_Response = " + response);

                if(response != null) {
                    responseJSON = new JSONObject(response);

                    resultCode = responseJSON.getString("resultCode");
                    resultMsg = responseJSON.getString("resultMsg");
                    device_mac = responseJSON.getString("value");
                    pref.put(SharedPrefUtil.DEVICE_MAC, device_mac);
                    System.out.println(device_mac);

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
}
