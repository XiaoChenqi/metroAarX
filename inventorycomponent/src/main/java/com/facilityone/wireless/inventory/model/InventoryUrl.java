package com.facilityone.wireless.inventory.model;

/**
 * Created by peter.peng on 2018/11/26.
 */

public interface InventoryUrl {

    //获取仓库列表数据
    String STORAGE_LIST_URL = "/m/v1/stock/warehouses";
    //获取仓库列表数据
    String WAREHOUSE_LIST_URL = "/m/v1/stock/warehouse/query";
    //获取供应商列表数据
    String PROVIDER_LIST_URL = "/m/v1/stock/provider";
    //判断物资是否存在
    String MATERIAL_EXIST_URL = "/m/v1/stock/inventory/exist";
    //新建物资
    String MATERIAL_CREATE_URL = "/m/v1/stock/inventory/create";
    //获取物资列表数据
    String MATERIAL_LIST_URL = "/m/v2/stock/inventorys";
    //通过id获取物资详情
    String MATERIAL_INFO_ID_URL = "/m/v1/stock/inventory/detail";
    //通过编码（二维码）获取物资详情
    String MATERIAL_INFO_QRCODE_URL = "/m/v1/stock/inventory/qrcodeinfo";
    //入库
    String INVENTORY_IN_URL = "/m/v1/stock/storage";
    //获取预定记录列表
    String RESERVE_RECORD_LIST_URL = "/m/v1/stock/reservations/list";
    //获取预定记录详情
    String RESERVE_RECORD_INFO_URL = "/m/v2/stock/reservations/detail";
    //批次列表
    String BATCH_LIST_URL = "/m/v2/stock/batch/list";
    //出库、移库
    String INVENTORY_OUT_URL = "/m/v1/stock/delivery";
    //库存预定审核、取消出库
    String INVENTORY_APPROVAL_URL = "/m/v1/stock/reservation/approval";
    //获取主管列表
    String SUPERVISOR_LIST_URL = "/m/v1/workteams/supervisor";
    //物资盘点
    String INVENTORY_CHECK_URL = "/m/v1/stock/check";
    //物资预定
    String INVENTORY_RESERVE_URL = "/m/v2/stock/reservations/reserve";
    //获取物资记录通过id
    String MATERIAL_RECORD_BY_ID_URL = "/m/v1/stock/inventory/record";
    //获取物资记录通过编码
    String MATERIAL_RECORD_BY_CODE_URL = "/m/v1/stock/inventory/record/code";
    //修改预订单人员
    String EDIT_RESERVATION_PERSON = "/m/v1/stock/reservation/person";



}
