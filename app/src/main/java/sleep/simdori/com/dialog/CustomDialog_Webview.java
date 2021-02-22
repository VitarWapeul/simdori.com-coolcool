package sleep.simdori.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import sleep.simdori.com.R;

/**
 * 사용자에게 알림창을 보여준다.
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com
 * @since       Android 5.1
 */
public class CustomDialog_Webview extends Dialog {
	// Views 
	private TextView title;
	private WebView wv; 
	private Button btn;
	
	public TextView getTitle(){
		return title;
	}
	public WebView getWv(){
		return wv;
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
	 * @param context : 호출한 액티비티
	 */
	public CustomDialog_Webview(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View view = View.inflate(context, R.layout.customdialog_webview, null);
		setContentView(view);
		setCancelable(true);

		// Views
		title = (TextView) findViewById(R.id.customDialog_webview_title);
		wv = (WebView) findViewById(R.id.customDialog_webview_wv);
		btn = (Button)findViewById(R.id.customDialog_webview_btn);
	}
	
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		super.setOnDismissListener(listener);
	}
} 