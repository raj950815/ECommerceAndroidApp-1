package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AlbertoSarco on 21/10/2016.
 */

public class OrderTracking implements Parcelable {

    private int orderTrackingStateId;
    private String details;
    private Date created;
    private OrderTrackingState orderTrackingState;

    public OrderTracking() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    private OrderTracking(Parcel in) {
        orderTrackingStateId = in.readInt();
        details = in.readString();
        orderTrackingState = in.readParcelable(OrderTrackingState.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderTrackingStateId);
        dest.writeString(details);
        dest.writeParcelable(orderTrackingState, flags);
    }

    public static final Creator<OrderTracking> CREATOR = new Creator<OrderTracking>() {
        @Override
        public OrderTracking createFromParcel(Parcel in) {
            return new OrderTracking(in);
        }

        @Override
        public OrderTracking[] newArray(int size) {
            return new OrderTracking[size];
        }
    };

    public int getOrderTrackingStateId() {
        return orderTrackingStateId;
    }

    public void setOrderTrackingStateId(int orderTrackingStateId) {
        this.orderTrackingStateId = orderTrackingStateId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedStringFormat() {
        try {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                    new Locale("es","VE")).format(getCreated());
        } catch (Exception e) {
            return null;
        }
    }

    public OrderTrackingState getOrderTrackingState() {
        return orderTrackingState;
    }

    public void setOrderTrackingState(OrderTrackingState orderTrackingState) {
        this.orderTrackingState = orderTrackingState;
    }
}