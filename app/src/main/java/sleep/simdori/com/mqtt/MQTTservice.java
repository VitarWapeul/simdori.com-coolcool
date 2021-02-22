package sleep.simdori.com.mqtt;

import java.util.Vector;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.util.LogPrinter;

import sleep.simdori.com.AppConst;
//import sleep.simdori.com.DatabaseOpenHelper;
import sleep.simdori.com.util.DatabaseHandler;
import sleep.simdori.com.util.SSLUtil;
import sleep.simdori.com.util.SharedPrefUtil;


///**
// * MQTT를 통해 장치관리 및 푸시기능을 수행한다.
// * HandlerThread를 통해 별도의 Thread를 사용한다.
// * @version    	2.00 03/08/16
// * @author      이선호
// * @see         sleep.simdori.com.activity
// * @since       Android 5.1
// */
public class MQTTservice extends Service {
    // API
    private SharedPrefUtil pref = null;
    private DatabaseHandler db 	= null;
    private Intent intent = null;

    // MQTT
    private static MQTTConnection connection = null;
    private final Messenger clientMessenger = new Messenger(new ClientHandler());
    private Message msg_reply = null; //msg_stop, msg_connect = null;
    private Bundle data = null;
    private static boolean serviceRunning = false;

    // 노티피케이션
    private PendingIntent pendingIntent = null;
    private Builder notificationCompat = null;
    private Notification notification = null;
    private NotificationManager notificationManager = null;
    private static int mid = 0;

    // 바운드된 액티비티에서 보내는 메시지
    public static final int REGISTER = 0;
    public static final int SUBSCRIBE = 1;
    public static final int UNSUBSCRIBE = 2;
    public static final int PUBLISH = 3;
    private static final int STOP = PUBLISH + 1;
    private static final int CONNECT = PUBLISH + 2;
    private static final int RESETTIMER = PUBLISH + 3;
    public static final int MESSAGE_ARRIVED = PUBLISH + 4;

    // 메시지의 데이터에 저장된 값
    public static final String MQTT = "mqtt";
    public static final String TOPIC = "topic";
    public static final String MESSAGE = "message";
    public static final String NOTIFICATION = "notification";
    public static final String STATUS = "status";

    /**
     * MQTTConnection(HandlerThread)를 초기화한다.
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - onCreate()");

        // API
        db = new DatabaseHandler(this);
        pref = new SharedPrefUtil(this);

        // MQTT Connection 초기화
        connection = new MQTTConnection();
    }

    /**
     * START_STICKY : 재생성과 onStartCommand() 호출(with null intent)
     * MQTTConnection(HandlerThread)를 실행한다.
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning()) {
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - Running!!");
            return START_STICKY;
        } else {
            super.onStartCommand(intent, flags, startId);
            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - onStartCommand()");
        }

        // MQTT Connection(MQTT Thread) 실행
        connection.start();
        return START_STICKY;
    }

    /**
     * MQTTConnection(HandlerThread)를 종료한다.
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - onDestory()");

        connection.end();
    }

    /**
     * bind를 통해서 Service객체의 reference를 가져올 수 있다.
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - onBind()");
        // clientMessenger의 바인더를 액티비티에 전달한다.
        return clientMessenger.getBinder();
    }

    /**
     * unbind를 통해서 Service객체의 reference를 반환한다.
     * @see android.app.Service#onUnbind(android.content.Intent)
     */
    @Override
    public boolean onUnbind(Intent intent) {
        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - onUnBind()");
        return true;
    }

//    /**
//     * 현재 서비스의 상태를 반환한다.
//     * @param 	None
//     * @throws 	None
//     * @return 	boolean : 현재 서비스의 상태
//     */
    private synchronized static boolean isRunning() {
        if (serviceRunning == false) {
            serviceRunning = true;
            return false;
        } else {
            return true;
        }
    }

    /**
     * 바운드된 액티비티에서 보내온 값을 MQTT Connection으로 보내 처리한다.
     */
    @SuppressLint("HandlerLeak")
    class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUBSCRIBE:
                case UNSUBSCRIBE:
                case PUBLISH:
                    // 액티비티에서 보낸 값을 MQTT Connection으로 보내 처리한다.
                    connection.makeRequest(msg);
                    break;

                case REGISTER:
                    // 사용자가 다른 경우 MQTT 재연결
                    if(!pref.getValue(SharedPrefUtil.MQTT_Connection, false)) {
                        pref.put(SharedPrefUtil.MQTT_Connection, false);
                        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - ClientHandler() - REGISTER - 다른 아이디이므로 재연결!!");

                        // MQTTConnection 초기화
                        connection.reConnection();

                        // MQTTConnection 종료
						/*msg_stop = Message.obtain(null, MQTTservice.STOP);
						connection.makeRequest(msg_stop);*/
                        // MQTTConnection 연결
                        //msg_connect = Message.obtain(null, MQTTservice.CONNECT);
                        //connection.makeRequest(msg_connect);

                    } else {
                        // 연결할 필요가 없으므로 알림X
                        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - ClientHandler() / REGISTER - 동일한 아이디이므로 통과!!!");

                        // 동일한 사용자인 경우 MQTT Subscribe!!
                        connection.prepareHandler();
                    }
                    break;

                default:
                    super.handleMessage(msg);
                    ReplytoClient(msg.replyTo, msg.what, AppConst.MQTT_Failed_Data);	// 잘못된 메시지
                    if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MQTT Service - ClientHandler() / 잘못된 메시지:" + msg.toString());
                    break;
            }
        }
    }

//    /**
//     * 연결된 Hanler에게 메시지를 전달한다.
//     * @param 	responseMessenger 	: 연결되는 메신저
//     * @param	type 				: 메시지의 종류
//     * @param	status 				: 상태값
//     * @throws 	None
//     * @return 	None
//     */
    private void ReplytoClient(Messenger responseMessenger, int type, int status) {
        /*
         * A response can be sent back to a requester when the replyTo field is
         * set in a Message, passed to this method as the first parameter.
         */
        if (responseMessenger != null) {
            data = new Bundle();
            data.putInt(STATUS, status);
            msg_reply = Message.obtain(null, type);
            msg_reply.setData(data);

            try {
                responseMessenger.send(msg_reply);
            } catch (RemoteException e) {
                e.printStackTrace();
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MQTT Service - ReplytoClient is Retried!!!");

                // 다시 메시지 보내기
                ReplytoClient(responseMessenger, type, status);
                return;
            }
        } else {
            // 전달할 메신저가 없으므로 알림X
        }
    }

//    /**
//     * 장치 연결 상태를 저장한다.
//     * DISCONNECTED : 연결 종료
//     * CONNECTED	: 연결 중
//     */
//    public enum CONNECT_STATE {
//        DISCONNECTED, CONNECTED
//    }

//    /**
//     * 별도의 스레드를 통해 메시지를 처리한다.
//     * 참조 사이트: https://realm.io/kr/news/android-thread-looper-handler/
//     * 참조 사이트: https://blog.nikitaog.me/2014/10/11/android-looper-handler-handlerthread-i/
//     * @version     2.00 03/08/16
//     * @author      이선호
//     * @see         sleep.simdori.com.activity
//     * @since       Android 5.1
//     */
    public class MQTTConnection extends HandlerThread {
        public MsgHandler msgHandler = null;
//        public CONNECT_STATE connState = CONNECT_STATE.DISCONNECTED;

        /**
         * 핸들러 초기화 – 사용자 정보를 토대로 Client 생성
         */
        public MQTTConnection() {
            super("MQTTConnection", Process.THREAD_PRIORITY_BACKGROUND);
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MQTT Service - MQTTConnection()");
            //msgHandler = new MsgHandler();
        }

        /**
         * 메시지 전달을 위한 루프를 생성하고, MQTT 커넥션을 시작한다.
         * @see android.os.HandlerThread#onLooperPrepared()
         */
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MQTT Service - MQTTConnection() / Start!!!");
            msgHandler = new MsgHandler(getLooper());
            msgHandler.sendMessage(Message.obtain(null, CONNECT));
        }

        /**
         * 핸들러 초기화 – 사용자 정보를 토대로 Client 생성
         */
        public void prepareHandler(){
            //msgHandler = new MsgHandler(getLooper());
            try {
                msgHandler.SubscribeUser();
            } catch (Exception e) {
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG,"MQTT Service - MQTTConnection() / prepareHandler() 실패!!!");
                e.printStackTrace();
            }
        }

        /**
         * MQTT 커넥션을 종료한다.
         */
        public void reConnection() {
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MQTT Service - MQTTConnection() / End!!!");
            if(msgHandler!=null) msgHandler.sendMessage(Message.obtain(null, STOP));
            msgHandler = new MsgHandler(getLooper());
            msgHandler.sendMessage(Message.obtain(null, CONNECT));
        }

        /**
         * MQTT 커넥션을 종료한다.
         */
        public void end() {
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MQTT Service - MQTTConnection() / End!!!");
            if(msgHandler!=null) msgHandler.sendMessage(Message.obtain(null, STOP));
        }


//        /**
//         * msgHandler에게 메시지를 전달한다.
//         * @param 	msg	: 실행할 메시지
//         * @throws 	None
//         * @return 	None
//         */
        public void makeRequest(Message msg) {
            // 액티비티에서 전달받은 메시지를 msgHandler에 보내 처리한다.
            if(msgHandler!=null) msgHandler.sendMessage(Message.obtain(msg));
        }

        /**
         * Handler를 상속하여 MQTT 관련 메시지를 처리한다.
         * MqttCallback를 구현하여, MQTT 메시지를 받았을 경우를 처리한다.
         */
        private class MsgHandler extends Handler implements MqttCallback {
            private final int MINTIMEOUT = 2000, MAXTIMEOUT = 32000;
            private int timeout = MINTIMEOUT;
            private int status, msgType, sensorType, sensorData = 0;
            //private int publish_retry = 0;
            private Bundle data = null;
            private String topic, message, willmessage, userTopic, deviceTopic, sensorTopic, tranferType, valueType = null;
            private String[] topics, messages = null;
            private String userID, uniqueID, uri, clientId, notication_mac="simdori", notication_msg = null;
            private char[] userPW = null;
            private boolean mqtt_ssl_status = false;
            private MqttClient client = null;
            private MqttConnectOptions options = new MqttConnectOptions();
            private Vector<String> mqttTopics = new Vector<String>();

            /**
             * 핸들러 초기화 – 사용자 정보를 토대로 Client 생성
             * @param looper
             */
            @SuppressLint("HandlerLeak")
            MsgHandler(Looper looper) {
                super(looper);

                uri = "ssl://" + AppConst.simdori_MQTT_Host + ":" + AppConst.MQTT_port;
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MQTT Service - MsgHandler() / 암호화: " + mqtt_ssl_status + " , 주소: " + uri);

                // 사용자 정보 가져오기
//                userID = pref.getValue(SharedPrefUtil.USER_ID, "");
                userID = "simdori";
//                userPW = pref.getValue(SharedPrefUtil.USER_PASSWORD, "").toCharArray();
                userPW = "simdori1!".toCharArray();
                uniqueID = pref.getValue(SharedPrefUtil.UNIQUE_ID, "");
//                clientId = userID + "_" + uniqueID ;
                clientId = "simdori" ;
//                userTopic = AppConst.Topic + userID + "/";
                userTopic = "simdori/";
                willmessage = AppConst.MQTT_WilMessage + userID + "/";

                // MQTT 옵션 설정
                try {
                    options.setCleanSession(true);
                    options.setUserName(userID);
                    options.setPassword(userPW);
                    options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
                    options.setKeepAliveInterval(AppConst.MQTT_KeepAlive);//MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);
                    options.setWill(userTopic, willmessage.getBytes(), 2, false);
                    options.setSocketFactory(SSLUtil.getSocketFactory(AppConst.MQTT_CA_FilePath, AppConst.MQTT_ClientCrt_FilePath, AppConst.MQTT_ClientKey_FilePath, "password"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / Option init Failed!!");
                }

//                // MQTT SSL 암호화 여부에 따라, SSl 옵션 설정
//                if(mqtt_ssl_status)
//                    options.setSocketFactory(SSLUtil.getSocketFactory(AppConst.MQTT_CA_FilePath, AppConst.MQTT_ClientCrt_FilePath, AppConst.MQTT_ClientKey_FilePath, "password"));

                // MQTT Client 생성
                try {
                    if (client == null) {
                        client = new MqttClient(uri, clientId, null);
                        client.setCallback(this);
                        //client.setTimeToWait(AppConst.MQTT_TimeToWait);
                        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / Client ID: " + userID);
                    } else {
                        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / Client is not null : " + client.toString());
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / Client init Failed!!");
                }
            }

            /**
             * MQTT 관련 메시지를 처리한다.
             * @see android.os.Handler#handleMessage(android.os.Message)
             * <Message>
             * STOP 	: Client의 콜백를 제거하고 종료한다.
             * CONNECT 	: 설정된 옵션을 적용하여 Client를 연결한다.
             * SUBSCRIBE 	: 특정 토픽을 구독한다.
             * UNSUBSCRIBE 	: 특정 토픽을 구독해제한다.
             * PUBLISH		: 특정 토픽과 메시지을 발행한다.
             */
            @Override
            public void handleMessage(Message msg) {
                System.out.println("메시지: " + msg);
                status = AppConst.MQTT_Success;

                switch (msg.what) {
                    case STOP:
                        client.setCallback(null);
                        if (client.isConnected()) {
                            try {
                                client.disconnect();
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - Client DisConnecting...");
                            } catch (MqttException e) {
                                status = AppConst.MQTT_Failed_Connection;
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - Client DisConnection attemp failed with reason code = " + e.getReasonCode() + e.getCause());
                            }
                        } else {
                            status = AppConst.MQTT_Already_Connection;
                            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - Already Client Disconnected: " + client.getClientId());
                            break;
                        }

                        this.dump(new LogPrinter(Log.DEBUG, AppConst.TAG), "");
                        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - Client DisConnection : " + client.getClientId());
                        break;

                    case CONNECT:
                        if (!client.isConnected()) {
                            try {
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service MsgHandler() / handleMessage() - Client Connecting...");
                                client.connectWithResult(options).waitForCompletion(); //AppConst.MQTT_TimeToWait);
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - MQTT Connection ClientID: " + client.getClientId());

                                // 관련 토픽 구독
                                SubscribeUser();

                            } catch (MqttException e) {
                                status = AppConst.MQTT_Failed_Connection;
                                e.printStackTrace();
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - Client Connection attemp failed with reason code = " + e.getReasonCode() + ", Client_ID = " + client.getClientId());
                                if (timeout < MAXTIMEOUT) {
                                    timeout *= 2;
                                } else {
                                    // 단순 정보이므로 알림X
                                }
                                this.sendMessageDelayed(Message.obtain(null, CONNECT), timeout);
                                return;
                            }
                        } else {
                            // 관련 토픽 구독
                            SubscribeUser();
//                            SubscribeDevice(db.getDeviceMac());
                            status = AppConst.MQTT_Already_Connection;
                            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - Already Client Connected: " + client.getClientId());
                        }
                        break;

                    case RESETTIMER:
                        timeout = MINTIMEOUT;
                        break;

                    case SUBSCRIBE:
                        if (client!=null && client.isConnected()) {
                            data = msg.getData();
                            if (data != null) {
                                topic = data.getString(TOPIC);
                                if (topic!=null && !topic.isEmpty()) {
                                    if(subscribe(topic)) {
                                        mqttTopics.add(topic);	// 토픽 추가
                                    } else {
                                        status = AppConst.MQTT_Failed_Connection;
                                    }
                                } else {
                                    status = AppConst.MQTT_Failed_Topic_Null;
                                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - SUBSCRIBE Topic is Null");
                                }
                            } else {
                                status = AppConst.MQTT_Failed_Bundle_Null;
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - SUBSCRIBE Bundle is Null");
                            }
                        } else {
                            status = AppConst.MQTT_Failed_Client_Null;
                            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - SUBSCRIBE Client Disconnected: " + client.getClientId());
                        }
                        break;

                    case UNSUBSCRIBE:
                        if (client!=null && client.isConnected()) {
                            data = msg.getData();
                            if (data != null) {
                                topic = data.getString(TOPIC);
                                if (topic!=null && !topic.isEmpty()) {
                                    if(unsubscribe(topic)) {
                                        mqttTopics.remove(topic);	// 토픽 제거
                                    } else {
                                        status = AppConst.MQTT_Failed_Connection;
                                    }
                                } else {
                                    status = AppConst.MQTT_Failed_Topic_Null;
                                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - UNSUBSCRIBE Topic & Message is Null");
                                }
                            } else {
                                status = AppConst.MQTT_Failed_Bundle_Null;
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - UNSUBSCRIBE Bundle is Null");
                            }
                        } else {
                            status = AppConst.MQTT_Failed_Client_Null;
                            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - UNSUBSCRIBE Client Disconnected: " + client.getClientId());
                            // 이미 연결되어 있으므로 알림X
                        }
                        break;

                    case PUBLISH:
                        if (client!=null && client.isConnected()) {
                            data = msg.getData();
                            if (data!=null) {
                                topic = data.getString(TOPIC);
                                message = data.getString(MESSAGE);
                                if (topic!=null && !topic.isEmpty() && message!=null && !message.isEmpty()) {
                                    if(publish(topic, message)) {
                                        status = AppConst.MQTT_Success;
                                        pref.put(SharedPrefUtil.MQTT_Publish_Status, true);
                                        //publish_retry = AppConst.QUEUE_ZERO;
                                    } else {
                                        status = AppConst.MQTT_Failed_Connection;
                                        pref.put(SharedPrefUtil.MQTT_Publish_Status, false);

										/*if (publish_retry < AppConst.MQTT_Publish_Baseline) {
											publish_retry++;
											if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - PUBLISH RETRY: " + publish_retry);

											Message message = Message.obtain(null, PUBLISH);
											message.setData(data);
											this.sendMessageDelayed(message, MINTIMEOUT);
											return;
										} else {
											publish_retry = AppConst.QUEUE_ZERO;
										}*/
                                    }
                                } else {
                                    status = AppConst.MQTT_Failed_Topic_Null;
                                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - PUBLISH Topic&Message is Null");
                                }
                            } else {
                                status = AppConst.MQTT_Failed_Bundle_Null;
                                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - PUBLISH Bundle is Null");
                            }
                        } else {
                            status = AppConst.MQTT_Failed_Client_Null;
                            if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - PUBLISH Client Disconnected: " + client.getDebug().toString());
                        }
                        break;

                    default:
                        status = AppConst.MQTT_Failed_Data;
                        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / handleMessage() - Incorrect Data " + msg.what);
                }
                ReplytoClient(msg.replyTo, msg.what, status);
            }

//            /**
//             * 로그인되어 있는 사용자의 Topic을 구독한다.
//             * @param 	None
//             * @throws 	None
//             * @return 	None
//             */
            public void SubscribeUser() {
                userTopic = AppConst.Topic + userID + "/";
                pref.put(SharedPrefUtil.USER_TOPIC, userTopic);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "MQTT Service - MsgHandler() / SubscribeUser() - 현재구독 중인 사용자 Topic은 " + userTopic);
                subscribe(userTopic);
            }

//            /**
//             * MQTT 서버에 구독을 신청한다.
//             * @param 	topic	: 구독할 토픽
//             * @throws 	None
//             * @return 	boolean	: 구독 성공여부
//             */
            private boolean subscribe(String topic) {
                try {
                    client.subscribe("sindori", 2);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / Subscribe Attemping... ");
                    return true;
                } catch (MqttException e) {
                    // 이 함수를 호출하는 곳에 false를 리텀함으로 알림 처리
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / Subscribe Failed with reason code = " + e.getReasonCode());
                    e.printStackTrace();
                    return false;
                }
            }

//            /**
//             * MQTT 서버에 구독 취소를 신청한다.
//             * @param 	topic	: 구독할 토픽
//             * @throws 	None
//             * @return 	boolean	: 구독 성공여부
//             */
            private boolean unsubscribe(String topic) {
                try {
                    client.unsubscribe(topic);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / UnSubscribe Attemping... ");
                    return true;
                } catch (MqttException e) {
                    // 이 함수를 호출하는 곳에 false를 리텀함으로 알림 처리
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / UnSubscribe Failed with reason code = " + e.getReasonCode());
                    e.printStackTrace();
                    return false;
                }
            }

//            /**
//             * MQTT 서버에 특정 메시시를 발행한다.
//             * @param 	topic	: 발행할 토픽
//             * @param	msg		: 발행할 메시지
//             * @throws 	None
//             * @return 	boolean	: 발행 성공여부
//             */
            public boolean publish(String topic, String message) {
				/*if(message.contains(AppConst.Msg_GPIO)){
					if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MQTT TIME - Topic Publish Step 2 : " + message);
				}*/

                try {
                    client.publish(topic, message.getBytes(), 2, true);
                    return true;
                } catch (MqttException e) {
                    // 이 함수를 호출하는 곳에 false를 리텀함으로 알림 처리
                    e.printStackTrace();
                    return false;
                }
            }


            @Override
            public void connectionLost(Throwable cause) {

            }

            /**
             * MQTT 서버에서 받은 토픽과 메시지를 처리할 부분을 구현한다.
             * "simdori/" 가 포함된 메시지만 저장한다.
             * 사용자 토픽 처리: 로그아웃(user_id/1/unique_id) - 브로드캐스트 리시버 전달
             * 사용자 토픽 처리: 장치공유(sharing_id/2~4/macaddress) - 노티피케이션 알림
             * 장치 토픽 처리:  RESPONSE 경우만 브로드캐스트 리시버 전달
             * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
             */
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                status = AppConst.MQTT_Success;
                if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / messageArrived() - 모든 토픽과 메시지 저장: " + topic + " , " + message.toString());
				/*if(message.toString().contains(AppConst.Msg_GPIO2)){
					if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MQTT TIME - Topic Subcribe Step 3: " + message.toString());
				}*/

                // 1. 메시지 처리
                if (topic.contains(AppConst.Topic) && message.toString().contains("/")) {
                    try {
                        topics = topic.split("/");
                        messages = message.toString().split("/");
                        tranferType = messages[0];
                        msgType = Integer.valueOf(messages[1]);
                        valueType = messages[2];
//                        if(!userTopic.equals(topic) && msgType!=AppConst.MQTT_Device_Alert)
//                            if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "MQTT Service - MsgHandler() / messageArrived() - 토픽과 메시지 저장: " + topic + " , " + message.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = AppConst.MQTT_Failed_Topic_Null;
                        if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "MQTT Service - MsgHandler() / messageArrived() - 토픽 처리 오류");
                    }
                } else {
                    // 설정된 토픽만 걸러냈기 때문에 알림X
                }

                // 2. 사용자 토픽 처리
                if (userTopic.equals(topic) && message!=null && !tranferType.contains(AppConst.Msg_Request)) {
                    Log.e(AppConst.TAG, "UserTopic: " + userTopic + " / Topic: " + topic);

                    // 2-1. 로그아웃 메시지 처리 - 브로드캐스트 리시버
                    if (uniqueID.equals(valueType) && !tranferType.contains(AppConst.Msg_Response)) {
                        Intent_MQTT(topic, message.toString());

                        // 2-2. 장치공유 메시지 처리 - 팝업 및 노티피케이션
                    }
                    else {
                        Intent_MQTT(topic, message.toString());
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }

            private void Intent_MQTT(String topic_broad, String message_broad) {
                intent = new Intent();
                intent.setAction("sleep.simdori.com.MQTT.PushReceived");
                intent.putExtra(TOPIC, topic_broad);
                intent.putExtra(MESSAGE, message_broad);
                sendBroadcast(intent);
            }
        }
    }
}
