package com.beyond.learning01;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class TaijiProvider extends AppWidgetProvider {
    private static final String IMAGEVIEW="android.intent.action.IMAGEVIEW";
    private static int i=0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.taiji_provider);
        views.setImageViewResource(R.id.imageView,R.drawable.taiji);
        Intent intent = new Intent();
        intent.setAction(IMAGEVIEW);
        intent.putExtra("name", 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        RemoteViews remoteViews= new RemoteViews(context.getPackageName(), R.layout.taiji_provider);
        String action=intent.getAction();
        int imageNo= intent.getIntExtra("name",-1);
        if (IMAGEVIEW.equals(action)){
            System.out.println(i);
            i=(i+1)%2;
            remoteViews.setImageViewResource(R.id.imageView,i>0?R.drawable.laozi:R.drawable.taiji);
            AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
            ComponentName componentName=new ComponentName(context,TaijiProvider.class);
            appWidgetManager.updateAppWidget(componentName,remoteViews);
        }else {
            super.onReceive(context,intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

