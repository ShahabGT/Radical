package ir.radical_app.radical.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import ir.radical_app.radical.R

class MyToast{


    companion object {
        fun create(context: Context,text:String){
            val nullParent :ViewGroup? = null
            val t:Toast = Toast.makeText(context,"",Toast.LENGTH_LONG)
            val view : View =LayoutInflater.from(context).inflate(R.layout.custom_toast,nullParent)
            val textView:TextView = view.findViewById(R.id.toast_text)
            textView.text=text
            t.view=view
            t.show()
        }

        fun createShort(context: Context,text:String){
            val nullParent :ViewGroup? = null
            val t:Toast = Toast.makeText(context,"",Toast.LENGTH_SHORT)
            val view : View =LayoutInflater.from(context).inflate(R.layout.custom_toast,nullParent)
            val textView:TextView = view.findViewById(R.id.toast_text)
            textView.text=text
            t.view=view
            t.show()
        }
    }
}