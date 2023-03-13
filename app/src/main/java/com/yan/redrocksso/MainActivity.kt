package com.yan.redrocksso

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.redrock.sso.Jwt
import com.yan.RedrockSSO.R
import com.redrock.sso.SSOUtils
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val layout = findViewById<FrameLayout>(R.id.layout)
    
        findViewById<Button>(R.id.btn_sso_login).apply {
            setOnClickListener {
                Toast.makeText(this@MainActivity, "SSO登陆", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    val data = SSOUtils.login(layout)
                    Toast.makeText(this@MainActivity, data.toString(), Toast.LENGTH_SHORT).show()
                    val token = SSOUtils.token(data.code)
//                    Log.e("token:",token.toString())
                    val account = Jwt.parseToken(token!!.access_token)
                    Log.e("account",account.toString())
                }
            }
        }
    }
}