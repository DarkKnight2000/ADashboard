package com.rishi.dash3.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rishi.dash3.activties.AddCourse
import com.rishi.dash3.adapters.ClassesAdapter
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.R
import io.realm.Realm

class AllCourseFrag : Fragment() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_allcourses, container, false)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        view.findViewById<RecyclerView>(R.id.recyclerView).layoutManager = layoutManager


        view.findViewById<Button>(R.id.crseAdd).setOnClickListener {
            val intent = Intent(this.context, AddCourse::class.java)
            this.startActivity(intent)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        val allCrs = realm.where(EachCourse::class.java).findAll()
        val adapter = ClassesAdapter(this.context!!, allCrs, realm)
        view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }


}
