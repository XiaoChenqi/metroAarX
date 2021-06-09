package com.example.common.scan.tool;


import java.util.ArrayList;
import java.util.List;

/**
 * 二维码基类
 *
 * @author Flynn.Yang
 * @version v2.0，2018/09/07
 * @see
 * @since Shang
 *
 * @desc 二维码 编码格式：{FUNCTION}|{SUB_FUNCTION}|...|{COMPANY_NAME}
 */

public class FMBaseQrcode {

    public static final String FM_BASE_QRCODE_SEPERATOR = "\\|";  //分隔符

    protected String qrcode;      //二维码源码

    protected String function;     // 主模块标识
    protected String subFunction;  // 子模块标识
    protected List<String> extendList;    //扩展信息列表
    protected String product;     //公司或者产品名称

    private  boolean isValid;


    public FMBaseQrcode(String str){
        qrcode = str;
        analysis();
    }

    public FMBaseQrcode(FMBaseQrcode qr) {
        qrcode = qr.qrcode;
        function = qr.function;
        subFunction = qr.subFunction;
        extendList = qr.extendList;
        product = qr.product;

        isValid = qr.isValid;
        analysisExtendInfo();
    }

    public boolean isValid() {
        return isValid;
    }

    //获取源码
    public String getQrcode() {
        return qrcode;
    }

    //获取主模块标识
    public String getFunction() {
        return function;
    }

    //获取子模块标识
    public String getSubFunction() {
        return subFunction;
    }

    //获取扩展信息列表
    public List<String> getExtendList() {
        return extendList;
    }


    //获取产品标识
    public String getProduct() {
        return product;
    }

    //子类需要根据各自需求重写本方法，实现扩展信息解析
    protected void analysisExtendInfo() {}


    //数据解析
    private void analysis() {
        if(qrcode != null && !"".equals(qrcode)) {
            String[] array =  qrcode.split(FM_BASE_QRCODE_SEPERATOR);
            if(array != null && array.length >= 4) { //一个合法的二维码最低包含四组信息
                int count = array.length;
                isValid = true;

                function = array[0];
                subFunction = array[1];
                product = array[count - 1];

                extendList = new ArrayList<>();
                for(int index=0;index<count-3;index++) {
                    String tmp = array[index + 2];
                    extendList.add(tmp);
                }

                analysisExtendInfo();   //回调，解析扩展信息
            }
        }
    }
}
