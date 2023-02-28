# RedrockSSO
 红岩网校飞书 SSO 登录工具 Android 端
 
## 使用方法
### 1、先在 build.gradle 中添加
```kotlin
android {
  packagingOptions {
    resources {
      excludes += '/META-INF/{AL2.0,LGPL2.1}'
      excludes += "/META-INF/INDEX.LIST"
      excludes += "/META-INF/io.netty.versions.properties"
    }
  }
}
```

### 2、在代码中使用
```kotlin
val layout = findViewById<FrameLayout>(R.id.layout)
findViewById<Button>(R.id.btn_sso_login).apply {
  setOnClickListener {
    lifecycleScope.launch {
      val data = SSOUtils.login(this@MainActivity, layout)
      Toast.makeText(this@MainActivity, data.toString(), Toast.LENGTH_SHORT).show()
    }
  }
}
```