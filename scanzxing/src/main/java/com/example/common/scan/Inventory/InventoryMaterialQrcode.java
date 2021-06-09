package com.example.common.scan.Inventory;

/**
 * @author flynn
 * @version v1.0 2018/9/7
 */
public class InventoryMaterialQrcode extends InventoryQrcode {

    public static final String FM_QRCODE_PATROL_FUNCTION_SUB_MATERIAL = "MATERIAL";

    private Long warehouseId;
    private String materialCode;


    public InventoryMaterialQrcode(String str) {
        super(str);
    }


    @Override
    protected void analysisExtendInfo() {
        if(isValid()) {
            String tmpId = extendList.get(0);
            String tmpCode = extendList.get(1);

            warehouseId = Long.valueOf(tmpId);   //解析点位信息
            materialCode = tmpCode;
        }

    }

    @Override
    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = false;
            if(function != null && FM_QRCODE_PATROL_FUNCTION_SUB_MATERIAL.equals(subFunction)) {
                if(extendList != null && extendList.size() >= 2) { //合法的点位二维码扩展信息至少包含两个
                    res = true;
                }
            }

        }
        return res;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public String getMaterialCode() {
        return materialCode;
    }
}
