appendLoaderJS("/assets/libs/chartjs/Chart.min.js");

var main;

function initApp() {
    main = new Vue({
        el: '#main',
        data: {
            contentLoadStatus: contentLoadStatus,
            content: null,

            statData: null,
        },
        methods: {
            isCurrentUrlAndActive: function (page) { return isCurrentUrlAndActive(page) }
        }
    });
}