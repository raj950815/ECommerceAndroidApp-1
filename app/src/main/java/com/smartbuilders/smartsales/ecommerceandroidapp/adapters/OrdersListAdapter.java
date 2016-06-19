package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class OrdersListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Order> mDataset;

    public OrdersListAdapter(Context context, ArrayList<Order> data) {
        mContext = context;
        mDataset = data;
    }

    @Override
    public int getCount() {
        if (mDataset!=null) {
            return mDataset.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if(view==null){//si la vista es null la crea sino la reutiliza
            view = LayoutInflater.from(mContext).inflate(R.layout.orders_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.orderNumber.setText(mContext.getString(R.string.order_number, mDataset.get(position).getOrderNumber()));
        viewHolder.orderDate.setText(mContext.getString(R.string.order_date, mDataset.get(position).getCreatedStringFormat()));
        viewHolder.orderLinesNumber.setText(mContext.getString(R.string.order_lines_number,
                String.valueOf(mDataset.get(position).getLinesNumber())));

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public TextView orderNumber;
        public TextView orderDate;
        public TextView orderLinesNumber;

        public ViewHolder(View v) {
            orderNumber = (TextView) v.findViewById(R.id.order_number_tv);
            orderDate = (TextView) v.findViewById(R.id.order_date_tv);
            orderLinesNumber = (TextView) v.findViewById(R.id.order_lines_number_tv);
        }
    }

    public void setData (ArrayList<Order> orders){
        mDataset = orders;
        notifyDataSetChanged();
    }
}