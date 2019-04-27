var main;

setLoaderNotHideMask();
setLoaderFinishCallback(function () {
    initApp();
    $.ajax({
        url: address_blog_api + 'auth/auth-test',
        type: 'get',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            if (!isNullOrEmpty(response) && response.success) {
                currentAuthedUser=response.data;
                
                main.currentUserId = currentAuthedUser.id;
                main.currentUserName = currentAuthedUser.name;

                $('.current-user-name').html(currentAuthedUser.name + '<i></i>');
                $('.current-user-head').attr('src', getImageUrlFormHash(currentAuthedUser.headimg));

                setTimeout(function(){ setLoaderHideMaskNow(); main.init(); },500)
            }else {
                $('.nav-user').hide();

                var setToRedirect = function(){
                    if(response.extendCode == '-3') location.href = '/sign-in/?redirect_url='+ encodeURI(location.href) + '&error=SessionOut';
                    else location.href = '/sign-in/?redirect_url='+ encodeURI(location.href) + '&error=RequestLogin';
                }
                
                //Re password
                var token = getQueryString("token");
                if(!isNullOrEmpty(token)){

                    $.ajax({
                        url: address_blog_api + 'user/x/password/token-test?token=' + token,
                        type: 'get',
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            if (!isNullOrEmpty(response) && response.success) {
                                setLoaderHideMaskNow();
                                main.init();
                                main.switchToRepassword(token);
                            }else setToRedirect();
                        }, error: function (xhr, err) {setToRedirect(); }
                    });

                }else setToRedirect();
            }
        }, error: function (xhr, err) { 
            swal('连接服务器异常', '请检查您的连接？', 'error');
        }
    });

})

function initApp() {

    main = new Vue({
        el: '#main',
        data: {

            
            currentIsRecoverPassword: false,
            currentRecoverPasswordToken: '',
            currentUserId: 0,
            currentUserName: '',
            currentAddStartValid: false,
            currentAddPasswdStartValid: false,
            currentAddPasswd2StartValid: false,
            currentAddUserName: '',
            currentAddUserFriendlyName: '',
            currentAddUserPasswordOld: '',
            currentAddUserPassword: '',
            currentAddUserPassword2: '',
            currentAddSuccess: false,
        },
        methods: {
                  
            init(){
                $('#change_new').focus(function(){
                    main.currentAddPasswdStartValid = true;
                    $('#password_type').slideDown();
                });
                $('#change_new').blur(function(){
                    if(!main.preSubmitValid())
                        $('#password_type').slideUp();
                });
                $('#change_new2').focus(function(){
                    main.currentAddPasswd2StartValid = true;
                });
            },
            switchToRepassword(tkn){
                this.currentIsRecoverPassword = true;
                this.currentRecoverPasswordToken = tkn;
            },
            validPassWord0(){
                if(this.currentAddStartValid && isNullOrEmpty(this.currentAddUserPasswordOld))
                    return ' is-invalid'
                return ''
            },
            validPassWord1(){
                if(this.currentAddPasswdStartValid && (this.validPassWord1Len() || this.validPassWord1Char() || this.validPassWord1UserName()))
                    return ' is-invalid'
                return ''
            },
            validPassWord1Len(){
                return (isNullOrEmpty(this.currentAddUserPassword) || this.currentAddUserPassword.length < 8)
            },
            validPassWord1Char() {
                return !new RegExp(/(?!^(\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\w~!@#$%\^&*?]+$/).test(this.currentAddUserPassword);
            },
            validPassWord1UserName(){
                var u = this.currentUserName;
                return u!='' && (u == this.currentAddUserPassword || this.currentAddUserPassword.indexOf(u) >= 0);
            },
            validPassWord2(){
                if((this.currentAddStartValid || this.currentAddPasswd2StartValid) && (isNullOrEmpty(this.currentAddUserPassword2) || this.currentAddUserPassword != this.currentAddUserPassword2 ))
                    return ' is-invalid'
                return ''
            },
            preSubmitValid(){
                
                if((!this.currentIsRecoverPassword && this.validPassWord0() == ' is-invalid')
                    || this.validPassWord2() == ' is-invalid') return true;

                if(this.validPassWord1Len() || this.validPassWord1Char() || this.validPassWord1UserName())
                    return true;

                return false;
            },
            submitChange(){

                this.currentAddStartValid = true;
                if(this.preSubmitValid()) {
                    $('#password_type').slideDown();
                    return;
                }

                if(main.currentAddUserPasswordOld == main.currentAddUserPassword){
                    swal('新密码不可与旧密码相同哦！','','warning');
                    return;
                }
                var t = toast('正在提交中...', 'loading', -1);

                if(this.currentIsRecoverPassword){
                    $.ajax({
                        url: address_blog_api + 'user/x/password',
                        type: 'post',
                        dataType: 'json',
                        data: JSON.stringify({ 
                            token: main.currentRecoverPasswordToken,
                            newPassword: md5(main.currentAddUserPassword)
                        }),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (response) {
                            toastClose(t);
                          if (response.success) {
                            main.currentAddSuccess = true;
                          } else swal('修改密码失败', response.message, 'error');
                        }, error: function (xhr, err) { 
                            toastClose(t);
                            toast('修改密码失败 : ' + err, 'error', 5000); 
                        }
                    });
                }else{
                    $.ajax({
                        url: address_blog_api + 'user/' + main.currentUserId + '/password',
                        type: 'post',
                        dataType: 'json',
                        data: JSON.stringify({ 
                            oldPassword: md5(main.currentAddUserPasswordOld),
                            newPassword: md5(main.currentAddUserPassword)
                        }),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (response) {
                            toastClose(t);
                          if (response.success) {
                            main.currentAddSuccess = true;
                          } else swal('修改密码失败', response.message, 'error');
                        }, error: function (xhr, err) { 
                            toastClose(t);
                            toast('修改密码失败 : ' + err, 'error', 5000); 
                        }
                    });
                }
            },
        }
    });
}