package sleep.simdori.com.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import sleep.simdori.com.AppConst;

/**
 * 재부팅 후 MQTT Service를 다시 실행시킨다. 
 *
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "onReceive");
		context.startService(new Intent(context, MQTTservice.class));
	}
}