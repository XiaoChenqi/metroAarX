package com.example.common.scan.patrol;


/**
 *
 * 点位二维码，用于点位巡视
 *
 */
public class PatrolSpotQrCode extends PatrolQrcode {

    public static final String FM_QRCODE_PATROL_FUNCTION_SUB_SPOT = "SPOT";

    private Long spotId;
    private String spotName;


    public PatrolSpotQrCode(String str) {
        super(str);
    }


    @Override
    protected void analysisExtendInfo() {
        if(isValid()) {   //合法的点位二维码扩展信息至少包含两个
            String tmpId = extendList.get(0);
            String tmpName = extendList.get(1);

            spotId = Long.valueOf(tmpId);   //解析点位信息
            spotName = tmpName;
        }

    }

    @Override
    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = false;
            if(function != null && FM_QRCODE_PATROL_FUNCTION_SUB_SPOT.equals(subFunction)) {
                if(extendList != null && extendList.size() >= 2) { //合法的点位二维码扩展信息至少包含两个
                    res = true;
                }
            }

        }
        return res;
    }

    public Long getSpotId() {
        return spotId;
    }

    public String getSpotName() {
        return spotName;
    }
}
