appendLoaderJS("/assets/js/components/common-table.min.js");
appendLoaderJS("/assets/js/components/common-pagination.min.js");

var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
    } else {
        main.archiveLoadError = '您没有权限查看此页面';
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

            tagsLoading: false,
            contentTags: null,

            addTagColors: [
                'CCFF66',
                '66CCCC',
                'FF6666',
                'FF6600',
                'FF0033',
                '99CC33',
                'FF9900',
                'CC3399',
                '0099CC',
                '009966',
                '0099CC',
                '99CC00',
                '999999',
                '3399CC',
                'FFFF00',
                'C9999',
                'FFCC33',
            ],

            currentIsAddTag: false,
            currentAddTagName: '',
            currentAddTagColor: '',
            currentAddTagId: 0,

            tableClassesPageSize: 10,
            tableClassesLoadStatus: 'notload',
            tableClassesDatas: null,
            tableClassesColumns: [
                {
                    width: '30px',
                    text: '#',
                    useData: 'name',
                    dataName: 'id'
                },
                {
                    width: 'auto',
                    text: '分类名称',
                    useData: 'name',
                    dataName: 'title',
                    useLink: true,
                },
                {
                    width: 'auto',
                    text: '说明文字',
                    useData: 'name',
                    dataName: 'previewText',
                },
                {
                    width: 'auto',
                    text: 'URL 名字',
                    useData: 'name',
                    dataName: 'urlName',
                },
                {
                    width: '150px',
                    text: '操作',
                    useSlot: true,
                    slotName: 'marchives-slot',
                },
            ],
            tableClassesPageCurrent: 1,
            tableClassesPageAll: 1,

            currentIsAddClass: false,
            currentAddClassTitle: '',
            currentAddClassPreviewText: '',
            currentAddClassPreviewImage: '',
            currentAddClassUrlName: '',
            currentAddClassId: 0,

        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            //Loads
            loadTags() {
                this.tagsLoading = true;
                setTimeout(function () {
                    $.get(blog_api_address + "tags", function (response) {
                        if (response.success) {
                            main.contentTags = response.data;
                            main.tagsLoading = false;
                        }
                    }, "json");
                }, 500);

            },
            loadClasses(force){
                if (main.tableClassesLoadStatus != 'loaded' || force) {
                    main.tableClassesLoadStatus = 'loading';

                    var url = blog_api_address + "classes/" + (main.tableClassesPageCurrent - 1) + "/" + main.tableClassesPageSize;
                    //Load comments
                    $.ajax({
                        url: url,
                        success: function (response) {
                            if (response.success) {
                                main.tableClassesDatas = response.data.content;
                                main.tableClassesPageCurrent = response.data.number + 1;
                                main.tableClassesPageAll = response.data.totalPages;
                                main.tableClassesLoadStatus = 'loaded';
                            } else main.tableClassesLoadStatus = 'failed';
                        }, error: function (xhr, err) {main.tableClassesLoadStatus = 'failed';}
                    });
                }
            },

            addTagColorChanged() {
                this.currentAddTagColor = $('#addtag_selectColors').val();
            },

            //Tags manager
            addTag(isDlg) {
                if (isDlg) {                  
                    if(isNullOrEmpty(main.currentAddTagName)) return;
                    $('#addTagDlg').modal('hide');
                    var url = blog_api_address + "tag";
                    //POST 新的条目
                    $.ajax({
                        url: url,
                        type: "post",
                        data: JSON.stringify({
                            name: main.currentAddTagName,
                            color: main.currentAddTagColor
                        }),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (dataObj) {
                            if (dataObj.success) {
                                //重新刷新返回的数据
                                main.currentAddTagId = dataObj.data.id;
                                var newData = {};
                                var i=0;
                                for (var key in main.contentTags) {
                                    newData[key] = main.contentTags[key];
                                    i++
                                }
                                newData[i]=dataObj.data;
                                main.contentTags = newData;
                                toast('添加标签成功', 'success')
                            }
                            else swal("抱歉！提交时发生了错误!", dataObj.message, "error");
                        
                        },
                        error: function (e) { swal("抱歉！抱歉！提交时发生了错误!", e.statusText, "error"); }
                    });
                }
                else {
                    main.currentAddTagName = '';
                    main.currentAddTagColor = '';
                    this.currentIsAddTag = true;
                    $('#addTagDlg').modal('show');
                }
            },
            changeTag(id, isDlg) {
                if (isDlg) {
                    if(isNullOrEmpty(main.currentAddTagName)) return;
                    $('#addTagDlg').modal('hide');
                    var url = blog_api_address + "tag/" + id;
                    //PUT 新的条目
                    $.ajax({
                        url: url,
                        type: "put",
                        data: JSON.stringify({
                            id: id,
                            name: main.currentAddTagName,
                            color: main.currentAddTagColor
                        }),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (dataObj) {
                            if (dataObj.success) {
                                //刷新数据
                                var newData = {};
                                for (var key in main.contentTags) {
                                    if (main.contentTags[key].id == id) {
                                        main.contentTags[key].name = main.currentAddTagName;
                                        main.contentTags[key].color = main.currentAddTagColor;
                                    }
                                    newData[key] = main.contentTags[key]
                                }
                                main.contentTags = newData;
                                toast('修改标签成功', 'success')
                            }
                            else {
                                swal("抱歉！提交时发生了错误!", dataObj.message, "error");
                                successCallback(false);
                            }
                        },
                        error: function (e) { successCallback(false); swal("抱歉！抱歉！提交时发生了错误!", e.statusText, "error"); }
                    });
                }
                else {
                    for (var key in main.contentTags) {
                        if (main.contentTags[key].id == id) {
                            main.currentAddTagName = main.contentTags[key].name;
                            main.currentAddTagColor = main.contentTags[key].color;
                            main.currentAddTagId = id;
                            main.currentIsAddTag = false;
                            $('#addTagDlg').modal('show');
                            break;
                        }
                    }
                }
            },
            deleteTag(id) {
                Swal.fire({
                    type: 'warning', title: '真的要删除这个标签吗?', text: "", confirmButtonColor: '#d33', confirmButtonText: '确定',
                    showCancelButton: true, cancelButtonColor: '#3085d6',
                    cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {
                        var url = blog_api_address + "tag/" + id;
                        $.ajax({
                            url: url,
                            type: "delete",
                            dataType: "json",
                            success: function (dataObj) {
                                if (dataObj.success) {
                                    toast('删除标签成功', 'success')
                                    //删除原来的数据
                                    var newData = {};
                                    for (var key in main.contentTags) {
                                        if (main.contentTags[key].id != id)
                                            newData[key] = main.contentTags[key]
                                    }
                                    main.contentTags = newData;
                                }
                                else toast('删除标签失败 ' + dataObj.message, 'error', 5000)
                            },
                            error: function (xhr, e) { toast('删除标签失败 ' + e, 'error', 5000) }
                        });
                    }
                });
            },

            //Classes manager
            addClass(isDlg){
                if (isDlg) {                  
                    if(
                        isNullOrEmpty(main.currentAddClassTitle)
                        || isNullOrEmpty(main.currentAddClassUrlName)
                    ) return;
                    $('#addClassDlg').modal('hide');
                    var url = blog_api_address + "class";
                    //POST 新的条目
                    $.ajax({
                        url: url,
                        type: "post",
                        data: JSON.stringify({
                            title: main.currentAddClassTitle,
                            previewText: main.currentAddClassPreviewText,
                            previewImage: main.currentAddClassPreviewImage,
                            urlName: main.currentAddClassUrlName
                        }),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (dataObj) {
                            if (dataObj.success) {
                                //重新刷新返回的数据
                                main.currentAddClassId = dataObj.data.id;
                                var newData = [];
                                var i=0;
                                for (var key in main.tableClassesDatas) {
                                    newData[key] = main.tableClassesDatas[key];
                                    i++
                                }
                                newData[i]=dataObj.data;
                                main.tableClassesDatas = newData;
                                toast('添加分类成功', 'success')
                            }
                            else {
                                swal("抱歉！提交时发生了错误!", dataObj.message, "error");
                            }
                        },
                        error: function (e) { swal("抱歉！抱歉！提交时发生了错误!", e.statusText, "error"); }
                    });
                }
                else {
                    main.currentAddClassTitle = '';
                    main.currentAddClassPreviewText = '';
                    main.currentAddClassPreviewImage = '';
                    main.currentAddClassUrlName = '';
                    main.currentIsAddClass = true;
                    $('#addClassDlg').modal('show');
                }
            },
            changeClass(id, isDlg) {
                if (isDlg) {
                    if(isNullOrEmpty(main.currentAddClassTitle)) return;
                    $('#addClassDlg').modal('hide');
                    var url = blog_api_address + "class/" + id;
                    //PUT 新的条目
                    $.ajax({
                        url: url,
                        type: "put",
                        data: JSON.stringify({
                            id: id,
                            title: main.currentAddClassTitle,
                            previewText: main.currentAddClassPreviewText,
                            previewImage: main.currentAddClassPreviewImage,
                            urlName: main.currentAddClassUrlName
                        }),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (dataObj) {
                            if (dataObj.success) {
                                //刷新数据
                                var newData = [];
                                for (var key in main.tableClassesDatas) {
                                    if (main.tableClassesDatas[key].id == id) {
                                        main.tableClassesDatas[key].title = main.currentAddClassTitle;
                                        main.tableClassesDatas[key].previewText = main.currentAddClassPreviewText;
                                        main.tableClassesDatas[key].previewImage = main.currentAddClassPreviewImage;
                                        main.tableClassesDatas[key].urlName = main.currentAddClassUrlName;
                                    }
                                    newData[key] = main.tableClassesDatas[key]
                                }
                                main.tableClassesDatas = newData;
                                toast('修改分类成功', 'success')
                            }
                            else {
                                swal("抱歉！提交时发生了错误!", dataObj.message, "error");
                                successCallback(false);
                            }
                        },
                        error: function (e) { successCallback(false); swal("抱歉！抱歉！提交时发生了错误!", e.statusText, "error"); }
                    });
                }
                else {
                    for (var key in main.tableClassesDatas) {
                        if (main.tableClassesDatas[key].id == id) {
                            main.currentAddClassTitle = main.tableClassesDatas[key].title;
                            main.currentAddClassPreviewText = main.tableClassesDatas[key].previewText;
                            main.currentAddClassPreviewImage = main.tableClassesDatas[key].previewImage;
                            main.currentAddClassUrlName = main.tableClassesDatas[key].urlName;
                            main.currentAddClassId = id;
                            main.currentIsAddClass = false;
                            $('#addClassDlg').modal('show');
                            break;
                        }
                    }
                }
            },
            deleteClass(id) {
                Swal.fire({
                    type: 'warning', title: '真的要删除这个分类吗?', text: "", confirmButtonColor: '#d33', confirmButtonText: '确定',
                    showCancelButton: true, cancelButtonColor: '#3085d6',
                    cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {
                        var url = blog_api_address + "class/" + id;
                        $.ajax({
                            url: url,
                            type: "delete",
                            dataType: "json",
                            success: function (dataObj) {
                                if (dataObj.success) {
                                    toast('删除分类成功', 'success')
                                    main.loadClasses(true);
                                }
                                else toast('删除分类失败 ' + dataObj.message, 'error', 5000)
                            },
                            error: function (xhr, e) { toast('删除分类失败 ' + e, 'error', 5000) }
                        });
                    }
                });
            },
            deleteClasses(selItems, successCallback) {
                var delPosts = this.tableClassesGenItemIds(selItems);
                Swal.fire({
                    type: 'warning', title: '您真的要删除选中的 ' + Object.keys(selItems).length + ' 个分类吗?',
                    confirmButtonColor: '#d33', confirmButtonText: '确定删除', showCancelButton: true, cancelButtonColor: '#3085d6',
                    cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {

                        $.ajax({
                            url: blog_api_address + '/classes',
                            type: 'delete',
                            data: JSON.stringify(delPosts),
                            contentType: "application/json; charset=utf-8",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            success: function (response) {
                              if (response.success) {
                                toast('删除所选分类成功', 'success');
                                successCallback();
                              } else { swal('删除失败', response.message, 'error'); }
                            }, error: function (xhr, err) { swal('删除失败', err, 'error'); }
                        });

                    }
                });
                
            },

            //Classes table
            genClassUrlName(){
                //this.currentAddClassUrlName = encodeURI(this.currentAddClassTitle); 
                this.currentAddClassUrlName = this.currentAddClassTitle; 
            },
            tableClassesItemClick(item){
                window.open(getClassRealUrl(item));
            },
            tableClassesGenItemIds(selItems){
                var delPosts = { classes: [] };
                var i = 0;
                for(var key in selItems) {
                    delPosts.classes[i] = selItems[key].id;
                    i++;
                }
                return delPosts;
            },    
            tableClassesPagerClick(item){
                if(main.tableClassesPageCurrent != item){
                    main.tableClassesPageCurrent = item;
                    main.loadClasses(true);
                }
            },
            tableClassesPageSizeChanged(newv){
                if(main.tableClassesPageSize != newv){
                    main.tableClassesPageSize = newv;
                    main.tableClassesLoadStatus = 'notload'
                    main.loadClasses(true);
                }
            },
            tableClassesCustomItemClick(customerControlId, item){
                if(customerControlId == 'del-all') { 
                    var selItems = this.$refs.tableClasses.getCheckedItems();
                    if(!selItems || Object.keys(selItems).length == 0) swal('删除', '请至少选中一个条目！', 'warning');
                    else this.deleteClasses(selItems, function(){ main.loadClasses(true); })
                }
                else if(customerControlId == 'del') this.deleteClass(item.id);
                else if(customerControlId == 'edit') this.changeClass(item.id);
                
            },
        }
    });

    setTimeout(function () {
        main.loadTags();
        main.loadClasses();
    }, 500)
}