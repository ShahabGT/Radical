package ir.radical_app.radical.arch.BuyHistory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.DateConverter;
import ir.radical_app.radical.classes.MyToast;
import ir.radical_app.radical.classes.MyUtils;
import ir.radical_app.radical.dialogs.ReceiptDialog;
import ir.radical_app.radical.R;

public class BuyHistoryAdapter extends PagedListAdapter<BuyItem, BuyHistoryAdapter.ItemViewHolder> {

    private Context mCtx;

    public BuyHistoryAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.row_buy_history, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        final BuyItem item = getItem(position);

        if (item != null) {
            holder.layout.setVisibility(View.GONE);

            holder.name.setText(item.getName());
            holder.total.setText(mCtx.getString(R.string.amount_model,MyUtils.Companion.moneySeparator(item.getTotalAmount())));
            holder.discount.setText(mCtx.getString(R.string.amount_model,MyUtils.Companion.moneySeparator(item.getTotalDiscount())));
            holder.pay.setText(mCtx.getString(R.string.amount_model,MyUtils.Companion.moneySeparator(item.getTotalPay())));
            holder.pay2.setText(mCtx.getString(R.string.amount_model,MyUtils.Companion.moneySeparator(item.getTotalPay())));
            String date = item.getDate();
            DateConverter dateConverter = new DateConverter();
            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
            holder.date.setText(mCtx.getString(R.string.messages_model3,dateConverter.toString(),date.substring(11)));
            holder.shopBuyId.setText(item.getShopBuyId());

            holder.layout.setOnClickListener(View-> {
                    if(!MyUtils.Companion.checkInternet(mCtx)){
                        MyToast.Companion.create(mCtx,mCtx.getString(R.string.internet_error));
                        return;
                    }
                    showReceiptDialog(Integer.parseInt(item.getTotalAmount()),Integer.parseInt(item.getTotalDiscount()),
                            Integer.parseInt(item.getTotalPay()),item.getName(),item.getShopBuyId());
            });

            holder.itemView.setOnClickListener(View-> {
                    if(holder.layout.getVisibility()==View.GONE){
                        holder.icon.setImageResource(R.drawable.vector_top);
                        holder.layout.setVisibility(View.VISIBLE);
                    }else{
                        holder.icon.setImageResource(R.drawable.vector_bottom);
                        holder.layout.setVisibility(View.GONE);

                    }
            });

        }
    }
    private void showReceiptDialog(int amount,int discount,int pay,String shopName,String shopBuyId) {
        ReceiptDialog receiptDialog = new ReceiptDialog(mCtx);
        receiptDialog.setAmount(amount);
        receiptDialog.setDiscount(discount);
        receiptDialog.setPay(pay);
        receiptDialog.setShopName(shopName);
        receiptDialog.setShopBuyId(shopBuyId);
        receiptDialog.setFromHistory(true);
        receiptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        receiptDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        receiptDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        receiptDialog.show();
        Window window = receiptDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }


    private static DiffUtil.ItemCallback<BuyItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BuyItem>() {
                @Override
                public boolean areItemsTheSame(BuyItem oldItem, BuyItem newItem) {
                    return oldItem.getShopId().equals(newItem.getShopId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(BuyItem oldItem, BuyItem newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name,date,total,discount,pay,pay2,shopBuyId;
        private ImageView icon;
        private ConstraintLayout layout;


        ItemViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.buy_row_name);
            icon = v.findViewById(R.id.buy_row_icon);
            layout = v.findViewById(R.id.buy_row_layout);
            date = v.findViewById(R.id.buy_row_date);
            total = v.findViewById(R.id.buy_row_total);
            discount = v.findViewById(R.id.buy_row_discount);
            pay = v.findViewById(R.id.buy_row_pay);
            pay2 = v.findViewById(R.id.buy_row_pay2);
            shopBuyId = v.findViewById(R.id.buy_row_shop_buy_id);
        }
    }
}
