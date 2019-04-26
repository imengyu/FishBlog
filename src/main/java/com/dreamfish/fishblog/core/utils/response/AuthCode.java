package com.dreamfish.fishblog.core.utils.response;

/**
 * 认证返回值
 */
public class AuthCode {

    public final static int UNKNOW = 0;
    public final static int SUCCESS = 1;
    public final static int SUCCESS_ALREDAY_LOGGED = 2;
    public final static int SUCCESS_GUEST = 3;

    public final static int FAIL_BAD_PASSWD = -1;
    public final static int FAIL_BAD_TOKEN = -2;
    public final static int FAIL_EXPIRED = -3;
    public final static int FAIL_USER_LOCKED = -4;
    public final static int FAIL_NOUSER_FOUND = -5;
    public final static int FAIL_NOT_LOGIN = -6;
    public final static int FAIL_SERVICE_UNAVAILABLE = -7;
    public final static int FAIL_BAD_IP = -8;
    public final static int FAIL_NO_PRIVILEGE = -9;
    public final static int FAIL_BAD_PARARM = -10;
    public final static int FAIL_NOT_ACTIVE = -11;
}
