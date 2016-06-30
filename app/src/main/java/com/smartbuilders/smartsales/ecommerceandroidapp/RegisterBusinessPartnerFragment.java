package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.UserBusinessPartnerBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by stein on 4/6/2016.
 */
public class RegisterBusinessPartnerFragment extends Fragment {

    private static final String STATE_BUSINESS_PARTNER_ID = "state_business_partner_id";
    private static final String STATE_BUSINESS_PARTNER = "state_business_partner";
    private static final String STATE_ORIGINAL_TAX_ID = "state_original_tax_id";

    private User mCurrentUser;
    private int mBusinessPartnerId;
    private BusinessPartner mUserBusinessPartner;
    private String mOriginalTaxId;

    public interface Callback {
        void onBusinessPartnerRegistered();
        void onBusinessPartnerUpdated();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView  = inflater.inflate(R.layout.fragment_register_business_partner, container, false);

        new Thread() {
            @Override
            public void run() {
                try {
                    if (getArguments()!=null) {
                        if (getArguments().containsKey(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = getArguments().getInt(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    } else if (getActivity().getIntent()!=null && getActivity().getIntent().getExtras()!=null) {
                        if(getActivity().getIntent().getExtras().containsKey(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID)) {
                            mBusinessPartnerId = getActivity().getIntent().getExtras().getInt(RegisterBusinessPartnerActivity.KEY_BUSINESS_PARTNER_ID);
                        }
                    }

                    if(savedInstanceState!=null){
                        if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER_ID)){
                            mBusinessPartnerId = savedInstanceState.getInt(STATE_BUSINESS_PARTNER_ID);
                        }
                        if(savedInstanceState.containsKey(STATE_BUSINESS_PARTNER)){
                            mUserBusinessPartner = savedInstanceState.getParcelable(STATE_BUSINESS_PARTNER);
                        }
                        if(savedInstanceState.containsKey(STATE_ORIGINAL_TAX_ID)){
                            mOriginalTaxId = savedInstanceState.getString(STATE_ORIGINAL_TAX_ID);
                        }
                    }

                    mCurrentUser = Utils.getCurrentUser(getContext());
                    if(mBusinessPartnerId>0 && mUserBusinessPartner==null){
                        mUserBusinessPartner = (new UserBusinessPartnerDB(getContext(), mCurrentUser))
                                .getActiveUserBusinessPartnerById(mBusinessPartnerId);
                        mOriginalTaxId = mUserBusinessPartner.getTaxId();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final EditText businessPartnerName = (EditText) rootView.findViewById(R.id.business_partner_name_editText);
                                final EditText businessPartnerCommercialName = (EditText) rootView.findViewById(R.id.business_partner_commercial_name_editText);
                                final EditText businessPartnerTaxId = (EditText) rootView.findViewById(R.id.business_partner_tax_id_editText);
                                final EditText businessPartnerAddress = (EditText) rootView.findViewById(R.id.business_partner_address_editText);
                                final EditText businessPartnerContactPerson = (EditText) rootView.findViewById(R.id.business_partner_contact_person_name_editText);
                                final EditText businessPartnerEmailAddress = (EditText) rootView.findViewById(R.id.business_partner_email_address_editText);
                                final EditText businessPartnerPhoneNumber = (EditText) rootView.findViewById(R.id.business_partner_phone_number_editText);

                                Button saveButton = (Button) rootView.findViewById(R.id.save_button);

                                if (mUserBusinessPartner !=null){
                                    businessPartnerName.setText(mUserBusinessPartner.getName());
                                    businessPartnerName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mUserBusinessPartner.setName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerCommercialName.setText(mUserBusinessPartner.getCommercialName());
                                    businessPartnerCommercialName.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mUserBusinessPartner.setCommercialName(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerTaxId.setText(mUserBusinessPartner.getTaxId());
                                    businessPartnerTaxId.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mUserBusinessPartner.setTaxId(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerAddress.setText(mUserBusinessPartner.getAddress());
                                    businessPartnerAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mUserBusinessPartner.setAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerContactPerson.setText(mUserBusinessPartner.getContactPerson());
                                    businessPartnerContactPerson.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mUserBusinessPartner.setContactPerson(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerEmailAddress.setText(mUserBusinessPartner.getEmailAddress());
                                    businessPartnerEmailAddress.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mUserBusinessPartner.setEmailAddress(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    businessPartnerPhoneNumber.setText(mUserBusinessPartner.getPhoneNumber());
                                    businessPartnerPhoneNumber.addTextChangedListener(new TextWatcher(){
                                        public void afterTextChanged(Editable s) {
                                            mUserBusinessPartner.setPhoneNumber(s.toString());
                                        }
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                                    });

                                    saveButton.setText(getString(R.string.update));
                                }

                                if (saveButton!=null) {
                                    saveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(getContext(), mCurrentUser);
                                            if (mUserBusinessPartner!=null) {
                                                if(!mUserBusinessPartner.getTaxId().equals(mOriginalTaxId)){
                                                    String result = UserBusinessPartnerBR.validateBusinessPartner(mUserBusinessPartner,
                                                            getContext(), mCurrentUser);
                                                    if(result==null){
                                                        result = userBusinessPartnerDB.updateUserBusinessPartner(mUserBusinessPartner);
                                                        if (result==null){
                                                            mOriginalTaxId = mUserBusinessPartner.getTaxId();
                                                            ((Callback) getActivity()).onBusinessPartnerUpdated();
                                                            Toast.makeText(getContext(), getString(R.string.business_partner_updated_successfully), Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                        }
                                                    }else{
                                                        new AlertDialog.Builder(getContext())
                                                                .setMessage(result)
                                                                .setNeutralButton(R.string.accept, null)
                                                                .show();
                                                    }
                                                }else{
                                                    String result = userBusinessPartnerDB.updateUserBusinessPartner(mUserBusinessPartner);
                                                    if (result==null){
                                                        ((Callback) getActivity()).onBusinessPartnerUpdated();
                                                        Toast.makeText(getContext(), getString(R.string.business_partner_updated_successfully), Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            } else {
                                                BusinessPartner userBusinessPartner = new BusinessPartner();
                                                userBusinessPartner.setName(businessPartnerName.getText().toString());
                                                userBusinessPartner.setCommercialName(businessPartnerCommercialName.getText().toString());
                                                userBusinessPartner.setTaxId(businessPartnerTaxId.getText().toString());
                                                userBusinessPartner.setAddress(businessPartnerAddress.getText().toString());
                                                userBusinessPartner.setContactPerson(businessPartnerContactPerson.getText().toString());
                                                userBusinessPartner.setEmailAddress(businessPartnerEmailAddress.getText().toString());
                                                userBusinessPartner.setPhoneNumber(businessPartnerPhoneNumber.getText().toString());
                                                String result = UserBusinessPartnerBR.validateBusinessPartner(userBusinessPartner,
                                                        getContext(), mCurrentUser);
                                                if (result==null) {
                                                    result = userBusinessPartnerDB.registerUserBusinessPartner(userBusinessPartner);
                                                    if (result==null){
                                                        ((Callback) getActivity()).onBusinessPartnerRegistered();
                                                    } else {
                                                        Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    new AlertDialog.Builder(getContext())
                                                            .setMessage(result)
                                                            .setNeutralButton(R.string.accept, null)
                                                            .show();
                                                }
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                rootView.findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                rootView.findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }.start();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUSINESS_PARTNER_ID, mBusinessPartnerId);
        outState.putParcelable(STATE_BUSINESS_PARTNER, mUserBusinessPartner);
        outState.putString(STATE_ORIGINAL_TAX_ID, mOriginalTaxId);
        super.onSaveInstanceState(outState);
    }
}
