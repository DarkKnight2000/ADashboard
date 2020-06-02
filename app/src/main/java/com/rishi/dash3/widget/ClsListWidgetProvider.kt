package com.rishi.dash3.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.rishi.dash3.R


class ClsListWidgetProvider:AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if(appWidgetIds == null) return
        for(widgetId:Int in appWidgetIds){
            val intent = Intent(context, MyWidgetRemoteViewsService::class.java)
            val remoteViews = RemoteViews(context?.packageName, R.layout.list_widget_layout)
            remoteViews.setRemoteAdapter(R.id.wListView, intent)
            appWidgetManager?.updateAppWidget(widgetId, remoteViews)
        }
    }
}