package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sepidsa.fortytwocalculator.data.ConstantContract;

/**
 * Created by Farshid on 5/20/2015.
 */
public class ConstantSelectFragment  extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ConstantSelectAdapter mConstantSelectAdapter;
    private ListView mListView;
    private Button mAddButton;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int CONSTANT_LOADER = 0;

    private static final String[] CONSTANT_COLUMNS = {
            ConstantContract.ConstantEntry.TABLE_NAME + "." + ConstantContract.ConstantEntry._ID,
            ConstantContract.ConstantEntry.COLUMN_NAME,
            ConstantContract.ConstantEntry.COLUMN_NUMBER,
            ConstantContract.ConstantEntry.COLUMN_SELECTED
    };

    public ConstantSelectFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CONSTANT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mConstantSelectAdapter = new ConstantSelectAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_constant_select, container, false);


        mListView = (ListView) rootView.findViewById(R.id.listview_constant);
        mAddButton = (Button) rootView.findViewById(R.id.button_add_item);
        mAddButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "yekan.ttf"));
        TextView empty = (TextView) rootView.findViewById(R.id.empty_list);
        mListView.setEmptyView(empty);
        mListView.setAdapter(mConstantSelectAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, final View view, final int position, long l) {
                TextView resultView = (TextView) view.findViewById(R.id.constant_number);
                String result = resultView.getText().toString();
                ((MainActivity) getActivity()).addNumberToCalculation(result);
                ((MainActivity) getActivity()).switchToMainFragment();
                dismiss();

//                Toast.makeText(getActivity(),
//                        "item " + position + " clicked , IMPLEMENT ME.", Toast.LENGTH_SHORT).show();
            }

        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(((MainActivity)getActivity()).getPremiumPreference()) {


                        LayoutInflater li = LayoutInflater.from(getActivity());
                        View promptsView = li.inflate(R.layout.constant_input_dialog, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        builder.setTitle(getActivity().getString(R.string.farsi_enter_constant));
                        builder.setCancelable(false);

                        // Set up the input

                        final EditText name = (EditText) promptsView.findViewById(R.id.input_name);
                        final EditText number = (EditText) promptsView.findViewById(R.id.input_number);
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

                        name.setInputType(InputType.TYPE_CLASS_TEXT);
                        number.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        builder.setView(promptsView);
                        name.requestFocus();
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        // Set up the buttons

                        builder.setPositiveButton(getActivity().getString(R.string.farsi_ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    String newName = name.getText().toString();
                                    Double newNumber = Double.parseDouble(number.getText().toString());
                                    ContentValues values = new ContentValues();
                                    values.put(ConstantContract.ConstantEntry.COLUMN_NAME, newName);
                                    values.put(ConstantContract.ConstantEntry.COLUMN_NUMBER, newNumber);
                                    values.put(ConstantContract.ConstantEntry.COLUMN_SELECTED, 1);

                                    getActivity().getContentResolver().insert(
                                            ConstantContract.ConstantEntry.CONTENT_URI,
                                            values
                                    );
                                    inputMethodManager.hideSoftInputFromWindow(name.getWindowToken(), 0);
                                } catch (NumberFormatException nfe) {
                                    dialog.cancel();
                                }

                            }
                        });
                        builder.setNegativeButton(getActivity().getString(R.string.farsi_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                inputMethodManager.hideSoftInputFromWindow(name.getWindowToken(), 0);
                                dialog.cancel();

                            }
                        });

                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        EditText.OnKeyListener keyListener = new EditText.OnKeyListener() {
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                    switch (keyCode) {
                                        case KeyEvent.KEYCODE_DPAD_CENTER:
                                        case KeyEvent.KEYCODE_ENTER:
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                                            return true;
                                        default:
                                            break;
                                    }
                                }
                                return false;
                            }
                        };
                        number.setOnKeyListener(keyListener);
                    }

                else {
                        Intent myIntent = new Intent(getActivity(), PremiumShowcasePagerActivity.class);
                        myIntent.putExtra("page", 3);
                        startActivity(myIntent);
                    }

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
        mConstantSelectAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mConstantSelectAdapter.swapCursor(null);
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
                ConstantContract.ConstantEntry.CONTENT_URI,
                CONSTANT_COLUMNS,
                null,
                null,
                null);
    }

}
