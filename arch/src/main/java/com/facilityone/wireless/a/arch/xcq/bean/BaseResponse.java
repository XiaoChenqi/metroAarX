package com.facilityone.wireless.a.arch.xcq.bean;


import com.facilityone.wireless.a.arch.xcq.Constants.Constant;

/**
 * Description：网络返回数据基类,这个根据情况可以修改
 * Created by：Kyle
 * Date：2017/2/6
 */
public class BaseResponse<T> {

    //    {
//        "status": 200,  // 所有的返回都是200
//        "message": "",  // 返回提示信息，后台返回的都是定义错误码处标记的中文信息
//        "devmsg": "",   // 开发用错误信息
//        "data": {},
//        "f1code": "200000", //返回200000表示提交成功，提交失败情况下，返回失败code
//        "msgLevel": "info"  //info、warn、error
//    }

    public int code;
    public int fmcode;
    public int f1code;
    public String message;
    public String devmsg;
    public String msgLevel;
    public T data;

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", fmcode=" + fmcode +
                ", f1code=" + f1code +
                ", message='" + message + '\'' +
                ", devmsg='" + devmsg + '\'' +
                ", msgLevel='" + msgLevel + '\'' +
                ", data=" + data +
                '}';
    }



    public int getCode() {
        return fmcode;
    }

    public void setCode(int code) {
        this.fmcode = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public boolean isOk(){
        return fmcode == Constant.CODE_OK;
    }


}
