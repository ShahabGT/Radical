package ir.radical_app.radical.adapters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.JustifiedTextView;
import ir.radical_app.radical.models.HelpItem;
import ir.radical_app.radical.R;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    private ArrayList<HelpItem> list;


    public HelpAdapter(ArrayList<HelpItem> list){
        this.list=list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_help,parent,false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HelpItem model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.text.setText(model.getDescription());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private JustifiedTextView text;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.help_item_title);
            text = itemView.findViewById(R.id.help_item_text);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            text.setLineSpacing(15);

        }
    }
}
