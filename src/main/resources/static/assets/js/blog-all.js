
var main = new Vue({
  el: '#main',
  data: {
    message: 'Hello Vue.js!',
    contentMainLoading: false,
    contentMainLoadFailed: false,
    contentMainLoadError: "",
    contentPosts: null,
    currentPostPage: 0,
    allPostPage: 0,
  },
  methods: {
    loadPosts: function () {
      var url = blog_api_address + "posts/page/" + this.currentPostPage + "/15?sortBy=date";
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
            main.contentPosts = main.resolvePostArr(main.contentPosts);
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
    getPostPrefix: function (prefixId){
      return getPostPrefix(prefixId);
    },
    getPostUrl: function (post) {
      return getPostRealUrl(post)
    },
    resolvePostArr: function(arr){
      var new_arr = {};
      var i = 0, y = "";
      for (var key in arr) { 
        if (arr[key].isHead) continue;
        if (typeof arr[key].postDate != 'undefined') {
          var this_y = arr[key].postDate.substr(0, 4);
          if (this_y != y) {
            y = this_y;
            new_arr[i] = {
              isHead: true,
              year: this_y
            }
            i++;
          }
        }
        new_arr[i] = arr[key];
        i++;
      }

      return new_arr;
    },
  }
})


setLoaderFinishCallback(function () {
  main.loadPosts();
  setTimeout(function() {main.$refs.sideArea.loadAll()}, 500);
})