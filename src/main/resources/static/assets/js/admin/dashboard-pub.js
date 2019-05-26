var contentLoadStatus = 0;
var contentTitleLoaded = 0;
var contentPage = getQueryString('page');
if(contentPage == null || contentPage == '') contentPage = getLastUrlAgrs(location.href).arg;
if(contentPage == 'admin') contentPage = 'index';
if(contentPage && contentPage != '') {
    appendLoaderJS('/assets/js/admin/dashboard-' + contentPage + '.min.js')
    setLoaderEndCallback(loadBase);
    setLoaderNotHideMask();
}

var currentAuthedUser = null;

function loadBase(){
    
    $.ajax({
        url: (contentPage == 'index' ? './dashboard-index' : '../dashboard-' + contentPage) + '.html',
        success: function (response) {
            if (!isNullOrEmpty(response)) {
                contentLoadStatus = 1;
                document.getElementById('dashboard_content').innerHTML = response;
                
                initApp();
                initAuthInfo(function(){
                    initBase();
                });
                
            } else  contentLoadStatus = -1, showInitError();
        }, error: function (xhr, err) { contentLoadStatus = -2; showInitError();  }
    });
}
function initBase(){
    var container = $(".side-panel li.side-has-children ul,.side-panel li.side-has-children ul li");
    if($(window).width() < 576) {
        $('.side-area').hide();
        $('.dashboard-area').removeClass('side-open');
        setTimeout(function(){$('.side-area').show();}, 400)
    }
    $('.side-panel .side-has-children ul').css('display', 'none');
    $('.side-panel .side-has-children ul li a.active').parent().parent().attr('style', '').parent().addClass('side-child-open');
    $('.side-panel li.side-has-children').click(function(e){
        if (!container.is(e.target) && container.has(e.target).length === 0) {
            $(this).toggleClass('side-child-open');
            $(this).find('ul').slideToggle();
        }
    });
    $('.side-area-switch').click(function(){ $('.dashboard-area').toggleClass('side-open'); });
}
function initAuthInfo(c){
    $.ajax({
        url: address_blog_api + 'auth/auth-test',
        type: 'get',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            if (!isNullOrEmpty(response) && response.success) {
                currentAuthedUser=response.data;
                if(currentAuthedUser.level == 3)
                    location.href = '/sign-in/?redirect_url='+ encodeURI(location.href) + '&error=RequestMorPrivilege';
                genUserMenuInfo(currentAuthedUser);
                if(typeof initAuthInfoEnd != 'undefined') initAuthInfoEnd(currentAuthedUser);
                c();
                if(currentAuthedUser){
                    fastUserCenterLoad();
                    fastUserCenterData(currentAuthedUser);
                }
                setLoaderHideMaskNow();
            }else {
                if(response.extendCode == '-3') location.href = '/sign-in/?redirect_url='+ encodeURI(location.href) + '&error=SessionOut';
                else location.href = '/sign-in/?redirect_url='+ encodeURI(location.href) + '&error=RequestLogin';
            }
        }, error: function (xhr, err) { 
            swal('连接服务器异常', '请检查您的连接？', 'error');
        }
    });
}
function showInitError(){
    initBase();
    $('#dashboard_content').html('<div class="container" style="height:100%">\
    <div class="box full text-center text-danger d-flex justify-content-center align-items-center flex-column">\
        <i class=" fa fa-times-circle-o" aria-hidden="true" style="font-size: 3.5em"></i>\
        <p class="text-secondary mt-2"><span class="h4">加载失败</span> (T_T)</p>\
        <button type="button" class="btn btn-primary" onclick="location.reload(true)">重新加载</button>\
    </div>\
    </div>')
}
function gotoPage(page, thisPage){
    if(thisPage==contentPage) return;
    var url = (contentPage == 'index' ? '.' : '../' + page) + '/';
    if(thisPage) location.href = url;
    else window.open(url)
}
function isCurrentUrlAndActive(page){
    if(contentPage == page){
        if(contentPage != 'index' && !contentTitleLoaded) {
            $('title').text($('#admin-side-switch-' + page).text() + " " +  $('title').text());
            contentTitleLoaded = true;
        }
        return 'active' 
    }
    return '';
}