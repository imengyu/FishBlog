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
            bloggerInfo: null,
        }
    },
    methods: {
        loadAll() {
            var main = this;

            var tags_url = address_blog_api + "tags?maxCount=15";
            var classes_url = address_blog_api + "classes?maxCount=5";
            var dates_url = address_blog_api + "month?maxCount=5";
            var blogger_url = address_blog_api + "user/admin";

            $.get(blogger_url, function (response) {
                if (response.success) {
                    main.bloggerInfo = response.data;
                }
            }, "json");
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
            $.get(address_blog_api + "tags", function (response) {
                if (response.success) {
                    main.contentTags = response.data;
                    main.contentAllTagsLoaded = true;
                    main.allClassCount = response.data.length;
                }
            }, "json");
        },
        loadMoreDates() {
            var main = this;
            $.get(address_blog_api + "month", function (response) {
                if (response.success) {
                    main.contentDates = response.data;
                    main.contentAllDatesLoaded = true;
                }
            }, "json");
        },
        loadMoreClasses() {
            var main = this;
            $.get(address_blog_api + "classes", function (response) {
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
        getImageUrl: function (str) {
            return getImageUrlFormHash(str);
        },
        getUserCardBackground() {
            if (!main.bloggerInfo || isNullOrEmpty(main.bloggerInfo.cardBackground)) return "/images/background/mebg.jpg"
            else return getImageUrlFormHash(main.bloggerInfo.cardBackground);
        },
    },
    template: '<div><div class="blog-side-content mt-3 no-display-sm no-display-md">\
<div v-if="bloggerInfo" class="blog-side-head mb-3">\
<div class="author" :style="\'background: url(\'+getUserCardBackground()+\') center center no-repeat;\'">\
<img class="img-responsive mt-3" :src="getImageUrl(bloggerInfo.headimg)" alt="head-img">\
</div>\
<div class="author_name"><a :href="bloggerInfo.hone" :title="bloggerInfo.friendlyName">\
<a v-if="bloggerInfo.gender == \'男\'"><i class="fa fa-mars mr-2 text-primary"></i></a>\
<a v-if="bloggerInfo.gender == \'女\'"><i class="fa fa-venus mr-2" style="color: #f76fe5"></i></a>\
DreamFish</a><span>博主</span>\
</div>\
<p>{{ bloggerInfo.introduction }}</p>\
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