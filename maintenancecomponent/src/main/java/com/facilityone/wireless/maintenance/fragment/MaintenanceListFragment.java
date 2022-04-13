package com.facilityone.wireless.maintenance.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridTagAdapter;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.basiclib.utils.SystemDateUtils;
import com.facilityone.wireless.componentservice.demand.DemandService;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.adapter.MaintenanceListAdapter;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;
import com.facilityone.wireless.maintenance.model.MaintenanceService;
import com.facilityone.wireless.maintenance.presenter.MaintenanceListPresenter;
import com.luojilab.component.componentlib.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Auther: karelie
 * @Date: 2021/8/17
 * @Infor: 维护工单通用列表
 */
public class MaintenanceListFragment extends BaseFragment<MaintenanceListPresenter> implements BaseQuickAdapter.OnItemClickListener, OnRefreshLoadMoreListener, DrawerLayout.DrawerListener, View.OnClickListener {
    private static final String LIST_TYPE = "list_type";
    private EditText mCodeEt;
    private EditText mSiteEt;

    private static final int MAINTENANCE_INFO = 4001;
    private static final int REQUEST_LOCATION = 20001;

    public static final int FAULT_DEVICE = 4007;
    public static final int DISPATCH_REQUEST_CODE = 4003;
    public static final int TOOLS = 4008;
    public static final int CHARGE = 4009;
    public static final int STEP = 4010;
    public static final int SPACE_LOCATION = 4011;
    public static final int PAYMENT = 4012;
    private final static int REQUEST_REASON = 20007;
    private final static int REQUEST_INVALID = 20008;
    private final static int REQUEST_SPECIALTY = 20009;

    private Integer mType;
    private ImageView mQueryMenuIv;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mMenuLL;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private MaintenanceListAdapter mAdapter;
    private List<MaintenanceEnity.MaintenanceListEnity> mList;
    /****************************************/
    //判断是否是同一批次工单条件
    private Long localWoId = null; //当前选中的Id
    private Long workTeamId = null; //当前选中的工作组Id
    /***************************************/
    private Page mPage;
    private LinearLayout mBottomMenuLl; //底部按钮布局
    private TextView mTvChooseAll; //全选按钮
    private TextView mTvComplete; //完成按钮
    private LinearLayout mMenuLlAll;//底部整体布局

    /*****************2021-11-18*****************/
    private LinearLayout mllCycle; //周期列表选项
    private LinearLayout mllSpecialty; //专业选项
    private EditText mSpecialty; //专业
    /***************************************/

    private GridTagAdapter mCycleTagAdapter;
    private MaintenanceService.ConditionBean mConditionBean;
    private List<AttachmentBean> mCycleAs;
    private RecyclerView mCycleRv;
    private static final int MAX_NUMBER = 3;//一行显示几个tag
    private ArrayList<String> mOrderIdsList; // 批量操作工单Id
    private ArrayList<Long> receiveIds; // 批量接单工单Id
    private Boolean isChooseOn = false; //是否是选择批量选择状态

    private static final int WORKORDER_INFO = 4001;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
        initOnClick();
    }

    public void getData() {
        showLoading();
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
        mBottomMenuLl.setVisibility(View.GONE);
        getPresenter().getMaintenanceList(mType, mPage, null, true);
    }

    private void initOnClick() {
        mQueryMenuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });

        mTvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomMenuLl.setVisibility(View.GONE);
                for (MaintenanceEnity.MaintenanceListEnity data : mList) {
                    data.choice = 0; //全部打开状态
                }
                changeRightMenu(mType);
                mAdapter.replaceData(mList);
                mAdapter.notifyDataSetChanged();
                if (mType == MaintenanceConstant.TWO){
                    mQueryMenuIv.setVisibility(View.VISIBLE);
                }
                isChooseOn = false; //当前是批量选择状态
                mTvChooseAll.setText("全选");
                localWoId = null;
                workTeamId = null;
                onRefresh();
            }
        });

        mTvChooseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mList.size() ==0 || mList == null){
                    ToastUtils.showShort("无数据");
                    return;
                }

                if (workTeamId == null && localWoId == null) {
                    localWoId = mList.get(0).pmId; //默认设置匹配Id为列表第一个元素的Id
                    workTeamId = mList.get(0).workTeamId; // 默认设置工作组Id为列表第一个元素的Id
                }
                if (mTvChooseAll.getText().equals("全选")) {
                    if (mType == MaintenanceConstant.ONE) {
                        for (MaintenanceEnity.MaintenanceListEnity data : mList) {
                            data.choice = 2; //选中状态
                        }
                        mAdapter.replaceData(mList);
                        mAdapter.notifyDataSetChanged();
                        mTvChooseAll.setText("取消");
                    } else {
                        for (MaintenanceEnity.MaintenanceListEnity data : mList) {
                            if (data.pmId.equals(localWoId)&& data.workTeamId.equals(workTeamId)) {
                                data.choice = 2; //选中状态
                            } else {
                                data.choice = 4; //全选后其余不同工单置灰
                            }
                        }
                        mAdapter.replaceData(mList);
                        mAdapter.notifyDataSetChanged();
                        mTvChooseAll.setText("取消");
                    }
                } else {
                    for (MaintenanceEnity.MaintenanceListEnity data : mList) {
                        data.choice = 1; //全部打开状态
                    }
                    mAdapter.replaceData(mList);
                    mAdapter.notifyDataSetChanged();
                    mTvChooseAll.setText("全选");
                    localWoId = null;
                    workTeamId = null;
                }

            }
        });
    }

    public void initData() {
        //数据刷新 将界面状态重置 列表内数据初始化将所有状态改编为默认状态
        mList.clear();
        isChooseOn = false;
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(LIST_TYPE, -1);
        }
        changeRightMenu(mType);
        getData();
    }

    public void changeRightMenu(Integer mType) {
        String title = null;
        removeRightView();
        switch (mType) {
            case MaintenanceConstant.ZERO:
            case MaintenanceConstant.ONE:
                title = "待处理维护工单";
                setRightTextButton("批量接单", R.id.maintenance_bulk_orders);
                break;
            case MaintenanceConstant.TWO:
                title = "待派工维护工单";
                mQueryMenuIv.setVisibility(View.VISIBLE);
                mllSpecialty.setVisibility(View.GONE);
                mllCycle.setVisibility(View.VISIBLE);
                setRightTextButton("批量派工", R.id.maintenance_batch_dispatch);
                break;
            case MaintenanceConstant.THREE:
                title = "待审批维护工单";
                break;
            case MaintenanceConstant.FOUR:
                title = "异常维护工单";
                break;
            case MaintenanceConstant.FIVE:
                title = "待存档维护工单";
                mQueryMenuIv.setVisibility(View.VISIBLE);
                mllSpecialty.setVisibility(View.VISIBLE);
                mllCycle.setVisibility(View.GONE);
                break;
            case MaintenanceConstant.SIX:
                title = "维护工单查询列表";
                break;
            case MaintenanceConstant.SEVEN:
                title = "待抽检维护工单";
                break;
        }
        setTitle(title + "");
    }

    public void refreshSuccessUI(List<MaintenanceEnity.MaintenanceListEnity> ms, boolean refresh, Page page) {
        dismissLoading();
//        changeRightMenu(mType);
        this.mPage = page;
        if (isChooseOn) {
            for (MaintenanceEnity.MaintenanceListEnity m : ms) {
                m.choice = 1;
                mTvChooseAll.setText("全选");
            }
        } else {
            for (MaintenanceEnity.MaintenanceListEnity m : ms) {
                m.choice = 0;
            }
        }

        /**
         * 刷新重置
         * */
        localWoId = null;
        workTeamId = null;

        if (refresh) {
            mList = new ArrayList<>();
            for (MaintenanceEnity.MaintenanceListEnity data : ms) {
                data.choice = 0; //全部打开状态
            }
            mList.addAll(ms);
            mAdapter.setNewData(ms);
            mRefreshLayout.finishRefresh();
        } else {
            mList.addAll(ms);
            mAdapter.addData(ms);
            mRefreshLayout.finishLoadMore();
        }
        if (mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        }
        mAdapter.notifyDataSetChanged();
    }

    //刷新页面
    private void onRefresh() {
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
        mBottomMenuLl.setVisibility(View.GONE);
        changeRightMenu(mType);
        getPresenter().getMaintenanceList(mType, mPage, mConditionBean, true);
    }


    public void noDataRefresh() {
        mList.clear();
        mAdapter.setNewData(mList);
        mAdapter.notifyDataSetChanged();
        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
    }

    private void initView() {
        mQueryMenuIv = findViewById(R.id.iv_query_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mBottomMenuLl = findViewById(R.id.ll_maintenance_bottom_menu);
        mTvChooseAll = findViewById(R.id.ll_maintenance_bottom_menu_all);
        mTvComplete = findViewById(R.id.ll_maintenance_bottom_menu_complete);
        mCodeEt = findViewById(R.id.work_order_code);
        mSiteEt = findViewById(R.id.work_order_site);
        mllCycle = findViewById(R.id.ll_maintenance_cycle);
        mllSpecialty = findViewById(R.id.ll_maintenance_specialty);
        mSpecialty = findViewById(R.id.work_order_specialty);
        mCycleRv = findViewById(R.id.work_order_query_menu_filter_cycle_fl);

        mSiteEt.setOnClickListener(this);
        mMenuLL = findViewById(R.id.ll_menu);
        mDrawerLayout.addDrawerListener(this);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setScrimColor(ContextCompat.getColor(getContext(), R.color.drawer_menu_transparent));
        int screenWidth = ScreenUtils.getScreenWidth();
        ViewGroup.LayoutParams lp = mMenuLL.getLayoutParams();
        lp.width = (screenWidth * 2 / 3);
        mMenuLL.setLayoutParams(lp);

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mList = new ArrayList<>();
        mAdapter = new MaintenanceListAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mConditionBean = new MaintenanceService.ConditionBean();
        mCycleAs = new ArrayList<>();
        AttachmentBean bb = new AttachmentBean();
        bb.value = -1L;
        bb.name = getString(R.string.maintenance_unlimited);
        bb.check = true;
        mCycleAs.add(bb);




        //优先级、状态、标签列表适配器
        mCycleAs.addAll(getPresenter().getWorkorderCycle(getContext()));
        mCycleTagAdapter = new GridTagAdapter(getContext(), mCycleAs);
        mCycleRv.setLayoutManager(new GridLayoutManager(getContext(), MAX_NUMBER));
        mCycleRv.setAdapter(mCycleTagAdapter);

        mSpecialty.setOnClickListener(this);

        findViewById(R.id.reset_btn).setOnClickListener(this);
        findViewById(R.id.sure_btn).setOnClickListener(this);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        mRefreshLayout.setOnRefreshListener(this);

    }


    //打开状态
    public void clearAll() {
        for (MaintenanceEnity.MaintenanceListEnity data : mList) {
            data.choice = 1;
        }
        mAdapter.notifyDataSetChanged();
    }

    //关闭状态
    public void ReBack(){
        for (MaintenanceEnity.MaintenanceListEnity data : mList) {
            data.choice = 0;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MaintenanceEnity.MaintenanceListEnity workorderItemBean = ((MaintenanceListAdapter) adapter).getData().get(position);
        if (workTeamId == null && localWoId == null) {
            workTeamId = workorderItemBean.workTeamId;
            localWoId = workorderItemBean.pmId;
        }

        if (workorderItemBean.choice == MaintenanceConstant.CHOICE_NO) {
            Integer status = workorderItemBean.status;
            Long woId = workorderItemBean.woId;
            String code = workorderItemBean.code;
            Router router = Router.getInstance();
            WorkorderService workorderService = (WorkorderService) router.getService(WorkorderService.class.getSimpleName());
            if (workorderService != null) {
                BaseFragment fragment;
                if (mType == MaintenanceConstant.FIVE) {
                    LogUtils.d("当前woId",woId);
                    fragment = workorderService.getWorkorderInfoFragment(status, code, woId, true, true);
                } else if (mType == MaintenanceConstant.ONE){
                    fragment = workorderService.getWorkorderInfoPendingFragment(status, code, woId, 1,true);
                }else if (mType == MaintenanceConstant.FOUR){
                    fragment = workorderService.getWorkorderInfoFragment(10, code,true,woId);
                }else if(mType == MaintenanceConstant.SEVEN){
                    fragment = workorderService.getWorkorderInfoFragment(MaintenanceConstant.WORKORDER_STATUS_NONE,code,woId,true,1);
                }else {
                    fragment = workorderService.getWorkorderInfoFragment(status, code, woId, true);
                }
                startForResult(fragment, MAINTENANCE_INFO);
            }
        } else if (workorderItemBean.choice == MaintenanceConstant.CHOICE_All) {
            if (mType == MaintenanceConstant.TWO) {
                if (mList.get(position).pmId .equals(localWoId)&& mList.get(position).workTeamId.equals(workTeamId)) {
                    mList.get(position).choice = 2;
                } else {
                    ToastUtils.showShort("非同批次工单");
                }
            } else {
                mList.get(position).choice = 2;
            }
            mAdapter.notifyItemChanged(position);
        } else if (workorderItemBean.choice == MaintenanceConstant.CHOICE_UP) {
            mList.get(position).choice = 3;
            mAdapter.notifyItemChanged(position);
            for (MaintenanceEnity.MaintenanceListEnity data : mList) {
                if (data.choice == 2) {
                    return; //若内部有选择状态，则跳出循环且跳出本次判断
                }
            }
            /**
             * @Auther: karelie
             * @Date: 2021/8/24
             * @Infor: 如果列表中没有选中状态，则清空当前列表内所有数据状态为默认且重置基础Id
             */
            clearAll();
            mTvChooseAll.setText("全选");
            localWoId = null;
            workTeamId = null;
        } else if (workorderItemBean.choice == MaintenanceConstant.CHOICE_DOWN) {
            if (mList.get(position).pmId == localWoId && mList.get(position).workTeamId == workTeamId) {
                mList.get(position).choice = 2;
                mAdapter.notifyItemChanged(position);
            } else {
                ToastUtils.showShort("非同批次工单");
            }
        } else if (workorderItemBean.choice == MaintenanceConstant.CHOICE_OFF) {
            ToastUtils.showShort("非同批次工单");
        }
    }

    @Override
    public void onRightTextMenuClick(View view) {
        super.onRightTextMenuClick(view);
//        mList = mAdapter.getData();
        if (mList == null || mList.size() == 0) {
            ToastUtils.showShort("暂无数据");
            return;
        }
        int viewId = view.getId();
        if (viewId == R.id.maintenance_bulk_orders) {
            Iterator<MaintenanceEnity.MaintenanceListEnity> it_b = mList.iterator();
            while(it_b.hasNext()){
                MaintenanceEnity.MaintenanceListEnity a=it_b.next();
                if (a.status != MaintenanceConstant.WORKORDER_STATUS_PUBLISHED) {
//                if (a.applicantName.equals("车站管理人员")) {
                    it_b.remove();
                }else {
                    a.choice = 1; //全部打开状态
                }
            }

            if (mList.size() == 0 ){
                noDataRefresh();
                dismissLoading();
                mAdapter.replaceData(mList);
                mAdapter.notifyDataSetChanged();
            }else {
                mBottomMenuLl.setVisibility(View.VISIBLE);
                isChooseOn = true; //当前是批量选择状态
                removeRightView();
                setRightTextButton("接单", R.id.maintenance_add_order);
                mAdapter.replaceData(mList);
                mAdapter.notifyDataSetChanged();
            }

        } else if (viewId == R.id.maintenance_batch_dispatch) {
            for (MaintenanceEnity.MaintenanceListEnity data : mList) {
                data.choice = 1; //全部打开状态
            }
            isChooseOn = true; //当前是批量选择状态
            mBottomMenuLl.setVisibility(View.VISIBLE);
            mQueryMenuIv.setVisibility(View.GONE);
            removeRightView();
            setRightTextButton("派单", R.id.maintenance_add_order);
            mAdapter.replaceData(mList);
            mAdapter.notifyDataSetChanged();
        } else if (viewId == R.id.maintenance_add_order) {
            mOrderIdsList = new ArrayList<>(); // 订单Id
            receiveIds = new ArrayList<>(); //接单Id
            switch (mType) {
                case MaintenanceConstant.ONE:
                    for (MaintenanceEnity.MaintenanceListEnity ids : mList) {
                        if (ids.choice == 2) {
                            receiveIds.add(ids.woId);
                        }
                    }
                    if (receiveIds.size() == 0 || receiveIds == null) {
                        ToastUtils.showShort("请选择工单后操作");
                        return;
                    }
                    MaintenanceEnity.ReceiveOrderReq req = new MaintenanceEnity.ReceiveOrderReq();
                    req.ids = receiveIds;
                    showLoading();
                    getPresenter().receiveOrder(req); //接单操作
                    break;
                case MaintenanceConstant.TWO:
                    for (MaintenanceEnity.MaintenanceListEnity ids : mList) {
                        if (ids.choice == MaintenanceConstant.CHOICE_UP) {
                            mOrderIdsList.add(ids.woId + "");
                        }
                    }
                    if (mOrderIdsList.size() == 0 || mOrderIdsList == null) {
                        ToastUtils.showShort("请选择工单后操作");
                        return;
                    }

                    //转路由
                    Router router = Router.getInstance();
                    WorkorderService workorderService = (WorkorderService) router.getService(WorkorderService.class.getSimpleName());
                    BaseFragment fragment;
                    fragment = workorderService.getOrderDispatchFragment(mOrderIdsList, "批量派工", "", null, null, workTeamId);
                    startForResult(fragment, DISPATCH_REQUEST_CODE);
                    break;
            }
        }

    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_maintenance_list;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public MaintenanceListPresenter createPresenter() {
        return new MaintenanceListPresenter();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (mPage == null || !mPage.haveNext()) {
            refreshLayout.finishLoadMore();
            ToastUtils.showShort("无更多数据");
            return;
        }
        getPresenter().getMaintenanceList(mType, mPage.nextPage(), mConditionBean, false);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefresh();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        SelectDataBean bean = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK);
        switch (requestCode) {
            //选择地址信息回调后操作
            case REQUEST_LOCATION:
                if (bean == null) {
                    mSiteEt.setText("");
                } else {
                    mSiteEt.setText(StringUtils.formatString(bean.getFullName()));
                    mConditionBean.location = bean.getLocation();
                    if (bean.getLocation().buildingId != null){
                        mConditionBean.locationId = bean.getLocation().buildingId;
                    }
                }
                break;
            case REQUEST_SPECIALTY:
                if (bean == null){
                    mSpecialty.setText("");
                }else {
                    mSpecialty.setText(bean.getName()+"");
                    if (bean.getId() != null){
                        mConditionBean.specialty = bean.getId();
                    }
                }
                break;
            default:
                //默认状态 非需求状态下回调实现初始化
                isChooseOn = false;
                changeRightMenu(mType);
                getData();
                break;
        }
    }


    public static MaintenanceListFragment getInstance(Integer type) {
        Bundle bundle = new Bundle();
        bundle.putInt(LIST_TYPE, type);
        MaintenanceListFragment instance = new MaintenanceListFragment();
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        //未知
//        KeyboardUtils.hideSoftInput(getActivity());

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.work_order_site) {
            startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_LOCATION), REQUEST_LOCATION);
        } else if(viewId == R.id.work_order_specialty){
            startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_SPECIALTY), REQUEST_SPECIALTY);
        }else if (viewId == R.id.reset_btn) {//重置
            resetCondition();
        } else if (viewId == R.id.sure_btn) {//确定
            String code = mCodeEt.getText().toString();
            if (TextUtils.isEmpty(code)) {
                mConditionBean.planName = null;
            } else {
                mConditionBean.planName = code;
            }
            getCycle();
//            SystemDateUtils.setCalendarPreciseValue(mConditionBeg, mConditionEnd);
//            conditionStartTime = mConditionBeg.getTimeInMillis();
//            conditionEndTime = mConditionEnd.getTimeInMillis();
//            mConditionBean.startDateTime = conditionStartTime;
//            mConditionBean.endDateTime = conditionEndTime;
            mDrawerLayout.closeDrawers();
            showLoading();
            onRefresh();
        }
    }

    private void resetCondition() {
        mCodeEt.setText("");
        mSiteEt.setText("");
        mSpecialty.setText("");

        mConditionBean.planName = null;
//        showConditionTime();
        mConditionBean.priority = null;
        mConditionBean.period = null;
        mConditionBean.typeId = null;
        mConditionBean.newStatus = null;
        mConditionBean.location = null;
        mConditionBean.locationId = null;
        mConditionBean.specialty = null;
        for (AttachmentBean priorityA : mCycleAs) {
            priorityA.check = false;
            if (priorityA.value == -1L) {
                priorityA.check = true;
            }
        }
        mCycleTagAdapter.notifyDataSetChanged();

    }

    public void getCycle() {
        List<Long> pId = new ArrayList<>();
        for (AttachmentBean a : mCycleAs) {
            if (a.value != -1L && a.check) {
                pId.add(a.value);
            }
        }
        if (pId.size() > 0) {
            mConditionBean.period = pId;
        } else {
            mConditionBean.period = null;
        }
    }
}
