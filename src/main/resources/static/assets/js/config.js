var blog_api_address = "/api/v1/";
//var image_center_address = 'https://images.imyzc.com/';
var image_center_address = 'http://images.localhost.com/';
var localDebug = true;
var https = false;

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