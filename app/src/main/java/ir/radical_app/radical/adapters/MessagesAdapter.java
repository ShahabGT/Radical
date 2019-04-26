package ir.radical_app.radical.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.DateConverter;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.dialogs.MessageDialog;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;

import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST;
import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST_MESSAGE_EXTRA;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MessageModel> list;
    private MyDatabase myDatabase;

    public MessagesAdapter(Context context , ArrayList<MessageModel> list){
        this.context=context;
        this.list=list;
        myDatabase = new MyDatabase(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_messages,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MessageModel model = list.get(position);
        holder.title.setTypeface(holder.title.getTypeface(), Typeface.NORMAL);
        holder.message.setTypeface(holder.title.getTypeface(), Typeface.NORMAL);
        holder.message.setTextColor(context.getResources().getColor(R.color.material_drawer_header_selection_subtext));
        holder.title.setText(model.getTitle());
        DateConverter dateConverter = new DateConverter();
        String date = model.getDate();
        dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
        holder.date.setText(context.getString(R.string.messages_model,dateConverter.toString().substring(5),date.substring(11,16)));
        holder.message.setText(model.getMessage());

        if(model.getRead().equals("0")){
            holder.title.setTypeface(holder.title.getTypeface(), Typeface.BOLD);
            holder.message.setTypeface(holder.title.getTypeface(), Typeface.BOLD);
            holder.message.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(View-> {

                MessageDialog messageDialog = new MessageDialog(context);
                messageDialog.setText(model.getMessage());
                messageDialog.setTitle(model.getTitle());
                messageDialog.setDate(dateConverter.toString()+" "+date.substring(11,16));

                messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                messageDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                messageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


                messageDialog.show();
                Window window = messageDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                myDatabase.readMessage(model.getId());
                list.get(position).setRead("1");
                Intent intent = new Intent();
                intent.setAction(RADICAL_BROADCAST);
                intent.putExtra(RADICAL_BROADCAST_MESSAGE_EXTRA,-1);
                context.sendBroadcast(intent);
                notifyItemChanged(position);

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title,date,message;

        ViewHolder(@NonNull View v) {
            super(v);

            title = v.findViewById(R.id.messages_row_title);
            date = v.findViewById(R.id.messages_row_date);
            message = v.findViewById(R.id.messages_row_message);

        }
    }
}
