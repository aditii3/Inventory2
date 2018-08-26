package com.example.android.store.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StoreContract {
    private StoreContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.store";
    public static final String PATH_STORE = "store";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE;

    public static final class StoreEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STORE);
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_TYPE = "type";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NO = "supplier_phone";


        public static final int TYPE_FRUITS_AND_VEG = 0;
        public static final int TYPE_STAPLE = 1;
        public static final int TYPE_FASHION = 2;
        public static final int TYPE_FOOD = 3;
        public static final int TYPE_HOME_NEED = 4;
        public static final int TYPE_HOME_CARE = 5;
        public static final int TYPE_ELECTRONICS = 6;

        public static boolean isValidType(int type) {
            if (type == TYPE_FRUITS_AND_VEG || type == TYPE_STAPLE || type == TYPE_FASHION || type == TYPE_FOOD || type == TYPE_HOME_NEED || type == TYPE_HOME_CARE || type == TYPE_ELECTRONICS) {
                return true;
            }
            return false;
        }
    }
}
