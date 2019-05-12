package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;

import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.dialogs.InfoDialog;
import ir.radical_app.radical.dialogs.UpgradeDialog;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private EditText fName,fFamily,fEmail,fInviteCode;
    private TextView fPlan,fExp,fNumber,saveText,fBirth;
    private Spinner fJob,fEdu,fRegion,fSex;
    private int isFirst=0;
    private LoadingDialog dialog;
    private MyDatabase myDatabase;
    private MaterialCardView save;
    private ConstraintLayout cardTime;
    private ImageView nEdit,fEdit,eEdit,jEdit,edEdit,rEdit,bEdit;
    private int plan ;


    public ProfileFragment() {
    }

    private void showInfoDialog() {
        if(MySharedPreference.Companion.getInstance(getContext()).getFirstProfile()){
            MySharedPreference.Companion.getInstance(getContext()).setFirstProfile();
        }else{
            return;
        }


        InfoDialog introDialog = new InfoDialog(getContext(),"با تکمیل این قسمت مارو تو خدمات دهی بهتر کمک میکنی و از ما هدیه دریافت میکنی");
        introDialog.setCancelable(true);
        introDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        introDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        introDialog.show();
        Window window = introDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);

        init(v);
        return v;
    }

    private void loadingDialog(boolean cancel){
        if(!cancel){
            dialog = new LoadingDialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(false);
            dialog.show();
        }else{
            dialog.dismiss();
        }
    }


    private void init(View v){
        plan = MySharedPreference.Companion.getInstance(getContext()).getPlan();
        fName = v.findViewById(R.id.profile_name);
        fFamily = v.findViewById(R.id.profile_family);
        fNumber = v.findViewById(R.id.profile_phone);
        fInviteCode = v.findViewById(R.id.profile_invitecode);
        fEmail = v.findViewById(R.id.profile_email);
        fEmail = v.findViewById(R.id.profile_email);
        fPlan = v.findViewById(R.id.profile_plan);
        fExp = v.findViewById(R.id.profile_exp);
        fJob = v.findViewById(R.id.profile_job);
        fEdu = v.findViewById(R.id.profile_edu);
        fRegion = v.findViewById(R.id.profile_region);
        fSex = v.findViewById(R.id.profile_sex);
        fBirth = v.findViewById(R.id.profile_birthday);
        save = v.findViewById(R.id.profile_save);
        saveText = v.findViewById(R.id.profile_save_text);
        cardTime = v.findViewById(R.id.profile_cardtime);
        myDatabase = new MyDatabase(getContext());

        nEdit = v.findViewById(R.id.profile_name_edit);
        fEdit = v.findViewById(R.id.profile_family_edit);
        eEdit = v.findViewById(R.id.profile_email_edit);
        jEdit = v.findViewById(R.id.profile_job_edit);
        edEdit = v.findViewById(R.id.profile_edu_edit);
        rEdit = v.findViewById(R.id.profile_region_edit);
        bEdit = v.findViewById(R.id.profile_birthday_edit);

        if(plan!=1){
            cardTime.setVisibility(View.VISIBLE);
        }

        onClicks();
        if(MyUtils.Companion.checkInternet(getContext()))
        getData();
        else{
            setVariables();
            MyToast.Companion.create(getContext(),getString(R.string.internet_error));

        }

        initSpinners();
    }

    private void initSpinners(){
        ArrayAdapter<CharSequence> jobadapter = ArrayAdapter.createFromResource(getContext(),R.array.jobs,R.layout.row_spinner);
        jobadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fJob.setAdapter(jobadapter);
        fJob.setPrompt(getString(R.string.profile_job));
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
        fEdu.setPrompt(getString(R.string.profile_edu));

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
        fRegion.setPrompt(getString(R.string.profile_region));
        fRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> sexdapter = ArrayAdapter.createFromResource(getContext(),R.array.sex,R.layout.row_spinner);
        sexdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fSex.setAdapter(sexdapter);
        fSex.setPrompt(getString(R.string.profile_gender));

        fSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setVariables(){
        fName.setText(MySharedPreference.Companion.getInstance(getContext()).getName());
        fFamily.setText(MySharedPreference.Companion.getInstance(getContext()).getFamily());
        fNumber.setText(MySharedPreference.Companion.getInstance(getContext()).getNumber());
        fEmail.setText(MySharedPreference.Companion.getInstance(getContext()).getEmail());
        String i = MySharedPreference.Companion.getInstance(getContext()).getInviteCode();
        if(!i.isEmpty()){
            fInviteCode.setEnabled(false);
            fInviteCode.setText(i);
        }
        showInfoDialog();

        if(!MySharedPreference.Companion.getInstance(getContext()).getName().isEmpty()){
            nEdit.setVisibility(View.VISIBLE);
            fName.setEnabled(false);
            fName.setBackground(null);

            fEdit.setVisibility(View.VISIBLE);
            fFamily.setEnabled(false);
            fFamily.setBackground(null);

            eEdit.setVisibility(View.VISIBLE);
            fEmail.setEnabled(false);
            fEmail.setBackground(null);

            fSex.setBackground(null);
            fSex.setEnabled(false);

            jEdit.setVisibility(View.VISIBLE);
            fJob.setEnabled(false);
            fJob.setBackground(null);

            jEdit.setVisibility(View.VISIBLE);
            fJob.setEnabled(false);
            fJob.setBackground(null);

            edEdit.setVisibility(View.VISIBLE);
            fEdu.setEnabled(false);
            fEdu.setBackground(null);

            rEdit.setVisibility(View.VISIBLE);
            fRegion.setEnabled(false);
            fRegion.setBackground(null);



        }
        if(!MySharedPreference.Companion.getInstance(getContext()).getBirthday().isEmpty()){
            fBirth.setText(MySharedPreference.Companion.getInstance(getContext()).getBirthday());
            bEdit.setVisibility(View.VISIBLE);
            fBirth.setEnabled(false);
        }

    }

    private void onClicks(){
        nEdit.setOnClickListener(View-> {
                nEdit.setVisibility(View.GONE);
                fName.setEnabled(true);
                fName.setBackground(getResources().getDrawable(R.drawable.input_bg));

        });
        fEdit.setOnClickListener(View-> {
                fEdit.setVisibility(View.GONE);
                fFamily.setEnabled(true);
                fFamily.setBackground(getResources().getDrawable(R.drawable.input_bg));
        });
        eEdit.setOnClickListener(View-> {
                eEdit.setVisibility(View.GONE);
                fEmail.setEnabled(true);
                fEmail.setBackground(getResources().getDrawable(R.drawable.input_bg));
        });
        jEdit.setOnClickListener(View-> {
                jEdit.setVisibility(View.GONE);
                fJob.setEnabled(true);
                fJob.setBackground(getResources().getDrawable(R.drawable.spinner_bg));

        });
        edEdit.setOnClickListener(View-> {
                edEdit.setVisibility(View.GONE);
                fEdu.setEnabled(true);
                fEdu.setBackground(getResources().getDrawable(R.drawable.spinner_bg));

        });
        rEdit.setOnClickListener(View-> {
                rEdit.setVisibility(View.GONE);
                fRegion.setEnabled(true);
                fRegion.setBackground(getResources().getDrawable(R.drawable.spinner_bg));

        });
        bEdit.setOnClickListener(View->{
            bEdit.setVisibility(View.GONE);
            fBirth.setEnabled(true);
        });
        fBirth.setOnClickListener(View->{
            PersianCalendar persianCalendar = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this::onDateSet,
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay()
            );
            PersianCalendar maxDate = new PersianCalendar();
            maxDate.setPersianDate(1389,12,29);

            PersianCalendar minDate = new PersianCalendar();
            minDate.setPersianDate(1300,1,1);
            datePickerDialog.setMaxDate(maxDate);
            datePickerDialog.setMinDate(minDate);
            datePickerDialog.setFirstDayOfWeek(Calendar.SATURDAY);

            datePickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog");

        });
        save.setOnClickListener(View-> {

                MyUtils.Companion.hideKeyboard(getActivity());
                if(!MyUtils.Companion.checkInternet(getContext())){
                    MyToast.Companion.create(getContext(),getContext().getString(R.string.internet_error));
                    return;
                }
                String n = fName.getText().toString();
                String b = fBirth.getText().toString();
                String f = fFamily.getText().toString();
                String e = fEmail.getText().toString();
                String i = fInviteCode.getText().toString();
                if(n.isEmpty() || n.length()<2){
                    MyToast.Companion.create(getContext(),getString(R.string.name_error));

                }else if(f.isEmpty() || f.length()<2){
                    MyToast.Companion.create(getContext(),getString(R.string.family_error));

                }else if (e.isEmpty() || !MyUtils.Companion.isEmailValid(e)){
                    MyToast.Companion.create(getContext(),getString(R.string.email_error));
                }else if (!i.isEmpty() && (i.length()<11 || !i.startsWith("09")) ){
                    MyToast.Companion.create(getContext(),getString(R.string.invite_error));
                }else if (!MyUtils.Companion.checkInternet(getContext())){
                    MyToast.Companion.create(getContext(),getString(R.string.internet_error));
                }else if(fSex.getSelectedItemPosition()==0 ||fEdu.getSelectedItemPosition()==0 ||
                        fJob.getSelectedItemPosition()==0 ||fRegion.getSelectedItemPosition()==0 ) {
                    MyToast.Companion.create(getContext(),getString(R.string.form_error));

                }else if(fBirth.getText().equals(getString(R.string.profile_birthday_select))) {
                    MyToast.Companion.create(getContext(),getString(R.string.birthday_error));
                }else{
                    setData(n,f,e,i,b);
                }

        });


    }

    private void getData(){
        loadingDialog(false);
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()){
            MyToast.Companion.create(getContext(),getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else{
            RetrofitClient.Companion.getInstance().getApi()
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
                                        String birthday = response.body().getBirthday();
                                        String family = response.body().getFamily();
                                        String email = response.body().getEmail();
                                        String plan = response.body().getPlanid();
                                        String invitecode = response.body().getInvitecode();
                                        if(plan.equals("2")){
                                            String remaining = response.body().getRemaining();
                                            fPlan.setText(getString(R.string.plan_diamond));
                                            fExp.setText(getString(R.string.plan_diamond_exp,Integer.parseInt(remaining)));
                                            if(Integer.parseInt(remaining)<=2){
                                                fExp.setTextColor(getResources().getColor(R.color.red));
                                            }
                                        }else{
                                            fPlan.setText(getString(R.string.plan_bronze));
                                            fExp.setText(getString(R.string.plan_buy));
                                            fExp.setTextColor(getResources().getColor(R.color.red));
                                            fExp.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showUpgradeDialog();
                                                }
                                            });
                                        }

                                    MySharedPreference.Companion.getInstance(getContext()).setSex(gender);
                                    MySharedPreference.Companion.getInstance(getContext()).setBirthday(birthday);
                                    MySharedPreference.Companion.getInstance(getContext()).setEmail(email);
                                    MySharedPreference.Companion.getInstance(getContext()).setName(name);
                                    MySharedPreference.Companion.getInstance(getContext()).setFamily(family);
                                    MySharedPreference.Companion.getInstance(getContext()).setInviteCode(invitecode);
                                    fJob.setSelection(Integer.parseInt(response.body().getJob()),true);
                                    fEdu.setSelection(Integer.parseInt(response.body().getEducation()),true);
                                    fRegion.setSelection(Integer.parseInt(response.body().getRegion()),true);
                                    fSex.setSelection(Integer.parseInt(response.body().getSex()),true);

                                    if(name.isEmpty()){
                                        isFirst=1;
                                        fName.setHint(getString(R.string.txt_default_name));
                                    }
                                    setVariables();
                                        break;
                                    case "wrongaccess":
                                        MyToast.Companion.create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                        default:
                                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                                }
                            }else{
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }
                        }
                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));

                        }
                    });
        }
    }

    private void setData(final String name,final String family,final String email,final String inviteCode,final String birthday){
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();

        if(number.isEmpty() || accessToken.isEmpty()) {
            MyToast.Companion.create(getContext(), getString(R.string.data_error));
            MySharedPreference.Companion.getInstance(getContext()).clear();
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }else if(number.equals(inviteCode)){
            MyToast.Companion.create(getContext(), getString(R.string.invitecide_error));

        }else{
            saveText.setText(getString(R.string.txt_loading));
            save.setEnabled(false);
            fEmail.setEnabled(false);
            fName.setEnabled(false);
            fFamily.setEnabled(false);
            fInviteCode.setEnabled(false);
            fRegion.setEnabled(false);
            fEdu.setEnabled(false);
            fJob.setEnabled(false);
            fSex.setEnabled(false);
            fBirth.setEnabled(false);
            RetrofitClient.Companion.getInstance().getApi()
                    .setProfile(number,accessToken,name,family,fSex.getSelectedItemPosition(),birthday,email,fEdu.getSelectedItemPosition(),fJob.getSelectedItemPosition(),fRegion.getSelectedItemPosition(),inviteCode,isFirst)
                    .enqueue(new Callback<JsonResponse>() {
                        @Override
                        public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                            saveText.setText(getString(R.string.profile_save));
                            fRegion.setEnabled(true);
                            fEdu.setEnabled(true);
                            fJob.setEnabled(true);
                            save.setEnabled(true);
                            fEmail.setEnabled(true);
                            fSex.setEnabled(true);
                            fBirth.setEnabled(true);

                            fName.setEnabled(true);
                            fFamily.setEnabled(true);
                            fInviteCode.setEnabled(true);
                            if(response.isSuccessful()){
                                String res = response.body().getMessage();
                                switch (res){
                                    case "ok":
                                        isFirst=0;
                                        MySharedPreference.Companion.getInstance(getContext()).setEmail(email);
                                        MySharedPreference.Companion.getInstance(getContext()).setName(name);
                                        MySharedPreference.Companion.getInstance(getContext()).setFamily(family);

                                        if(response.body().getDescription().equals("ok")) {
                                            MyToast.Companion.create(getContext(), getString(R.string.txt_profile_success));
                                            MySharedPreference.Companion.getInstance(getContext()).setInviteCode(inviteCode);
                                        }else {
                                            MyToast.Companion.create(getContext(), getString(R.string.txt_profile_success_noinvite));
                                        }
                                        setVariables();
                                        break;
                                    case "wrongaccess":
                                        MyToast.Companion.create(getContext(),getString(R.string.access_error));
                                        MySharedPreference.Companion.getInstance(getContext()).clear();
                                        myDatabase.deleteMessageTable();
                                        startActivity(new Intent(getActivity(), SplashActivity.class));
                                        getActivity().finish();
                                        break;

                                    default:
                                        MyToast.Companion.create(getContext(),getString(R.string.general_error));
                                }
                            }else{
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonResponse> call, Throwable t) {
                            saveText.setText(getString(R.string.profile_save));
                            save.setEnabled(true);
                            fEmail.setEnabled(true);
                            fName.setEnabled(true);
                            fBirth.setEnabled(true);

                            fFamily.setEnabled(true);
                            fRegion.setEnabled(true);
                            fEdu.setEnabled(true);
                            fSex.setEnabled(true);
                            fJob.setEnabled(true);
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                        }
                    });
        }


    }

    private void showUpgradeDialog() {
        UpgradeDialog upgradeDialog = new UpgradeDialog(getActivity());

        upgradeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        upgradeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        upgradeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        upgradeDialog.show();
        Window window = upgradeDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year+"/"+(monthOfYear+1)+"/"+dayOfMonth;
        fBirth.setText(date);
    }
}
