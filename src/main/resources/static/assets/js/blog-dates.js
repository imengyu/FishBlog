
var main = new Vue({
  el: '#main',
  data: {
    message: 'Hello Vue.js !',
    contentMainLoading: false,
    contentMainLoadFailed: false,
    contentMainLoadError: "",
    contentPosts: null,
    currentPostPage: 0,
    allPostPage: 0,
    contentLoadDateA: "0",
    contentLoadDateM: "0",
    contentLoadDateY: "0",
    currentDateShow: "",
  },
  methods: {
    loadStart: function(){
      var cm = getQueryString('month');
      if(isNullOrEmpty(cm)) cm = getLastUrlAgrs(location.href);
      if(isNumber(cm.arg)) this.contentLoadDateM = cm.arg;
      var cy = getQueryString('year');
      if(isNullOrEmpty(cy)) cy =getLastUrlAgrs(cm.removedStr + '/');
      if(isNumber(cy.arg)) this.contentLoadDateY = cy.arg;
      if(this.contentLoadDateM != "0" && this.contentLoadDateY.length == 4){
        this.contentLoadDateA = this.contentLoadDateY + "-" + this.contentLoadDateM;
        this.loadDatePosts();
      }
      else this.currentDateShow = "神秘时间";
      $('title').text('文章归档 ' + this.currentDateShow + ' - ALONE SPACE');
    },
    loadDatePosts: function(){
      var url = address_blog_api + "posts/page/" + this.currentPostPage + "/10?sortBy=date&byDate=" + this.contentLoadDateY + "-" + this.contentLoadDateM;
      var oldScrollTop = $('body,html').scrollTop();
      this.currentDateShow =  this.contentLoadDateY + " 年 " + this.contentLoadDateM + " 月 发表的文章";
      this.contentMainLoading = true;
      $.ajax({
        url: url,
        success: function (response) {
          main.contentMainLoading = false;
          if (response.success) {
            main.allPostPage = response.data.totalPages;
            if (main.contentPosts == null) main.contentPosts = response.data.content;
            else main.contentPosts = mergeJsonArray(main.contentPosts, response.data.content);
            main.currentPostPage++;
            $('body,html').scrollTop(oldScrollTop);
          } else {
            main.contentMainLoadError = response.message;
            main.contentMainLoadFailed = true;
          }
        }, error: function (xhr, err) {
          main.contentMainLoadError = err;
          main.contentMainLoadFailed = true;
        }
      });
    },
    getDateString: function (str) {
      arr = str.split("-");
      return arr[0] + " 年 " + arr[1] + " 月";
    },
    getImageUrl: function (str) {
      return getImageUrlFormHash(str);
    },
    getPostUrl: function (post) {
      return getPostRealUrl(post)
    },
    getPostPrefix: function (prefixId){
      return getPostPrefix(prefixId);
    },
  }
})


setLoaderFinishCallback(function () {
  main.loadStart();
  setTimeout(function() {main.$refs.sideArea.loadAll()}, 500);
})