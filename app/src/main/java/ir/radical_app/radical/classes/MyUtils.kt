package ir.radical_app.radical.classes

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager
import ir.radical_app.radical.R
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern

class MyUtils {

    companion object{
        fun checkInternet(context: Context):Boolean{
            val cntMng:ConnectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInf = cntMng.activeNetworkInfo
            return netInf!=null && netInf.isConnected
        }

        fun hideKeyboard(activity: Activity){
                val inptMng : InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            try {
                inptMng.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }catch(e:Exception){}


        }

        fun shareCode(activity: Activity,title:String){
            val intent= Intent(Intent.ACTION_SEND)
            intent.type="text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,title)
            activity.startActivity(Intent.createChooser(intent,activity.getString(R.string.txt_share)))

        }
        fun removeNotification(context: Context){
            val notificationManager:NotificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Const.NOTIFICATION_ID)


        }

        fun isEmailValid(Email: String): Boolean {
            var email = Email
            val pattern: Pattern
            val res: Boolean
            val matcher: Matcher
            val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(emailPattern)
            matcher = pattern.matcher(email)
            if (matcher.matches()) {
                email = email.toLowerCase()
                res = email.contains("@yahoo.") || email.contains("@gmail.") || email.contains("@aol.") || email.contains("@hotmail.") || email.contains("@ymail.") || email.contains("@live.")
            } else {
                res = false
            }
            return res
        }

        fun moneySeparator(text : String):String{
            if (text.length<4) return text
            var len=text.length
            var res=text
            do{
                res=res.substring(0,len-3)+","+res.substring(len-3)
                len-=3
            }while (len>3)
            return res
        }




    }


}