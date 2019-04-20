//Constracts
//常量

var address_blog_api = "/api/v1/"; //后端api地址，可选择前端与后端分离，可以处于不同主机上，但是服务器需要设置跨域

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


//用户浏览数据排除路径
var excludeStatPath = [
    '/sign-in/',
    '/sign-out/',
    '/admin/',
]

//网站Logo和标题
var siteName = 'ALONE SPACE';
var siteLogo = '';
var siteLogoSize = {
    width: '66px',
    height: '26px'
};

//网站主菜单栏设置
var menuConfig = [
    {
        name: '关于我',
        url: '/archives/post/about/'
    },
    {
        name: '所有文章',
        url: '/archives/'
    },
    {
        name: '归档',
        url: '/archives/month/'
    },
    {
        name: '主页',
        url: '/'
    },
]
//底部联系方式
var socialConfig = [
    {
        icon: 'qq',
        name: '我的 QQ',
        tooltip: 'QQ 1501076885',
        color: 'rgb(33,143,235)',
        url: '#',
    },
    {
        icon: 'weixin',
        name: '我的微信',
        tooltip: '<img src=\'/images/mmqrcode1543412167834.jpg\' alt=\'mmqrcode\' width=200 height=200/>',
        color: 'rgb(0,184,4)',
        url: '/images/mmqrcode1543412167834.jpg',
    },
    {
        icon: 'github',
        name: '我的 Github',
        url: 'https://github.com/717021',
        color: '#000'
    },
]
//网站自定义生成设置
var constConfig = {
    autoFooter: true,
    autoTitle: true,

    footerShowLinks: [
        {
            name: '网站地图',
            url: '/sitemap.html'
        },
        {
            name: '网站后台',
            url: '/sign-in/'
        }
    ],
    icpRecord: '浙ICP备18051956号-1', //ICP 备案号
    policeRecord: {
        title: '浙公网安备 33080202000390号',
        url: 'https://www.beian.gov.cn/portal/registerSystemInfo?recordcode=33080202000390'
    }, //公安备案号
}


