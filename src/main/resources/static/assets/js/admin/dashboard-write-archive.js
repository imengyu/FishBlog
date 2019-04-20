appendLoaderJS("/assets/js/components/fisheditor.min.js");
appendLoaderJS("/assets/libs/compress/base64.min.js");
appendLoaderJS("/assets/libs/editormd/js/editormd.min.js");
appendLoaderCSS("/assets/libs/editormd/css/editormd.min.css");
appendLoaderJS("/assets/js/components/common-table.min.js");
appendLoaderJS("/assets/js/components/common-pagination.min.js");
appendLoaderJS("/assets/js/components/common-image-list.min.js");
appendLoaderJS("/assets/libs/BigPicture/BigPicture.min.js");

var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
        main.loadArchive();
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

            classficationLoaded: false,
            tagsLoading: false,
            classesLoading: false,
            contentTags: null,
            contentClasses: null,

            tableCommentAllPageSize: 10,
            tableCommentAllLoadStatus: 'notload',
            tableCommentAllDatas: null,
            tableCommentAllColumns: [
                {
                    width: '30px',
                    text: '#',
                    useData: 'name',
                    dataName: 'id'
                },
                {
                    width: '140px',
                    text: '用户名(ID) / IP',
                    useData: 'name',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return item.authorName + ' (' + item.authorId + ') <br/> ' + (item.authorIp ? item.authorIp : '');
                    }
                },
                {
                    width: '120px',
                    text: '时间',
                    useData: 'name',
                    dataName: 'postDate'
                },
                {
                    width: 'auto',
                    text: '内容',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return base64.decode(item.commentContent);
                    }
                },
                {
                    width: '70px',
                    text: '操作',
                    useSlot: true,
                    slotName: 'marchives-slot',
                },
            ],
            tableCommentAllPageCurrent: 1,
            tableCommentAllPageAll: 1,

            isNew: false,

            archiveObject: null,
            archiveLoadError: '',
            archiveLoadStatus: 'notload',
            archiveId: 0,
            archiveType: 'unknow',
            archiveStatus: 0,
            archiveTags: null,
            archiveClass: '',
            archiveContent: '',
            archiveShowLastModifyDate: true,

            mediaCenterItems: null,
            mediaCenterLoadStatus: 'notload',
            mediaCenterPageCurrent: 1,
            mediaCenterPageAll: 1,
            mediaCenterPageSize: 10,

            mediaCenterCurrentEditItem: null,
            mediaCenterCurrentEditItemOldTitle: '',

            mdEditor: null,
        },
        watch: {
            archiveType(newV, oldV) {
                if (newV == 'html') {
                    setTimeout(function () { main.$refs.fishHtmlEditor.init() }, 500);
                } else if (newV == 'markdown') {
                    setTimeout(function () { main.initEditorMd() }, 1000);
                }
                $('#marchives-tab li:first-child a').tab('show')
            }
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },
            //
            initEditorMd() {
                main.mdEditor = editormd("marchive-editormd", {
                    width: "100%",
                    height: main.getEditorHeight(),
                    syncScrolling: "single",
                    path: "/assets/libs/"
                });

                var tick;
                main.mdEditor.on("change", function(){
                    clearTimeout(tick);
                    tick=setTimeout(function(){main.contentChanged(main.mdEditor.getMarkdown());}, 200);
                })
            },
            contentChanged(content){
                this.archiveContent = content;
            },
            getEditorHeight(){
                return $('#dashboard_content').height() - 100;
            },

            //标签
            addTag(id) {
                var newData = {};
                var i = 0;
                for (var key in main.archiveTags) {
                    if (main.archiveTags[key].id == id) {
                        //已经有这个标签了
                        toast('已经添加这个标签了哦！', 'info');
                        return;
                    }
                    newData[i] = main.archiveTags[key];
                    i++;
                }
                for (var key in main.contentTags) {
                    if (main.contentTags[key].id == id) {
                        newData[i] = main.contentTags[key];
                        break;
                    }
                }
                main.archiveTags = newData;
            },
            removeTag(id) {
                var newData = {};
                for (var key in main.archiveTags) {
                    if (main.archiveTags[key].id != id)
                        newData[key] = main.archiveTags[key];
                }
                main.archiveTags = newData;
            },
            clearTag(){
                Swal.fire({
                    type: 'warning', title: '真的要清空该文章上的所有标签？', text: "", confirmButtonColor: '#d33', confirmButtonText: '确定',
                    showCancelButton: true, cancelButtonColor: '#3085d6',
                    cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {
                        main.archiveTags = [];
                        toast('清空文章标签成功', 'success')
                    }
                });
            },

            genPostUrlName(){
                this.archiveObject.urlName = encodeURI(this.archiveObject.title); 
            },
            //跳转
            gotoManageTags() {
                gotoPage('manage-tags', false);
            },
            gotoManageClasses() {
                gotoPage('manage-classes', false);
            },
            goView(){
                window.open(getPostRealUrl(archiveObject));
            },

            //设置类型
            setArchiveType(type) {
                Swal.fire({
                    type: 'warning', // 弹框类型
                    title: '真的要修改文章类型吗', //标题
                    text: "注意，修改文章类型以后会使用不同的编辑器，需要您手动修改文章，以保证显示正常", //显示内容

                    confirmButtonColor: '#3085d6',// 确定按钮的 颜色
                    confirmButtonText: '确定',// 确定按钮的 文字
                    showCancelButton: true, // 是否显示取消按钮
                    cancelButtonColor: '#aaa', // 取消按钮的 颜色
                    cancelButtonText: "取消", // 取消按钮的 文字

                    focusCancel: true, // 是否聚焦 取消按钮
                    reverseButtons: true  // 是否 反转 两个按钮的位置 默认是  左边 确定  右边 取消
                }).then((isConfirm) => {
                    if (isConfirm.value) {
                        main.archiveType = type;
                        toast('修改文章类型成功！', 'success')
                    }
                });
            },

            getPostPrefix(p){
                return getPostPrefix(p)
            },
            getArchiveCanPublish() {
                return main.archiveStatus == archiveStatus.PRIVATE || main.archiveStatus == archiveStatus.DRAFT;
            },
            getArchiveCanUnPublish() {
                return main.archiveStatus == archiveStatus.PUBLISH;
            },
            getArchiveCanUnChange() {
                return !main.isNew;
            },

            //加载
            loadArchive() {
                if (!this.currentUser) return;
                var currentPostPage = getQueryString('archive');
                if (currentPostPage == null) currentPostPage = getLastUrlAgrs(location.href).arg;
                if (!isNumber(currentPostPage)) {
                    if (currentPostPage == '' || currentPostPage == 'write-archive') {

                        main.isNew = true;

                        //新建文章初始化
                        //初始化元素
                        main.archiveObject = {

                            author: "DreamFish",
                            authorId: main.currentUser.id,
                            commentCount: 0,
                            content: "",
                            enableComment: true,
                            headimg: "",
                            headimgMask: false,
                            id: 0,
                            postClass: "",
                            postDate: "",
                            postNextId: 0,
                            postNextTitle: "",
                            postPrefix: 0,
                            postPrvId: 0,
                            postTagNames: [],
                            previewImage: "",
                            previewText: "",
                            status: 1,
                            tags: "",
                            title: "",
                            type: "markdown",
                            urlName: "",
                            viewCount: 0,
                        };
                        main.archiveLoadError = '';
                        main.archiveId = 0;
                        main.archiveType = 'markdown';
                        main.archiveStatus = archiveStatus.DRAFT;//草稿
                        main.archiveTags = [];
                        main.archiveClass = '';
                        main.archiveContent = '';

                        main.archiveLoadStatus = 'loaded';
                    } else {
                        main.archiveLoadStatus = 'failed';
                        main.archiveLoadError = '找不到指定的文章';
                    }
                } else {
                    main.archiveLoadStatus = 'loading';

                    //下载文章完整内容
                    var url = address_blog_api + "post/" + currentPostPage + "?authPrivate=true";
                    $.ajax({
                        url: url,
                        success: function (response) {
                            if (response.success) {
                                main.archiveObject = response.data;
                                if(response.data.content)  main.archiveContent = base64.decode(response.data.content);
                                else main.archiveContent = '';
                                main.archiveId = response.data.id;
                                main.archiveType = response.data.type;
                                main.archiveTags = response.data.postTagNames;
                                main.archiveStatus = response.data.status;
                                if(response.data.postClass) main.archiveClass = response.data.postClass.split(':')[0];
                                main.archiveLoadStatus = 'loaded';

                                //从管理页点 发布 来的
                                if(main.getArchiveCanPublish() && getQueryString('publish')=='click'){
                                    main.publish();
                                }

                            } else {
                                main.archiveLoadStatus = 'failed';
                                main.archiveLoadError = response.message;
                            }
                        }, error: function (xhr, err) {
                            main.archiveLoadStatus = 'failed';
                            main.archiveLoadError = err;
                        }
                    });

                }
            },
            loadClassfication() {
                if (!this.classficationLoaded) {
                    this.loadClasses(true);
                    this.loadTags(true);
                    this.classficationLoaded = true;
                }
            },
            loadClasses(fromAuto) {
                this.classesLoading = true;
                setTimeout(function () {
                    $.get(address_blog_api + "classes", function (response) {
                        if (response.success) {
                            main.contentClasses = response.data;
                            main.classesLoading = false;
                            if (!fromAuto) toast('刷新文章分类列表成功', 'success', 3000);
                        }
                    }, "json");
                }, 500);
            },
            loadTags(fromAuto) {
                this.tagsLoading = true;
                setTimeout(function () {
                    $.get(address_blog_api + "tags", function (response) {
                        if (response.success) {
                            main.contentTags = response.data;
                            main.tagsLoading = false;
                            if (!fromAuto) toast('刷新文章标签列表成功', 'success', 3000);
                        }
                    }, "json");
                }, 500);

            },
            loadComments(force) {
                if (!main.isNew && (main.tableCommentAllLoadStatus != 'loaded' || force)) {
                    main.tableCommentAllLoadStatus = 'loading';

                    var url = address_blog_api + "post/" + main.archiveId + "/comments/" + (main.tableCommentAllPageCurrent - 1) + "/" + main.tableCommentAllPageSize;
                    //Load comments
                    $.ajax({
                        url: url,
                        success: function (response) {
                            if (response.success) {
                                main.tableCommentAllDatas = response.data.content;
                                main.tableCommentAllPageCurrent = response.data.number + 1;
                                main.tableCommentAllPageAll = response.data.totalPages;
                                main.tableCommentAllLoadStatus = 'loaded';
                            } else main.tableCommentAllLoadStatus = 'failed';
                        }, error: function (xhr, err) {main.tableCommentAllLoadStatus = 'failed';}
                    });
                }
            },
            loadMediaCenter(force) {
                if (!main.isNew && (main.mediaCenterLoadStatus != 'loaded' || force)) {
                    main.tableCommentAllLoadStatus = 'loading';

                    var url = address_blog_api + "images/post/" + main.archiveId + "/" + (main.mediaCenterPageCurrent - 1) + "/" + main.mediaCenterPageSize;
                    //Load comments
                    $.ajax({
                        url: url,
                        success: function (response) {
                            if (response.success) {
                                main.mediaCenterItems = response.data.content;
                                main.mediaCenterPageCurrent = response.data.number + 1;
                                main.mediaCenterPageAll = response.data.totalPages;
                                main.mediaCenterLoadStatus = 'loaded';
                            } else main.mediaCenterLoadStatus = 'failed';
                        }, error: function (xhr, err) {main.mediaCenterLoadStatus = 'failed';}
                    });
                }
            },

            //评论列表事件
            tableGenItemIds(selItems){
                var delPosts = { comments: [] };
                var i = 0;
                for(var key in selItems) {
                    delPosts.comments[i] = selItems[key].id;
                    i++;
                }
                return delPosts;
            },
            tableCommentAllPagerClick(item){
                if(main.tableCommentAllPageCurrent != item){
                    main.tableCommentAllPageCurrent = item;
                    main.loadComments(true);
                }
            },
            tableCommentAllPageSizeChanged(newv){
                if(main.tableAllPageSize != newv){
                    main.tableAllPageSize = newv;
                    main.tableAllLoadStatus = 'notload'
                    main.loadComments();
                }
            },
            tableCommentAllCustomItemClick(customerControlId, item){
                if(customerControlId == 'del') {
                    Swal.fire({
                        type: 'warning', title: '确定删除此条评论？', text: "此评论将会被删除", confirmButtonColor: '#d33', confirmButtonText: '确定',
                        showCancelButton: true, cancelButtonColor: '#3085d6',
                        cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                    }).then((isConfirm) => {
                        if (isConfirm.value) {

                            var url = address_blog_api + "post/" + main.archiveId + "/comments/" + item.id;
                            $.ajax({
                                url: url,
                                type: 'delete',
                                contentType: "application/json; charset=utf-8",
                                dataType: "json",
                                contentType: "application/json; charset=utf-8",
                                success: function (response) {
                                  main.tableAllLoadStatus = 'loaded';
                                  if (response.success) {
                                    toast('删除所选评论成功', 'success');
                                    main.loadComments(true);
                                  } else { swal('删除失败', response.message, 'error'); }
                                }, error: function (xhr, err) { swal('删除失败', err, 'error'); }
                            });
                        }
                    });
                }else if(customerControlId == 'del-all') {
                    var delComments = this.tableGenItemIds(main.$refs.tableCommentAll.getCheckedItems());
                    if(Object.keys(delComments.comments).length == 0){
                        swal('没有要删除的评论', '请先选择需要删除的评论', 'info');
                        return;
                    }
                    Swal.fire({
                        type: 'warning', title: "确定删除选中的 " + Object.keys(delComments.comments).length + " 条评论？", text: "选中的评论将会被删除", confirmButtonColor: '#d33', confirmButtonText: '确定',
                        showCancelButton: true, cancelButtonColor: '#3085d6',
                        cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                    }).then((isConfirm) => {
                        if (isConfirm.value) {
                            var url = address_blog_api + "post/" + main.archiveId + "/comments";
                            $.ajax({
                                url: url,
                                type: 'delete',
                                data: JSON.stringify(delComments),
                                contentType: "application/json; charset=utf-8",
                                dataType: "json",
                                contentType: "application/json; charset=utf-8",
                                success: function (response) {
                                  main.tableAllLoadStatus = 'loaded';
                                  if (response.success) {
                                    toast('删除所选评论成功', 'success');
                                    main.loadComments(true);
                                  } else { swal('删除失败', response.message, 'error'); }
                                }, error: function (xhr, err) { swal('删除失败', err, 'error'); }
                            });

                        }
                    });
                }
            },
            


            //媒体库事件
            mediaListAdd(link, title){
               
                if(!title)title='';
                if(main.archiveType == 'markdown'){
                    $('#marchives-tab li:first-child a').tab('show')

                    var objStart = '!['+title+']('+ link+' "'+title+'")'
                    if(main.mdEditor.cm.somethingSelected()){
                        var old = main.mdEditor.cm.getSelection();
                        main.mdEditor.cm.replaceSelection(objStart + old);
                    }
                    else main.mdEditor.cm.replaceSelection(objStart);
                    setTimeout(function(){
                        main.mdEditor.cm.refresh();
                    },300)
                    
                }else if(main.archiveType == 'html'){

                    $('#marchives-tab li:first-child a').tab('show')
                    main.$refs.fishHtmlEditor.insertOrReplace('<div class="pgc-img"><img src="'+link+'" alt="'+title+'"><p class="pgc-img-caption">'+title+'</p></div>', null, false, true)
                    
                }else toast('纯文本文章无法插入图片哦！','info', 5000);
            },
            mediaListEditTitle(item){
                this.mediaCenterCurrentEditItem = item;
                this.mediaCenterCurrentEditItemOldTitle = item.title;
                $('#editMediaImageDlg').modal('show');
            },
            mediaListEditTitleSave(){
                if(this.mediaCenterCurrentEditItem) {

                    $.ajax({
                        url: address_blog_api + "images/post/" + main.archiveId + '/' + main.mediaCenterCurrentEditItem.hash,
                        type: "put",
                        data: JSON.stringify(main.mediaCenterCurrentEditItem),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function (dataObj) {
                            if (dataObj.success) {
                                //重新刷新返回的数据
                                main.mediaCenterCurrentEditItem = dataObj.data;
                                toast('更新图片标题成功！','success');
                            }
                            else swal("抱歉！上传失败了!", "提交时发生了错误 " + dataObj.message, "error");
                        },
                        error: function (e) { swal("抱歉！上传失败了!", "提交时发生了错误 " + e.statusText, "error"); }
                    });

                    this.mediaCenterCurrentEditItem = null;
                }
            },
            mediaListShowBig(el, item){
                BigPicture({
                    el: el.get(0),
                    imgSrc: getImageUrlFormHashWithType(item.hash, item.type)
                });
            },
            mediaListDelete(item){
                Swal.fire({
                    type: 'warning', title: '真的要删除这张图片吗', html: "它将会彻底删除，<br/>注意：在文章中的图片引用需要您<span class=\"text-primary\">手动</span>删除", confirmButtonColor: '#d33', confirmButtonText: '确定',
                    showCancelButton: true, cancelButtonColor: '#3085d6',
                    cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {
                        $.ajax({
                            url: address_blog_api + "images/post/" + main.archiveId + '/' + item.hash,
                            type: "delete",
                            contentType: "application/json; charset=utf-8",
                            dataType: "json",
                            success: function (dataObj) {
                                if (dataObj.success) {
                                    main.loadMediaCenter(true);
                                    toast('删除图片成功！','success');
                                }
                                else swal("抱歉！删除失败了!", "提交时发生了错误 " + dataObj.message, "error");
                            },
                            error: function (e) { swal("抱歉！删除失败了!", "提交时发生了错误 " + e.statusText, "error"); }
                        });
                    }
                });
            },
            mediaListCopyLink(link){
                $('#copyText').val(link).focus().select();
	            if (document.execCommand('copy', false, null)) {
                    toast('复制链接成功！您可以直接在文章中粘贴使用', 'success', 4500);
	            }else toast('无法复制链接，您可以右键点击图片，选择复制图片链接', 'error', 7500);
            },

            //
            mediaCenterListPagerClick(item) {
                if (main.mediaCenterPageCurrent != item) {
                    main.mediaCenterPageCurrent = item;
                    main.loadMediaCenter(true);
                }
            },
            mediaCenterListPageSizeChanged(newv) {
                if (main.mediaCenterPageSize != newv) {
                    main.mediaCenterPageSize = newv;
                    main.mediaCenterLoadStatus = 'notload'
                    main.loadMediaCenter();
                }
            },
            changeUploadImage(){
                var uploadImage = document.getElementById("uploadImage");
                var fileObj = uploadImage.files[0]; // js 获取文件对象
                var url = address_blog_api + 'images/post/' + main.archiveId;
                var t = toast('正在上传...', 'loading', -1);

                //上传成功响应
                var uploadComplete = function (evt) {
                    toastClose(t);
                    //服务断接收完文件返回的结果
                    var data = JSON.parse(evt.target.responseText);
                    if (data.success) {
                        //设置 新返回的 图片 hash 值
                        main.loadMediaCenter(true);
                        toast('上传图片成功！', 'success', 5000);
                    }
                    else toast('上传图片失败！' + data.message + ' ( ' + data.extendCode + ' )', 'error', 5000);
                }
                //上传失败
                var uploadFailed = function uploadFailed(evt) {
                    toastClose(t);
                    toast('上传图片失败！请检查您的网络', 'error', 5000);
                }

                var form = new FormData(); // FormData 对象
                form.append("file", fileObj); // 文件对象

                xhr = new XMLHttpRequest();  // XMLHttpRequest 对象
                xhr.open("post", url, true); //post方式，url为服务器请求地址，true 该参数规定请求是否异步处理。
                xhr.onload = uploadComplete; //请求完成
                xhr.onerror = uploadFailed; //请求失
                xhr.send(form); //开始上传，发送form数据

                uploadImage.value = '';
            },
            uploadImage(){ $('#uploadImage').click() },

            //
            //
            //<p class="text-success"><i class="fa fa-check-circle-o"></i> 出现这行字说明博客更新提交成功了！</p>

            //参数验证
            saveValidate(){

                if(isNullOrEmpty(this.archiveObject.title)) {
                    swal('您必须输入文章的标题才能保存文章哦', '您正在提交没有标题的文章', 'warning');
                    return true;
                }
                if(isNullOrEmpty(this.archiveContent)) {
                    swal('您必须输入写一些文章内容才能保存文章哦', '您正在提交一篇空的文章', 'warning');
                    return true;
                }


            },
            //文章提交操作
            //保存
            saveSubmit(targetStatus, successCallback){

                //先验证参数
                if(this.saveValidate()){
                    successCallback(false);
                    return;
                }

                //同步一些已修改数据至 Object 中
                this.archiveObject.type = this.archiveType;
                //生成Tags标签数据
                var tagsStr = '-';
                for(var key in this.archiveTags) 
                    tagsStr += this.archiveTags[key].id + '-';
                if(tagsStr!='-')
                    this.archiveObject.tags = tagsStr;
                if(this.archiveShowLastModifyDate)
                    this.archiveObject.lastmodifyDate = new Date().format('YYYY-MM-dd HH:ii:ss');
                else this.archiveObject.lastmodifyDate = null;
                this.archiveObject.postClass = this.archiveClass;
                //拷贝内容
                this.archiveObject.content = base64.encode(this.archiveContent);
                //设置一些初始值
                if (main.isNew) {
                    if(isNullOrEmpty(this.archiveObject.urlName)) genPostUrlName(); 
                    if(main.currentUser) {
                        //设置作者id
                        main.archiveObject.authorId = main.currentUser.id;
                        //设置作者名字 
                        if(!isNullOrEmpty(main.currentUser.friendlyName))
                            main.archiveObject.author = main.currentUser.friendlyName;
                        else if(!isNullOrEmpty(main.currentUser.name))
                            main.archiveObject.author = main.currentUser.name;
                    }      
                }
                //修改文章状态
                if(targetStatus!=-1){
                    this.archiveObject.status = targetStatus;
                    this.archiveStatus = targetStatus;
                }

                setTimeout(function () {

                    //提交
                    if (main.isNew) {
                        var url = address_blog_api + "post";
                        //POST 新的条目
                        $.ajax({
                            url: url,
                            type: "post",
                            data: JSON.stringify(main.archiveObject),
                            contentType: "application/json; charset=utf-8",
                            dataType: "json",
                            success: function (dataObj) {
                                if (dataObj.success) {
                                    //重新刷新返回的数据
                                    main.archiveObject = dataObj.data;
                                    main.archiveId = main.archiveObject.id;
                                    //提交以后将变为修改文章
                                    main.isNew = false;
                                    successCallback();
                                }
                                else {
                                    swal("抱歉！发表失败了!", "提交时发生了错误 " + dataObj.message, "error");
                                    successCallback(false);
                                }
                            },
                            error: function (e) { successCallback(false); swal("抱歉！发表失败了!", "提交时发生了错误 " + e.statusText, "error"); }
                        });
                    } else {
                        var url = address_blog_api + "post/" + main.archiveId;
                        //PUT 更新条目
                        $.ajax({
                            url: url,
                            type: "put",
                            data: JSON.stringify(main.archiveObject),
                            contentType: "application/json; charset=utf-8",
                            dataType: "json",
                            success: function (dataObj) {
                                if (dataObj.success)
                                    successCallback(true);
                                else {
                                    swal("抱歉！发表失败了!", "提交时发生了错误 " + dataObj.message, "error");
                                    successCallback(false);
                                }
                            },
                            error: function (e) { successCallback(false); swal("抱歉！发表失败了!", "提交时发生了错误 " + e.statusText, "error"); }
                        });
                    }

                }, 600);
            },


            //一些按钮
            //放弃修改
            cancelChange() {
                Swal.fire({
                    type: 'warning', title: '真的要放弃修改吗', text: "注意，您的未保存修改将会丢失！", confirmButtonColor: '#d33', confirmButtonText: '确定',
                    showCancelButton: true, cancelButtonColor: '#3085d6',
                    cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {
                        main.loadArchive();
                        toast('已将文章恢复为修改前状态', 'success')
                    }
                });
            },
            //保存修改
            saveChange(st) {
                Swal.fire({
                    type: 'question', title: '您真的要现在保存修改吗', text: "", confirmButtonColor: '#3085d6', confirmButtonText: '保存',
                    showCancelButton: true, cancelButtonColor: '#999', cancelButtonText: "再改改", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {

                        var targetStatus = -1;
                        var t = toast('正在提交文章', 'loading', -1);
                        if (st=='draft') targetStatus = archiveStatus.DRAFT;
                        else if(st=='private') targetStatus = archiveStatus.PRIVATE;
                        else if(st=='publish') targetStatus = archiveStatus.PUBLISH;
                        main.saveSubmit(targetStatus, function(success){
                            toastClose(t);
                            if(success) swal('文章修改提交成功！', '', 'success')
                        });
                    }
                });
            },
            //发布
            publish() {
                Swal.fire({
                    type: 'warning', title: '真的要发布文章吗', text: "您的文章将会被发布", confirmButtonColor: '#3085d6', confirmButtonText: '立即发布',
                    showCancelButton: true, cancelButtonColor: '#999', cancelButtonText: "再改改", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {

                        var t = toast('正在提交文章', 'loading', -1);
                        main.saveSubmit(archiveStatus.PUBLISH, function(success){
                            toastClose(t);
                            if(success) swal('文章发布成功！', '', 'success')
                        });                  
                    }
                });
            },
            //撤回
            unPublish() {
                Swal.fire({
                    type: 'warning', title: '真的要撤回文章吗', text: "撤回后您的文章只有您自己能看到（它将被保存在草稿箱中）", confirmButtonColor: '#3085d6', confirmButtonText: '确定撤回',
                    showCancelButton: true, cancelButtonColor: '#999', cancelButtonText: "取消", focusCancel: true, reverseButtons: true
                }).then((isConfirm) => {
                    if (isConfirm.value) {

                        var t = toast('正在撤回文章', 'loading', -1);
                        main.saveSubmit(archiveStatus.DRAFT, function(success){
                            toastClose(t);
                            if(success) toast('成功将文章撤回到草稿箱', 'success');
                        });                  
                    }
                });
            },
        }
    });
    window.onbeforeunload = function() {
        return "请确认您的文章保存了以后再离开此页面哦！";
    };
}