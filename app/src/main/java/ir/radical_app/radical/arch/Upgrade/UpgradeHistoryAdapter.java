package ir.radical_app.radical.arch.Upgrade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.DateConverter;
import ir.radical_app.radical.classes.MyUtils;

public class UpgradeHistoryAdapter extends PagedListAdapter<UpgradeItem, UpgradeHistoryAdapter.ItemViewHolder> {

    private Context mCtx;

    public UpgradeHistoryAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.row_upgrade_history, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final UpgradeItem item = getItem(position);

        if (item != null) {
            holder.layout.setVisibility(View.GONE);

            holder.description.setText(item.getDescription());
            holder.wallet.setText(mCtx.getString(R.string.amount_model,MyUtils.Companion.moneySeparator(item.getWalletPrice())));
            holder.amount.setText(mCtx.getString(R.string.amount_model,MyUtils.Companion.moneySeparator(item.getAmount())));
            holder.giftcode.setText(item.getGiftCode());
            if(item.getRescode().isEmpty())
                holder.rescode.setText(item.getMerchantId().substring(16));
            else
                holder.rescode.setText(item.getRescode());
            String date = item.getDate();
            DateConverter dateConverter = new DateConverter();
            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
            holder.date.setText(mCtx.getString(R.string.messages_model3,dateConverter.toString(),date.substring(11)));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.layout.getVisibility()==View.GONE){
                        holder.icon.setImageResource(R.drawable.vector_top);
                        holder.layout.setVisibility(View.VISIBLE);
                    }else{
                        holder.icon.setImageResource(R.drawable.vector_bottom);
                        holder.layout.setVisibility(View.GONE);

                    }
                }
            });

        }
    }


    private static DiffUtil.ItemCallback<UpgradeItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UpgradeItem>() {
                @Override
                public boolean areItemsTheSame(UpgradeItem oldItem, UpgradeItem newItem) {
                    return oldItem.getRescode().equals(newItem.getRescode());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(UpgradeItem oldItem, UpgradeItem newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView description,date,rescode,giftcode,amount,wallet;
        private ImageView icon;
        private ConstraintLayout layout;


        ItemViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.upgrade_row_description);
            icon = v.findViewById(R.id.upgrade_row_icon);
            layout = v.findViewById(R.id.upgrade_row_layout);
            date = v.findViewById(R.id.upgrade_row_date);
            rescode = v.findViewById(R.id.upgrade_row_rescode);
            giftcode = v.findViewById(R.id.upgrade_row_gift);
            amount = v.findViewById(R.id.upgrade_row_pay);
            wallet = v.findViewById(R.id.upgrade_row_wallet);



        }
    }
}
