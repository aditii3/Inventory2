package com.example.android.store.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseLongArray;

public class MySparseLongArray extends SparseLongArray implements Parcelable {
    public MySparseLongArray() {
        super();
    }

    protected MySparseLongArray(Parcel in) {
        long[] valus = in.createLongArray();
        int[] keys = in.createIntArray();
        for (int i = 0; i < valus.length; i++) {
            append(keys[i], valus[i]);
        }
    }

    public static final Creator<MySparseLongArray> CREATOR = new Creator<MySparseLongArray>() {
        @Override
        public MySparseLongArray createFromParcel(Parcel in) {
            return new MySparseLongArray(in);
        }

        @Override
        public MySparseLongArray[] newArray(int size) {
            return new MySparseLongArray[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        long[] values = new long[size()];
        int[] keys = new int[size()];
        for (int i = 0; i < size(); i++) {
            values[i] = valueAt(i);
            keys[i] = keyAt(i);
        }
        dest.writeLongArray(values);
        dest.writeIntArray(keys);
    }
}
