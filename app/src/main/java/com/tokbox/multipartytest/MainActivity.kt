package com.tokbox.multipartytest

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*;
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_join_room.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity<CallActivity>()
                /*
                doAsync {
                    val sessData = getSessionData(input_room_name.text.toString())
                    uiThread {
                        val act = Intent(this@MainActivity, RoomActivity::class.java)
                        act.putExtra("sessionData", sessData)
                        startActivity(act)
                    }
                }
                */
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    fun getSessionData(roomName: String): SessionData {
        val jsonText = URL("https://opentokdemo.tokbox.com/room/$roomName/info").readText()
        val jsonObj = JSONObject(jsonText)

        return SessionData(jsonObj.getString("sessionId"),
                jsonObj.getString("token"),
                jsonObj.getString("apiKey"))
    }
}
