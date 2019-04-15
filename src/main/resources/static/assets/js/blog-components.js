// 评论组件
Vue.component('side-area', {
    data: function () {
        return {
            contentTagsLoaded: false,
            contentClassesLoaded: false,
            contentDatesLoaded: false,
            contentAllTagsLoaded: false,
            contentAllClassesLoaded: false,
            contentAllDatesLoaded: false,
            contentTags: null,
            contentDates: null,
            contentClasses: null,
            allClassCount: 0,
            allDateCount: 0,
            allTagCount: 0,
        }
    },
    methods: {
        loadAll() {
            var main = this;

            var tags_url = blog_api_address + "tags?maxCount=15";
            var classes_url = blog_api_address + "classes?maxCount=5";
            var dates_url = blog_api_address + "month?maxCount=5";

            $.get(tags_url, function (response) {
                if (response.success) {
                    main.contentTagsLoaded = true;
                    main.contentTags = response.data.list;
                    main.allTagCount = response.data.allCount;
                }
            }, "json");
            $.get(classes_url, function (response) {
                if (response.success) {
                    main.contentClassesLoaded = true;
                    main.contentClasses = response.data.list;
                    main.allClassCount = response.data.allCount;
                }
            }, "json");
            $.get(dates_url, function (response) {
                if (response.success) {
                    main.contentDatesLoaded = true;
                    main.contentDates = response.data.list;
                    main.allDateCount = response.data.allCount;

                }
            }, "json");
        },
        loadMoreTags() {
            var main = this;
            $.get(blog_api_address + "tags", function (response) {
                if (response.success) {
                    main.contentTags = response.data;
                    main.contentAllTagsLoaded = true;
                    main.allClassCount = response.data.length;
                }
            }, "json");
        },
        loadMoreDates() {
            var main = this;
            $.get(blog_api_address + "month", function (response) {
                if (response.success) {
                    main.contentDates = response.data;
                    main.contentAllDatesLoaded = true;
                }
            }, "json");
        },
        loadMoreClasses() {
            var main = this;
            $.get(blog_api_address + "classes", function (response) {
                if (response.success) {
                    main.contentClasses = response.data;
                    main.contentAllClassesLoaded = true;
                }
            }, "json");
        },
        goViewTag (tagId) {
            var newUrl = partPositions.viewTag + tagId + '/';
            if(location.pathname.indexOf(partPositions.viewTag) == 0 && location.pathname != partPositions.viewAll) location.href = newUrl;
            else window.open(newUrl)
        },
        goViewDate (year, month) {
            var newUrl = partPositions.viewDate + year + "/" + month + '/';
            if(location.pathname.indexOf(partPositions.viewDate) == 0 && location.pathname != partPositions.viewAll) location.href = newUrl;
            else window.open(newUrl)
        },
        goViewClass (classIdOrName) {
            var newUrl = partPositions.viewClass + classIdOrName + '/';
            if(location.pathname.indexOf(partPositions.viewClass) == 0 && location.pathname != partPositions.viewAll) location.href = newUrl;
            else window.open(newUrl)
        },
        getDateString: function (str) {
            arr = str.split("-");
            return arr[0] + " 年 " + arr[1] + " 月";
        },
    },
    template: '<div><div class="blog-side-content mt-3 no-display-sm no-display-md">\
<div class="blog-side-head mb-3">\
<div class="author">\
<img class="img-responsive mt-3" src="/images/default/head-img.jpg" alt="head-img">\
</div>\
<div class="author_name"><a href="https://www.imyzc.com/about.html" title="梦想的小鱼">DreamFish</a><span>博主</span>\
</div>\
<p>喜欢自己 快乐随意<br>I\'m an ugly man...</p>\
</div>\
</div>\
<div class="blog-side-content mt-3">\
<h5 class="blog-title">分类标签</h5>\
<div class="tags" v-if="contentTagsLoaded">\
<a v-for="tag in contentTags" class="tag-color" v-on:click="goViewTag(tag.id)"\
href="javascript:void(0);" :id="\'tag_\'+tag.id"\
:style="\'background-color:#\'+tag.color+\';\'">{{ tag.name }}</a>\
</div>\
<div v-if="contentTagsLoaded && allTagCount > 15 && !contentAllTagsLoaded" class="text-center mt-2">\
<button v-on:click="loadMoreTags" class="flat flat-btn btn-link">显示全部</button></div>\
</div>\
<div class="blog-side-content mt-3">\
<h5 class="blog-title">文章归档</h5>\
<div class="dates" v-if="contentDatesLoaded">\
<a v-for="date in contentDates" class="tag-color" v-on:click="goViewDate(date.date.substr(0,4), date.date.substr(5, 2))" href="javascript:void(0);">{{ getDateString(date.date) + \' (\' + date.count + \')\' }}</a>\
</div>\
<div v-if="contentDatesLoaded && allDateCount > 5 && !contentAllDatesLoaded" class="text-center mt-2">\
<button v-on:click="loadMoreDates" class="flat flat-btn btn-link">显示全部</button></div>\
</div>\
<div class="blog-side-content mt-3">\
<h5 class="blog-title">文章分类</h5>\
<div class="tags" v-if="contentClassesLoaded">\
<a v-for="pclass in contentClasses" class="tag-color bg-info"\
v-on:click="goViewClass(pclass.urlName ? pclass.urlName : pclass.id)"\
href="javascript:void(0);">{{ pclass.title + \' (\' + pclass.count + \')\' }}</a>\
</div>\
<div v-if="contentClassesLoaded && allClassCount > 5 && !contentAllClassesLoaded" class="text-center mt-2">\
<button v-on:click="loadMoreClasses" class="flat flat-btn btn-link">显示全部</button></div>\
</div></div>'
})