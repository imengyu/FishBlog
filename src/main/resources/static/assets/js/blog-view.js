var main = new Vue({
    el: '#main',
    data: {
        message: 'Hello Vue.js!',
        currentPostIdOrName: null,
        currentPostId: 0,
        isLoading: false,
        isLoaded: false,
        loadFailed: false,
        laodErr: "",

        postTags: null,
        postContent: null,
        postContentWithHtml: null,
        postCanComment: false,

        prvPostTitle: "没有了",
        nextPostTitle: "没有了",
        hasPrvPost: false,
        hasNextPost: false,

        previewCommentItem: null,
    },
    methods: {
        loadPost: function () {
            var currentPostPage = getQueryString('post');
            if (currentPostPage == null) currentPostPage = getLastUrlAgrs(location.href).arg;
            this.currentPostIdOrName = currentPostPage;
            if (!isNullOrEmpty(this.currentPostIdOrName) && currentPostPage != 'post') {
                var url = address_blog_api + "post/" + this.currentPostIdOrName;
                this.isLoading = true;
                $.ajax({
                    url: url,
                    success: function (response) {
                        main.isLoading = false;
                        if (response.success) {
                            main.postContent = response.data;
                            main.currentPostId = response.data.id;
                            main.postTags = response.data.postTagNames;
                            if (main.postContent.enableComment) main.postCanComment = true;
                            main.generatePostContent();
                            main.initComment();
                            main.isLoaded = true;
                            main.updateViewCount();
                        } else {
                            main.laodErr = response.message;
                            main.loadFailed = true;
                        }
                    }, error: function (xhr, err) {
                        main.isLoading = false;
                        main.laodErr = err;
                        main.loadFailed = true;
                    }
                });
            } else {
                main.isLoading = false;
                main.laodErr = "没有找到指定的文章";
                main.loadFailed = true;
            }
        },

        //Generate
        generateHtml: function () {
            var a = null;
            //Choose archive types
            if (this.postContent.type == "html") a = base64.decode(this.postContent.content);
            else if (this.postContent.type == "markdown") { a = base64.decode(this.postContent.content); var b = new showdown.Converter(); a = b.makeHtml(a); }
            else if (this.postContent.type == "txt") a = this.postContent.content;
            this.postContentWithHtml = a;
        },
        generatePostContent: function () {
            this.generateHtml();
            //设置上一篇下一篇
            if (this.postContent.postPrvTitle) {
                this.prvPostTitle = this.postContent.postPrvTitle;
                this.hasPrvPost = true;
            }
            if (this.postContent.postNextTitle) {
                this.nextPostTitle = this.postContent.postNextTitle;
                this.hasNextPost = true;
            }
            if (this.postContent.headimgMask) {
                //$('.main-menu-white').addClass('main-menu-white-fade-mask');
            }
            $('title').text(this.postContent.title + ' - ALONE SPACE');
            setTimeout(function () {
                //前期代码处理
                //高亮代码
                $('#post_content pre code').each(function (i, block) {
                    $(this).html(replaceBlockBadChr($(this).html()));
                    $(this).parent().before($('<div class="bd-clipboard"><button class="btn-clipboard" title data-original-title="复制到剪贴板">复制</button></div>'));
                    hljs.highlightBlock(block);
                });
                //剪贴板提示
                $('.btn-clipboard').tooltip();          
                //加载图片
                $('#post_content img').each(function (i, block) {
                    var dsrc = $(this).attr('data-src');
                    if(!isNullOrEmpty(dsrc)) $(this).attr('src', getImageUrlFormHashWithType(dsrc, $(this).attr('data-image-type')));
                    //图片加载失败处理
                    if (!this.complete || (typeof this.naturalWidth != "undefined" && this.naturalWidth == 0)){
                        $(this).bind('error.replaceSrc', function () {
                            $(this).attr('src',  "/images/img_failed.png");
                            $(this).attr('style',  "width: 250px");
                            $(this).unbind('error.replaceSrc');
                        }).trigger('load');
                    }
                    if (this.complete && (typeof this.naturalWidth != "undefined" && this.naturalWidth == 0)){
                        $(this).attr('src',  "/images/img_failed.png");
                        $(this).attr('style',  "width: 250px");
                    }
                });                
                //大图支持
                $('#post_content img').click(function () {
                    BigPicture({
                        el: $(this).get(0),
                        imgSrc: $(this).attr('src')
                    });
                });
                //目录生成
                main.generateCatalog();
            }, 500);
            setTimeout(function () {
                //跳转到参数位置
                if(!isNullOrEmpty(location.hash))
                    scrollToEle(decodeURI(location.hash));
            }, 1500);
        },
        generateCatalog: function () {
            //Create catalog
            var P = $('div.post-container'), a, n, t, l, i, c, id;
            a = P.find('h1,h2,h3,h4,h5,h6');
            var $catalog_body = $('#catalog_content');
            var $catalog_scroller = $("#side_catalog");

            a.each(function () {
                n = $(this).prop('tagName').toLowerCase();
                $(this).attr('id', $(this).text().replace(/(!|\"|#|\$|%|&|\'|\(|\)|\*|\+|\?|\^|\{|\}|,|\.|\/|:|;|<|=|>|@|\[|\]|\(|\)|`|\\|~| ){1}/g, '-'));
                i = "#" + $(this).prop('id');
                t = $(this).text();
                c = $('<a href="' + i + '" rel="nofollow">' + t + '</a>');
                l = $('<li class="' + n + '_nav"></li>').append(c);
                $catalog_body.append(l);
            });
            // toggle side catalog
            $(".catalog-toggle").click((function (e) {
                e.preventDefault();
                $('.side-catalog').toggleClass("fold")
            }))
            // onePageNav
            $catalog_body.onePageNav({
                currentClass: "active",
                changeHash: !1,
                easing: "swing",
                filter: "",
                scrollSpeed: 700,
                scrollOffset: -70,
                scrollThreshold: .2,
                begin: null,
                end: null,
                scrollChange: function($parent) {

                },
            });
            // Bulid anchors
            anchors.add().remove('.blog-content h1').remove('.no-anchor').remove('.sidebar-container h5');

            var width = $(window).width();
            var catalog_floated = false, catalog_bottom_pined = false;
            var $post_content_end = $('#post_content_end');
            var $side_catalog = $('#side_catalog');
            var old_catalog_top = $side_catalog.offset().top - 65;           

            catalogScroll();
            //Catalog scroll 
            function catalogScroll()
            {
                if (width >= 992) {
                    if ($(document).scrollTop() >= old_catalog_top && !catalog_floated) {
                        $side_catalog.addClass('side-catalog-fixed');
                        catalog_floated = true;
                    } else if ($(document).scrollTop() <= old_catalog_top && catalog_floated) {
                        $side_catalog.removeClass('side-catalog-fixed');
                        catalog_floated = false;
                    } else if ($(document).scrollTop() > $post_content_end.offset().top - $side_catalog.height() - 60 && !catalog_bottom_pined) {
                        $side_catalog.removeClass('side-catalog-fixed').attr('style', 'position: absolute; top: auto; bottom: 0px;');
                        catalog_bottom_pined = true;
                        catalog_floated = true;
                    } else if ($(document).scrollTop() <= $post_content_end.offset().top - $side_catalog.height() - 60 && catalog_bottom_pined) {
                        $side_catalog.attr('style', ''); 
                        catalog_bottom_pined=false;
                        catalog_floated = false;
                    }

                }else {
                    $side_catalog.addClass('side-catalog-fixed');
                    if ($(document).scrollTop() > $post_content_end.offset().top - 200 && !catalog_bottom_pined) {
                        $('#side_catalog_on_toggle').fadeOut();
                        if ($('body').hasClass('side-catalog-mobile-active')) {
                            $('body').removeClass('side-catalog-mobile-active');
                            $('#side_catalog').removeClass('side-catalog-slidein');
                        }
                        catalog_bottom_pined = true;
                    } else if ($(document).scrollTop() <= $post_content_end.offset().top - 200 && catalog_bottom_pined) {
                        catalog_bottom_pined=false;
                        $('#side_catalog_on_toggle').fadeIn();
                    }
                }
            }
            
            $(document).scroll(function (e) { catalogScroll(); });
            $(window).resize(function () { width = $(window).width(); });    
            $(document).click(function (e) {
                var container = $("#side_catalog, #side_catalog_close_toggle, #side_catalog_on_toggle");
                if (!container.is(e.target) && container.has(e.target).length === 0) {
                    if ($('body').hasClass('side-catalog-mobile-active')) {
                        $('body').removeClass('side-catalog-mobile-active');
                        $('#side_catalog').removeClass('side-catalog-slidein');
                    }
                }
            });   
        },
        switchCatalog: function() {
            $('#side_catalog').toggleClass('side-catalog-slidein');
            $('body').toggleClass('side-catalog-mobile-active');
        },
        initComment: function () {
            if (this.postContent.enableComment){
                setTimeout(function () {
                    setTimeout(function () { main.$refs.postComment.loadLastUserInfo(); }, 1500);
                    //加载第一次的评论
                    var postHeight = $('#comment').offset().top;
                    var commentFirstLoaded = false;
                    $(document).scroll(function (e) {
                        if ($(document).scrollTop() >= postHeight && !commentFirstLoaded) {
                            main.$refs.postComment.loadPostComment();
                            commentFirstLoaded = true;
                        }
                    });
                }, 500);
            }
        },
        updateViewCount: function(){
            setTimeout(function () {
                if(document.referrer!=document.location.toString())
                    $.get(address_blog_api + "post/updateViewCount?id=" + main.currentPostId);
            },1500)
        },

        //Clicks
        getNextPostUrl: function () {
            if (this.postContent.postNextId != 0)
                return partPositions.viewPost + this.postContent.postNextId + "/";
            return 'javascript:void(0)';
        },
        getPrevPostUrl: function () {
            if (this.postContent.postPrvId != 0)
                return partPositions.viewPost + this.postContent.postPrvId + "/";
            return 'javascript:void(0)';
        },
        goViewTag: function (tagId) {
            window.open(partPositions.viewTag + tagId + '/')
        },
        //Image Url
        getImageUrl: function (str) {
            return getImageUrlFormHash(str);
        },
        getPostPrefix: function (prefixId){
            return getPostPrefix(prefixId);
        },
    }
})

setLoaderFinishCallback(function () {
    main.loadPost();
})