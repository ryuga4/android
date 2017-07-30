package com.jankun.deals

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.beust.klaxon.JSON
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel


import com.github.kittinunf.fuel.httpGet
import com.github.salomonbrys.kotson.toJsonArray


import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        super.onCreate(savedInstanceState)


        if (isNetworkConnected()){
            thread {
                //addUserGET(android_id,"NAME")
                init(android_id)
            }
        } else {

        }






    }


    fun init(android_id : String) {
        if (getUsers().any{it.get("id")==android_id}) startActivity<Logged>()
        else {
            runOnUiThread {
                verticalLayout {
                    val e = editText("Name") {

                    }
                    button("Rejestruj") {
                        onClick {
                            addUserGET(android_id,e.text.toString())
                            startActivity<Logged>()
                        }
                    }

                }
            }
        }
    }

    fun addFriend(id1 : String, id2: String) {
        Fuel.get("http://cljs-jan.herokuapp.com/add-friend?id1=$id1&id2=$id2").responseString{_,_,result ->
            Log.d("ADDED FRIEND",result.get())
        }
    }

    fun addUserGET(id : String, name : String) {
        Fuel.get("http://cljs-jan.herokuapp.com/add-user?name=$name&id=$id").responseString{_,_,result ->

            //Log.d("ADDED USER",result.get())
        }
    }

    fun getUsers() : JsonArray<JsonObject> {

        val (request, response, result) = "http://cljs-jan.herokuapp.com/users".httpGet().responseString()

        return sToJsonArray(result.get())
    }

    fun propose(id1 :String,id2 : String,money : Double) {
        Fuel.get("http://cljs-jan.herokuapp.com/propose?id1=$id1&id2=$id2&money=$money").responseString{_,_,result ->
            Log.d("Proposed",result.get())
        }
    }
    fun accept(id1 :String,id2 : String) {
        Fuel.get("http://cljs-jan.herokuapp.com/accept?id1=$id1&id2=$id2").responseString{_,_,result ->
            Log.d("Accepted",result.get())
        }
    }

    fun checkDeal(id1 : String, id2 : String) {
        Fuel.get("http://cljs-jan.herokuapp.com/check-deal?id1=$id1&id2=$id2").responseString{_,_,result ->
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