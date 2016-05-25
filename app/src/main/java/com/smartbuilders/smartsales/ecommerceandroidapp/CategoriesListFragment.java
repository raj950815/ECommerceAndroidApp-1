package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.CategoryAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductCategoryDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;

/**
 * Created by Alberto on 26/3/2016.
 */
public class CategoriesListFragment extends Fragment {

    private ListView mListView;
    private CategoryAdapter mCategoryAdapter;
    private User mCurrentUser;

    public interface Callback {
        public void onItemSelected(ProductCategory productCategory);
        public void onItemLongSelected(ProductCategory productCategory);
    }

    public CategoriesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_list, container, false);

        if(getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null){
            if(getActivity().getIntent().getExtras().containsKey(CategoriesListActivity.KEY_CURRENT_USER)){
                mCurrentUser = getActivity().getIntent().getExtras().getParcelable(CategoriesListActivity.KEY_CURRENT_USER);
            }
        }

        ProductCategoryDB categoryDB = new ProductCategoryDB(getContext(), mCurrentUser);

        mCategoryAdapter = new CategoryAdapter(getActivity(), categoryDB.getActiveProductCategories());

        mListView = (ListView) rootView.findViewById(R.id.categories_list);
        mListView.setAdapter(mCategoryAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                ProductCategory productCategory = (ProductCategory) adapterView.getItemAtPosition(position);
                if (productCategory != null) {
                    ((Callback) getActivity()).onItemSelected(productCategory);
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ProductCategory productCategory = (ProductCategory) parent.getItemAtPosition(position);
                if (productCategory != null) {
                    ((Callback) getActivity()).onItemLongSelected(productCategory);
                }
                return true;
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setElevation(10f);
        }

        return rootView;
    }
}
