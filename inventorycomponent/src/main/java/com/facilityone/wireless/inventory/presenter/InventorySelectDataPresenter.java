package com.facilityone.wireless.inventory.presenter;

import android.text.TextUtils;

import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.commonpresenter.CommonBasePresenter;
import com.facilityone.wireless.a.arch.ec.logon.UserUrl;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.PinyinUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.fragment.InventorySelectDataFragment;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.InventoryUrl;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.ProviderService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.model.SupervisorService;
import com.fm.tool.network.model.BaseResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/26.
 */

public class InventorySelectDataPresenter extends CommonBasePresenter<InventorySelectDataFragment> {

    /**
     * 数据过滤
     *
     * @param strFilter
     * @param selectDataBeanList
     */
    public void filter(int fromType,String strFilter, List<InventorySelectDataBean> selectDataBeanList) {
        if(selectDataBeanList == null) {
            getV().refreshView(null);
            return;
        }
        for (InventorySelectDataBean selectDataBean : selectDataBeanList) {
            selectDataBean.start = 0;
            selectDataBean.end = 0;
            selectDataBean.subStart = 0;
            selectDataBean.subEnd = 0;
        }
        if (TextUtils.isEmpty(strFilter)) {
            getV().refreshView(selectDataBeanList);
            return;
        }
        strFilter = strFilter.replace(" ","");
        strFilter = strFilter.toLowerCase();
        List<InventorySelectDataBean> temp = new ArrayList<>();
        for (InventorySelectDataBean selectDataBean : selectDataBeanList) {
            if (!TextUtils.isEmpty(selectDataBean.name) && selectDataBean.name.toLowerCase().contains(strFilter)) {
                temp.add(selectDataBean);

                int start = selectDataBean.name.toLowerCase().indexOf(strFilter);
                int end = start + strFilter.length();
                selectDataBean.start = start;
                selectDataBean.end = end;

                continue;
            }
            if(!TextUtils.isEmpty(selectDataBean.nameFirstLetters) && selectDataBean.nameFirstLetters.contains(strFilter)) {
                temp.add(selectDataBean);

                int start = selectDataBean.nameFirstLetters.indexOf(strFilter);
                int end = start + strFilter.length();
                selectDataBean.start = start;
                selectDataBean.end = end;

                continue;
            }
            if(!TextUtils.isEmpty(selectDataBean.namePinyin)) {
                String[] strArr = pinyinToStrArr(selectDataBean.namePinyin);
                int start = isMatch(strArr, strFilter);
                if(start != -1) {
                    temp.add(selectDataBean);
                    selectDataBean.start = start;
                    if(strArr[start].length() >= strFilter.length()) {
                        selectDataBean.end = start + 1;
                    }else {
                        selectDataBean.end = endIndex(strArr, strFilter.substring(strArr[start].length()), start + 1);
                    }
                    continue;
                }
            }
            if(fromType == InventoryConstant.SELECT_MATERIAL
                    || fromType == InventoryConstant.SELECT_MATERIAL_OUT
                    || fromType == InventoryConstant.SELECT_MATERIAL_MOVE
                    || fromType == InventoryConstant.SELECT_MATERIAL_RESERVE) {
                if(!TextUtils.isEmpty(selectDataBean.subStr) && selectDataBean.subStr.toLowerCase().contains(strFilter)) {
                    temp.add(selectDataBean);

                    int start = selectDataBean.subStr.toLowerCase().indexOf(strFilter);
                    int end = start + strFilter.length();
                    selectDataBean.subStart = start;
                    selectDataBean.subEnd = end;

                    continue;
                }
                if(!TextUtils.isEmpty(selectDataBean.subFirstLetters) && selectDataBean.subFirstLetters.contains(strFilter)) {
                    temp.add(selectDataBean);

                    int start = selectDataBean.subFirstLetters.toLowerCase().indexOf(strFilter);
                    int end = start + strFilter.length();
                    selectDataBean.subStart = start;
                    selectDataBean.subEnd = end;

                    continue;
                }
                if(!TextUtils.isEmpty(selectDataBean.subPinyin)) {
                    String[] strArr = pinyinToStrArr(selectDataBean.subPinyin);
                    int start = isMatch(strArr, strFilter);
                    if(start != -1) {
                        temp.add(selectDataBean);
                        selectDataBean.subStart = start;
                        if(strArr[start].length() >= strFilter.length()) {
                            selectDataBean.subEnd = start + 1;
                        }else {
                            selectDataBean.subEnd = endIndex(strArr, strFilter.substring(strArr[start].length()), start + 1);
                        }
                        continue;
                    }
                }
            }
        }

        getV().refreshView(temp);
    }

    /**
     * 联网获取仓库列表数据
     *
     * @param page
     */
    public void getStorageListData(Page page, final long employeeId) {
        StorageService.StorageListRequest request = new StorageService.StorageListRequest();
        request.page = page;
        if (employeeId != -1) {
            request.employeeId = employeeId;
        }

        OkGo.<BaseResponse<StorageService.StorageListBean>>post(FM.getApiHost() + InventoryUrl.STORAGE_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<StorageService.StorageListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        StorageService.StorageListBean data = response.body().data;
                        List<InventorySelectDataBean> totalSelectDataList = getV().getTotalSelectDataList();
                        if (data != null && data.contents != null && data.contents.size() > 0) {
                            for (int i = 0; i < data.contents.size(); i++) {
                                StorageService.Storage storage = data.contents.get(i);
                                InventorySelectDataBean selectDataBean = new InventorySelectDataBean();
                                selectDataBean.id = storage.warehouseId;
                                selectDataBean.name = storage.name;
                                selectDataBean.namePinyin = PinyinUtils.ccs2Pinyin(selectDataBean.name);
                                selectDataBean.nameFirstLetters = PinyinUtils.getPinyinFirstLetters(selectDataBean.name);
                                selectDataBean.target = storage;
                                selectDataBean.spareParts=storage.spareParts;
                                totalSelectDataList.add(selectDataBean);
                            }
                        }

                        if (data != null && data.page != null && data.page.haveNext()) {
                            getStorageListData(data.page.nextPage(), employeeId);
                        } else if (totalSelectDataList != null && totalSelectDataList.size() > 0) {
                            getV().refreshView(totalSelectDataList);
                        } else {
                            getV().getListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        super.onError(response);
                        getV().getListDataError();
                    }
                });

    }


    /**
     * 联网获取供应商列表数据
     *
     * @param page
     * @param inventoryId
     */
    public void getProviderListData(final Page page, final long inventoryId) {
        ProviderService.ProviderListRequest request = new ProviderService.ProviderListRequest();
        request.page = page;
        request.inventoryId = inventoryId;

        OkGo.<BaseResponse<ProviderService.ProviderListBean>>post(FM.getApiHost() + InventoryUrl.PROVIDER_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<ProviderService.ProviderListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<ProviderService.ProviderListBean>> response) {
                        ProviderService.ProviderListBean data = response.body().data;
                        List<InventorySelectDataBean> totalSelectDataList = getV().getTotalSelectDataList();
                        if (data != null && data.contents != null && data.contents.size() > 0) {
                            for (int i = 0; i < data.contents.size(); i++) {
                                ProviderService.Provider provider = data.contents.get(i);
                                InventorySelectDataBean selectDataBean = new InventorySelectDataBean();
                                selectDataBean.id = provider.providerId;
                                selectDataBean.name = provider.name;
                                selectDataBean.namePinyin = PinyinUtils.ccs2Pinyin(selectDataBean.name);
                                selectDataBean.nameFirstLetters = PinyinUtils.getPinyinFirstLetters(selectDataBean.name);
                                selectDataBean.target = provider;
                                totalSelectDataList.add(selectDataBean);
                            }
                        }

                        if (data != null && data.page != null && data.page.haveNext()) {
                            getProviderListData(data.page.nextPage(), inventoryId);
                        } else if (totalSelectDataList != null && totalSelectDataList.size() > 0) {
                            getV().refreshView(totalSelectDataList);
                        } else {
                            getV().getListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<ProviderService.ProviderListBean>> response) {
                        super.onError(response);
                        getV().getListDataError();
                    }
                });
    }

    /**
     * 联网获取物料数据列表
     *
     * @param page
     * @param warehouseId
     * @param materialCondition
     */
    public void getMaterialListData(Page page, final long warehouseId, final MaterialService.MaterialCondition materialCondition) {
        MaterialService.MaterialListRequest request = new MaterialService.MaterialListRequest();
        request.page = page;
        request.condition = materialCondition;
        request.warehouseId = warehouseId;

        OkGo.<BaseResponse<MaterialService.MaterialListBean>>post(FM.getApiHost() + InventoryUrl.MATERIAL_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<MaterialService.MaterialListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<MaterialService.MaterialListBean>> response) {
                        MaterialService.MaterialListBean data = response.body().data;
                        List<InventorySelectDataBean> totalSelectDataList = getV().getTotalSelectDataList();
                        if (data != null && data.contents != null && data.contents.size() > 0) {
                            for (int i = 0; i < data.contents.size(); i++) {
                                MaterialService.Material material = data.contents.get(i);
                                InventorySelectDataBean selectDataBean = new InventorySelectDataBean();
                                selectDataBean.id = material.inventoryId;
                                selectDataBean.name = material.materialName;
                                selectDataBean.namePinyin = PinyinUtils.ccs2Pinyin(selectDataBean.name);
                                selectDataBean.nameFirstLetters = PinyinUtils.getPinyinFirstLetters(selectDataBean.name);
                                String subStr = StringUtils.formatString(material.materialBrand)
                                        + (TextUtils.isEmpty(material.materialModel) ? "" : "("+StringUtils.formatString(material.materialModel)+")");
                                selectDataBean.subStr = subStr;
                                selectDataBean.subPinyin = PinyinUtils.ccs2Pinyin(selectDataBean.subStr);
                                selectDataBean.subFirstLetters = PinyinUtils.getPinyinFirstLetters(selectDataBean.subStr);
                                selectDataBean.target = material;
                                totalSelectDataList.add(selectDataBean);
                            }
                        }

                        if (data != null && data.page != null && data.page.haveNext()) {
                            getMaterialListData(data.page.nextPage(), warehouseId, materialCondition);
                        } else if (totalSelectDataList != null && totalSelectDataList.size() > 0) {
                            getV().refreshView(totalSelectDataList);
                        } else {
                            getV().getListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<MaterialService.MaterialListBean>> response) {
                        super.onError(response);
                        getV().getListDataError();
                    }
                });
    }

    /**
     * 联网获取领料人（获取当前项目下的员工列表）
     */
    public void getReceivingPersonList() {
        String request = "{}";
        OkGo.<BaseResponse<List<UserService.UserInfoBean>>>post(FM.getApiHost() + UserUrl.USER_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<List<UserService.UserInfoBean>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<List<UserService.UserInfoBean>>> response) {
                        List<UserService.UserInfoBean> data = response.body().data;
                        List<InventorySelectDataBean> totalSelectDataList = getV().getTotalSelectDataList();
                        if (data != null && data.size() > 0) {
                            for (int i = 0; i < data.size(); i++) {
                                UserService.UserInfoBean userInfoBean = data.get(i);
                                InventorySelectDataBean selectDataBean = new InventorySelectDataBean();
                                selectDataBean.id = userInfoBean.emId;
                                selectDataBean.name = userInfoBean.name;
                                selectDataBean.namePinyin = PinyinUtils.ccs2Pinyin(selectDataBean.name);
                                selectDataBean.nameFirstLetters = PinyinUtils.getPinyinFirstLetters(selectDataBean.name);
                                selectDataBean.target = userInfoBean;
                                totalSelectDataList.add(selectDataBean);
                            }
                        }


                        if (totalSelectDataList != null && totalSelectDataList.size() > 0) {
                            getV().refreshView(totalSelectDataList);
                        } else {
                            getV().getListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<UserService.UserInfoBean>>> response) {
                        super.onError(response);
                        getV().getListDataError();
                    }
                });
    }

    /**
     * 联网获取主管数据列表
     *
     * @param laborerId
     */
    public void getSupervisorListData(long laborerId) {
        laborerId = laborerId == -1 ? FM.getEmId() : laborerId;
        String request = "{\"laborerId\":" + laborerId + "}";
        OkGo.<BaseResponse<List<SupervisorService.Supervisor>>>post(FM.getApiHost() + InventoryUrl.SUPERVISOR_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(request)
                .execute(new FMJsonCallback<BaseResponse<List<SupervisorService.Supervisor>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<List<SupervisorService.Supervisor>>> response) {
                        List<SupervisorService.Supervisor> data = response.body().data;
                        List<InventorySelectDataBean> totalSelectDataList = getV().getTotalSelectDataList();
                        if (data != null && data.size() > 0) {
                            for (int i = 0; i < data.size(); i++) {
                                SupervisorService.Supervisor supervisor = data.get(i);
                                InventorySelectDataBean selectDataBean = new InventorySelectDataBean();
                                selectDataBean.id = supervisor.supervisorId;
                                selectDataBean.name = supervisor.name;
                                selectDataBean.namePinyin = PinyinUtils.ccs2Pinyin(selectDataBean.name);
                                selectDataBean.nameFirstLetters = PinyinUtils.getPinyinFirstLetters(selectDataBean.name);
                                selectDataBean.target = supervisor;
                                totalSelectDataList.add(selectDataBean);
                            }
                        }

                        if (totalSelectDataList != null && totalSelectDataList.size() > 0) {
                            getV().refreshView(totalSelectDataList);
                        } else {
                            getV().getListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<SupervisorService.Supervisor>>> response) {
                        super.onError(response);
                        getV().getListDataError();
                    }
                });
    }

    /**
     * 联网获取指定仓库id的仓库管理员
     *
     * @param page
     * @param warehouseId
     */
    public void getStorageAdministrator(Page page, final long warehouseId) {
        StorageService.StorageListRequest request = new StorageService.StorageListRequest();
        request.page = page;
        OkGo.<BaseResponse<StorageService.StorageListBean>>post(FM.getApiHost() + InventoryUrl.STORAGE_LIST_URL)
                .isSpliceUrl(true)
                .tag(getV())
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<StorageService.StorageListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        boolean isFind = false;
                        StorageService.StorageListBean data = response.body().data;
                        List<InventorySelectDataBean> totalSelectDataList = getV().getTotalSelectDataList();
                        List<StorageService.Administrator> administratorList = null;
                        if (data != null && data.contents != null && data.contents.size() > 0) {
                            for (int i = 0; i < data.contents.size(); i++) {
                                StorageService.Storage storage = data.contents.get(i);
                                if (storage.warehouseId.equals(warehouseId)) {
                                    if (storage.administrator != null) {
                                        administratorList = new ArrayList<StorageService.Administrator>();
                                        administratorList.addAll(storage.administrator);
                                    }
                                    isFind = true;
                                    break;
                                }
                            }
                        }

                        if (isFind && administratorList != null && administratorList.size() > 0) {
                            for (int i = 0; i < administratorList.size(); i++) {
                                StorageService.Administrator administrator = administratorList.get(i);
                                InventorySelectDataBean selectDataBean = new InventorySelectDataBean();
                                selectDataBean.id = administrator.administratorId;
                                selectDataBean.name = administrator.name;
                                selectDataBean.namePinyin = PinyinUtils.ccs2Pinyin(selectDataBean.name);
                                selectDataBean.nameFirstLetters = PinyinUtils.getPinyinFirstLetters(selectDataBean.name);
                                List<StorageService.Administrator> list = new ArrayList<>();
                                list.add(administrator);
                                selectDataBean.target = list;
                                totalSelectDataList.add(selectDataBean);
                            }
                        }

                        if (data != null && data.page != null && data.page.haveNext() && !isFind) {
                            getStorageAdministrator(data.page.nextPage(), warehouseId);
                        } else if (totalSelectDataList != null && totalSelectDataList.size() > 0) {
                            getV().refreshView(totalSelectDataList);
                        } else {
                            getV().getListDataError();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<StorageService.StorageListBean>> response) {
                        super.onError(response);
                        getV().getListDataError();
                    }
                });

    }
}
