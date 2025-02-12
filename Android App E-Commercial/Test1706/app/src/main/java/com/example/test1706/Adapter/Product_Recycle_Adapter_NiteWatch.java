package com.example.test1706.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.test1706.DetailsProductActivity;
import com.example.test1706.R;
import com.example.test1706.SQLite.CartSqliteHelper;
import com.example.test1706.model.Product;
import com.example.test1706.SQLite.ProductSqliteHelper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Product_Recycle_Adapter_NiteWatch extends RecyclerView.Adapter<Product_Recycle_Adapter_NiteWatch.ViewHolder> {
    private List<Product> list_data;
    private Context mContext;
    private static final String TAG = "Product_Recycle_Adapter";
    private boolean isNight;
    private int currentlayout;
    private TextView textCartItemCount;
    private AppBarLayout appBarLayout;

    public void setAppBarLayout(AppBarLayout appBarLayout) {
        this.appBarLayout = appBarLayout;
    }

    public void setTextCartItemCount(TextView textCartItemCount) {
        this.textCartItemCount = textCartItemCount;
    }

    public boolean isNight() {
        return isNight;
    }

    public void setNight(boolean night) {
        isNight = night;
    }

    public Product_Recycle_Adapter_NiteWatch(Context mContext, List<Product> list_data, int currentlayout) {
        this.list_data = list_data;
        this.mContext = mContext;
        this.currentlayout = currentlayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(currentlayout, viewGroup, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
            final Product productt = list_data.get(i % list_data.size());
            if (productt == null) {
                return;
            }
            Glide.with(mContext.getApplicationContext())
                    .load(list_data.get(i % list_data.size()).getImage())
                    .apply(new RequestOptions().fitCenter())
                    .into(viewHolder.mImage);
            Glide.with(mContext.getApplicationContext())
                    .load(list_data.get(i % list_data.size()).getImage_Night())
                    .apply(new RequestOptions().fitCenter())
                    .into(viewHolder.mImageNight);

            long time = 3000;
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(time);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setStartOffset(50);
            fadeOut.setDuration(time);

            if (isNight && viewHolder.mImage.getVisibility() == View.VISIBLE) {
                viewHolder.mImage.setAnimation(fadeOut);
                viewHolder.mImage.setVisibility(View.INVISIBLE);
                viewHolder.mImageNight.setVisibility(View.VISIBLE);
                viewHolder.mImageNight.setAnimation(fadeIn);
                viewHolder.mlayout_horizontal_nitewatch_item.setBackgroundColor(mContext.getResources().getColor(R.color.clearblack));

            } else if (!isNight && viewHolder.mImageNight.getVisibility() == View.VISIBLE) {
                viewHolder.mImageNight.setAnimation(fadeOut);
                viewHolder.mImageNight.setVisibility(View.INVISIBLE);
                viewHolder.mImage.setVisibility(View.VISIBLE);
                viewHolder.mImage.setAnimation(fadeIn);
                viewHolder.mlayout_horizontal_nitewatch_item.setBackgroundColor(mContext.getResources().getColor(R.color.black_cardview_nitewatch));
            }

            viewHolder.mName.setText(list_data.get(i % list_data.size()).getProduct_Name());
            viewHolder.mCategory.setText(list_data.get(i % list_data.size()).getCategory());


            viewHolder.mbtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductSqliteHelper productSqliteHelper = new ProductSqliteHelper(mContext);
                    productSqliteHelper.addProduct(productt);

                    Intent intent = new Intent(mContext, DetailsProductActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ProductName", productt.getProduct_Name());
                    b.putString("ProductCategory", productt.getCategory());
                    intent.putExtras(b);
                    mContext.startActivity(intent);
                }
            });


            viewHolder.mbtnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartSqliteHelper cartSqliteHelper = new CartSqliteHelper(mContext);

                    if (!cartSqliteHelper.CheckExists(productt)) {
                        cartSqliteHelper.addCart(productt);
                    } else {
                        cartSqliteHelper.PlusOneQuantity(productt);
                    }
                    setupBadge(cartSqliteHelper.getCartQuantityCount());

                    final Animation slideDown_toolbar = AnimationUtils.loadAnimation(mContext, R.anim.toolbar_slidedown);
                    if (appBarLayout != null) {
                        if (appBarLayout.getVisibility() == View.INVISIBLE) {
                            appBarLayout.setVisibility(View.VISIBLE);
                            appBarLayout.startAnimation(slideDown_toolbar);
                        }
                    }
                    Toast.makeText(mContext, mContext.getString(R.string.dathem) + productt.getProduct_Name(), Toast.LENGTH_SHORT).show();
                }
            });

            if (viewHolder.tv_creation_time_viewedproduct != null) {
                viewHolder.tv_creation_time_viewedproduct.setText(ThoiGianChat(productt.getCreateDate()));
            }
            //giá
            viewHolder.mPrice.setText(((String) ("$" + list_data.get(i % list_data.size()).getPrice())));
            viewHolder.tv_discount_percent.setText(String.valueOf("-" + productt.getDiscount() + "%"));
            int price_not_discount = productt.getPrice() + (productt.getPrice() * productt.getDiscount() / 100);
            viewHolder.tv_realprice.setText(String.valueOf("$" + price_not_discount));


    }

    private String ThoiGianChat(long date) {
        String thoigian = "";
        Date datetime = new Date();
        datetime.setTime(date);
        Date currentday = new Date();
        long diffInMillies = Math.abs(datetime.getTime() - currentday.getTime());
        long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff / (60 * 60 * 24 * 30) > 0) {
            thoigian = Math.round(diff / (60 * 60 * 24 * 30)) + mContext.getString(R.string.thangtruoc);
        } else if (diff / (60 * 60 * 24) > 0) {
            thoigian = Math.round(diff / (60 * 60 * 24)) + mContext.getString(R.string.ngaytruoc);
        } else if (diff / (60 * 60) > 0) {
            thoigian = Math.round(diff / (60 * 60)) + mContext.getString(R.string.giotruoc);
        } else if (diff / (60) > 0) {
            thoigian = Math.round(diff / (60)) + mContext.getString(R.string.phuttruoc);
        } else if (diff > 0) {
            thoigian = Math.round(diff) + mContext.getString(R.string.giaytruoc);
        } else {
            thoigian = mContext.getString(R.string.vuaxong);
        }

        return thoigian;
    }

    private void setupBadge(int mCartItemCount) {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list_data == null ? 0 : list_data.size() * 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mPrice, mCategory, tv_creation_time_viewedproduct, tv_discount_percent, tv_realprice;
        ImageView mImage, mImageNight;
        Button mbtnView, mbtnCart;
        LinearLayout mlayout_horizontal_nitewatch_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_realprice = (TextView) itemView.findViewById(R.id.tv_realprice);
            tv_discount_percent = (TextView) itemView.findViewById(R.id.tv_discount_percent);
            tv_creation_time_viewedproduct = (TextView) itemView.findViewById(R.id.tv_creation_time_viewedproduct);
            mlayout_horizontal_nitewatch_item = (LinearLayout) itemView.findViewById(R.id.layout_horizontal_nitewatch_item);
            mName = (TextView) itemView.findViewById(R.id.tv_horizontal_name);
            mPrice = (TextView) itemView.findViewById(R.id.tv_horizontal_price);
            mCategory = (TextView) itemView.findViewById(R.id.tv_horizontal_category);
            mImage = (ImageView) itemView.findViewById(R.id.img_horizontal_product);
            mbtnView = (Button) itemView.findViewById(R.id.btnview_horizontal_nitewatch);
            mbtnCart = (Button) itemView.findViewById(R.id.btncart_horizontal_nitewatch);
            mImageNight = (ImageView) itemView.findViewById(R.id.img_horizontal_product_night);
        }

    }
}
