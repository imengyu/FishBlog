//URLs
//静态路径

var address_blog_api = "/api/v1/"; //后端api地址，可选择前端与后端分离，可以处于不同主机上，但是服务器需要设置跨域
var address_image_center = 'https://images.imyzc.com/'; //图片静态读取路径，推荐使用nginx开一个新子站专门存放图片
//var address_image_center = 'http://images.localhost.com/';

//Constracts
//常量

var partPositions = {
    viewAll: "/archives/",
    viewPost: "/archives/post/",
    viewTag: "/archives/tag/",
    viewDate: "/archives/month/",
    viewClass: "/archives/class/",
}
var userLevels = {
    baned: 0,
    admin: 1,
    writer: 2,
    guest: 3
}

var userPrivileges = {
    manageAllArchives: 0x1,
    manageClassAndTags: 0x2,
    manageMediaCenter: 0x4,
    manageUsers: 0x8,
    gaintPrivilege: 0x10,
    globalSettings: 0x20,
}

var archiveStatus = {
    PUBLISH: 1,
    PRIVATE: 0,
    DRAFT: 2,
}

var excludeStatPath = [
    '/sign-in/',
    '/sign-out/',
    '/admin/',
]

//Settings
//设置

var sendStats = true; // 是否发送用户浏览数据
var anonymousComment = true; // 允许匿名评论

