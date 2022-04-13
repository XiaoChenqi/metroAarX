package com.facilityone.wireless.workorder.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMBottomInputSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.fragment.WorkOrderUtils;
import com.facilityone.wireless.workorder.fragment.WorkorderApprovalFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderDispatchFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderInfoFragment;
import com.facilityone.wireless.workorder.module.WorkorderConstant;
import com.facilityone.wireless.workorder.module.WorkorderLaborerService;
import com.facilityone.wireless.workorder.module.WorkorderOptService;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.module.WorkorderUrl;
import com.fm.tool.network.model.BaseResponse;
import com.luojilab.component.componentlib.router.Router;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import razerdp.basepopup.BasePopupWindow;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/7/11 上午10:11
 */
public class WorkorderInfoPresenter extends BaseWorkOrderPresenter<WorkorderInfoFragment> {
    private static String TAG = "Karelie";
    private boolean taskStatus = false;
    private boolean needSample = false;
    private static final int CODE_FOR_LEDGER = 6002;


    @Override
    public void getWorkorderInfoSuccess(Long woId, WorkorderService.WorkorderInfoBean data) {
        super.getWorkorderInfoSuccess(woId, data);
        if (data != null) {
//            if (data.workOrderLaborers != null && data.workOrderLaborers.size() > 0) {
//                boolean laborer = processingLaborer(data.workOrderLaborers, getV().getStatus(), data.status);
//                if (!laborer) {
////                    getV().setRefreshStatus(-1);
//                    getV().setRefreshStatus(data.status);
//                }
//                getV().setLaborer(laborer);

//            }
            if (data.needSample != null) {
                needSample = data.needSample;
            }
            getV().refreshBasicInfoUI(data);
            //请求物料
            getWorkorderReserveRecordList(woId);
            getV().refreshHistoryUI(data);
            getV().refreshLaborerUI(data);
            getV().refreshEquipmentUI(data);
            getV().refreshToolsAndBplUI(data);
            getV().refreshPayment(data);
            getV().refreshFaultObject(data);
            getV().getData(data);
        } else {
            getV().refreshError();
        }
    }

    @Override
    public void getWorkorderInfoError() {
        super.getWorkorderInfoError();
        getV().refreshError();
    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/8/25 10:40
     * @Description: 获取最后一次签到记录
     */
    public void getLastAttendance(LocationBean workInfoLocation) {
        getV().showLoading();
        String json = "{}";
        OkGo.<BaseResponse<WorkorderService.WorkorderAttendanceResp>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_ATTENDANCE_LAST)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(json)
                .execute(new FMJsonCallback<BaseResponse<WorkorderService.WorkorderAttendanceResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<WorkorderService.WorkorderAttendanceResp>> response) {
                        getV().dismissLoading();
                        WorkorderService.WorkorderAttendanceResp data = response.body().data;
                        if (data != null) {
                            //判空
                            if (isLocationNull(data.location, workInfoLocation)) {
                                if (data.location.siteId != null && workInfoLocation.siteId != null) {

                                    if (data.buildingIds != null && workInfoLocation.buildingId != null) {
//                                        workInfoLocation.buildingId.equals(data.location.buildingId)
                                        if (workInfoLocation.siteId.equals(data.location.siteId) && isInLocationList(workInfoLocation.buildingId, data)) {
                                            getV().canOpt(true, true);
                                        } else {
                                            getV().canOpt(false, true);
//
                                        }
                                    } else {
                                        getV().canOpt(false, true);
                                    }

                                } else {
                                    getV().canOpt(false, true);
                                }


                            } else {
                                getV().canOpt(false, false);
                            }
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.WorkorderAttendanceResp>> response) {
                        super.onError(response);
                        getV().dismissLoading();
                        getWorkorderInfoError();
                    }
                });
    }

    /**
     * 获取工单信息
     *
     * @param woId
     */
//    public void getWorkorderInfo(final Long woId) {
//        String json = "{\"woId\":" + woId + "}";
//        OkGo.<BaseResponse<WorkorderService.WorkorderInfoBean>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_INFO_URL)
//                .tag(getV())
//                .isSpliceUrl(true)
//                .upJson(json)
//                .execute(new FMJsonCallback<BaseResponse<WorkorderService.WorkorderInfoBean>>() {
//                    @Override
//                    public void onSuccess(Response<BaseResponse<WorkorderService.WorkorderInfoBean>> response) {
//                        WorkorderService.WorkorderInfoBean data = response.body().data;
//                        if (data != null) {
//                            if (data.workOrderLaborers != null && data.workOrderLaborers.size() > 0) {
//                                boolean laborer = processingLaborer(data.workOrderLaborers, getV().getStatus(), data.status);
//                                if (!laborer) {
//                                    getV().setRefreshStatus(-1);
//                                }
//                                getV().setLaborer(laborer);
//                            }
//                            getV().refreshBasicInfoUI(data);
//                            //请求物料
//                            getWorkorderReserveRecordList(woId);
//                            getV().refreshHistoryUI(data);
//                            getV().refreshLaborerUI(data);
//                            getV().refreshEquipmentUI(data);
//                            getV().refreshToolsAndBplUI(data);
//                            getV().refreshPayment(data);
//                        } else {
//                            getV().refreshError();
//                        }
//                        getV().dismissLoading();
//                    }
//
//                    @Override
//                    public void onError(Response<BaseResponse<WorkorderService.WorkorderInfoBean>> response) {
//                        super.onError(response);
//                        getV().refreshError();
//                    }
//                });
//    }

    /**
     * 加工一下执行人是否可操作
     *
     * @param workOrderLaborers 执行人列表
     * @param status            用于排除查询页面
     * @param integer           工单状态
     * @return
     */
    public boolean processingLaborer(List<WorkorderLaborerService.WorkorderLaborerBean> workOrderLaborers, int status, Integer integer) {
        boolean laborerHave = false;//当前工单是否属于登录用户
        for (WorkorderLaborerService.WorkorderLaborerBean workOrderLaborer : workOrderLaborers) {
            if (status == WorkorderConstant.WORK_STATUS_NONE) {
                workOrderLaborer.canOpt = false;
                continue;
            }
            if (workOrderLaborer.laborerId != null && workOrderLaborer.laborerId.equals(FM.getEmId())) {
                laborerHave = true;
            }
            if (integer != null && integer == WorkorderConstant.WORK_STATUS_PROCESS) {
                if (workOrderLaborer.laborerId != null && workOrderLaborer.laborerId.equals(FM.getEmId())
                        && workOrderLaborer.status == WorkorderConstant.WORKORDER_STATUS_PERSONAL_ACCEPT) {
                    workOrderLaborer.canOpt = true;
                    continue;
                }
            }
            workOrderLaborer.canOpt = false;
        }
        return laborerHave;
    }

    /**
     * 更多按钮点击后显示的菜单
     *
     * @param context            上下文
     * @param status             工单状态
     * @param woId               工单id
     * @param approvalId         执行人id
     * @param code               工单code
     * @param workOrderMaterials 物料
     */
    public void onMoreMenuClick(final Context context,
                                final Boolean isSign,
                                final Boolean needSignOn,
                                final int status,
                                final boolean acceptWorkOrder,
                                final Long woId,
                                final Long approvalId,
                                final String code,
                                final String sendWorkContent,
                                final Long estimateStartTime,
                                final Long estimateEndTime,
                                final List<WorkorderService.WorkorderReserveRocordBean> workOrderMaterials,
                                Boolean isSignOn,
                                final boolean isMaintenanceOrder,
                                final List<Integer> currentRoles,
                                final boolean fromMessage
    ) {
        List<String> menu = new ArrayList<>();
        boolean finished = false;
        switch (status) {
            case WorkorderConstant.WORK_STATUS_CREATED:// 已创建
            case WorkorderConstant.WORK_STATUS_SUSPENDED_NO:// 已暂停(不继续工作)
                if (hasPermission( WorkorderConstant.DISPATCH_STAFF_PERMISSION, currentRoles)) {
                    menu.add(getV().getString(R.string.workorder_arrange_order));
                    menu.add(getV().getString(R.string.workorder_approval_title));
                    menu.add(getV().getString(R.string.workorder_stop));
                }
                finished = true;
                break;
            case WorkorderConstant.WORK_STATUS_PUBLISHED:// 已发布
                if (hasPermission(WorkorderConstant.DISPATCH_STAFF_PERMISSION, currentRoles)) {
                    menu.add(getV().getString(R.string.workorder_accept_order));
                    menu.add(getV().getString(R.string.workorder_back_order));
                    menu.add(getV().getString(R.string.workorder_approval_title));
                }
                break;
            case WorkorderConstant.WORK_STATUS_PROCESS:// 处理中
                if (acceptWorkOrder) {
                    if (getV().getTagStatus() != null && getV().getTagStatus().equals(WorkorderConstant.APPLICATION_FOR_SUSPENSION)) {
                        if (hasPermission(WorkorderConstant.PAUSE_PERMISSION, currentRoles)) {
                            menu.add("审批");
                        }
                    } else if (getV().getTagStatus() != null && getV().getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)){
                        if (hasPermission(WorkorderConstant.VOID_PERMISSION, currentRoles)) {
                            menu.add("审批");
                        }
                    }else {
                        menu.add(getV().getString(R.string.workorder_finish));
                        menu.add(getV().getString(R.string.workorder_stop_order));
                        menu.add(getV().getString(R.string.workorder_stop));
                        menu.add(getV().getString(R.string.workorder_back_order));
                        menu.add(getV().getString(R.string.workorder_approval_title));
                    }
                } else {
                    menu.add(getV().getString(R.string.workorder_accept_order));
                    menu.add(getV().getString(R.string.workorder_back_order));
                    menu.add(getV().getString(R.string.workorder_approval_title));
                }
                break;
            case WorkorderConstant.WORK_STATUS_SUSPENDED_GO:// 已暂停(继续工作)
                menu.add(getV().getString(R.string.workorder_continue_order));
                break;
            /**
             * @Auther: karelie
             * @Date: 2021/8/12
             * @Infor: 四运独有 新派工单
             */
            case WorkorderConstant.WORK_STATUS_TERMINATED:// 已终止
                if (hasPermission(WorkorderConstant.VERIFIER_PERMISSION, currentRoles)) {
                    if (!isMaintenanceOrder){
                        menu.add("新派工单");
                    }
                    menu.add(getV().getString(R.string.workorder_verify_tip));
                    menu.add("作废申请");
                }
                break;
            case WorkorderConstant.WORK_STATUS_COMPLETED:// 已完成  待存档
                if (getV().getTagStatus() != null && getV().getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)) {
                    if (hasPermission( WorkorderConstant.VOID_PERMISSION, currentRoles)) {
                        menu.add(getV().getString(R.string.workorder_approval_order));
                    }
                } else {
                    if (hasPermission(WorkorderConstant.VERIFIER_PERMISSION, currentRoles)) {
                        menu.add(getV().getString(R.string.workorder_verify_tip));
                    }
                    if (hasPermission( WorkorderConstant.ARCHIVE_PERMISSION, currentRoles)) {
                        menu.add(getV().getString(R.string.workorder_archive));
                    }
                }
                break;
            case WorkorderConstant.WORK_STATUS_VERIFIED:// 已验证
                if (hasPermission( WorkorderConstant.ARCHIVE_PERMISSION, currentRoles)) {
                    menu.add(getV().getString(R.string.workorder_archive));
                }
                break;
            case WorkorderConstant.WORK_STATUS_ARCHIVED:// 已存档
                break;
            case WorkorderConstant.WORK_STATUS_APPROVAL:// 待审批
                menu.add(getV().getString(R.string.workorder_approval_order));
                break;
            case WorkorderConstant.WORK_STATUS_UBNORMAL:// 异常
                if (getV().getTagStatus() != null && getV().getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)){
                    if (hasPermission( WorkorderConstant.VOID_PERMISSION, currentRoles)) {
                        menu.add("审批");
                    }
                }else {
                    menu.add("审批");
                }
                break;
            case WorkorderConstant.WORK_STATUS_MAINTENCE:// 计划性维护
                if (hasPermission( WorkorderConstant.VERIFIER_PERMISSION, currentRoles)) {
                    menu.add("验证");
                }

                if (hasPermission( WorkorderConstant.ARCHIVE_PERMISSION, currentRoles)) {
                    menu.add(getV().getString(R.string.workorder_archive));
                }
                break;
            case WorkorderConstant.WORK_STATUS_MAINTENCE_NOT:// 计划性维护
                if (hasPermission( WorkorderConstant.ARCHIVE_PERMISSION, currentRoles)) {
                    menu.add(getV().getString(R.string.workorder_archive));
                }
                break;

        }

        if (menu.size() == 0) {
            return;
        }
        menu.add(getV().getString(R.string.workorder_cancel));

        BottomTextListSheetBuilder builder = new BottomTextListSheetBuilder(context);
        builder.addArrayItem(menu);
        final boolean finalFinished = finished;
        builder.setOnSheetItemClickListener(new BottomTextListSheetBuilder.OnSheetItemClickListener() {
            @Override
            public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                dialog.dismiss();
                if (tag.equals(getV().getString(R.string.workorder_arrange_order))) {

                    boolean isDispatch = true;
                    if (workOrderMaterials != null && workOrderMaterials.size() > 0) {
                        for (WorkorderService.WorkorderReserveRocordBean workorderReserveRocordBean : workOrderMaterials) {
                            if (workorderReserveRocordBean.reservationPersonId == null
                                    || workorderReserveRocordBean.administrator == null
                                    || workorderReserveRocordBean.supervisor == null) {
                                isDispatch = false;
                            }
                        }
                    }
                    if (isDispatch) {
                        getV().startForResult(WorkorderDispatchFragment.getInstance(woId, code, sendWorkContent, estimateStartTime, estimateEndTime),
                                WorkorderInfoFragment.DISPATCH_REQUEST_CODE);
                    } else {
                        FMWarnDialogBuilder warnDialogBuilder = new FMWarnDialogBuilder(context);
                        warnDialogBuilder.setTitle(getV().getString(R.string.workorder_tip_title));
                        warnDialogBuilder.setSure(getV().getString(R.string.workorder_confirm));
                        warnDialogBuilder.setCancel(getV().getString(R.string.workorder_cancel));
                        warnDialogBuilder.setTip(R.string.workorder_none_any_people);
                        warnDialogBuilder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, View view) {
                                dialog.dismiss();
                                getV().startForResult(WorkorderDispatchFragment.getInstance(woId, code, sendWorkContent, estimateStartTime, estimateEndTime),
                                        WorkorderInfoFragment.DISPATCH_REQUEST_CODE);
                            }
                        });
                        warnDialogBuilder.addOnBtnCancelClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, View view) {
                                dialog.dismiss();
                            }
                        });
                        warnDialogBuilder.create(R.style.fmDefaultWarnDialog).show();
                    }
                } else if (tag.equals(getV().getString(R.string.workorder_stop))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    if (getV().isAllStart() || finalFinished) {
                        endWorkOrder(context, woId);
                    } else {
                        ToastUtils.showShort(R.string.workorder_terminal_error);
                    }
                } else if (tag.equals(getV().getString(R.string.workorder_approval_title))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    getV().startForResult(WorkorderApprovalFragment.getInstance(woId)
                            , WorkorderInfoFragment.APPROVAL_REQUEST_CODE);

                } else if (tag.equals(getV().getString(R.string.workorder_approval_order))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    if (getV().getTagStatus() != null && (getV().getTagStatus().equals(WorkorderConstant.APPLICATION_VOID)||
                            getV().getTagStatus().equals(WorkorderConstant.APPLICATION_FOR_SUSPENSION)
                    )) {
                        approvalWorkOrder(context, woId, approvalId, true);
                    } else {
                        approvalWorkOrder(context, woId, approvalId, false);
                    }
                } else if (tag.equals(getV().getString(R.string.workorder_verify_tip))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    if (status == WorkorderConstant.WORK_STATUS_TERMINATED) {
                        verifiedWorkOrder(context, woId, true);
                    } else {
                        verifiedWorkOrder(context, woId, false);
                    }

                } else if (tag.equals(getV().getString(R.string.workorder_archive))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }

                    workorderOptCommon(woId, null, null, WorkorderConstant.WORKORDER_OPT_TYPE_ARCHIVE, null, null);
                } else if (tag.equals(getV().getString(R.string.workorder_accept_order))) {
                    workorderOptCommon(woId, null, WorkorderConstant.WORKORDER_OPT_TYPE_ORDER, null, false, null, null);
                } else if (tag.equals(getV().getString(R.string.workorder_finish))) {

                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }

                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }

                    if (isMaintenanceOrder) {
                        //判断当前是否有设备任务还在处理 且为维护工单
                        isDoneDevice(isSignOn, isMaintenanceOrder, woId, approvalId, context);
                    } else {
                        //不是维护工单不需要判断设备完成任务
                        complete(isSignOn, isMaintenanceOrder, woId, approvalId, context);


                    }


                } else if (tag.equals(getV().getString(R.string.workorder_stop_order))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    suspendedWorkOrder(context, woId);
                } else if (tag.equals(getV().getString(R.string.workorder_back_order))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    backWorkOrder(context, woId);

                } else if (tag.equals(getV().getString(R.string.workorder_approval_title))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    approvalWorkOrder(context, woId, approvalId, false);
                } else if (tag.equals(getV().getString(R.string.workorder_continue_order))) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    workorderOptCommon(woId, null, WorkorderConstant.WORKORDER_OPT_TYPE_CONTINUE, null,
                            false, null, null);
                } else if (tag.equals("新派工单")) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    getV().newOrder();
                } else if (tag.equals("作废申请")) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }
                    getV().invalidOrder(woId);
                } else if (tag.equals("抽检")) {
                    if (!isSign) {
                        ToastUtils.showShort("请先签到");
                        return;
                    }
                    if (!needSignOn) {
                        ToastUtils.showShort(R.string.workorder_sign_error);
                        return;
                    }

                    fetchSampleTemplateById(context,woId);

                }
            }
        });
        builder.build().show();

    }

    /**
     * @param permission   需要查询的权限
     * @param currentRoles 详情中所有的权限
     * @Author: Karelie
     * @Method：hasPermission
     * @Description：判断当前操作是否有权限在内部，只需要判断从消息中跳转的工单以及非维护工单
     */
    public boolean hasPermission(Integer permission, List<Integer> currentRoles) {
        if (currentRoles != null){
            if (currentRoles.size() > 0) {
                for (Integer role : currentRoles) {
                    if (role.equals(permission)) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }


    public void complete(boolean hasSignOn, boolean isMaintenanceOrder, Long woId, Long approvalId, Context context) {
        //判断是否条件处理完成
        if (!getV().canDo()) {
            return;
        }

        if (hasSignOn) {
            ToastUtils.showShort("车站值班人员未签字");
            return;
        }

        String defaultReason = null;
        Long defaultObject = null;
        if (!isMaintenanceOrder) {
            if (getV().getFaultObjectId() == null) {
                ToastUtils.showShort("请选择故障对象");
                return;
            } else {
                defaultObject = getV().getFaultObjectId();
            }
        }


        //其他原因
        if (!isMaintenanceOrder) {
            if (getV().getOperateReasonId() == 1) {
                defaultReason = getV().getOtherReason();
                if (defaultReason == null || defaultReason.equals("")) {
                    ToastUtils.showShort("请输入具体故障原因");
                    return;
                }
            }
        }

        if (isMaintenanceOrder) {
            if (getV().getNewStatus() == WorkorderConstant.WORKORER_PROCESS) {
                cpPremisson(woId, WorkorderConstant.ORDER_COMPLETE, null, defaultObject, defaultReason);
            } else {
                doSomeThing(true, WorkorderConstant.ORDER_COMPLETE, woId, approvalId, defaultObject, defaultReason);
            }

        } else {
            if (getV().isAllStart()) {
                int allDeviceFinished = getV().isAllDeviceFinished();
                if (allDeviceFinished == 0) {
                    if (TextUtils.isEmpty(getV().getWorkDoneReminder())){
                        workorderOptCommon(woId, null, getV().getOperateReasonId(), WorkorderConstant.WORKORDER_OPT_TYPE_COMPLETION, defaultObject, defaultReason);
                    }else {
                        getV().showCompleteDiaglog(woId, null, getV().getOperateReasonId(), WorkorderConstant.WORKORDER_OPT_TYPE_COMPLETION, defaultObject, defaultReason);
                    }
                } else {
                    Long finalDefaultObject = defaultObject;
                    String finalDefaultReason = defaultReason;
                    new FMWarnDialogBuilder(context).setIconVisible(false)
                            .setSureBluBg(true)
                            .setTitle(R.string.workorder_tip_title)
                            .setSure(R.string.workorder_confirm)
                            .setTip(String.format(getV().getString(R.string.workorder_device_un_completed_tip), allDeviceFinished))
                            .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, View view) {
                                    dialog.dismiss();
                                    if (TextUtils.isEmpty(getV().getWorkDoneReminder())){
                                        workorderOptCommon(woId, null, getV().getOperateReasonId(), WorkorderConstant.WORKORDER_OPT_TYPE_COMPLETION, finalDefaultObject, finalDefaultReason);
                                    }else {
                                        getV().showCompleteDiaglog(woId, null, getV().getOperateReasonId(), WorkorderConstant.WORKORDER_OPT_TYPE_COMPLETION, finalDefaultObject, finalDefaultReason);
                                    }
                                }
                            }).create(R.style.fmDefaultWarnDialog).show();
                }
            } else {
                ToastUtils.showShort(R.string.workorder_finish_error);
            }
        }
    }


    //申请作废
    public void invalidOrderPost(Long woId, String desc, Long operateReasonId) {
        getV().showLoading();
        WorkorderOptService.InvalidOrderPostReq request = new WorkorderOptService.InvalidOrderPostReq();
        request.woId = woId;
        request.desc = desc;
        request.operateReasonId = operateReasonId;
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.INVALID_ORDER_POST)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    //终止
    private void endWorkOrder(Context context, final Long woId) {
        FMBottomInputSheetBuilder builder = new FMBottomInputSheetBuilder(context);
        builder.setOnSaveInputListener(new FMBottomInputSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                workorderOptCommon(woId, input, null, WorkorderConstant.WORKORDER_OPT_TYPE_TERMINATE, null, null);
            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {

            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {

            }
        });
        QMUIBottomSheet build = builder.build();
        builder.getSingleBtn().setVisibility(View.VISIBLE);
        builder.setTitle(R.string.workorder_terminal);
        builder.setDescHint(R.string.workorder_hint_reason_stop);
        builder.setBtnText(R.string.workorder_stop);
        builder.setSingleBtnBg(R.drawable.btn_common_bg_selector);
        builder.setSingleNeedInput(false);
        build.show();
    }

    //审批
    private void approvalWorkOrder(Context context, final Long woId, final Long approvalId, boolean isError) {
        FMBottomInputSheetBuilder builder = new FMBottomInputSheetBuilder(context);
        builder.setOnSaveInputListener(new FMBottomInputSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String input) {

            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                if (isError) {
                    approvalErrorWorkorder(woId, approvalId, input, 1);
                } else {
                    approvalWorkorder(woId, approvalId, input, WorkorderConstant.WORKORDER_APPROVAL_FAIL);
                }

            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                if (isError) {
                    approvalErrorWorkorder(woId, approvalId, input, 0);
                } else {
                    approvalWorkorder(woId, approvalId, input, WorkorderConstant.WORKORDER_APPROVAL_PASS);
                }

            }
        });
        QMUIBottomSheet build = builder.build();
        builder.getLLTwoBtn().setVisibility(View.VISIBLE);
        builder.setTitle(R.string.workorder_approval_tip);
        builder.setDescHint(getV().getString(R.string.wrokorder_hint_reason_approval));
        builder.getRightBtn().setBackgroundResource(R.drawable.btn_common_bg_selector);
        builder.setTwoBtnLeftInput(false);
        builder.setTwoBtnRightInput(false);
        builder.setLeftBtnText(R.string.workorder_reject);
        builder.setRightBtnText(R.string.workorder_pass);
        build.show();
    }

    //审批网络请求
    private void approvalWorkorder(final Long woId, final Long approvalId, String input, int type) {
        getV().showLoading();
        WorkorderOptService.WorkorderOptApprovalVReq request = new WorkorderOptService.WorkorderOptApprovalVReq();
        request.woId = woId;
        request.approvalId = approvalId;
        request.operateType = type;
        request.content = input;
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_OPT_APPROVAL_V_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                        getV().setNeedJump(true);
//                        getV().setRefreshStatus(WorkorderConstant.WORK_STATUS_NONE);
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    //异常审批
    private void approvalErrorWorkorder(final Long woId, final Long approvalId, String input, int type) {
        getV().showLoading();
        WorkorderOptService.WorkorderOptApprovalVErrorReq request = new WorkorderOptService.WorkorderOptApprovalVErrorReq();
        request.woId = woId;
        request.approveNote = input;
        request.status = type;
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_OPT_APPROVAL_V_ERROR_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                        getV().setNeedJump(true);
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    //验证
    private void verifiedWorkOrder(Context context, final Long woId, boolean hideRight) {
        FMBottomInputSheetBuilder builder = new FMBottomInputSheetBuilder(context);
        builder.setOnSaveInputListener(new FMBottomInputSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String input) {

            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                workorderOptCommon(woId, input, null, WorkorderConstant.WORKORDER_OPT_TYPE_VERIFY_FAIL, null, null);
            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                workorderOptCommon(woId, input, null, WorkorderConstant.WORKORDER_OPT_TYPE_VERIFY_PASS, null, null);
            }
        });
        QMUIBottomSheet build = builder.build();
        builder.getLLTwoBtn().setVisibility(View.VISIBLE);
        if (hideRight) {
            builder.getRightBtn().setVisibility(View.GONE);
        } else {
            builder.getRightBtn().setVisibility(View.VISIBLE);
        }
        builder.setTitle(R.string.workorder_tip_verify);
        builder.setDescHint(R.string.workorder_desc);
        builder.getRightBtn().setBackgroundResource(R.drawable.btn_common_bg_selector);
        builder.setTwoBtnLeftInput(false);
        builder.setTwoBtnRightInput(false);
        builder.setLeftBtnText(R.string.workorder_reject);
        builder.setRightBtnText(R.string.workorder_pass);
        build.show();
    }

    //显示暂停弹窗
    private void suspendedWorkOrder(Context context, final Long woId) {
        getV().showPauseDialog(context, woId);
    }

    //暂停工单
    public void pauseWorkOrder(Long woId, String input, Long optId, int type, Long time) {
        getV().showLoading();
        WorkorderOptService.WorkorderOptPauseReq request = new WorkorderOptService.WorkorderOptPauseReq();
        request.woId = woId;
        request.desc = input;
        request.type = type;
        request.operateReasonId = optId;
        request.endTime = time;
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_OPT_PAUSE_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                        getV().setNeedJump(false);
                        if (false) {
                            if (type == WorkorderConstant.WORKORDER_OPT_TYPE_SUSPENSION_CONTINUED) {
                                getV().setRefreshStatus(WorkorderConstant.WORK_STATUS_SUSPENDED_GO);
                                getV().setNeedJump(false);
                            } else if (type == WorkorderConstant.WORKORDER_OPT_TYPE_VERIFY_PASS) {
                                getV().setRefreshStatus(WorkorderConstant.WORK_STATUS_VERIFIED);
                                getV().setNeedJump(false);
                            }
                            getV().pop();
                        } else {
                            getV().getRefreshLayout().autoRefresh();
                        }

                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    //异常工单审批请求
    public void approveExceptionWorkorder(Long woId, int status, String approvalNote) {
        getV().showLoading();
        WorkorderOptService.WorkorderOptExceptionApprovalReq request = new WorkorderOptService.WorkorderOptExceptionApprovalReq();
        request.woId = woId;
        request.status = status;
        request.approveNote = approvalNote;
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_OPT_EXCEPTION_APPROVAL_V_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                        getV().setNeedJump(true);
//                        getV().setRefreshStatus(WorkorderConstant.WORK_STATUS_NONE);
                        getV().pop();
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    //原因列表
    private void reasonList() {
        getV().showLoading();
        WorkorderService.WorkorderReasonQueryReq request = new WorkorderService.WorkorderReasonQueryReq();
        request.type = 0;
        request.page = new Page();
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_REASON_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        Object temp = response.body().data;
                    }


                });
    }

    //退单
    private void backWorkOrder(Context context, final Long woId) {
        FMBottomInputSheetBuilder builder = new FMBottomInputSheetBuilder(context);
        builder.setOnSaveInputListener(new FMBottomInputSheetBuilder.OnInputBtnClickListener() {
            @Override
            public void onSaveClick(QMUIBottomSheet dialog, String input) {
                dialog.dismiss();
                workorderOptCommon(woId, input, null, WorkorderConstant.WORKORDER_OPT_TYPE_SINGLE_BACK, null, null);
            }

            @Override
            public void onLeftClick(QMUIBottomSheet dialog, String input) {

            }

            @Override
            public void onRightClick(QMUIBottomSheet dialog, String input) {

            }
        });
        QMUIBottomSheet build = builder.build();
        builder.getSingleBtn().setVisibility(View.VISIBLE);
        builder.setTitle(R.string.workorder_back_order);
        builder.setDescHint(R.string.workorder_back_reason_hint);
        builder.setBtnText(R.string.workorder_back_order);
        builder.setSingleBtnBg(R.drawable.btn_common_bg_selector);
        builder.setSingleNeedInput(false);
        build.show();
    }

    //通用工单操作
    public void workorderOptCommon(Long woId, String input, Long operateReasonId, int type, Long componentId, String causeOther) {
        workorderOptCommon(woId, input, type, operateReasonId, true, componentId, causeOther);
    }

    private void workorderOptCommon(Long woId, String input,
                                    final int type, Long operateReasonId,
                                    final boolean needJump, Long componentId, String causeOther) {
        getV().showLoading();
        WorkorderOptService.WorkorderOptCommonReq request = new WorkorderOptService.WorkorderOptCommonReq();
        request.woId = woId;
        request.operateDescription = input;
        request.operateType = type;
        request.operateReasonId = operateReasonId;
        request.causeOther = causeOther;
        request.componentId = componentId;


        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + WorkorderUrl.WORKORDER_OPT_COMMON_URL)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        getV().dismissLoading();
                        ToastUtils.showShort(R.string.workorder_operate_success);
                        getV().setNeedJump(needJump);
                        if (needJump) {
                            if (type == WorkorderConstant.WORKORDER_OPT_TYPE_SUSPENSION_CONTINUED) {
                                getV().setRefreshStatus(WorkorderConstant.WORK_STATUS_SUSPENDED_GO);
                                getV().setNeedJump(false);
                            } else if (type == WorkorderConstant.WORKORDER_OPT_TYPE_VERIFY_PASS) {
                                getV().setRefreshStatus(WorkorderConstant.WORK_STATUS_VERIFIED);
                                getV().setNeedJump(false);
                            }
                            getV().pop();
                        } else {
                            getV().getRefreshLayout().autoRefresh();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        getV().dismissLoading();
                    }
                });
    }

    @Override
    public void setPriority(Map<Long, String> p) {
        getV().setPriority(p);
    }

    @Override
    public void getWorkOrderMaterialError() {
        getV().refreshMaterialUI(null);
    }

    @Override
    public void getWorkOrderMaterialSuccess(List<WorkorderService.WorkorderReserveRocordBean> data) {
        getV().refreshMaterialUI(data);
    }


    /**
     * @return
     * @Created by: kuuga
     * @Date: on 2021/8/31 14:16
     * @Description: 位置信息判空
     */
    private static boolean isLocationNull(LocationBean remoteBean, LocationBean localBean) {
        return remoteBean != null && localBean != null;

    }


    /**
     * @return
     * @Created by: kuuga
     * @Date: on 2021/8/31 14:16
     * @Description: 工单位置是否处于签到区间
     */
    private static boolean isInLocationList(Long orderBuildingId, WorkorderService.WorkorderAttendanceResp remoteData) {
        for (Long id : remoteData.buildingIds) {
            if (id.equals(orderBuildingId)) {
                return true;
            }
        }
        return false;

    }


    public void isDoneDevice(boolean isSignOn, boolean isMaintenanceOrder, Long woId, Long approvalId, Context context) {
        getV().showLoading();
        WorkorderService.ShortestTimeReq request = new WorkorderService.ShortestTimeReq();
        request.woId = null;
        request.eqCode = null;
        OkGo.<BaseResponse<WorkorderService.ShortestTimeResp>>post(FM.getApiHost() + WorkorderUrl.QUERY_SHORTEST_TIME)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<WorkorderService.ShortestTimeResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<WorkorderService.ShortestTimeResp>> response) {
                        getV().dismissLoading();
                        WorkorderService.ShortestTimeResp resp = response.body().data;
                        if (resp.executable != null) {
                            if (resp.executable) {
                                ToastUtils.showShort("目前已经有维护任务在执行中，请先完成。");
                                return;
                            } else {
                                complete(isSignOn, isMaintenanceOrder, woId, approvalId, context);
                            }
                        }

                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.ShortestTimeResp>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        ToastUtils.showShort("数据异常");
                    }
                });

    }

    /**
     * @Creator:Karelie
     * @Data: 2021/10/11
     * @TIME: 16:09
     * @Introduce: 判断维护工单当前是否有进行中的倒计时
     **/
    public void isDoneDevice() {
        getV().showLoading();
        final boolean[] cando = {false};
        WorkorderService.ShortestTimeReq request = new WorkorderService.ShortestTimeReq();
        request.woId = null;
        request.eqCode = null;
        OkGo.<BaseResponse<WorkorderService.ShortestTimeResp>>post(FM.getApiHost() + WorkorderUrl.QUERY_SHORTEST_TIME)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<WorkorderService.ShortestTimeResp>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<WorkorderService.ShortestTimeResp>> response) {
                        getV().dismissLoading();
                        WorkorderService.ShortestTimeResp resp = response.body().data;
                        if (resp != null) {
                            if (resp.executable) {
                                ToastUtils.showShort("请先完成进行中的维护设备。");
                                return;
                            } else {
                                getV().WorkOrderCanDo(WorkorderConstant.PLAN_STEP);

                            }
                        } else {
                            getV().WorkOrderCanDo(WorkorderConstant.PLAN_STEP);
                        }


                    }

                    @Override
                    public void onError(Response<BaseResponse<WorkorderService.ShortestTimeResp>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        ToastUtils.showShort("数据异常");
                    }
                });

    }

    /**
     * @return
     * @Creator:Karelie
     * @Data: 2021/11/23
     * @TIME: 9:50
     * @Introduce: 访问接口判断当前工单是否可以进行操作
     */
    public void NFCPremission(Long workId, Integer doWhat) {
        //TODO 11.30不开放
        Map<Object, Object> json = new HashMap<>();
        json.put("woId", workId);
        OkGo.<BaseResponse<Boolean>>post(FM.getApiHost() + WorkorderUrl.NFC_CAN_DO)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(json))
                .execute(new FMJsonCallback<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Boolean>> response) {
                        getV().dismissLoading();
                        getV().NFCPression(response.body().data, doWhat);
                    }

                    @Override
                    public void onError(Response<BaseResponse<Boolean>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                        ToastUtils.showShort("数据异常");

                    }
                });
    }

    /**
     * 单独判断处理完成是否需要匹配完所有NFC标签
     */
    public void cpPremisson(Long woId, Integer way, Long approvalId, Long defaultObject, String defaultReason) {
        Map<Object, Object> json = new HashMap<>();
        json.put("woId", woId);
        OkGo.<BaseResponse<Boolean>>post(FM.getApiHost() + WorkorderUrl.COMPLETE_PREMISSION)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(json))
                .execute(new FMJsonCallback<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Boolean>> response) {
                        getV().dismissLoading();
                        doSomeThing(response.body().data, way, woId, approvalId, defaultObject, defaultReason);
                    }

                    @Override
                    public void onError(Response<BaseResponse<Boolean>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                    }
                });
    }

    public void doSomeThing(boolean cando, Integer way, Long woId, Long approvalId, Long defaultObject, String defaultReason) {
        if (!cando) {
            ToastUtils.showShort("请先触碰房间NFC标签。");
            return;
        }
        switch (way) {
            case WorkorderConstant.ORDER_COMPLETE:
                if (getV().isAllStart()) {
                    int allDeviceFinished = getV().isAllDeviceFinished();
                    if (allDeviceFinished == 0) {
                        workorderOptCommon(woId, null, getV().getOperateReasonId(), WorkorderConstant.WORKORDER_OPT_TYPE_COMPLETION, defaultObject, defaultReason);
                    } else {
                        Long finalDefaultObject = defaultObject;
                        String finalDefaultReason = defaultReason;
                        new FMWarnDialogBuilder(getV().getContext()).setIconVisible(false)
                                .setSureBluBg(true)
                                .setTitle(R.string.workorder_tip_title)
                                .setSure(R.string.workorder_confirm)
                                .setTip(String.format(getV().getString(R.string.workorder_device_un_completed_tip), allDeviceFinished))
                                .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, View view) {
                                        dialog.dismiss();
                                        workorderOptCommon(woId, null, getV().getOperateReasonId(), WorkorderConstant.WORKORDER_OPT_TYPE_COMPLETION, finalDefaultObject, finalDefaultReason);
                                    }
                                }).create(R.style.fmDefaultWarnDialog).show();
                    }
                } else {
                    ToastUtils.showShort(R.string.workorder_finish_error);
                }
                break;
            case WorkorderConstant.ORDER_STOP:
                suspendedWorkOrder(getV().getContext(), woId);
                break;
            case WorkorderConstant.ORDER_DONE:
                endWorkOrder(getV().getContext(), woId);
                break;
            case WorkorderConstant.ORDER_CHARGE_BACK:
                backWorkOrder(getV().getContext(), woId);
                break;
            case WorkorderConstant.ORDER_APPROVAL:
                approvalWorkOrder(getV().getContext(), woId, approvalId, false);
                break;
        }

    }


    /**
     * @Created by: kuuga
     * @Date: on 2022/2/11 11:01
     * @Description:抽检模板弹窗
     */
    public void showSampleTemplateDialog(Context context,List<WorkorderService.SampleTemplate> menuList,Long woId){
        BottomTextListSheetBuilder builder = new BottomTextListSheetBuilder(context);
        builder.addArrayItem(WorkOrderUtils.getValueList(menuList));
        builder.setTitle("请先选择需要抽检的模板");
        builder.setShowTitle(true);
        builder.setOnSheetItemClickListener(new BottomTextListSheetBuilder.OnSheetItemClickListener() {
            @Override
            public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                dialog.dismiss();
                if (!tag.contains("已抽检")){
                    Long woCode= WorkOrderUtils.getKey(menuList,tag);
                    Router router = Router.getInstance();
                    com.facilityone.wireless.componentservice.maintenance.MaintenanceService workorderService = (com.facilityone.wireless.componentservice.maintenance.MaintenanceService) router.getService(com.facilityone.wireless.componentservice.maintenance.MaintenanceService.class.getSimpleName());

                    if (workorderService != null) {
                        BaseFragment fragment = workorderService.getElectronicLedger2(woId,tag,woCode);
                        getV().startForResult(fragment, CODE_FOR_LEDGER);

                    }
                }
            }
        });
        builder.build().show();
    }

    /**
     * @Created by: kuuga
     * @Date: on 2022/2/11 11:01
     * @Description:获取抽检模板ID及状态
     */
    public void fetchSampleTemplateById(Context context,Long woId){
        Map<Object, Object> json = new HashMap<>();
        json.put("woId", woId);
        OkGo.<BaseResponse<List<WorkorderService.SampleTemplate>>>
                post(FM.getApiHost() + WorkorderUrl.SAMPLE_TEMPLATE)
                .tag(getV())
                .isSpliceUrl(true)
                .upJson(toJson(json))
                .execute(new FMJsonCallback<BaseResponse<List<WorkorderService.SampleTemplate>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<List<WorkorderService.SampleTemplate>>> response) {
                        getV().dismissLoading();
                        List<WorkorderService.SampleTemplate> data = response.body().data;
                        if (data!=null&&!data.isEmpty()){
                            if (data.size()>1){
//                                Map<String,String> menuMap = new HashMap<>();
//
//                                for (WorkorderService.SampleTemplate item : data) {
//                                    if (item.samplePass){
//                                        menuMap.put(item.sampleId.toString(),item.sampleName+"(已抽检)");
//                                    }else {
//                                        menuMap.put(item.sampleId.toString(),item.sampleName);
//                                    }
//
//                                }
                                showSampleTemplateDialog(context, data,woId);
                            }else {
                                Router router = Router.getInstance();
                                com.facilityone.wireless.componentservice.maintenance.MaintenanceService workorderService = (com.facilityone.wireless.componentservice.maintenance.MaintenanceService) router.getService(com.facilityone.wireless.componentservice.maintenance.MaintenanceService.class.getSimpleName());

                                if (workorderService != null) {
                                    BaseFragment fragment = workorderService.getElectronicLedger2(woId,data.get(0).sampleName,data.get(0).sampleId);
                                    getV().startForResult(fragment, CODE_FOR_LEDGER);

                                }
                            }
                        }else {
                            ToastUtils.showShort("暂无模板");
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<List<WorkorderService.SampleTemplate>>> response) {
                        ToastUtils.showShort(R.string.workorder_operate_fail);
                        super.onError(response);
                    }
                });


    }


}
