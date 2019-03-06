package ir.radical_app.radical.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.radical_app.radical.R;

public class MyUtils {


    public static boolean checkInternet(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
            return activeInfo!=null && activeInfo.isConnected();

        }catch (NullPointerException e){return false;}

    }

    public static void hideKeyboard(Activity activity){
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){}
    }
    public static void shareCode(Activity activity,String title){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title);
        activity.startActivity(Intent.createChooser(sharingIntent, activity.getString(R.string.txt_share)));
    }


    public static boolean isEmailValid(String email)
    {
        Pattern pattern;
        boolean res;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        if(matcher.matches()){
            email=email.toLowerCase();
            res = email.contains("@yahoo.") || email.contains("@gmail.") || email.contains("@aol.") || email.contains("@hotmail.") || email.contains("@ymail.") || email.contains("@live.");
        }else {res=false;}
        return  res;
    }

    public static int dateDiff(String buy,String exp,String now){
        long buyDate = stringToDate(buy);
        long expDate = stringToDate(exp);
        long nowDate = stringToDate(now);
        long remaining = nowDate-buyDate;
        long res = expDate-buyDate;
        return ((int) (res / (1000*60*60*24)))-((int) (remaining / (1000*60*60*24)));
    }

    private static long stringToDate(String data){
        long res= 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            Date date = format.parse(data);
            res = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return res;
    }

    public static String moneySeparator(String text) {

        int len = text.length();
        if(len>3) {
            String res="";
            while(len>3) {
                res = ","+text.substring(len-3, len) + res;

                len-=3;
            }

            res = text.substring(0,len)+res;
            return res;
        }else {
            return text;
        }
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }


}
