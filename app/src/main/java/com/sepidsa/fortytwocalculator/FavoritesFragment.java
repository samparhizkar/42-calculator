package com.sepidsa.fortytwocalculator;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sepidsa.fortytwocalculator.data.LogContract;

/**
 * Created by Farshid on 5/20/2015.
 */
public class FavoritesFragment  extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private FavoritesAdapter mFavoritesAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int LOG_LOADER = 0;

    private static final String[] LOG_COLUMNS = {
            LogContract.LogEntry.TABLE_NAME + "." + LogContract.LogEntry._ID,
            LogContract.LogEntry.COLUMN_RESULT,
            LogContract.LogEntry.COLUMN_OPERATION,
            LogContract.LogEntry.COLUMN_TAG,
            LogContract.LogEntry.COLUMN_STARRED
    };

    public FavoritesFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFavoritesAdapter = new FavoritesAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);


        mListView = (ListView) rootView.findViewById(R.id.listview_log);
        TextView empty = (TextView) rootView.findViewById(R.id.empty_list);
        mListView.setEmptyView(empty);
        mListView.setAdapter(mFavoritesAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, final View view, final int position, long l) {
                TextView resultView = (TextView) view.findViewById(R.id.result);
                String result = resultView.getText().toString();
                ((MainActivity) getActivity()).addNumberToCalculation(result);
                ((MainActivity) getActivity()).switchToMainFragment();
                dismiss();

            }

        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoritesAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoritesAdapter.swapCursor(null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LogContract.LogEntry.CONTENT_URI,
                LOG_COLUMNS,
                LogContract.LogEntry.COLUMN_STARRED + "=?",
                new String[]{"1"},
                null);
    }

}
