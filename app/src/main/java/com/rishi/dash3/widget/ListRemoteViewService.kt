package com.rishi.dash3.widget

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.rishi.dash3.R
import com.rishi.dash3.utils.getSeg
import com.rishi.dash3.utils.intToTime
import com.rishi.dash3.models.EachClass
import com.rishi.dash3.models.Settings
import com.rishi.dash3.utils.weekDays
import io.realm.Realm
import io.realm.RealmResults


class MyWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MyWidgetRemoteViewsFactory(this.applicationContext, intent)
    }
}

class MyWidgetRemoteViewsFactory(applicationContext: Context, intent: Intent?) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var realm:Realm
    private lateinit var dataSet:RealmResults<EachClass>
    private var mContext: Context? = applicationContext

    override fun onCreate() {
        realm = Realm.getDefaultInstance()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        val cal = Calendar.getInstance()
        val mDay = cal.get(Calendar.DAY_OF_WEEK)
        val mDate = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(
            Calendar.YEAR)
        val settings = realm.where(Settings::class.java).findFirst() ?: return
        dataSet = realm.where(EachClass::class.java).equalTo("day", weekDays[mDay-1]  + " " + getSeg(
            mDate,
            settings
        )
        ).`in`("date", arrayOf(mDate, "")).findAll()

    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION) {
            return null
        }
        val curCls = dataSet[position] ?: return null
        val rv = RemoteViews(mContext?.packageName, R.layout.widget_list_item)
        rv.setTextViewText(R.id.wcCrsename, curCls.code)
        rv.setTextViewText(R.id.wcRoom, curCls.room)
        rv.setTextViewText(R.id.wctartTime,
            intToTime(curCls.startTime)
        )
        rv.setTextViewText(R.id.wcEndTime,
            intToTime(curCls.endTime)
        )

        return rv
    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        realm.close()
    }

}