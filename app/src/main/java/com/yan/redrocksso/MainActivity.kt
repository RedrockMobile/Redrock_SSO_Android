package com.yan.redrocksso

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.yan.RedrockSSO.R
import com.yan.lib.sso.HttpService
import com.yan.lib.sso.SSOActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HttpService.start()
        findViewById<Button>(R.id.btn_sso_login).apply {
            setOnClickListener {
                Toast.makeText(this@MainActivity, "SSO登陆", Toast.LENGTH_SHORT).show()
                SSOActivity.startSSOActivity(this@MainActivity){ s1,s2 ->
                    Log.e("s1:",s1.toString())
                    Log.e("s2:",s2.toString())
                }
            }
        }
    }
}