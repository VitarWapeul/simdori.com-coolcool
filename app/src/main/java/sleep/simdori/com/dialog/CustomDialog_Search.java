package sleep.simdori.com.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import sleep.simdori.com.R;

/**
 * 로그인화면에서 ID/비밀번호찾기를 눌렀을 때
 * ID 찾기 알림창을 먼저 보여준다. 
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.adapter
 * @since       Android 5.1
 */
public class CustomDialog_Search extends Dialog {
	// Views
	private LinearLayout select_linear, id_linear, pw_linear;
	private TextView id, pw;
	private EditText id_edit, pw_id;
	private Button id_btn_cancle, id_btn_confirm, pw_btn_cancel, pw_btn_confirm;
	
	public LinearLayout getLinear(){
		return select_linear;
	}
	public LinearLayout getID_Linear(){
		return id_linear;
	}
	public LinearLayout getPW_Linear(){
		return pw_linear;
	}
	public TextView getID(){
		return id;
	}
	public TextView getPW(){
		return pw;
	}
	public EditText getID_Edit(){
		return id_edit;
	}
	public EditText getPW_id(){
		return pw_id;
	}
	public Button getID_Btn_Cancel(){
		return id_btn_cancle;
	}
	public Button getID_Btn_Confirm(){
		return id_btn_confirm;
	}
	public Button getPW_Btn_Cancel(){
		return pw_btn_cancel;
	}
	public Button getPW_Btn_Confirm(){
		return pw_btn_confirm;
	}		
	
	/**
	 * 커스텀 다이얼로그를 초기화하는 함수
	 * <View>
	 * select_linear 	: ID / 비밀번호 선택창
	 * id : 			: ID 타이틀
	 * id_edit			: 이메일 입력
	 * id_btn_cancle 	: 취소 버튼
	 * id_btn_confirm 	: 확인 버튼
	 * pw				: 비밀번호 타이틀
	 * pw_id			: ID 입력
	 * pw_edit			: 이메일 입력
	 * pw_btn_cancle 	: 취소 버튼
	 * pw_btn_confirm 	: 확인 버튼
	 * @param context
	 */
	
	public CustomDialog_Search(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
		//팝업 외부 뿌연 효과
		layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		//뿌연 효과 정도
		layoutParams.dimAmount= 1.0f;
		//적용
		getWindow().setAttributes(layoutParams);
		View view = View.inflate(context, R.layout.customdialog_search, null);
		setContentView(view);
		setCancelable(true);

		// ID 또는 비밀번호 선택창
		select_linear = (LinearLayout) findViewById(R.id.customDialog_select);
		
		// ID 찾기
		id = (TextView) findViewById(R.id.customDialog_id);
		id_linear = (LinearLayout) findViewById(R.id.ID_Search_Linear);
		id_edit = (EditText) findViewById(R.id.customDialog_ID_edit);
		id_btn_cancle = (Button)findViewById(R.id.customDialog_ID_btn_Cancel);
		id_btn_confirm = (Button)findViewById(R.id.customDialog_ID_btn_Confirm);
		
		// 비밀번호 찾기
		pw = (TextView) findViewById(R.id.customDialog_pw);
		pw_linear = (LinearLayout) findViewById(R.id.PW_Search_Linear);
		pw_id = (EditText) findViewById(R.id.customDialog_PW_id);
		pw_btn_cancel = (Button)findViewById(R.id.customDialog_PW_btn_Cancel);
		pw_btn_confirm = (Button)findViewById(R.id.customDialog_PW_btn_Confirm);
	}
	
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		super.setOnDismissListener(listener);
	}
} 