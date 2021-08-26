package com.facilityone.wireless.inventory.presenter;

import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.fragment.InventoryCreateFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/26.
 */

public class InventoryCreatePresenter extends CommonBasePresenter<InventoryCreateFragment> {
    /**
     * 联网判断当前仓库中该物资是否已经存在
     *
     * @param warehouseId
     * @param code
     */
    public void MaterialExist(long warehouseId, String code) {
        getV().showLoading();
        String request = "{\"warehouseId\":" + warehouseId + ",\"code\":\"" + code + "\"}";
        OkGo.<BaseResponse<Integer>>post(FM.getApiHost() + InventoryUrl.MATERIAL_EXIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<Integer>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Integer>> response) {
                        getV().dismissLoading();
                        Integer data = response.body().data;
                        if (data != null) {
                            switch (data) {
                                case InventoryConstant.NEVER_EXIST:
                                    getV().getMaterialInfo();
                                    break;
                                case InventoryConstant.THIS_EXIST:
                                    ToastUtils.showShort(R.string.inventory_exist_same_warehouse_tip);
                                    break;
                                case InventoryConstant.OTHER_EXIST:
                                    new FMWarnDialogBuilder(getV().getContext())
                                            .setTitle(R.string.inventory_create_title)
                                            .setTip(R.string.inventory_exist_other_warehouse_tip)
                                            .setSure(R.string.inventory_sure)
                                            .setCancel(R.string.inventory_cancel)
                                            .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                                                @Override
                                                public void onClick(QMUIDialog dialog, View view) {
                                                    dialog.dismiss();
                                                    getV().getMaterialInfo();
                                                }
                                            })
                                            .show();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<Integer>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    @Override
    public void uploadFileSuccess(List<String> ids, int type) {
        MaterialService.MaterialCreateRequst materialCreateRequst = getV().getMaterialCreateRequst();
        if (materialCreateRequst.pictures != null) {
            materialCreateRequst.pictures.addAll(ids);
        } else {
            materialCreateRequst.pictures = ids;
        }
    }

    @Override
    public void uploadFileFinish(int type) {
        saveMaterialInfo();
    }

    public void saveMaterialInfo() {
        getV().showLoading();
        MaterialService.MaterialCreateRequst requst = getV().getMaterialCreateRequst();

        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + InventoryUrl.MATERIAL_CREATE_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(requst))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.inventory_operate_success);
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
}
