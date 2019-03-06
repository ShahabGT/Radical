package ir.radical_app.radical.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {

    private static MySharedPreference instanse=null;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    public static MySharedPreference getInstance(Context context){
            if(instanse==null){
                instanse =  new MySharedPreference(context);
            }

            return instanse;

    }


    private MySharedPreference(Context context){

        sp = context.getSharedPreferences("RadicalPreference",Context.MODE_PRIVATE);
        editor = sp.edit();

    }

    public void clear(){
        editor.clear().apply();
    }

    public void setLanguage(String language){
        editor.putString("language",language).apply();
    }

    public String getLanguage(){
        return sp.getString("language","en");
    }

    public void setFirstBoot(boolean firstBoot){
        editor.putBoolean("firstBoot",firstBoot).apply();
    }
    public boolean getFirstBoot(){
        return  sp.getBoolean("firstBoot",true);
    }

    public void setIsLogin(boolean IsLogin){
        editor.putBoolean("IsLogin",IsLogin).apply();
    }
    public boolean getIsLogin(){
        return  sp.getBoolean("IsLogin",false);
    }

    public void setFBToken(String FBToken){
        editor.putString("FBToken",FBToken).apply();
    }
    public String getFBToken(){
        return sp.getString("FBToken","");
    }

    public void setAccessToken(String AccessToken){
        editor.putString("AccessToken",AccessToken).apply();
    }
    public String getAccessToken(){
        return sp.getString("AccessToken","");
    }

    public void setNumber(String number){
        editor.putString("number",number).apply();
    }
    public String getNumber(){
        return sp.getString("number","");
    }

    public void setSex(String sex){
        editor.putString("sex",sex).apply();
    }
    public String getSex(){
        return sp.getString("sex","");
    }

    public void setEmail(String Email){
        editor.putString("Email",Email).apply();
    }
    public String getEmail(){
        return sp.getString("Email","");
    }

    public void setName(String Name){
        editor.putString("Name",Name).apply();
    }
    public String getName(){
        return sp.getString("Name","");
    }

    public void setInviteCode(String InviteCode){
        editor.putString("InviteCode",InviteCode).apply();
    }
    public String getInviteCode(){
        return sp.getString("InviteCode","");
    }
}
