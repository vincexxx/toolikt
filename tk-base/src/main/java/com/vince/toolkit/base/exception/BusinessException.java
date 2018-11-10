package com.vince.toolkit.base.exception;

import com.vince.toolkit.base.util.result.BaseRltCode;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 8031080537422413357L;

    private String code = BaseRltCode.E_9999;
    private String msg = "error";

    public BusinessException() {
        super();
    }

    public BusinessException(String code, String msg) {
        super("[code]:" + code + "[msg]:" + msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String code, String msg, Throwable cause) {
        super(cause);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
        this.msg = String.format(msgFormat, args);
    }

    public BusinessException(String code, String msgFormat, Object... args) {
        super("[code]:" + code + "[msg]:" + String.format(msgFormat, args));
        this.code = code;
        this.msg = String.format(msgFormat, args);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
