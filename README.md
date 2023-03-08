# RedrockSSO
 红岩网校飞书 SSO 登录工具 Android 端
 
## 使用方法
### 1、settings.gradle
```groovy
dependencyResolutionManagement {
  repositories {
    maven { url "https://jitpack.io" }
  }
}
```

### 2、build.gradle
[![](https://jitpack.io/v/RedrockMobile/Redrock_SSO_Android.svg)](https://jitpack.io/#RedrockMobile/Redrock_SSO_Android)
```groovy
dependencies {
  // 版本号请看上方标签
  implementation("com.github.RedrockMobile:Redrock_SSO_Android:x.x")
}
```

### 3、在代码中使用
```kotlin
val layout = findViewById<FrameLayout>(R.id.layout)
findViewById<Button>(R.id.btn_sso_login).apply {
  setOnClickListener {
    lifecycleScope.launch {
      val data = SSOUtils.login(layout)
      Toast.makeText(this@MainActivity, data.toString(), Toast.LENGTH_SHORT).show()
    }
  }
}
```

## 留给后人
如果有修改，请发布对应的 release 和 jitpack 依赖。jitpack 依赖只需要打开 jitpack 官网输入该仓库地址即可，不需要任何权限和账号，任何人都可以发布

### 才发现飞书有专门的 SDK
文档地址：https://open.feishu.cn/document/uAjLw4CM/uYjL24iN/mobile-app/sdk  
获取用户信息：https://open.feishu.cn/document/common-capabilities/sso/api/get-user-info  
算了，飞书提供的 SDK 很简单，自己用原生 Socket 手搓服务器也算学到了一些东西，就懒得改它了🤣