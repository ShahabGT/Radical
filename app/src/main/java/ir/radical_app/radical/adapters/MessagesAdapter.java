package ir.radical_app.radical.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.DateConverter;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MessageModel> list;

    public MessagesAdapter(Context context , ArrayList<MessageModel> list){
        this.context=context;
        this.list=list;
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
        holder.title.setText(model.getTitle());
        DateConverter dateConverter = new DateConverter();
        String date = model.getDate();
        dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
        holder.date.setText(context.getString(R.string.messages_model,dateConverter.toString(),date.substring(11)));
        holder.message.setText(model.getMessage());

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
