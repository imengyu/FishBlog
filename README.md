# FishBlog
Fish Blog API System


说明
---
基于 Spring boot 开发的，一个简单的博客系统（后端API部分）

其前端代码已全部从该项目中脱离至前端项目 [FishFront](https://github.com/717021/FishFront) 中。

关于此项目的配套数据库文件可以在 [这里](http://storage.imyzc.com/项目/FishBlog/base.sql) 下载到。
初始管理员账号是admin，密码是123456 。登录后可更改密码。

> 本项目必须与前端项目 FishFront 一起运行，默认情况下，FishFront 和 FishBlog 是完全前后端分离的，他们部署在不同的服务器或软件上，例如前端部署在 nginx 使用域名 abc.com ，而后端在另一台服务器上使用 api.abc.com 访问后端服务。当然，假如你只想用 Spring boot 一个项目或部署在docker下的话，可以将 FishFront 的 dist 已生成项目放在本项目 static 文件夹下，并配置 FishFront 的后端服务路径即可。

关于更多信息，请参照其他说明文档。


