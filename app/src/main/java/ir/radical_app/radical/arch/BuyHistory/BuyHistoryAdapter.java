package ir.radical_app.radical.arch.BuyHistory;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.classes.DateConverter;
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
            holder.name.setText(item.getName());
            holder.total.setText(mCtx.getString(R.string.amount_model,MyUtils.moneySeparator(item.getTotalAmount())));
            holder.discount.setText(mCtx.getString(R.string.amount_model,MyUtils.moneySeparator(item.getTotalDiscount())));
            holder.pay.setText(mCtx.getString(R.string.amount_model,MyUtils.moneySeparator(item.getTotalPay())));
            Uri uri = Uri.parse(mCtx.getString(R.string.main_image_url,item.getShopId()));
            holder.pic.setImageURI(uri);
            String date = item.getDate();
            DateConverter dateConverter = new DateConverter();
            dateConverter.gregorianToPersian(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),Integer.parseInt(date.substring(8,10)));
            holder.date.setText(mCtx.getString(R.string.messages_model,dateConverter.toString(),date.substring(11)));
            holder.shopBuyId.setText(item.getShopBuyId());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                ShopActivity.shopId=holder.id.getText().toString();
//                mCtx.startActivity(new Intent(mCtx, ShopActivity.class));
                    showReceiptDialog(Integer.parseInt(item.getTotalAmount()),Integer.parseInt(item.getTotalDiscount()),
                            Integer.parseInt(item.getTotalPay()),item.getName(),item.getShopBuyId());
                }
            });

        } else {
            Toast.makeText(mCtx, "Item is null", Toast.LENGTH_LONG).show();
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
        receiptDialog.setCanceledOnTouchOutside(false);
        receiptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        receiptDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        receiptDialog.show();
        Window window = receiptDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        receiptDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    receiptDialog.dismiss();
                return true;
            }
        });
    }


    private static DiffUtil.ItemCallback<BuyItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BuyItem>() {
                @Override
                public boolean areItemsTheSame(BuyItem oldItem, BuyItem newItem) {
                    return oldItem.getShopId().equals(newItem.getShopId());
                }

                @Override
                public boolean areContentsTheSame(BuyItem oldItem, BuyItem newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name,date,total,discount,pay,shopBuyId;
            private SimpleDraweeView pic;

         ItemViewHolder(View v) {
            super(v);
            pic = v.findViewById(R.id.buy_row_image);
            name = v.findViewById(R.id.buy_row_name);
            date = v.findViewById(R.id.buy_row_date);
            total = v.findViewById(R.id.buy_row_total);
            discount = v.findViewById(R.id.buy_row_discount);
            pay = v.findViewById(R.id.buy_row_pay);
            shopBuyId = v.findViewById(R.id.buy_row_shop_buy_id);
        }
    }
}
