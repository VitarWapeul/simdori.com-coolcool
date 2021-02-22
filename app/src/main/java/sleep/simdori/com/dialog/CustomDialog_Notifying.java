package sleep.simdori.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import sleep.simdori.com.R;

/**
 * 사용자에게 알림창을 보여준다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.adapter
 * @since       Android 5.1
 */
public class CustomDialog_Notifying extends Dialog {
	// Views 
	private TextView title, msg;
	private Button btn;
	
	public TextView getTitle(){
		return title;
	}
	public TextView getMsg(){
		return msg;
	}
	public Button getBtn(){
		return btn;
	}	
	
	/**
	 * 커스텀 다이얼로그를 초기화하는 함수
	 * <View>
	 * title : 	다이얼로그 타이틀
	 * msg	: 	다이얼로그 알림 메시지
	 * btn	: 	확인 버튼
	 * 
	 * @param context
	 */
	public CustomDialog_Notifying(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View view = View.inflate(context, R.layout.customdialog_notifying, null);
		setContentView(view);
		setCancelable(true);

		// Views
		title = (TextView) findViewById(R.id.customDialog_notifying_title);
		msg = (TextView) findViewById(R.id.customDialog_notifying_msg);
		btn = (Button)findViewById(R.id.customDialog_notifying_btn);
	}
	
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		super.setOnDismissListener(listener);
	}
} 