package ir.radical_app.radical.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.stephenvinouze.materialnumberpickercore.MaterialNumberPicker;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import ir.radical_app.radical.activities.SplashActivity;
import ir.radical_app.radical.arch.Shops.ShopsItem;
import ir.radical_app.radical.arch.Shops.ShopsResponse;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.data.RetrofitClient;
import ir.radical_app.radical.dialogs.InfoDialog;
import ir.radical_app.radical.dialogs.LoadingDialog;
import ir.radical_app.radical.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Float.NaN;


public class StatsFragment extends Fragment {

    private TextView yearcount,yearprice,totalcount,totalprice,countyeartitle,counttotalyeartitle,sumtotalyeartitle,pieTitle;
    private TextView selectyear;
    private BarChart mBarChart;
    private PieChart mPieChart;
    private LoadingDialog dialog;
    private Typeface typeface;


    public StatsFragment() {
    }

    private void showInfoDialog() {
        if(MySharedPreference.Companion.getInstance(getContext()).getFirstStat()){
            MySharedPreference.Companion.getInstance(getContext()).setFirstStat();
        }else{
            return;
        }


        InfoDialog introDialog = new InfoDialog(getContext(),"از این قسمت میتونی پولی که صرفه جویی کردی ببینی!");
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
        typeface = ResourcesCompat.getFont(getContext(), R.font.vazir);
        yearcount = v.findViewById(R.id.stats_totalyearcount);
        yearprice =  v.findViewById(R.id.stats_totalyearprice);
        countyeartitle =  v.findViewById(R.id.stats_countyeartitle);
        totalcount =  v.findViewById(R.id.stats_totalcount);
        totalprice =  v.findViewById(R.id.stats_totalprice);
        selectyear =  v.findViewById(R.id.stats_yearselector);
        counttotalyeartitle =  v.findViewById(R.id.stats_counttotalyeartitle);
        sumtotalyeartitle =  v.findViewById(R.id.stats_sumtotalyeartitle);
        pieTitle =  v.findViewById(R.id.stats_pietitle);

        mBarChart =   v.findViewById(R.id.barchart);
        mPieChart =  v.findViewById(R.id.piechart);
        getData("2019");
        onClicks();
    }

    private void onClicks(){
        selectyear.setOnClickListener(View-> {
                final MaterialNumberPicker numberPicker = new MaterialNumberPicker(getContext(),1398,1499,1398,Color.TRANSPARENT,Color.BLACK,60,Typeface.BOLD,false,false,"vazir.ttf");

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
        });
    }

    private void getData(String year){
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
                                yearprice.setText(getString(R.string.amount_model, MyUtils.Companion.moneySeparator(result.get(12).getSum())));
                                totalprice.setText(getString(R.string.amount_model, MyUtils.Companion.moneySeparator(result.get(13).getSum())));


                                String a =(Integer.parseInt(year)-621)+"";
                                countyeartitle.setText(getString(R.string.stats_countyeartitle,a));
                                counttotalyeartitle.setText(getString(R.string.stats_counttotalyeartitle,a));
                                sumtotalyeartitle.setText(getString(R.string.stats_sumtotalyeartitle,a));
                                pieTitle.setText(getString(R.string.stats_title2,a));
                                Barchart(priceargs);
                                PieChart(Integer.parseInt(a),Float.valueOf(result.get(12).getSum()));
                                showInfoDialog();

                            }else{
                                loadingDialog(true);
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                MyToast.Companion.create(getContext(),getString(R.string.general_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<ShopsResponse> call, Throwable t) {
                            loadingDialog(true);
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                            MyToast.Companion.create(getContext(),getString(R.string.general_error));
                        }
                    });
        }

    }

    private void Barchart(float[] args){

        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1,args[0]));
        barEntries.add(new BarEntry(2,args[1]));
        barEntries.add(new BarEntry(3,args[2]));
        barEntries.add(new BarEntry(4,args[3]));
        barEntries.add(new BarEntry(5,args[4]));
        barEntries.add(new BarEntry(6,args[5]));
        barEntries.add(new BarEntry(7,args[6]));
        barEntries.add(new BarEntry(8,args[7]));
        barEntries.add(new BarEntry(9,args[8]));
        barEntries.add(new BarEntry(10,args[9]));
        barEntries.add(new BarEntry(11,args[10]));
        barEntries.add(new BarEntry(13,args[11]));
        BarDataSet barDataSet = new BarDataSet(barEntries,null);
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
        barData.setValueTypeface(typeface);
        barData.setValueTextSize(10);
        mBarChart.animateY(1000);
        mBarChart.setData(barData);
        mBarChart.setFitBars(true);
        mBarChart.setScaleEnabled(false);
        mBarChart.setPinchZoom(true);
        mBarChart.setVisibleXRangeMaximum(10);
        mBarChart.setHardwareAccelerationEnabled(true);


        Description description = new Description();
        description.setEnabled(false);
        mBarChart.setDescription(description);

        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);




        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTypeface(typeface);
        xAxis.setXOffset(-10);
        xAxis.setYOffset(-10);
        xAxis.disableGridDashedLine();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setDrawLabels(true);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور",
                "مهر","آبان","آذر","دی","بهمن","اسفند"}));

        YAxis leftAxis = mBarChart.getAxisLeft();
        YAxis rightAxis = mBarChart.getAxisRight();
        leftAxis.setTypeface(typeface);
        rightAxis.setTypeface(typeface);
        rightAxis.setEnabled(false);

        mBarChart.setNoDataTextTypeface(typeface);
        mBarChart.invalidate();
        mBarChart.notifyDataSetChanged();


    }

    private void PieChart(int year,float arg){
        List<PieEntry> pieEntries = new ArrayList<>();

        if(arg>=10000000)
            pieEntries.add(new PieEntry(arg,"تخفیف کسب شده در سال "+year));
        else{
            float remaning = 10000000-arg;
            pieEntries.add(new PieEntry(arg,"تخفیف کسب شده در سال "+year));
            pieEntries.add(new PieEntry(remaning,"مانده"));
        }
        mPieChart.animateXY(1000,1000);
        PieDataSet pieDataSet = new PieDataSet(pieEntries,null);
        int colorYellow = getResources().getColor(R.color.yellow);
        int colorYellowLight = getResources().getColor(R.color.yellow_light);
        pieDataSet.setColors(colorYellow,colorYellowLight);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTypeface(typeface);
        pieData.setValueTextSize(18);
//        pieDataSet.setValueLinePart1OffsetPercentage(80.f);
//        pieDataSet.setValueLinePart1Length(0.2f);
//        pieDataSet.setValueLinePart2Length(0.4f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        mPieChart.setData(pieData);

        Description description = new Description();
        description.setEnabled(false);
        mPieChart.setDescription(description);
        mPieChart.setCenterText("تخفیف های کسب شده");
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setDrawEntryLabels(false);

        Legend legend = mPieChart.getLegend();
        legend.setTypeface(typeface);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mPieChart.setCenterTextTypeface(typeface);
        mPieChart.setEntryLabelTypeface(typeface);
        mPieChart.setHardwareAccelerationEnabled(true);
        mPieChart.setNoDataTextTypeface(typeface);
        mPieChart.setRotationEnabled(false);

        mPieChart.notifyDataSetChanged();
        mPieChart.invalidate();

    }

}
