package com.jankun.deals

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel


import com.github.kittinunf.fuel.httpGet



import org.jetbrains.anko.*

import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        thread {
            addUserGET("ąąą","BBBBBBBb")

        }





        verticalLayout {
            padding = dip(30)
            editText {
                hint = "Name"
                textSize = 24f
            }
            editText {
                hint = "Password"
                textSize = 24f
            }
            button("Login") {
                textSize = 26f
            }
        }
    }

    private fun addFriend(id1 : String, id2: String) {
        Fuel.get("http://cljs-jan.herokuapp.com/add-friend?id1=$id1&id2=$id2").responseString{_,_,result ->
            Log.d("ADDED FRIEND",result.get())
        }
    }

    private fun addUserGET(id : String, name : String) {
        Fuel.get("http://cljs-jan.herokuapp.com/add-user?name=$name&id=$id").responseString{_,_,result ->
            Log.d("ADDED USER",result.get())
        }
    }

    private fun getUsers() : JsonArray<JsonObject> {

        val (request, response, result) = "http://cljs-jan.herokuapp.com/users".httpGet().responseString()
        val ret = "[ "+result.get().replace(":([^ ]*)".toRegex()) {
                "\"${it.groupValues[1]}\" :"
        }.replace(", \"_id\".*>".toRegex()){""}.replace("\\}\\{".toRegex()){"}, {"} +
         "]"

        return sToJson(ret)
    }
    private fun sToJson(s : String) : JsonArray<JsonObject> {
        val parser: Parser = Parser()
        val stringBuilder: StringBuilder = StringBuilder(s)
        val json = parser.parse(stringBuilder) as JsonArray<JsonObject>
        return json
    }


    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        return networkInfo != null && networkInfo.isConnected // 3
    }

}