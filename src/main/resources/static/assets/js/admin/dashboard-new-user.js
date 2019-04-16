appendLoaderJS("/assets/libs/compress/md5.min.js");

var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user && (getUserHasPrivilege(user, userPrivileges.manageUsers) || user.level == userLevels.admin)) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
        main.hasPrivilege = true;
    }    
}
function initApp() {

    main = new Vue({
        el: '#main',
        data: {
            contentLoadStatus: contentLoadStatus,
            content: null,

            hasPrivilege: false,
            userInfoLoaded: false,
            userHasManagePrivilege: false,
            currentUser: null,

            currentAddStartValid: false,
            currentAddUserName: '',
            currentAddUserFriendlyName: '',
            currentAddUserPassword: '',
            currentAddUserPassword2: '',
            currentAddSuccess: false,
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            clearInput(){
                this.currentAddSuccess = false;
                this.currentAddStartValid = false;
                this.currentAddUserName = '';
                this.currentAddUserFriendlyName = '';
                this.currentAddUserPassword = '';
                this.currentAddUserPassword2 = '';
            },
            validPassWord1(){
                if(this.currentAddStartValid && (isNullOrEmpty(this.currentAddUserPassword) || this.currentAddUserPassword.length < 8))
                    return ' is-invalid'
                return ''
            },
            validPassWord2(){
                if(this.currentAddStartValid && (isNullOrEmpty(this.currentAddUserPassword2) || this.currentAddUserPassword != this.currentAddUserPassword2 ))
                    return ' is-invalid'
                return ''
            },
            preSubmitValid(){
                this.currentAddStartValid = true;
                if(isNullOrEmpty(this.currentAddUserName) || isNullOrEmpty(this.currentAddUserFriendlyName)
                    || isNullOrEmpty(this.currentAddUserPassword) || isNullOrEmpty(this.currentAddUserPassword2)) return true;
                if(this.validPassWord1() == ' is-invalid') return true;
                if(this.validPassWord2() == ' is-invalid') return true;
                return false;
            },
            submit(){

                if(this.preSubmitValid()) return;

                var t = toast('正在提交中...', 'loading', -1);

                $.ajax({
                    url: blog_api_address + 'users',
                    type: 'post',
                    dataType: 'json',
                    data: JSON.stringify({ 
                        name: main.currentAddUserName,
                        friendlyName: main.currentAddUserFriendlyName,
                        passwd: md5(main.currentAddUserPassword)
                    }),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (response) {
                        toastClose(t);
                      if (response.success) {
                        main.currentAddSuccess = true;
                      } else swal('添加用户失败', response.message, 'error');
                    }, error: function (xhr, err) { 
                        toastClose(t);
                        toast('添加用户失败 : ' + err, 'error', 5000); 
                    }
                });

            },
        }
    });
}