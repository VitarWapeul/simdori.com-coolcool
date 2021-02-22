package sleep.simdori.com.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import sleep.simdori.com.R;
import sleep.simdori.com.activity.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link sleep.simdori.com.fragment.ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment implements View.OnClickListener {

    // View
    Button finishButton;

    int HR; // HeartRate

    public static sleep.simdori.com.fragment.ResultFragment newInstance() {
        return new sleep.simdori.com.fragment.ResultFragment();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static sleep.simdori.com.fragment.ResultFragment newInstance(String param1, String param2) {
        sleep.simdori.com.fragment.ResultFragment fragment = new sleep.simdori.com.fragment.ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_result, container, false);

        finishButton = (Button) v.findViewById(R.id.resultfinishbutton);
        finishButton.setOnClickListener(this);
        TextView RHR = (TextView) v.findViewById(R.id.HRR);


        //Bundle bundle = getActivity().getIntent().getExtras();
        Bundle bundle = this.getArguments();


        if (bundle != null) {
            HR = bundle.getInt("bpm");
            RHR.setText(String.valueOf(HR));

        }

        ImageButton imgprofileButton = v.findViewById(R.id.imgprofilebtn);

        imgprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).replaceFragment(SettingFragment.newInstance());
            }
        });


        return v;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.resultfinishbutton:
            {
                ((HomeActivity)getActivity()).replaceFragment(HomeFragment.newInstance());
                break;
            }
        }
    }
}