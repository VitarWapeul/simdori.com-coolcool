package sleep.simdori.com.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.LinkedList;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.mqtt.MQTTservice;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mmWaveSleepMeasureFragment extends Fragment implements View.OnClickListener {

    // Values
    String device_mac;
    String userNameValue;

    // View
    TextView mmwaveSleepMeasureDeviceMac;
    TextView UserName;

    // API
    private SharedPrefUtil pref = null;
    private LinkedList<Integer> queue = null;

    // MQTT
    private IntentFilter intentFilter 		= null;
    private Messenger service = null;
    private final Messenger serviceHandler = new Messenger(new ServiceHandler());
    private boolean mBound = false;
    private Message msg = null;
    private Bundle data = null;
    private int status = 0, mid_device = 0 ;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            service = new Messenger(binder);
            mBound = true;

            msg = Message.obtain(null, MQTTservice.REGISTER);
            try {
                service.send(msg);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - MQTT ServiceConnection onServiceConnected()");
            } catch (RemoteException e) {
                // MQTT 토픽을 처리하지 못하였습니다.
                e.printStackTrace();
                ToastUtils.ToastShow(getActivity(), getString(R.string.msg_mqtt_fetch_error));
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT ServiceConnection Failed!!!");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
            mBound = false;
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT ServiceConnection onServiceDisconnected()");
        }
    };

    public mmWaveSleepMeasureFragment() {
        // Required empty public constructor
    }

    public static mmWaveSleepMeasureFragment newInstance() {
        return new mmWaveSleepMeasureFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // API
        queue = new LinkedList<Integer>();
        pref = new SharedPrefUtil(getActivity());
        device_mac = pref.getValue(SharedPrefUtil.DEVICE_MAC, "");
        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_mmwave_sleep_measure, container, false);

        mmwaveSleepMeasureDeviceMac = (TextView) v.findViewById(R.id.mmwaveSleepMeasureDeviceMac);
        mmwaveSleepMeasureDeviceMac.setText(device_mac);
        UserName = (TextView) v.findViewById(R.id.mmWaveSleepMeasureusername);
        UserName.setText(userNameValue);

        Button mmWaveSleepMeasureStopButton = (Button) v.findViewById(R.id.mmwaveSleepStopButton);
        mmWaveSleepMeasureStopButton.setOnClickListener(this);

        return v;
    }

    /**
     * MQTT 서비스에 연결하여, ServiceConnection를 전달한다.
     * @see Activity
     */
    @Override
    public void onStart() {
        super.onStart();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - onStart()");

        if (!mBound) {
            if (getActivity().bindService(new Intent(getContext(), MQTTservice.class), serviceConnection, 0)) {
                mBound = true;
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - MQTT Bind Successfullly 1: " + serviceConnection.toString());
            } else {
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT Bind Failed 2: " + serviceConnection.toString());
            }
        } else {
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT Binded Already but into here 3: " + serviceConnection.toString());
        }
    }

    /**
     * MQTT 서비스에 연결한 ServiceConnection를 unBind 처리한다.
     * @see Activity
     */
    @Override
    public void onStop() {
        super.onStop();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - onStop()");

        if (mBound) {
            getActivity().unbindService(serviceConnection);
            mBound = false;
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - MQTT Unbinded Successfullly 1: " + serviceConnection.toString());
        } else {
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - MQTT Unbinded Already but into here 3: " + serviceConnection.toString());
        }
    }

    @Override
    public void onClick(View v) {
        Button trackingButton = (Button) v;
        switch(trackingButton.getId()){
            case R.id.mmwaveSleepStopButton:
            {
                // publish 메시지 보내야함

                publishTopic(AppConst.Topic + device_mac, AppConst.mmWaveStop);

                ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                break;
            }
        }
    }

    /**
     * MQTT 서버에 연결하여 Topic을 발행한다.
     * @param 	topic		: 발행할 토픽
     * @param	message 	: 발행할 메시지
     * @return 	None
     */
    public void publishTopic(String topic, String message) {
        if (!mBound) return;
        if (!topic.isEmpty() && !message.isEmpty()) {
            data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, topic);
            data.putCharSequence(MQTTservice.MESSAGE, message);

            msg = Message.obtain(null, MQTTservice.PUBLISH);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try {

                service.send(msg);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Publish Attemping...");
            } catch (RemoteException e) {
                e.printStackTrace();
                ToastUtils.ToastShow(getActivity(), getString(R.string.msg_mqtt_fetch_error));
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Publish Failed with exception:" + e.getMessage());
            }
        } else {
            // MQTT 토픽을 처리하지 못하였습니다.
            ToastUtils.ToastShow(getActivity(), getString(R.string.msg_mqtt_fetch_error));
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MainActivity - PublishTopic() / MQTT Topic and Message Required.");
        }
    }

    /**
     * 바인드된 서비스에서 보내는 메시지를 처리한다.
     */
    @SuppressLint("HandlerLeak")
    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // MQTT 상태여부 가져오기
            data = msg.getData();
            status = data.getInt(MQTTservice.STATUS);

            // MQTT 상태에 따라 처리
            switch(status) {
                case AppConst.MQTT_Success:
                    queue.clear();
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler / MQTT - Message=" + msg.what + " / Status=" + status);

                case AppConst.MQTT_Failed_Connection:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT 연결 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Topic_Null:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT TOPIC 처리 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Bundle_Null:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT Bundle 처리 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Client_Null:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT 클라이언트 연결 실패: " + queue.size());
                    break;

                case AppConst.MQTT_Failed_Data:
                    queue.offer(status);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "GroupActivity - ServiceHandler() / MQTT DATA 처리 실패: " + queue.size());
                    break;

                default :
                    super.handleMessage(msg);
                    break;
            }

            // 응답 큐가 6개(30초)를 넘기는 경우
            if(queue.size() > AppConst.Queue_GPIO_BaseLine) {
                // 2. MQTT 상태 저장
                pref.put(SharedPrefUtil.MQTT_STATUS, false);

                // 3. 서버에 연결하지 못하였습니다. 네트워크를 확인하거나 잠시 후 다시 시도하십시오.
                ToastUtils.ToastShow(getActivity(), R.string.msg_mqtt_fetch_error);
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "Main/GroupActivity - ServiceHandler() / 네트워크 연결 실패!!");
            } else {
                // 1. MQTT 상태 저장
                pref.put(SharedPrefUtil.MQTT_STATUS, true);
            }
        }
    }
}

