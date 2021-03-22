# 新闻Remix混合 安卓原生应用 (A remix/news composite android app）

[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://nodesource.com/products/nsolid)

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

# 技术栈(Tech stack)

Rxjava, Retrofit, Room, Fresco, etc

# 主要构架和组件 （Architecture and components）

小型应用 无构架

全局 三个activities, 两个services, 两个 broadcast receivers 其中 主Activity 含有4个fragments, 1 个 自定义 View

问题: UI在部分手机会出现异常

# 主要API (Major public apis): 

网易云 (Netease cloud api) 和 News break open api： (Netease music cloud api and News break open api)


问题: 由于api不够完善, 采用少量数据不断重复来模拟无限加载列表 (这个app 主要是加强对各种组件的掌握和使用)

(Use minor data to mock infi loading list)


Future task: 自己完善 API 重新设计   (Need optimation on the api)

# 主要Features (Major features)

1 无限加载浏览功能  (Infinite loading list)

2 播放 暂停 Remix音乐, timer 显示进度  (Music playing throuch MediaPlayer api while using timer to show progress)


3 点赞 Remix 功能和 收藏功能 (Like/Dislike feature)


4 通过网易云 id 添加 Remix (Music/Remix adding feature)


5 浏览少量新闻 summary, api 只提供 summary 这里主要只是为了练习. (News Reading feature)


6 切换音乐播放 的 生命周期。允许 边看新闻边听歌  (Remix mode switch feature)

7 新闻加载完毕 nofication  (News notifications)

