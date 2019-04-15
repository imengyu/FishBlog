
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
    contentLoadClass: "none",
    contentClass: null,
  },
  methods: {
    loadStart: function(){
      this.contentLoadClass = getQueryString('class');
      if(isNullOrEmpty(this.contentLoadClass)) this.contentLoadClass = getLastUrlAgrs(location.href).arg;
      if(this.contentLoadClass != "none" && this.contentLoadClass.length > 0){
        this.loadCurrentClassInfo();
        this.loadClassPosts();
      }
    },
    loadClassPosts: function(){
      var url = blog_api_address + "posts/page/" + this.currentPostPage + "/10?sortBy=date&byClass=" + this.contentLoadClass;
      var oldScrollTop = $('body,html').scrollTop();
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
    loadCurrentClassInfo: function(){
      var classes_url = blog_api_address + "class/" + this.contentLoadClass;
      $.get(classes_url, function (response) {
        if (response.success) {
          main.contentClass = response.data;
        }
      }, "json");
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