package sleep.simdori.com.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.data.OAuthLoginState;

import sleep.simdori.com.AppConst;
import sleep.simdori.com.activity.IotManageActivity;
import sleep.simdori.com.activity.LoginActivity;
import sleep.simdori.com.R;
import sleep.simdori.com.activity.ModifyActivity;
import sleep.simdori.com.asynctask.Set_LogoutAsyncTask;
import sleep.simdori.com.util.PopupUtils;
import sleep.simdori.com.util.SharedPrefUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String language = "";
    private String setting_webview_uri = "";

    // API
    private SharedPrefUtil pref = null;

    // View
    TextView userName;
    TextView userEmail;
    Button logOutButton;
    Button modifyUserButton;
    Button QnA;
    Button set;
    Button inquiry;
    Button help;
    Button IotManageButton;

    private AsyncTask<String, Void, Integer> mAsyncTask_Logout;

    //naver
    OAuthLogin mOAuthLoginModule;
    Context mContext;

    //google

    // Values
    private String loginId = "";
    private String loginEmail = "";


    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        pref = new SharedPrefUtil(getActivity());

    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        loginId = pref.getValue(SharedPrefUtil.USER_ID, loginId);
        loginEmail = pref.getValue(SharedPrefUtil.USER_EMAIL, loginEmail);

        logOutButton = (Button) v.findViewById(R.id.logOutButton);
        modifyUserButton = (Button) v.findViewById(R.id.userAccountManagementButton);
        QnA = (Button) v.findViewById(R.id.QnA);
        set = (Button) v.findViewById(R.id.set);
        inquiry = (Button) v.findViewById(R.id.inquiry);
        help = (Button) v.findViewById(R.id.help);
        IotManageButton = (Button) v.findViewById(R.id.IotManageButton);

        logOutButton.setOnClickListener(this);
        modifyUserButton.setOnClickListener(this);
        QnA.setOnClickListener(this);
        set.setOnClickListener(this);
        inquiry.setOnClickListener(this);
        help.setOnClickListener(this);
        IotManageButton.setOnClickListener(this);


        userName = (TextView) v.findViewById(R.id.settingUserName);
        userEmail = (TextView) v.findViewById(R.id.settingUserEmail);

        mContext = getActivity().getApplicationContext();

        modifyUserButton = v.findViewById(R.id.userAccountManagementButton);

//        if(GoogleSignIn.getLastSignedInAccount(mContext) != null){
        if(false){
            //구글 회원정보 가져오기
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mContext);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();

                newInstance(personName);

                userName.setText(personName);
                userEmail.setText(personEmail);

                modifyUserButton.setVisibility(View.GONE);
            }
        }else if(false){
//        }else if(OAuthLoginState.OK.equals(OAuthLogin.getInstance().getState(mContext))){
            //네이버 회원정보 가져오기
            modifyUserButton.setVisibility(View.GONE);
        }else{
            //sqlite에서 정보 가져와서 보여주기

            userName.setText(loginId);
            userEmail.setText(loginEmail);

            modifyUserButton.setVisibility(View.VISIBLE);
        }

        return v;


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.logOutButton:
            {

//                if(/*googlelogin*/GoogleSignIn.getLastSignedInAccount(mContext) != null){
                if(false){
                    googleSignOut(mContext);

                    Toast.makeText(mContext, "구글 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
//                else if(OAuthLogin.getInstance() != null){
//                    mOAuthLoginModule = OAuthLogin.getInstance();
//                    mOAuthLoginModule.init(mContext ,getString(R.string.naver_client_id) ,getString(R.string.naver_client_secret) ,getString(R.string.naver_client_name));
//                    mOAuthLoginModule.logout(mContext);
//
//                    Toast.makeText(mContext, "네이버 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
//                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
//                break;
//                }
                else if(/*kakaologin*/false){
                    break;
                }else{
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        mAsyncTask_Logout = new Set_LogoutAsyncTask(getActivity(), loginId, loginEmail).execute();
                    } else {
                        mAsyncTask_Logout = new Set_LogoutAsyncTask(getActivity(), loginId, loginEmail).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));

                    // 2. 로그인 정보 삭제
                    pref.put(SharedPrefUtil.USER_ID, "");
                    pref.put(SharedPrefUtil.USER_PASSWORD, "");
                    pref.put(SharedPrefUtil.USER_EMAIL, "");
                    pref.put(SharedPrefUtil.USER_PHONE, "");
                    pref.put(SharedPrefUtil.LOGIN_STATUS, 0);
                    break;
                }

            }

            case R.id.userAccountManagementButton:
            {
                getActivity().startActivity(new Intent(getActivity(), ModifyActivity.class));
                break;
            }
            case R.id.IotManageButton:
            {
                getActivity().startActivity(new Intent(getActivity(), IotManageActivity.class));
                break;
            }
            case R.id.QnA:
            {
                // QnA
                setting_webview_uri = AppConst.QnA_host;
                PopupUtils.WebView(v.getContext(), "자주 묻는 질문", setting_webview_uri);
                break;
            }

            case R.id.inquiry:
            {
                //inquiry
                setting_webview_uri = AppConst.inquiry_host;
                PopupUtils.WebView(v.getContext(), "문의", setting_webview_uri);
                break;
            }

            case R.id.help:
            {
                //help
                setting_webview_uri = AppConst.help_host;
                PopupUtils.WebView(v.getContext(), "도움말", setting_webview_uri);
                break;
            }

        }

    }

    public void googleSignOut(Context context) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

}