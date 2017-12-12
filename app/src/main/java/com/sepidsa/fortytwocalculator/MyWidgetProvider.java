/*
package com.sepidsa.fortytwocalculator;

*/
/**
 * Created by ehsanparhizkar on 11/16/14.
 *//*

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";
   private static final String [] mTokens = {"0","1","2","3","4","5","6","7","8", "9","+","−","÷","\\u00d7","=","C",".","%"    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            // create some random data
            int number = (new Random().nextInt(100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            Log.w("WidgetExample", String.valueOf(number));
            // Set the text
//            remoteViews.setTextViewText(R.id.update, String.valueOf(number));

            // Register an onClickListener
            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);

            for (int index = 0 ; index < getButtonsID().length ;index ++){
                 pendingIntent = PendingIntent.getBroadcast(context,0, makeIntent(context,mTokens[index],appWidgetIds), PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(getButtonsID()[index], pendingIntent);
            }

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
//            context.startService(new Intent(context.getApplicationContext(), AppControllerService.class));

        }
    }

    int[] getButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{ R.id.button0_widget, R.id.button1_widget, R.id.button2_widget, R.id.button3_widget,
                R.id.button4_widget,  R.id.button5_widget,   R.id.button6_widget,  R.id.button9_widget, R.id.button8_widget, R.id.button7_widget,

                R.id.buttonPlus_widget, R.id.buttonMinus_widget, R.id.buttonDevide_widget, R.id.buttonTimes_widget, R.id.button_equals_widget, R.id.button_clear_widget, R.id.buttonPoint_widget,
                R.id.buttonPercent_widget,
        };
        return buttonsPointerArray;

    }

    Intent makeIntent(Context context, String message, int[] appWidgetIds){
        Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra("message",message);
         return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        String  buttonValue = intent.getStringExtra("message");


                switch (buttonValue){
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    remoteViews.setTextViewText(R.id.update,R.id.update);
                   TextView result = remoteViews.getLayoutId();


                }
            TextView martin = new TextView(remoteViews.getLayoutId(R.id.update));
            remoteViews.setTextViewText(R.id.textView_widget,"dick # " + number);
        remoteViews.setTextViewText(R.id.update,"hello");
//        Toast.makeText(context,"dick # " + number, Toast.LENGTH_SHORT).show();
        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, MyWidgetProvider.class),remoteViews);

            //your onClick action is here
//        }
    }

    public static void setActionClick(String actionClick) {
        ACTION_CLICK = actionClick;
    }
}*/
