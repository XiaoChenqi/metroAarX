package com.example.common.scan.patrol;

/**
 *
 * 站点二维码，用于点位巡检
 *
 */
public class PatrolBuildingQrcode extends PatrolQrcode {
    public static final String FM_QRCODE_PATROL_FUNCTION_SUB_SPOT = "BUILDING";

    protected Long buildingId;
    protected String buildingName;


    public PatrolBuildingQrcode(String str) {
        super(str);
    }


    @Override
    protected void analysisExtendInfo() {
        if(isValid()) {   //合法的点位二维码扩展信息至少包含两个
            String tmpId = extendList.get(0);
            String tmpName = extendList.get(1);

            buildingId = Long.valueOf(tmpId);   //解析站点信息
            buildingName = tmpName;
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

    public Long getBuildingId() {
        return buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }
}
