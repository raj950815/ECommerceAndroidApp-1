package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingCartAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.Callback {

    private static final String STATE_SALES_ORDER_ID = "state_sales_order_id";
    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";
    private static final String STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION = "STATE_LISTVIEW_CURRENT_FIRST_POSITION";

    private User mCurrentUser;
    private int mSalesOrderId;
    private int mBusinessPartnerId;
    private ShoppingCartAdapter mShoppingCartAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mRecyclerViewCurrentFirstPosition;
    private ArrayList<OrderLine> mOrderLines;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        mCurrentUser = Utils.getCurrentUser(getContext());

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_SALES_ORDER_ID)){
                mSalesOrderId = savedInstanceState.getInt(STATE_SALES_ORDER_ID);
            }
            if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)){
                mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
            }
            if(savedInstanceState.containsKey(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION)){
                mRecyclerViewCurrentFirstPosition = savedInstanceState.getInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_SALES_ORDER_ID)){
                mSalesOrderId = getActivity().getIntent().getExtras()
                        .getInt(ShoppingCartActivity.KEY_SALES_ORDER_ID);
            }
            if(getActivity().getIntent().getExtras().containsKey(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID)){
                mBusinessPartnerId = getActivity().getIntent().getExtras()
                        .getInt(ShoppingCartActivity.KEY_BUSINESS_PARTNER_ID);
            }
        }

        if (mSalesOrderId>0) {
            mOrderLines = (new OrderLineDB(getContext(), mCurrentUser)).getOrderLinesBySalesOrderId(mSalesOrderId);
        }else {
            mOrderLines = (new OrderLineDB(getContext(), mCurrentUser)).getShoppingCart();
        }

        if (mOrderLines==null || mOrderLines.size()==0) {
            view.findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            view.findViewById(R.id.shoppingCart_items_list).setVisibility(View.GONE);
            view.findViewById(R.id.shoppingCart_data_linearLayout).setVisibility(View.GONE);
        } else {
            mShoppingCartAdapter = new ShoppingCartAdapter(getContext(), this, mOrderLines, mSalesOrderId <= 0,  mCurrentUser);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.shoppingCart_items_list);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLinearLayoutManager);
            recyclerView.setAdapter(mShoppingCartAdapter);

            if (mRecyclerViewCurrentFirstPosition!=0) {
                recyclerView.scrollToPosition(mRecyclerViewCurrentFirstPosition);
            }

            view.findViewById(R.id.proceed_to_checkout_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(getContext())
                                    .setMessage(R.string.proceed_to_checkout_question)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            OrderDB orderDB = new OrderDB(getContext(), mCurrentUser);
                                            String result;
                                            if (mSalesOrderId > 0) {
                                                result = orderDB.createOrderFromOrderLines(mSalesOrderId, mBusinessPartnerId, mOrderLines);
                                            } else {
                                                result = orderDB.createOrderFromShoppingCart();
                                            }
                                            if(result == null){
                                                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                                                intent.putExtra(OrderDetailActivity.KEY_ORDER, orderDB.getLastFinalizedOrder());
                                                startActivity(intent);
                                                getActivity().finish();
                                            }else{
                                                new AlertDialog.Builder(getContext())
                                                        .setMessage(result)
                                                        .setNeutralButton(android.R.string.ok, null)
                                                        .show();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        }
                    });
            ((TextView) view.findViewById(R.id.total_lines))
                    .setText(getString(R.string.order_lines_number, String.valueOf(mOrderLines.size())));
        }
        return view;
    }

    @Override
    public void updateQtyOrdered(OrderLine orderLine) {
        DialogUpdateQuantityOrdered dialogUpdateQuantityOrdered =
                DialogUpdateQuantityOrdered.newInstance(orderLine, mSalesOrderId <= 0, mCurrentUser);
        dialogUpdateQuantityOrdered.setTargetFragment(this, 0);
        dialogUpdateQuantityOrdered.show(getActivity().getSupportFragmentManager(),
                DialogUpdateQuantityOrdered.class.getSimpleName());
    }

    public void reloadShoppingCart(){
        if (mSalesOrderId <= 0){
            mOrderLines = (new OrderLineDB(getActivity(), mCurrentUser)).getShoppingCart();
        }
        mShoppingCartAdapter.setData(mOrderLines);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SALES_ORDER_ID, mSalesOrderId);
        outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
        if(mLinearLayoutManager!=null) {
            if (mLinearLayoutManager instanceof GridLayoutManager) {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstVisibleItemPosition());
            } else {
                outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION,
                        mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
            }
        } else {
            outState.putInt(STATE_RECYCLERVIEW_CURRENT_FIRST_POSITION, mRecyclerViewCurrentFirstPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
