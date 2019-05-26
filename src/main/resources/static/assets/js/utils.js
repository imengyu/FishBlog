function mergeJSON(minor, main) {
  for(var key in minor) {
      if(main[key] === undefined) { // 不冲突的，直接赋值 
          main[key] = minor[key];
          continue;
      }
      // 冲突了，如果是Object，看看有么有不冲突的属性
      // 不是Object 则以（minor）为准为主，
      if(isJSON(minor[key])||isArray(minor[key])) { // arguments.callee 递归调用，并且与函数名解耦 
        main[key] = mergeJSON(minor[key], main[key]);
      }else{
        main[key] = minor[key];
      }
  }
  return main;
}
function isJSON(target) {
  return typeof target == "object" && target.constructor == Object;
}
function isArray(o) {
  return Object.prototype.toString.call(o) == '[object Array]';
}
function mergeJsonArray(a, b){
  var r = {};
  var i = 0;
  for(var key in a){ 
    r[i] = a[key];
    i++;
  }
  for(var key in b){ 
    r[i] = b[key];
    i++;
  }
  return r;
}
function getLastUrlAgrs(url){
  if(url.lastIndexOf('/')>0)
    url=url.substr(0, url.lastIndexOf('/'));
  var rp = url.lastIndexOf('/');
  var rs = {
    arg:'',
    removedStr:''
  }
  if(rp > 0){
    rs.arg = url.substr(rp + 1);
    rs.removedStr = url.substr(0, rp);
  }
  return rs;
}
function getLastHashAgr(url){
  if(url.lastIndexOf('#')>0)
    return url.split('#')[1];
  return rs;
}
function getCurrentFullHost(){
  return location.protocol + '//' + location.host;
}
function getPostPrefix(prefixId){
  switch(prefixId){
    case 0: return '';
    case 1: return '<span class="tag-post-prefix original">原创</span>';
    case 2: return '<span class="tag-post-prefix reprint">转载</span>';
    case 3: return '<span class="tag-post-prefix album">视频</span>';
    case 4: return '<span class="tag-post-prefix video">相册</span>';
  }
  return '';
}
function getImageUrlFormHash(str){
  if(str && str.indexOf('http') == 0) return str;
  else if(str) return address_image_center + str + ".jpg";
  else return str;
}
function getImageUrlFormHashWithType(hash, type){
  var ftype = type ? type : 'jpg';
  if(hash && hash.indexOf('http') == 0) return hash;
  else if(hash) return address_image_center + hash + "." + ftype;
  else return hash;
}
function getPostRealUrl(post){
  return partPositions.viewPost + (post.urlName ? post.urlName.replace(/\+/g, ' ') : post.id) + '/'
}
function getClassRealUrl(post){
  return partPositions.viewClass + (post.urlName ? post.urlName.replace(/\+/g, ' ') : post.id) + '/'
}
function getQueryString(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
  var r = window.location.search.substr(1).match(reg);
  if (r != null) return unescape(r[2]); return null;
}
function getUserHasPrivilege(userData, privilege){
  if(!userData) return false;
  return (userData.privilege & privilege) != 0;
}
function genUserMenuInfo(userData) {
  $('.nav-user').remove();
  var userMsgPoint = userData.messageCount ? '<span class="current-user-message-count">' + userData.messageCount + '</span>' : '';
  var userName = isNullOrEmpty(userData.friendlyName) ? userData.name : userData.friendlyName;
  var userHead = isNullOrEmpty(userData.headimg) ? '/images/default/head-default.png' : getImageUrlFormHash(userData.headimg);
  if (userData.level == userLevels.admin || userData.level == userLevels.writer) {
    $('#header-menu').append($('<li class="nav-user">' + (location.pathname != '/admin/write-archive/' ? '<button type="button" class="flat-pill flat-btn flat-btn-transparent flat-danger mr-2" onclick="location.href = \'/admin/write-archive/\'">写文章</button>' : '') + '<div id="current_user_center_dropdown" class="dropdown"><img class="current-user-head" src="' + userHead + '">' + userMsgPoint + '</div><div id="current_user_dropdown" class="dropdown"><span id="current_user_name" class="current-user-name" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-offset="0,20">' + userName + '<i></i></span></div></li>'));
    $('#current_user_dropdown').append($('<div class="dropdown-menu dropdown-menu-right shadow-dirty">\
      <a class="dropdown-item" href="/admin/write-archives/">写文章</a>\
      <a class="dropdown-item" href="/admin/manage-archives/">文章管理</a>\
      <a class="dropdown-item" href="/admin/user-center/">个人信息</a>\
      <div class="dropdown-divider"></div>\
      <a class="dropdown-item" href="/sign-out/">退出登录</a>\
    </div>'));
  } else {
    $('#header-menu').append($('<li class="nav-user"><div id="current_user_center_dropdown" class="dropdown"><img class="current-user-head" src="' + userHead + '">' + userMsgPoint + '</div><div id="current_user_dropdown" class="dropdown"><span id="current_user_name" class="current-user-name" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-offset="0,20">' + userName + '<i></i></span></div></li>'));
    $('#current_user_dropdown').append($('<div class="dropdown-menu dropdown-menu-right shadow-dirty">\
      <a class="dropdown-item" href="/user/">我的个人信息</a>\
      <a class="dropdown-item" href="/sign-out/">退出登录</a>\
    </div>'));
  }
  $('#current_user_name').click(function(){
    $('.nav-user-menu').dropdown('toggle');
  })


}
/**
 * 判断一个字符串是否为空
 * @param {*} str 要判断的字符串
 */
function isNullOrEmpty(str){
  if(typeof str == 'undefined') return true;
  if(str==null || str=='')return true;
  return false;
}
function isNumber(val) {
  var regPos = /^\d+(\.\d+)?$/; //非负浮点数
  var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
  if(regPos.test(val) || regNeg.test(val)) {
      return true;
      } else {
      return false;
      }
}
function pad(num, n) {
  var len = num.toString().length;
  while(len < n) {
    num = "0" + num;
    len++;
  }
  return num;
}

/**  
 * 日期格式化（原型扩展或重载）  
 * 格式 YYYY/yyyy/ 表示年份  
 * MM/M 月份  
 * dd/DD/d/D 日期  
 * @param {formatStr} 格式模版  
 * @type string  
 * @returns 日期字符串  
 */  
Date.prototype.format = function(formatStr){   
  var str = formatStr;   
  //var Week = ['日','一','二','三','四','五','六'];   
  str=str.replace(/yyyy|YYYY/,this.getFullYear());  
  str=str.replace(/MM/,pad(this.getMonth() + 1, 2));   
  str=str.replace(/dd|DD/, pad(this.getDate(), 2));   
  str=str.replace(/HH/,pad(this.getHours(), 2));   
  str=str.replace(/hh/,pad(this.getHours()>12?this.getHours()-12:this.getHours(), 2));   
  str=str.replace(/mm/,pad(this.getMinutes(), 2));
  str=str.replace(/ii/,pad(this.getMinutes(), 2));
  str=str.replace(/ss/,pad(this.getSeconds(), 2));
 return str;   
} 

//textare getCursorPosition
function getInputCursorPosition(input){
  var el = $(input).get(0);
  var pos = 0;
  if ('selectionStart' in el) {
      pos = el.selectionStart;
  } else if ('selection' in document) {
      el.focus();
      var Sel = document.selection.createRange();
      var SelLength = document.selection.createRange().text.length;
      Sel.moveStart('character', -el.value.length);
      pos = Sel.text.length - SelLength;
  }
  return pos;
}