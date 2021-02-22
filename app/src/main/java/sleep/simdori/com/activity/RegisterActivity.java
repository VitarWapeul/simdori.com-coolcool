package sleep.simdori.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.AppUI;
import sleep.simdori.com.R;
import sleep.simdori.com.asynctask.Set_RegisterAsyncTask;
import sleep.simdori.com.util.PopupUtils;
import sleep.simdori.com.util.SharedPrefUtil;
import sleep.simdori.com.util.ToastUtils;

/**
 * 사용자 아이디, 비밀번호, 이메일을 입력받아 회원가입을 진행한다.
 * 회원가입약관, 개인정보취급방침을 동의를 체크하여야 진행이 된다.
 * @version 2.00 03/08/16
 * @author 이선호
 * @see sleep.simdori.com.activity
 * @since Android 5.1
 */
public class RegisterActivity extends Activity {
    // API
    private SharedPrefUtil pref = null;
    private Pattern pattern_id, pattern_email, pattern_phone = null;
    private Matcher m_id, m_email, m_phone = null;
    private Locale locale = null;
    private Intent intent = null;
    private AsyncTask<String, Void, Integer> mAsyncTask_Register = null;

    // Values
    private EditText register_id_input, register_mail_input, register_phone_input, register_pw_input, register_pw_inputcheck;
    private String language, register_webview_uri, id, pw, pw2, email, phone = "";
    private boolean register_areements_view, register_personal_view = false; //나중에 false로 바꿔야함

    // Views
    private Activity mActivity;
    private ProgressBar pb;
    private RelativeLayout touch_layout, top_back;
    private LinearLayout register_id, register_email, register_phone, register_pw, register_pwcheck;
    private CheckBox register_all_cb, register_areements_cb, register_personal_cb;
    private Button register_areements_btn, register_personal_btn;
    private Button register_btn_confirm, register_btn_cancel;
    private Button btn_dialog;

    @Override
    protected void onResume() {
        super.onResume();
        AppUI.setRegisterActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUI.setRegisterActivity(null);
    }

    @Override
    protected void onDestroy() {
        // 실행중인 팝업 닫기
        if(PopupUtils.getNotity()!=null) PopupUtils.getNotity().dismiss();
        if(pb!=null) pb.setVisibility(View.GONE);

        // 실행되고 있는 AsyncTask 종료
        if(mAsyncTask_Register != null) mAsyncTask_Register.cancel(true);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // 메인 화면으로 이동
        intent = new Intent(sleep.simdori.com.activity.RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppConst.INTENT_REGISTER:
                try {
                    register_id_input.setText(data.getStringExtra(SharedPrefUtil.USER_ID));
                    register_mail_input.setText(data.getStringExtra(SharedPrefUtil.USER_EMAIL));
                    register_phone_input.setText(data.getStringExtra(SharedPrefUtil.USER_PHONE));
                    register_pw_input.setText(data.getStringExtra(SharedPrefUtil.USER_PASSWORD));
                    register_pw_inputcheck.setText(data.getStringExtra(SharedPrefUtil.USER_PW_CHECK));
                } catch (Exception e) {
                    // 뷰와 관련되어 알림X
                    e.printStackTrace();
                }
                break;

            default:
                // 처리할 내용이 없어 알림X
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mActivity = this;

        // 현재언어 가져오기
        pref = new SharedPrefUtil(this);
        locale = getResources().getConfiguration().locale;
        language = locale.getLanguage();
        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "RegisterActivity - 회원가입 현재언어는 " + language);

        initViews();
        eventListener();
    }

    /**
     * 뷰 생성 및 초기화
     */
    private void initViews() {
        // 진행바
        pb = (ProgressBar) findViewById(R.id.pb);
        // 이전화면
        top_back = (RelativeLayout) findViewById(R.id.top_back);
        // 키보드 감추기
        touch_layout = (RelativeLayout) findViewById(R.id.touch_layout);

        register_id = (LinearLayout) findViewById(R.id.register_id);
        register_email = (LinearLayout) findViewById(R.id.register_mail);
        register_phone = (LinearLayout) findViewById(R.id.register_phone);
        register_pw = (LinearLayout) findViewById(R.id.register_pw);
        register_pwcheck = (LinearLayout) findViewById(R.id.register_pw_check);

        // ID 입력
        register_id_input = (EditText) findViewById(R.id.register_id_input);
        // Email 입력
        register_mail_input = (EditText) findViewById(R.id.register_mail_input);
        // 전화번호 입력
        register_phone_input = (EditText) findViewById(R.id.register_phone_input);
        // 비밀번호 확인
        register_pw_input = (EditText) findViewById(R.id.register_pw_input);
        register_pw_inputcheck = (EditText) findViewById(R.id.register_pw_inputcheck);

        // 약관 모두 동의
        register_all_cb = (CheckBox) findViewById(R.id.register_all_cb);
        // 약관동의 체크
        register_areements_cb = (CheckBox) findViewById(R.id.register_agreements_cb);
        // 약관보기 버튼
        register_areements_btn = (Button) findViewById(R.id.register_agreements_btn);
        // 개인정보취급방침동의 체크
        register_personal_cb = (CheckBox) findViewById(R.id.register_personal_cb);
        // 개인정보취급방침보기 버튼
        register_personal_btn = (Button) findViewById(R.id.register_personal_btn);

        // 취소
        register_btn_cancel = (Button) findViewById(R.id.register_btn_cancel);
        // 확인
        register_btn_confirm = (Button) findViewById(R.id.register_btn_confirm);
    }

    /**
     * 이벤트 생성
     */
    public void eventListener() {
        // 이전화면
        top_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 키보드 감추기
        touch_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(touch_layout.getWindowToken(), 0);
            }
        });

        // 아이디 입력 시 포커스 주기
        register_id_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    register_id_input.setHint(null);
                    register_mail_input.setHint(R.string.Required_Field);
                    register_phone_input.setHint(R.string.Optional_Field);
                    register_pw_input.setHint(R.string.Required_Field);
                    register_pw_inputcheck.setHint(R.string.Required_Field);
                    register_id.setBackgroundResource(R.drawable.round_edit_white3);
                    register_email.setBackgroundResource(R.drawable.round_view_white3);
                    register_phone.setBackgroundResource(R.drawable.round_view_white3);
                    register_pw.setBackgroundResource(R.drawable.round_view_white3);
                    register_pwcheck.setBackgroundResource(R.drawable.round_view_white3);
                }
            }
        });

        // 이메일 입력 시 포커스 주기
        register_mail_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    register_id_input.setHint(R.string.Required_Field);
                    register_mail_input.setHint(null);
                    register_phone_input.setHint(R.string.Optional_Field);
                    register_pw_input.setHint(R.string.Required_Field);
                    register_pw_inputcheck.setHint(R.string.Required_Field);
                    register_id.setBackgroundResource(R.drawable.round_view_white3);
                    register_email.setBackgroundResource(R.drawable.round_edit_white3);
                    register_phone.setBackgroundResource(R.drawable.round_view_white3);
                    register_pw.setBackgroundResource(R.drawable.round_view_white3);
                    register_pwcheck.setBackgroundResource(R.drawable.round_view_white3);
                }
            }
        });

        // 전화번호 입력 시 포커스 주기
        register_phone_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    register_id_input.setHint(R.string.Required_Field);
                    register_mail_input.setHint(R.string.Required_Field);
                    register_phone_input.setHint(null);
                    register_pw_input.setHint(R.string.Required_Field);
                    register_pw_inputcheck.setHint(R.string.Required_Field);
                    register_id.setBackgroundResource(R.drawable.round_view_white3);
                    register_email.setBackgroundResource(R.drawable.round_view_white3);
                    register_phone.setBackgroundResource(R.drawable.round_edit_white3);
                    register_pw.setBackgroundResource(R.drawable.round_view_white3);
                    register_pwcheck.setBackgroundResource(R.drawable.round_view_white3);
                }
            }
        });

        // 비밀번호 입력 시 포커스 주기
        register_pw_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    register_id_input.setHint(R.string.Required_Field);
                    register_mail_input.setHint(R.string.Required_Field);
                    register_phone_input.setHint(R.string.Optional_Field);
                    register_pw_input.setHint(null);
                    register_pw_inputcheck.setHint(R.string.Required_Field);
                    register_id.setBackgroundResource(R.drawable.round_view_white3);
                    register_email.setBackgroundResource(R.drawable.round_view_white3);
                    register_phone.setBackgroundResource(R.drawable.round_view_white3);
                    register_pw.setBackgroundResource(R.drawable.round_edit_white3);
                    register_pwcheck.setBackgroundResource(R.drawable.round_view_white3);
                }
            }
        });

        // 비밀번호 재입력 시 포커스 주기
        register_pw_inputcheck.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    register_id_input.setHint(R.string.Required_Field);
                    register_mail_input.setHint(R.string.Required_Field);
                    register_phone_input.setHint(R.string.Optional_Field);
                    register_pw_input.setHint(R.string.Required_Field);
                    register_pw_inputcheck.setHint(null);
                    register_id.setBackgroundResource(R.drawable.round_view_white3);
                    register_email.setBackgroundResource(R.drawable.round_view_white3);
                    register_phone.setBackgroundResource(R.drawable.round_view_white3);
                    register_pw.setBackgroundResource(R.drawable.round_view_white3);
                    register_pwcheck.setBackgroundResource(R.drawable.round_edit_white3);
                }
            }
        });

        // 약관 모두 동의
        register_all_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    register_areements_cb.setChecked(true);
                    register_personal_cb.setChecked(true);
                } else {
                    register_areements_cb.setChecked(false);
                    register_personal_cb.setChecked(false);
                }
            }
        });


        // 회원가입약관 보기
        register_areements_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입약관 주소
                if (language.equals("ko")) {
                    register_webview_uri = AppConst.Agreements_host;
                } else if (language.equals("en")) {
                    register_webview_uri = AppConst.Agreements_en_host;
                } else if (language.equals("zh")) {
                    register_webview_uri = AppConst.Agreements_zh_host;
                } else {
                    // 정보를 처리하지 못했습니다.
                    ToastUtils.ToastShow(mActivity, R.string.dialog_Luci_FAIL);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "RegisterActivity - 회원가입약관 처리 오류");
                    return;
                }
                register_areements_view = true;
                PopupUtils.WebView(mActivity, getString(R.string.settings_terms), register_webview_uri);
            }
        });

        // 개인정보방침 보기
        register_personal_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 개인정보방침 주소
                if (language.equals("ko")) {
                    register_webview_uri = AppConst.Personal_host;
                } else if (language.equals("en")) {
                    register_webview_uri = AppConst.Personal_en_host;
                } else if (language.equals("zh")) {
                    register_webview_uri = AppConst.Personal_zh_host;
                } else {
                    // 정보를 처리하지 못했습니다.
                    ToastUtils.ToastShow(mActivity, R.string.dialog_Luci_FAIL);
                    if(AppConst.DEBUG_ALL) Log.w(AppConst.TAG, "RegisterActivity - 개인정보방침 처리 오류");
                    return;
                }
                register_personal_view = true;
                PopupUtils.WebView(mActivity, getString(R.string.settings_personal), register_webview_uri);
            }
        });

        // 회원가입
        register_btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 회원정보 가져오기
                id = register_id_input.getText().toString().trim();
                pw = register_pw_input.getText().toString().trim();
                pw2 = register_pw_inputcheck.getText().toString().trim();
                email = register_mail_input.getText().toString().trim();
                phone = register_phone_input.getText().toString().trim();
                if(AppConst.DEBUG_ALL) Log.i(AppConst.TAG, "회원가입 input : ID= " + id + ", PW= " + pw + ", Email= " + email  + ", Phone= " + phone);

                // 영문자, 숫자만 입력 가능
                pattern_id  = Pattern.compile("^[a-zA-Z0-9]*$");
                m_id = pattern_id.matcher(id);
                // 이메일 형식만 입력 가능
                pattern_email = Pattern.compile("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
                m_email = pattern_email.matcher(email);
                // 전화번호 형식만 입력 가능
                pattern_phone = Pattern.compile("^s|^([0-9]{2,3})-?([0-9]{3,4})-?([0-9]{4})$"); //("^\d{3}-\d{3,4}-\d{4}$");
                m_phone = pattern_phone.matcher(phone);


                // 아이디가 입력되지 않은 경우
                if (id.equals("")) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_id_null));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_id_input.requestFocus();
                        }
                    });

                    // 슈퍼 아이디가 입력된 경우
                } else if (id.equals(AppConst.Super_ID)) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.not_find_id));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_id_input.requestFocus();
                        }
                    });

                    // 특수 문자를 입력한 경우
                } else if (!m_id.find()) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_id_not_form));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_id_input.requestFocus();
                        }
                    });

                    // 이메일 입력되지 않은 경우
                } else if (email.equals("")) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_email_null));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_mail_input.requestFocus();
                        }
                    });

                    // 이메일 형식이 아닌 경우
                } else if (!m_email.find()) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_email_not_form));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_mail_input.requestFocus();
                        }
                    });

                    // 핸드폰 형식이 아닌 경우
                } else if (!phone.equals("") && !m_phone.find()) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_phone_null));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_phone_input.requestFocus();
                        }
                    });

                    // 비밀번호가 입력되지 않은 경우
                } else if (pw.equals("")) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_pw_null));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_pw_input.requestFocus();
                        }
                    });

                    // 비밀번호를 재입력하지 않은 경우
                } else if (pw2.equals("")) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_pw_check));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_pw_inputcheck.requestFocus();
                        }
                    });

                    // 비밀번호가 서로 다른 경우
                } else if (!pw.equals(pw2)) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.toast_pw_not_same));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_pw_inputcheck.requestFocus();
                        }
                    });

                    // 회원가입 약관을 보지 않은 경우
                } else if (!register_areements_view) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.register_terms_agreements_View));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_areements_cb.requestFocus();
                        }
                    });

                    // 회원가입 약관에 동의하지 않은 경우
                } else if (!register_areements_cb.isChecked()) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.register_terms_agreements_Null));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_areements_cb.requestFocus();
                        }
                    });

                    // 회원가입 약관을 보지 않은 경우
                } else if (!register_personal_view) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.register_terms_personal_View));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_personal_cb.requestFocus();
                        }
                    });

                    // 개인정보취급방침에 동의하지 않은 경우
                } else if (!register_personal_cb.isChecked()) {
                    btn_dialog = PopupUtils.Notify_Exception_Btn(mActivity, getString(R.string.register_terms_personal_Null));
                    btn_dialog.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupUtils.getNotify_Exception().dismiss();
                            register_personal_cb.requestFocus();
                        }
                    });

//                    // Wi-Fi 연결
//                } else if (!Network.getWIFIStatus(mActivity)) {
//                    pref.put(SharedPrefUtil.WIFI_CONNECTION, false);
//                    connection_Popup();

                    // 회원가입 연결
                } else {
                    register(id, email, pw, phone);
                }
            }
        });

        // 회원가입 취소
        register_btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

//    /**
//     * WIFI 연결
//     * @param None
//     * @throws None
//     * @return None
//     */
//    public void connection_Popup() {
//        // 1. Wi-Fi 연결이 필요합니다. 설정 페이지로 이동하시겠습니까?
//        btn_dialog = PopupUtils.Notify(mActivity, R.string.msg_wifi_intent, R.string.Btn_wifi);
//        if(AppConst.DEBUG_ALL) Log.d(AppConst.TAG, "ReigsterActivity - Connection_Popup() / 인터넷 연결 오류");
//
//        // 2. Wi-Fi 연결
//        btn_dialog.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 2-1. 알림창 닫기
//                PopupUtils.getNotity().dismiss();
//
//                // 2-2. Wi-Fi 설정페이지로 이동
//                intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });
//    }

    /**
     * 회원가입 연결
     * @param id	: 사용자 입력 아이디
     * @param email : 사용자 입력 이메일
     * @param pw	: 사용자 입력 비밀번호
     * @throws None
     * @return None
     */
    public void register(String id, String email, String pw, String phone) {
        if (Build.VERSION.SDK_INT < AppConst.HONEYCOMB) {
            mAsyncTask_Register = new Set_RegisterAsyncTask(mActivity, pb, id, email, pw, phone).execute();
        } else {
            mAsyncTask_Register = new Set_RegisterAsyncTask(mActivity, pb, id, email, pw, phone).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
