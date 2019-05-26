appendLoaderJS("/assets/js/components/common-table.min.js");
appendLoaderJS("/assets/js/components/common-pagination.min.js");
appendLoaderJS("/assets/libs/compress/base64.min.js");

var main;

function initAuthInfoEnd(user) {
    main.userInfoLoaded = true;
    if (user && (getUserHasPrivilege(user, userPrivileges.globalSettings) || user.level == userLevels.admin)) {
        if (user.level == userLevels.admin || (user.privilege & userPrivileges.manageAllArchives) != 0)
            main.userHasManagePrivilege = true;
        main.currentUser = user;
        main.loadSettings();
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

            settingsData: null,
            settingsOldData: null,
            settingsLoadFailed: false,

            tableAllPageSize: 10,
            tableAllLoadStatus: 'notload',
            tableAllDatas: null,
            tableAllColumns: [
                {
                    width: 'auto',
                    text: '时间',
                    useData: 'name',
                    dataName: 'datetime',
                },
                {
                    width: 'auto',
                    text: '操作',
                    useData: 'name',
                    dataName: 'action',
                },
                {
                    width: '90px',
                    text: '操作用户/IP',
                    useData: 'custom',
                    customDataFunc: function(item){
                        return '<span class="badge badge-pill badge-primary mr-2">' + item.userId + '</span>' + item.userName + '<br/><span class="text-secondary">' + item.ip + '</span>' ;
                    }
                },
            ],
            tableAllPageCurrent: 1,
            tableAllPageAll: 1,

            anySetFailed: false,

            setSendStats: true,
            setAnonymousComment: true,
            setMaxStatSaveDays: 30,
            setimageCenter: '',
            setEnableSearch: true,
            setEnableRegister: true,
            setsideCustomArea: '',

            version: '未知',
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            findSet(name){
                for(var key in main.settingsData){
                    if(main.settingsData[key].name==name)
                        return main.settingsData[key];                   
                }
                return null;
            },
            loadSettings(){
                $.ajax({
                    url: address_blog_api + 'settings',
                    success: function (response) {
                        if (response.success) { 
                            main.settingsData = response.data; 

                            main.setSendStats = main.findSet('sendStats').data == 'true';
                            main.setAnonymousComment = main.findSet('anonymousComment').data == 'true';
                            main.setMaxStatSaveDays = parseInt(main.findSet('maxStatSaveDays').data);
                            main.setimageCenter = main.findSet('imageCenter').data;
                            main.setsideCustomArea = base64.decode(main.findSet('sideCustomArea').data.replace(" ","+"));
                            main.setEnableSearch = main.findSet('enableSearch').data == 'true';
                        }
                        else { toast('加载设置失败！' + response.message,'error',5000); main.settingsLoadFailed = true;}
                    }, error: function (xhr, err) { toast('加载设置失败！' + err,'error',5000); main.settingsLoadFailed = true }
                });
            },
            loadOpLog(force){
                if(force || this.tableAllLoadStatus != 'loaded'){
                    main.tableAllLoadStatus = 'loading';
                    $.ajax({
                        url: address_blog_api + 'stat/oplog/' + (main.tableAllPageCurrent - 1) + '/' + main.tableAllPageSize,
                        success: function (response) {
                          main.tableAllLoadStatus = 'loaded';
                          if (response.success) {
                            main.tableAllDatas = response.data.content;
                            main.tableAllPageCurrent = response.data.number + 1;
                            main.tableAllPageAll = response.data.totalPages;
                          } else { main.tableAllLoadStatus = 'failed'; }
                        }, error: function (xhr, err) { main.tableAllLoadStatus = 'failed'; }
                    });
                }
            },
            loadVersion(){
                $.ajax({
                    url: address_blog_api + 'version',
                    success: function (response) {
                        if (response.success) { main.version = response.data; }        
                    }, error: function (xhr, err) {  }
                }); 
            },
            saveSettings(){

                main.anySetFailed = false;
                if(main.setSendStats != (main.findSet('sendStats').data == 'true'))
                    main.updateSettings('sendStats', main.setSendStats ? 'true' : 'false');
                if(main.setAnonymousComment != (main.findSet('anonymousComment').data == 'true'))
                    main.updateSettings('anonymousComment', main.setAnonymousComment ? 'true' : 'false');
                if(main.setMaxStatSaveDays != main.findSet('maxStatSaveDays').data)
                    main.updateSettings('maxStatSaveDays', main.setMaxStatSaveDays);
                if(main.setimageCenter != main.findSet('imageCenter').data)
                    main.updateSettings('imageCenter', main.setimageCenter);
                if(base64.encode(main.setsideCustomArea) != main.findSet('sideCustomArea').data)
                    main.updateSettings('sideCustomArea', base64.encode(main.setsideCustomArea));
                if(main.setEnableSearch != (main.findSet('enableSearch').data == 'true'))
                    main.updateSettings('enableSearch', main.setEnableSearch ? 'true' : 'false');
                if(main.setEnableRegister != (main.findSet('enableRegister').data == 'true'))
                    main.updateSettings('enableRegister', main.setEnableRegister ? 'true' : 'false');
                if(!main.anySetFailed) toast('保存设置成功', 'success', 3000);
            },
            updateSettings(name, val){
                $.ajax({
                    url: address_blog_api + 'settings/' + name,
                    type: 'post',
                    data: 'value=' + val,
                    success: function (response) {
                        if (!response.success) { 
                            toast('保存设置 ' + name + ' 失败！' + response.message,'error',5000); 
                            main.anySetFailed = true;
                        }
                    }, error: function (xhr, err) { toast('保存设置 ' + name + ' 失败！' + err,'error',5000); main.anySetFailed = true }
                });
            },

            tableAllPagerClick(item){
                if(main.tableAllPageCurrent != item){
                    main.tableAllPageCurrent = item;
                    main.loadOpLog(true);
                }
            },
            tableAllPageSizeChanged(newv){
                if(main.tableAllPageSize != newv){
                    main.tableAllPageSize = newv;
                    main.tableAllLoadStatus = 'notload'
                    main.loadOpLog(true);
                }
            },
        }
    });
}