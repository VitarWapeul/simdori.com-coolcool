package sleep.simdori.com.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import sleep.simdori.com.AppConst;

/**
 * 서버와 JSON통신을 하기 위한 공통 클래스
 * 암호화는 AES256를 사용하나, 현재는 제외되어 있다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class JsonConnect {
    // API
    private SharedPrefUtil pref	= null;
    private JSONObject json 			= null;
    private URL url 					= null;
    private HttpURLConnection conn    = null;
    private OutputStream os   	= null;
    private InputStream is   	= null;
    private ByteArrayOutputStream baos 	= null;

    // Values
    private boolean aes_status 	= true;
    private String key = "abcdefghijklmnopqrstuvwxyz123456";
    private String encrypt, decrypt, session, response = "";

    public JsonConnect(Context context, String uri) {
        // API
        json = new JSONObject();

        // 세션 및 암호화 가져오기
        pref = new SharedPrefUtil(context);
        session = pref.getValue(SharedPrefUtil.SESSION_ID, "");
        aes_status = pref.getValue(SharedPrefUtil.AES_STATUS, true);

        // 연결할 주소 생성
        try {
            this.url = new URL(uri);
            if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "JSON 통신 주소는 : " + url.toString());
        } catch (MalformedURLException e) {
            // 다른 곳에서 알림 처리
            e.printStackTrace();
        }
    }

    /**
     * <Request>
     * 라이브러리	: HttpURLConnection
     * 형식 		: POST
     * 프로토콜		: JSON
     * 세션			: true
     * <Rseponse>
     * 라이브러리	: HttpURLConnection
     * 형식 		: POST
     * 프로토콜		: JSON
     * 세션			: true
     * @param 	None
     * @throws 	None
     * @return 	None
     */
    public String Connect(JSONObject job) {
        try {
            // AES 암호화
            if(aes_status) {
                //encrypt = AES.encrypt(job.toString(), key);

                encrypt = sleep.simdori.com.util.AES256Cipher.AES_Encode(job.toString(), key);
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "Encrypt_Data: " + encrypt);
            } else {
                encrypt = job.toString();
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "No Encrypt_Data: " + encrypt);
            }
            json.put("data", encrypt);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 세션 처리
            conn.setRequestProperty("Cookie", session);

            os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();

            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData = null;
            int nLength = 0;
            while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();
            response = new String(byteData);
            json = new JSONObject(response);
            response = json.getString("data");

            // AES 부호화
            if(aes_status) {
                //decrypt = AES.decrypt(response, key);
                decrypt = sleep.simdori.com.util.AES256Cipher.AES_Decode(response, key);
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Decrypt Data : " + decrypt);
            } else {
                // 암호화 처리 문제이므로 알림X
                decrypt = response;
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "No Decrypt Data : " + decrypt);
            }

            // 세션 저장
            List <String> cookies = conn.getHeaderFields().get("set-cookie");
            if(cookies != null) {
                for (String cookie : cookies) {
                    session = cookie.split(";\\s*")[0];
                    pref.put(SharedPrefUtil.SESSION_ID, session);
                    if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Cookie : " + session);
                }
            } else {
                if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "Defalut Cookie : " + session);
            }
        } catch (Exception e) {
            // 다른 곳에서 알림 처리
            e.printStackTrace();
        }
        return decrypt;
    }
}
