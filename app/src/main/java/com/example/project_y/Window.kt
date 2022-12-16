package com.example.project_y

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*
import androidx.core.app.ServiceCompat
import androidx.core.app.ServiceCompat.stopForeground


class Window(  // declaring required variables
 private val context: Context) {
    private var LAYOUT_TYPE = 0;
    private val mView: View
    private var mParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager
    private val layoutInflater: LayoutInflater

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
                // than filling the screen
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.RGBA_F16
            )
        }
        // getting a LayoutInflater
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.popup_window, null)
        // set onClickListener on the remove button, which removes
        // the view from the window
        mView.findViewById<View>(R.id.window_close).setOnClickListener { close() }
        // Define the position of the
        // window within the screen
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        mParams!!.gravity = Gravity.TOP or Gravity.START;
        mParams!!.x = 0 // Initial Position of window
        mParams!!.y = 100 // Initial Position of window



        mView.findViewById<View>(R.id.root_popup_window)
            .setOnTouchListener(object : View.OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    Log.d("AD", "Action E$event")
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d("AD", "Action Down")
                            initialX = mParams!!.x
                            initialY = mParams!!.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            Log.d("AD", "Action Up")
                            val Xdiff = (event.rawX - initialTouchX).toInt()
                            val Ydiff = (event.rawY - initialTouchY).toInt()
                            if (Xdiff < 10 && Ydiff < 10) {
//                                if (mView.isViewCollapsed()) {
//                                    collapsedView.setVisibility(View.GONE)
//                                    expandedView.setVisibility(View.VISIBLE)
//                                }
                            }
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            Log.d("AD", "Action Move")
                            mParams!!.x = initialX + (event.rawX - initialTouchX).toInt()
                            mParams!!.y = initialY + (event.rawY - initialTouchY).toInt()
                            mWindowManager.updateViewLayout(mView, mParams)
                            return true
                        }
                    }
                    return false
                }
            })

        mView.findViewById<View>(R.id.window_close).setOnClickListener {
            close()
            context.stopService(Intent(context, ForegroundService::class.java))
        }

    }

    fun open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView.windowToken == null) {
                if (mView.parent == null) {
                    mWindowManager.addView(mView, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    fun close() {
        try {
            // remove the view from the window
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            // invalidate the view
            mView.invalidate()
            // remove all views
            (mView.parent as ViewGroup).removeAllViews()

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }
}
