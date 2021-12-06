package es.usj.mastertsa.onunez.session301

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.JsonReader
import android.view.Gravity
import android.widget.Toast
import es.usj.mastertsa.onunez.session301.databinding.ActivitySeventhBinding
import kotlinx.android.synthetic.main.activity_seventh.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException

class SeventhActivity : AppCompatActivity() {
    companion object {
        private const val URL = "https://www.dropbox.com/s/2psyzixpaq26s7k/countries.json?dl=1"
    }
    private var downloadManager: DownloadManager? = null
    private var downloadReference: Long = 0
    private val bindings : ActivitySeventhBinding by lazy {
        ActivitySeventhBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seventh)
        startDownload.setOnClickListener { download() }
        displayDownload.setOnClickListener { display() }
        checkStatus.setOnClickListener { check() }
        cancelDownload.setOnClickListener { cancel() }
    }

    private fun download() {
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(URL)
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverRoaming(false)
        request.setTitle("My Data Download")
        request.setDescription("Android Data download using DownloadManager.")
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS,"countries.json")
        downloadReference = downloadManager!!.enqueue(request)
        bindings.countryData.text = "Getting data from Server, Please WAIT..."
        bindings.checkStatus.isEnabled = true
        bindings.cancelDownload.isEnabled = true
    }

    private fun display() {
        val intent = Intent()
        intent.action = DownloadManager.ACTION_VIEW_DOWNLOADS
        startActivity(intent)
    }
    private fun check() {
        val myDownloadQuery = DownloadManager.Query()
        myDownloadQuery.setFilterById(downloadReference)
        val cursor = downloadManager!!.query(myDownloadQuery)
        if (cursor.moveToFirst()) {
            checkStatus(cursor)
        }
    }
    private fun cancel() {
        downloadManager!!.remove(downloadReference)
        bindings.checkStatus.isEnabled = false
        bindings.countryData.text = "Download of the file cancelled..."
    }

    private fun checkStatus(cursor: Cursor) {
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status = cursor.getInt(columnIndex)
        val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        val reason = cursor.getInt(columnReason)
        val filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
        val filename = cursor.getString(filenameIndex)
        var statusText = ""
        var reasonText = ""
        when (status) {
            DownloadManager.STATUS_FAILED -> {
                statusText = "STATUS_FAILED"
               reasonText = when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> "ERROR_CANNOT_RESUME"
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> "ERROR_DEVICE_NOT_FOUND"
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "ERROR_FILE_ALREADY_EXISTS"
                    DownloadManager.ERROR_FILE_ERROR -> "ERROR_FILE_ERROR"
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> "ERROR_HTTP_DATA_ERROR"
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> "ERROR_INSUFFICIENT_SPACE"
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "ERROR_TOO_MANY_REDIRECTS"
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "ERROR_UNHANDLED_HTTP_CODE"
                    DownloadManager.ERROR_UNKNOWN -> "ERROR_UNKNOWN"
                   else -> { "ERROR_UNKNOWN" }
               }
            }
            DownloadManager.STATUS_PAUSED -> {
                statusText = "STATUS_PAUSED"
                reasonText = when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> "PAUSED_QUEUED_FOR_WIFI"
                    DownloadManager.PAUSED_UNKNOWN -> "PAUSED_UNKNOWN"
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> "PAUSED_WAITING_FOR_NETWORK"
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> "PAUSED_WAITING_TO_RETRY"
                    else -> { "PAUSED_UNKNOWN" }
                }
            }
            DownloadManager.STATUS_PENDING ->
                statusText = "STATUS_PENDING"
            DownloadManager.STATUS_RUNNING ->
                statusText = "STATUS_RUNNING"
            DownloadManager.STATUS_SUCCESSFUL -> {
                statusText = "STATUS_SUCCESSFUL"
                reasonText = "Filename:\n$filename"
            }
        }
        val toast = Toast.makeText(this@SeventhActivity,
            statusText + "\n" + reasonText, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 25, 400)
        toast.show()
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val referenceId =
                intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadReference == referenceId) {
                bindings.cancelDownload.isEnabled = false
                var ch: Int
                val file: ParcelFileDescriptor
                val strContent = StringBuffer("")
                val countries = StringBuffer("")
                try {
                    file = downloadManager!!.openDownloadedFile(downloadReference)
                    val fileInputStream = ParcelFileDescriptor.AutoCloseInputStream(file)
                    ch = fileInputStream.read()
                    while (ch != -1) {
                        strContent.append(ch.toChar())
                        ch = fileInputStream.read()
                    }
                    val responseObj = JSONObject(strContent.toString())
                    val countriesObj = responseObj.getJSONArray("countries")
                    for (i in 0 until countriesObj.length()) {
                        val countryInfo = countriesObj.getJSONObject(i).toString()
//                        val country = Gson().fromJson<Country>(countryInfo, Country::class.java)
//                        countries.append(country.name + ": " + country.code + "\n")
                    }
                    bindings.countryData.text = countries.toString()
                    val toast = Toast.makeText(this@SeventhActivity,"Downloading of data just finished", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 25, 400)
                    toast.show()
                } catch (e: FileNotFoundException) { e.printStackTrace()
                } catch (e: IOException) { e.printStackTrace()
                } catch (e: JSONException) { e.printStackTrace() }
            }
        }
    }
}

data class Country(val name: String?, val code: String?)