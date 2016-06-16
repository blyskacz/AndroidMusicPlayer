package blyskacz.musicforeveryone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Created by Szymon on 2016-02-20.
 */
public class AppWidget extends AppWidgetProvider
{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for(int i=0; i<appWidgetIds.length; i++)
        {
            int widgetId = appWidgetIds[i];

            Intent play = new Intent(context, musicPlayer.class);
            PendingIntent pendingplay = PendingIntent.getActivities(context, 0, new Intent[]{play}, 0);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setOnClickPendingIntent(R.id.playWidget, pendingplay);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
