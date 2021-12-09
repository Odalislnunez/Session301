package es.usj.mastertsa.onunez.session301

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import es.usj.mastertsa.onunez.session301.databinding.ActivityFifthABinding

class FifthAActivity : AppCompatActivity() {
    private var permissionGranted = false
    private var uri: Uri? = null
    private val bindings: ActivityFifthABinding by lazy {
        ActivityFifthABinding.inflate(layoutInflater) }

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            with(bindings.root) {
                when {
                    granted -> {
                        snackBar("Permission granted!")
                        recordVideo()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    -> {
                        snackBar("Permission denied, show more info!")
                    }
                    else -> snackBar("Permission denied")
                }
            }
        }
    private val takeVideo =
        registerForActivityResult(ActivityResultContracts.TakeVideo()) {
            if (it != null && uri != null) {
                bindings.videoView.setVideoURI(uri)
                Toast.makeText(this, "Video taken!",
                    Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindings.root)
        bindings.btnLoadVideo.setOnClickListener {
            loadVideos()
        }
        bindings.btnPlayVideo.setOnClickListener {
            playVideo()
        }
        bindings.btnRecordVideo.setOnClickListener {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun playVideo() {
        bindings.videoView.setVideoURI(uri)
        bindings.videoView.start()
    }

    private fun recordVideo() {
        val filename = bindings.etFilename.text.toString()
        val videoUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
        val videoDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, filename)
        }
        contentResolver.insert(videoUri, videoDetails).let {
            uri = it
            takeVideo.launch(uri)
        }
    }

    private fun loadVideos() {
        val listIntent = Intent(this@FifthAActivity,
            FifthBActivity::class.java)
        startActivity(listIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
        permissionGranted = when (requestCode) {
            0 -> (grantResults.isNotEmpty() &&
                    grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED)
            else -> false
        }
    }
}

fun View.snackBar(message: String, duration: Int =
    BaseTransientBottomBar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}