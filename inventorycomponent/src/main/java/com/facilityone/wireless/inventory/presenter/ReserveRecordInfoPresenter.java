package com.facilityone.wireless.inventory.presenter;

import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMBottomInputSheetBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.fragment.ReserveRecordInfoFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.ReserveService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by peter.peng on 2018/12/4.
 */

public class ReserveRecordInfoPresenter extends InventoryCommonPresenter<ReserveRecordInfoFragment> {
    /**
     * 当更多按钮点击时
     */
    public void onMoreMenuClick(final long activityId) {
        List<String> menu = new ArrayList<>();
        menu.add(getV().getString(R.string.inventory_out_title));
        menu.add(getV().getString(R.string.inventory_cancel_out));
        menu.add(getV().getString(R.string.inventory_cancel));

        new BottomTextListSheetBuilder(getV().getContext())
                .addArrayItem(menu)
                .setOnSheetItemClickListener(new BottomTextListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        if (tag.equals(getV().getString(R.string.inventory_out_title))) {
                            //判断界面输入值是否有效
                            if (getV().isValid()) {
                                MaterialService.MaterialOutRequest request = getV().getRequest();
                                InventoryMaterialOut(request);
                            }
                        } else if (tag.equals(getV().getString(R.string.inventory_cancel_out))) {
                            ShowMaterialCancelOutView(activityId,InventoryConstant.INVENTORY_APPROVAL_CANCEL_OUT);
                        }
                    }

                })
                .build()
                .show();
    }

    /**
     * 显示取消出库、取消预定原因输入框
     */
    public void ShowMaterialCancelOutView(final long activityId, final int type) {
        FMBottomInputSheetBuilder builder = new FMBottomInputSheetBuilder(getV().getContext());
        builder.setOnSaveInputListener(new FMBottomInputSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                InventoryAproval(activityId,input,type);
            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
            }
        });
        QMUIBottomSheet dialog = builder.build();
        builder.getSingleBtn().setVisibility(View.VISIBLE);
        String title = "";
        String tip = "";
        switch (type) {
            case InventoryConstant.INVENTORY_APPROVAL_CANCEL_OUT :
                title = getV().getString(R.string.inventory_cancel_out);
                tip = getV().getString(R.string.inventory_cancel_out_tip);
                break;
            case InventoryConstant.INVENTORY_APPROVAL_CANCEL_BOOK:
                title = getV().getString(R.string.inventory_reserve_cancel);
                tip = getV().getString(R.string.inventory_cancel_reserve_tip);
                break;
        }
        builder.setTitle(title);
        builder.setDescHint(R.string.inventory_input_reason);
        builder.getSingleBtn().setBackgroundResource(R.drawable.btn_common_bg_selector);
        builder.setSingleNeedInput(true);
        builder.setShowTip(tip);
        builder.setBtnText(R.string.inventory_sure);
        dialog.show();
    }

    /**
     * 预定单审核，取消出库
     * @param desc
     * @param type
     */
    private void InventoryAproval(long activityId, String desc, int type) {
        getV().showLoading();
        MaterialService.InventoryApprovalRequest request = new MaterialService.InventoryApprovalRequest();
        request.activityId = activityId;
        request.type = type;
        request.desc = desc;

        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + InventoryUrl.INVENTORY_APPROVAL_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_success);
                        getV().setFragmentResult(ISupportFragment.RESULT_OK,null);
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                    }
                });
    }

    /**
     * 物资出库成功后回调
     */
    @Override
    public void InventoryMaterialOutSuccess() {
        super.InventoryMaterialOutSuccess();
        getV().setFragmentResult(ISupportFragment.RESULT_OK,null);
        getV().pop();
    }

    /**
     * 联网获取预定详情信息
     *
     * @param activityid
     */
    public void getReserveRecordInfo(long activityid) {
        getV().showLoading();
        String request = "{\"requestId\":" + activityid + "}";
        OkGo.<BaseResponse<ReserveService.ReserveRecordInfoBean>>post(FM.getApiHost() + InventoryUrl.RESERVE_RECORD_INFO_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<ReserveService.ReserveRecordInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<ReserveService.ReserveRecordInfoBean>> response) {
                        getV().dismissLoading();
                        ReserveService.ReserveRecordInfoBean data = response.body().data;
                        if (data != null) {
                            getV().getReserveRecordInfoSuccess(data);
                        } else {
                            getV().getReserveRecordInfoError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<ReserveService.ReserveRecordInfoBean>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        getV().getReserveRecordInfoError();
                    }
                });
    }

    public List<MaterialService.MaterialInfo> MaterialList2MaterInfoList(List<MaterialService.ReserveMaterial> materialList) {
        List<MaterialService.MaterialInfo> materialInfoList = new ArrayList<>();
        if (materialList != null) {
            for (MaterialService.ReserveMaterial material : materialList) {
                MaterialService.MaterialInfo materialInfo = Material2MaterInfo(material);
                materialInfoList.add(materialInfo);
            }
        }

        return materialInfoList;
    }

    public MaterialService.MaterialInfo Material2MaterInfo(MaterialService.ReserveMaterial material) {
        MaterialService.MaterialInfo materialInfo = new MaterialService.MaterialInfo();
        materialInfo.inventoryId = material.inventoryId;
        materialInfo.code = material.materialCode;
        materialInfo.name = material.materialName;
        materialInfo.brand = material.materialBrand;
        materialInfo.model = material.materialModel;
        materialInfo.unit = material.materialUnit;
        materialInfo.cost = material.cost;
        materialInfo.amount = material.amount;
        materialInfo.bookAmount = material.bookAmount;
        materialInfo.receiveAmount = material.receiveAmount;
        return materialInfo;
    }

    /**
     * 显示库存审核输入框
     */
    public void ShowInventoryApprovalView(final long activityId) {
        FMBottomInputSheetBuilder builder = new FMBottomInputSheetBuilder(getV().getContext());
        builder.setOnSaveInputListener(new FMBottomInputSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();

            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                InventoryAproval(activityId,input,InventoryConstant.INVENTORY_APPROVAL_NOT_PASS);
            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                InventoryAproval(activityId,input,InventoryConstant.INVENTORY_APPROVAL_PASS);
            }
        });
        QMUIBottomSheet dialog = builder.build();
        builder.getLLTwoBtn().setVisibility(View.VISIBLE);
        builder.setTitle(R.string.inventory_audit_book);
        builder.setDescHint(R.string.inventory_input_reason);
        builder.getRightBtn().setBackgroundResource(R.drawable.btn_common_bg_selector);
        builder.setLeftBtnText(R.string.inventory_not_pass);
        builder.setRightBtnText(R.string.inventory_pass);
        builder.setTwoBtnLeftInput(true);
        builder.setTwoBtnRightInput(false);
        builder.setShowTip(getV().getString(R.string.inventory_input_reason));
        dialog.show();
    }

    public void editReservationPerson(long activityId,long administrator,long reservePerson,long supervisor) {
        getV().showLoading();
        ReserveService.EditReservationPersonRequest request = new ReserveService.EditReservationPersonRequest();
        request.activityId = activityId;
        request.administrator = administrator;
        request.supervisor = supervisor;
        request.reservePerson = reservePerson;
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + InventoryUrl.EDIT_RESERVATION_PERSON)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_success);
                        getV().setFragmentResult(ISupportFragment.RESULT_OK,null);
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }
}
