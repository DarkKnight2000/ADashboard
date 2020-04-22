package com.rishi.dash3.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.R
import com.rishi.dash3.intToTime
import io.realm.Realm
import kotlinx.android.synthetic.main.edit_info_card.view.*

class InfoAdapter(val context: Context, val clsses:MutableList<EachClass>, val canEdit:Boolean, val realm:Realm, var crse:EachCourse?) : RecyclerView.Adapter<InfoAdapter.EachViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): EachViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.edit_info_card,parent,false)
        return  EachViewHolder(view)
    }

    override fun getItemCount(): Int {
        return clsses.size
    }

    override fun onBindViewHolder(p0: EachViewHolder, pos: Int) {
        val cls = clsses[pos]
        //Log.i("All crses codes",cls.toString())
        p0.setData(cls, pos, canEdit)
    }

    inner class EachViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private var crseClss: EachClass = EachClass()
        private var crsePos: Int = 0

        fun setData(cls: EachClass, pos:Int, canEdit: Boolean){
            if(canEdit) {
                if (cls.date != "") itemView.weekdayView.text = cls.date
                else itemView.weekdayView.text = cls.day
            }
            else itemView.weekdayView.text = cls.code
            itemView.startTime.text = intToTime(cls.startTime)
            itemView.endTime.text = intToTime(cls.endTime)
            itemView.room.text = cls.room
            itemView.btnBin.visibility = if (canEdit) View.VISIBLE else View.GONE

            this.crseClss = cls
            this.crsePos = pos

            Log.i("Got tags ", crseClss.id.toString())
            itemView.btnBin.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Warning!!")
                //set message for alert dialog
                builder.setMessage("Are you sure you want to delete this class? This cannot be undone!")

                //performing positive action
                builder.setPositiveButton("Delete"){_, _ ->
                    if(clsses.map { it.id }.contains(crseClss.id)) {
                        val pos1 = clsses.map { it.id }.indexOf(crseClss.id)
                        Log.i("Got at pos ", pos1.toString())
                        clsses.removeAt(pos1)
                        notifyItemRemoved(crsePos)
                        notifyItemRangeChanged(crsePos, clsses.size)
                        //Toast.makeText(context,"delete  id " + crseClss.id,Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(context,"Cant delete class",Toast.LENGTH_SHORT).show()
                }
                //performing cancel action
                builder.setNegativeButton("Cancel"){_ , _ ->
                    //Toast.makeText(context,"Delete aborted",Toast.LENGTH_LONG).show()
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCanceledOnTouchOutside(true)
                alertDialog.show()
            }
            itemView.setOnClickListener {
                Toast.makeText(context, "Course name : "+realm.where(EachCourse::class.java).equalTo("crsecode", cls.code).findFirst()?.crsename, Toast.LENGTH_SHORT).show()
            }

        }
    }
}
