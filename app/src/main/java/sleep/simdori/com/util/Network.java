package sleep.simdori.com.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.R;
import sleep.simdori.com.util.PopupUtils;
import sleep.simdori.com.util.SharedPrefUtil;

/**
 * 네트워크 정보 및 연결을 처리한다.
 * @version 2.00 03/08/16
 * @author 이선호
 * @see sleep.simdori.com.activity
 * @since Android 5.1
 */
public class Network {
    // API
    private static SharedPrefUtil pref = null;
    private static ConnectivityManager conMgr = null;
    private static NetworkInfo wifi_info = null, mobile_info = null, activeNetwork = null;
    private static WifiManager wifiManager = null;
    private static WifiInfo wifiInfo = null;
    private static WifiConfiguration config = null, tempConfig = null;
    private static List<WifiConfiguration> configs = null;

    // Values
    private static boolean status = false;
    private static int networkID = -1;

    public Network(Context context) {
        pref = new SharedPrefUtil(context);
        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Wi-Fi를 실행한다.
     * @param context : 호출한 액티비티
     * @throws None
     * @return None
     */
    public static void setWifiOn(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            try {
                wifiManager.setWifiEnabled(true);
            } catch (Exception e) {
                // Wi-Fi정보를 처리하지 못했습니다.
                e.printStackTrace();
                PopupUtils.Notify_Exception(context, context.getString(R.string.dialog_WIFI_FAIL));
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - Wi-fi 시작 오류!!");
            }
        }
    }

    /**
     * 보안키 없이 Wi-Fi에 연결한다.
     * @param context  : 호출한 액티비티
     * @param pb  : 호츨한 액티비티의 프로그레스바
     * @param ssid : 연결하려는 Wi-fi 이름
     * @throws None
     * @return int : Wi-Fi 연결상태
     */
//	public static int setWifiConnection_OPEN(Context context, ProgressBar pb, String ssid) {
//		pref = new SharedPrefUtil(context);
//		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//		try {
//			config = new WifiConfiguration();
//			config.SSID = "\"" + ssid + "\""; // "6450CE4e"
//			config.status = WifiConfiguration.Status.DISABLED;
//			config.priority = 40;
//			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//			config.allowedAuthAlgorithms.clear();
//			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//
//			/*if(getWifiConnectityStatus(context)) {
//				wifiManager.disconnect();
//				if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> Disconnect");
//			}
//			int formerNetworkId = wifiManager.updateNetwork(config);
//	        networkID = formerNetworkId == -1 ? wifiManager.addNetwork(config) : formerNetworkId;
//	        wifiManager.enableNetwork(networkID, true);
//	        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> Enable Network: " + networkID);
//	        wifiManager.saveConfiguration();
//	        wifiManager.reconnect();
//	        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> Reconnect");
//	        if(networkID != -1) {
//	        	return AppConst.WIFI_CONNECTION_SUCCESS;
//	        }*/
//
//			// 1. 동일한 WifiConfiguration가 있는지 체크
//			tempConfig = getConfigs(context, ssid);
//			if (tempConfig != null) {
//				wifiManager.removeNetwork(tempConfig.networkId);
//				if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection / 삭제된 SSID는 : " + tempConfig.SSID);
//			} else {
//				// 동일한 WifiConfiguration가 없으므로 알림X
//			}
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection / 추가된 SSID는 : " + config.toString() + " / NetworkID: " + networkID);
//			/*networkID = wifiManager.getConnectionInfo().getNetworkId();
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> what is connected: " + networkID);
//			wifiManager.removeNetwork(networkID);
//			wifiManager.saveConfiguration();
//			networkID = wifiManager.addNetwork(config);
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> to be connected: " + networkID);*/
//
//			// 2. EnableNetwork으로 Wi-Fi 연결 성공한 경우
//			if (wifiManager.enableNetwork(15, true)) {
//				// 2-1. Wi-Fi 연결상태 확인
//				int rerty = 0;
//				while(!getWifiConnectityStatus(context)) {
//					if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection / Fail Wifi Connection!!!");
//					rerty++;
//					if (rerty>500) {
//						return 0;
//					} else {
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// 다른 곳에서 알림 처리
//							e.printStackTrace();
//						}
//					}
//				}
//
//				// 2-2. Wi-Fi 연결 플래그 저장
//				pref.put(SharedPrefUtil.WIFI_CONNECTION, true);
//				if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection / Wi-Fi 연결 성공: SSID= " + config.SSID + " / preSharedKey= " + config.preSharedKey);
//				return AppConst.WIFI_CONNECTION_SUCCESS;
//
//			// 3. EnableNetwork으로 Wi-Fi 연결에 실패한 경우
//			} else {
//				// 다른 곳에서 알림 처리
//				if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection / Wi-Fi 연결 실패: SSID= " + config.SSID + " / preSharedKey= " + config.preSharedKey);
//			}
//		} catch (Exception e) {
//			// 다른 곳에서 알림 처리
//			e.printStackTrace();
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection / Wi-Fi 연결 실패!!");
//		}
//
//		// 프로그레스바 없애기
//		if(pb != null) {
//			pb.setVisibility(View.GONE);
//			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Wi-Fi 선택 프로그레스바 끝");
//		} else {
//			// 뷰와 관련되어 알림X
//		}
//		return AppConst.WIFI_CONNECTION_FAIL;
//
//		/*config.SSID = "\"" + ssid + "\"";
//		config.status = WifiConfiguration.Status.DISABLED;
//		config.priority = 40;
//		config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//		config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//		config.preSharedKey = "\"".concat(key).concat("\"");*/
//	}

    /**
     * 암호화 방식에 맞춰서 Wi-Fi에 연결한다.
     * @param context  : 호출한 액티비티
     * @param ssid : Wi-fi 네트워크 이름
     * @param key : Wi-fi 네트워크 보안키
     * @param encryprion : Wi-fi 암호화 방식
     * @throws None
     * @return int : Wi-Fi 연결상태
     */
    public static int setWifiConnection(Context context, String ssid, String key, String encryprion) {
        pref = new SharedPrefUtil(context);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // 0. SSID를 위한 WifiConfiguration 생성
        config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\""; // "6450CE4e"
        //config.status = WifiConfiguration.Status.ENABLED;
        //config.priority = 40;

        // 1. 암호화 형식에 맞춰 WifiConfiguration 저장
        if(encryprion.equalsIgnoreCase("NONE")) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if(encryprion.equalsIgnoreCase("WEP")) {
            config.hiddenSSID = true;
            config.wepTxKeyIndex = 0;
            config.wepKeys[0] = "\"" + key + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else {
            config.preSharedKey = "\"" + key + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.status = WifiConfiguration.Status.ENABLED;
        }

		/*if(getWifiConnectityStatus(context)) {
			wifiManager.disconnect();
			if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> Disconnect");
		}

		int formerNetworkId = wifiManager.updateNetwork(config);
		networkID = formerNetworkId == -1 ? wifiManager.addNetwork(config) : formerNetworkId;
		wifiManager.enableNetwork(networkID, true);
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> Enable Network: " + networkID);

		wifiManager.saveConfiguration();
		wifiManager.reconnect();
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - NetworkID >> Reconnect");

		if(networkID != -1) {
			return AppConst.WIFI_CONNECTION_SUCCESS;
		}*/

        // 2. 기존 네크워크 구성을 제거하고, 새로운 네트워크 구성으로 연결
        tempConfig = getConfigs(context, ssid);
        if (tempConfig != null) {
            status = wifiManager.removeNetwork(tempConfig.networkId);
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection() / 삭제된 SSID: " + tempConfig.SSID + " , Status: " + status + " , Encryption: " + encryprion );
        } else {
            // 동일한 WifiConfiguration가 없으므로 처리X
        }
        networkID = wifiManager.addNetwork(config);
        wifiManager.saveConfiguration();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection() / 추가된 SSID: " + config.SSID + " , NetworkID: " + networkID + " , Encryption: " + encryprion);

        // 3. EnableNetwork으로 Wi-Fi 연결 성공한 경우
        if (wifiManager.enableNetwork(networkID, true)) {
            // 3-1. Wi-Fi 연결상태 확인
            int rerty = 0;
            while(!getWifiConnectityStatus(context)) {
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection() / Fail Wifi Connection!!!");
                rerty++;
                if (rerty>50) {
                    return 0;
                } else {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        // 다른 곳에서 알림 처리
                        e.printStackTrace();
                    }
                }
            }

            // 3-2. Wi-Fi 연결 플래그 저장
            pref.put(SharedPrefUtil.WIFI_CONNECTION, true);
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection() / Wi-Fi 연결 성공: preSharedKey: " + config.preSharedKey + " ? wepKeys: " + config.wepKeys[0]);
            return AppConst.WIFI_CONNECTION_SUCCESS;

            // 4. EnableNetwork으로 Wi-Fi 연결에 실패한 경우
        } else {
            // 다른 곳에서 알림 처리
            if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Network - SetWifiConnection() / Wi-Fi 연결 실패: preSharedKey: " + config.preSharedKey + " ? wepKeys: " + config.wepKeys[0]);
        }
        return AppConst.WIFI_CONNECTION_FAIL;
    }


    /**
     * 특정 SSID의 WifiConfiguration 정보를 반환한다.
     * @param context : 호출한 액티비티
     * @throws None
     * @return GetAPList : Wi-Fi 정보
     */
    public static WifiConfiguration getConfigs(Context context, String SSID) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        configs = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + SSID + "\"")) {
                return config;
            } else {
                // 일치하는 SSID가 없음
            }
        }
        return null;
    }

    /**
     * 스캔목록의 암호화 형식을 반환한다.
     * @param scanResult : 스캔 목록
     * @throws None
     * @return String : 암호화 형식
     */
    public static String getScanResultSecurity(ScanResult scanResult) {
        String security = "";
        String cap = scanResult.capabilities;
        String[] securityModes = {"WPA2-PSK", "WPA-PSK", "WEP"};
        for (int i=securityModes.length-1; i>=0; i--) {
            if (cap.contains(securityModes[i])) {
                security = securityModes[i];
            } else {
                security = "NONE";
            }
        }

		/*String[] securityType = {"CCMP+TKIP", "CCMP", "TKIP", "WEP"};
		for (int i=securityType.length-1; i>=0; i--) {
            if (cap.contains(securityType[i])) {
            	encryption[0] = securityType[i];
            } else {
            	encryption[0] = "NONE";
            }
        }*/
        return security;
    }

    /**
     * 스캔목록의 암호화 형식/키를 반환한다.
     * @param scanResult : 스캔 목록
     * @throws None
     * @return String[] : 암호화 형식/키
     */
    public static String[] getScanResultEncryption(ScanResult scanResult) {
        String[] encryption = new String[2];
        String cap = scanResult.capabilities;
        if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Network - getScanResultSecurity() / Capabilities: " + scanResult.capabilities);

        // 암호화 방식
        if(cap.contains("WPA2-PSK")) {
            encryption[0] = AppConst.ApCliAuthMode_WPA2;
        } else if(cap.contains("WPA-PSK")) {
            encryption[0] = AppConst.ApCliAuthMode_WPA;
        } else if(cap.contains("WEP")) {
            encryption[0] = AppConst.ApCliAuthMode_WEP;
        } else {
            encryption[0] = AppConst.ApCliAuthMode_NONE;
        }

        // 암호화 키
        if(cap.contains("CCMP")) {
            encryption[1] = AppConst.ApCliEncrypType_AES;
        } else if(cap.contains("TKIP")) {
            encryption[1] = AppConst.ApCliEncrypType_TKIP;
        } else if(cap.contains("WEP")) {
            encryption[1] = AppConst.ApCliEncrypType_WEP;
        } else {
            encryption[1] = AppConst.ApCliEncrypType_NONE;
        }
        return encryption;
    }

//    /**
//     * 연결되어 있는 공유기 정보를 반환한다.
//     * @param context  : 호출한 액티비티
//     * @throws None
//     * @return GetAPList : 공유기 정보
//     */
//    public static GetAPList getAPInfo(Context context) {
//        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        wifiInfo = wifiManager.getConnectionInfo();
//        String ssid = wifiInfo.getSSID();
//        String bssid = wifiInfo.getBSSID();
//        int ip = wifiManager.getDhcpInfo().serverAddress;
//        String uri = android.text.format.Formatter.formatIpAddress(ip);
//
//        GetAPList list = new GetAPList(ssid, bssid, uri);
//        if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get APList: SSID= " + ssid + " / BSSID= " + bssid + " / URI= " + uri);
//
//        return list;
//    }

    /**
     * 연결되어 있는 서버주소를 반환한다.
     * @param context : 호출한 액티비티
     * @throws None
     * @return String : 서버주소
     */
    public static String getServerIP(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = wifiManager.getDhcpInfo().serverAddress;
        return android.text.format.Formatter.formatIpAddress(ip);
    }

    public static String getPhoneMAC(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String phone_mac = wifiManager.getConnectionInfo().getMacAddress();
        if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Get Phone MAC= " + phone_mac);

        return phone_mac;
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getPhoneMAC(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString().toLowerCase();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }


    /**
     * 모바일 및 와이파이를 가지고 있는지를 알려준다.
     * @param context  : 호출한 액티비티
     * @throws None
     * @return boolean : 네트워크 존재 여부
     */
    public static boolean getConnectivity(Context context) {
        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi_info = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobile_info = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi_info != null || mobile_info != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 네트워크에 연결되어 있는지를 알려준다.
     * @param context  : 호출한 액티비티
     * @throws None
     * @return boolean : 네트워크 연결 여부
     */
    public static boolean getConnectivityStatus(Context context) {
        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = conMgr.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            } else {
                // 단순히 네트워크 연결상태를 확인하는 것이므로 알림X
            }
        } else {
            // 단순히 네트워크 연결상태를 확인하는 것이므로 알림X
        }
        return false;
    }

    /**
     * Wi-FI에 연결되어 있는지를 알려준다.
     * @param context : 호출한 액티비티
     * @throws None
     * @return boolean : Wi-Fi 연결 여부
     */
    public static boolean getWIFIStatus(Context context) {
        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi_info = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi_info == null) {
            return false;
        } else if (wifi_info.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Wi-FI에 연결되어 있는지를 알려준다.
     * @param context : 호출한 액티비티
     * @throws None
     * @return boolean : Wi-Fi 연결 여부
     */
    public static boolean getWIFISConnectedStatus(Context context) {
        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi_info = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi_info == null) {
            return false;
        } else if (wifi_info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Wi-Fi 네트워크에 연결되어 있는지를 알려준다.
     * @param context : 호출한 액티비티
     * @throws None
     * @return boolean : Wi-Fi 네트워크 연결 여부
     */
    public static boolean getWifiConnectityStatus(Context context) {
        conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = conMgr.getActiveNetworkInfo();

        if (activeNetwork!=null && activeNetwork.getTypeName().equals("WIFI")) {
            if (activeNetwork.isConnected()) {
                return true;
            } else {
                // 아래에서 false 반환
            }
        } else {
            // 아래에서 false 반환
        }
        return false;
    }
}
