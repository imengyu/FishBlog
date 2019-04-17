/*!
 * 
 * FishBlog system 前端基础组件
 * V.1.0.0316.1000
 * 
 */


var loader_arr_css = new Array();
var loader_arr_js = new Array();
var loader_stats = "not load";
var loader_finish_callback = new Array();
var loader_endload_callback = new Array();
var loader_all_finished = false;

var allJsCount = 0;
var allCssCount = 0;
var loadedJsCount = 0;
var loadingJsIndex = 0;
var loadedCssCount = 0;

var localDebug = false;
var uidz = 1123493;
var uidg = 15;

var loading_progress_bar = null;
var body = null;

var doNotHideMask = false;

//=============== 
//Entry

function setLoaderCSS(css_arr) { loader_arr_css = css_arr; }
function setLoaderJS(js_arr) { loader_arr_js = js_arr; }
function appendLoaderCSS(css) { 
    loader_arr_css.push(css);
    allCssCount++;
}
function appendLoaderJS(js) { loader_arr_js.push(js);allJsCount++; }
function getLoaderStatus() { return loader_stats; }

function setLoaderFinishCallback(callback) { loader_finish_callback.push(callback); }
function setLoaderEndCallback(callback) { loader_endload_callback.push(callback); }
function setLoaderNotHideMask(){ doNotHideMask = true; }
function setLoaderHideMaskNow(){ loaderHideMsak(); }

//===============

//Loading funs
function preRecUrl(url, pre) {
    if (url.indexOf('cdn:') == 0) {
        if (!localDebug) return (https ? 'https://' : 'https://') + 'cdn.imyzc.com/' + url.substring(4);
        else return '/assets/' + url.substring(4);
    }
    else if (url.indexOf('/') == -1)
        return '/assets/' + pre + '/' + url;
    return url;
}
function loadCSS(url) {
    var loaded = false;
    var cssLink = document.createElement("link");
    if (url == null || url == "") { loadedCssCount++; callback(true); }

    cssLink.rel = "stylesheet";
    cssLink.rev = "stylesheet";
    cssLink.type = "text/css";
    cssLink.href = preRecUrl(url, 'css');
    cssLink.onload = cssLink.onreadystatechange = function () {
        if (!loaded && (!cssLink.readyState || /loaded|complete/.test(cssLink.readyState))) {
            cssLink.onload = cssLink.onreadystatechange = null;
            loaded = true;
            loadedCssCount++;
            update_progress();
        }
    }
    document.getElementsByTagName("head")[0].appendChild(cssLink)
}
function loadJS(src, callback) {
    if (src == null || src == "") {
        if (typeof callback === "function") callback(true);
        return;
    }

    var script = document.createElement("script");
    var head = document.getElementsByTagName("head")[0];
    var loaded = false;

    script.setAttribute('type', 'text/javascript');
    script.src = preRecUrl(src, 'js');
    if (typeof callback === "function") {
        script.onload = script.onreadystatechange = function () {
            if (!loaded && (!script.readyState || /loaded|complete/.test(script.readyState))) {
                script.onload = script.onreadystatechange = null;
                loaded = true;
                callback(loaded)
            }
        }
    }
    setTimeout(function () { if (!loaded) callback(false) }, 15000);
    head.appendChild(script)
}

//Content loader
function loaderLoadBackgroundImage() {
    if (typeof jQuery != 'undefined') {
        $('.dealy-load-bgimg').each(function () {
            var data = 'url(\'' + $(this).attr('data-original') + '\')';
            $(this).css('background-image', data);
        });
        $('.img-async').each(function () {
            $(this).attr('src', $(this).attr('data-original'));
        });
    }
}

//Progress bar
function calc_precent() { return parseInt((loadedJsCount / allJsCount) * 50) + parseInt((loadedCssCount / allCssCount) * 50); }
function update_progress() { loading_progress_bar.setAttribute('style', 'width:' + calc_precent() + '%;') }//Set progress

//===============
//Test

function loaderTestConfig() {
    //获取主机地址
    https = window.document.location.href.indexOf('https://') == 0;
    var localhostPath = getHostName();
    localDebug = localhostPath.indexOf('localhost') == 0;
    allCssCount = loader_arr_css.length;
    allJsCount = loader_arr_js.length;
}
function loaderFinishTest() {
    if (!loader_all_finished) {
        loader_all_finished = true;
        console.log("Loaded JS : " + loadedJsCount + "/" + allJsCount);
        console.log("Loaded CSS : " + loadedCssCount + "/" + allCssCount);
        if (typeof jQuery == 'undefined') {
            document.getElementById('loading').setAttribute('style', 'display:none;');
            var errtip = document.createElement('div');
            errtip.id = 'noscript-warning';
            errtip.innerHTML = "<div id=\"noscript-warning\" style=\"color:#fff;background-color:#AE0000;position:fixed;left:0;top:5px;right:0;padding:3px;width: 100%;z-index: 1050;text-align: center;\">我们需要一些额外的 JavaScript 才能正常显示页面, 但是未能成功加载，您看到的页面可能不正常.   <a href=\"javascript:void(0)\" onclick=\"location.reload(true);\">刷新</a></div>";
            document.getElementById('loading-progress-bar').appendChild(errtip);
            return;
        }
        if (loadedJsCount < allJsCount) {
            $("#loading-progress-bar").css('color', '#fff').css('background-color', '#AE0000').css('width', '100%');
            setTimeout(function () {
                setTimeout(function () {
                    $("#loading-progress-bar").prepend($("<div id=\"noscript-warning\" style=\"color:#fff;background-color:#8E0000;position:fixed;left:0;top:5px;right:0;padding:3px;width: 100%;z-index: 1050;text-align: center;\">我们需要一些额外的 JavaScript 才能正常显示页面, 但是未能成功加载，您看到的页面可能不正常.  <a href=\"javascript:void(0)\" onclick=\"loaderEnd(true);\">关闭并继续浏览</a> | <a href=\"javascript:void(0)\" onclick=\"location.reload(true);\">刷新</a></div>"));
                }, 300);
            }, 500);
        }
        /*if (loadedCssCount < allCssCount) {
            var errtip = document.createElement('div');
            errtip.id = 'noscript-warning';
            errtip.innerHTML = "<div style=\"color:#fff;background-color:#FF6600;position:fixed;left:0;top:5px;right:0;padding:3px;width: 100%;z-index: 1050;text-align: center;\">我们需要一些额外的 CSS 样式表才能正常显示页面, 但是未能成功加载，您看到的页面可能不正常.<a href=\"#\" onclick=\"document.getElementById('noscript-warning').setAttribute('style', 'display:none;');\">关闭</a></div>";
            document.getElementsByTagName('body')[0].appendChild(errtip);
        }*/
    }
}

//===============
//Worker

function loaderJsWorker() {
    loadJS(loader_arr_js[loadingJsIndex], function (succeed) {
        if (succeed) loadedJsCount++;
        loadingJsIndex++;
        update_progress();
        if (loadingJsIndex < allJsCount) loaderJsWorker()
        if (loadingJsIndex >= allJsCount) loaderEnd(false)
    })
}
function loaderStart() {

    //生成加载中遮罩
    loaderCreateUI();

    //初始化配置
    loaderTestConfig();

    //设置加载超时
    setTimeout(function () { loaderFinishTest() }, 20000);
    //加载 css
    if (loader_arr_css instanceof Array)
        for (var j = 0, len = loader_arr_css.length; j < len; j++)
            loadCSS(loader_arr_css[j])
    //加载 js
    if (loader_arr_js instanceof Array)
        loaderJsWorker()
    else loaderEnd(false)
}

//===============


function loaderEnd(click) {
    //加载 css
    if (loader_arr_css instanceof Array && loadedCssCount < allCssCount){
        for (var j = loadedCssCount, len = loader_arr_css.length; j < len; j++)
            loadCSS(loader_arr_css[j])
    }

    loaderCallcallbacks(loader_endload_callback);

    loading_progress_bar.setAttribute('style', 'width:100%');

    //load image
    if (!click) loaderFinishTest();
    loaderLoadBackgroundImage();
    loaderFinish(click);
}
function loaderCallcallbacks(callbacks) {
    callbacks.forEach(function (e) { if (typeof e === "function") e(); });
}
function loaderFinish(click) {
    //Hide loading
    if (typeof jQuery == 'undefined') {
        body.setAttribute('class', 'no-scroll');
        body.setAttribute('style', 'visibility:visible;');
        body.innerHTML = "<h1 style='text-align:center;'>加载必要的 JS 失败</h1>";
    } else {
        if (click) {
            $('#noscript-warning').fadeOut();
            $('#loading-progress-bar').css('height', '5px');
        } else if (loadedJsCount >= allJsCount) {
            $('#loading-progress-bar').css('height', '0px').css('color', '').css('background-color', '');
            $('#noscript-warning').remove();
            setTimeout(function () {
                $('#loading-progress-bar').css('display', 'none').css('height', '5px');
            }, 300);
        }
        //Init blog
        blogInitnaize();

        //发送用户浏览页面数据PV
        sendStat();

        //Call back
        loaderCallcallbacks(loader_finish_callback);
        
        if(doNotHideMask) return;

        loaderHideMsak();
    }
}
function loaderHideMsak(){

    $("#loading").fadeOut(800);
    //Body invisible class clear
    $("body").removeClass('not-load');
    $("body").removeClass('no-scroll');
}

//===============

//生成加载中遮罩
function loaderCreateUI() {
    body = document.getElementsByTagName('body')[0];
    loading_progress_bar = document.createElement('div');
    pre_loader = document.createElement('div');
    body.appendChild(loading_progress_bar);
    body.appendChild(pre_loader);
    body.setAttribute('class', 'no-scroll ' + body.getAttribute('class').replace('not-load', ''));
    loading_progress_bar.outerHTML = '<div id="loading-progress-bar"></div>';
    pre_loader.outerHTML = '<!--Loading overlay--><div id="loading" style="z-index:1001;"><div id="loading-progress-bg"></div><div id="loading-center"><div id="loading-center-absolute"><span id="loading-simple-roll"></span></div><p>加载中<br />很快就好了</p></div></div>';
    loading_progress_bar = document.getElementById('loading-progress-bar');
}

//博客组件初始化
function blogInitnaize() {

    var top = true, currentTop = true, stopResizeNav = false;
    var width = $(window).width();
    //console.log(width);
    if ($('.main-menu').first().parent().attr("id") == 'header-minimum')
        stopResizeNav = true;
    if (width < 768) {
        $('.main-menu').css('padding-top', '15px');
        $('.main-menu').css('padding-bottom', '15px');
    } else if ($('#header-minimum').length == 0) {
        $('.main-menu').css('padding-top', '40px');
        $('.main-menu').css('padding-bottom', '30px');
    } else {
        $('.main-menu').css('padding-top', '15px');
        $('.main-menu').css('padding-bottom', '15px');
    }
    if (!$('.main-menu').hasClass('noscroll')) {
        top = $(this).scrollTop() < 100;
        if (!top && !stopResizeNav) {
            $('.go-top').fadeIn();
            $('.main-menu').addClass('header-scrolled');
            if (width > 768) {
                $('.main-menu').css('padding-top', '15px');
                $('.main-menu').css('padding-bottom', '15px');
            }
        } else {
            $('.main-menu-white-auto-mask').addClass('main-menu-white-fade-mask');
        }
    }

    //Fix header height


    //-------Lazy load js --------//  
    if (typeof $("img").lazyload != 'undefined') $("img").lazyload({ effect: "fadeIn", threshold: 300 });

    //-------Load ansyc image---------//
    $(".img-async").each(function () {
        if ($(this).attr('data-original') != null) {
            $(this).attr('src', $(this).attr('data-original'));
        }
    });

    //------- Window resize event --------//  
    $(window).resize(function () {
        width = $(window).width();
    });

    //------- Superfist nav menu  js --------//  
    if (typeof $("img").superfish != 'undefined') $('.nav-menu').superfish({
        animation: {
            opacity: 'show'
        },
        speed: 200
    });

    //------- Mobile Nav  js --------//  
    if ($('#nav-menu-container').length) {
        var $mobile_nav = $('#nav-menu-container').clone().prop({
            id: 'mobile-nav'
        });
        $mobile_nav.find('> ul').attr({
            'class': '',
            'id': ''
        });
        $('body').append($mobile_nav);
        //$('body').prepend('<button type="button" id="mobile-nav-toggle"><i class="lnr lnr-menu"></i></button>');
        $('body').append('<div id="mobile-body-overly"></div>');
        $('#mobile-nav').find('.menu-has-children').prepend('<i class="fa fa-angle-down"></i>').prop('href', 'javascript;');
        $('#mobile-nav').find('.menu-has-children ul').attr('style', 'display:none;');
        $('mobile-body-overly').attr('style', 'display:none;');

        $('.menu-has-children a').click(function (e) {
            $(this).toggleClass('menu-item-active');
            $(this).nextAll('ul').eq(0).slideToggle();
            $(this).prev().toggleClass("reverse-icon");
        });
        $('.menu-has-children i').click(function (e) {
            $(this).next().toggleClass('menu-item-active');
            $(this).nextAll('ul').eq(0).slideToggle();
            $(this).toggleClass("reverse-icon");
        });
        $('#mobile-nav-toggle').click(function (e) {
            $('body').toggleClass('mobile-nav-active');
            $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
            $('#mobile-body-overly').toggle();
        });

        $(document).click(function (e) {
            var container = $("#mobile-nav, #mobile-nav-toggle");
            if (!container.is(e.target) && container.has(e.target).length === 0) {
                if ($('body').hasClass('mobile-nav-active')) {
                    $('body').removeClass('mobile-nav-active');
                    $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
                    $('#mobile-body-overly').fadeOut();
                }
            }
        });
    } else if ($("#mobile-nav, #mobile-nav-toggle").length) {
        $("#mobile-nav, #mobile-nav-toggle").hide();
    }

    //------- Back to top Scroll --------//  
    $(window).scroll(function () {
        if ($(this).scrollTop() < 300) $('.go-top').fadeOut();
        else $('.go-top').fadeIn();
    });
    //------- Back to topevent --------//  
    $('.go-top').click(function (event) {
        event.preventDefault();
        $('html, body').animate({ scrollTop: 0 }, 300);
    });
    $('.go-top').tooltip({
        boundary: 'window',
        title: '回到顶部',
        placement: 'left',
        delay: 200
    })

    //------- Enable tooltips everywhere --------//  
    $('[data-toggle="tooltip"]').tooltip();

    if (!$('.main-menu').hasClass('noscroll')) {
        //------- Header Scroll Class  js --------//  
        $(window).scroll(function () {
            top = $(this).scrollTop() < 100;
            if (!stopResizeNav && currentTop != top) {
                currentTop = top;
                if (top) {
                    $('.main-menu').removeClass('header-scrolled');
                    $('.main-trans-white').addClass('text-white');
                    $('.main-menu-white-auto-mask').addClass('main-menu-white-fade-mask');
                    if (width > 768) {
                        $('.main-menu').css('padding-top', '40px');
                        $('.main-menu').css('padding-bottom', '30px');
                    }
                } else {
                    $('.main-trans-white').removeClass('text-white');
                    $('.main-menu').addClass('header-scrolled');
                    $('.main-menu-white-auto-mask').removeClass('main-menu-white-fade-mask');
                    if (width > 768) {
                        $('.main-menu').css('padding-top', '15px');
                        $('.main-menu').css('padding-bottom', '15px');
                    }
                }
            }
        });
    }

    $('body').append($('<div class="toast-overlay-wrapper position-fixed"></div>'));
    /** Footer buttons */
    $('#footer_weixin').popover({
        trigger: 'hover',
        html: true,
        content: "<img src='/images/mmqrcode1543412167834.jpg' alt='mmqrcode' width=200 height=200/>",
        title: "扫一扫加我微信",
        placement: "top"
    });
    $('#footer_qq').tooltip({ title: 'QQ 1501076885' });
    $('#footer_github').tooltip({ title: '访问我的 Github' });
    /*
    $('#footer_qq').popover({
        trigger: 'hover',
        html: true,
        content: "<img src='/images/qrcode_1543412723170.jpg' alt='mmqrcode' width=250 height=342/>",
        placement: "top"
    });
    */
}

//PV更新
function isStatExclude(path){
    for(var expath in excludeStatPath){
        if(path.indexOf(excludeStatPath[expath]) == 0) return true;
    }
}
function sendStat(){
    if(sendStats){
        if(document.referrer!=document.location.toString() && !isStatExclude(location.pathname)){

            $.ajax({
                url: address_blog_api + 'stat',
                type: 'post',
                dataType: 'json',
                data: JSON.stringify({ "url": document.location.toString() }),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (response) {
                  main.tableUsersLoadStatus = 'loaded';
                  if (!response.success) toast('发送计数数据失败 : ' + response.message, 'error', 5000);
                }, error: function (xhr, err) {toast('发送计数数据失败 : ' + err, 'error', 5000); }
            });
        }
    }
}

//===============

function scrollToPos(top) {
    $('body,html').animate({ scrollTop: top }, 1000);
}
function scrollToEle(id) {
    if ($(id).length > 0) {
        var top = $(id).offset().top - 70;
        $('body,html').animate({ scrollTop: top }, 1000);
    }
}
function highlightChildCode($id) {
    $('#' + $id + ' pre code').each(function (i, block) {
        $(this).html(replaceBlockBadChr($(this).html()));
        hljs.highlightBlock(block);
    });
}
function highlightAllCode() {
    $('.highlight pre code').each(function (i, block) {
        $(this).html(replaceBlockBadChr($(this).html()));
        hljs.highlightBlock(block);
    });
}
function replaceBlockBadChr(str) {
    return str.replace(/^\s+|\s+$/g, '');
}

// toast('您的信息已成功提交', 'success', 3000);setTimeout(function(){ toast('信息提交失败 <a href="#">重试</a>', 'error', 3000)}, 2000);

//Toasts
//=====================================

var toasts = [];
var toastCurrentTop = 15;
var toastCount = 0;

function toastTypeToIcon(type){
    switch(type){
        case 'error':
            return '<i class="toast-icon fa fa-times-circle-o text-danger"></i>';
        case 'warning':
            return '<i class="toast-icon fa fa-exclamation-triangle text-warning"></i>';
        case 'info':
            return '<i class="toast-icon fa fa-info-circle text-primary"></i>';
        case 'success':
            return '<i class="toast-icon fa fa-check-circle-o text-success"></i>';
        case 'loading':
            return '<i class="toast-icon spinner-grow text-primary" style="width:26px;height:26px" role="status"></i>';
    }
}
function toastRemove(toast, $alert){
    toastCurrentTop -= $alert.height() + 36;    
    $alert.remove();

    for(var i = toasts.length - 1, start = toasts.indexOf(toast); i > start; i--)
        toasts[i].top = toasts[i - 1].top;
    for(var i = toasts.indexOf(toast), size = toasts.length; i < size-1; i++) {
        toasts[i] = toasts[i + 1];
        toasts[i].alert.css('top', toasts[i].top);
    }
    toasts.pop(toasts[toasts.length-1]);
    toastCount--;
}
function toastClose(toast, anim){
    $alert = toast.alert;
    if(anim=='slide') $alert.slideUp(300, function(){ toastRemove(toast, $(this)) });
    else $alert.fadeOut(600, function(){ toastRemove(toast, $(this)) });
}
function toastClear(time, toast){
    setTimeout(function(){toastClose(toast)},time)
}
function toast(str, type, time){
    uidz += parseInt(Math.random() * 10);
    if(!time) time = 2500;

    var top = toastCurrentTop;

    $newAlert = $('<div class="toast-alert" id="toast-' + uidz + '">' + toastTypeToIcon(type) + 
        '<div class="toast-text">' +  str + '</div></div>')
    $('.toast-overlay-wrapper').append($newAlert);
    $newAlert.css('top', top + 'px');
    $newAlert.css('left', ($(window).width() / 2 - $newAlert.width() / 2) + 'px');

    toastCurrentTop += $newAlert.height() + 36;
    var toast = {
        alert: $newAlert,
        top: top
    };
    toasts[toastCount] = toast;
    toastCount++;

    if(time!=-1) toastClear(time, toast);
    return toast;
}

function getHostName() {
    var curWwwPath = window.document.location.href;
    if (curWwwPath.indexOf('https://') == 0)
        curWwwPath = curWwwPath.substr(7);
    else if (curWwwPath.indexOf('https://') == 0)
        curWwwPath = curWwwPath.substr(8);
    var pathName = window.document.location.pathname;
    if (pathName == '') {
        return curWwwPath;
    } else {
        var pos = curWwwPath.indexOf(pathName);
        return curWwwPath.substring(0, pos);
    }
}

window.onload = loaderStart;