/**
 * 检测字符串是否以某个字符串结尾
 */
String.prototype.endWith = function (endStr) {
    var d = this.length - endStr.length;
    return (d >= 0 && this.lastIndexOf(endStr) == d);
}
/**
 * 检测字符串是否以某个字符串开头
 */
String.prototype.startWith = function (compareStr) {
    return this.indexOf(compareStr) == 0;
}
/**
 * 除去字符串两端的空格
 */
String.prototype.trim = function () {
    return trim(this);
}
/**
 * 在 index 位置插入字符串
 * @param {*} index 要插入的位置
 * @param {*} insetstr 要被插入的字符串
 */
String.prototype.insert = function (index,insetstr) {
    return insert(this,index,insetstr);
}

/**
 * 除去字符串两端的空格
 * @param {*} str 要操作的字符串
 */
function trim(str){ return str.replace('/(^\s*)|(\s*$)/g', ''); }
/**
 * 在 index 位置插入字符串
 * @param {*} str 要操作的字符串
 * @param {*} index 要插入的位置
 * @param {*} insetstr 要被插入的字符串
 */
function insert(str,index,insetstr){
    var start = str.substr(0, index);
    var end = index < str.length ? str.substr(index) : '';
    return start + insetstr + end;
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
/**
 * 判断字符串是否是 Base64 编码
 * @param {*} str 
 */
function isBase64(str){
    return /^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$/.test(str);
}