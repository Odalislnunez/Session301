package es.usj.mastertsa.onunez.session301

import android.R
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import es.usj.mastertsa.onunez.session301.databinding.ActivityFifthBBinding


class FifthBActivity : AppCompatActivity() {
    private val files : MutableList<Uri> = mutableListOf()
    private lateinit var bindings : ActivityFifthBBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityFifthBBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        retrieveVideoList()
        bindings.lvVideos.adapter = ArrayAdapter(this,
            R.layout.simple_list_item_1, files.map { it.encodedPath
            })
        bindings.lvVideos.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                bindings.videoViewList.setVideoURI(files[position])
                bindings.videoViewList.start()
            }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun retrieveVideoList() {
        val externalUri: Uri =
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.TITLE,
            MediaStore.MediaColumns.RELATIVE_PATH
        )
        val cursor = contentResolver.query(
            externalUri, projection, null, null,
            MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )
        val idColumn: Int? =
            cursor?.getColumnIndex(MediaStore.MediaColumns._ID)
        while (cursor?.moveToNext() == true) {
            val videoUri: Uri = Uri.withAppendedPath(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                idColumn?.let { cursor.getString(it) }
            )
            files.add(videoUri)
        }
        cursor?.close()
    }
}