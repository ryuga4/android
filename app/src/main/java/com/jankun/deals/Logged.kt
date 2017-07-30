package com.jankun.deals

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import org.jetbrains.anko.button
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import kotlin.concurrent.thread

class Logged : AppCompatActivity() {

    private var mTextMessage: TextView? = null

    private val mOnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {

            when (item.itemId) {
                R.id.navigation_home -> {
                    mTextMessage!!.setText(R.string.title_home)
                    friends()
                    return true
                }
                R.id.navigation_dashboard -> {
                    mTextMessage!!.setText(R.string.title_dashboard)
                    return true
                }
                R.id.navigation_notifications -> {
                    mTextMessage!!.setText(R.string.title_notifications)
                    return true
                }
            }
            return false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged)

        mTextMessage = findViewById(R.id.message) as TextView
        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
    fun friends() {
        val android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        verticalLayout {
            thread {
                getOne(android_id).map.forEach { s, any ->
                    runOnUiThread {
                        linearLayout {
                            textView(s)
                            textView(any.toString())
                        }
                    }
                }
            }
        }
    }

    fun propositions() {

    }
    fun addfriend() {

    }


    fun addFriend(id1 : String, id2: String) {
        Fuel.get("http://cljs-jan.herokuapp.com/add-friend?id1=$id1&id2=$id2").responseString{ _, _, result ->
            Log.d("ADDED FRIEND",result.get())
        }
    }

    fun addUserGET(id : String, name : String) {
        Fuel.get("http://cljs-jan.herokuapp.com/add-user?name=$name&id=$id").responseString{ _, _, result ->

            //Log.d("ADDED USER",result.get())
        }
    }

    fun getUsers() : JsonArray<JsonObject> {

        val (request, response, result) = "http://cljs-jan.herokuapp.com/users".httpGet().responseString()

        return sToJsonArray(result.get())
    }

    fun propose(id1 :String,id2 : String,money : Double) {
        Fuel.get("http://cljs-jan.herokuapp.com/propose?id1=$id1&id2=$id2&money=$money").responseString{ _, _, result ->
            Log.d("Proposed",result.get())
        }
    }
    fun accept(id1 :String,id2 : String) {
        Fuel.get("http://cljs-jan.herokuapp.com/accept?id1=$id1&id2=$id2").responseString{ _, _, result ->
            Log.d("Accepted",result.get())
        }
    }

    fun checkDeal(id1 : String, id2 : String) {
        Fuel.get("http://cljs-jan.herokuapp.com/check-deal?id1=$id1&id2=$id2").responseString{ _, _, result ->
            Log.d("Checked",result.get())
        }
    }

    fun getOne(id : String) : JsonObject {
        val (request, response, result) = "http://cljs-jan.herokuapp.com/get-one?id=$id".httpGet().responseString()

        return sToJsonObject(result.get())
    }
    fun getOneProp(id : String) : JsonObject {
        val (request, response, result) = "http://cljs-jan.herokuapp.com/get-one-prop?id=$id".httpGet().responseString()

        return sToJsonObject(result.get())
    }
    fun sToJsonArray(s : String) : JsonArray<JsonObject> {
        val parser: Parser = Parser()
        val stringBuilder: StringBuilder = StringBuilder(s)
        val json = parser.parse(stringBuilder) as JsonArray<JsonObject>
        return json
    }
    fun sToJsonObject(s : String) : JsonObject {
        val parser: Parser = Parser()
        val stringBuilder: StringBuilder = StringBuilder(s)
        val json = parser.parse(stringBuilder) as JsonObject
        return json
    }



    fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        return networkInfo != null && networkInfo.isConnected // 3
    }

}
