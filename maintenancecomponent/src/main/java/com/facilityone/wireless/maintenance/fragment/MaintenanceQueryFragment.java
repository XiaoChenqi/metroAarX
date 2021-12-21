package com.facilityone.wireless.maintenance.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridTagAdapter;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.DatePickUtils;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.basiclib.utils.SystemDateUtils;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.adapter.MaintenanceListAdapter;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;
import com.facilityone.wireless.maintenance.model.MaintenanceService;
import com.facilityone.wireless.maintenance.presenter.MaintenancePresenter;
import com.facilityone.wireless.maintenance.presenter.MaintenanceQueryPresenter;

import com.luojilab.component.componentlib.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.facilityone.wireless.a.arch.utils.DatePickUtils.YEAR_MONTH;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:工单查询
 * Date: 2018/7/4 下午4:14
 */
public class MaintenanceQueryFragment extends BaseFragment<MaintenanceQueryPresenter> implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener, OnRefreshLoadMoreListener, DrawerLayout.DrawerListener {
    private View mQueryHead;
    private TextView mPreTv;
    private TextView mNextTv;
    private TextView mShowMouthTv;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private ImageView mQueryMenuIv;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mMenuLL;

    private EditText mCodeEt;
    private EditText mSiteEt;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private TextView mServiceTv;
    private TextView mTvClearType;
    private ImageView mTvClearWoIv;
    private ImageView mTvClearWoDescIv;

    private RecyclerView mCycleRv;
    private RecyclerView mStatusRv;
    private RecyclerView mLabelRv;

    private static final int MAX_NUMBER = 3;//一行显示几个tag
    private static final int MAINTENANCE_INFO = 4001;
    private static final int REQUEST_LOCATION = 20001;
    private static final int REQUEST_SERVICE_TYPE = 4003;
    private static final String MY_REPAIR = "my_repair";

    private MaintenanceListAdapter mAdapter;
    private GridTagAdapter mCycleTagAdapter;
    private GridTagAdapter mStatusTagAdapter;
    private GridTagAdapter mLabelTagAdapter;
    private Page mPage;

    private Calendar mCalendarBeg = Calendar.getInstance();
    private Calendar mCalendarEnd = Calendar.getInstance();

    private Calendar mConditionBeg;
    private Calendar mConditionEnd;

    private Long startTime, endTime;
    private Long conditionStartTime, conditionEndTime;
    private MaintenanceService.ConditionBean mConditionBean;
    private List<AttachmentBean> mCycleAs;
    private List<AttachmentBean> mStatusAs;
    private List<AttachmentBean> mLabelAs;

    private int clickPosition;
    private boolean mFromMyRepair;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
        initDrawerLayout();
//        getPresenter().queryPriority(true);
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mFromMyRepair = arguments.getBoolean(MY_REPAIR, false);
        }
    }

    @Override
    public MaintenanceQueryPresenter createPresenter() {
        return new MaintenanceQueryPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_maintenance_query;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    private void initView() {
        mQueryHead = findViewById(R.id.query_head);
        mPreTv = findViewById(R.id.history_pre_iv);
        mNextTv = findViewById(R.id.history_next_iv);
        mShowMouthTv = findViewById(R.id.history_time_sep);
        mQueryMenuIv = findViewById(R.id.iv_query_menu);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mMenuLL = findViewById(R.id.ll_menu);
        mCodeEt = findViewById(R.id.work_order_code);
        mSiteEt=findViewById(R.id.work_order_site);
        mCycleRv = findViewById(R.id.work_order_query_menu_filter_cycle_fl);
        mStatusRv = findViewById(R.id.work_order_query_menu_filter_status_fl);
        mLabelRv = findViewById(R.id.work_order_query_menu_filter_label_fl);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MaintenanceListAdapter(MaintenanceConstant.MAINTENANCE_SEVEN);
        mRecyclerView.setAdapter(mAdapter);

        if (mFromMyRepair) {
//            setTitle(R.string.workorder_my_report_fault);
            mQueryHead.setVisibility(View.GONE);
            mQueryMenuIv.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            setTitle("维护工单查询");
            mQueryHead.setVisibility(View.VISIBLE);
            mQueryMenuIv.setVisibility(View.VISIBLE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        mCycleAs = new ArrayList<>();
        mStatusAs = new ArrayList<>();
        mLabelAs=new ArrayList<>();
        AttachmentBean bb = new AttachmentBean();
        bb.value = -1L;
        bb.name = getString(R.string.maintenance_unlimited);
        bb.check = true;
        mStatusAs.add(bb);
        mCycleAs.add(bb);
        mLabelAs.add(bb);

        //优先级、状态、标签列表适配器
        mCycleAs.addAll(getPresenter().getWorkorderCycle(getContext()));
        mStatusAs.addAll(getPresenter().getWorkorderStatus(getContext()));
        mLabelAs.addAll(getPresenter().getWorkorderLabels(getContext()));

        mCycleTagAdapter = new GridTagAdapter(getContext(), mCycleAs);
        mCycleRv.setLayoutManager(new GridLayoutManager(getContext(), MAX_NUMBER));
        mCycleRv.setAdapter(mCycleTagAdapter);

        mStatusTagAdapter = new GridTagAdapter(getContext(), mStatusAs);
        mStatusRv.setLayoutManager(new GridLayoutManager(getContext(), MAX_NUMBER));
        mStatusRv.setAdapter(mStatusTagAdapter);

        mLabelTagAdapter = new GridTagAdapter(getContext(), mLabelAs);
        mLabelRv.setLayoutManager(new GridLayoutManager(getContext(), MAX_NUMBER));
        mLabelRv.setAdapter(mLabelTagAdapter);


        mPreTv.setOnClickListener(this);
        mNextTv.setOnClickListener(this);
        mSiteEt.setOnClickListener(this);
        mShowMouthTv.setOnClickListener(this);
        mRefreshLayout.setOnRefreshLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
        mQueryMenuIv.setOnClickListener(this);
//        mStartTimeTv.setOnClickListener(this);
//        mEndTimeTv.setOnClickListener(this);
//        mServiceTv.setOnClickListener(this);
        mDrawerLayout.addDrawerListener(this);
        findViewById(R.id.work_order_query_menu_filter_reset_btn).setOnClickListener(this);
        findViewById(R.id.work_order_query_menu_filter_sure_btn).setOnClickListener(this);
        findViewById(R.id.work_order_query_menu_operate_ll).setOnClickListener(this);

        int screenWidth = ScreenUtils.getScreenWidth();
        ViewGroup.LayoutParams lp = mMenuLL.getLayoutParams();
        lp.width = (screenWidth * 2 / 3);
        mMenuLL.setLayoutParams(lp);

        mConditionBean = new MaintenanceService.ConditionBean();

        mCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
//                    mTvClearWoIv.setVisibility(View.GONE);
                } else {
//                    mTvClearWoIv.setVisibility(View.VISIBLE);
                }
            }
        });



        initDate();
        getData();
    }

    private void getData() {
        showLoading();
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
        getPresenter().getMaintenanceList(MaintenanceConstant.SIX,mPage,mConditionBean,false);
    }

    private void resetCondition() {
        mCodeEt.setText("");
        mSiteEt.setText("");
        mConditionBean.woCode = null;
//        showConditionTime();
        mConditionBean.priority = null;
        mConditionBean.period=null;
        mConditionBean.location=null;
        mConditionBean.typeId = null;
        mConditionBean.newStatus = null;
        for (AttachmentBean cycleA : mCycleAs) {
            cycleA.check = false;
            if ( cycleA.value == -1L) {
                cycleA.check = true;
            }
        }
        mCycleTagAdapter.notifyDataSetChanged();
        for (AttachmentBean statusA : mStatusAs) {
            statusA.check = false;
            if (statusA.value == -1L) {
                statusA.check = true;
            }
        }
        mStatusTagAdapter.notifyDataSetChanged();
        for (AttachmentBean labelA : mLabelAs) {
            labelA.check = false;
            if (labelA.value == -1L) {
                labelA.check = true;
            }
        }
        mLabelTagAdapter.notifyDataSetChanged();
    }

    private void initDrawerLayout() {
        mDrawerLayout.setScrimColor(ContextCompat.getColor(getContext(), R.color.drawer_menu_transparent));
    }

    //设置初始时间 时间范围从0:00 到23:59
    private void initDate() {
        SystemDateUtils.showMonth(null, mCalendarBeg, mCalendarEnd);
        showDate(mCalendarBeg.get(Calendar.YEAR), mCalendarBeg.get(Calendar.MONTH));
    }

    //展示当前所选时间
    private void showDate(int year, int month) {
        startTime = mCalendarBeg.getTimeInMillis();
        endTime = mCalendarEnd.getTimeInMillis();
        mConditionBean.startDateTime = startTime;
        mConditionBean.endDateTime = endTime;
//
        mShowMouthTv.setText(year + getString(R.string.maintenance_year) + String.format("%02d", month + 1) + getString(R.string.maintenance_month));
        showConditionTime();
    }

    private void showConditionTime() {
        //设置过滤
        mConditionBeg = Calendar.getInstance();
        mConditionBeg.set(mCalendarBeg.get(Calendar.YEAR),
                mCalendarBeg.get(Calendar.MONTH), 1);
        mConditionEnd = Calendar.getInstance();
        mConditionEnd.set(mCalendarBeg.get(Calendar.YEAR),
                mCalendarBeg.get(Calendar.MONTH),
                mCalendarBeg.getActualMaximum(Calendar.DAY_OF_MONTH));

        Date startTime = mConditionBeg.getTime();
        Date endTime = mConditionEnd.getTime();

        conditionStartTime = mCalendarBeg.getTimeInMillis();
        conditionEndTime = mCalendarEnd.getTimeInMillis();
//
//        String startTimes = TimeUtils.date2String(startTime, DateUtils.SIMPLE_DATE_FORMAT_YMD);
//        mStartTimeTv.setText(startTimes);
//        String endTimes = TimeUtils.date2String(endTime, DateUtils.SIMPLE_DATE_FORMAT_YMD);
//        mEndTimeTv.setText(endTimes);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.history_pre_iv) {//上一月
            preMonth();
        } else if (viewId == R.id.history_next_iv) {//下一月
            nextMonth();
        } else if (viewId == R.id.history_time_sep) {//选时间
            pickDate();
        } else if (viewId == R.id.iv_query_menu) {//菜单
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }

        else if (viewId == R.id.work_order_site) {//选择站点
            startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_LOCATION), REQUEST_LOCATION);
        } else if (viewId == R.id.work_order_query_menu_filter_reset_btn) {//重置
            resetCondition();
        } else if (viewId == R.id.work_order_query_menu_filter_sure_btn) {//确定
            String code = mCodeEt.getText().toString();
            if (TextUtils.isEmpty(code)) {
                mConditionBean.planName = null;
            } else {
                mConditionBean.planName = code;
            }
            getCycle();
            getStatus();
            getLabel();
            mDrawerLayout.closeDrawers();
            showLoading();
            onRefresh();
        }
//
        else if (viewId == R.id.work_order_query_menu_operate_ll) {

        }
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

    public void getStatus() {
        List<Long> statusId = new ArrayList<>();
        for (AttachmentBean a : mStatusAs) {
            if (a.value != -1L && a.check) {
                statusId.add(a.value);
            }
        }
        if (statusId.size() > 0) {
            mConditionBean.newStatus = statusId;
        } else {
            mConditionBean.newStatus = null;
        }
    }

    public void getLabel() {
        List<Long> labelId = new ArrayList<>();
        for (AttachmentBean a : mLabelAs) {
            if (a.value != -1L && a.check) {
                labelId.add(a.value);
            }
        }
        if (labelId.size() > 0) {
            mConditionBean.tag = labelId;
        } else {
            mConditionBean.tag = null;
        }
    }


//    private boolean detectionTime() {
//        if (conditionEndTime < conditionStartTime) {
//            ToastUtils.showShort(R.string.workorder_select_time_error);
//            return false;
//        }
//        return true;
//    }

    private void pickDate() {
        DatePickUtils.pickDateDefault(getActivity(), mCalendarBeg, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                showLoading();
                mCalendarBeg.setTime(date);
                resetDateTime(null);
            }
        }, YEAR_MONTH);
    }

    //下一月
    private void nextMonth() {
        showLoading();
        resetDateTime(false);
    }

    //上一月
    private void preMonth() {
        showLoading();
        resetDateTime(true);
    }

    //重置开始时间和结束时间
    private void resetDateTime(Boolean isPre) {
        SystemDateUtils.showMonth(isPre, mCalendarBeg, mCalendarEnd);
        showDate(mCalendarBeg.get(Calendar.YEAR), mCalendarBeg.get(Calendar.MONTH));
        mConditionBeg = mCalendarBeg;
        mConditionEnd = mCalendarEnd;
        onRefresh();
    }

    private void onRefresh() {
        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
        getPresenter().getMaintenanceList(MaintenanceConstant.SIX,mPage,mConditionBean,true);
    }
    public void noDataRefresh(List<MaintenanceEnity.MaintenanceListEnity> data) {
        mAdapter.setNewData(data);
        mAdapter.notifyDataSetChanged();
        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        clickPosition = position;

        clickPosition = position;
        MaintenanceEnity.MaintenanceListEnity workorderItemBean = ((MaintenanceListAdapter) adapter).getData().get(position);
        Integer status = workorderItemBean.status;
        Long woId = workorderItemBean.woId;
        String code = workorderItemBean.code;


        Router router = Router.getInstance();
        WorkorderService workorderService = (WorkorderService) router.getService(WorkorderService.class.getSimpleName());
        if (workorderService != null) {
            BaseFragment fragment = workorderService.getWorkorderInfoFragment(MaintenanceConstant.WORKORDER_STATUS_NONE,code,woId,true);
            startForResult(fragment,MAINTENANCE_INFO);
        }

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (mPage == null || !mPage.haveNext()) {
            refreshLayout.finishLoadMore();
            ToastUtils.showShort(R.string.workorer_no_more_data);
            return;
        }
        getPresenter().getMaintenanceList(MaintenanceConstant.SIX,mPage.nextPage(),mConditionBean,false);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onRefresh();
    }

    public void refreshSuccessUI(List<MaintenanceEnity.MaintenanceListEnity> ms, Page page, boolean refresh) {
        if (page != null) {
            this.mPage = page;
        }
        for (MaintenanceEnity.MaintenanceListEnity m : ms) {
            m.choice = 0;
        }
        if (refresh) {
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(ms);
            mRefreshLayout.finishRefresh();
        } else {
            mAdapter.addData(ms);
            mRefreshLayout.finishLoadMore();
        }
        if (mAdapter.getData().size() == 0) {
            mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
        }
        dismissLoading();
    }

    public void refreshErrorUI() {
        mAdapter.setEmptyView(getErrorView(mRefreshLayout));
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
        dismissLoading();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        KeyboardUtils.hideSoftInput(getActivity());
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        SelectDataBean bean = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK);
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (bean == null) {
                    mSiteEt.setText("");
                } else {
                    mSiteEt.setText(StringUtils.formatString(bean.getFullName()));
                    mConditionBean.location=bean.getLocation();
                    mConditionBean.location.roomId=null;
                    mConditionBean.location.floorId=null;
                    mConditionBean.location.cityId=null;
                }
                break;
        }
    }

    public void setPriorityAs(List<AttachmentBean> priorityAs) {
        mCycleAs.clear();
        if (priorityAs != null) {
            mCycleAs.addAll(priorityAs);
        }
        mCycleTagAdapter.notifyDataSetChanged();
    }

    public void setPriority(Map<Long, String> priority) {
//        mAdapter.setPriority(priority);
    }

    public static MaintenanceQueryFragment getInstance() {
        return new  MaintenanceQueryFragment();
    }

    public static  MaintenanceQueryFragment getInstance(boolean my) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MY_REPAIR, my);
        MaintenanceQueryFragment workorderQueryFragment = new  MaintenanceQueryFragment();
        workorderQueryFragment.setArguments(bundle);
        return workorderQueryFragment;
    }
}