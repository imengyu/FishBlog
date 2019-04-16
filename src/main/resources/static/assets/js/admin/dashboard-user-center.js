var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user && (getUserHasPrivilege(user, userPrivileges.manageUsers) || user.level == userLevels.admin)) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
        setTimeout(function () {
            if (user.gender == "男") {
                $('#radioMale').prop('checked', true);
            } else if (user.gender == "女") {
                $('#radioFemale').prop('checked', false);
            }
        }, 500)
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

            changeHead(b) {
                if (b) this.uploadFile();
                else $('#avatar').click();
            },
            uploadFile() {
                var avatar = document.getElementById("avatar");
                var fileObj = avatar.files[0]; // js 获取文件对象
                var url = blog_api_address + 'user/' + main.currentUser.id + '/head';

                //上传成功响应
                var uploadComplete = function (evt) {
                    //服务断接收完文件返回的结果
                    var data = JSON.parse(evt.target.responseText);
                    if (data.success) {
                        //设置 新返回的 图片 hash 值
                        main.currentUser.headimg = data.data;
                        toast('更换头像成功', 'success', 5000);
                    }
                    else toast('上传头像失败！' + data.message + ' ( ' + data.extendCode + ' )', 'error', 5000);
                }
                //上传失败
                var uploadFailed = function uploadFailed(evt) {
                    toast('上传头像失败！请检查您的网络', 'error', 5000);
                }

                var form = new FormData(); // FormData 对象
                form.append("file", fileObj); // 文件对象

                xhr = new XMLHttpRequest();  // XMLHttpRequest 对象
                xhr.open("post", url, true); //post方式，url为服务器请求地址，true 该参数规定请求是否异步处理。
                xhr.onload = uploadComplete; //请求完成
                xhr.onerror = uploadFailed; //请求失
                xhr.send(form); //开始上传，发送form数据

                avatar.value = '';
            },
            setGender(m) {
                if (m) {
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
            getUserHead() {
                if (isNullOrEmpty(main.currentUser.headimg)) return "/images/default/head-default.png"
                else return getImageUrlFormHash(main.currentUser.headimg);
            },
            submit() {
                this.currentStartValid = true;
                if (isNullOrEmpty(main.currentUser.friendlyName)) return;

                var t = toast('正在提交中...', 'loading', -1);

                $.ajax({
                    url: blog_api_address + 'user/' + main.currentUser.id + '',
                    type: 'put',
                    dataType: 'json',
                    data: JSON.stringify(main.currentUser),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (response) {
                        toastClose(t);
                      if (response.success) {
                        toast('修改个人信息成功！', 'success', 5000);
                      } else swal('修改个人信息失败', response.message, 'error');
                    }, error: function (xhr, err) { 
                        toastClose(t);
                        toast('修改个人信息失败 : ' + err, 'error', 5000); 
                    }
                });

            },


        }
    });
}