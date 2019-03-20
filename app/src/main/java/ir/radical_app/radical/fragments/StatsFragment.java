package ir.radical_app.radical.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import java.text.NumberFormat;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.arch.Shops.ShopsItem;
import ir.radical_app.radical.arch.Shops.ShopsResponse;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends Fragment {

    private TextView yearcount,yearprice,totalcount,totalprice,countyeartitle,counttotalyeartitle,sumtotalyeartitle;
    private MaterialButton selectyear;
    private BarChart mBarChart;
    private PieChart mPieChart;
    private LoadingDialog dialog;




    public StatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_stats, container, false);

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

        yearcount = v.findViewById(R.id.stats_totalyearcount);
        yearprice =  v.findViewById(R.id.stats_totalyearprice);
        countyeartitle =  v.findViewById(R.id.stats_countyeartitle);
        totalcount =  v.findViewById(R.id.stats_totalcount);
        totalprice =  v.findViewById(R.id.stats_totalprice);
        selectyear =  v.findViewById(R.id.stats_yearselector);
        counttotalyeartitle =  v.findViewById(R.id.stats_counttotalyeartitle);
        sumtotalyeartitle =  v.findViewById(R.id.stats_sumtotalyeartitle);

        mBarChart =   v.findViewById(R.id.barchart);
        mPieChart =  v.findViewById(R.id.piechart);
        getData("2018");
        onClicks();
    }

    private void onClicks(){
        selectyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(getContext())
                        .minValue(1397)
                        .maxValue(1499)
                        .defaultValue(1397)
                        .backgroundColor(Color.WHITE)
                        .separatorColor(Color.TRANSPARENT)
                        .textColor(Color.BLACK)
                        .textSize(20)
                        .enableFocusability(false)
                        .wrapSelectorWheel(true)
                        .build();
                final AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getContext());

                builder
                        .setTitle("انتخاب سال")
                        .setView(numberPicker)
                        .setCancelable(true)
                        .setPositiveButton("انتخاب", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String a = String.valueOf(numberPicker.getValue()+621);
                                getData(a);
                            }
                        })
                        .setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void getData(String year){
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
                    .getStats(number,accessToken,year)
                    .enqueue(new Callback<ShopsResponse>() {
                        @Override
                        public void onResponse(Call<ShopsResponse> call, Response<ShopsResponse> response) {
                            loadingDialog(true);
                            if(response.isSuccessful()){
                                List<ShopsItem> result = response.body().getData();

                                float[] countargs = new float[12];
                                float[] priceargs = new float[12];
                                for (int i=0;i<12;i++){
                                    countargs[i]= Float.valueOf(result.get(i).getCount());
                                    priceargs[i]= Float.valueOf(result.get(i).getSum());
                                }
                                NumberFormat nf= NumberFormat.getNumberInstance();
                                nf.setMaximumFractionDigits(0);
                                yearcount.setText( result.get(12).getCount());
                                totalcount.setText( result.get(13).getCount());
                                yearprice.setText(getString(R.string.amount_model, MyUtils.moneySeparator(result.get(12).getSum())));
                                totalprice.setText(getString(R.string.amount_model, MyUtils.moneySeparator(result.get(13).getSum())));
                                Barchart(countargs);
                                PieChart(priceargs);

                                String a =(Integer.parseInt(year)-621)+"";
                                countyeartitle.setText(getString(R.string.stats_countyeartitle,a));
                                counttotalyeartitle.setText(getString(R.string.stats_counttotalyeartitle,a));
                                sumtotalyeartitle.setText(getString(R.string.stats_sumtotalyeartitle,a));


                            }else{
                                loadingDialog(true);
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<ShopsResponse> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Create(getContext(),getString(R.string.general_error));
                        }
                    });



        }

    }


    private void Barchart(float[] args){
        mBarChart.clearChart();
        mBarChart.addBar(new BarModel("فروردین",args[0], 0xFF123456));
        mBarChart.addBar(new BarModel("اردیبهشت",args[1],  0xFF343456));
        mBarChart.addBar(new BarModel("خرداد",args[2], 0xFF563456));
        mBarChart.addBar(new BarModel("تیر",args[3], 0xFF873F56));
        mBarChart.addBar(new BarModel("مرداد",args[4], 0xFF56B7F1));
        mBarChart.addBar(new BarModel("شهریور",args[5],  0xFF343456));
        mBarChart.addBar(new BarModel("مهر",args[6], 0xFF1FF4AC));
        mBarChart.addBar(new BarModel("آبان",args[7],  0xFF1BA4E6));
        mBarChart.addBar(new BarModel("آذر",args[8],  0xFF1BA4E6));
        mBarChart.addBar(new BarModel("دی",args[9],  0xFF1BA4E6));
        mBarChart.addBar(new BarModel("بهمن",args[10],  0xFF1BA4E6));
        mBarChart.addBar(new BarModel("اسفند",args[11],  0xFF1BA4E6));
        mBarChart.update();
        mBarChart.startAnimation();
    }
    private void PieChart(float[] args){
        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel("فروردین", args[0], Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("اردیبهشت", args[1], Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("خرداد", args[2], Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("تیر", args[3], Color.parseColor("#FED70E")));
        mPieChart.addPieSlice(new PieModel("مرداد", args[4], Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("شهریور", args[5], Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("مهر", args[6], Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("آبان", args[7], Color.parseColor("#FED70E")));
        mPieChart.addPieSlice(new PieModel("آذر", args[8], Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("دی", args[9], Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("بهمن", args[10], Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("اسفند", args[11], Color.parseColor("#FED70E")));
        mPieChart.update();
        mPieChart.startAnimation();
    }

}
