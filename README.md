
@[toc]

## 更新记录

### 2021-12-06

 - WebView支持多进程多任务显示，类似微信小程序切换效果。WebView最多启动4个任务，当达到4个任务进程时，会将最早启动的进程关闭掉，然后启动一个新的任务进程。

![在这里插入图片描述](https://img-blog.csdnimg.cn/ee6699611b5d439ea240a4683fbb755e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBASC5aV2Vp,size_15,color_FFFFFF,t_70,g_se,x_16)


### 2021-10-20

- 提供接口外部监听使用
- WebView与Activity或Fragment生命周期绑定
- 处理重定向问题，兼容Android7.0以下，设置WebViewCallback#onInterceptLoading(..)


```
// 获取指定命令的实例
val command = CommandHelper.INSTANCE.getCommand<CommandLogin>("login")
// 注册监听，必须在WebView启动前设置监听
command?.registerCommandMonitor(object : ICommandMonitor {
         override fun onMonitor(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface) {
                Toast.makeText(App.INSTANCE, "登录中...", Toast.LENGTH_LONG).show()

          }

        })
        
        
```



 

## 前言概述
几乎所有的App都会用到WebView组件，用WebView承载业务功能也是一种选择，毕竟不用等待应用市场的审核，提升业务上线与bug修复的及时性，但WebView加载业务功能，也有很大的缺陷，体验不好(主要体现在加载、交互上)、耗内存；**耗内存的问题这里提供多进程的设计方案，让WebView在单独的一个进程中运行，这样做的好处是分担主进程的内存压力，另外WebView进程发生崩溃了，也不会影响到主进程的正常运行。**

WebView跨进程通信是本章的核心，另外也基于腾讯X5服务进行了二次封装，先简单总结下这个库的特点：

 - **高可靠**，跨进程通信设计方案，减少主进程的内存压力，降低OOM的概率；即使WebView运行出了问题也不影响主进程。如果不使用多进程方案，WebView也不会不存在内存泄漏问题。
 -  **可扩展**，简化了繁琐的配置可灵活按需设置、Js与原生的交互通信、可以满足activity与fragment的需求，对增加功能很方便。
 -  **预加载**，提前初始化WebView实例。

本章涉及的知识点：

 - Android进程间的Binder通信、AIDL使用。
 - 设计模式-单例模式、Builder模式、命令模式
 - 注解处理器AutoService库，是Google提供的，也是组件化间通信的一种选择。
 - 少许的Js知识

在很多App应用上都会用到多进程，看看我们最熟悉微信，是怎样的

> adb shell "ps |grep keyword"
> keyword：是app的包名

![在这里插入图片描述](https://img-blog.csdnimg.cn/ca308b316bf64a25aed37d0ce87e5527.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2h6dzIwMTc=,size_16,color_FFFFFF,t_70)
com.tencent.mm:appbrand ：小程序进程，微信每启动一个小程序，都会建立一个独立的进程 appbrand[x], 最多开5个进程。

微信将小程序放在独立的进程，其实是向系统再申请一块独立内存来使用，有效突破默认下的内存限制。同时在独立进程中运行，即使小程序Cash了，也不会影响主进程的正常运行，毕竟小程序是开放的，每个开发者写出代码质量是参差不齐的；放在独立进程是很有必要的。

同样地，WebView也是一样的，不同版本的内核可能会不一样，手机厂商可能也会修改定制自己的WebView，各种兼容性问题，每个人写的WebView代码质量都有不同，对内存管理认知程度也不一样，如果因为WebView的Crash影响体验是得不偿失的，因此把WebView放在独立进程也是必要的。

## 基本使用
和启动普通Activity是一样的，如下

```java
 Intent intent = new Intent(this, WebViewActivity.class);
 intent.putExtra(Constants.URL, url);
 intent.putExtra(Constants.TITLE, title);
 intent.putExtra(Constants.JS_OBJECT_NAME,"hYi");
 startActivity(intent);
```
如果需要与Js进行交互的话，必须携带Constants.JS_OBJECT_NAME的参数。如果定制需要可以继承WebViewActivity和WebViewFragment进行修改。

如果不想使用默认的WebViewActivity，可以直接使用封装过WebView，如下：

```java
   mWebView = WebViewManager.newBuilder(this)
                .setRootView(webViewContainer)
                .injectedJsObject(mJsName)
                .setWebUrl(mUrl)
                .setWebViewCallback(this)
                .build()
                .start()
```
其中setViewContainer()是传入WebView的父容器。另外如果此时需要多进程的话，必须在你定制的Activity中声明process属性，如下：
```html
<activity
       android:name=".ui.WebViewActivity"
       android:process=":webview" />
 // 多进程多任务效果
  <activity
            android:name=".ui.WebViewActivity$Small1"
            android:process=":app_small1"
            android:launchMode="singleTask"
            android:taskAffinity=".small1" />

        <activity
            android:name=".ui.WebViewActivity$Small2"
            android:process=":app_small2"
            android:launchMode="singleTask"
            android:taskAffinity=".small2" />

        <activity
            android:name=".ui.WebViewActivity$Small3"
            android:process=":app_small3"
            android:launchMode="singleTask"
            android:taskAffinity=".small3" />

        <activity
            android:name=".ui.WebViewActivity$Small4"
            android:process=":app_small4"
            android:launchMode="singleTask"
            android:taskAffinity=".small4" />
      
```

关于Js与原生交互的功能实现，下面有讲。

## 架构流程
整个架构的流程图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/31ec2be9b5b744a39ee48c1bba1965f5.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2h6dzIwMTc=,size_16,color_FFFFFF,t_70)
核心类

 - WebViewCommandDispatcher：接收到Js的Command命令后，负责将命令分发到main进程处理，实现了ServiceConnection接口。
 - MainCommandManager：负责管理着所有Command命令，并在Main进程接收Web进程分发的Command命令；该类是Main进程上的Binder代理对象。
 -  Command：负责实现具体功能。
 -  ICallbackFromMainToWebInterface：Web进程上的Binder代理对象，将Main进程的结果传递给Web进程中。

 如果在Js中发送命令后，即调用原生函数，需要实现对应的功能，实现Command接口即可，如下：

```kotlin
@AutoService(Command::class)
class CommandShowToast : BaseCommand() {

    override fun commandName(): String {
        return "showToast"
    }

    override fun execCommand(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface) {
        LogUtils.d("executeCommand curr thread "+ Thread.currentThread().name)
        Toast.makeText(App.INSTANCE, parameters["message"].toString(), Toast.LENGTH_SHORT).show()
    }
}
```

在实现类中，需要添加类注解`@AutoService(Command::class)`，目的是确保MainCommandManager能够找到并注册。

在executeCommand()方法中实现功能，其中commandName()方法是定义命令名称，需要与Js发送出来是对应一致的，否则executeCommand()方法是无法触发的，即Js调用原生函数会失败。如下

> 这需要与前端协调商量好，推荐前端直接使用 https://github.com/751496032/hYi-jssdk ，封装Js与Native交互函数，适配了Android与iOS系统，模仿微信公众号jssdk，前端可以通过CDN方式引用Js文件

```javascript
function callAppToast(){
     console.log("callAppToast.");
     window.global.takeNativeAction("showToast", {message: "来自html的消息"});
 }

global.takeNativeAction = function(commandName, parameters){
    console.log("global takeNativeAction")
    var request = {};
    request.name = commandName;
    request.param = parameters;
    window.xxwebview.takeNativeAction(JSON.stringify(request));
}

```
Js发送携带的参数，可以在executeCommand()方法中parameters中取出。

![在这里插入图片描述](https://img-blog.csdnimg.cn/290bb27011e540b6802a45d8c3f906ef.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2h6dzIwMTc=,size_16,color_FFFFFF,t_70)



## 命令模式
其实整个流程就是基于命令模式来实现的，是这个方案的核心。开发者只需关注本身业务的实现，中间的流程无需去关心。

> 关于标准的命令模式可参考  > [标准的命令模式](https://www.runoob.com/design-pattern/command-pattern.html)


![在这里插入图片描述](https://img-blog.csdnimg.cn/c05ca4b4085b42959e5c21901856df8d.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2h6dzIwMTc=,size_16,color_FFFFFF,t_70)

## 预初始化
对于WebView的性能，给人最直观的莫过于：打开速度比native慢。

是的，当我们打开一个WebView页面，页面往往会慢吞吞的loading很久，若干秒后才出现你所需要看到的页面。

对于一个普通用户来讲，打开一个WebView通常会经历以下几个阶段：

 - 打开新页面，初始化阶段（WebView），无任何交互
 - 下载Js脚本、CSS样式，接着Html渲染；然后执行Js脚本，这个阶段是白屏，一直在loading状态。
 - 当Js脚本执行成功后，从后台拿到了数据，最后显示数据。

如果从程序上观察，WebView启动过程大概分为以下几个阶段：
![在这里插入图片描述](https://img-blog.csdnimg.cn/deed4343729f4fd8ad0d2535233732df.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2h6dzIwMTc=,size_16,color_FFFFFF,t_70)

在客户端能处理的更多是在第一阶段，WebView初始化阶段，这里的预加载就是将WebView提前初始化完成。WebViewPool

```java
 public X5WebView getX5WebView(Context context) {
        // 使用栈顶的
        X5WebView webView = mWebViewStackCached.pop();
        if (webView == null) {
            prepare(context);
            webView = mWebViewStackCached.pop();
        }
        // WebView不为空，则开始使用预创建的WebView，并且替换Context
        MutableContextWrapper contextWrapper = (MutableContextWrapper) webView.getContext();
        contextWrapper.setBaseContext(context.getApplicationContext());
        prepare(context);
        return webView;
    }
```

离线包方案，可以在闲时先把H5资源静默预下载到本地，然后需要的时候再去从本地加载H5，这种方案是可以缩短白屏的时间，对优化很有帮助，不过实现的难度就有些大了，需要前后端以及App端同时配合完成。

> 参考 https://github.com/al-liu/OCat-MobilePlatform

## 前端使用文档

[前端使用文档](https://github.com/751496032/hYi-sdk)

##  参考

 - [WebView性能、体验分析与优化](https://tech.meituan.com/2017/06/09/webviewperf.html)
 - [基于腾讯x5封源库，提高60%开发效率](https://juejin.cn/post/6844903950785708040#heading-27)
 - [Android WebView独立进程解决方案](https://www.jianshu.com/p/b66c225c19e2)
