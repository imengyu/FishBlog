var main;

setLoaderFinishCallback(function () {
    initApp();

    setTimeout(function(){
        main.init();
    },800);
})

function initApp() {

    main = new Vue({
        el: '#main',
        data: {
            currentAddStartValid: false,
            currentAddPasswdStartValid: false,
            currentAddPasswd2StartValid: false,
            currentAddUserName: '',
            currentAddUserFriendlyName: '',
            currentAddUserEmail: '',
            currentAddUserEmailStartValid: false,
            currentAddUserPassword: '',
            currentAddUserPassword2: '',
            currentAddSuccess: false,
        },
        methods: {
                  
            canRegister(){
                return enableRegister
            },
            init(){
                $('#signup_new').focus(function(){
                    main.currentAddPasswdStartValid = true;
                    $('#password_type').slideDown();
                });
                $('#signup_new').blur(function(){
                    if(!main.preSubmitValid())
                        $('#password_type').slideUp();
                });
                $('#signup_new2').focus(function(){
                    main.currentAddPasswd2StartValid = true;
                });
                $('#signup_email').focus(function(){
                    main.currentAddUserEmailStartValid = true;
                });
            },
            validEmail0(){
                if(this.currentAddStartValid && isNullOrEmpty(this.currentAddUserEmail))
                    return ' is-invalid'
                return this.validEmail1() ? ' is-invalid' : '';
            },
            validEmail1(){
                if(!this.currentAddStartValid && !this.currentAddUserEmailStartValid)return false;
                var email = this.currentAddUserEmail;
                if(email=='')return false;
                
                var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/; //定义一个正则表达式
                if(reg.test(email)) return false
                else return true;
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
                return false;
            },
            validPassWord2(){
                if((this.currentAddStartValid || this.currentAddPasswd2StartValid) && (isNullOrEmpty(this.currentAddUserPassword2) || this.currentAddUserPassword != this.currentAddUserPassword2 ))
                    return ' is-invalid'
                return ''
            },
            preSubmitValid(){
                
                if(this.validEmail0() == ' is-invalid'
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

                if(main.currentAddUserEmail == main.currentAddUserPassword){
                    swal('密码不可与邮箱相同！','','error');
                    return;
                }

                var t = toast('正在提交中...', 'loading', -1);
                $.ajax({
                    url: address_blog_api + 'users/sign-up',
                    type: 'post',
                    dataType: 'json',
                    data: JSON.stringify({ 
                        email: main.currentAddUserEmail,
                        passwd: md5(main.currentAddUserPassword)
                    }),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (response) {
                        toastClose(t);
                      if (response.success) {
                        main.currentAddSuccess = true;
                      } else swal('提交失败', response.message, 'error');
                    }, error: function (xhr, err) { 
                        toastClose(t);
                        toast('提交失败 : ' + err, 'error', 5000); 
                    }
                });

            },
        }
    });
}