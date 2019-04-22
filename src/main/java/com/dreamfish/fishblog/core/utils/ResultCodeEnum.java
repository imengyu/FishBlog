package com.dreamfish.fishblog.core.utils;

/**
 * 全局返回值
 * @author cjbi
 */
public enum ResultCodeEnum {

    OK("200", "成功"),
    BAD_REQUEST("400", "请求参数有误"),
    UNAUTHORIZED("401", "未授权"),
    FORIBBEN("403", "拒绝访问"),
    NOT_FOUNT("404", "未找到指定资源"),
    UPLOAD_ERROR("461", "上传失败"),
    PARAMS_MISS("483", "缺少接口中必填参数"),
    PARAM_ERROR("484", "参数非法"),
    FAILED_DEL_OWN("485", "不能删除自己"),
    FAILED_RES_ALREADY_EXIST("486", "指定的资源已存在"),
    FAILED_AUTH("487", "认证失败"),
    FAILED_DOWNLOAD("488", "下载请求失败"),
    FAILED_MULTIPLE_ACTION("489", "用户无法重复执行操作"),
    NOT_IMPLEMENTED("499", "接口未实现"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误"),
    NOT_VALIABLE("501", "业务异常");

    private String code;
    private String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
