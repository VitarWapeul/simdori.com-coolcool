package sleep.simdori.com.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;

public class TestAsyncTask extends AsyncTask {

    // API
    private SharedPrefUtil pref = null;
    private JsonConnect json	= null;

    private String id = "vitarbest";

    // Values
    private int retry_request = 0;
    private JSONObject job, responseJSON, responseValue = null;
    private String response, resultCode, resultMsg = "";
    private JSONArray responseArray, responseUsage, responseSaving, responseSdata = null;

    private ArrayList<Float> arr_usages = new ArrayList<Float>();
    private ArrayList<Integer> arr_savings = new ArrayList<Integer>();

    // Views
    private Context context;
    private ProgressBar pb;

    public TestAsyncTask(Context context, ProgressBar pb, String id){
        this.context = context;
        this.pb = pb;
        this.id = id;

        // 재시도 횟수
        pref = new SharedPrefUtil(context);
        retry_request = pref.getValue(SharedPrefUtil.RETRY_REQUEST+"Register", 0);

        // 연결 정보
        json = new JsonConnect(context, AppConst.Device_GetDailyWh_host);

    }

    @Override
    protected Integer doInBackground(Object... objects) {
        try {
            job = new JSONObject();
            job.put("id", id);
//            job.put("device_mac", "94:76:B7:09:FE:10");
//            job.put("lang", "ko");

            // JSON 처리
            response = json.Connect(job);

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

//    /**
//     * <Request>
//     * uri			: http://simdori:3000/updateSocketIOT;
//     * id			: 사용자 ID
//     * device_mac 	: 장치 MAC주소
//     * socket 		: 소켓 번호
//     * socket_img 	: 소켓 아이콘
//     * socket_name 	: 소켓 이름
//     * lang			: 현재 언어
//     *
//     * <Response>
//     * resultCode	: 1(성공)
//     * resultMsg	: 결과 메시지
//     *
//     * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
//     */
//    @Override
//    protected Integer doInBackground(Object... objects) {
//        // 재시도 횟수를 초과하는 경우, 사용자에게 알림 후 종료
//        if(retry_request > AppConst.Network_Retry_Baseline) {
//            return AppConst.Network_Failed_RetryOver;
//        } else {
//            try {
//                job = new JSONObject();
//                job.put("id", id);
//                job.put("device_mac", "94:76:B7:09:FE:10");
//                job.put("lang", "ko");
//                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "DevicePowerActivity - Set_Device_GetDailyWhData_AsyncTask / Data_Request = " + job.toString());
//
//                // JSON 처리
//                response = json.Connect(job);
//                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "DevicePowerActivity - Set_Device_GetDailyWhData_AsyncTask / Data_Response = " + response);
//
//                if (response != null) {
//                    responseJSON = new JSONObject(response);
//                    resultCode = responseJSON.getString("resultCode");
//                    resultMsg = responseJSON.getString("resultMsg");
//                    if (resultCode.equals(AppConst.Network_ResultMSG_Success)) {
//                        // 사용 전력량 가져오기
//                        responseUsage = responseJSON.getJSONArray("usedWh");
//                        for(int i=0; i<responseUsage.length(); i++) {
//                            responseValue = responseUsage.getJSONObject(i);
//                            String value = Double.toString(responseValue.getDouble("value"));
//                            arr_usages.add(Float.valueOf(value));
//                        }
//
//                        // 절감 전력령 가져오기
//                        responseSaving = responseJSON.getJSONArray("savedWh");
//                        for(int i=0; i<responseSaving.length(); i++) {
//                            responseValue = responseSaving.getJSONObject(i);
//                            arr_savings.add(responseValue.getInt("data"));
////							String value = Double.toString(responseValue.getDouble("standByPower"));
////							arr_Sdata.add(Float.valueOf(value));
//                        }
//                    } else {
//                        return AppConst.Network_Failed_Data;
//                    }
//                } else {
//                    return AppConst.Network_Failed_Response;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return AppConst.Network_Failed_Connection;
//            }
//
//            System.out.println(arr_savings);
//            return AppConst.Network_Success;
//        }
//    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(pb!=null) pb.setVisibility(View.VISIBLE);
    }
}
