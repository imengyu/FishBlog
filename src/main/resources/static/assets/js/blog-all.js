
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
      var url = address_blog_api + "posts/page/" + this.currentPostPage + "/15?sortBy=date";
      $('#main').css('height', $('#main').height() + 'px');

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
            main.reloadPostStats();
            main.currentPostPage++;
          } else {
            main.contentMainLoadError = response.message;
            main.contentMainLoadFailed = true;
          }
          $('#main').css('height', '');
        }, error: function (xhr, err) {
          main.contentMainLoadError = err;
          main.contentMainLoadFailed = true;
          $('#main').css('height', '');
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
          if (this_y != y && !arr[key].topMost) {
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
    reloadPostStats: function(){
      var getPosts = { archives: [] };
      for(var key in main.contentPosts)
        if(main.contentPosts[key].id) 
          getPosts.archives.push(main.contentPosts[key].id);

      var findOldPosts = function(id){
        for(var key in main.contentPosts)
          if(main.contentPosts[key].id==id) 
            return main.contentPosts[key];
        return null;
      }
      var reloadPostsStat = function(arr){
        for(var key in arr) {
          var o = findOldPosts(key);
          if(o){
            o.viewCount = arr[key].viewCount;
            o.commentCount = arr[key].commentCount;
          }
        }
      }

      $.ajax({
        url: address_blog_api + 'posts/stat',
        type: 'post',
        data: JSON.stringify(getPosts),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
          if (response.success) 
            reloadPostsStat(response.data);
        }, error: function (xhr, err) {}
      });
      
    },
  }
})


setLoaderFinishCallback(function () {
  main.loadPosts();
  setTimeout(function() {main.$refs.sideArea.loadAll()}, 500);
})