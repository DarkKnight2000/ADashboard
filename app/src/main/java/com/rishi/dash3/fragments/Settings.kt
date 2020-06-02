package com.rishi.dash3.fragments


import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.JsonReader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.rishi.dash3.R
import com.rishi.dash3.activties.MainActivity
import com.rishi.dash3.utils.getSeg
import com.rishi.dash3.utils.isGreaterDate
import com.rishi.dash3.models.EachClass
import com.rishi.dash3.models.EachCourse
import com.rishi.dash3.models.Settings
import com.rishi.dash3.utils.*
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.*

class Settings : Fragment() {

    lateinit var realm: Realm
    private val fileName = "MyTimetable.txt"

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
            val permissionCheck = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WAKE_LOCK)
            sett.sendNotif = permissionCheck == PackageManager.PERMISSION_GRANTED
            realm.commitTransaction()
        }
        //context?.startService(Intent(context, NotifService::class.java))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inpUriStr = arguments?.getString("uri", "")
        if(inpUriStr != null && inpUriStr != "") {
            arguments?.putString("uri", null)
            Log.i("test0", inpUriStr.toString())
            try {
                val inpUri = Uri.parse(inpUriStr)
                Log.i("test0", inpUri.toString())
                if (inpUri != null) {
                    importFile(
                        InputStreamReader(
                            ObjectInputStream(
                                activity!!.contentResolver.openInputStream(
                                    inpUri
                                )
                            )
                        )
                    )
                }
            } catch (e: Exception) {

            }
        }


        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val ids = arrayOf(R.id.semStart, R.id.seg1, R.id.seg2, R.id.seg3)
        var s:TextView
        for(id in ids){
            s = view.findViewById(id)
            s.setOnClickListener(dateSetter(this.context!!, s))
        }
        view.findViewById<Button>(R.id.updateSem).setOnClickListener{
            if(isGreaterDate(
                    seg1.text.toString(),
                    seg2.text.toString()
                ) || isGreaterDate(
                    seg2.text.toString(),
                    seg3.text.toString()
                ) || isGreaterDate(
                    semStart.text.toString(),
                    seg1.text.toString()
                )
            ){
                Toast.makeText(this.context!!, "Dates are not in chronological order", Toast.LENGTH_SHORT).show()
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
                c.day = c.day.substring(0,4) + getSeg(
                    c.date,
                    sett.semStart,
                    sett.seg1End,
                    sett.seg2End,
                    sett.seg3End
                )
            }
            realm.commitTransaction()
            Toast.makeText(context!!, "Updated", Toast.LENGTH_SHORT).show()
            if(sett.sendNotif) restartNotifService(this.context!!)
        }

        view.findViewById<Button>(R.id.reset).setOnClickListener{
            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle("Warning!")
            builder.setMessage("This will delete all courses and their classes and cannot be undone!!")

            builder.setPositiveButton("DELETE EVERYTHING"){_, _ ->
                realm.beginTransaction()
                realm.where(EachClass::class.java).findAll().deleteAllFromRealm()
                realm.where(EachCourse::class.java).findAll().deleteAllFromRealm()
                realm.commitTransaction()
                Toast.makeText(this.context!!,"Reset successful!!",Toast.LENGTH_LONG).show()
                //stopNotifService(this.context!!)
                view.findViewById<CheckBox>(R.id.notifCheck).isChecked = false
            }
            builder.setNegativeButton("Cancel"){_,_ -> return@setNegativeButton}
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.show()
        }

        view.findViewById<CheckBox>(R.id.themeChanger).isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        view.findViewById<CheckBox>(R.id.themeChanger).setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                val sharedPref = activity?.getSharedPreferences(activity?.packageName + "_sharedprefs", Context.MODE_PRIVATE)
                val editor = (sharedPref?.edit())
                editor?.putInt("ThemePref", AppCompatDelegate.MODE_NIGHT_YES)
                editor?.apply()
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                val sharedPref = activity?.getSharedPreferences(activity?.packageName + "_sharedprefs", Context.MODE_PRIVATE)
                val editor = (sharedPref?.edit())
                editor?.putInt("ThemePref", AppCompatDelegate.MODE_NIGHT_NO)
                editor?.apply()
            }

            activity!!.finish()
            startActivity(Intent(activity, MainActivity::class.java))
        }

        view.findViewById<CheckBox>(R.id.notifCheck).setOnCheckedChangeListener { _, b ->
            if(b){
                restartNotifService(context!!)
                // Requesting permission in lower android versions
                /*val permissionCheck = ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.WAKE_LOCK)

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    val permsRequestCode = 200

                    ActivityCompat.requestPermissions(
                        this.activity!!,
                        arrayOf(Manifest.permission.WAKE_LOCK), permsRequestCode)
                } else {*/
                    val sett = realm.where(Settings::class.java).findFirst()!!
                    realm.beginTransaction()
                    sett.sendNotif = true
                    realm.commitTransaction()
                //}
            }
            else{
                val sett = realm.where(Settings::class.java).findFirst()!!
                realm.beginTransaction()
                sett.sendNotif = false
                realm.commitTransaction()
                stopNotifService(context!!)
                Toast.makeText(context!!, "Notifications turned off", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.exportData).setOnClickListener {
            try {

                val myExternalFile = File(context!!.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                //Log.i("test0", "${context!!.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}")
                //if(!myExternalFile.exists()) myExternalFile.createNewFile()
                val fos = ObjectOutputStream(FileOutputStream(myExternalFile))
                writeCourses(fos, realm)

                val install = Intent(Intent.ACTION_SEND)
                //install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                val apkURI = FileProvider.getUriForFile(
                    context!!, context!!.applicationContext
                        .packageName.toString() + ".fileprovider", myExternalFile
                )
                install.putExtra(Intent.EXTRA_STREAM, apkURI)
                install.setType("application/json")
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //install.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myExternalFile))
                startActivity(Intent.createChooser(install, "Share data to.."))

            } catch (e: IOException) {
                Toast.makeText(context!!, "There was an error while sharing...", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        view.findViewById<Button>(R.id.importData).setOnClickListener {

            val intent = Intent()
                .setType("text/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
        }

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

    /*override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

            when (requestCode) {
                200 -> {
                    val sett = realm.where(Settings::class.java).findFirst()!!
                    realm.beginTransaction()
                    sett.sendNotif = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if(sett.sendNotif) restartNotifService(this.context!!)
                    else stopNotifService(this.context!!)
                    realm.commitTransaction()
                }
            }
    }*/

    private fun isExternalStorageReadOnly(): Boolean {
        val extStorageState: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }

    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val inputFileUri = data!!.data //The uri with the location of the file
            val a = ObjectInputStream(activity!!.contentResolver.openInputStream(inputFileUri))
            val b = InputStreamReader(a, "UTF-8")
            importFile(b)
        }
    }

    fun importFile(b:InputStreamReader?){
        val newSettings: Settings
        val readCrses: List<EachCourse>
        if(b != null) {
            val objInpStream = JsonReader(b)
            try {
                newSettings = readSettings(objInpStream)
                readCrses = readAllCourses(objInpStream)
                Log.i("test0", readCrses.toString())
                Toast.makeText(context!!, "Done reading data", Toast.LENGTH_SHORT).show()
            }
            catch(e:IOException){
                Toast.makeText(context!!, "Invalid data file", Toast.LENGTH_LONG).show()
                return
            }

            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle("Warning!")
            builder.setMessage("This will delete all current courses and import from input file!!")

            builder.setPositiveButton("Continue"){_, _ ->
                newSettings.sendNotif = realm.where(Settings::class.java).findFirst()!!.sendNotif
                realm.beginTransaction()
                realm.where(EachClass::class.java).findAll().deleteAllFromRealm()
                realm.where(EachCourse::class.java).findAll().deleteAllFromRealm()
                realm.where(Settings::class.java).findAll().deleteAllFromRealm()
                realm.copyToRealm(readCrses)
                realm.copyToRealm(newSettings)
                realm.commitTransaction()
                Toast.makeText(this.context!!,"Import successful!!",Toast.LENGTH_LONG).show()
                if(realm.where(Settings::class.java).findFirst()!!.sendNotif) restartNotifService(
                    this.context!!
                )

                val frag = activity!!.supportFragmentManager.findFragmentByTag("sets")!!
                activity!!.supportFragmentManager.beginTransaction()
                    .detach(frag)
                    .attach(frag)
                    .commitAllowingStateLoss()
                Log.i("test0", "notifs ${realm.where(Settings::class.java).findFirst()!!.sendNotif}")
                if(newSettings.sendNotif) restartNotifService(context!!)
            }
            builder.setNegativeButton("Cancel"){_,_ -> return@setNegativeButton}
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCanceledOnTouchOutside(true)
            alertDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
