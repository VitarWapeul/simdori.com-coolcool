package sleep.simdori.com.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;
import sleep.simdori.com.asynctask.Get_BPMDetailAsynctask;
import sleep.simdori.com.util.SharedPrefUtil;

public class BPMDetailFragment extends Fragment implements View.OnClickListener {

    // API
    private SharedPrefUtil pref = null;
    private AsyncTask<String, Void, Integer> mAsyncTask_SelectBPMDetail = null;

    // View
    Activity mActivity;

    TextView date;
    TextView topUserName;
    TextView userName;
    TextView userState;
    TextView bpm;
    TextView stress;
    TextView Arrhythmia;
    TextView tachycardia;
    TextView bloodoxygen;
    TextView diagnosis;
    ImageView bradycardia;
    ImageView stateImg;
    Button backButton;

    // Values
    String bpmDetail;
    String[] bpmDetailArr;
    String bpmId;
    String userNameValue;
    String dateValue = "2021-02-16 08:46:12";
    String userStateValue = "걷기";
    String bloodoxygenValue;
    String ArrhythmiaValue;
    String diagnosisValue;
    int bpmValue;
    String stressValue = "98";

    @Override
    public void onStart() {
        super.onStart();
        mActivity = getActivity();
        // API
        pref = new SharedPrefUtil(mActivity);
        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
        bpmId = pref.getValue("bpmId", "");
        selectBPMDetail(userNameValue, bpmId);
        setText();
    }

    public static sleep.simdori.com.fragment.BPMDetailFragment newInstance() {
        return new sleep.simdori.com.fragment.BPMDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bpmdetail, container, false);

        mActivity = getActivity();

        date = (TextView) v.findViewById(R.id.bpmdetaildate);
        topUserName = (TextView) v.findViewById(R.id.bpmdetailtopusername);
        userName = (TextView) v.findViewById(R.id.bpmdetailusername);
        userState = (TextView) v.findViewById(R.id.bpmdetailstate);
        bpm = (TextView) v.findViewById(R.id.bpmdetailbpmvalue);
        stress = (TextView) v.findViewById(R.id.bpmstress);
        Arrhythmia = (TextView) v.findViewById(R.id.Arrhythmia);
        tachycardia = (TextView) v.findViewById(R.id.tachycardia);
        bloodoxygen = (TextView) v.findViewById(R.id.bloodoxygen);
        diagnosis = (TextView) v.findViewById(R.id.diagnosis);
        stateImg = (ImageView) v.findViewById(R.id.bpmdetailstateimg);
        bradycardia = (ImageView) v.findViewById(R.id.bradycardia);

        setText();

        backButton = (Button) v.findViewById(R.id.bpmdetailbackbutton);
        backButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bpmdetailbackbutton:
            {
                ((HomeActivity)getActivity()).replaceFragment(BPMResultFragment.newInstance());
                break;
            }
        }
    }

    public void selectBPMDetail(String id, String no){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mAsyncTask_SelectBPMDetail = new Get_BPMDetailAsynctask(getContext(), id, no).execute();
        } else {
            mAsyncTask_SelectBPMDetail = new Get_BPMDetailAsynctask(getContext(), id, no).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void setText(){

        mActivity = getActivity();
        // API
        pref = new SharedPrefUtil(mActivity);

        bpmDetail = pref.getValue("bpmDetail", "");
        bpmDetailArr = bpmDetail.split(",");

        userNameValue = pref.getValue(SharedPrefUtil.USER_ID, "");
        bpmValue = Integer.parseInt(bpmDetailArr[0]);
        stressValue = bpmDetailArr[3];
        dateValue = bpmDetailArr[1];
        userStateValue = bpmDetailArr[2];
        bloodoxygenValue = bpmDetailArr[4];
        ArrhythmiaValue = bpmDetailArr[5];
        diagnosisValue = bpmDetailArr[6];

        userName.setText(userNameValue);
        topUserName.setText(userNameValue);
        bpm.setText(String.valueOf(bpmValue));
        date.setText(dateValue);
        userState.setText(userStateValue);
        stress.setText(stressValue);
        bloodoxygen.setText(bloodoxygenValue);
        Arrhythmia.setText(ArrhythmiaValue);
        diagnosis.setText(diagnosisValue);

        // state에 따라서 그림 다르게 해야함
        if(userStateValue.equals("걷기")){
            stateImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.walk));

        }else if(userStateValue.equals("휴식")){

        }else if(userStateValue.equals("이완")){
            stateImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.relaxation_black));
        }else if(userStateValue.equals("뛰기")){

        }else if(userStateValue.equals("과격한 운동")){

        }

        if(bpmValue <= 60){
            bradycardia.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bad));
        }else{
            bradycardia.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.smile));
        }
        if(bpmValue >= 95){
            tachycardia.setText("빈맥");
            tachycardia.setTextColor(Color.RED);
        }else{
            tachycardia.setText("정상");
            tachycardia.setTextColor(Color.GREEN);
        }
        if(bloodoxygenValue.equals("정상")){
            bloodoxygen.setTextColor(Color.GREEN);
        }else{
            bloodoxygen.setTextColor(Color.RED);
        }
        if(ArrhythmiaValue.equals("정상")){
            Arrhythmia.setTextColor(Color.GREEN);
        }else{
            Arrhythmia.setTextColor(Color.RED);
        }
    }
}
