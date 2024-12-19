​
etpack Compose作为构建原生Android界面的工具包，更少的代码需求、强大的工具和直观的Kotlin API都能更好地帮助Android界面开发，不仅布局，它在动画交互方面也有着强大的能力

当下的移动软件基本会在开屏时用动画衔接加载进入软件的主界面或登录界面，而这次分享的内容则就是通过Jetpack Compose来制作一个软件载入动画

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/dfbeddb05fcf467c81eaf681a214ddc3.gif#pic_center)
在此载入动画中，主要由两个元素，背景与图标构成，我们可以添加box组件以及在box组件中添加一个子组件icon

```kotlin
@Composable
fun Splash() {
    Box(
        modifier = Modifier
            .background(Purple200)
            .fillMaxSize(),//将组件填充至最大尺寸
        contentAlignment = Alignment.Center//将组件对齐至中心
    ) {
        Icon(
            modifier = Modifier
                .size(120.dp),
            imageVector = Icons.Default.Send,
            contentDescription = "Icon",
            tint = Color.White
        )
    }
}
```

在运行代码前，可通过preview提前预览布局

```kotlin
@Composable
@Preview
fun SplashPreview() {
    Splash(alpha = 1f)
}
```

而在编写Splash的Box布局时，fillMaxSize()和Alignment.Center分别让布局能够充满全屏以及对齐至中心，达到所需要的效果


                                    **未调用相应语句的预览结果**

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/087baa87e34849469fd9e507d5e352a9.png#pic_center)

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/ab6291b86e9c4224be1d89822fda419e.png#pic_center)
开始的布局搭建完成后，接下来就需要创建一个动画

```kotlin
@Composable
fun AnimatedSplashScreen(navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(//控制透明度
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000//淡入动画的时间
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(5000)//动画持续时间
        navController.popBackStack()
        navController.navigate(Screen.Home.route)
    }
    Splash(alpha = alphaAnim.value)
}
```

代码第二部分LaunchedEffect是控制动画整体的播放，在startAnimation=true时播放，持续5秒，然后切至Home屏幕

第一部分则是对特定部分的透明度切换，实现淡入动画的效果，在startAnimation=true时变为完全不透明，其它状态则变为完全透明，在设置透明度的同时也增加了条件，在主动画播放3s后才执行透明度切换

而加载动画中透明度切换的对象为icon，所以需要在Splash相应的Box布局中进行更改

```kotlin
@Composable
fun Splash(alpha: Float) {
    Box(
        modifier = Modifier
            .background(Purple200)
            .fillMaxSize(),//将组件填充至最大尺寸
        contentAlignment = Alignment.Center//将组件对齐至中心
    ) {
        Icon(
            modifier = Modifier
                .size(120.dp)
                .alpha(alpha = alpha),
            imageVector = Icons.Default.Send,
            contentDescription = "Icon",
            tint = Color.White
        )
    }
}
```

而因为动画是从一个屏幕状态过渡到另一个屏幕状态，所以要在Home屏幕外添加另一个状态用于播放动画

```kotlin
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route//播放动画的屏幕状态
    ) {
        composable(route = Screen.Splash.route) {
            AnimatedSplashScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
```

最后更改MainActivity，即可运行app，获得如开篇图片的加载动画效果

```kotlin
package com.example.animatedsplashscreendemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.animatedsplashscreendemo.navigation.SetupNavGraph
import com.example.animatedsplashscreendemo.ui.theme.AnimatedSplashScreenDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimatedSplashScreenDemoTheme {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}
```

