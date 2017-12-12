package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sepidsa.fortytwocalculator.data.LogContract;

/**
 * Created by Farshid on 5/17/2015.
 */
public class LogAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 1;
    private Context mContext;
    private String m_Text;


    public LogAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String result = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_RESULT));
        String operation = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_OPERATION));
        String tag = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));
        Boolean starred = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED)) != 0;
        int iconColor = ((MainActivity)mContext).getDialpadFontColor();
        viewHolder.position = cursor.getInt((cursor.getColumnIndex(LogContract.LogEntry._ID)));

        viewHolder.resultView.setText(result);
        viewHolder.operationView.setText(operation);
        viewHolder.tagView.setText(tag);
        viewHolder.starredButton.setChecked(starred);
        viewHolder.starredButton.setOnClickListener(mStarOnClickListener);

        if(((MainActivity) context).isRetroThemeSelected()) {
            viewHolder.tagView.setBackgroundColor(Color.parseColor("#bdbdbd"));

        }else {
            viewHolder.tagView.setBackgroundColor(((MainActivity) mContext).getAccentColorCode());

        }
        viewHolder.tagView.setTypeface(Typeface.createFromAsset(context.getAssets(), "notoregular.ttf"));
        viewHolder.tagView.setOnClickListener(mLabelButtonOnClickListener);


        viewHolder.deleteButton.setOnClickListener(mDeleteButtonOnClickListener);
        viewHolder.deleteButton.setTypeface(Typeface.createFromAsset(context.getAssets(), "flaticon.ttf"));
        viewHolder.labelButton.setOnClickListener(mLabelButtonOnClickListener);
        viewHolder.labelButton.setTypeface(Typeface.createFromAsset(context.getAssets(), "flaticon.ttf"));
        viewHolder.shareButton.setOnClickListener(mShareButtonOnClickListener);
        viewHolder.shareButton.setTypeface(Typeface.createFromAsset(context.getAssets(), "flaticon.ttf"));

        viewHolder.useButton.setOnClickListener(mUseButtonOnClickListener);
        viewHolder.useButton.setTypeface(Typeface.createFromAsset(context.getAssets(), "flaticon.ttf"));

        if(!((MainActivity) context).isRetroThemeSelected()) {

            viewHolder.resultView.setTextColor(((MainActivity) context).getAccentColorCode());

            viewHolder.operationView.setTextColor(iconColor);
            if(((MainActivity) context).getKeypadBackgroundColorCode() == Color.WHITE){
                viewHolder.arrow.setTextColor(Color.parseColor("#EEEEEE"));
            }else{
                viewHolder.arrow.setTextColor(iconColor);
            }


            viewHolder.shareButton.setTextColor(iconColor);
            viewHolder.labelButton.setTextColor(iconColor);
            viewHolder.deleteButton.setTextColor(iconColor);
            viewHolder.useButton.setTextColor(iconColor);
        }else {
            viewHolder.arrow.setTextColor(Color.parseColor("#bdbdbd"));

        }



        if((viewHolder.toolbar.getVisibility() == View.VISIBLE) && !viewHolder.checked) {
            LinearLayout.LayoutParams viewLayoutParams = (LinearLayout.LayoutParams) viewHolder.toolbar.getLayoutParams();
            viewLayoutParams.bottomMargin = 0 - viewHolder.toolbar.getHeight();
            viewHolder.toolbar.setVisibility(View.GONE);
        } else {
            viewHolder.toolbar.setVisibility(View.VISIBLE);
            viewHolder.checked=false;
        }
        viewHolder.arrow.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "flaticon.ttf"));
        viewHolder.arrow.setRotation(0);
        viewHolder.arrow.setTranslationY(0);
        viewHolder.arrow.setAlpha(1);


    }

    public static class ViewHolder {
        public final TextView resultView;
        public final TextView operationView;
        public final TextView tagView;
        public final CheckBox starredButton;
        public final View toolbar;
        public final TextView arrow;
        public final FrameLayout tagBackground;

        public int position;
        public boolean checked;

        public final Button deleteButton;
        public final Button labelButton;
        public final Button shareButton;
        public final Button useButton;

        public ViewHolder(View view) {

            resultView = (TextView) view.findViewById(R.id.result);
            operationView = (TextView) view.findViewById(R.id.operation);
            tagView = (TextView) view.findViewById(R.id.LOG_tag);
            starredButton = (CheckBox) view.findViewById(R.id.log_checkbox);
            toolbar = view.findViewById(R.id.toolbar);
            arrow = (TextView) view.findViewById(R.id.arrow);
            tagBackground = (FrameLayout) view.findViewById(R.id.tag_background);


            checked = false;

            deleteButton = (Button) view.findViewById(R.id.deleteButton);
            labelButton = (Button) view.findViewById(R.id.labelButton);
            shareButton = (Button) view.findViewById(R.id.shareButton);
            useButton = (Button) view.findViewById(R.id.useButton);

        }
    }


    private CompoundButton.OnClickListener mStarOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
                viewHolder.checked = true;

                String selection = LogContract.LogEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(position)};
                Uri uri = LogContract.LogEntry.CONTENT_URI;

                Cursor cursor = mContext.getContentResolver().query(
                        uri,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
                if (cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    int isCheckedInteger = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED));
                    isCheckedInteger = 1 - isCheckedInteger;
                    ContentValues values = new ContentValues();
                    values.put(LogContract.LogEntry.COLUMN_STARRED, isCheckedInteger);
                    mContext.getContentResolver().update(
                            uri,
                            values,
                            selection,
                            selectionArgs
                    );



                }
            }
        }
    };

    Button.OnClickListener mDeleteButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            final View parent = findParentRecursively(view);
            if (parent != null) {

                ((MainActivity)mContext).playSound(((MainActivity)mContext).getClearSoundID());

                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
                String selection = LogContract.LogEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(position)};
                Uri uri = LogContract.LogEntry.CONTENT_URI;

                mContext.getContentResolver().delete(
                        uri,
                        selection,
                        selectionArgs
                );
            }



        }
    };


    Button.OnClickListener mLabelButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                final int position = viewHolder.position;
//                viewHolder.checked = true;

                final String selection = LogContract.LogEntry._ID + "=?";
                final String[] selectionArgs = new String[]{String.valueOf(position)};
                final Uri uri = LogContract.LogEntry.CONTENT_URI;

                Cursor cursor = mContext.getContentResolver().query(
                        uri,
                        null,
                        selection,
                        selectionArgs,
                        null
                );
                if (cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    String currentLabel = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.farsi_label));

                    // Set up the input

                    final EditText input = new EditText(mContext);
                    final InputMethodManager inputMethodManager = (InputMethodManager)  mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setCancelable(false);
                    input.setText(currentLabel);
                    input.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    // Set up the buttons

                    builder.setPositiveButton(mContext.getString(R.string.farsi_ok), new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newLabel = input.getText().toString();
                            ContentValues values = new ContentValues();
                            values.put(LogContract.LogEntry.COLUMN_TAG, newLabel);
                            mContext.getContentResolver().update(
                                    uri,
                                    values,
                                    selection,
                                    selectionArgs
                            );
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        }
                    });
                    builder.setNegativeButton(mContext.getString(R.string.farsi_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
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
                    input.setOnKeyListener(keyListener);

                }
            }
        }
    };


    Button.OnClickListener mShareButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                String shareBody = viewHolder.operationView.getText() + " = " + viewHolder.resultView.getText();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String subject = viewHolder.tagView.getText().toString();

                if( !subject.equals("") ) {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, subject + ": " + shareBody);
                } else {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                }

                mContext.startActivity(Intent.createChooser(sharingIntent, "Share"));
            }
        }
    };


    Button.OnClickListener mUseButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            View parent = findParentRecursively(view);
            if (parent != null) {
                ViewHolder viewHolder = (ViewHolder) parent.getTag();
                ((MainActivity) mContext).addNumberToCalculation(viewHolder.resultView.getText().toString());
                ((MainActivity) mContext).switchToMainFragment();

            }
        }
    };


    public View findParentRecursively(View view) {
        if (view.getTag() != null) {
            return view;
        }
        View parent = (View) view.getParent();
        if (parent == null) {
            return null;
        }
        return findParentRecursively(parent);
    }




//    public View findParentById(View view, int targetId) {
//        if (view.getId() == targetId) {
//            return view;
//        }
//        View parent = (View) view.getParent();
//        if (parent == null) {
//            return null;
//        }
//        return findParentById(parent, targetId);
//    }

}