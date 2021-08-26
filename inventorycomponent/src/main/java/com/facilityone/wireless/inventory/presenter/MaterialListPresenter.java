package com.facilityone.wireless.inventory.presenter;

import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BasePresenter;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.fragment.MaterialListFragment;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/12/11.
 */

public class MaterialListPresenter extends BasePresenter<MaterialListFragment> {

    /**
     * 联网获取特定仓库下的物资列表数据
     *
     * @param warehouseId
     * @param condition
     * @param page
     * @param refresh
     */
    public void getMaterialListData(long warehouseId, MaterialService.MaterialCondition condition, Page page, final boolean refresh) {
        MaterialService.MaterialListRequest request = new MaterialService.MaterialListRequest();
        request.warehouseId = warehouseId;
        request.condition = condition;
        request.page = page;

        OkGo.<BaseResponse<MaterialService.MaterialListBean>>post(FM.getApiHost() + InventoryUrl.MATERIAL_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<MaterialService.MaterialListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<MaterialService.MaterialListBean>> response) {
                        MaterialService.MaterialListBean data = response.body().data;
                        if (data != null && data.contents != null) {
                            getV().getMaterialListDataSuccess(data.contents, data.page, refresh);
                        } else {
                            getV().getMaterialListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<MaterialService.MaterialListBean>> response) {
                        super.onError(response);
                        ToastUtils.showShort(R.string.inventory_operate_fail);
                        getV().getMaterialListDataError();
                    }
                });
    }

    /**
     * 获取物资查询数量类型
     * @param context
     * @return
     */
    public List<AttachmentBean> getMaterialType(Context context) {
        List<AttachmentBean> typeList = new ArrayList<>();
        String[] stringArray = context.getResources().getStringArray(R.array.material_type);
        if(stringArray != null && stringArray.length > 0) {
            for (int i = 0; i < stringArray.length; i++) {
                AttachmentBean attachmentBean = new AttachmentBean();
                attachmentBean.value = i;
                attachmentBean.name = stringArray[i];
                attachmentBean.check = false;
                typeList.add(attachmentBean);
            }
        }

        return typeList;
    }

}
