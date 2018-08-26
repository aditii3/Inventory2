package com.example.android.store.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.store.R;
import com.example.android.store.data.StoreContract;
import com.example.android.store.data.StoreContract.StoreEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_CONTENT_LOADER = 1;
    private static final int IMAGE_REQUEST_CODE = 101;
    private static final int REQUEST_STORAGE_CODE = 100;
    private Uri currentUri;
    @BindView(R.id.product_name_edit)
    EditText productNameEt;
    @BindView(R.id.product_price_edit)
    EditText productPriceEt;
    @BindView(R.id.supplier_name_edit)
    EditText supplierNameEt;
    @BindView(R.id.supplier_no_edit)
    EditText supplierNoEt;
    @BindView(R.id.spinner_edit)
    Spinner spinner;
    @BindView(R.id.product_quantity_edit)
    EditText productQuantityEt;
    @BindView(R.id.product_image_edit)
    ImageView productImageEdit;
    private int productType;
    Uri imageUri;
    boolean productChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            final View decor = getWindow().getDecorView();
            if (decor == null)
                return;
            postponeEnterTransition();
            decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onPreDraw() {
                    decor.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.shared));


        currentUri = getIntent().getData();
        if (currentUri == null) {
            setTitle(getString(R.string.add_product));
            Glide.with(this).load(R.drawable.ic_menu_gallery).apply(RequestOptions.fitCenterTransform()).into(productImageEdit);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_CONTENT_LOADER, null, this);

        }

        setSpinner();
    }

    @OnTouch({R.id.product_name_edit, R.id.product_price_edit, R.id.product_quantity_edit, R.id.supplier_no_edit, R.id.supplier_name_edit})
    boolean setEditingToTrue() {
        productChanged = true;
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null) {
            outState.putString("image", imageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String img = savedInstanceState.getString("image");
        if (img != null) {
            Glide.with(this).load(Uri.parse(img)).apply(RequestOptions.circleCropTransform()).into(productImageEdit);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            if (data != null) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).apply(RequestOptions.placeholderOf(R.drawable.ic_camera_alt)).into(productImageEdit);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startImagePicker();
                }
            }
        }
    }

    @OnClick(R.id.product_image_edit)
    void pickImage() {
        if (currentUri == null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_CODE);
                return;
            }
            startImagePicker();
        }

    }

    private void startImagePicker() {
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), IMAGE_REQUEST_CODE);
    }

    private void setSpinner() {
        ArrayAdapter typeSpinner = ArrayAdapter.createFromResource(this, R.array.array_type_spinner, android.R.layout.simple_spinner_item);
        typeSpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(typeSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.food))) {
                        productType = StoreContract.StoreEntry.TYPE_FOOD;
                    } else if (selection.equals(getString(R.string.staple))) {
                        productType = StoreContract.StoreEntry.TYPE_STAPLE;
                    } else if (selection.equals(getString(R.string.fashion))) {
                        productType = StoreContract.StoreEntry.TYPE_FASHION;
                    } else if (selection.equals(getString(R.string.fruits))) {
                        productType = StoreContract.StoreEntry.TYPE_FRUITS_AND_VEG;
                    } else if (selection.equals(getString(R.string.home_care))) {
                        productType = StoreContract.StoreEntry.TYPE_HOME_CARE;
                    } else if (selection.equals(getString(R.string.home_need))) {
                        productType = StoreContract.StoreEntry.TYPE_HOME_NEED;
                    } else if (selection.equals(getString(R.string.electronics))) {
                        productType = StoreContract.StoreEntry.TYPE_ELECTRONICS;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                productType = StoreContract.StoreEntry.TYPE_HOME_NEED;
            }
        });
    }

    private void saveProduct() {
        String name = productNameEt.getText().toString().trim();
        String quantity = productQuantityEt.getText().toString().trim();
        String price = productPriceEt.getText().toString().trim();
        String supplierNo = supplierNoEt.getText().toString().trim();
        String supplieName = supplierNameEt.getText().toString().trim();


        if (imageUri == null ||
                TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(quantity) ||
                TextUtils.isEmpty(price) ||
                TextUtils.isEmpty(supplierNo) ||
                TextUtils.isEmpty(supplieName)) {
            Toast.makeText(this, "Enter all details to save product", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, name);
        cv.put(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE, imageUri.toString());
        cv.put(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(price));
        cv.put(StoreContract.StoreEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(quantity));
        cv.put(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplieName);
        cv.put(StoreContract.StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO, supplierNo);
        cv.put(StoreContract.StoreEntry.COLUMN_PRODUCT_TYPE, productType);

        if (currentUri == null) {
            Uri newUri = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, cv);
            if (newUri == null) {
                Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsChanged = getContentResolver().update(currentUri, cv, null, null);
            if (rowsChanged == 0) {
                Toast.makeText(this, "Error updating product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_product_edit);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!productChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete_product_edit:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productChanged) {
                    NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.unsaved_changes_dialog));
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_msg));
        builder.setPositiveButton(getString(R.string.delete_msg), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteProduct() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error_deleting), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.success_deleting), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StoreEntry.COLUMN_ID,
                StoreEntry.COLUMN_PRODUCT_NAME,
                StoreEntry.COLUMN_PRODUCT_IMAGE,
                StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreEntry.COLUMN_PRODUCT_QUANTITY,
                StoreEntry.COLUMN_PRODUCT_TYPE,
                StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO};
        return new CursorLoader(this, currentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameColIndex = data.getColumnIndex(StoreEntry.COLUMN_PRODUCT_NAME);
            int priceColIndex = data.getColumnIndex(StoreEntry.COLUMN_PRODUCT_PRICE);
            int quantityColIndex = data.getColumnIndex(StoreEntry.COLUMN_PRODUCT_QUANTITY);
            int typeColIndex = data.getColumnIndex(StoreEntry.COLUMN_PRODUCT_TYPE);
            int supplierNameColIndex = data.getColumnIndex(StoreEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColIndex = data.getColumnIndex(StoreEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NO);
            productNameEt.setText(data.getString(nameColIndex));
            productQuantityEt.setText(String.valueOf(data.getInt(quantityColIndex)));
            productPriceEt.setText(String.valueOf(data.getInt(priceColIndex)));
            supplierNameEt.setText(data.getString(supplierNameColIndex));
            supplierNoEt.setText(data.getString(supplierPhoneColIndex));
            String s = data.getString(data.getColumnIndex(StoreEntry.COLUMN_PRODUCT_IMAGE));
            if (s != null) {
                Uri imageUri = Uri.parse(s);

                Glide.with(this)
                        .load(imageUri)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_camera_alt))
                        .apply(RequestOptions.circleCropTransform())
                        .into(productImageEdit);
            }

            productImageEdit.setEnabled(true);
            int type = data.getInt(typeColIndex);
            switch (type) {
                case StoreEntry.TYPE_FRUITS_AND_VEG:
                    spinner.setSelection(0);
                    break;
                case StoreEntry.TYPE_STAPLE:
                    spinner.setSelection(1);
                    break;
                case StoreEntry.TYPE_FASHION:
                    spinner.setSelection(2);
                    break;
                case StoreEntry.TYPE_FOOD:
                    spinner.setSelection(3);
                    break;
                case StoreEntry.TYPE_HOME_CARE:
                    spinner.setSelection(5);
                    break;
                case StoreEntry.TYPE_ELECTRONICS:
                    spinner.setSelection(6);
                    break;
                default:
                    spinner.setSelection(4);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        productNameEt.setText("");
        productQuantityEt.setText("");
        productPriceEt.setText("");
        supplierNoEt.setText("");
        supplierNoEt.setText("");
        spinner.setSelection(4);

    }


}