package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ProductRecyclerViewAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductsListFragment extends Fragment {

    private static final String STATE_CURRENT_USER = "state_current_user";

    private ProgressDialog waitPlease;
    private User mCurrentUser;
    private int productCategoryId;
    private int productSubCategoryId;
    private int productBrandId;
    private int productId;
    private String productName;
    private ArrayList<Product> products;
    private ProductRecyclerViewAdapter mProductRecyclerViewAdapter;

    public ProductsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_products_list, container, false);

        if( savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
            if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_ID)){
                productId = getActivity().getIntent().getExtras().getInt(ProductsListActivity.KEY_PRODUCT_ID);
            }
            if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_CURRENT_USER)) {
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(ProductsListActivity.KEY_CURRENT_USER);
            }
            if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID)){
                productCategoryId = getActivity().getIntent().getExtras().getInt(ProductsListActivity.KEY_PRODUCT_CATEGORY_ID);
            }
            if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID)){
                productSubCategoryId = getActivity().getIntent().getExtras().getInt(ProductsListActivity.KEY_PRODUCT_SUBCATEGORY_ID);
            }
            if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_BRAND_ID)){
                productBrandId = getActivity().getIntent().getExtras().getInt(ProductsListActivity.KEY_PRODUCT_BRAND_ID);
            }
            if(getActivity().getIntent().getExtras().containsKey(ProductsListActivity.KEY_PRODUCT_NAME)){
                productName = getActivity().getIntent().getExtras().getString(ProductsListActivity.KEY_PRODUCT_NAME);
            }
        }

        waitPlease = ProgressDialog.show(getContext(), getString(R.string.loading), getString(R.string.wait_please), true, false);
        new Thread() {
            @Override
            public void run() {
                try {
                    if (productCategoryId != 0) {
                        products = new ProductDB(getContext(), mCurrentUser).getProductsByCategoryId(productCategoryId);
                    } else if (productSubCategoryId != 0) {
                        products = new ProductDB(getContext(), mCurrentUser).getProductsBySubCategoryId(productSubCategoryId);
                    } else if (productBrandId != 0) {
                        products = (new ProductDB(getContext(), mCurrentUser)).getProductsByBrandId(productBrandId);
                    } else if (productName != null) {
                        products = (new ProductDB(getContext(), mCurrentUser)).getProductsByName(productName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(products!=null && !products.isEmpty()) {
                                if (productCategoryId != 0) {
                                    TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    categorySubcategoryResultsTextView.append(new SpannableString(" ("+products.size()+" Resultados) "));
                                } else if (productSubCategoryId != 0) {
                                    TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString(products.get(0).getProductCategory().getDescription() + " >> ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    Spannable wordTwo = new SpannableString(" "+products.get(0).getProductSubCategory().getDescription()+" ");
                                    wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_subcategory)),
                                            0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.append(wordTwo);
                                    categorySubcategoryResultsTextView.append(new SpannableString(" ("+products.size()+" Resultados) "));
                                } else if (productBrandId != 0) {
                                    TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString(products.get(0).getProductBrand().getDescription() + " ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    categorySubcategoryResultsTextView.append(new SpannableString("("+products.size()+" Resultados) "));
                                } else if (productName != null) {
                                    TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                    Spannable word = new SpannableString("Búsqueda: \""+productName+"\" ");
                                    word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_category)), 0,
                                            word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    categorySubcategoryResultsTextView.setText(word);
                                    categorySubcategoryResultsTextView.append(new SpannableString("("+products.size()+" Resultados) "));
                                }
                            } else if(products==null){
                                products = new ArrayList<>();
                            }

                            if(products.isEmpty()){
                                TextView categorySubcategoryResultsTextView = (TextView) view.findViewById(R.id.category_subcategory_results);
                                Spannable word = new SpannableString("No se encontraron productos para mostrar. ");
                                word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0,
                                        word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                categorySubcategoryResultsTextView.append(word);
                            }

                            mProductRecyclerViewAdapter = new ProductRecyclerViewAdapter(getActivity(), products, true,
                                    ProductRecyclerViewAdapter.REDIRECT_PRODUCT_DETAILS, mCurrentUser);

                            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.product_list_result);
                            // use this setting to improve performance if you know that changes
                            // in content do not change the layout size of the RecyclerView
                            mRecyclerView.setHasFixedSize(true);
                            if (useGridView()) {
                                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getSpanCount()));
                            }else{
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            }
                            mRecyclerView.setAdapter(mProductRecyclerViewAdapter);

                            if(!products.isEmpty() && productId != 0){
                                for(int pos = 0; pos < mProductRecyclerViewAdapter.getItemCount(); pos++){
                                    if(mProductRecyclerViewAdapter.getItemId(pos) == productId){
                                        mRecyclerView.scrollToPosition(pos);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                waitPlease.dismiss();
                                waitPlease.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }.start();

        return view;
    }

    private boolean useGridView(){
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }else {
            switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    switch(getResources().getDisplayMetrics().densityDpi) {
                        case DisplayMetrics.DENSITY_LOW:
                            break;
                        case DisplayMetrics.DENSITY_MEDIUM:
                        case DisplayMetrics.DENSITY_HIGH:
                        case DisplayMetrics.DENSITY_XHIGH:
                            return  true;
                    }
                    break;
                //case Configuration.SCREENLAYOUT_SIZE_LARGE:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                //    break;
                //case Configuration.SCREENLAYOUT_SIZE_SMALL:
                //    break;
            }
        }
        return false;
    }

    private int getSpanCount() {
        switch(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                switch(getResources().getDisplayMetrics().densityDpi) {
                    case DisplayMetrics.DENSITY_LOW:
                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                    case DisplayMetrics.DENSITY_HIGH:
                    case DisplayMetrics.DENSITY_XHIGH:
                        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            return 3;
                        }
                        break;
                }
                break;
            //case Configuration.SCREENLAYOUT_SIZE_LARGE:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            //    break;
            //case Configuration.SCREENLAYOUT_SIZE_SMALL:
            //    break;
        }
        return 2;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
