package sleep.simdori.com.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.activity.LoginActivity;
import sleep.simdori.com.asynctask.Get_BPMAsynctask;
import sleep.simdori.com.asynctask.Set_IDAsyncTask;
import sleep.simdori.com.util.SharedPrefUtil;

public class BPMResultFragment extends Fragment implements View.OnClickListener {

    // API
    private SharedPrefUtil pref = null;
    private AsyncTask<String, Void, Integer> mAsyncTask_SelectBPM = null;

    // View
    TextView userNameText;
    Activity mActivity;

    int idN = 0;
    int bpmN = 1;
    int dateTimeN = 2;

    // Values
    int count = 0;
    String bpm = "78";
    String[] bpmResult;
    String userName = "vitarbest";
    String state = "걷기";
    String date = "2021년 02월 10일 10:38";

    // Dynamic View
    LayoutInflater mInflater;

    public static sleep.simdori.com.fragment.BPMResultFragment newInstance() {
        return new sleep.simdori.com.fragment.BPMResultFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bpmresult, container, false);

        mActivity = getActivity();

        userNameText = (TextView) v.findViewById(R.id.bpmresultusername);

        // API
        pref = new SharedPrefUtil(mActivity);
        userName = pref.getValue(SharedPrefUtil.USER_ID, "");
        userNameText.setText(userName);

        selectBPM(getContext(), v, userName);

        bpm = pref.getValue("bpmResult", "");
        System.out.println(bpm);
        bpmResult = bpm.split(",");

        if(bpmResult.length >= 3){
            for(int i = 0; i < bpmResult.length; i = i + 4){
                createView(getContext(), v, bpmResult[i], bpmResult[i + 1], userName, bpmResult[i + 3], bpmResult[i + 2]);
            }
        }

        return v;
    }

    @Override
    public void onClick(View v) {

    }

    public void selectBPM(Context context, View v, String id){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_SelectBPM = new Get_BPMAsynctask(getContext(), id).execute();
        } else {
            mAsyncTask_SelectBPM = new Get_BPMAsynctask(getContext(), id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    // 서버 DB에 저장된 데이터를 가져와 동적으로 View를 생성하여 보여주는 함수
    public void createView(Context context, View v, String bpmId, String bpm, String name, String state, String date){

        LinearLayout mRootLinear = (LinearLayout) v.findViewById(R.id.bpmresultlayout);

        LinearLayout BPMRootLayout = new LinearLayout((context.getApplicationContext()));
        LinearLayout BPMElementLayout = new LinearLayout((context.getApplicationContext()));
        LinearLayout userInfoLayout = new LinearLayout((context.getApplicationContext()));
        BPMRootLayout.setOrientation(LinearLayout.VERTICAL);
        BPMRootLayout.setId(Integer.parseInt(bpmId));
        BPMRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.put("bpmId", bpmId);
                ((HomeActivity)getActivity()).replaceFragment(BPMDetailFragment.newInstance());
            }
        });
        BPMRootLayout.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.border));
        BPMRootLayout.setPadding(10, 20, 10, 20);
        BPMRootLayout.setGravity(Gravity.CENTER);
        BPMElementLayout.setOrientation(LinearLayout.HORIZONTAL);
        BPMElementLayout.setGravity(Gravity.CENTER);
        userInfoLayout.setGravity(Gravity.END);
        userInfoLayout.setOrientation(LinearLayout.VERTICAL);

        TextView bpmValue = new TextView((context.getApplicationContext()));
        TextView bpmstr = new TextView((context.getApplicationContext()));
        ImageView userImage = new ImageView((context.getApplicationContext()));
        TextView userName = new TextView((context.getApplicationContext()));
        TextView userState = new TextView((context.getApplicationContext()));
        TextView dateView = new TextView((context.getApplicationContext()));

        bpmValue.setText(bpm);
        bpmValue.setTextSize(45);
        bpmstr.setText("bpm");
        bpmstr.setTextSize(23);

        // state에 따라서 그림 다르게 해야함
        if(state.equals("걷기")){
            userImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.walk));

        }else if(state.equals("휴식")){

        }else if(state.equals("이완")){
            userImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.relaxation_black));
        }else if(state.equals("뛰기")){

        }else if(state.equals("과격한 운동")){

        }

        userName.setText(name);
        userName.setTextSize(20);
        userState.setText(state);
        userState.setTextSize(20);
        dateView.setText(date);
        dateView.setTextSize(10);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        param.leftMargin = 40;

        bpmValue.setLayoutParams(param);
        bpmValue.setTextColor(getResources().getColor(R.color.bpmcolor));
        bpmstr.setLayoutParams(param);
        bpmstr.setGravity(Gravity.BOTTOM);
        bpmstr.setTextColor(getResources().getColor(R.color.bpmcolor));
        userImage.setLayoutParams(param);
        userName.setLayoutParams(param);
        userName.setTextColor(Color.WHITE);
        userState.setLayoutParams(param);
        userState.setTextColor(Color.WHITE);
        dateView.setLayoutParams(param);
        dateView.setTextColor(Color.WHITE);

        userInfoLayout.addView(userName);
        userInfoLayout.addView(userState);
        userInfoLayout.addView(dateView);

        BPMElementLayout.addView(bpmValue);
        BPMElementLayout.addView(bpmstr);
        BPMElementLayout.addView(userImage);
        BPMElementLayout.addView(userInfoLayout);

        BPMRootLayout.addView(BPMElementLayout);

        mRootLinear.addView(BPMRootLayout);

    }

}
