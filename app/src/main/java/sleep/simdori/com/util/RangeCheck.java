package sleep.simdori.com.util;

import android.content.Context;
import android.util.Log;

import sleep.simdori.com.AppConst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 서버에서 받은 값의 오류를 체크한다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class RangeCheck {
	
	/**
	 * 이메일 형식인지 체크한다.
	 * @param email: 사용자 이메일
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Email(Context context, String email) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		Pattern p = Pattern.compile("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
		Matcher m = p.matcher(email);
		if (m.find()) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, email + "는 올바른 이메일입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Email");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, email + "는 올바른 이메일이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 전화번호 형식인지 체크한다.
	 * @param email: 사용자 이메일
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Phone(Context context, String phone) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		Pattern p = Pattern.compile("^([0-9]{2,3})-?([0-9]{3,4})-?([0-9]{4})$"); //("^\d{3}-\d{3,4}-\d{4}$");
		Matcher m = p.matcher(phone);
		if (m.find()) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, phone + "는 올바른 전화번호입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "phone");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, phone + "는 올바른 전화번호가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 공유 요청을 체크한다. 
	 * @param email: 공유 요청 (2: 장치공유/ 3: 공유 수락/ 4: 공유 거절/ 5: 공유 해제 / 6: 장치공유 요청/ 7: 장치공유 요청수락 / 8: 장치공유 요청거절)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Sharing_Popup (Context context, int sharing_popup) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (1<sharing_popup && sharing_popup<9) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, sharing_popup + "는 올바른 팝업입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "SharingPopup");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, sharing_popup + "는 올바른 팝업이 아닙니다!");
			return false; 
		}	
	}
	 
	/**
	 * 센서 타입이 40자리 이내인지 체크한다. 
	 * @param sensor_type: 센서 타입 
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Sensor_Type (Context context, int sensor_type) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<sensor_type && sensor_type<40) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, sensor_type + "는 올바른 센서 값입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "SharingPopup");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, sensor_type + "는 올바른 센서 값이 아닙니다!");
			return false; 
		}
	}
	
	/**
	 * 장치 이름이 50자리 이내인지 체크한다. 
	 * @param 임대리스트 내 장치 이름 (1~50) 
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean HostName(Context context, String hostName) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<hostName.length() && hostName.length()<51) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, hostName + "는 올바른 공유기 이름입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "DeviceName");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, hostName + "는 올바른 공유기 이름이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 연결시간이 10자리 이내인지 체크한다. 
	 * @param deviceName: 장치 연결시간 (1~10) 
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Expires(Context context, String expires) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<expires.length() && expires.length()<11) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, expires + "는 올바른 연결시간입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "DeviceName");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, expires + "는 올바른 연결시간이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * IP 주소를 체크한다. 
	 * @param deviceMac: 장치 IP ("000.000.000.000")
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean IPAddr(Context context, String ipaddr) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		Pattern p = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
		Matcher m = p.matcher(ipaddr);
		if (m.find()) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, ipaddr + "는 올바른 IP주소입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "IPAddr");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, ipaddr + "는 올바른 IP주소가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 등록된 맥주소인지 체크한다. 
	 * @param deviceMac: 장치 맥주소 (16자리 맥형식)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Register_Mac(Context context, String deviceMac) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		String[] deviceMacs = deviceMac.split(":");
		String Mac00 = "64";
		String Mac01 = "50";
		String Mac011 = "40";
		String Mac02 = "f4";
		String Mac03 = "00";
		String Mac04 = "0c";
		String Mac05 = "e7";
		
		if ((deviceMacs[0].equals(Mac00) || deviceMacs[0].equals(Mac03)) 
				&& (deviceMacs[1].equals(Mac01) || deviceMacs[1].equals(Mac04) || deviceMacs[1].equals(Mac011)) 
				&& (deviceMacs[2].equals(Mac02) || deviceMacs[2].equals(Mac05))) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "RangeCheck() - " + deviceMac + "는 등록된 맥주소입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "DeviceMac");
//			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAGmac, "RangeCheck() - " + deviceMac + "는 등록된 맥주소가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 16자리 맥주소인지 체크한다. 
	 * @param deviceMac: 장치 맥주소 (16자리 맥형식)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean DeviceMac(Context context, String deviceMac) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		Pattern p = Pattern.compile("^([0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}$");
		Matcher m = p.matcher(deviceMac);
		if (m.find()) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, deviceMac + "는 올바른 맥주소입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "DeviceMac");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, deviceMac + "는 올바른 맥주소가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 장치 이름이 10자리 이내인지 체크한다. 
	 * @param deviceName: 장치 이름 
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean DeviceName(Context context, String deviceName) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (-1<deviceName.length() && deviceName.length()<11) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, deviceName + "는 올바른 장치이름입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "DeviceName");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, deviceName + "는 올바른 장치이름이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 장치상태가 0~1 이내인지 체크한다.
	 * @param device_status: 장치 상태 (0:OFF/ 1:ON)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean DeviceStatus(Context context, int device_status) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (device_status>=0 && device_status<2) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, device_status + "는 장치상태입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "DeviceStatus");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, device_status + "는 올바른 장치상태이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 장치 공유상태가  0~2 이내인지  체크한다. 
	 * @param status: 장치 공유상태 (0: 공유안됨 / 1: 공유요청 / 2: 공유수락 / 3: 공유받음)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean SharingStatus(Context context, int status) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (status>=0 && status<4) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, status + "는 올바른 공유상태입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "SharingStatus");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, status + "는 올바른 공유상태가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 소켓번호가 1~4 이내인지 체크한다. 
	 * @param socket: 소켓번호 (1~4)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Socket(Context context, int socket) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<socket && socket<5) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, socket + "는 올바른 소컷번호입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Socket");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, socket + "는 올바른 소켓번호가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 소켓이름이 10자리 이내인지 체크한다. 
	 * @param socketName: 소켓이름
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean SocketName(Context context, String socketName) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<socketName.length() && socketName.length()<11) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, socketName + "는 올바른 소켓 이름입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "SocketName");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, socketName + "는 올바른 소켓이름이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 소켓이미지가 0~30 이내인지 체크한다. 
	 * @param socketImg: 소켓 이미지
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean SocketImg(Context context, int socketImg) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (-1<socketImg && socketImg<3000) {	
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, socketImg + "는 올바른 소켓 이미지입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "SocketImg");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, socketImg + "는 올바른 소켓 이미지가 아닙니다!");
			return false; 
		}	
	}

	
	/**
	 * 소켓상태가 0~15 이내인지 체크한다. 
	 * @param socket_status: 소켓상태 (0~15)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean SocketStatus(Context context, int socket_status) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (socket_status>=0 && socket_status<16) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, socket_status + "는 올바른 소켓상태입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "SocketStatus");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, socket_status + "는 올바른 소켓상태가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * GPIO상태가 0~1 이내인지 체크한다. 
	 * @param gpio_status: 소켓상태 (0:OFF/ 1:ON)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean GPIOStatus(Context context, int gpio_status) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (gpio_status>=0 && gpio_status<2) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, gpio_status + "는 GPIO상태입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "GPIOStatus");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, gpio_status + "는 올바른 GPIO상태가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 권한이 0~1 이내인지 체크한다. 
	 * @param gpio_status: 소켓상태 (0:OFF/ 1:ON)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Auth(Context context, int auth) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (auth>=0 && auth<2) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, auth + "는 올바른 권한 값입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Auth");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, auth + "는 올바른 권한 값이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 플러그 0~4 이내인지 체크한다. 
	 * @param plug: 플러그
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Plug(Context context, int plug) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (plug>=0 && plug<5) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, plug + "는 올바른 플러그입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Plug");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, plug + "는 올바른 플러그가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 기준치가 숫자인지 체크한다. 
	 * @param threshold: 기준치 
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Threshold(Context context, String threshold) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		//실수값 체크, shlee/2018-02-04
		Pattern p = Pattern.compile("[-+]?\\d*\\.?\\d+");
//		Pattern p = Pattern.compile("^([0-9]$");
		Matcher m = p.matcher(threshold);
		if (m.find()) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, threshold + "는 올바른 기준치입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Threshold");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, threshold + "는 올바른 기준치가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 전력 사용량이 숫자인지 체크한다. 
	 * @param threshold: 기준치
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean StanByPower(Context context, float stanbypower) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		Pattern p = Pattern.compile("[-+]?\\d*\\.?\\d+");
		Matcher m = p.matcher(stanbypower+"");
		if (m.find()) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, stanbypower + "는 올바른 전력값입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Threshold");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, stanbypower + "는 올바른 전력값이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 관리자 암호 설정여부가 0~1 이내인지 체크한다. 
	 * @param gpio_status: 소켓상태 (0:OFF/ 1:ON)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Admin(Context context, int admin) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (admin>=0 && admin<2) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, admin + "는 올바른 관리자 암호입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Admin");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, admin + "는 올바른 관리자 암호가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * 시간 형식을 체크한다. 
	 * @param time: 시간 (0000-00-00 00:00)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean Time(Context context, String time) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		Pattern p = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$");
		Matcher m = p.matcher(time);
		if (m.find()) {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "Time");
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, time + "는 올바른 시간입니다.");
			return true;
		} else {
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, time + "는 올바른 시간이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * SSID가 0~30 이내인지 체크한다. 
	 * @param ssid: 네트워크 이름 (1~30)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean SSID(Context context, String ssid) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<ssid.length() && ssid.length()<31) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, ssid + "는 올바른 SSID입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "SSID");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, ssid + "는 올바른 SSID가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * KEY가 0~30 이내인지 체크한다. 
	 * @param KEY: 네트워크 보안키 (1~30)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean KEY(Context context, String key) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<key.length() && key.length()<31) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, key + "는 올바른 KEY입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "KEY");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, key + "는 올바른 KEY가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * Mode가 형식에 맞는지 체크한다. 
	 * @param mode: 네트워크 운용모드 (ap/ sta/ adhoc)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean APMODE(Context context, String mode) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if(mode.equals("ap") || mode.equals("sta") || mode.equals("adhoc")) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, mode + "는 올바른 Mode입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "APMODE");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, mode + "는 올바른 Mode가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * Encryption가 형식에 맞는지 체크한다. 
	 * @param Encryption: 보안 종류 (none/ wep-open/ wep-shared/ psk/ psk2/ psk-mixed)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean APEncryption(Context context, String encryption) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if(encryption.equals("none") || encryption.equals("wep-open") || encryption.equals("wep-share") ||
				encryption.equals("psk") || encryption.equals("psk2") || encryption.equals("psk-mixed")) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, encryption + "는 올바른 Encryption입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "APEncryption");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, encryption + "는 올바른 Encryption이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * Encryption KEY가 형식에 맞는지 체크한다. 
	 * @param Encryption: 보안키 (auto/ ccmp/ tkip/ tkip+ccmp)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean APEncryptionKEY(Context context, String encryptionkey) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if(encryptionkey.equals("auto") || encryptionkey.equals("ccmp") || 
				encryptionkey.equals("tkip") || encryptionkey.equals("tkip+ccmp")) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, encryptionkey + "는 올바른 Encryption KEY입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "APEncryptionKEY");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, encryptionkey + "는 올바른 Encryption KEY가 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * IFNAME가 형식에 맞는지 체크한다. 
	 * @param mode: 네트워크 모드 (eth0.2/ apcli0/ eth0.1)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean IFNAME(Context context, String mode) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if(mode.equals("eth0.2") || mode.equals("apcli0") || mode.equals("eth0.1")) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, mode + "는 올바른 IFNAME입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "IFNAME");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, mode + "는 올바른 IFNAME이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * ApCliChannel가 0~13 이내인지 체크한다. 
	 * @param ssid: 네트워크 이름 (1~30)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean ApCliChannel(Context context, String channel) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if(channel.equals(AppConst.ApCliChannel_AUTO) || channel.equals(AppConst.ApCliChannel_1) || channel.equals(AppConst.ApCliChannel_2) ||
				channel.equals(AppConst.ApCliChannel_3) || channel.equals(AppConst.ApCliChannel_4) || channel.equals(AppConst.ApCliChannel_5) ||
				channel.equals(AppConst.ApCliChannel_6) || channel.equals(AppConst.ApCliChannel_7) || channel.equals(AppConst.ApCliChannel_8) ||
				channel.equals(AppConst.ApCliChannel_9) || channel.equals(AppConst.ApCliChannel_10) || channel.equals(AppConst.ApCliChannel_11) ||
				channel.equals(AppConst.ApCliChannel_12) || channel.equals(AppConst.ApCliChannel_13)) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, channel + "는 올바른 채널값입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "IFNAME");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, channel + "는 올바른 채널값이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * ApCliSSID가 0~30 이내인지 체크한다. 
	 * @param ssid: 네트워크 이름 (1~30)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean ApCliSSID(Context context, String ssid) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<ssid.length() && ssid.length()<31) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, ssid + "는 올바른 ApCliSSID입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "ApCliSSID");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, ssid + "는 올바른 ApCliSSID이 아닙니다!");
			return false; 
		}	
	}
	
	/**
	 * ApCliKey가 0~30 이내인지 체크한다. 
	 * @param KEY: 네트워크 보안키 (1~30)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean ApCliKey(Context context, String key) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if (0<key.length() && key.length()<31) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, key + "는 올바른 ApCliKey입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "ApCliKey");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, key + "는 올바른 ApCliKey가 아닙니다!");
			return false; 
		}	
	}
	
	
	/**
	 * ApCliAuthMode가 형식에 맞는지 체크한다. 
	 * @param ApCliAuthMode: 암호화 방식 (WPAPSK/ WPA2PSK/ WEP/ NONE)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean ApCliAuthMode(Context context, String mode) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if(mode.equals("WPAPSK") || mode.equals("WPA2PSK") || mode.equals("WEP") || mode.equals("NONE")) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, mode + "는 올바른 ApCliAuthMode입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "ApCliAuthMode");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, mode + "는 올바른 ApCliAuthMode가 아닙니다!");
			return false; 
		}	
	}
	
	
	/**
	 * ApCliEncrypType가 형식에 맞는지 체크한다. 
	 * @param Encryption: 암호화 키 (AES/ TKIP/ WEP/ NONE)
	 * @return boolean: 오류 체크 결과값
	 */
	public static boolean ApCliEncrypType(Context context, String encryption) {
		SharedPrefUtil pref = new SharedPrefUtil(context);
		if(encryption.equals("AES") || encryption.equals("TKIP") || encryption.equals("WEP") || encryption.equals("NONE")) {
			//if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, encryption + "는 올바른 ApCliEncrypType입니다.");
			return true;
		} else {
			pref.put(SharedPrefUtil.RANGE_CHECK_ISSUE, "ApCliEncrypType");
			if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, encryption + "는 올바른 ApCliEncrypType이 아닙니다!");
			return false; 
		}	
	}
}
