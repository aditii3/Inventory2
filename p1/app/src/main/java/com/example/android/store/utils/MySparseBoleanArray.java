package com.example.android.store.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;


public class MySparseBoleanArray extends SparseBooleanArray implements Parcelable {
    public MySparseBoleanArray() {
        super();
    }

    protected MySparseBoleanArray(Parcel in) {
        boolean[] values = in.createBooleanArray();
        int[] keys = in.createIntArray();
        for (int i = 0; i < values.length; i++) {
            append(keys[i], values[i]);
        }
    }

    public static final Creator<MySparseBoleanArray> CREATOR = new Creator<MySparseBoleanArray>() {
        @Override
        public MySparseBoleanArray createFromParcel(Parcel in) {
            return new MySparseBoleanArray(in);
        }

        @Override
        public MySparseBoleanArray[] newArray(int size) {
            return new MySparseBoleanArray[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        boolean[] values = new boolean[size()];
        int[] keys = new int[size()];
        for (int i = 0; i < size(); i++) {
            values[i] = valueAt(i);
            keys[i] = keyAt(i);
        }
        dest.writeBooleanArray(values);
        dest.writeIntArray(keys);
    }
}
