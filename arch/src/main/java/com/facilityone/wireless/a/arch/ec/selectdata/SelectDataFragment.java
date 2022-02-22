package com.facilityone.wireless.a.arch.ec.selectdata;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.R;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.a.arch.ec.utils.RecyclerViewUtil;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.SiteDao;
import com.facilityone.wireless.a.arch.widget.SearchBox;
import com.facilityone.wireless.basiclib.utils.PinyinUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description: 数据选择页面
 * Date: 2018/10/25 11:03 AM
 */
public class SelectDataFragment extends BaseFragment<SelectDataPresenter> implements BaseQuickAdapter.OnItemChildClickListener {

    private RecyclerView mRecyclerView;
    private SearchBox mSearchBox;

    private static final String DATA_TITLE = "data_title";
    private static final String DATA_TYPE = "data_type";
    private static final String DATA_PARENT_ID = "data_parent_id";
    private static final String DATA_DEP = "data_dep";
    private static final String DATA_SERVICE_TYPE = "data_service_type";
    private static final String DATA_WORKORDER_TYPE_ID = "data_workorder_type_id";
    private static final String DATA_LOCATION = "data_location";
    private static final String DATA_LOCATION_NAME = "data_location_name";
    private static final String PATROL_ITEM_EXCEPTION = "patrol_item_exception";
    private static final String REASON_TYPE="reason_type";

    private SelectDataAdapter mAdapter;
    private int mFromType;
    private int mReasonType;
    private SelectDataBean mDepSelectData;
    private SelectDataBean mServiceTypeSelectData;
    private Long mWorkorderType;
    private LocationBean mLocation;
    private List<SelectDataBean> mTotal;
    private List<SelectDataBean> mTotalLocation;
    private List<SelectDataBean> mShow;
    private SelectDataBean mBackBean;
    private int mLevel;
    private Long mParentId;
    private String mTitle;
    private LocationBean mLocationBean;
    private Map<Integer, List<SelectDataBean>> mListMap;
    private Map<Integer, SelectDataBean> mDataBeanMap;
    private boolean back;
    private String initTitle;
    private String mLocationName;
    private List<Long> mParentIds;
    private boolean inShowAllLocation;
    private boolean mShowSite = false; //此变量控制是否显示位置的site层级 true-显示， false-不显示
    private List<SelectDataBean> firstList; //第一层级列表数据
    private List<SelectDataBean> secondList; //第二层级列表数据
    private List<SelectDataBean> secondAllList; //第二级根据Id查询的Id
    private List<SelectDataBean> allList; //所有数据
    private Long localId; //当前点击后的Id
    private Integer localPostion;//当前选择的位置
    private String newOrderIndex ; //新派工单点击的位置
    @Override
    public SelectDataPresenter createPresenter() {
        return new SelectDataPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_arch_select_data;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
        getData();
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mFromType = arguments.getInt(DATA_TYPE);
            mReasonType = arguments.getInt(REASON_TYPE);
            mDepSelectData = arguments.getParcelable(DATA_DEP);
            mParentId = arguments.getLong(DATA_PARENT_ID, -1L);
            mTitle = arguments.getString(DATA_TITLE, "");
            mServiceTypeSelectData = arguments.getParcelable(DATA_SERVICE_TYPE);
            mWorkorderType = arguments.getLong(DATA_WORKORDER_TYPE_ID);
            mLocation = arguments.getParcelable(DATA_LOCATION);
            mLocationName = arguments.getString(DATA_LOCATION_NAME, "");
            if (!TextUtils.isEmpty(arguments.getString(ISelectDataService.SELECT_NEWORDER_POSITION))){
                newOrderIndex = arguments.getString(ISelectDataService.SELECT_NEWORDER_POSITION); // 新派工单点击的位置
            }

        }
        mLocationBean = new LocationBean();
        mListMap = new HashMap<>();
        mDataBeanMap = new HashMap<>();
        if (mFromType == ISelectDataService.DATA_TYPE_LOCATION) {
            getPresenter().queryAllLocation(mShowSite);
            if (mParentId == -1) {
                SiteDao siteDao = new SiteDao();
                List<SelectDataBean> locationSites = siteDao.queryLocationSites(true);
                if (locationSites != null && locationSites.size() > 0) {
                    mParentId = locationSites.get(0).getId();
                    mLocationBean.siteId = mParentId;
                    mLocationBean.buildingId = null;
                    mLocationBean.floorId = null;
                    mLocationBean.roomId = null;
                }
            }
        }else if (mFromType==ISelectDataService.DATA_TYPE_REASON){
            getPresenter().queryReason(mReasonType);
        }else if (mFromType == ISelectDataService.DATA_TYPE_INVALIDD){
            getPresenter().queryReason(mReasonType);
        }else if (mFromType == ISelectDataService.DATA_TYPE_FAULT_OBJECT){
            getPresenter().queryReason(mReasonType);
        }else if (mFromType == ISelectDataService.DATA_TYPE_SPECIALTY){
            getPresenter().getProfessional();
        } else if (mFromType == ISelectDataService.DATA_TYPE_LOCATION_BOARDING){
            getPresenter().queryAllLocation(mShowSite);
            if (mParentId == -1) {
                SiteDao siteDao = new SiteDao();
                List<SelectDataBean> locationSites = siteDao.queryLocationSites(true);
                if (locationSites != null && locationSites.size() > 0) {
                    mParentId = locationSites.get(0).getId();
                    mLocationBean.siteId = mParentId;
                    mLocationBean.buildingId = null;
                    mLocationBean.floorId = null;
                    mLocationBean.roomId = null;
                }
            }
        }
    }

    private void initView() {
//        setSwipeBackEnable(false);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSearchBox = findViewById(R.id.search_box);
        mSearchBox.setOnSearchBox(new SearchBox.OnSearchBox() {
            @Override
            public void onSearchTextChanged(String curCharacter) {
                //搜索
                if (TextUtils.isEmpty(curCharacter)) {
                    mAdapter.setShowSubTitle(false);
                    if (mTotal != null) {
                        for (SelectDataBean selectDataBean : mTotal) {
                            selectDataBean.setStart(0);
                            selectDataBean.setEnd(0);
                            selectDataBean.setSubStart(0);
                            selectDataBean.setSubEnd(0);
                        }
                        refreshHaveData(mTotal, true);
                    }
                    inShowAllLocation = false;
                    return;
                }
                if (mFromType == ISelectDataService.DATA_TYPE_LOCATION) {
                    if (mTotalLocation == null) {
                        refreshHaveData(mTotalLocation, false);
                        return;
                    }
                    mAdapter.setShowSubTitle(true);
                    inShowAllLocation = true;
                    getPresenter().filter(mFromType, curCharacter, mTotalLocation);
                } else {
                    if (mTotal == null) {
                        refreshHaveData(mTotal, true);
                        return;
                    }
                    getPresenter().filter(mFromType, curCharacter, mTotal);
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mShow = new ArrayList<>();
        mAdapter = new SelectDataAdapter(mShow, mFromType);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setEmptyView(getLoadingView((ViewGroup) mRecyclerView.getParent()));
        //        if(mFromType != ISelectDataService.DATA_TYPE_EQU && mFromType != ISelectDataService.DATA_TYPE_EQU_ALL) {
        //            setRightTextButton(R.string.arch_confirm, R.id.select_data_back_id);
        //        }
    }

    @Override
    public void onRightTextMenuClick(View view) {
        setBackResult(true);
    }

    public void getData() {
        initTitle = getString(R.string.arch_data_select);
        if (mFromType == ISelectDataService.DATA_TYPE_LOCATION && !mShowSite) {
            mLevel = 1;
        } else {
            mLevel = 0;
        }
        back = false;
        queryDb();
    }

    private void queryDb() {
        if (back) {
            mListMap.put(mLevel, null);
            mLevel--;
            mBackBean = mDataBeanMap.get(mLevel - 1);
            if (mBackBean != null) {
                setLocationBean(mLevel - 1, mBackBean.getId());
            }
        } else {
            mBackBean = mDataBeanMap.get(mLevel);
            mLevel++;
        }
        if (mFromType == ISelectDataService.DATA_TYPE_LOCATION) {
            if (mLevel == 1 || mLevel == 2) {
                removeRightView();
            } else {
                removeRightView();
                setRightTextButton(R.string.arch_confirm, R.id.select_data_back_id);
            }
        } else {
            if (mLevel == 1) {
                removeRightView();
            } else {
                removeRightView();
                setRightTextButton(R.string.arch_confirm, R.id.select_data_back_id);
            }
        }
        if (mBackBean != null) {
            if (mParentIds == null) {
                mParentIds = new ArrayList<>();
            }
            mParentIds.clear();
            mParentIds.addAll(mBackBean.getParentIds());
            mParentId = mBackBean.getId();
        }

        List<SelectDataBean> selectDataBeen = mListMap.get(mLevel);
        if (selectDataBeen != null) {
            LogUtils.d("本地缓存");
            setTotal(selectDataBeen);
            refreshHaveData(selectDataBeen, true);
        } else {
            LogUtils.d("数据库");
            switch (mFromType) {
                case ISelectDataService.DATA_TYPE_LOCATION:
                    initTitle = getString(R.string.arch_location);
                    getPresenter().queryLocation(mLevel, mParentId);
                    break;
                case ISelectDataService.DATA_TYPE_EQU://根据位置选取设备
                    initTitle = getString(R.string.arch_device);
                    getPresenter().queryEqu(mLocation, mLocationName);
                    break;
                case ISelectDataService.DATA_TYPE_EQU_ALL://全部设备
                    initTitle = getString(R.string.arch_device);
                    getPresenter().queryEqu();
                    break;
                case ISelectDataService.DATA_TYPE_DEP:
                    initTitle = getString(R.string.arch_org);
                    getPresenter().queryDep(mLevel, mParentId, mParentIds);
                    break;
                case ISelectDataService.DATA_TYPE_SERVICE_TYPE:
                    initTitle = getString(R.string.arch_service_stype);
                    getPresenter().queryServiceType(mLevel, mParentId, mParentIds);
                    break;
                case ISelectDataService.DATA_TYPE_KNOWLEDGE:
                    initTitle = TextUtils.isEmpty(mTitle) ? getString(R.string.arch_knowledge_classify) : mTitle;
                    getPresenter().queryKnowledgeType(mLevel, mParentId);
                    break;
                case ISelectDataService.DATA_TYPE_DEMAND_TYPE:
                    initTitle = getString(R.string.arch_demand_type);
                    getPresenter().queryDemandType(mLevel, mParentId);
                    break;
                case ISelectDataService.DATA_TYPE_EQU_TYPE:
                    initTitle = getString(R.string.arch_system_type);
                    getPresenter().queryEquType(mLevel, mParentId);
                    break;
                case ISelectDataService.DATA_TYPE_FLOW_PRIORITY:
                    initTitle = getString(R.string.arch_priority);
                    getPresenter().queryFlowPriority(mDepSelectData
                            , mServiceTypeSelectData
                            , mWorkorderType
                            , mLocation);
                    break;
                case ISelectDataService.DATA_TYPE_PRIORITY:
                    initTitle = getString(R.string.arch_priority);
                    getPresenter().queryPriority();
                    break;
                case ISelectDataService.DATA_TYPE_WORKORDER_TYPE:
                    initTitle = getString(R.string.arch_order_type);
                    List<SelectDataBean> t = new ArrayList<>();
                    SelectDataBean self = new SelectDataBean();
                    self.setId(0L);
                    self.setName(getString(R.string.arch_order_type_self));
                    self.setFullName(getString(R.string.arch_order_type_self));
                    self.setNamePinyin(PinyinUtils.ccs2Pinyin(self.getName()));
                    self.setNameFirstLetters(PinyinUtils.getPinyinFirstLetters(self.getName()));
                    self.setFullNamePinyin(PinyinUtils.ccs2Pinyin(self.getFullName()));
                    self.setFullNameFirstLetters(PinyinUtils.getPinyinFirstLetters(self.getFullName()));
                    t.add(self);
                    SelectDataBean plan = new SelectDataBean();
                    plan.setId(1L);
                    plan.setName(getString(R.string.arch_order_type_correct));
                    plan.setFullName(getString(R.string.arch_order_type_correct));
                    plan.setNamePinyin(PinyinUtils.ccs2Pinyin(plan.getName()));
                    plan.setNameFirstLetters(PinyinUtils.getPinyinFirstLetters(plan.getName()));
                    plan.setFullNamePinyin(PinyinUtils.ccs2Pinyin(plan.getFullName()));
                    plan.setFullNameFirstLetters(PinyinUtils.getPinyinFirstLetters(plan.getFullName()));
                    t.add(plan);
                    setTotal(t);
                    refreshHaveData(t, true);
                    break;
                case ISelectDataService.DATA_TYPE_PATROL_EXCEPTION://巡检异常
                    initTitle = getString(R.string.arch_exception_select);
                    Bundle arguments = getArguments();
                    List<SelectDataBean> temp = null;
                    if (arguments != null) {
                        temp = arguments.getParcelableArrayList(PATROL_ITEM_EXCEPTION);
                    }
                    for (SelectDataBean selectData : temp) {
                        selectData.setNamePinyin(PinyinUtils.ccs2Pinyin(selectData.getName()));
                        selectData.setNameFirstLetters(PinyinUtils.getPinyinFirstLetters(selectData.getName()));
                        selectData.setFullNamePinyin(PinyinUtils.ccs2Pinyin(selectData.getFullName()));
                        selectData.setFullNameFirstLetters(PinyinUtils.getPinyinFirstLetters(selectData.getFullName()));
                    }
                    setTotal(temp);
                    refreshHaveData(temp, true);
                    break;
                case ISelectDataService.DATA_VISIT_PAY:
                    initTitle = getString(R.string.arch_visit_pay);
                    getPresenter().getVisitPay();
                    break;
                case ISelectDataService.DATA_TYPE_LOCATION_BOARDING:
                    initTitle = "选择站点";
                    getPresenter().queryLocation( mParentId);
                    break;
            }
        }

        if (mBackBean != null) {
            setTitle(StringUtils.formatString(mBackBean.getName()));
        } else {
            setTitle(initTitle);
        }
        mSearchBox.clearSearchBox();
    }

    private void setLocationBean(int level, Long id) {
        if (mFromType == ISelectDataService.DATA_TYPE_LOCATION) {
            switch (level) {
                case ISelectDataService.LOCATION_SITE:
                    mLocationBean.siteId = id;
                    mLocationBean.buildingId = null;
                    mLocationBean.floorId = null;
                    mLocationBean.roomId = null;
                    break;
                case ISelectDataService.LOCATION_BUILDING:
                    mLocationBean.buildingId = id;
                    mLocationBean.floorId = null;
                    mLocationBean.roomId = null;
                    break;
                case ISelectDataService.LOCATION_FLOOR:
                    mLocationBean.floorId = id;
                    mLocationBean.roomId = null;
                    break;
                case ISelectDataService.LOCATION_ROOM:
                    mLocationBean.roomId = id;
                    break;
            }
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (mFromType==ISelectDataService.DATA_TYPE_REASON ||
                mFromType==ISelectDataService.DATA_TYPE_INVALIDD ||
                mFromType == ISelectDataService.DATA_TYPE_FAULT_OBJECT){

            if (mFromType == ISelectDataService.DATA_TYPE_FAULT_OBJECT
                    || mFromType == ISelectDataService.DATA_TYPE_REASON){
                if (mLevel <1) {
                    removeRightView();
                } else {
                    removeRightView();
                    setRightTextButton(R.string.arch_confirm, R.id.select_data_back_id);
                }
            }
            localPostion = position;
            secondAllList = new ArrayList<>();
            SelectDataBean beanOne = mShow.get(position);
            if (checkSecond(beanOne.getId(),secondList)){
                for (SelectDataBean second : secondList) {
                    if (second.getParentId().equals(beanOne.getId())){
                        secondAllList.add(second);
                    }
                }
                mLevel ++;
                localId = beanOne.getId();
                RecyclerViewUtil.setHeightMatchParent(false, mRecyclerView);
                mAdapter.replaceData(secondAllList);
                mAdapter.notifyDataSetChanged();
            }else {
                back = false;
                SelectDataBean bean = mShow.get(position);
                mBackBean = bean;
                setBackResult(false);
            }
        }else {
            back = false;
            SelectDataBean bean = mShow.get(position);
            mBackBean = bean;
            mDataBeanMap.put(mLevel, bean);
            setLocationBean(mLevel, mBackBean.getId());
            if (bean.getHaveChild()) {
                queryDb();
                return;
            }
            mListMap.clear();
            mListMap = null;
            mDataBeanMap.clear();
            mDataBeanMap = null;
            setBackResult(false);
        }
    }

     /**
      * @Auther: karelie
      * @Date: 2021/8/16
      * @Infor: 查询是否存在二级选项
      */
     public boolean checkSecond(Long Id,List<SelectDataBean> list){
         for (SelectDataBean listChcek : list) {
             if (listChcek.getParentId().equals(Id)) {
                 return true;
             }
         }
         return false;
     }

    /**
     * @param selectDataBeen
     * @param isSave         是否将selectDataBeen保存到mListMap中
     */
    public void refreshHaveData(List<SelectDataBean> selectDataBeen, boolean isSave) {
        if (isSave && mListMap != null) {
            mListMap.put(mLevel, selectDataBeen);
        }
        mShow.clear();
        if (selectDataBeen != null && selectDataBeen.size() > 0) {
            RecyclerViewUtil.setHeightMatchParent(false, mRecyclerView);
            mShow.addAll(selectDataBeen);
            mAdapter.notifyDataSetChanged();
        } else {
            RecyclerViewUtil.setHeightMatchParent(true, mRecyclerView);
            mAdapter.setEmptyView(getNoDataView((ViewGroup) mRecyclerView.getParent()));
        }

    }


    @Override
    public void leftBackListener() {
        mSearchBox.clearSearchBox();
        if (mFromType == ISelectDataService.DATA_TYPE_LOCATION && !mShowSite && mLevel ==2 ) {
            mLevel--;
        }
        if (mLevel == 1) {
            if (mFromType==ISelectDataService.DATA_TYPE_REASON
                    || mFromType==ISelectDataService.DATA_TYPE_INVALIDD
                    || mFromType==ISelectDataService.DATA_TYPE_FAULT_OBJECT
                       ){
                setBackResult(false);
            }else {
                super.leftBackListener();
            }

        } else {
            if (mFromType==ISelectDataService.DATA_TYPE_REASON
                    || mFromType==ISelectDataService.DATA_TYPE_INVALIDD
                    || mFromType==ISelectDataService.DATA_TYPE_FAULT_OBJECT){
                mShow.clear();
                mLevel--;
//                getPresenter().queryReason(mReasonType);
                mAdapter.replaceData(getLastList(localId));
                mAdapter.notifyDataSetChanged();
                if (mLevel < 2 && (mFromType == ISelectDataService.DATA_TYPE_FAULT_OBJECT
                        || mFromType == ISelectDataService.DATA_TYPE_REASON))
                {
                    removeRightView();
                }
            }else {
                back = true;
                queryDb();
            }
        }
    }

    public List<SelectDataBean> getLastList(Long localId){
        List<SelectDataBean> lastList = new ArrayList<>();
        if (mLevel == 1){
            for (SelectDataBean data : allList) {
                if (data.getParentId()==null){
                    lastList.add(data);
                }
            }
        }else {
            for (SelectDataBean data : allList) {
                if (data.getParentId()!=null && data.getId()== localId){
                    lastList.add(data);
                }
            }
        }


        return lastList;
    }

    @Override
    public boolean onBackPressedSupport() {
        if (mFromType == ISelectDataService.DATA_TYPE_LOCATION && !mShowSite && mLevel == 2) {
            mLevel--;
        }

        if (mLevel == 1) {
            if (mFromType==ISelectDataService.DATA_TYPE_REASON
                    || mFromType==ISelectDataService.DATA_TYPE_INVALIDD
                    || mFromType==ISelectDataService.DATA_TYPE_FAULT_OBJECT){
                setBackResult(false);
                return true;
            }else {
                return super.onBackPressedSupport();
            }

        } else {
            if (mFromType==ISelectDataService.DATA_TYPE_REASON
                    || mFromType==ISelectDataService.DATA_TYPE_INVALIDD
                    || mFromType==ISelectDataService.DATA_TYPE_FAULT_OBJECT){
                mShow.clear();
                mLevel--;
//                getPresenter().queryReason(mReasonType);
                mAdapter.replaceData(getLastList(localId));
                mAdapter.notifyDataSetChanged();
                if (mLevel < 2 && (mFromType == ISelectDataService.DATA_TYPE_FAULT_OBJECT
                        || mFromType == ISelectDataService.DATA_TYPE_REASON))
                {
                    removeRightView();
                }
            }else {
                back = true;
                queryDb();
            }
            return true;
        }
    }


    /**
     * @param needChange 传值方法 区分确定和item点击
     * */
    private void setBackResult(boolean needChange) {
        Bundle bundle = new Bundle();
        if (mBackBean != null && mFromType == ISelectDataService.DATA_TYPE_LOCATION) {
            if (!inShowAllLocation) {
                mBackBean.setLocation(mLocationBean);
            } else {
                if (mBackBean.getLocation() == null) {
                    mBackBean.setLocation(mLocationBean);
                }
            }
        }else if (needChange && (mFromType == ISelectDataService.DATA_TYPE_FAULT_OBJECT
                || mFromType == ISelectDataService.DATA_TYPE_REASON)){
            back = false;
            SelectDataBean bean = getSelectData(localId,allList);
            mBackBean = bean;
        }
        if (newOrderIndex != null){
            bundle.putString(ISelectDataService.SELECT_NEWORDER_POSITION,newOrderIndex);
        }
        bundle.putParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK, mBackBean);
        setFragmentResult(RESULT_OK, bundle);
        pop();
    }

    public SelectDataBean getSelectData(Long id,List<SelectDataBean> list){
        SelectDataBean data = new SelectDataBean();
        for (SelectDataBean dataBean : list) {
            if (dataBean.getId() == id){
                data = dataBean;
            }
        }
        return data;
    }

    public void setTotal(List<SelectDataBean> total) {
        mTotal = total;
        allList = total;
    }

    public void getReasonAll(List<SelectDataBean> total){
        firstList = new ArrayList<>();
        secondList = new ArrayList<>();
        for (SelectDataBean first : total) {
            if (first.getParentId() == null){
                firstList.add(first);
            }else {
                secondList.add(first);
            }
        }

        if (total != null && total.size() > 0) {
            RecyclerViewUtil.setHeightMatchParent(false, mRecyclerView);
            mShow.addAll(firstList);
            mAdapter.notifyDataSetChanged();
        } else {
            RecyclerViewUtil.setHeightMatchParent(true, mRecyclerView);
            mAdapter.setEmptyView(getNoDataView((ViewGroup) mRecyclerView.getParent()));
        }

    }

    public void setTotalLocation(List<SelectDataBean> totalLocation) {
        this.mTotalLocation = totalLocation;
    }

    @Override
    public void onDestroyView() {
        if (getPresenter().mCompositeDisposable != null) {
            getPresenter().mCompositeDisposable.clear();
        }
        super.onDestroyView();
    }

    public static SelectDataFragment getInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type, Long parentId) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        bundle.putLong(DATA_PARENT_ID, parentId == null ? -1L : parentId);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type, Long parentId, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        bundle.putLong(DATA_PARENT_ID, parentId);
        bundle.putString(DATA_TITLE, title);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type, ArrayList<SelectDataBean> data) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        bundle.putParcelableArrayList(PATROL_ITEM_EXCEPTION, data);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type, LocationBean locationBean, String locationName) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        bundle.putParcelable(DATA_LOCATION, locationBean);
        bundle.putString(DATA_LOCATION_NAME, locationName);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type
            , SelectDataBean depSelectData
            , SelectDataBean serviceTypeSelectData
            , Long workorderTypeId
            , LocationBean locationBean) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        if (depSelectData != null) {
            bundle.putParcelable(DATA_DEP, depSelectData);
        }
        if (serviceTypeSelectData != null) {
            bundle.putParcelable(DATA_SERVICE_TYPE, serviceTypeSelectData);
        }
        if (workorderTypeId != null) {
            bundle.putLong(DATA_WORKORDER_TYPE_ID, workorderTypeId);
        }
        bundle.putParcelable(DATA_LOCATION, locationBean);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type
            , SelectDataBean depSelectData
            , SelectDataBean serviceTypeSelectData
            , Long workorderTypeId
            , LocationBean locationBean,String index) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        if (depSelectData != null) {
            bundle.putParcelable(DATA_DEP, depSelectData);
        }
        if (serviceTypeSelectData != null) {
            bundle.putParcelable(DATA_SERVICE_TYPE, serviceTypeSelectData);
        }
        if (workorderTypeId != null) {
            bundle.putLong(DATA_WORKORDER_TYPE_ID, workorderTypeId);
        }
        bundle.putParcelable(DATA_LOCATION, locationBean);
        bundle.putString(ISelectDataService.SELECT_NEWORDER_POSITION,index);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type,int reasonType) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        bundle.putInt(REASON_TYPE,reasonType);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static SelectDataFragment getInstance(int type,String index) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_TYPE, type);
        bundle.putString(ISelectDataService.SELECT_NEWORDER_POSITION,index);
        SelectDataFragment instance = new SelectDataFragment();
        instance.setArguments(bundle);
        return instance;
    }

}
