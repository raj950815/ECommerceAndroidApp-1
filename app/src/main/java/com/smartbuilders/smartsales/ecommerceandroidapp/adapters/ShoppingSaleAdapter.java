package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.ProductDetailFragment;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 7/4/2016.
 */
public class ShoppingSaleAdapter extends BaseAdapter {

    private Context mContext;
    private Activity mActivity;
    private ArrayList<OrderLine> mDataset;
    private User mCurrentUser;
    private OrderLineDB orderLineDB;

    public ShoppingSaleAdapter(Context context, Activity activity, ArrayList<OrderLine> data, User user) {
        mContext = context;
        mActivity = activity;
        mDataset = data;
        mCurrentUser = user;
        orderLineDB = new OrderLineDB(context, user);
    }

    @Override
    public int getCount() {
        return mDataset.size();
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_sale_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if(mDataset.get(position).getProduct().getImageFileName()!=null){
            Bitmap img = Utils.getThumbByFileName(mContext, mCurrentUser, mDataset.get(position).getProduct().getImageFileName());
            if(img!=null){
                viewHolder.productImage.setImageBitmap(img);
            }else{
                viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
            }
        }else{
            viewHolder.productImage.setImageResource(mDataset.get(position).getProduct().getImageId());
        }

        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductDetailFragment.KEY_PRODUCT, mDataset.get(position).getProduct());
                mContext.startActivity(intent);
            }
        });

        viewHolder.productName.setText(mDataset.get(position).getProduct().getName());

        viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.delete_from_shopping_sale_question,
                                mDataset.get(position).getProduct().getName()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String result = orderLineDB.deleteOrderLine(mDataset.get(position));
                                if(result == null){
                                    mDataset.remove(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        viewHolder.productPrice.setText(String.valueOf(mDataset.get(position).getPrice()));
        viewHolder.productPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.fragment_edit_qty_requested);
                dialog.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv).setVisibility(View.GONE);

                dialog.findViewById(R.id.cancel_dialog_qty_requested_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }
                );

                ((Button) dialog.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button)).setText(R.string.accept);
                dialog.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {

                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
                dialog.setTitle(R.string.price_title);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            mActivity.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        });

        viewHolder.productTaxPercentage.setText(String.valueOf(mDataset.get(position).getTaxPercentage()));
        viewHolder.productTaxPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.fragment_edit_qty_requested);
                dialog.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv).setVisibility(View.GONE);

                dialog.findViewById(R.id.cancel_dialog_qty_requested_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }
                );

                ((Button) dialog.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button)).setText(R.string.accept);
                dialog.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {

                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
                dialog.setTitle(R.string.tax_title);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            mActivity.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        });

        viewHolder.qtyOrdered.setText(String.valueOf(mDataset.get(position).getQuantityOrdered()));
        viewHolder.qtyOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.fragment_edit_qty_requested);

                dialog.findViewById(R.id.product_availability_dialog_edit_qty_requested_tv).setVisibility(View.GONE);

                dialog.findViewById(R.id.cancel_dialog_qty_requested_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }
                );

                ((Button) dialog.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button)).setText(R.string.accept);
                dialog.findViewById(R.id.addtoshoppingcart_dialog_qty_requested_button).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    int qtyRequested = Integer
                                            .valueOf(((EditText) dialog.findViewById(R.id.qty_requested_editText)).getText().toString());
                                    String result = orderLineDB.updateQtyRequested(mDataset.get(position), qtyRequested);
                                    if(result == null){
                                        mDataset.get(position).setQuantityOrdered(qtyRequested);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
                dialog.setTitle(R.string.qty_ordered_title);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            mActivity.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        });


        view.setTag(viewHolder);
        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        // each data item is just a string in this case
        public ImageView productImage;
        public ImageView deleteItem;
        public TextView productName;
        public TextView totalLine;
        public EditText qtyOrdered;
        public EditText productPrice;
        public EditText productTaxPercentage;
        public EditText totalLineAmount;

        public ViewHolder(View v) {
            productImage = (ImageView) v.findViewById(R.id.product_image);
            productName = (TextView) v.findViewById(R.id.product_name);
            totalLine = (TextView) v.findViewById(R.id.total_line);
            productPrice = (EditText) v.findViewById(R.id.product_price);
            productTaxPercentage = (EditText) v.findViewById(R.id.product_tax_percentage);
            totalLineAmount = (EditText) v.findViewById(R.id.total_line_amount);
            deleteItem = (ImageView) v.findViewById(R.id.delete_item_button_img);
            qtyOrdered = (EditText) v.findViewById(R.id.qty_ordered);
        }
    }
}
