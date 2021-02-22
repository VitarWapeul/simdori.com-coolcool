package sleep.simdori.com.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * 모든 클래스에서 동일하게 사용되는 값을 저장한다. 아래의 KEY 값에 OBJEBT를 저정하여 사용할 수 있다.
 *
 * @version 2.00 03/08/16
 * @author 이선호
 * @see sleep.simdori.com.activity
 * @since Android 5.1
 */
public class SharedPrefUtil {
    private static Context mContext = null;

    // simdori
    public static final String PREF_NAME 	= "sleep.simdori.com"; 		// 앱 이름
    public static final String LANGUAGE 	= "LANGUAGE"; 					// 현재 언어
    public static final String FIRST 		= "FIRST"; 						// 튜토리얼 활성화 여부
    public static final String AES_STATUS 	= "AES_STATUS"; 				// 암호화 활성화 여부 (일반: 0/ 암호화: 1)
    public static final String MQTT_SSL 	= "MQTT_SSL"; 					// MQTT SSL 암호화 여부 (일반: 0/ 암호화: 1)
    public static final String RESPONSE_PROCESSING = "RESPONSE_PROCESSING"; // 응답 처리 여부
    public static final String SENSOR_STATUS = "SENSOR_STATUS"; 			// 센서 유무
    public static final String VOICE_ALERT_STATUS 	= "VOICE_ALERT_STATUS"; 	// 음성 알람 여부
    public static final String GAS_ALERT_STATUS 	= "GAS_ALERT_STATUS"; 		// 가스 알람 여부
    public static final String FIRE_ALERT_STATUS 	= "FIRE_ALERT_STATUS"; 		// 화재 알람 여부
    public static final String AZURE_SERVER 	= "AZURE_SERVER"; 		// AZURE Server 사용 여부
    public static final String FWUPDATE_STATUS 	= "FWUPDATE_STATUS"; 	// 펌웨어 업데이트 여부
    //20170321 DY 버전체크 처리
    public static final String VERSION_CHECK_STATUS = "VERSION_CHECK_STATUS"; 	// 버전체크용

    public static final String DATE = "DATE"; 	// 현재 날짜
    public static final String SLEEP = "SLEEP"; 	// sleep 데이터 전송용
    public static final String SLEEPDAYS = "SLEEPDAYS"; 	// sleep 데이터 전송용

    // MQTT
    public static final String MQTT 			= "MQTT"; 				// MQTT
    public static final String MQTT_URL 		= "MQTT_URL"; 			// MQTT_URL
    public static final String MQTT_TOPIC 		= "MQTT_TOPIC"; 		// MQTT_TOPIC
    public static final String MQTT_MESSAGE		= "MQTT_MESSAGE"; 		// MQTT_MESSAGE
    public static final String USER_TOPIC 		= "USER_TOPIC"; 		// 사용자 Topic
    public static final String SUB_TOPIC 		= "SUB_TOPIC"; 			// 구독 Topic
    public static final String PUB_TOPIC 		= "PUB_TOPIC"; 			// 발행 Topic
    public static final String GPIO0_Status 	= "GPIO0_STATUS"; 		// GPIO0 ON/OFF
    public static final String MQTT_Connection 	= "MQTT_CONNECTION";		// MQTT 연결 여부
    public static final String MQTT_Publish_Status = "MQTT_Publish_Status";	// MQTT 발행 여부
    public static final String MQTT_STATUS		= "MQTT_STATUS"; 			// MQTT 상태 여부
    public static final String NETWORK_CONNECTION 	= "NETWORK_CONNECTION"; // NETWORK 연결 여부

    // Intent
    public static final String TEMP_BBS_INTENT 	= "TMEP_BBS_INTENT"; 	// 호출한 인텐트
    public static final String TEMP_BBS_TITLE 	= "TMEP_BBS_TITLE"; 	// 타이틀
    public static final String TEMP_BBS			= "TMEP_BBS"; 			// 주소
    public static final String INTENT_STATUS 	= "INTENT_STATUS"; 		// 직접 인텐트 (0: 기본/ 1: 사용자/ 2: 설정)
    public static final String INTENT_TITLE 	= "INTENT_TITLE"; 		// 직접 인텐트 (0: 기본/ 1: 사용자/ 2: 설정)
    public static final String INTENT_DATE 		= "INTENT_DATE"; 		// 직접 인텐트 (0: 기본/ 1: 사용자/ 2: 설정)
    public static final String INTENT_CONTENTS 	= "INTENT_CONTENTS"; 		// 직접 인텐트 (0: 기본/ 1: 사용자/ 2: 설정)

    // 재시도
    public static final String RETRY_REQUEST 		= "RETRY_REQUEST"; 		// 재시도 횟수 (3회 이상 시 알림)
    public static final String RANGE_CHECK_ISSUE 	= "RANGE_CHECK_ISSUE"; 	// 오류체크 문제
    public static final String WIFI_SETTING_GPIO 	= "WIFI_SETTING_GPIO"; 	// Wi-Fi 설정 GPIO 제어

    // 로그인 상태
    public static final String LOGIN_STATUS 		= "LOGIN_STATUS"; 		// 로그인 여부 (로그아웃: 0/ 로그인: 1/ 자동로그인: 2/ 페이스북 로그인:3)
    public static final String LOGOUT_POPUP 		= "LOGOUT_POPUP"; 		// 로그아웃 팝업
    public static final String LOGOUT_MQTT 			= "LOGOUT_MQTT"; 		// 동시로그인 로그아웃 팝업
    public static final String CONNECTION_POPUP 	= "CONNECTION_POPUP"; 	// 인터넷 오류 팝업
    public static final String LOGIN_PW_SAVE 		= "LOGIN_PW_SAVE";		// 비밀번호 저장여부 (일반: 0/ 비밀번호저장: 1)
    public static final String AUTO_LOGIN_STATUS 	= "AUTO_LOGIN_STATUS"; 	// 자동로그인 여부 (일반: 0/ 자동로그인: 1)
    public static final String RELOADING_LIST 		= "RELOADING_LIST"; 	// 장치 목록 불러오기
    public static final String RELOADING_STATUS		= "RELOADING_STATUS"; 	// 장치 목록 불러오기
    public static final String MODIFY_LINK			= "MODIFY_LINK";			// SNS 수정페이지
    public static final String FACEBOOK_LOGIN		= "FACEBOOK_LOGIN";			// SNS 수정페이지

    // 사용자 정보
    public static final String USER_ID 			= "USER_ID"; 		// 계정 아이디
    public static final String SESSION_ID 		= "SESSION_ID"; 	// 세션 아이디
    public static final String USER_EMAIL 		= "USER_EMAIL"; 	// 계정 이메일
    public static final String USER_PHONE 		= "USER_PHONE"; 	// 계정 이메일
    public static final String USER_PASSWORD 	= "USER_PASSWORD"; 	// 계정 비밀번호
    public static final String USER_PW_CHECK 	= "USER_PW_CHECK"; 	// 계정 비밀번호 재입력
    public static final String UNIQUE_ID 		= "UNIQUE_ID"; 		// APP 고유 아이디
    public static final String LOGOUT_ID 		= "LOGOUT_ID"; 		// 자동 로그아웃 여부 (미설정: 0/ 설정: 1)

    // 장치 정보
    public static final String DEVICE_MAC 	= "DEVICE_MAC"; 		// 장치 BSSID (MAC_Address)
    public static final String DEVICE_BSSID 	= "DEVICE_BSSID"; 		// 장치 BSSID (MAC_Address)
    public static final String DEVICE_SSID 		= "DEVICE_SSID"; 		// 장치 SSID
    public static final String DEVICE_VERSION 	= "DEVICE_VERSION"; 	// 장치 버전
    public static final String DEVICE_BUILDDATE	= "DEVICE_BUILDDATE"; 	// 장치 빌드날짜
    public static final String DEVICE_LOCATION 	= "DEVICE_LOCATION"; 	// 장치 위치
    public static final String DEVICE_LATITUDE	= "DEVICE_LATITUDE"; 	// 장치 위도
    public static final String DEVICE_LONTITUDE	= "DEVICE_LONTITUDE"; 	// 장치 경도
    public static final String DEVICE_IMAGE 	= "DEVICE_IMAGE"; 		// 장치 아이콘
    public static final String DEVICE_STATUS 	= "DEVICE_STATUS"; 		// 장치 상태
    public static final String DEVICE_REGISTER 	= "DEVICE_REGISTER"; 	// 장치 등록 가능상태
    public static final String DEVICE_CONNECTION = "DEVICE_CONNECTION"; // 장치 연결 경로
    public static final String WIFI_CONNECTION 	= "WIFI_CONNECTION"; 	// WIFI 연결 경로

    // GPIO
    public static final String GPIO_RESPONSE 		= "GPIO_RESPONSE"; 						// 소켓 제어 응답
    public static final String GPIO_RESPONSE_CLOSE 	= "GPIO_RESPONSE_CLOSE"; 				// 소켓 제어 응답완료
    public static final String DEVICE_SOCKET 		= "DEVICE_SOCKET"; 						// 장치 일괄 제어
    public static final String DEVICE_GROUPGPIO_RESPONSE 	= "DEVICE_GROUPGPIO_RESPONSE"; 		// 장치 일괄 제어 응답
    public static final String DEVICE_GROUPGPIO_CLOSE 		= "DEVICE_GROUPGPIO_CLOSE"; 		// 장치 일괄 제어 응답완료
    public static final String DEVICE_SOCKET2 		= "DEVICE_SOCKET2"; 						// 그룹 일괄 제어
    public static final String DEVICE_SOCKETGPIO_RESPONSE 	= "DEVICE_SOCKETGPIO_RESPONSE"; 	// 그룹 일괄 제어 응답
    public static final String DEVICE_SOCKETGPIO_CLOSE 		= "DEVICE_SOCKETGPIO_CLOSE"; 		// 장치 일괄 제어 응답완료
    public static final String GROUP_SOCKET 		= "GROUP_SOCKET"; 							// 일괄 제어

    // 장치 공유
    public static final String SHARING_ID 		= "SHARING_ID"; 		// 공유한 ID
    public static final String SHARED_ID 		= "SHARED_ID"; 			// 공유받은 ID
    public static final String SHARING_MAC 		= "SHARING_MAC"; 		// 공유받은 MAC
    public static final String SHARING_POPUP 	= "SHARING_POPUP"; 		// 공유 팝업
    public static final String SHARING_REQUEST 	= "SHARING_REQUESTR"; 	// 공유요청 알림
    public static final String SHARING_ACCEPTED = "SHARING_ACCEPTED"; 	// 공유요청 수락 알림
    public static final String SHARING_REJECTED = "SHARING_REJECTED"; 	// 공유요청 거절 알림
    public static final String SHARING_RELEASE 	= "SHARING_RELEASE"; 	// 공유해제 알림

    // 장치 설정
    public static final String MASTERPLUG_SETTING 	= "MASTERPLUG_SETTING"; 	// 마스터 플러그 설정 여부
    public static final String ALERT_VOICE_STATUS 	= "ALERT_VOICE_STATUS"; 	// 음성 알림 설정 여부
    public static final String ALERT_GAS_STATUS 	= "ALERT_GAS_STATUS"; 		// 가스 알림 설정 여부
    public static final String ALERT_FIRE_STATUS 	= "ALERT_FIRE_STATUS"; 		// 화재 알림 설정 여부

    // LuCI
    public static final String LuCI_WiFi_Setting 	= "LuCI_WiFi_Setting"; 	// Wi-Fi 설정
    public static final String LuCI_WiFi_Popup 		= "LuCI_WiFi_Popup"; 	// Wi-Fi 설정 팝업
    public static final String LuCI_WiFi_Password 	= "LuCI_WiFi_Password"; // Wi-Fi 비밀번호
    public static final String LuCI_WiFi_SSID 		= "LuCI_WiFi_SSID"; 	// Wi-Fi SSID
    public static final String LuCI_WiFi_BSSID 		= "LuCI_WiFi_BSSID"; 	// Wi-Fi MAC
    public static final String LuCI_WiFi_URI 		= "LuCI_WiFi_URI"; 		// Wi-Fi 주소
    public static final String LuCI_WiFi_CHANGED 	= "LuCI_WiFi_CHANGED"; 	// Wi-Fi 설정 변경

    public static final String LuCI_Session 		= "LuCI_Session"; 			// Luci 세션
    public static final String LuCI_GPIO 			= "LuCI_GPIO"; 				// Luci GPIO 상태
    public static final String LuCI_AMDIN 			= "LuCI_AMDIN"; 			// Luci 관리자 암호
    public static final String LuCI_SSID 			= "LuCI_SSID"; 				// Luci SSID
    public static final String LuCI_MODE 			= "LuCI_MODE"; 				// Luci 모드
    public static final String LuCI_Encrytion 		= "LuCI_Encryption"; 		// Luci 암호화 종류
    public static final String LuCI_KEY 			= "LuCI_KEY"; 				// Luci 암호화 키
    public static final String LuCI_Title 			= "LuCI_Title"; 			// Luci 리피터 모드 타이틀
    public static final String LuCI_ifname 			= "LuCI_ifname"; 			// Luci 리피터 모드
    public static final String LuCI_ApCliSsid 		= "LuCI_ApCliSsid"; 		// Luci 리피터 네트워크 이름
    public static final String LuCI_ApCliAuthMode 	= "LuCI_ApCliAuthMode"; 	// Luci 리피터 암호화 방식
    public static final String LuCI_ApCliEncrypType = "LuCI_ApCliEncrypType"; 	// Luci 리피터 암호화 키
    public static final String LuCI_ApCliPassWord 	= "LuCI_ApCliPassWord"; 	// Luci 리피터 네트워크 보안키
    public static final String LuCI_ApCliChannel 	= "LuCI_ApCliChannel"; 		// Luci 리피터 네트워크 채널
    public static final String LuCI_IPAddr 			= "LuCI_IPAddr"; 			// Luci 리피터 고정 IP주소
    public static final String LuCI_SubnetMask 		= "LuCI_SubnetMask"; 		// Luci 리피터 서브넷 마스크
    public static final String LuCI_GateWay 		= "LuCI_GateWay"; 			// Luci 리피터 게이트웨이
    public static final String LuCI_PrimaryDNS 		= "LuCI_PrimaryDNS"; 		// Luci 리피터 기본 DNS
    public static final String LuCI_SecondartDNS 	= "LuCI_SecondartDNS"; 		// Luci 리피터 보조 DNS

    public static final String Refresh_Wakeup = "Refresh_Wakeup";
    public static final String FW_Update = "FW_Update";

    public SharedPrefUtil(Context c) {
        mContext = c;
    }

    public void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, long value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(key, value);
        editor.commit();
    }

    public void put(String key, Set<String> value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putStringSet(key, value);
        editor.commit();
    }

    public Set<String> getValue(String key, Set<String> dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getStringSet(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public int getValue(String key, int dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public long getValue(String key, long dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getLong(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}
