package com.rishi.dash3.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.rishi.dash3.Activties.CourseInfo
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.R
import io.realm.Realm
import kotlinx.android.synthetic.main.course_list_card.view.*


class ClassesAdapter(val context: Context, val clsses:MutableList<EachCourse>, val realm: Realm) : RecyclerView.Adapter<ClassesAdapter.EachViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): EachViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.course_list_card,parent,false)
        return  EachViewHolder(view)
    }

    override fun getItemCount(): Int {
        return clsses.size //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: EachViewHolder, pos: Int) {
        val cls = clsses[pos]
        p0.setData(cls, pos)
    }


    inner class EachViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){

        private var crseClss: EachCourse? = null
        private var crsePos: Int = 0

        init {
            itemView.setOnClickListener{
                if(clsses.size >= crsePos){
                    val intent = Intent(context, CourseInfo::class.java)

                    intent.putExtra("name",clsses[crsePos].crsename)
                    intent.putExtra("code",clsses[crsePos].crsecode)
                    intent.putExtra("slot",clsses[crsePos].defSlot)
                    context.startActivity(intent)

                }
                else
                    Toast.makeText(context,"Cant open intent", Toast.LENGTH_SHORT).show()

            }
            itemView.btnBin.setOnClickListener{
                if(clsses.contains(crseClss)) {
                    realm.beginTransaction()
                    clsses.remove(crseClss)
                    realm.commitTransaction()
                    notifyItemRemoved(crsePos)
                    notifyItemRangeChanged(crsePos,clsses.size)
                    Toast.makeText(context, "Deleted at, Size left "+ crsePos + clsses.size, Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(context,"Cant delete", Toast.LENGTH_SHORT).show()
            }
        }

        fun setData(cls: EachCourse?, pos:Int){
            itemView.cardTitle.text = cls!!.crsecode

            this.crseClss = cls
            this.crsePos = pos

        }
    }
}
