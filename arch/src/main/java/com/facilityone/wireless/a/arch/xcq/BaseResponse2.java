package com.facilityone.wireless.a.arch.xcq;

import java.io.Serializable;

public class BaseResponse2<T> implements Serializable {
    public int code;
    public int fmcode;
    public int f1code;
    public String message;
    public String devmsg;
    public String msgLevel;
    public T data;

    public BaseResponse2() {
    }

    public String toString() {
        return "BaseResponse{code=" + this.code + ", fmcode=" + this.fmcode + ", f1code=" + this.f1code + ", message='" + this.message + '\'' + ", devmsg='" + this.devmsg + '\'' + ", msgLevel='" + this.msgLevel + '\'' + ", data=" + this.data + '}';
    }
}
