/*
 Created by V K on 12/13/20.
 */

package com.example.connectionclient.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.connectionclient.R
import com.example.conectioncall.call.store.AppData
import com.example.conectioncall.call.NetworkStatus
import com.example.conectioncall.call.apicalls.TestAPICall
import com.example.conectioncall.call.protocol.common.*
import com.example.conectioncall.call.protocol.common.exception.Error
import com.example.conectioncall.call.protocol.common.store.ISecureStore
import com.example.conectioncall.call.protocol.common.store.IValueStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class MainActivity : AppCompatActivity(), IPlatformResourceDelegate, IServiceDelegate {

    // To call connectioncall APIs
    private var mNetworkStatus: INetworkStatus? = null
    // To call connectioncall APIs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        // To call connectioncall APIs
        initializePlatformResources()
        Service.sharedInstance().initializeServiceEnvironment(applicationContext, R.raw.environment)
        // To call connectioncall APIs

        callTestAPI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun callTestAPI() {

        val testApiCall =
            TestAPICall()
        Service.sharedInstance().send(testApiCall)

        Service.sharedInstance().send(testApiCall, IResponseDelegate {

            if (it.containsKey(Error.Key)) {
                Log.d("TestAPI", "Error")
            } else {

                Log.d("TestAPI response", it["status"].toString())
            }

        })
    }

    // To call connectioncall APIs
    override fun getValueStore(): IValueStore? {
        return AppData.getInstance().valueStore()
    }

    override fun getSecureStore(): ISecureStore? {
        return AppData.getInstance().secureStore()
    }

    override fun getNetworkStatus(): INetworkStatus? {
        return mNetworkStatus
    }

    override fun onEvent(event: Service.Event?, info: HashMap<String, Any>?) {
        TODO("Not yet implemented")
    }

    private fun initializePlatformResources() {
        if (mNetworkStatus == null) {
            mNetworkStatus =
                NetworkStatus(applicationContext)
        }
        Service.sharedInstance().setPlatformResourceDelegate(this)
    }

    // To call connectioncall APIs

}