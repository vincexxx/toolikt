package com.vince.toolkit.base.util.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

public class CommonRlt<T> implements Serializable {

    private static final long serialVersionUID = 892913062179185791L;

    @JSONField(name = "rlt_code")
    private String code;// 返回码
    @JSONField(name = "rlt_msg")
    private String msg;// 返回码对应信息
    private T data;// 返回数据

    public CommonRlt() {
        super();
    }

    public CommonRlt(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CommonRlt(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String toJSONString() {
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
