package ir.radical_app.radical.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.fragments.SpecificCategoryFragment;
import ir.radical_app.radical.models.JsonResponse;

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.ViewHolder> {

    private Context context;
    private FragmentActivity activity;
    private List<JsonResponse> list;

    public CategoryAdapter2(Context context, List<JsonResponse> list, FragmentActivity activity){

        this.context=context;
        this.activity=activity;
        this.list=list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category2,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        JsonResponse model = list.get(position);
        holder.title.setText(model.getName());
        holder.id.setText(model.getCategoryId());
        Uri uri = Uri.parse(context.getString(R.string.category_image_url,model.getCategoryId()));
        holder.pic.setImageURI(uri);


        holder.itemView.setOnClickListener(View-> {

                if(!MyUtils.Companion.checkInternet(context)){
                    MyToast.Companion.create(context,context.getString(R.string.internet_error));
                    return;
                }
                Const.Companion.setCategory(holder.id.getText().toString());
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, new SpecificCategoryFragment())
                        .addToBackStack(null)
                        .commit();

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
