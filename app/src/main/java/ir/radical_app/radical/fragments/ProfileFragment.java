package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;

import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.dialogs.ColorChooserDialog;
import ir.radical_app.radical.listeners.ColorListener;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private SimpleDraweeView avatar;
    private SwitchCompat genderSwitch;
    private String gender = "male";
    private EditText fName,fEmail,fInviteCode;
    private MaterialButton save,fColor;
    private TextView fPlan,fExp,fNumber;
    private Spinner fJob,fEdu,fRegion;
    private View selectedColor;
    private int isFirst=0,color=0;
    private LoadingDialog dialog;
    private MyDatabase myDatabase;


    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);

        init(v);
        return v;
    }

    private void loadingDialog(boolean cancel){
        if(!cancel){
            dialog = new LoadingDialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.show();
        }else{
            dialog.dismiss();
        }
    }


    private void init(View v){
        fName = v.findViewById(R.id.profile_name);
        fNumber = v.findViewById(R.id.profile_phone);
        fInviteCode = v.findViewById(R.id.profile_invitecode);
        fEmail = v.findViewById(R.id.profile_email);
        fEmail = v.findViewById(R.id.profile_email);
        fPlan = v.findViewById(R.id.profile_plan);
        fExp = v.findViewById(R.id.profile_exp);
        fJob = v.findViewById(R.id.profile_job);
        fEdu = v.findViewById(R.id.profile_edu);
        fRegion = v.findViewById(R.id.profile_region);
        fColor = v.findViewById(R.id.profile_color);
        selectedColor = v.findViewById(R.id.profile_selected_color);
        save = v.findViewById(R.id.profile_save);
        avatar = v.findViewById(R.id.profile_avatar);
        genderSwitch = v.findViewById(R.id.profile_switch);
        myDatabase = new MyDatabase(getContext());
        onClicks();
        if(MyUtils.checkInternet(getContext()))
        getData();
        else{
            setVariables();
            MyToast.Create(getContext(),getString(R.string.internet_error));

        }
        fColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorChooserDialog dialog = new ColorChooserDialog(getContext());
                dialog.setTitle(R.string.profile_color_select);
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int colorId) {
                        color=colorId;
                        ShapeDrawable d = new ShapeDrawable(new OvalShape());
                        d.setBounds(50, 50, 50, 50);
                        d.getPaint().setStyle(Paint.Style.FILL);
                        d.getPaint().setColor(colorId);
                        selectedColor.setBackground(d);
                    }
                });
                dialog.show();
            }
        });
        initSpinners();
    }

    private void initSpinners(){
        ArrayAdapter<CharSequence> jobadapter = ArrayAdapter.createFromResource(getContext(),R.array.jobs,R.layout.row_spinner);
        jobadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fJob.setAdapter(jobadapter);
        fJob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> edudapter = ArrayAdapter.createFromResource(getContext(),R.array.edu,R.layout.row_spinner);
        edudapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fEdu.setAdapter(edudapter);
        fEdu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> regiondapter = ArrayAdapter.createFromResource(getContext(),R.array.region,R.layout.row_spinner);
        regiondapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fRegion.setAdapter(regiondapter);
        fRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setVariables(){
        fName.setText(MySharedPreference.getInstance(getContext()).getName());
        fNumber.setText(MySharedPreference.getInstance(getContext()).getNumber());
        fEmail.setText(MySharedPreference.getInstance(getContext()).getEmail());
        String i = MySharedPreference.getInstance(getContext()).getInviteCode();
        if(!i.isEmpty()){
            fInviteCode.setEnabled(false);
        }
        fInviteCode.setText(i);
        if(MySharedPreference.getInstance(getActivity()).getSex().equals("female")) {
            genderSwitch.setChecked(true);
            avatar.setActualImageResource(R.drawable.female);
        }

    }

    private void onClicks(){
        genderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                {
                    gender="male";
                    avatar.setActualImageResource(R.drawable.male);
                }else {
                    gender="female";
                    avatar.setActualImageResource(R.drawable.female);
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.hideKeyboard(getActivity());
                String n = fName.getText().toString();
                String e = fEmail.getText().toString();
                String i = fInviteCode.getText().toString();
                if(n.isEmpty() || n.length()<2){
                    MyToast.Create(getContext(),getString(R.string.name_error));

                }else if (e.isEmpty() || !MyUtils.isEmailValid(e)){
                    MyToast.Create(getContext(),getString(R.string.email_error));
                }else if(color==0){
                    MyToast.Create(getContext(),getString(R.string.color_error));
                }else if (!MyUtils.checkInternet(getContext())){
                    MyToast.Create(getContext(),getString(R.string.internet_error));
                }else{
                    setData(n,e,i);
                }
            }
        });


    }

    private void getData(){
        loadingDialog(false);
        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Create(getContext(),getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.getInstance().getApi()
                    .getProfile(number,accessToken)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                String res = response.body().getMessage();
                                switch (res){
                                    case "ok":
                                        String gender = response.body().getSex();
                                        String name = response.body().getName();
                                        String email = response.body().getEmail();
                                        String plan = response.body().getPlanid();
                                        String invitecode = response.body().getInvitecode();
                                        if(plan.equals("2")){

                                            String buydate = response.body().getBuydate();
                                            String expdate = response.body().getExpdate();
                                            String nowdate = response.body().getNowdate();
                                            fPlan.setText(getString(R.string.plan_diamond));
                                            fExp.setText(getString(R.string.plan_diamond_exp
                                                    ,MyUtils.dateDiff(buydate,expdate,nowdate)));
                                        }else{
                                            fPlan.setText(getString(R.string.plan_bronze));
                                            fExp.setText(getString(R.string.plan_bronze_exp));
                                        }

                                    MySharedPreference.getInstance(getContext()).setSex(gender);
                                    MySharedPreference.getInstance(getContext()).setEmail(email);
                                    MySharedPreference.getInstance(getContext()).setName(name);
                                    MySharedPreference.getInstance(getContext()).setInviteCode(invitecode);
                                    fJob.setSelection(Integer.parseInt(response.body().getJob()),true);
                                    fEdu.setSelection(Integer.parseInt(response.body().getEducation()),true);
                                    fRegion.setSelection(Integer.parseInt(response.body().getRegion()),true);
                                    if(Integer.parseInt(response.body().getColor())!=0){
                                        ShapeDrawable d = new ShapeDrawable(new OvalShape());
                                        d.setBounds(50, 50, 50, 50);
                                        d.getPaint().setStyle(Paint.Style.FILL);
                                        d.getPaint().setColor(Integer.parseInt(response.body().getColor()));
                                        selectedColor.setBackground(d);
                                        color=Integer.parseInt(response.body().getColor());
                                    }

                                    if(name.isEmpty()){
                                        isFirst=1;
                                        fName.setHint(getString(R.string.txt_default_name));
                                    }
                                    setVariables();
                                        break;
                                    case "wrongaccess":
                                        MyToast.Create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                        default:
                                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                                            MyToast.Create(getContext(),getString(R.string.general_error));
                                }
                            }else{
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Create(getContext(),getString(R.string.general_error));
                            }
                        }
                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Create(getContext(),getString(R.string.general_error));

                        }
                    });
        }
    }

    private void setData(final String name,final String email,final String inviteCode){
        String number = MySharedPreference.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()) {
            MyToast.Create(getContext(), getString(R.string.data_error));
            MySharedPreference.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else if(number.equals(inviteCode)){
            MyToast.Create(getContext(), getString(R.string.invitecide_error));

        }else{
            save.setText(getString(R.string.txt_loading));
            save.setEnabled(false);
            fEmail.setEnabled(false);
            fName.setEnabled(false);
            fInviteCode.setEnabled(false);
            fRegion.setEnabled(false);
            fEdu.setEnabled(false);
            fJob.setEnabled(false);
            fColor.setEnabled(false);
            RetrofitClient.getInstance().getApi()
                    .setProfile(number,accessToken,name,gender,email,fEdu.getSelectedItemPosition(),fJob.getSelectedItemPosition(),fRegion.getSelectedItemPosition(),color,inviteCode,isFirst)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                            isFirst=0;
                            save.setText(getString(R.string.profile_save));
                            fRegion.setEnabled(true);
                            fEdu.setEnabled(true);
                            fJob.setEnabled(true);
                            fColor.setEnabled(true);
                            save.setEnabled(true);
                            fEmail.setEnabled(true);
                            fName.setEnabled(true);
                            fInviteCode.setEnabled(true);
                            if(response.isSuccessful()){
                                String res = response.body().getMessage();
                                switch (res){
                                    case "ok":
                                        MySharedPreference.getInstance(getContext()).setSex(gender);
                                        MySharedPreference.getInstance(getContext()).setEmail(email);
                                        MySharedPreference.getInstance(getContext()).setName(name);
                                        MySharedPreference.getInstance(getContext()).setInviteCode(inviteCode);
                                        MyToast.Create(getContext(),getString(R.string.txt_profile_success));
                                        setVariables();
                                        break;
                                    case "wrongaccess":
                                        MyToast.Create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.getInstance(getContext()).clear();
                                        myDatabase.deleteMessageTable();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                    default:
                                        MyToast.Create(getContext(),getString(R.string.general_error)+1);
                                }
                            }else{
                                MyToast.Create(getContext(),getString(R.string.general_error)+2);
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            save.setText(getString(R.string.profile_save));
                            save.setEnabled(true);
                            fEmail.setEnabled(true);
                            fName.setEnabled(true);
                            fRegion.setEnabled(true);
                            fEdu.setEnabled(true);
                            fJob.setEnabled(true);
                            fColor.setEnabled(true);
                            MyToast.Create(getContext(),getString(R.string.general_error)+3);
                        }
                    });
        }


    }



}
