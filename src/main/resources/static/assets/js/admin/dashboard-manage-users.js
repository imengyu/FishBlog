appendLoaderJS("/assets/js/components/common-table.min.js");
appendLoaderJS("/assets/js/components/common-pagination.min.js");

var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user && (getUserHasPrivilege(user, userPrivileges.manageUsers) || user.level == userLevels.admin)) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
        main.userPrivileges = userPrivileges;
        main.loadUsers();
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

            tableUsersPageSize: 10,
            tableUsersLoadStatus: 'notload',
            tableUsersLoadError: '',
            tableUsersDatas: null,
            tableUsersColumns: [
                {
                    width: '32px',
                    text: '',
                    useSlot: true,
                    slotName: 'userhead-slot',
                },
                {
                    width: '30px',
                    text: '#',
                    useData: 'name',
                    dataName: 'id'
                },
                {
                    width: 'auto',
                    text: '用户名',
                    useData: 'name',
                    dataName: 'name',
                    useLink: true,
                },
                {
                    width: 'auto',
                    text: '用户邮箱',
                    useData: 'name',
                    dataName: 'email',
                },
                {
                    width: 'auto',
                    text: '友好名字',
                    useData: 'name',
                    dataName: 'friendlyName',
                },
                {
                    width: 'auto',
                    text: '用户组 / 用户权限',
                    useData: 'custom',
                    customDataFunc: function(user){
                        var html = '';
                        if(user.level == userLevels.admin) html = '<span class="tag-post-prefix bg-danger">管理员</span>';
                        else if(user.level == userLevels.writer) html = '<span class="tag-post-prefix bg-primary">作者</span>';
                        else if(user.level == userLevels.guest) html = '<span class="tag-post-prefix bg-success">游客</span>';
                        else if(user.level == 0) html = '<span class="tag-post-prefix bg-secondary">已封禁</span>';
                        html += '&nbsp;&nbsp;';
                        
                        if(user.level == userLevels.admin){

                        }else if(user.level == userLevels.writer){
                            if(getUserHasPrivilege(user, userPrivileges.manageAllArchives)) html += '<span class="tag-post-prefix bg-danger">管理所有文章权限</span>';
                            if(getUserHasPrivilege(user, userPrivileges.manageClassAndTags)) html += '<span class="tag-post-prefix bg-primary">管理分类标签权限</span>';
                            if(getUserHasPrivilege(user, userPrivileges.manageMediaCenter)) html += '<span class="tag-post-prefix album">管理媒体库权限</span>';
                            if(getUserHasPrivilege(user, userPrivileges.manageUsers)) html += '<span class="tag-post-prefix video">管理其他用户权限</span>';
                            if(getUserHasPrivilege(user, userPrivileges.gaintPrivilege)) html += '<span class="tag-post-prefix reprint">授予用户权限权限</span>';
                            if(getUserHasPrivilege(user, userPrivileges.globalSettings)) html += '<span class="tag-post-prefix bg-warning">修改系统设置权限</span>';
                        }
                        return html;
                    }
                },
                {
                    width: 'auto',
                    text: '',
                    useData: 'custom',
                    customDataFunc: function(user){
                        if(user.userFrom == 'github') return '<i class="fa fa-github text-dark" style="font-size: 26px;" data-toggle="tooltip" title="此用户来自 Github"></i>';
                        if(user.userFrom == 'qq') return '<i class="fa fa-qq text-primary" style="font-size: 26px;" data-toggle="tooltip" title="此用户来自 QQ"></i>';
                        if(user.userFrom == 'weixin') return '<i class="fa fa-weixin text-success" style="font-size: 26px;" data-toggle="tooltip" title="此用户来自 微信"></i>';
                        if(user.userFrom == 'weibo') return '<i class="fa fa-weibo text-danger" style="font-size: 26px;" data-toggle="tooltip" title="此用户来自 微博"></i>';
                        return '';
                    }
                },
                {
                    width: '50px',
                    text: '操作',
                    useSlot: true,
                    slotName: 'marchives-slot',
                },
            ],
            tableUsersPageCurrent: 1,
            tableUsersPageAll: 1,

            currentEditPrvOld: 0,
            currentEditPrvUser: null,
            userPrivileges: null,
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            loadUsers(force){
                if (main.tableUsersLoadStatus != 'loaded' || force) {
                    main.tableUsersLoadStatus = 'loading';

                    var url = address_blog_api + "users/" + (main.tableUsersPageCurrent - 1) + "/" + main.tableUsersPageSize;
                    //Load comments
                    $.ajax({
                        url: url,
                        success: function (response) {
                            if (response.success) {
                                main.tableUsersDatas = response.data.content;
                                main.tableUsersPageCurrent = response.data.number + 1;
                                main.tableUsersPageAll = response.data.totalPages;
                                main.tableUsersLoadStatus = 'loaded';
                            } else main.tableUsersLoadStatus = 'failed';
                        }, error: function (xhr, err) {main.tableUsersLoadStatus = 'failed';}
                    });
                }
            },

            addUser(){
                location.href = '../new-user/';
            },

            isUserCurrent(item){
                if(this.currentUser && this.currentUser.id == item.id) return true;
                return false;
            },

            //Classes table
            tableUsersItemClick(item){
                window.open('/user/' + item.id);
            },
            tableUsersPagerClick(item){
                if(main.tableUsersPageCurrent != item){
                    main.tableUsersPageCurrent = item;
                    main.loadUsers(true);
                }
            },
            tableUsersPageSizeChanged(newv){
                if(main.tableUsersPageSize != newv){
                    main.tableUsersPageSize = newv;
                    main.tableUsersLoadStatus = 'notload'
                    main.loadUsers(true);
                }
            },
            tableUsersCustomItemClick(customerControlId, item){
                if(customerControlId == 'del') {

                    if(item.id == main.currentUser.id) toast('您不能注销自己', 'error', 3500);
                    else if(item.level == userLevels.admin) toast('无法注销管理员', 'error', 3500);
                    else 
                    Swal.fire({
                        type: 'warning', title: '您真的要注销此用户?',  text: "注意，此操作不能恢复！该用户将会被永久注销！",              
                        confirmButtonColor: '#d33', confirmButtonText: '确定注销',
                        showCancelButton: true, cancelButtonColor: '#3085d6', cancelButtonText: "取消",
                        focusCancel: true, reverseButtons: true
                    }).then((isConfirm) => {
                        if (isConfirm.value) {

                            $.ajax({
                                url: address_blog_api + 'user/' + item.id,
                                type: 'delete',
                                success: function (response) {
                                  main.tableUsersLoadStatus = 'loaded';
                                  if (response.success) {
                                    toast('成功注销该用户', 'success');
                                    main.loadUsers(true);
                                  } else { swal('注销失败', response.message, 'error'); }
                                }, error: function (xhr, err) { swal('注销失败', err, 'error'); }
                            });

                        }
                    });
                }
                else if(customerControlId == 'edit-ban') {
                    if(item.id == main.currentUser.id) toast('您不能封禁自己', 'error', 3500);
                    else if(item.level == userLevels.admin) toast('无法封禁管理员', 'error', 3500);
                    else Swal.fire({
                        type: 'warning', title: '您真的要封禁用户?',  text: "该用户将会被封禁",              
                        confirmButtonColor: '#d33', confirmButtonText: '确定封禁',
                        showCancelButton: true, cancelButtonColor: '#3085d6', cancelButtonText: "取消",
                        focusCancel: true, reverseButtons: true
                    }).then((isConfirm) => {
                        if (isConfirm.value) {
                            $.ajax({
                                url: address_blog_api + 'user/' + item.id + '/ban',
                                type: 'post',
                                success: function (response) {
                                  main.tableUsersLoadStatus = 'loaded';
                                  if (response.success) {
                                    toast('成功封禁该用户', 'success');
                                    main.loadUsers(true);
                                  } else { swal('封禁失败', response.message, 'error'); }
                                }, error: function (xhr, err) { swal('封禁失败', err, 'error'); }
                            });
                        }
                    });
                }
                else if(customerControlId == 'edit-unban') {
                    Swal.fire({
                        type: 'warning', title: '您真的要解除用户封禁?',  text: "该用户将会被解除封禁",              
                        confirmButtonColor: '#3085d6', confirmButtonText: '确定解除',
                        showCancelButton: true, cancelButtonColor: '#999', cancelButtonText: "取消",
                        focusCancel: true, reverseButtons: true
                    }).then((isConfirm) => {
                        if (isConfirm.value) {
                            $.ajax({
                                url: address_blog_api + 'user/' + item.id + '/unban',
                                type: 'post',
                                success: function (response) {
                                  main.tableUsersLoadStatus = 'loaded';
                                  if (response.success) {
                                    toast('成功解封该用户', 'success');
                                    main.loadUsers(true);
                                  } else { swal('解封失败', response.message, 'error'); }
                                }, error: function (xhr, err) { swal('解封失败', err, 'error'); }
                            });
                        }
                    });
                }
                else if(customerControlId == 'edit-prv') {
                    main.currentEditPrvUser = item;
                    main.currentEditPrvOld = item.privilege;
                   
                    if(item.level == userLevels.baned) toast('该用户已被封禁，无法设置权限', 'error', 3500);
                    else if(item.id == main.currentUser.id) toast('您不能对自己设置权限', 'error', 3500);
                    else if(item.level == userLevels.admin) toast('无法设置管理员的权限', 'error', 3500);
                    else if(item.level == userLevels.guest) toast('游客无法设置权限', 'error', 3500);
                    else if(main.currentUser.privilege == 0) swal('您没有权限授予他人权限', '请类型管理员确认您是否有权限授予其他用户', 'error');
                    else if(item.level == userLevels.writer) $('#eeitPrivilegeModal').modal('show');
                }
                
            },

            getUserHead(user){
                if (isNullOrEmpty(user.headimg)) return "/images/default/head-default.png"
                else return getImageUrlFormHash(user.headimg);
            },
            getPrivilegeCanUse(){
                var html = '';
                var user = main.currentUser;

                if(getUserHasPrivilege(user, userPrivileges.manageAllArchives)) html += '<a href="javascript:;" onclick="main.currentUserAddPriviedge(userPrivileges.manageAllArchives)" class="tag-mgr-item pr-3 bg-danger text-white cursor-pointer">管理所有文章权限</a>';
                if(getUserHasPrivilege(user, userPrivileges.manageClassAndTags)) html += '<a href="javascript:;" onclick="main.currentUserAddPriviedge(userPrivileges.manageClassAndTags)" class="tag-mgr-item pr-3 bg-primary text-white cursor-pointer">管理分类标签权限</a>';
                if(getUserHasPrivilege(user, userPrivileges.manageMediaCenter)) html += '<a href="javascript:;" onclick="main.currentUserAddPriviedge(userPrivileges.manageMediaCenter)" class="tag-mgr-item pr-3 bg-info text-white cursor-pointer">管理媒体库权限</a>';
                if(getUserHasPrivilege(user, userPrivileges.manageUsers)) html += '<a href="javascript:;" onclick="main.currentUserAddPriviedge(userPrivileges.manageUsers)" class="tag-mgr-item pr-3 bg-dark text-white cursor-pointer">管理其他用户权限</a>';
                if(getUserHasPrivilege(user, userPrivileges.gaintPrivilege)) html += '<a href="javascript:;" onclick="main.currentUserAddPriviedge(userPrivileges.gaintPrivilege)" class="tag-mgr-item pr-3 bg-primary text-white cursor-pointer">授予用户权限权限</a>';
                if(getUserHasPrivilege(user, userPrivileges.globalSettings)) html += '<a href="javascript:;" onclick="main.currentUserAddPriviedge(userPrivileges.globalSettings)" class="tag-mgr-item pr-3 bg-warning text-white cursor-pointer">修改系统设置权限</a>';

                if(html=='') html += '<div class="text-center text-secondary pt-2 pb-1">您没有权限可以授予其他用户</div>';
                return html;
            },
            getPrivilegeCurrentUser(){

                if(!main.currentEditPrvUser) return '';

                var html = '';
                var user = main.currentEditPrvUser;

                if(user.level == userLevels.admin) html = '<span class="tag-post-prefix bg-danger">管理员</span>';
                else if(user.level == userLevels.writer) html = '<span class="tag-post-prefix bg-primary">作者</span>';
                else if(user.level == userLevels.guest) html = '<span class="tag-post-prefix bg-success">游客</span>';
                else if(user.level == 0) html = '<span class="tag-post-prefix bg-secondary">已封禁</span>';
                html += '<br/>';
                
                if(getUserHasPrivilege(user, userPrivileges.manageAllArchives)) html += '<div class="tag-mgr-item bg-danger text-white cursor-pointer">管理所有文章权限<a href="javascript:;" onclick="main.currentUserRemovePriviedge(userPrivileges.manageAllArchives)"><i class="fa fa-times"></i></a></div>';
                if(getUserHasPrivilege(user, userPrivileges.manageClassAndTags)) html += '<div class="tag-mgr-item bg-primary text-white cursor-pointer">管理分类标签权限<a href="javascript:;" onclick="main.currentUserRemovePriviedge(userPrivileges.manageClassAndTags)"><i class="fa fa-times"></i></a></div>';
                if(getUserHasPrivilege(user, userPrivileges.manageMediaCenter)) html += '<div class="tag-mgr-item bg-info text-white cursor-pointer">管理媒体库权限<a href="javascript:;" onclick="main.currentUserRemovePriviedge(userPrivileges.manageMediaCenter)"><i class="fa fa-times"></i></a></div>';
                if(getUserHasPrivilege(user, userPrivileges.manageUsers)) html += '<div class="tag-mgr-item bg-dark text-white cursor-pointer">管理其他用户权限<a href="javascript:;" onclick="main.currentUserRemovePriviedge(userPrivileges.manageUsers)"><i class="fa fa-times"></i></a></div>';
                if(getUserHasPrivilege(user, userPrivileges.gaintPrivilege)) html += '<div class="tag-mgr-item bg-primary text-white cursor-pointer">授予用户权限权限<a href="javascript:;" onclick="main.currentUserRemovePriviedge(userPrivileges.gaintPrivilege)"><i class="fa fa-times"></i></a></div>';
                if(getUserHasPrivilege(user, userPrivileges.globalSettings)) html += '<div class="tag-mgr-item bg-warning text-white cursor-pointer">修改系统设置权限<a href="javascript:;" onclick="main.currentUserRemovePriviedge(userPrivileges.globalSettings)"><i class="fa fa-times"></i></a></div>';

                return html;
            },
            currentUserAddPriviedge(prv){
                if(!main.currentUser) return;
                if(!getUserHasPrivilege(main.currentUser, prv)) {
                    toast('您不能授予他人您没有的权限', 'error', 3500);
                    return;
                }
                if(main.currentEditPrvUser){
                    main.currentEditPrvUser.privilege |= prv;
                }
            },
            currentUserRemovePriviedge(prv){
                if(!main.currentUser) return;
                if(!getUserHasPrivilege(main.currentUser, prv)) {
                    toast('您不能撤销他人您没有的权限', 'error', 3500);
                    return;
                }
                if(main.currentEditPrvUser){
                    main.currentEditPrvUser.privilege ^= prv;
                }
            },
            resetPrivilege(){
                main.currentEditPrvUser.privilege = main.currentEditPrvOld;
            },
            savePrivilege(){
                $.ajax({
                    url: address_blog_api + 'user/' + main.currentEditPrvUser.id + '/privilege',
                    type: 'post',
                    dataType: 'json',
                    data: JSON.stringify({ privilege: main.currentEditPrvUser.privilege }),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (response) {
                      main.tableUsersLoadStatus = 'loaded';
                      if (response.success) {
                        toast('成功设置用户权限', 'success');
                      } else { 
                          toast('设置权限失败 : ' + response.message, 'error', 5000);
                          main.resetPrivilege();
                     }
                    }, error: function (xhr, err) { 
                        toast('设置权限失败 : ' + err, 'error', 5000); 
                        main.resetPrivilege();
                    }
                });
            },
        }
    });
}