var main = new Vue({
    el: '#main',
    data: {

        currentAuthedUser: null,
        currentAuthed: false,

        currentUserEditing: false,
        currentUser: null,
        currentUserLoadStatus: 'notload',
        currentUserLoadError: ''
    },
    methods: {
        loadUser: function () {
            var currentUserId = getQueryString('user');
            if (currentUserId == null) currentUserId = getLastUrlAgrs(location.href).arg;
            if(!isNumber(currentUserId) || currentUserId == 'user') currentUserId = '0';
            var url = address_blog_api + "user/" + currentUserId;
            $.ajax({
                url: url,
                success: function (response) {
                    if (response.success) {
                        main.currentUserLoadStatus = 'loaded';
                        main.currentUser = response.data;
                        if(main.currentAuthedUser && main.currentAuthedUser.id == main.currentUser.id)
                            main.currentAuthed = true;
                    } else {
                        main.currentUserLoadStatus = 'failed';
                        main.currentUserLoadError = response.message;
                    }
                }, error: function (xhr, err) {
                    main.currentUserLoadStatus = 'failed';
                    main.currentUserLoadError = err;
                }
            });
        },
        edit(){
            if(this.currentAuthedUser){
                if(this.currentAuthedUser.level == userLevels.writer || this.currentAuthedUser.level == userLevels.admin){
                    location.href = '/admin/user-center/';
                }else {
                    this.currentUserEditing = true;
                    setTimeout(function(){
                        if (main.currentUser.gender == '男') {
                            $('#radioMale').prop('checked', true);
                            $('#radioFemale').prop('checked', false);
                        }
                        else if( main.currentUser.gender == '女') {
                            $('#radioMale').prop('checked', false);
                            $('#radioFemale').prop('checked', true);
                        }
                    },200)
                }
            }
        },
        save(){
            var t = toast('正在提交中...', 'loading', -1);

            $.ajax({
                url: address_blog_api + 'user/' + main.currentUser.id + '',
                type: 'put',
                dataType: 'json',
                data: JSON.stringify(main.currentUser),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (response) {
                    toastClose(t);
                  if (response.success) {
                    toast('修改个人信息成功！', 'success', 5000);
                    this.currentUserEditing = false;
                  } else swal('修改个人信息失败', response.message, 'error');
                }, error: function (xhr, err) { 
                    toastClose(t);
                    toast('修改个人信息失败 : ' + err, 'error', 5000); 
                }
            });
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
        getFriendlyName(){
            if(this.currentUser){
                if(!isNullOrEmpty(this.currentUser.friendlyName))
                    return this.currentUser.friendlyName;
                return this.currentUser.name;
            }
            return '加载失败';
        },
        getUserHead() {
            if (isNullOrEmpty(main.currentUser.headimg)) return "/images/default/head-default.png"
            else return getImageUrlFormHash(main.currentUser.headimg);
        },
        getUserCardBackground() {
            if (isNullOrEmpty(main.currentUser.cardBackground)) return "/images/background/mebg.jpg"
            else return getImageUrlFormHash(main.currentUser.cardBackground);
        },
    }
})

function initAuthInfoEnd(authedUser){
    if(authedUser){
        main.currentAuthedUser = authedUser;
        if(main.currentUser && main.currentUser.id == main.currentAuthedUser.id)
            main.currentAuthed = true;
    }
}

setLoaderFinishCallback(function () {
    main.loadUser();
})