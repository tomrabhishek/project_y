package com.example.project_y

import android.content.*
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ServiceCompat.stopForeground
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkOverlayPermission()
        startService()
    }

//     method for starting the service
    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this, ForegroundService::class.java))
                } else {
                    startService(Intent(this, ForegroundService::class.java))
                }
            }
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    // method to ask user to grant the Overlay permission
    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                requestOverlayDisplayPermission()
            }
        }
    }

    private fun requestOverlayDisplayPermission() {
        // An AlertDialog is created
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // This dialog can be closed, just by
        // taping outside the dialog-box
        builder.setCancelable(false)

        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed")

        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.")

        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings",
            DialogInterface.OnClickListener { dialog, which -> // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                        "package:$packageName"
                    )
                )

                // This method will start the intent. It takes two parameter,
                // one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK)
            })

        // The Dialog will show in the screen
        builder.show()
    }


    // check for permission again when user grants it from
    // the device settings, and start the service
    override fun onResume() {
        super.onResume()
        startService()
    }
}