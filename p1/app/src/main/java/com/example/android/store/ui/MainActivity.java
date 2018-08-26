package com.example.android.store.ui;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.store.R;
import com.example.android.store.adapter.ProductAdapter;
import com.example.android.store.data.StoreContract.StoreEntry;
import com.example.android.store.utils.MySparseBoleanArray;
import com.example.android.store.utils.MySparseLongArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CONTENT_LOADER = 1;
    ProductAdapter adapter;
    @BindView(R.id.product_list)
    ListView listView;
    @BindView(R.id.empty_view)
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        adapter = new ProductAdapter(this, null);
        listView.setAdapter(adapter);
        listView.setEmptyView(layout);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                Pair<View, String>[] p = new Pair[1];
                p[0] = Pair.create(view.findViewById(R.id.product_image), getString(R.string.transition_img));
                Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, p).toBundle();
                startActivity(intent, b);
            }
        });

        getLoaderManager().initLoader(CONTENT_LOADER, null, this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle("selected " + checkedCount);
                adapter.toggleSelection(position, id);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_items:
                        MySparseLongArray ar = adapter.getSelectedId();
                        if (adapter.getSelectedId().size() == adapter.getItemCount()) {
                            int r = getContentResolver().delete(StoreEntry.CONTENT_URI, null, null);
                            if (r != -1) {
                                Toast.makeText(MainActivity.this, "deleted all items", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            String selection = StoreEntry._ID + "=?";
                            String[] selectionArgs = new String[1];
                            for (int i = 0; i < adapter.getSelectedId().size(); i++) {
                                selectionArgs[0] = String.valueOf(ar.get(i));
                                getContentResolver().delete(StoreEntry.CONTENT_URI, selection, selectionArgs);
                            }
                        }
                        mode.finish();
                        return true;

                    default:
                        return false;

                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listView.clearChoices();
                adapter.clear();

            }

        });


    }

    @OnClick(R.id.fab_add_product)
    void addProductActivity() {
        startActivity(new Intent(MainActivity.this, EditProductActivity.class));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MySparseBoleanArray sel = savedInstanceState.getParcelable("selected_pos");
        adapter.setSelected(sel);
        MySparseLongArray selId = savedInstanceState.getParcelable("selected_id");
        adapter.setSelectedId(selId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selected_pos", adapter.getSelected());
        outState.putParcelable("selected_id", adapter.getSelectedId());
    }

    public void sellProduct(int rowId, int quantity) {
        --quantity;
        ContentValues cv = new ContentValues();
        cv.put(StoreEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        getContentResolver().update(ContentUris.withAppendedId(StoreEntry.CONTENT_URI, rowId), cv, null, null);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StoreEntry.COLUMN_ID,
                StoreEntry.COLUMN_PRODUCT_NAME,
                StoreEntry.COLUMN_PRODUCT_IMAGE,
                StoreEntry.COLUMN_PRODUCT_TYPE,
                StoreEntry.COLUMN_PRODUCT_QUANTITY,
                StoreEntry.COLUMN_PRODUCT_PRICE
        };

        return new CursorLoader(
                this,
                StoreEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
