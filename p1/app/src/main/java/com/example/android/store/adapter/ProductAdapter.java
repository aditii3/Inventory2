package com.example.android.store.adapter;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.store.R;
import com.example.android.store.data.StoreContract.StoreEntry;
import com.example.android.store.ui.MainActivity;
import com.example.android.store.utils.MySparseBoleanArray;
import com.example.android.store.utils.MySparseLongArray;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductAdapter extends CursorAdapter {
    private MySparseLongArray selectedId;
    private MySparseBoleanArray selected;
    int myColor = Color.parseColor("#FFFDE7");

    public ProductAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        selected = new MySparseBoleanArray();
        selectedId = new MySparseLongArray();

    }

    @Override
    public View newView(Context context, final Cursor cursor, final ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        ProductHolder holder = new ProductHolder(v);
        v.setTag(holder);

        return v;
    }


    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        ProductHolder holder = (ProductHolder) view.getTag();
        int idIndex = cursor.getColumnIndex(StoreEntry.COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
        int typeIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_TYPE);
        final int quantityIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);
        int priceIndex = cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE);
        final int rowId = cursor.getInt(idIndex);
        int type = cursor.getInt(typeIndex);
        final int quantity = cursor.getInt(quantityIndex);
        setType(type, holder.productType, context);
        holder.productName.setText(cursor.getString(nameIndex));
        if (quantity > 0) {
            holder.productQuantity.setText(String.format(context.getString(R.string.in_stock), quantity));
        } else {
            holder.productQuantity.setText(String.format(context.getString(R.string.out_stock), quantity));
        }
        holder.productPrice.setText(String.format(context.getString(R.string.price_text), cursor.getInt(priceIndex)));
        holder.shipProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    MainActivity activity = (MainActivity) context;
                    activity.sellProduct(rowId, quantity);
                }
            }
        });
        Glide.with(context)
                .load(Uri.parse(cursor.getString(cursor.getColumnIndex(StoreEntry.COLUMN_PRODUCT_IMAGE))))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.productImage);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        if (selected.size() > 0 && selected.get(position)) {
            v.setBackgroundColor(0x9934B5E4);

        } else {
            v.setBackgroundColor(myColor);
        }
        return v;
    }

    private void setType(int type, TextView view, Context context) {
        switch (type) {
            case 0:
                view.setText(context.getString(R.string.fruits));
                break;
            case 1:
                view.setText(context.getString(R.string.staple));
                break;
            case 2:
                view.setText(context.getString(R.string.fashion));
                break;
            case 3:
                view.setText(context.getString(R.string.food));
                break;
            case 4:
                view.setText(context.getString(R.string.home_need));
                break;
            case 5:
                view.setText(context.getString(R.string.home_care));
                break;
            case 6:
                view.setText(context.getString(R.string.electronics));
                break;
            default:
                throw new IllegalArgumentException("Illegal product type");
        }

    }


    class ProductHolder {
        @BindView(R.id.product_name)
        TextView productName;
        @BindView(R.id.product_type)
        TextView productType;
        @BindView(R.id.product_quantity)
        TextView productQuantity;
        @BindView(R.id.ship_product)
        ImageView shipProduct;
        @BindView(R.id.product_price)
        TextView productPrice;
        @BindView(R.id.product_image)
        ImageView productImage;


        ProductHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    public void toggleSelection(int position, long id) {
        selectView(position, !selected.get(position), id);
    }


    public void selectView(int position, boolean val, long id) {

        if (val) {
            selected.put(position, val);
            selectedId.put(position, id);

        } else {
            selected.delete(position);
            selectedId.delete(position);

        }
        notifyDataSetChanged();
    }

    public void setSelected(MySparseBoleanArray selected) {
        this.selected = selected;
    }


    public int getItemCount() {
        return getCursor().getCount();
    }

    public MySparseBoleanArray getSelected() {
        return selected;
    }

    public void clear() {
        selected.clear();
    }

    public MySparseLongArray getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(MySparseLongArray array) {
        selectedId = array;
    }


}
