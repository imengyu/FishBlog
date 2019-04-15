var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user && (getUserHasPrivilege(user, userPrivileges.manageUsers) || user.level == userLevels.admin)) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
        setTimeout(function(){
            if(user.gender == "男"){
                $('#radioMale').prop('checked', true);
            }else if(user.gender == "女"){
                $('#radioFemale').prop('checked', false);
            }
        },500)
    } else
        main.tableUsersLoadStatus = 'noprivilege';
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
            currentUserIsMale: false,
            currentUserIsFemale: false,


            currentStartValid: false,
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            setGender(m){
                if(m) {
                    main.currentUser.gender = '男';
                    $('#radioMale').prop('checked', true);
                    $('#radioFemale').prop('checked', false);
                }
                else {
                    main.currentUser.gender = '女';
                    $('#radioMale').prop('checked', false);
                    $('#radioFemale').prop('checked', true);
                }
            },
            getUserHead(){
                if(isNullOrEmpty(main.currentUser.headimg)) return "/images/default/head-default.png"
                else return getImageUrlFormHash(main.currentUser.headimg);
            },
            submit(){
                this.currentStartValid = true;
                if(isNullOrEmpty(main.currentUser.friendlyName)) return;



            },


        }
    });
}