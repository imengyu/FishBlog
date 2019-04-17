appendLoaderJS("/assets/libs/chartjs/Chart.min.js");
appendLoaderJS("/assets/js/components/common-table.min.js");
appendLoaderJS("/assets/js/components/common-pagination.min.js");

var main;

function initApp() {
    main = new Vue({
        el: '#main',
        data: {
            contentLoadStatus: contentLoadStatus,
            content: null,

            statData: null,
            dayLog: null,
            topTenPage: null,
            topTenArchives: null,

            showFullPageLog: false,

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
                    width: '100px',
                    text: '日期',
                    useData: 'name',
                    dataName: 'date',
                },
                {
                    width: 'auto',
                    text: '访问URL',
                    useData: 'name',
                    dataName: 'url',
                },
                {
                    width: '50px',
                    text: '查看数',
                    useData: 'name',
                    dataName: 'count',
                },
            ],
            tableAllPageCurrent: 1,
            tableAllPageAll: 1,
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) },

            loadStatSimple() {
                var main = this;
                $.get(address_blog_api + "stat", function (response) {
                    if (response.success) main.statData = response.data;
                }, "json");
                $.get(address_blog_api + "stat/daylog", function (response) {
                    if (response.success){
                        main.dayLog = response.data;
                        if(ain.dayLog) setTimeout(main.loadStatDayLogChart, 500);
                    }
                }, "json");
                $.get(address_blog_api + "stat/topPage?maxCount=10", function (response) {
                    if (response.success) main.topTenPage = response.data;
                }, "json");
                $.get(address_blog_api + "stat/topPost?maxCount=10", function (response) {
                    if (response.success) main.topTenArchives = response.data;
                }, "json");
            },
            loadStatDayLogChart(){
                var labels = [];
                var datasPv = [];
                var datasIp = [];
                var datasComment = [];

                for(var key in main.dayLog){
                    labels.push(main.dayLog[key].date);
                    datasPv.push(main.dayLog[key].pv);
                    datasIp.push(main.dayLog[key].ip);
                    datasComment.push(main.dayLog[key].comment);
                }

                var ctx = document.getElementById("chartDayLog").getContext("2d");
                var myLineChart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: '当日的评论数',
                            data: datasComment,
                            backgroundColor: ['rgba(138, 195, 73, 0.2)'],
                            borderColor: ['rgba(76, 175, 80, 1)'],
                            borderWidth: 1
                        },{
                            label: '当日访客数（基于IP）',
                            data: datasIp,
                            backgroundColor: ['rgba(33, 148, 242, 0.2)'],
                            borderColor: ['rgba(33, 148, 242, 1)'],
                            borderWidth: 1
                        },{
                            label: '当日页面查看数（PageView）',
                            data: datasPv,
                            backgroundColor: ['rgba(255, 99, 132, 0.2)'],
                            borderColor: ['rgba(255, 99, 132, 1)'],
                            borderWidth: 1
                        },]
                    },
                });
            },
            loadFullPageStat(force){
                if(force || this.tableAllLoadStatus != 'loaded'){
                    main.tableAllLoadStatus = 'loading';
                    $.ajax({
                        url: address_blog_api + 'stat/daylog/' + (main.tableAllPageCurrent - 1) + '/' + main.tableAllPageSize,
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
            tableAllScrollTo(){
                setTimeout(function(){
                    var top = $("#full_log_stat").offset().top - 70;
                    $('#dashboard_content').scrollTop(1000);
                }, 500);
            },
            tableAllPagerClick(item){
                if(main.tableAllPageCurrent != item){
                    main.tableAllPageCurrent = item;
                    main.loadFullPageStat(true);
                }
            },
            tableAllPageSizeChanged(newv){
                if(main.tableAllPageSize != newv){
                    main.tableAllPageSize = newv;
                    main.tableAllLoadStatus = 'notload'
                    main.loadFullPageStat(true);
                }
            },

            getPostUrl(post) {
                return getPostRealUrl(post)
            },
        }
    });

    setTimeout(function () { main.loadStatSimple(); }, 300)
}