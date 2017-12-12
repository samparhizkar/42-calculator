package com.sepidsa.fortytwocalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.sepidsa.fortytwocalculator.data.LogContract;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Farshid on 5/17/2015.
 */
public class AnimatedLogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG_ = AnimatedLogFragment.class.getSimpleName();

    private LogAdapter mLogAdapter;
    private ListView mListView;
    private Button mClearButton;
    private Button mExpandButton;
    private SearchView mSearchView;
    private int mPosition = ListView.INVALID_POSITION;
    private Fragment mLogFragment;

    private static final String SELECTED_KEY = "selected_position";

    private static final int LOG_LOADER = 0;

    private static final String[] LOG_COLUMNS = {
            LogContract.LogEntry.TABLE_NAME + "." + LogContract.LogEntry._ID,
            LogContract.LogEntry.COLUMN_RESULT,
            LogContract.LogEntry.COLUMN_OPERATION,
            LogContract.LogEntry.COLUMN_TAG,
            LogContract.LogEntry.COLUMN_STARRED
    };

    public AnimatedLogFragment() {
        mLogFragment = this;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mLogAdapter = new LogAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);


        mListView = (ListView) rootView.findViewById(R.id.listview_log);
        mClearButton = (Button) rootView.findViewById(R.id.button_clear_log);
        mExpandButton = (Button) rootView.findViewById(R.id.button_export_log);
        mSearchView = (SearchView) rootView.findViewById(R.id.log_search_view);
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(((MainActivity) getActivity()).getAccentColorCode());


        //setting icon typefaces for these 2 buttons

        mClearButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "flaticon.ttf"));
        mExpandButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "flaticon.ttf"));
        if(!((MainActivity)getActivity()).isRetroThemeSelected()) {
            mExpandButton.setTextColor(((MainActivity) getActivity()).getDialpadFontColor());
            mClearButton.setTextColor(((MainActivity)getActivity()).getDialpadFontColor());

        }

        View empty =  rootView.findViewById(R.id.empty_view);
        mListView.setEmptyView(empty);
        mListView.setAdapter(mLogAdapter);
        getLoaderManager().restartLoader(0, null, (android.support.v4.app.LoaderManager.LoaderCallbacks) mLogFragment);


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    getLoaderManager().restartLoader(0, null, (android.support.v4.app.LoaderManager.LoaderCallbacks) mLogFragment);
                } else {
                    Bundle filter = new Bundle();
                    filter.putString("filter", newText);
                    getLoaderManager().restartLoader(0, filter, (android.support.v4.app.LoaderManager.LoaderCallbacks) mLogFragment);


                }

                return true;
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, final View view, final int position, long l) {

                final View toolbar = view.findViewById(R.id.toolbar);


                // Creating the expand animation for the item

                ExpandAnimation expandAni = new ExpandAnimation(getActivity(), mListView, view, toolbar, 250);

                // Start the animation on the toolbar

                toolbar.startAnimation(expandAni);
            }

        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor = getActivity().getContentResolver().query(
                        LogContract.LogEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

                if(cursor.moveToNext()) {
                    View checkBoxView = View.inflate(getActivity(), R.layout.checkbox, null);
                    final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);

                    checkBox.setText(getActivity().getString(R.string.farsi_clear_starred));

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(getActivity().getString(R.string.farsi_clear_log));
                    builder.setMessage(getActivity().getString(R.string.farsi_clear_log_confirm))
                            .setView(checkBoxView)
                            .setCancelable(false)
                            .setPositiveButton(getActivity().getString(R.string.farsi_yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (checkBox.isChecked()) {
                                        getActivity().getContentResolver().delete(LogContract.LogEntry.CONTENT_URI, null, null);


                                    } else {
                                        getActivity().getContentResolver().delete(
                                                LogContract.LogEntry.CONTENT_URI,
                                                LogContract.LogEntry.COLUMN_STARRED + "!=?",
                                                new String[]{"1"}
                                        );
                                    }
                                    ((MainActivity)getActivity()).playSound(((MainActivity)getActivity()).getClearSoundID());

                                }
                            })
                            .setNegativeButton(getActivity().getString(R.string.farsi_no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    Toast.makeText(getActivity(),getActivity().getString(R.string.farsi_list_is_empty), Toast.LENGTH_SHORT).show();
                }

            }
        });

        mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = "";
                Cursor cursor = getActivity().getContentResolver().query(
                        LogContract.LogEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                String result;
                String operation;
                String tag;
                Boolean starred;

                while (cursor.moveToNext()) {
                    result = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_RESULT));
                    operation = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_OPERATION));
                    tag = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));
                    starred = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED)) != 0;

                    if (!tag.equals("")) tag += " :\n";
                    String starString = "";
                    if (starred) starString = " (*)";

                    shareText += tag + operation + " = " + result + starString + "\n\n";
                }


                if (!shareText.equals("")) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String subject = "42 Calculations";
                    shareText += "\nGenerated by 42 calculator42.";

                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);

                    getActivity().startActivity(Intent.createChooser(sharingIntent, "Share"));
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.farsi_list_is_empty), Toast.LENGTH_SHORT).show();
                }


            }
        });

//        mListView.smoothScrollToPosition(mLogAdapter.getCount() - 1 );

        return rootView;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLogAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            mListView.smoothScrollToPosition(mPosition);
//            mListView.setSelection(mLogAdapter.getCount() -1);
//        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLogAdapter.swapCursor(null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mPosition = mListView.getLastVisiblePosition();
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(args == null) {
            return new CursorLoader(getActivity(),
                    LogContract.LogEntry.CONTENT_URI,
                    LOG_COLUMNS,
                    null,
                    null,
                    null);
        } else {
            String filter = args.getString("filter");
            return new CursorLoader(getActivity(),
                    LogContract.LogEntry.CONTENT_URI,
                    LOG_COLUMNS,
                    LogContract.
                            LogEntry.COLUMN_TAG + " like ?" + " or " +
                            LogContract.LogEntry.COLUMN_RESULT_NO_COMMA + " like ?" + " or " +
                            LogContract.LogEntry.COLUMN_RESULT + " like ?" + " or " +
                            LogContract.LogEntry.COLUMN_OPERATION + " like ?",
                    new String[]{"%" + filter + "%", "%" + filter + "%", "%" + filter + "%", "%" + filter + "%"},
                    null);
        }
    }


    public void scrollToLast() {
        mListView.smoothScrollToPosition(mLogAdapter.getCount()+1);
    }


}
