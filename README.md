# 新闻Remix混合 安卓原生应用 （A remix/news composite android app）

[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://nodesource.com/products/nsolid)

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

技术栈(Tech stack)
Rxjava, Retrofit, room, fresco, etc

主要 构架 和 组件 （architecture and components）
MVC
全局 三个activities, 两个services, 两个 broadcast receivers 其中 主Activity 含有4个fragments
由于api 不够完善。 采用少量数据不断重复来模拟无限加载列表 (主要 加强对 recyclerview 的掌握和使用)

主要 API: 网易云 (netease cloud api) 和 News break open api： (netease music cloud api and News break open api：
Future task: 自己完善 API 重新设计 部署 backend   (need optimation on the api)

主要Features (major features)
1 播放 暂停 Remix音乐  (music playing)
2 点赞 Remix 功能和 收藏功能 (Like/Dislike feature)
3 通过网易云 id 添加 Remix (Music/Remix adding feature)
4 浏览少量新闻 summary, api 只提供 summary 这里主要只是为了练习. (News reading feature)
5 切换音乐播放 的 生命周期。允许 边看新闻边听歌  (Music mode switch feature)





