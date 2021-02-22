package sleep.simdori.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.item.GetDeviceList;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.JsonConnect;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

public class SleepTrackingActivity extends AppCompatActivity {

    TimeZone koreaTimeZone;
    Date date;
    DateFormat dateFormat;

    TextView currentTime;
    private SlidrInterface slidr;

//    // for test
//    String device_Mac = "94:76:B7:09:FE:11";
//    String topic = "simdori/" + device_Mac + "/";
//    String message = "REQUEST/5/9/test";
//    private SharedPrefUtil pref = null;
//    Activity mActivity;

//    // MQTT
//    private Messenger service = null;
//    private Bundle data = null;
//    private Message msg = null;
//    boolean publish_status = false;
//
//    private final Messenger serviceHandler = new Messenger(new ServiceHandler());

    // Values
//    private GetDeviceList device = null;
//    private String user_ID, topic_Status, message_Status = null;
//    private int device_Status = 0;
//    private int status 	= 0;
//    private LinkedList<Integer> queue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracking);

        currentTime = (TextView) findViewById(R.id.currentTime);
        dateFormat = new SimpleDateFormat("HH:mm");
        koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(koreaTimeZone);

        slidr = Slidr.attach(this);
//        showCurrentTime();

//        // for test
//        mActivity = this;
//        queue = new LinkedList<Integer>();
//        status = 0;
//        // API
//        pref = new SharedPrefUtil(this);
//        user_ID = pref.getValue(SharedPrefUtil.USER_ID, "");
//
//        // 1. 장치상태 MQTT 발행
//        try {
//            topic_Status = AppConst.Topic + device_Mac + "/";
//            message_Status = AppConst.Msg_Device + user_ID + "/";
//            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "DeviceActivity - MQTT 장치상태 체크 / Publish Topic= " + topic_Status + " - Message= " + message_Status);
//
//        } catch (Exception e) {
//            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "DeviceActivity - MQTT 장치상태 발행 실패 / Publish Topic= " + topic_Status + " - Message= " + message_Status);
//            e.printStackTrace();
//        }
//        msg = Message.obtain(null, MQTTservice.PUBLISH);
//        data = new Bundle();
//        data.putCharSequence(MQTTservice.TOPIC, topic);
//        data.putCharSequence(MQTTservice.MESSAGE, message);
//        msg.setData(data);
//        msg.replyTo = serviceHandler;
//        try {
//            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "DeviceActivity - PublishTopic() / MQTT Publish Attemping...");
//            service.send(msg);
//        } catch (RemoteException e) {
//            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "DeviceActivity - MQTT Publish Failed with exception:" + e.getMessage());
//            e.printStackTrace();
//        }

    }

    private void showCurrentTime(){

        Runnable task = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        date = new Date();
                        currentTime.setText(dateFormat.format(date));
                        Thread.sleep(1000);
                    }catch(InterruptedException e){
                    }
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

//    //for test
//    /**
//     *  바인드된 서비스에 메시지를 전달한다.
//     */
//    @SuppressLint("HandlerLeak")
//    class ServiceHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            // MQTT 상태여부 가져오기
//            data = msg.getData();
//            status = data.getInt(MQTTservice.STATUS);
//
//            // MQTT 상태에 따라 처리
//            switch(status) {
//                case AppConst.MQTT_Success:
//                    queue.clear();
//                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler / MQTT - Message=" + msg.what + " / Status=" + status);
//
//                case AppConst.MQTT_Failed_Connection:
//                    queue.offer(status);
//                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT 연결 실패: " + queue.size());
//                    break;
//
//                case AppConst.MQTT_Failed_Topic_Null:
//                    queue.offer(status);
//                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT TOPIC 처리 실패: " + queue.size());
//                    break;
//
//                case AppConst.MQTT_Failed_Bundle_Null:
//                    queue.offer(status);
//                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT Bundle 처리 실패: " + queue.size());
//                    break;
//
//                case AppConst.MQTT_Failed_Client_Null:
//                    queue.offer(status);
//                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT 클라이언트 연결 실패: " + queue.size());
//                    break;
//
//                case AppConst.MQTT_Failed_Data:
//                    queue.offer(status);
//                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT DATA 처리 실패: " + queue.size());
//                    break;
//
//                default :
//                    super.handleMessage(msg);
//                    break;
//            }
//
//            // 응답 큐가 6개(30초)를 넘기는 경우
//            if(queue.size() > AppConst.Queue_GPIO_BaseLine) {
//                // 2. MQTT 상태 저장
//                pref.put(SharedPrefUtil.MQTT_STATUS, false);
//
//                // 3. 서버에 연결하지 못하였습니다. 네트워크를 확인하거나 잠시 후 다시 시도하십시오.
//                ToastUtils.ToastShow(mActivity, R.string.msg_mqtt_fetch_error);
//                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / 네트워크 연결 실패!!");
//            } else {
//                // 1. MQTT 상태 저장
//                pref.put(SharedPrefUtil.MQTT_STATUS, true);
//            }
//        }
//    }
}
