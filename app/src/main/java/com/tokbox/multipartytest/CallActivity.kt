package com.tokbox.multipartytest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.opentok.android.Subscriber
import kotlinx.android.synthetic.main.activity_call.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast


class CallActivity : AppCompatActivity() {

    class SubscriberData (val containerView: RelativeLayout) {
        var otSubscriber: Subscriber? = null
    }

    val TAG: String = "Informacion"
    var messageViewOpen = true
    lateinit var messageView: CardView
    lateinit var imageBtn: ImageButton
    lateinit var messageText: EditText
    private var decorView: View? = null

    private val PERMISSION_REQUEST_CODE = 123
    private var subContainerArray : Array<SubscriberData> = emptyArray()

    private lateinit var videoCall: RelativeLayout

    private val hydeSystemBar = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        messageView = findViewById(R.id.messageView)
        imageBtn = findViewById(R.id.imageButton)
        messageViewChange()
        val messageBtn = findViewById<FloatingActionButton>(R.id.messageButton)
        messageText = findViewById(R.id.editMessage)
        messageBtn.setOnClickListener { view: View? ->
            messageViewChange()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }

        videoCall = findViewById(R.id.callView)
        videoCall.setOnClickListener { view: View? ->
            //messageView.visibility = View.GONE
            //messageViewOpen = false
        }
        messageText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) imageBtn.visibility = View.GONE
            else imageBtn.visibility = View.VISIBLE
        }

        decorView = window.decorView
        decorView?.systemUiVisibility = hydeSystemBar

        decorView?.setOnSystemUiVisibilityChangeListener { visibility: Int ->
            Log.d(TAG, "Bar")
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView?.systemUiVisibility = hydeSystemBar
                Log.d(TAG, "Bar if")
            }
        }

        finishCall.onClick {
            finish()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(
                            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                    ),
                    PERMISSION_REQUEST_CODE
            )
        } else {
            startViewModel()
        }

    }


    private fun startViewModel(){
        val viewModel: ViewModelCall by viewModels()
        val callObserver = Observer<View> { viewToShow ->
            val screenSize = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT)
            if (viewToShow.parent != null){
                (viewToShow.parent as ViewGroup).removeView(viewToShow)
            }
            videoCall.addView(viewToShow, screenSize)
        }
        viewModel.getView().observe(this, callObserver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startViewModel()
        } else {
            toast("App does not have permission to record video and audio. Exiting")
            finish()
        }
    }

    private fun messageViewChange() {
        if (messageViewOpen) {
            messageView.visibility = View.GONE
            messageViewOpen = false
        } else {
            messageView.visibility = View.VISIBLE
            messageViewOpen = true
        }
    }
}
