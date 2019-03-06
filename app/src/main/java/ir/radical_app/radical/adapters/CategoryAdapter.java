package ir.radical_app.radical.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.fragments.SpecificCategoryFragment;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private FragmentActivity activity;
    private ArrayList<JsonResponse> list;

    public CategoryAdapter(Context context, ArrayList<JsonResponse> list,FragmentActivity activity){

        this.context=context;
        this.activity=activity;
        this.list=list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        JsonResponse model = list.get(position);
//        if(model.getName().length()>9)
//            holder.title.setText(model.getName().substring(0,10)+"...");
//        else
            holder.title.setText(model.getName());

        holder.id.setText(model.getCategoryId());
        Uri uri = Uri.parse(context.getString(R.string.category_image_url,model.getCategoryId()));
        holder.pic.setImageURI(uri);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Const.category=holder.id.getText().toString();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, new SpecificCategoryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView pic;
        private TextView id,title;

        ViewHolder(@NonNull View v) {
            super(v);

            pic= v.findViewById(R.id.category_row_image);
            title= v.findViewById(R.id.category_row_title);
            id= v.findViewById(R.id.category_row_id);
        }
    }
}
