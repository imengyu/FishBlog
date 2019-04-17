setLoaderFinishCallback(function(){
    initVerifyCode();
	initLastSets();
	initErrorInfo();
});

var authCode = {
	FAIL_BAD_PASSWD: -1,
    FAIL_BAD_TOKEN: -2,
    FAIL_EXPIRED: -3,
    FAIL_USER_LOCKED: -4,
    FAIL_NOUSER_FOUND: -5,
    FAIL_NOT_LOGIN: -6,
    FAIL_SERVICE_UNAVAILABLE: -7,
    FAIL_BAD_IP: -8,
    FAIL_NO_PRIVILEGE: -9
}
var verifyed = false;
var loginSending = false;

function initVerifyCode(){
    $('#valid_panel').slideVerify({
		type : 2,		//类型
		vOffset : 5,	//误差量，根据需求自行调整
		vSpace : 5,	//间隔
		imgName : ['/images/background/verify/1.jpg', '/images/background/verify/2.jpg', '/images/background/verify/3.jpg', '/images/background/verify/4.jpg', '/images/background/verify/5.jpg'],
		imgSize : {
			width: '296px',
			height: '200px',
		},
		blockSize : {
			width: '40px',
			height: '40px',
		},
		barSize : {
			width : '100%',
			height : '40px',
		},
		ready : function() {
		},
		success : function() {
			$('#valid_panel').fadeOut(150, function() {
				$('#valid_panel').html('<div class="verify-bar-area" style="border-color: rgb(92, 184, 92); background-color: rgb(255, 255, 255);height: 40px; line-height: 40px;"><span class="verify-msg" style="color: rgb(92, 184, 92);"><i class="fa fa-check-circle-o"></i> 验证成功！</span></div>');
				$('#valid_panel').fadeIn(200);
			})
			verifyed = true;
		},
		error : function() {
			verifyed = false;
		}
		
	});
}
function initLastSets(){
	var v = window.localStorage.getItem("last_user");
	if(!isNullOrEmpty(v)) $('#log_usrname').val(v);
}
function resetVerifyCode(){
	verifyed = false;
	$('#valid_panel').html('');
	initVerifyCode();
}
function initErrorInfo(){
	var a = getQueryString('error');
    if(!isNullOrEmpty(a)) {
		switch(a){
			case 'BadRequest': swal('错误的登录请求', '请检查登录请求是否正确', 'error'); break;
			case 'SessionOut': swal('您的登录信息已过期', '为了保证安全，您需要重新登录', 'warning'); break;
			case 'RequestLogin': swal('请登录', '您需要登录才能访问该页面', 'warning'); break;
		}
	}
}

function login(){
	var userName = $('#log_usrname').val();
	var psw = $('#log_psw').val();
	var url = address_blog_api + "auth";

    if(loginSending) return;
	
	loginSending = true;
	$('#logon_form').fadeOut(200, function(){
		$('#log_sending').fadeIn();
	})

	var reshowLog = function(){
		loginSending = false;
		$('#logon_form').fadeIn(200, function(){
			$('#log_sending').fadeOut();
		})
	}

	if(isNullOrEmpty(userName)){
		$('#log_usrname').addClass('is-invalid');
		return;
	}
	if(isNullOrEmpty(psw)){
		$('#log_psw').addClass('is-invalid');
		return;
	}
	if(!verifyed){
		swal("请先验证", "您需要完成验证才能登录", "info");
		return;
	}

    $("#log_psw").val('');
    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify({
			"name": userName,
			"passwd": md5(psw)
		}),
		contentType: "application/json; charset=utf-8",  
        dataType: "json",
        success: function(data) {
            if (data.success) {
				window.localStorage.setItem("last_user", $("#log_usrname").val());
				var a = getQueryString('redirect_url');
    			if(!isNullOrEmpty(a))location.href = decodeURI(a);
				else location.href = '/admin/';
			} else {
				resetVerifyCode();
				reshowLog();
				var extendCode = data.extendCode;
				if(extendCode == authCode.FAIL_NOUSER_FOUND)
					swal("登录失败", "用户不存在", 'error')
				else if(extendCode == authCode.FAIL_USER_LOCKED)
					swal("登录失败", "该用户已被封禁，无法登录。请联系管理员解封", 'error')
				else if(extendCode == authCode.FAIL_BAD_PASSWD)
					swal("登录失败", "用户名或密码不正确", 'error')
				else swal("登录失败", data.message, 'error')
			}
        },
        error: function(jqXHR, errMsg) { resetVerifyCode(); reshowLog(); swal("登录失败", "请求失败 : " + errMsg, 'error') }
    })
}
function loginGithub(){
	Swal.fire({
		title: '登录中',
		type: 'info',
		html: '<div class="text-center"><span class="simple-loading"></span><p class="text-secondary mt-2">正在登录中，请稍后</p></div>', // HTML
		focusConfirm: true, //聚焦到确定按钮
		showCloseButton: true,//右上角关闭
	})
	location.href = 'https://github.com/login/oauth/authorize?client_id=d31012693b9ba3773cde&scope=user&redirect_uri=' + encodeURI(getCurrentFullHost() + address_blog_api + 'auth/githubAuthCallback/user');
}
function loginWeiXin(){
	swal("暂不支持微信登录", "敬请期待", "info");
}

function j(){
	if(!$('#logon-switch-admin').hasClass('active')){
		$('#logon-switch-guest').removeClass('active');
		$('#logon-switch-admin').addClass('active');
		$('#logon-guest-ctl').fadeOut(200, function(){ $('#logon-admin-ctl').fadeIn() })
	}
}
function f(){
	if(!$('#logon-switch-guest').hasClass('active')){
		$('#logon-switch-admin').removeClass('active');
		$('#logon-switch-guest').addClass('active');
		$('#logon-admin-ctl').fadeOut(200, function(){ $('#logon-guest-ctl').fadeIn() })
	}
}
function k(){ if (event.keyCode == 13) login() }
function c(n){ $('#' + n).removeClass('is-invalid'); }