package com.rishi.dash3.fragments


//import com.rishi.dash3.services.NotifService
import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rishi.dash3.Models.EachClass
import com.rishi.dash3.Models.EachCourse
import com.rishi.dash3.Models.Settings
import com.rishi.dash3.R
import com.rishi.dash3.getSeg
import com.rishi.dash3.isGreaterDate
import com.rishi.dash3.notifications.restartNotifService
import com.rishi.dash3.notifications.stopNotifService
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_settings.*

class Settings : Fragment() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        if(realm.where(Settings::class.java).findFirst() == null) {
            realm.beginTransaction()
            val sett = realm.createObject(Settings::class.java)
            sett.semStart = "04/12/2019"
            sett.seg2End = "18/12/2019"
            sett.seg3End = "25/12/2019"
            sett.seg1End = "11/12/2019"
            realm.commitTransaction()
        }
        //context?.startService(Intent(context, NotifService::class.java))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val ids = arrayOf(R.id.semStart, R.id.seg1, R.id.seg2, R.id.seg3)
        var s:TextView
        for(id in ids){
            s = view.findViewById(id)
            s.setOnClickListener(dateSetter(this.context!!, s))
        }
        view.findViewById<Button>(R.id.updateSem).setOnClickListener{
            if(isGreaterDate(seg1.text.toString(),seg2.text.toString()) || isGreaterDate(seg2.text.toString(),seg3.text.toString()) || isGreaterDate(semStart.text.toString(),seg1.text.toString())){
                Toast.makeText(this.context!!, "Segment dates are not in chronological order", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sett = realm.where(Settings::class.java).findFirst()!!
            realm.beginTransaction()
            sett.semStart = semStart.text.toString()
            sett.seg1End = seg1.text.toString()
            sett.seg2End = seg2.text.toString()
            sett.seg3End = seg3.text.toString()
            val a = realm.where(EachClass::class.java).notEqualTo("date", "").findAll()
            for(c in a){
                c.day = c.day.substring(0,4) + getSeg(c.date, sett.semStart, sett.seg1End, sett.seg2End, sett.seg3End)
            }
            realm.commitTransaction()
            Toast.makeText(context!!, "Updated", Toast.LENGTH_SHORT).show()
            restartNotifService(this.context!!)
        }

        view.findViewById<Button>(R.id.reset).setOnClickListener{
            val builder = AlertDialog.Builder(this.context!!)
            //set title for alert dialog
            builder.setTitle("Warning!")
            //set message for alert dialog
            builder.setMessage("This will delete all courses and their classes and cannot be undone!!")

            //performing positive action
                builder.setPositiveButton("DELETE EVERYTHING"){_, _ ->
                realm.beginTransaction()
                realm.where(EachClass::class.java).findAll().deleteAllFromRealm()
                realm.where(EachCourse::class.java).findAll().deleteAllFromRealm()
                realm.commitTransaction()
                Toast.makeText(this.context!!,"Reset successful!!",Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("Cancel"){_,_ -> return@setNegativeButton}
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.show()
        }

        view.findViewById<CheckBox>(R.id.notifCheck).setOnCheckedChangeListener { _, b ->
            if(b){
                restartNotifService(context!!)
                // Requesting permission in lower android versions
                val permissionCheck = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WAKE_LOCK)

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    val permsRequestCode = 200

                    ActivityCompat.requestPermissions(
                        this.activity!!,
                        arrayOf(Manifest.permission.WAKE_LOCK), permsRequestCode)
                } else {
                    val sett = realm.where(Settings::class.java).findFirst()!!
                    realm.beginTransaction()
                    sett.sendNotif = true
                    realm.commitTransaction()
                }
            }
            else{
                val sett = realm.where(Settings::class.java).findFirst()!!
                realm.beginTransaction()
                sett.sendNotif = false
                realm.commitTransaction()
                stopNotifService(context!!)
            }
        }

        /*view.findViewById<Button>(R.id.exportData).setOnClickListener {
            try {
                Toast.makeText(context, "${checkPer(context, this)}", Toast.LENGTH_SHORT).show()
                if(isExternalStorageAvailable() && checkPer(context, this)){
                    val fileOutPutStream = FileOutputStream(myExternalFile)
                    fileOutPutStream.write("123".toByteArray())
                    fileOutPutStream.close()
                    Toast.makeText(context, "Saved! ${myExternalFile.absolutePath}", Toast.LENGTH_LONG).show()
                    val sendIntent = Intent(Intent.ACTION_VIEW)
                    sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val uri = FileProvider.getUriForFile(context!!, context?.applicationContext?.packageName + ".provider", myExternalFile)
                    sendIntent.setDataAndType(uri, /*MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileName))*/"text/*")
                    if(sendIntent.resolveActivity(activity!!.packageManager) != null)
                    //val shareIntent = Intent.createChooser(sendIntent, "Sharing with")
                    startActivity(sendIntent)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.importData).setOnClickListener {

        }

        myExternalFile = File(activity?.getExternalFilesDir(filepath), fileName)
        //if(!myExternalFile.exists()) myExternalFile.createNewFile()
        */
         */

        val sett = realm.where(Settings::class.java).findFirst()!!
        view.findViewById<TextView>(R.id.semStart).text = sett.semStart
        view.findViewById<TextView>(R.id.seg1).text = sett.seg1End
        view.findViewById<TextView>(R.id.seg2).text = sett.seg2End
        view.findViewById<TextView>(R.id.seg3).text = sett.seg3End
        view.findViewById<CheckBox>(R.id.notifCheck).isChecked = sett.sendNotif
        return view
    }

    private fun dateSetter(context:Context, view:TextView) : View.OnClickListener{
        return View.OnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, mnth, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, mnth)
                cal.set(Calendar.DAY_OF_MONTH, day)
                val mn = if(mnth < 9) "0"+(mnth+1) else (mnth+1).toString()
                val dy = if(day < 10) "0$day" else "$day"
                val mDate = String.format("%s/%s/%d", dy, mn, year)
                view.text = mDate
            }
            DatePickerDialog(
                context, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                    Calendar.DAY_OF_MONTH
                )
            ).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

            when (requestCode) {
                200 -> {
                    val sett = realm.where(Settings::class.java).findFirst()!!
                    realm.beginTransaction()
                    sett.sendNotif = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    realm.commitTransaction()
                }
            }
    }

    /*private fun isExternalStorageReadOnly():Boolean {
        val extStorageState = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED_READ_ONLY == (extStorageState)) {
            return true
        }
        return false
    }

    private fun isExternalStorageAvailable():Boolean {
        val extStorageState = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == (extStorageState)) {
            return true
        }
        return false
    }

    private fun checkPer(context:Context?, t:Fragment):Boolean{
        if(context != null && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) return true
            ActivityCompat.requestPermissions(t.activity as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            2)
        return false
    }



        private fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String?>?,
        grantResults: IntArray
    ) {
        when (permsRequestCode) {
            200 -> {
                sendNotif = grantResults[0] == PackageManager.PERMISSION_GRANTED

            }
        }
    }
    */

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
