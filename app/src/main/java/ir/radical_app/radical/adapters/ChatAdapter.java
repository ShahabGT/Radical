package ir.radical_app.radical.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.DateConverter;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<MessageModel> list;

    public ChatAdapter(Context context,  ArrayList<MessageModel> list){
        this.context=context;
        this.activity=(Activity)context;
        this.list=list;

    }

    public void addMessageItem(MessageModel model){
        this.list.add(model);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_chat,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        MessageModel messages = list.get(position);
        try {
            if (messages.getSender().equals("1")||messages.getSender().equals("admin")) {
                holder.main.setGravity(Gravity.LEFT);
                holder.message.setBackground(activity.getResources().getDrawable(R.drawable.shape_other));
            } else {

                holder.main.setGravity(Gravity.RIGHT);
                holder.message.setBackground(activity.getResources().getDrawable(R.drawable.shape_me));
            }

            holder.message.setText(messages.getMessage());
            String date = messages.getDate();
            DateConverter dateConverter = new DateConverter();

            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
            holder.time.setText(date.substring(11,16));
        }catch (Exception e){}
    }


    @Override
    public int getItemCount() {
            return list.size();


    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView time,message;
        LinearLayout main;


        ViewHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.chat_row_message);
            time=itemView.findViewById(R.id.chat_row_time);
            main=itemView.findViewById(R.id.chat_row_main_linear);
        }
    }
}
