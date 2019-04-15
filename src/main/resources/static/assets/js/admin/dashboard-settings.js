var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user && (getUserHasPrivilege(user, userPrivileges.globalSettings) || user.level == userLevels.admin)) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
    } else
        main.settingLoadStatus = 'noprivilege';
}
function initApp() {

    main = new Vue({
        el: '#main',
        data: {
            contentLoadStatus: contentLoadStatus,
            content: null,

            userInfoLoaded: false,
            userHasManagePrivilege: false,
            currentUser: null,

            settingLoadStatus: 'notload',
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            
        }
    });
}