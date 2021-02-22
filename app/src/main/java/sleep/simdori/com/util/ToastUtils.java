package sleep.simdori.com.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * 사용자에 토스트 알림창을 보여준다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.mqtttest.activity
 * @since       Android 5.1
 */
public class ToastUtils {
    /**
     * 사용자에게 보여줄 토스트 알림창 메시지를 설정한다.
     * @param 	Context	: 호출한 액티비티
     * @param	int		: 알림 메시지 주소(Sting.xml 내에 ID)
     * @throws 	None
     * @return 	None
     */
    public static void ToastShow(Context context, int stringId){
        SharedPreferences sp = context.getSharedPreferences("taost", Context.MODE_PRIVATE);
        SharedPreferences.Editor se = sp.edit();

        long now = System.currentTimeMillis();

        if(sp.contains(stringId+"")){
            long time = Long.parseLong(sp.getString(""+stringId, "0"));
            if((now-time) > 3000){
                try{
                    Toast.makeText(context, context.getResources().getString(stringId), 0).show();
                }catch(Exception e){}
                se.putString(""+stringId, ""+now);
                se.commit();
            }
        } else {
            try{
                Toast.makeText(context, context.getResources().getString(stringId), 0).show();
            }catch(Exception e){}
            se.putString(""+stringId, ""+now);
            se.commit();
        }
    }

    /**
     * 사용자에게 보여줄 토스트 알림창 메시지를 설정한다.
     * @param 	context	: 호출한 액티비티
     * @param	str	: 알림 메시지
     * @throws 	None
     * @return 	None
     */
    public static void ToastShow(Context context, String str){
        SharedPreferences sp = context.getSharedPreferences("taost", Context.MODE_PRIVATE);
        SharedPreferences.Editor se = sp.edit();

        long now = System.currentTimeMillis();

        if(sp.contains(str)){
            long time = Long.parseLong(sp.getString(str, "0"));
            if((now-time) > 3000){
                Toast.makeText(context, str, 0).show();
                se.putString(str, ""+now);
                se.commit();
            }
        } else {
            Toast.makeText(context, str, 0).show();
            se.putString(str, ""+now);
            se.commit();
        }
    }

    /**
     * 사용자에게 토스트 알림창을 보여준다.
     * @param 	Context		: 호출한 액티비티
     * @param	Toast toast : 토스트 알림창
     * @param	int			: 알림 메시지 주소(Sting.xml 내에 ID)
     * @throws 	None
     * @return 	None
     */
    public static void ToastShow(Context context, Toast toast, int stringId){
        if(toast != null){
            SharedPreferences sp = context.getSharedPreferences("taost", Context.MODE_PRIVATE);
            SharedPreferences.Editor se = sp.edit();

            long now = System.currentTimeMillis();

            if(sp.contains(stringId+"")){
                long time = Long.parseLong(sp.getString(""+stringId, "0"));
                if((now-time) > 3000){
                    toast.show();
                    se.putString(""+stringId, ""+now);
                    se.commit();
                }
            } else {
                toast.show();
                se.putString(""+stringId, ""+now);
                se.commit();
            }
        }
    }

    /**
     * 사용자에게 토스트 알림창을 보여준다.
     * @param 	Context		: 호출한 액티비티
     * @param	Toast toast : 토스트 알림창
     * @param	Sting		: 알림 메시지
     * @throws 	None
     * @return 	None
     */
    public static void ToastShow(Context context, Toast toast, String str){
        if(toast != null){
            SharedPreferences sp = context.getSharedPreferences("taost", Context.MODE_PRIVATE);
            SharedPreferences.Editor se = sp.edit();

            long now = System.currentTimeMillis();

            if(sp.contains(str)){
                long time = Long.parseLong(sp.getString(str, "0"));
                if((now-time) > 3000){
                    toast.show();
                    se.putString(str, ""+now);
                    se.commit();
                }
            } else {
                toast.show();
                se.putString(str, ""+now);
                se.commit();
            }
        }
    }
}

