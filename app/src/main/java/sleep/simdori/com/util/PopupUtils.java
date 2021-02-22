package sleep.simdori.com.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.dialog.CustomDialog_Notify;
import sleep.simdori.com.dialog.CustomDialog_Notifying;
import sleep.simdori.com.dialog.CustomDialog_Webview;

/**
 * 사용자에게 팝업을 띄우기 위한 클래스
 * @version     2.00 03/08/16
 * @author      이선호
 * @see         sleep.simdori.com.activity
 * @since       Android 5.1
 */
public class PopupUtils {
    // API
    private static Context mContext = null;
    private static CustomDialog_Notifying notifying = null;
    private static CustomDialog_Notify notify = null;
//    private static CustomDialog_Notifing notifing = null;
//    private static CustomDialog_Notify_Check notify_check = null;
//    private static CustomDialog_Sharing edit = null;
//    private static CustomDialog_Sharing2 edit2 = null;
//    private static CustomDialog_Key key = null;
    private static CustomDialog_Webview webview = null;

    // Value
    private static WebView wv = null;

    public static PopupUtils newInstance(Context c) {
        PopupUtils popupUtils = new PopupUtils(c);
        return popupUtils;
    }

    public PopupUtils(Context c) {
        mContext = c;
        notifying = new CustomDialog_Notifying(mContext);
        notify = new CustomDialog_Notify(mContext);
//        notify_check = new CustomDialog_Notify_Check(mContext);
//        notifing = new CustomDialog_Notifing(mContext);
//        edit = new CustomDialog_Sharing(mContext);
//        edit2 = new CustomDialog_Sharing2(mContext);
//        key = new CustomDialog_Key(mContext);
    }

    public static CustomDialog_Notifying getNotify_Exception() {
        return notifying;
    }

    public static CustomDialog_Notify getNotity() {
        return notify;
    }
//
//    public static CustomDialog_Notifing getNotifing() {
//        return notifing;
//    }
//
//    public static CustomDialog_Notify_Check getNotify_check() {
//        return notify_check;
//    }
//
//    public static CustomDialog_Sharing getEdit() {
//        return edit;
//    }
//
//    public static CustomDialog_Sharing2 getEdit2() {
//        return edit2;
//    }
//
//    public static CustomDialog_Key getKey() {
//        return key;
//    }

    /**
     * 예외처리 알림을 보여준다.
     * @param 	mContext : 호출한 액티비티
     * @param	msg	: 알림 메시지
     * @throws 	None
     * @return 	None
     */
    public static void Notify_Exception(Context mContext, String msg) {
        notifying = new CustomDialog_Notifying(mContext);
        notifying.show();
        notifying.getMsg().setText(msg);
        notifying.getBtn().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifying.dismiss();
            }
        });
    }

//    /**
//     * 예외처리 알림을 보여준다.
//     * @param 	mContext : 호출한 액티비티
//     * @param	msg	: 알림 메시지
//     * @throws 	None
//     * @return 	None
//     */
//    public static void Notify_Exception(Context mContext, int msg) {
//        notifying = new CustomDialog_Notifying(mContext);
//        notifying.show();
//        notifying.getMsg().setText(msg);
//        notifying.getBtn().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notifying.dismiss();
//            }
//        });
//    }
//
//    /**
//     * 예외처리 알림을 보여준다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @throws 	None
//     * @return 	None
//     */
//    public static void Notify_Exception(Context mContext, String title, String msg) {
//        notifying = new CustomDialog_Notifying(mContext);
//        notifying.show();
//        notifying.getTitle().setText(title);
//        notifying.getMsg().setText(msg);
//        notifying.getBtn().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notifying.dismiss();
//            }
//        });
//    }

    /**
     * 예외처리 알림창 확인 버튼을 반환한다.
     * @param 	Context : 호출한 액티비티
     * @param	String	: 알림 메시지
     * @throws 	None
     * @return 	Button	: 확인 버튼
     */
    public static Button Notify_Exception_Btn(Context mContext, String msg) {
        notifying = new CustomDialog_Notifying(mContext);
        notifying.show();
        notifying.getMsg().setText(msg);
        return notifying.getBtn();
    }

    /**
     * 알림창 확인 버튼을 반환한다.
     * @param 	Context : 호출한 액티비티
     * @param	String	: 알림 메시지
     * @param	int		: 버튼 이름
     * @throws 	None
     * @return 	Button	: 확인 버튼
     */
    public static Button Notify(Context mContext, int msg, int confirm) {
        notify = new CustomDialog_Notify(mContext);
        notify.show();
        notify.getMsg().setText(msg);
        notify.getBtn_Cancel().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.dismiss();
            }
        });
        notify.getBtn_Confirm().setText(confirm);
        return notify.getBtn_Confirm();
    }
//
//    /**
//     * 20170321 DY 버전체크 처리
//     * 일주일동안 안보이기 체크박스가 있는
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @param	int		: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static Button NotifyCheck(Context mContext, int msg, int confirm) {
//        notify_check = new CustomDialog_Notify_Check(mContext);
//        notify_check.show();
//        notify_check.getMsg().setText(msg);
//        notify_check.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notify_check.dismiss();
//            }
//        });
//        notify_check.getBtn_Confirm().setText(confirm);
//        return notify_check.getBtn_Confirm();
//    }
//
    /**
     * 알림창 확인 버튼을 반환한다.
     * @param 	Context : 호출한 액티비티
     * @param	String	: 알림 메시지
     * @param	int		: 버튼 이름
     * @throws 	None
     * @return 	Button	: 확인 버튼
     */
    public static Button Notify(Context mContext, String msg, int confirm) {
        notify = new CustomDialog_Notify(mContext);
        notify.show();
        notify.getMsg().setText(msg);
        notify.getBtn_Cancel().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.dismiss();
            }
        });
        notify.getBtn_Confirm().setText(confirm);
        return notify.getBtn_Confirm();
    }
//
    /**
     * 알림창 확인 버튼을 반환한다.
     * @param 	Context : 호출한 액티비티
     * @param	String	: 알림 메시지
     * @param	int		: 버튼 이름
     * @throws 	None
     * @return 	Button	: 확인 버튼
     */
    public static Button Notify(Context mContext, String title, String msg, int confirm) {
        notify = new CustomDialog_Notify(mContext);
        notify.show();
        notify.getTitle().setText(title);
        notify.getMsg().setText(msg);
        notify.getBtn_Cancel().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.dismiss();
            }
        });
        notify.getBtn_Confirm().setText(confirm);
        return notify.getBtn_Confirm();
    }
//
    /**
     * 알림창 확인 버튼을 반환한다.
     * @param 	mContext : 호출한 액티비티
     * @param	title	: 타이틀
     * @param	msg		: 알림 메시지
     * @param	confirm	: 버튼 이름
     * @throws 	None
     * @return 	Button	: 확인 버튼
     */
    public static Button Notify(Context mContext, int title, int msg, int confirm) {
        notify = new CustomDialog_Notify(mContext);
        notify.show();
        notify.getTitle().setText(title);
        notify.getMsg().setText(msg);
        notify.getBtn_Cancel().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.dismiss();
            }
        });
        notify.getBtn_Confirm().setText(confirm);
        return notify.getBtn_Confirm();
    }
//
    /**
     * 알림창 확인 버튼을 반환한다.
     * @param 	mContext : 호출한 액티비티
     * @param	title	: 타이틀
     * @param	msg		: 알림 메시지
     * @param	confirm	: 버튼 이름
     * @throws 	None
     * @return 	Button	: 확인 버튼
     */
    public static Button Notify(Context mContext, int title, int msg, int cancle, int confirm) {
        notify = new CustomDialog_Notify(mContext);
        notify.show();
        notify.getTitle().setText(title);
        notify.getMsg().setText(msg);
        notify.getBtn_Cancel().setText(cancle);
        notify.getBtn_Cancel().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notify.dismiss();
            }
        });
        notify.getBtn_Confirm().setText(confirm);
        return notify.getBtn_Confirm();
    }
//
//
//    /**
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	mContext : 호출한 액티비티
//     * @param	title	: 타이틀
//     * @param	msg		: 알림 메시지
//     * @param	confirm	: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static ArrayList<Button> Notifing(Context mContext, int title, int msg, int cancel, int skip, int confirm) {
//        notifing = new CustomDialog_Notifing(mContext);
//        notifing.show();
//        notifing.getTitle().setText(title);
//        notifing.getMsg().setText(msg);
//        notifing.getBtn_Cancel().setText(cancel);
//        notifing.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notifing.dismiss();
//            }
//        });
//        notifing.getBtn().setText(skip);
//        notifing.getBtn_Confirm().setText(confirm);
//
//        ArrayList<Button> buttons = new ArrayList<Button>();
//        buttons.add(notifing.getBtn());
//        buttons.add(notifing.getBtn_Confirm());
//        return buttons;
//    }
//
//
//
//    /**
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @param	int		: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static Button Edit(Context mContext, int title, int msg) {
//        edit = new CustomDialog_Sharing(mContext);
//        edit.show();
//        edit.getTitle().setText(title);
//        edit.getMsg().setText(msg);
//        edit.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edit.dismiss();
//            }
//        });
//        return edit.getBtn_Confirm();
//    }
//
//    /**
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @param	int		: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static Button Edit(Context mContext, String title, int msg) {
//        edit = new CustomDialog_Sharing(mContext);
//        edit.show();
//        edit.getTitle().setText(title);
//        edit.getMsg().setText(msg);
//        edit.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edit.dismiss();
//            }
//        });
//        return edit.getBtn_Confirm();
//    }
//
//
//    /**
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @param	int		: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static Button Edit2(Context mContext, int title, int msg) {
//        edit2 = new CustomDialog_Sharing2(mContext);
//        edit2.show();
//        edit2.getTitle().setText(title);
//        edit2.getMsg().setText(msg);
//        edit2.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edit2.dismiss();
//            }
//        });
//        return edit2.getBtn_Confirm();
//    }
//
//    /**
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @param	int		: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static Button Edit2(Context mContext, String title, int msg) {
//        edit2 = new CustomDialog_Sharing2(mContext);
//        edit2.show();
//        edit2.getTitle().setText(title);
//        edit2.getMsg().setText(msg);
//        edit2.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edit2.dismiss();
//            }
//        });
//        return edit2.getBtn_Confirm();
//    }
//
//    /**
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @param	int		: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static Button Key(Context mContext, int title, int msg) {
//        key = new CustomDialog_Key(mContext);
//        key.show();
//        key.getTitle().setText(title);
//        key.getMsg().setText(msg);
//        key.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                key.dismiss();
//            }
//        });
//        return key.getBtn_Confirm();
//    }
//
//    /**
//     * 알림창 확인 버튼을 반환한다.
//     * @param 	Context : 호출한 액티비티
//     * @param	String	: 알림 메시지
//     * @param	int		: 버튼 이름
//     * @throws 	None
//     * @return 	Button	: 확인 버튼
//     */
//    public static Button Key(Context mContext, String title, int msg) {
//        key = new CustomDialog_Key(mContext);
//        key.show();
//        key.getTitle().setText(title);
//        key.getMsg().setText(msg);
//        key.getBtn_Cancel().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                key.dismiss();
//            }
//        });
//        return key.getBtn_Confirm();
//    }
//
//
    /**
     * 웹뷰 알림창 확인 버튼을 반환한다.
     * @param 	Context : 호출한 액티비티
     * @param	title	: 타이틀
     * @param	uri		: 주소
     * @throws 	None
     * @return 	None
     */
    public static void WebView(Context mContext, String title, String uri) {
        webview = new CustomDialog_Webview(mContext);
        webview.show();
        webview.getTitle().setText(title);
        webview.getBtn().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.dismiss();
            }
        });
//
        // 웹뷰 보여주기
        wv = webview.getWv();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            WebView.setWebContentsDebuggingEnabled(false);
        } else {
            // 기능을 지원하지 않으므로 알림X
        }
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setJavaScriptEnabled(true);  	// 웹뷰에서 자바 스크립트 사용
        wv.loadUrl(uri);					   			// 웹뷰에서 불러올 URL 입력
        wv.setWebViewClient(PopupUtils.newInstance(mContext).new WishWebViewClient());
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "개인정보방침 팝업 보기");
    }
//
    private class WishWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }
    }
}
