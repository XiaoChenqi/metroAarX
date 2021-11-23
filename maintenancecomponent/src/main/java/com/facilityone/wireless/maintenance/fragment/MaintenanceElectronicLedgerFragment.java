package com.facilityone.wireless.maintenance.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import com.facilityone.wireless.a.arch.ec.adapter.GridTagAdapter;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.maintenance.R;
import com.facilityone.wireless.maintenance.adapter.ElectronicLedgerAdapter;
import com.facilityone.wireless.maintenance.adapter.MaintenanceListAdapter;
import com.facilityone.wireless.maintenance.model.AccountCheck;
import com.facilityone.wireless.maintenance.model.CheckContentModel;
import com.facilityone.wireless.maintenance.model.CheckModel;
import com.facilityone.wireless.maintenance.model.MaintenanceConstant;
import com.facilityone.wireless.maintenance.model.MaintenanceEnity;
import com.facilityone.wireless.maintenance.model.MaintenanceService;
import com.facilityone.wireless.maintenance.model.SelectorModel;
import com.facilityone.wireless.maintenance.presenter.MaintenanceELPresenter;
import com.facilityone.wireless.maintenance.presenter.MaintenanceListPresenter;
import com.google.gson.Gson;
import com.luojilab.component.componentlib.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
  * @Auther: karelie
  * @Date: 2021/8/17
  * @Infor: 维护工单通用列表
  */
public class MaintenanceElectronicLedgerFragment extends BaseFragment<MaintenanceELPresenter> implements BaseQuickAdapter.OnItemClickListener, OnRefreshLoadMoreListener{
    private static final String LIST_TYPE = "list_type";


    private static final int MAINTENANCE_INFO = 4001;
    private static final int REQUEST_LOCATION = 20001;

    public static final int FAULT_DEVICE = 4007;
    public static final int TOOLS = 4008;
    public static final int CHARGE = 4009;
    public static final int STEP = 4010;
    public static final int SPACE_LOCATION = 4011;
    public static final int PAYMENT = 4012;
    private final static int REQUEST_REASON = 20007;
    private final static int REQUEST_INVALID = 20008;

    private Integer mType;


    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private List<MaintenanceEnity.MaintenanceListEnity> mList;
    private String localWoId = null; //当前选中的Id
    private Page mPage;



    private static final int MAX_NUMBER = 3;//一行显示几个tag
    ElectronicLedgerAdapter mElAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
//        initOnClick();
//        getData();
    }

    private void getData() {
//        showLoading();
//        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
//        getPresenter().getMaintenanceList(mType,mPage,null);
//        getPresenter().getList();
    }

    private void initOnClick() {


    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(LIST_TYPE, -1);
        }
        setTitle("电子台账");
        getData();

    }

    public void refreshSuccessUI(List<MaintenanceEnity.MaintenanceListEnity> ms) {
        dismissLoading();
        for (MaintenanceEnity.MaintenanceListEnity m : ms) {
            m.choice = 0;
        }
//        mAdapter.replaceData(ms);
//        if (ms != null) {
//            localWoId = ms.get(0).code; //默认设置匹配Id为列表第一个元素的Id
//        }
//        mAdapter.notifyDataSetChanged();
    }
    //刷新页面
    private void onRefresh() {
//        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
    }


    public void noDataRefresh() {
//        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setRightTextButton("提交",R.id.maintenance_ele_uoload_menu_id);
        List<MaintenanceEnity.ElectronicLedgerEntity> datas=new ArrayList<>();
        mElAdapter=new ElectronicLedgerAdapter(datas);
        mRecyclerView.setItemViewCacheSize(200);
        mRecyclerView.setAdapter(mElAdapter);
        mockData();





    }

    public void mockData(){
        List<MaintenanceEnity.ElectronicLedgerEntity> datas=new ArrayList<>();
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(1,"电源系统",0));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("两路电源进线验收情况",0,0)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("双电源切换功验收情况",0,0)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("后备电源自动投用验收情况",0,0)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("后备电源电池性能正常",0,0)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("控制与驱动电源运行正常",0,0)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(5,"年检",0));
        //年检
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("蓄电池放电试验满足上下行各3次开关门",0,0)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("进线电缆绝缘＞0.5Ω",0,0)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("门机绝缘电阻＞0.5Ω",0,0)));
        /**门机系统**/
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(1,"门机系统",1));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("门体绝缘验收情况",0,1)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("门体结构验收情况",0,1)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("门锁紧机构状态验收情况",0,1)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("传动机构(电机等)功能验收情况",0,1)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("导轨、地槛状态验收情况",0,1)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(5,"季检",1));
        //季检
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("活动门闭门时间隙≤10mm",0,1)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(5,"年检",1));
        //年检
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("活动门关门力＜150N",0,1)));
        /**监控系统**/
        //季检
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(1,"监控系统",2));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(5,"季检",2));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("PSA验收情况",0,2)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("PEC验收情况",0,2)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("PSC验收情况",0,2)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("PSL验收情况",0,2,"(需抽查上下行各1个控制箱，并在备注栏注明具体位置)")));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("LCB验收情况",0,2,"(需抽查上下行各4扇门，并在备注栏注明具体位置)")));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("互锁解除开关验收情况",0,2)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("信号旁路装置状态验收情况",0,2)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("报警提示/状态指示设备验收情况",0,2)));
        /**外围设备**/
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(1,"外围设备",3));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("红外线探测装置运行正常",0,3)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("防夹人灯柱运行正常",0,3)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("滑动门防夹板验收情况",0,3)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("防踏空橡胶条验收情况",0,3)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("防踏空提示灯验收情况",0,3)));
        /**工器具**/
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(1,"工器具",4));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(4,new SelectorModel("万用表",0, new SelectorModel("是否合格有效",0,null),4)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(4,new SelectorModel("兆欧表",0, new SelectorModel("是否合格有效",0,null),4)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(4,new SelectorModel("测力计/测拉力计",0, new SelectorModel("是否合格有效",0,null),4)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(4,new SelectorModel("塞尺/卡尺",0, new SelectorModel("是否合格有效",0,null),4)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("套筒扳手",1,4)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("三角钥匙",1,4)));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(2,new SelectorModel("常用工器具",1,4)));





        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(1,"备注",5));
        datas.add(new MaintenanceEnity.ElectronicLedgerEntity(3,"工器具",5));


        mElAdapter.setNewData(datas);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MaintenanceEnity.MaintenanceListEnity workorderItemBean = ((MaintenanceListAdapter) adapter).getData().get(position);
        localWoId = workorderItemBean.code;
        if (workorderItemBean.choice == MaintenanceConstant.CHOICE_NO) {
            Integer status = workorderItemBean.status;
            Long woId = workorderItemBean.woId;
            String code = workorderItemBean.code;
            Router router = Router.getInstance();
            WorkorderService workorderService = (WorkorderService) router.getService(WorkorderService.class.getSimpleName());
            if (workorderService != null) {
                BaseFragment fragment;
                if (mType==MaintenanceConstant.FIVE){
                    fragment = workorderService.getWorkorderInfoFragment(status,code,woId,true,true);
                }else {
                     fragment = workorderService.getWorkorderInfoFragment(status,code,woId,true);
                }
                startForResult(fragment,MAINTENANCE_INFO);
            }
        }


    }

    @Override
    public void onRightTextMenuClick(View view) {
        super.onRightTextMenuClick(view);



        int viewId = view.getId();
         if (viewId==R.id.maintenance_ele_uoload_menu_id){
            List<MaintenanceEnity.ElectronicLedgerEntity> list=mElAdapter.getData();
            boolean isUpload=false;
            for (MaintenanceEnity.ElectronicLedgerEntity temp:list) {
                if (temp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO)
                {
                    if (TextUtils.isEmpty(temp.value)){
                        ToastUtils.showLong("有类目未选择");
                        isUpload=false;
                        break;

                    }
                }else if (temp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO_SUB){
                    if (TextUtils.isEmpty(temp.value)||TextUtils.isEmpty(temp.subValue)){
                        ToastUtils.showLong("有类目未选择");
                        isUpload=false;
                        break;

                    }
                }
                isUpload=true;
            }
            if (isUpload){
                List<CheckModel> checkModels=new ArrayList<>();

                for (MaintenanceEnity.ElectronicLedgerEntity titleItem:list){
                    if (titleItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_HEADER){
                        CheckModel tempItem=new CheckModel();
                        tempItem.setTitle(titleItem.content.toString());
                        //同一组对象数组
                        List<CheckContentModel> checkContentModels=new ArrayList<>();
                        for (MaintenanceEnity.ElectronicLedgerEntity innerTemp:list){
                            if (innerTemp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_HEADER||innerTemp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT){
                            }

                            //次标题
                            else if (innerTemp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_SUB_HEADER){
                                if (innerTemp.parent==(titleItem.parent)){
                                    CheckContentModel tempContent=new CheckContentModel();
                                    tempContent.setName(innerTemp.content.toString());
                                    checkContentModels.add(tempContent);
                                }

                            }
                            else {
                                SelectorModel contentModel= (SelectorModel) innerTemp.content;

                                if (contentModel.getPid().equals(titleItem.parent)){
                                    //单行文本+单选组
                                    if (innerTemp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO){
                                            CheckContentModel tempContent=new CheckContentModel();
                                            tempContent.setName(contentModel.getName());

                                            if (contentModel.getValue()==0){
                                                tempContent.setValue("正常");
                                                tempContent.setOtherValue("异常");

                                            }else {
                                                tempContent.setValue("携带");
                                                tempContent.setOtherValue("未携带");
                                            }
                                        if (contentModel.getState()==0){
                                            tempContent.setSelectedValue(tempContent.getValue());
                                        }else if (contentModel.getState()==1){
                                            tempContent.setSelectedValue(tempContent.getOtherValue());
                                        }
                                            checkContentModels.add(tempContent);
                                        if (!TextUtils.isEmpty(contentModel.getTips())){
                                            CheckContentModel tipsContent=new CheckContentModel();
                                            tipsContent.setName(contentModel.getTips());
                                            checkContentModels.add(tipsContent);
                                        }
                                    //单行文本+单选组+子单选组
                                    }else if (innerTemp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO_SUB){
                                        CheckContentModel tempContent=new CheckContentModel();
                                        tempContent.setName(contentModel.getName()+"sameLine_是否合格有效：");
                                        tempContent.setValue("携带sameLine_是");
                                        tempContent.setOtherValue("未携带sameLine_否");
                                        if (contentModel.getState()==0){
                                            if (contentModel.getSub().getState()==0){
                                                tempContent.setSelectedValue("携带sameLine_是");
                                            }else if (contentModel.getSub().getState()==1){
                                                tempContent.setSelectedValue("携带sameLine_否");
                                            }
                                        }else {
                                            if (contentModel.getSub().getState()==0){
                                                tempContent.setSelectedValue("未携带sameLine_是");
                                            }else if (contentModel.getSub().getState()==1){
                                                tempContent.setSelectedValue("未携带sameLine_否");
                                            }
                                        }

                                        checkContentModels.add(tempContent);
                                    //单行文本判断
                                    } else if (innerTemp.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_SUB_HEADER){
                                        CheckContentModel tempContent=new CheckContentModel();
                                        tempContent.setName(contentModel.getName());

                                    }
                                }
                            }
                        }
                        tempItem.setContents(checkContentModels);
                        checkModels.add(tempItem);
                    //输入区域判断
                    } else if ( titleItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT){
                            CheckModel tempItem=new CheckModel();
                            tempItem.setRemark(titleItem.value);
                            checkModels.add(tempItem);
                        }
                    }

                getPresenter().pushAccountCheck(new AccountCheck(checkModels));

//                pop();
            }



        }

     }

     @Override
    public Object setLayout() {
        return R.layout.fragment_maintenance_electronic_ledger;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public MaintenanceELPresenter createPresenter() {
        return new MaintenanceELPresenter();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

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

    }


    public static MaintenanceElectronicLedgerFragment getInstance(Integer type) {
        Bundle bundle = new Bundle();
        bundle.putInt(LIST_TYPE, type);
        MaintenanceElectronicLedgerFragment instance = new MaintenanceElectronicLedgerFragment();
        instance.setArguments(bundle);
        return instance;
    }


    public void onUploadSucces(){
        ToastUtils.showShort("提交成功");
        pop();
    }




}
