package ir.radical_app.radical.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.text.setVisibility(View.GONE);
        holder.text.setText(model.getDescription());

        holder.itemView.setOnClickListener(View-> {

                if(holder.text.getVisibility()== android.view.View.GONE){
                    holder.icon.setImageResource(R.drawable.vector_top);
                    holder.text.setVisibility(android.view.View.VISIBLE);
                }else{
                    holder.icon.setImageResource(R.drawable.vector_bottom);
                    holder.text.setVisibility(android.view.View.GONE);

                }

        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title,text;
        private ImageView icon;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.help_item_icon);

            title = itemView.findViewById(R.id.help_item_title);
            text = itemView.findViewById(R.id.help_item_text);

        }
    }
}
