appendLoaderJS("/assets/js/components/common-table.min.js");
appendLoaderJS("/assets/js/components/common-pagination.min.js");

var main = null;

function initAuthInfoEnd(user){
    main.userInfoLoaded = true;
    if(user)  {
        if(user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
        main.loadTable('all');
    }
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

            tableAllAnyDeleted: false,
            tableAllShowAllUserArchive: false,
            tableAllPageSize: 10,
            tableAllLoadStatus: 'notload',
            tableAllDatas: null,
            tableAllColumns: [
                {
                    width: '30px',
                    text: '#',
                    useData: 'name',
                    dataName: 'id'
                },
                {
                    width: '70px',
                    text: '',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return getPostPrefix(item.postPrefix);
                    }
                },
                {
                    width: 'auto',
                    text: '文章名称',
                    useData: 'name',
                    dataName: 'title',
                    useLink: true,
                },
                {
                    width: 'auto',
                    text: '所属分类',
                    useData: 'custom',
                    customDataFunc: function(item){
                        if(item.postClass) return item.postClass.split(':')[1];
                        return '';
                    }
                },
                {
                    width: '130px',
                    text: '浏览/评论/赞',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return item.viewCount + ' / ' + item.commentCount + ' / ' + item.likeCount;
                    }
                },
                {
                    width: '90px',
                    text: '文章状态',
                    useData: 'custom',
                    customDataFunc: function(item){
                        switch(item.status){
                            case 0: return '私有文章';
                            case 1: return '公开文章';
                            case 2: return '文章草稿';
                        }
                        return '';
                    }
                },
                {
                    width: '150px',
                    text: '操作',
                    useSlot: true,
                    slotName: 'marchives-slot',
                },
            ],
            tableAllPageCurrent: 1,
            tableAllPageAll: 1,

            tablePublishAnyDeleted: false,
            tablePublishPageSize: 10,
            tablePublishLoadStatus: 'notload',
            tablePublishDatas: null,
            tablePublishColumns: [
                {
                    width: '30px',
                    text: '#',
                    useData: 'name',
                    dataName: 'id'
                },
                {
                    width: '70px',
                    text: '',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return getPostPrefix(item.postPrefix);
                    }
                },
                {
                    width: 'auto',
                    text: '文章名称',
                    useData: 'name',
                    dataName: 'title',
                    useLink: true,
                },
                {
                    width: 'auto',
                    text: '所属分类',
                    useData: 'custom',
                    customDataFunc: function(item){
                        if(item.postClass) return item.postClass.split(':')[1];
                        return '';
                    }
                },
                {
                    width: '130px',
                    text: '浏览/评论/赞',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return item.viewCount + ' / ' + item.commentCount + ' / ' + item.likeCount;
                    }
                },
                {
                    width: '150px',
                    text: '操作',
                    useSlot: true,
                    slotName: 'marchives-slot',
                },
            ],
            tablePublishPageCurrent: 1,
            tablePublishPageAll: 1,

            tableDrafthAnyDeleted: false,
            tableDraftPageSize: 10,
            tableDraftLoadStatus: 'notload',
            tableDraftDatas: null,
            tableDraftColumns: [
                {
                    width: '30px',
                    text: '#',
                    useData: 'name',
                    dataName: 'id'
                },
                {
                    width: '70px',
                    text: '',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return getPostPrefix(item.postPrefix);
                    }
                },
                {
                    width: 'auto',
                    text: '文章名称',
                    useData: 'name',
                    dataName: 'title',
                },
                {
                    width: 'auto',
                    text: '所属分类',
                    useData: 'custom',
                    customDataFunc: function(item){
                        if(item.postClass) return item.postClass.split(':')[1];
                        return '';
                    }
                },
                {
                    width: 'auto',
                    text: '最近一次的修改时间',
                    useData: 'custom',
                    customDataFunc: function(item){
                        if(item.lastmodifyDate) return item.lastmodifyDate;
                        else return item.postDate;
                    }
                },
                {
                    width: '200px',
                    text: '操作',
                    useSlot: true,
                    slotName: 'marchives-draft-slot',
                },
            ],
            tableDraftPageCurrent: 1,
            tableDraftPageAll: 1,
        },
        methods: {

            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            tableAnyDeleted(){
                main.tableAllAnyDeleted=true;
                main.tablePublishAnyDeleted=true;
                main.tableDrafthAnyDeleted=true;
            },
            tableItemClick(item){
                window.open(getPostRealUrl(item));
            },
            tableItemEditClick(itemId){
                window.open("../write-archive/?archive=" + itemId);
            },
            tableGenItemIds(selItems){
                var delPosts = { archives: [] };
                var i = 0;
                for(var key in selItems) {
                    delPosts.archives[i] = selItems[key].id;
                    i++;
                }
                return delPosts;
            },          
            tableDeleteItem(itemId, successCallback){
                Swal.fire({
                    type: 'warning', // 弹框类型
                    title: '您真的要删除这一篇文章吗?', //标题
                    text: "注意，此操作不能恢复！", //显示内容
                
                    confirmButtonColor: '#d33',// 确定按钮的 颜色
                    confirmButtonText: '确定删除',// 确定按钮的 文字
                    showCancelButton: true, // 是否显示取消按钮
                    cancelButtonColor: '#3085d6', // 取消按钮的 颜色
                    cancelButtonText: "取消", // 取消按钮的 文字
                
                    focusCancel: true, // 是否聚焦 取消按钮
                    reverseButtons: true  // 是否 反转 两个按钮的位置 默认是  左边 确定  右边 取消
                }).then((isConfirm) => {
                    if (isConfirm.value) {

                        $.ajax({
                            url: address_blog_api + '/post/' + itemId,
                            type: 'delete',
                            success: function (response) {
                              main.tableAllLoadStatus = 'loaded';
                              if (response.success) {
                                toast('成功删除一篇文章', 'success');
                                successCallback();
                              } else { swal('删除失败', response.message, 'error'); }
                            }, error: function (xhr, err) { swal('删除失败', err, 'error'); }
                        });

                    }
                });
            },
            tableDeleteSelect(selItems, successCallback){
                var delPosts = this.tableGenItemIds(selItems);
                Swal.fire({
                    type: 'warning', // 弹框类型
                    title: '您真的要删除选中的 ' + Object.keys(selItems).length + ' 篇文章吗?', //标题
                    text: "注意，此操作不能恢复！", //显示内容
                
                    confirmButtonColor: '#d33',// 确定按钮的 颜色
                    confirmButtonText: '确定删除',// 确定按钮的 文字
                    showCancelButton: true, // 是否显示取消按钮
                    cancelButtonColor: '#3085d6', // 取消按钮的 颜色
                    cancelButtonText: "取消", // 取消按钮的 文字
                
                    focusCancel: true, // 是否聚焦 取消按钮
                    reverseButtons: true  // 是否 反转 两个按钮的位置 默认是  左边 确定  右边 取消
                }).then((isConfirm) => {
                    if (isConfirm.value) {

                        $.ajax({
                            url: address_blog_api + '/posts',
                            type: 'delete',
                            data: JSON.stringify(delPosts),
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            success: function (response) {
                              if (response.success) {
                                toast('删除所选文章成功', 'success');
                                successCallback();
                              } else { swal('删除失败', response.message, 'error'); }
                            }, error: function (xhr, err) { swal('删除失败', err, 'error'); }
                        });

                    }
                });
            },
            
            //All
            tableAllPagerClick(item){
                if(main.tableAllPageCurrent != item){
                    main.tableAllPageCurrent = item;
                    main.reloadTable('all');
                }
            },
            tableAllPageSizeChanged(newv){
                if(main.tableAllPageSize != newv){
                    main.tableAllPageSize = newv;
                    main.tableAllLoadStatus = 'notload'
                    main.loadTable('all');
                }
            },
            tableAllCustomItemClick(customerControlId, item){
                if(customerControlId == 'del-all') { 
                    var selItems = main.$refs.tableAll.getCheckedItems();
                    if(!selItems || Object.keys(selItems).length == 0) swal('删除', '请至少选中一个条目！', 'warning');
                    else main.tableDeleteSelect(selItems, function(){ main.tableAnyDeleted();main.$refs.tableAll.unCheckAll(); main.reloadTable('all'); })
                }
                else if(customerControlId == 'del') main.tableDeleteItem(item.id, function(){ main.tableAnyDeleted();main.$refs.tableAll.unCheckAll(); main.reloadTable('all') });
                else if(customerControlId == 'edit') main.tableItemEditClick(item.id);
                else if(customerControlId == 'switch-show-all') main.reloadTable('all')
                
            },

            //Publish
            tablePublishPagerClick(item){
                if(main.tablePublishPageCurrent != item){
                    main.tablePublishPageCurrent = item;
                    main.reloadTable('publish');
                }
            },
            tablePublishPageSizeChanged(newv){
                if(main.tablePublishPageSize != newv){
                    main.tablePublishPageSize = newv;
                    main.tablePublishLoadStatus = 'notload'
                    main.loadTable('publish');
                }
            },
            tablePublishCustomItemClick(customerControlId, item){
                if(customerControlId == 'del-all') {
                    var selItems = main.$refs.tablePublish.getCheckedItems();
                    if(!selItems || Object.keys(selItems).length == 0) swal('删除', '请至少选中一个条目！', 'warning');
                    else main.tableDeleteSelect(selItems, function(){ main.tableAnyDeleted();main.$refs.tablePublish.unCheckAll(); main.reloadTable('publish'); })
                }
                else if(customerControlId == 'del') main.tableDeleteItem(item.id, function(){main.tableAnyDeleted();main.$refs.tablePublish.unCheckAll(); main.reloadTable('publish')});
                else if(customerControlId == 'edit') main.tableItemEditClick(item.id);
                
            },

            //Draft
            tableDraftPagerClick(item){
                if(main.tableDraftPageCurrent != item){
                    main.tableDraftPageCurrent = item;
                    main.reloadTable('draft');
                }
            },
            tableDraftPageSizeChanged(newv){
                if(main.tableDraftPageSize != newv){
                    main.tableDraftPageSize = newv;
                    main.tableDraftLoadStatus = 'notload'
                    main.loadTable('draft');
                }
            },
            tableDraftCustomItemClick(customerControlId, item){
                if(customerControlId == 'del-all') {
                    var selItems = main.$refs.tableDraftC.getCheckedItems();
                    if(!selItems || Object.keys(selItems).length == 0) swal('删除', '请至少选中一个条目！', 'warning');
                    else main.tableDeleteSelect(selItems, function(){ main.tableAnyDeleted();main.$refs.tableDraftC.unCheckAll(); main.reloadTable('craft'); })
                }
                else if(customerControlId == 'del') main.tableDeleteItem(item.id, function(){ main.tableAnyDeleted();main.$refs.tableDraft.unCheckAll(); main.reloadTable('craft')});
                else if(customerControlId == 'edit') main.tableItemEditClick(item.id);
                else if(customerControlId == 'publish'){
                    window.open("../write-archive/?archive=" + item.id + "&publish=click");
                }
            },

            reloadTable(page){
                if(page == 'all') main.tableAllLoadStatus = 'notload'
                else if(page == 'publish') main.tablePublishLoadStatus = 'notload'
                else if(page == 'draft') main.tableDraftLoadStatus = 'notload'
                this.loadTable(page);
            },
            loadTable: function (page){
                if(page == 'all' && (this.tableAllAnyDeleted || this.tableAllLoadStatus != 'loaded')){
                    main.tableAllAnyDeleted = false;
                    main.tableAllLoadStatus = 'loading';
                    $.ajax({
                        url: address_blog_api + 'posts/page/' + (main.tableAllPageCurrent - 1) + '/' + main.tableAllPageSize + '?sortBy=date&byStatus=all&byUser=' + (main.tableAllShowAllUserArchive ? '0' : main.currentUser.id),
                        success: function (response) {
                          main.tableAllLoadStatus = 'loaded';
                          if (response.success) {
                            main.tableAllDatas = response.data.content;
                            main.tableAllPageCurrent = response.data.number + 1;
                            main.tableAllPageAll = response.data.totalPages;
                            main.reloadPostStats(page);
                          } else { main.tableAllLoadStatus = 'failed'; }
                        }, error: function (xhr, err) { main.tableAllLoadStatus = 'failed'; }
                    });
                }else if(page == 'publish' && (this.tablePublishAnyDeleted || this.tablePublishLoadStatus != 'loaded')){
                    main.tablePublishAnyDeleted = false;
                    main.tablePublishLoadStatus = 'loading';
                    $.ajax({
                        url: address_blog_api + 'posts/page/' + (main.tablePublishPageCurrent - 1) + '/' + main.tablePublishPageSize + '?sortBy=date&byStatus=publish&byUser=' + main.currentUser.id,
                        success: function (response) {
                          main.tablePublishLoadStatus = 'loaded';
                          if (response.success) {
                            main.tablePublishDatas = response.data.content;
                            main.tablePublishPageCurrent = response.data.number + 1;
                            main.tablePublishPageAll = response.data.totalPages;
                            main.reloadPostStats(page);
                          } else { main.tablePublishLoadStatus = 'failed'; }
                        }, error: function (xhr, err) { main.tablePublishLoadStatus = 'failed'; }
                      });
                }else if(page == 'draft' && (this.tableDraftAnyDeleted || this.tableDraftLoadStatus != 'loaded')){
                    main.tableDraftAnyDeleted = false;
                    main.tableDraftLoadStatus = 'loading';
                    $.ajax({
                        url: address_blog_api + 'posts/page/' + (main.tableDraftPageCurrent - 1) + '/' + main.tableDraftPageSize + '?sortBy=date&byStatus=draft&byUser=' + main.currentUser.id,
                        success: function (response) {
                          main.tableDraftLoadStatus = 'loaded';
                          if (response.success) {
                            main.tableDraftDatas = response.data.content;
                            main.tableDraftPageCurrent = response.data.number + 1;
                            main.tableDraftPageAll = response.data.totalPages;
                            main.reloadPostStats(page);
                          } else { main.tableDraftLoadStatus = 'failed'; }
                        }, error: function (xhr, err) { main.tableDraftLoadStatus = 'failed'; }
                      });
                }
            },
            reloadPostStats: function(page){
                var getPosts = { archives: [] };
                var contentPosts = null;
                if(page == 'all') contentPosts = main.tableAllDatas
                else if(page == 'publish') contentPosts = main.tablePublishDatas
                else if(page == 'draft') contentPosts = main.tableDraftDatas

                for (var key in contentPosts)
                    if (contentPosts[key].id)
                        getPosts.archives.push(contentPosts[key].id);

                var findOldPosts = function (id) {
                    for (var key in contentPosts)
                        if (contentPosts[key].id == id)
                            return contentPosts[key];
                    return null;
                }
                var reloadPostsStat = function (arr) {
                    for (var key in arr) {
                        var o = findOldPosts(key);
                        if (o) {
                            o.viewCount = arr[key].viewCount;
                            o.commentCount = arr[key].commentCount;
                            o.likeCount = arr[key].likeCount;
                        }
                    }
                }

                $.ajax({
                    url: address_blog_api + 'posts/stat',
                  type: 'post',
                  data: JSON.stringify(getPosts),
                  contentType: "application/json; charset=utf-8",
                  dataType: "json",
                  success: function (response) {
                    if (response.success) 
                      reloadPostsStat(response.data);
                  }, error: function (xhr, err) {}
                });
                
            },
        }
    });
}