package com.example.common.scan.patrol;


import com.example.common.scan.tool.MixQrcode;

/**
 *
 * 点位设备混合二维码
 *
 * @author flynn
 * @version v1.0 2018/9/7
 */
public class MixSpotEquipmentQrcode extends MixQrcode {

    public static final String FM_QRCODE_FUNCTION_SUB_SPOT_EQUIPMENT = "SPOT_EQUIPMENT";

    protected Long spotId;
    protected Long equipmentId;

    public MixSpotEquipmentQrcode(String str) {
        super(str);
    }

    @Override
    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = false;
            if(subFunction != null && FM_QRCODE_FUNCTION_SUB_SPOT_EQUIPMENT.equals(subFunction)
                    && extendList != null && extendList.size() >= 2) {
                res = true;
            }
        }
        return res;
    }

    @Override
    protected void analysisExtendInfo() {
        if(isValid()) {
            String tmpSpotId = extendList.get(0);
            String tmpEquipmentId = extendList.get(1);

            spotId = Long.valueOf(tmpSpotId);
            equipmentId = Long.valueOf(tmpEquipmentId);
        }
    }

    public Long getSpotId() {
        return spotId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }
}
