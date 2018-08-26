package com.example.android.store.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

public class StoreProvider extends ContentProvider {
    StoreDBHelper helper;
    private static final int PRODUCTS = 3;
    private static final int PRODUCT_ID = 4;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_STORE, PRODUCTS);
        matcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_STORE + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new StoreDBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor c;
        int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                c = database.query(StoreContract.StoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = database.query(StoreContract.StoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Query for an invalid URI" + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return StoreContract.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return StoreContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    private void insertProduct(ContentValues cv) {
        if (cv.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME) == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        if (cv.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME) == null) {
            throw new IllegalArgumentException("Supplier name required");
        }
        Integer price = cv.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price <= 0) {
            throw new IllegalArgumentException("Invalid product price");
        }
        Integer type = cv.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_TYPE);
        if (type == null || !StoreContract.StoreEntry.isValidType(type)) {
            throw new IllegalArgumentException("Invalid product type");
        }
        Integer quantity = cv.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity for product");
        }
        String no = cv.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO);
        if (!TextUtils.isDigitsOnly(no)) {
            throw new IllegalArgumentException("Supplier phone no. is invalid");
        }
    }


    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri newUri;
        insertProduct(values);
        SQLiteDatabase database = helper.getWritableDatabase();
        int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                long id = database.insert(StoreContract.StoreEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    Toast.makeText(getContext(), "Failed to insert product", Toast.LENGTH_SHORT).show();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                newUri = ContentUris.withAppendedId(uri, id);
                return newUri;
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int match = matcher.match(uri);
        int rowsAffected;
        switch (match) {
            case PRODUCTS:
                rowsAffected = database.delete(StoreContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsAffected = database.delete(StoreContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI for deletion " + uri);
        }
        if (rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAffected;

    }

    private void updateProduct(ContentValues cv) {
        if (cv.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME)) {
            if (cv.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME) == null) {
                throw new IllegalArgumentException("Product name required");
            }
            if (cv.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE)) {
                Integer price = cv.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE);
                if (price != null && price <= 0) {
                    throw new IllegalArgumentException("Invalid price for product");
                }
            }
            if (cv.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_TYPE)) {
                Integer type = cv.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_TYPE);
                if (type == null || !StoreContract.StoreEntry.isValidType(type)) {
                    throw new IllegalArgumentException("Invalid type for product");
                }
            }
            if (cv.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY)) {
                Integer quantity = cv.getAsInteger(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY);
                if (quantity == null || quantity < 0) {
                    throw new IllegalArgumentException("Invalid quantity for product");
                }
            }
            if (cv.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
                if (cv.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME) == null) {
                    throw new IllegalArgumentException("Invalid supplier name");
                }
            }
            if (cv.containsKey(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO)) {
                String no = cv.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO);
                if (!TextUtils.isDigitsOnly(no)) {
                    throw new IllegalArgumentException("Invalid supplier no");
                }
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        int rowsUpdated;
        updateProduct(values);
        SQLiteDatabase database = helper.getWritableDatabase();
        int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsUpdated = database.update(StoreContract.StoreEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(StoreContract.StoreEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Not a valid request for updation" + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
