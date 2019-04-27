var currentAuthedUser=null;

setLoaderEndCallback(function(){
    $.ajax({
        url: address_blog_api + 'auth/auth-test',
        type: 'get',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            if (!isNullOrEmpty(response) && response.success) {
                currentAuthedUser=response.data;
                genUserMenuInfo(currentAuthedUser);

                if(typeof initAuthInfoEnd != 'undefined') initAuthInfoEnd(currentAuthedUser);
            }else  if(typeof initAuthInfoEnd != 'undefined') initAuthInfoEnd(null);
        }, error: function (xhr, err) { if(typeof initAuthInfoEnd != 'undefined') initAuthInfoEnd(null); tocast('连接服务器异常 请检查您的连接？', 'error', 5000);}
    });
});