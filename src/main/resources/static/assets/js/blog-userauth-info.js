var currentAuthedUser=null;
var initAuthendCallback = new Array()
var authInfoInited = false;
var authInfoCalled = false;

setLoaderEndCallback(function(){
    authLoadAuthInfo();
});

function authLoadAuthInfo(){
    $.ajax({
        url: address_blog_api + 'auth/auth-test',
        type: 'get',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            if (!isNullOrEmpty(response) && response.success) {
                currentAuthedUser=response.data;
                
                genUserMenuInfo(currentAuthedUser);
                authCallcallbacks(initAuthendCallback, currentAuthedUser);

                authInfoInited = true;
            }else  authCallcallbacks(initAuthendCallback, null);
        }, error: function (xhr, err) { authCallcallbacks(initAuthendCallback, null); tocast('连接服务器异常 请检查您的连接？', 'error', 5000);}
    });
}
function authCallcallbacks(callbacks, u) {
    callbacks.forEach(function (e) { if (typeof e === "function") e(u); });
    authInfoCalled = true;
}
function authSetInfoLoadFinishCallback(c){
    if(authInfoCalled) c(currentAuthedUser); 
    else initAuthendCallback.push(c);
    
}