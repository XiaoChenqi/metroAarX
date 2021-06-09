package com.example.common.scan.Inventory;


import com.example.common.scan.tool.FMBaseQrcode;

/**
 *
 * 库存二维码
 *
 * @author flynn
 * @version v1.0 2018/9/7
 */
public class InventoryQrcode extends FMBaseQrcode {

    public static final String FM_QRCODE_FUNCTION_INVENTORY = "STOCK";

    public InventoryQrcode(String str) {
        super(str);
    }

    @Override
    public boolean isValid() {
        boolean res = super.isValid();
        if(res) {
            res = function != null && FM_QRCODE_FUNCTION_INVENTORY.equals(function);
        }
        return res;
    }
}
