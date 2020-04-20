package com.tokbox.multipartytest

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.opentok.android.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL

class ViewModelCall(application: Application): AndroidViewModel(application), Session.SessionListener{
    //Sacar el context
    private val context: Context = getApplication<Application>().applicationContext
    private val subscriber: MutableLiveData<View> by lazy {
        getCall()
    }
    private val TAG: String = "Information"
    private var otSession : Session?= null
    private var otPub : Publisher? = null

    private lateinit var sessData: SessionData

    fun getView(): LiveData<View>{
        return subscriber
    }

    private fun getCall(): MutableLiveData<View>{
        doAsync {
            sessData = getSessionData("testmultiparty")
            uiThread {
                createAndConnectSession()
            }
        }
        return MutableLiveData<View>()
    }

    private fun getSessionData(roomName: String): SessionData {
        val jsonText = URL("https://opentokdemo.tokbox.com/room/$roomName/info").readText()
        val jsonObj = JSONObject(jsonText)

        return SessionData(jsonObj.getString("sessionId"),
                jsonObj.getString("token"),
                jsonObj.getString("apiKey"))
    }


    private fun createAndConnectSession() {
        otSession = Session.Builder(context, sessData.apiKey, sessData.sessionId).sessionOptions(object: Session.SessionOptions() {
            override fun useTextureViews(): Boolean {
                return true
            }
        }).build()
        otSession?.setSessionListener(this)
        otSession?.connect(sessData.token)

    }

    override fun onError(sess: Session?, error: OpentokError?) {
        error("Session connection error $error")
    }

    override fun onConnected(sess: Session?) {
        Log.i(TAG, "Connected")
        otPub = Publisher.Builder(context).build()
        otPub?.let {
            it.publishAudio = false
            it.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FILL)
            otSession?.publish(it)
            //addVideoView(publisher_container, it.view)
        }
    }

    override fun onStreamReceived(sess: Session?, stream: Stream?) {
        val sub = Subscriber.Builder(context, stream).build()
        sub.subscribeToAudio = false
        sub.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL)
        otSession?.subscribe(sub)
        //subContainerArray[it].otSubscriber = sub
        //addVideoView(subContainerArray[it].containerView, sub.view)
        subscriber.value = sub.view
        return
    }

    override fun onStreamDropped(sess: Session?, stream: Stream?) {
        subscriber.value = null
    }

    override fun onDisconnected(p0: Session?) {

    }

    override fun onCleared() {
        super.onCleared()
        otSession?.disconnect()
        Log.i(TAG, "onCleared")
    }
}