package es.usj.mastertsa.onunez.session301

import android.R
import android.net.Uri
import android.os.Build.VERSION_CODES.Q
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import es.usj.mastertsa.onunez.session301.databinding.ActivityFourthBBinding

class FourthBActivity : AppCompatActivity() {

    @RequiresApi(Q)
    private fun retrievePhotoList() {
        val externalUri: Uri =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
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
            val photoUri: Uri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                idColumn?.let { cursor.getString(it) }
            )
            files.add(photoUri)
        }
        cursor?.close()
    }

    private val files : MutableList<Uri> = mutableListOf()
    private lateinit var bindings : ActivityFourthBBinding

    @RequiresApi(Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityFourthBBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        retrievePhotoList()
        bindings.lvPhotos.adapter = ArrayAdapter(this,
            R.layout.simple_list_item_1, files.map { it.encodedPath
            })
        bindings.lvPhotos.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                bindings.ivSelectedPhoto.setImageURI(files[position])
            }
    }
}

