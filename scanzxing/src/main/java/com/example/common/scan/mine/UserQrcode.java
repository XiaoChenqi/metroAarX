package com.example.common.scan.mine;

import com.example.common.scan.tool.CommonQrcode;

/**
 *
 * 用户二维码
 *
 * 格式：
 * BASIC|EM|{emId}|{emType}|{name}|F-ONE
 *
 * emType:员工类型，Integer
 *  0 --- 总部；1---委外；2---线路；3---车站
 *
 * @author flynn
 * @version v1.0 2018/9/7
 */
public class UserQrcode extends CommonQrcode {

    public static final String FM_QRCODE_COMMON_FUNCTION_SUB_EMPLOYEE = "EM";

    protected Long employeeId;
    protected int type;
    protected String name;


    public UserQrcode(String str) {
        super(str);
    }


    @Override
    protected void analysisExtendInfo() {
        if(isValid()) {   //合法的点位二维码扩展信息至少包含两个
            String tmpId = extendList.get(0);
            String tmpType = extendList.get(1);
            String tmpName = extendList.get(2);

            employeeId = Long.valueOf(tmpId);   //解析站点信息
            type = Integer.valueOf(tmpType);
            name = tmpName;
        }

    }

    @Override
    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = false;
            if(function != null && FM_QRCODE_COMMON_FUNCTION_SUB_EMPLOYEE.equals(subFunction)) {
                if(extendList != null && extendList.size() >= 3) { //合法的用户二维码扩展信息至少包含三个
                    res = true;
                }
            }

        }
        return res;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
