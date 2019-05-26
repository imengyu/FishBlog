var main = new Vue({
    el: '#main',
    data: {

        currentAuthedUser: null,
        currentAuthed: false,

        currentUserEditing: false,
        currentUser: null,
        currentUserLoadStatus: 'notload',
        currentUserLoadError: '',

        tableMessagesPageSize: 10,
        tableMessagesLoadStatus: 'notload',
        tableMessagesDatas: null,
        tableMessagesColumns: [
            {
                width: '30px',
                text: '#',
                useData: 'custom',
                customDataFunc: function(item){
                    if(!item.haveRead){
                        return '<span class="badge badge-pill badge-danger">未读</span>';
                    }
                }
            },
            {
                width: '50px',
                text: '时间',
                useData: 'name',
                dataName: 'title',
                useLink: true,
            },
            {
                width: '200px',
                text: '内容',
                scroll: true,
                useData: 'name',
                useData: 'custom',
                customDataFunc: function(item){
                    if(!isNullOrEmpty(item.content)){
                        return base64.decode(item.content);
                    }
                    return '';
                },
            },
            {
                width: '90px',
                text: '操作',
                useSlot: true,
                slotName: 'marchives-slot',
            },
        ],
        tableMessagesPageCurrent: 1,
        tableMessagesPageAll: 1,

        currentMessageTitle: '',
        currentMessageContent: '',
    },
    methods: {
        loadUser: function () {
            var currentUserId = getQueryString('user');
            if (currentUserId == null) currentUserId = getLastUrlAgrs(location.href).arg;
            if (!isNumber(currentUserId) || currentUserId == 'user') currentUserId = '0';
            var url = address_blog_api + "user/" + currentUserId;
            $.ajax({
                url: url,
                success: function (response) {
                    if (response.success) {
                        main.currentUserLoadStatus = 'loaded';
                        main.currentUser = response.data;
                        if (main.currentAuthedUser && main.currentAuthedUser.id == main.currentUser.id){
                            main.currentAuthed = true;
                            if(!isNullOrEmpty(location.hash)){
                                if(location.hash.indexOf('#message-') == 0){
                                    main.messageCenter();
                                }
                            }
                        }
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
        edit() {
            if (this.currentAuthedUser) {
                if (this.currentAuthedUser.level == userLevels.writer || this.currentAuthedUser.level == userLevels.admin) {
                    location.href = '/admin/user-center/';
                } else {
                    this.currentUserEditing = true;
                    setTimeout(function () {
                        if (main.currentUser.gender == '男') {
                            $('#radioMale').prop('checked', true);
                            $('#radioFemale').prop('checked', false);
                        }
                        else if (main.currentUser.gender == '女') {
                            $('#radioMale').prop('checked', false);
                            $('#radioFemale').prop('checked', true);
                        }
                    }, 200)
                }
            }
        },
        save() {
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
                        main.currentUserEditing = false;
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
        messageCenter() {
            this.loadMessages();
            $('#messageCenterModal').modal('show');
        },
        getFriendlyName() {
            if (this.currentUser) {
                if (!isNullOrEmpty(this.currentUser.friendlyName))
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

        loadMessages(force){
            if (main.tableMessagesLoadStatus != 'loaded' || force) {
                main.tableMessagesLoadStatus = 'loading';

                var url = address_blog_api + "user/" + main.currentAuthedUser.id + '/messages/' + (main.tableMessagesPageCurrent - 1) + "/" + main.tableMessagesPageSize;
                //Load comments
                $.ajax({
                    url: url,
                    success: function (response) {
                        if (response.success) {
                            authLoadAuthInfo();
                            main.tableMessagesDatas = response.data.content;
                            main.tableMessagesPageCurrent = response.data.number + 1;
                            main.tableMessagesPageAll = response.data.totalPages;
                            main.tableMessagesLoadStatus = 'loaded';
                        } else main.tableMessagesLoadStatus = 'failed';
                    }, error: function (xhr, err) {main.tableMessagesLoadStatus = 'failed';}
                });
            }
        },            
        deleteMessages(selItems, successCallback) {
            var delPosts = this.tableMessagesGenItemIds(selItems);
            Swal.fire({
                type: 'warning', title: '您真的要删除选中的 ' + Object.keys(selItems).length + ' 条消息吗?',
                confirmButtonColor: '#d33', confirmButtonText: '确定删除', showCancelButton: true, cancelButtonColor: '#3085d6',
                cancelButtonText: "取消", focusCancel: true, reverseButtons: true
            }).then((isConfirm) => {
                if (isConfirm.value) {

                    $.ajax({
                        url: address_blog_api + "user/" + main.currentAuthedUser.id + '/messages',
                        type: 'delete',
                        data: JSON.stringify(delPosts),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                          if (response.success) {
                            toast('删除所选消息成功', 'success');
                            successCallback();
                          } else { swal('删除失败', response.message, 'error'); }
                        }, error: function (xhr, err) { swal('删除失败', err, 'error'); }
                    });

                }
            });
            
        },
        readMessages(selItems, successCallback) {
            var delPosts = this.tableMessagesGenItemIds(selItems);
            $.ajax({
                url: address_blog_api + "user/" + main.currentAuthedUser.id + '/messages/read',
                type: 'post',
                data: JSON.stringify(delPosts),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function (response) {
                  if (response.success) {
                    toast('标记已读成功', 'success');
                    successCallback();
                  } else { swal('标记已读失败', response.message, 'error'); }
                }, error: function (xhr, err) { swal('标记已读失败', err, 'error'); }
            });        

        },

        read(){
            var selItems = this.$refs.tableMessages.getCheckedItems();
            if(!selItems || Object.keys(selItems).length == 0) swal('标记已读', '请至少选中一个条目！', 'warning');
            else this.readMessages(selItems, function(){ main.loadMessages(true); })
        },
        readAll(){
            Swal.fire({
                type: 'warning', title: '您真的要标记全部消息为已读吗?',
                confirmButtonColor: '#d33', confirmButtonText: '确定标记', showCancelButton: true, cancelButtonColor: '#3085d6',
                cancelButtonText: "取消", focusCancel: true, reverseButtons: true
            }).then((isConfirm) => {
                if (isConfirm.value) {

                    $.ajax({
                        url: address_blog_api + "user/" + main.currentAuthedUser.id + '/messages/readall',
                        type: 'get',
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                          if (response.success) {
                            toast('标记全部消息为已读成功', 'success');
                            main.loadMessages(true);
                          } else { swal('全部消息为已读失败', response.message, 'error'); }
                        }, error: function (xhr, err) { swal('全部消息为已读失败', err, 'error'); }
                    });

                }
            });
        },

        tableMessagesItemClick(item){
            this.currentMessageTitle = '';
            this.currentMessageContent = '';
            if(item.content)
                this.currentMessageContent = item.content;
            if(item.title)
                this.title = item.title;
            $('#messageCenterModal').modal('hide');
            $('#messageDatailModal').modal('show');
        },
        tableMessagesGenItemIds(selItems){
            var delPosts = { messages: [] };
            var i = 0;
            for(var key in selItems) {
                delPosts.messages[i] = selItems[key].id;
                i++;
            }
            return delPosts;
        },    
        tableMessagesPagerClick(item){
            if(main.tableMessagesPageCurrent != item){
                main.tableMessagesPageCurrent = item;
                main.loadMessages(true);
            }
        },
        tableMessagesPageSizeChanged(newv){
            if(main.tableMessagesPageSize != newv){
                main.tableMessagesPageSize = newv;
                main.tableMessagesLoadStatus = 'notload'
                main.loadMessages(true);
            }
        },
        tableMessagesCustomItemClick(customerControlId, item){
            if(customerControlId == 'del-all') { 
                var selItems = this.$refs.tableMessages.getCheckedItems();
                if(!selItems || Object.keys(selItems).length == 0) swal('删除', '请至少选中一个条目！', 'warning');
                else this.deleteMessages(selItems, function(){ main.loadMessages(true); })
            }
            else if(customerControlId == 'del') this.deleteMessages([item], function(){ main.loadMessages(true); })
            else if(customerControlId == 'read') this.readMessages([item], function(){ main.loadMessages(true); })
            
        },
        
    }
})

appendLoaderJS("/assets/js/components/common-table.min.js");
appendLoaderJS("/assets/js/components/common-pagination.min.js");

authSetInfoLoadFinishCallback(function (authedUser) {
    if (authedUser) {
        main.currentAuthedUser = authedUser;
        if (main.currentUser && main.currentUser.id == main.currentAuthedUser.id)
            main.currentAuthed = true;
    }
});

setLoaderFinishCallback(function () {
    main.loadUser();
});