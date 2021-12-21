package com.facilityone.wireless.patrol.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.OrdersBean;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.PatrolSpotDao;
import com.facilityone.wireless.a.arch.offline.dao.PatrolTaskDao;
import com.facilityone.wireless.a.arch.offline.model.entity.DBPatrolConstant;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolEquEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolItemEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolPicEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolSpotEntity;
import com.facilityone.wireless.a.arch.offline.model.entity.PatrolTaskEntity;
import com.facilityone.wireless.a.arch.offline.model.service.PatrolDbService;
import com.facilityone.wireless.a.arch.offline.util.FMDBHelper;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.a.arch.utils.PictureSelectorManager;
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.patrol.R;
import com.facilityone.wireless.patrol.adapter.PatrolItemAdapter;
import com.facilityone.wireless.patrol.module.PatrolConstant;
import com.facilityone.wireless.patrol.module.PatrolTransmitService;
import com.facilityone.wireless.patrol.presenter.PatrolItemPresenter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luojilab.component.componentlib.router.Router;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检检查项
 * Date: 2018/11/8 5:11 PM
 */
public class PatrolItemFragment extends BaseFragment<PatrolItemPresenter> implements View.OnClickListener
        , CompoundButton.OnCheckedChangeListener
        , BaseQuickAdapter.OnItemChildClickListener
        , BottomTextListSheetBuilder.OnSheetItemClickListener
        , PatrolItemAdapter.OnContentChangeListener {

    private RecyclerView mRecyclerView;
    private LinearLayout mLlDeviceSwitch;
    private Switch mSwitch;
    private TextView mTvPre;
    private TextView mTvNext;
    private RadioGroup mRgChoice;//车站工况选择项
    private RadioButton mRbLeft; //通风
    private RadioButton mRbRight; //空调
    private TextView mChoiceTitle; //选项标题

    private static final String PATROL_EQU_LIST = "patrol_equ_list";
    private static final String PATROL_SPOT_ID = "patrol_spot_id";
    private static final String PATROL_SPOT_NAME = "patrol_spot_name";
    private static final String CLICK_POSITION = "click_position";
    private static final String PATROL_TIME = "patrol_spot_time";
    private static final String PATROL_LOCATION = "patrol_spot_location";
    private static final String PATROL_SPOT = "patrol_spot";
    private static final int REQUEST_EXCEPTION = 50001;
    private static final int MAX_PHOTO = 2000;

    private Long mSpotId;
    private String mSpotName;
    private int mDevicePosition;
    private List<PatrolEquEntity> mEntities;
    private List<PatrolItemEntity> mItemEntities;
    private List<PatrolItemEntity> mItemChoiceList; //选择后的数据源
    private PatrolItemAdapter mAdapter;
    private int mItemClickPosition;
    private boolean mChange;//是否改变了item内容
    private boolean mClickLastOne;
    private int mTempPosition;
    private LocationBean mLocation;
    private PatrolSpotEntity mPatrolSpotEntity;
    private boolean mBack;
    private String mWaterMark;
    private String mNeedTime;
    private boolean canComplete=false;
    private String attention ;
    private String name = "";
    private Integer itemChoice = -1;


    @Override
    public PatrolItemPresenter createPresenter() {
        return new PatrolItemPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_patrol_item;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mSpotId = arguments.getLong(PATROL_SPOT_ID);
            mDevicePosition = arguments.getInt(CLICK_POSITION, 0);
            mEntities = arguments.getParcelableArrayList(PATROL_EQU_LIST);
            mLocation=arguments.getParcelable(PATROL_LOCATION);
            mSpotName = arguments.getString(PATROL_SPOT_NAME,"");
            mNeedTime=arguments.getString(PATROL_TIME,"");
            mPatrolSpotEntity=arguments.getParcelable(PATROL_SPOT);
        }
        if (mEntities == null || mEntities.size() <= mDevicePosition) {
            pop();
            return;
        }


        initView();
        initOnClick();
    }

    private void initOnClick() {
        for (int i = 0; i < mRgChoice.getChildCount(); i++) {
            mRgChoice.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId()== R.id.rb_patrol_item_left){
                        checkRadioFun(view,"normal");
                    }else if (view.getId()== R.id.rb_patrol_item_right){
                        checkRadioFun(view,"abnormal");
                    }
                }
            });
        }
    }

    private void initView() {
        setSwipeBackEnable(false);
        mRecyclerView = findViewById(R.id.recyclerView);
        mLlDeviceSwitch = findViewById(R.id.device_ll);
        mSwitch = findViewById(R.id.device_switch);
        mTvPre = findViewById(R.id.pre_btn);
        mTvNext = findViewById(R.id.next_btn);

        //2021-12-9
        mRgChoice = findViewById(R.id.rb_patrol_item_rg);
        mRbLeft = findViewById(R.id.rb_patrol_item_left);
        mRbRight = findViewById(R.id.rb_patrol_item_right);
        mChoiceTitle = findViewById(R.id.device_title);

        mTvPre.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mItemEntities = new ArrayList<>();
        mAdapter = new PatrolItemAdapter(mItemEntities, this);
        mAdapter.setContentChangeListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);

        mSwitch.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                mChange = true;
            }
        });

        showTitleMenu();
        getItemList();
        setRightImageButton(R.drawable.icon_attention,R.id.patrol_attention_id);
        PatrolTaskDao db = new PatrolTaskDao();
        if (mPatrolSpotEntity !=null && mPatrolSpotEntity.getTaskId() != null){
            PatrolTaskEntity ptData = db.getTask(mPatrolSpotEntity.getTaskId());
            attention = ptData.getPrecautions()+"";
        }

    }

    public void getItemList() {
        mTempPosition = mDevicePosition;
        showLoading();
        setBtn();
        if (mEntities.get(mDevicePosition).getCompleted() != DBPatrolConstant.TRUE_VALUE) {
            mChange = true;
        }
        getPresenter().getPatrolItemList(mEntities.get(mDevicePosition));
    }

    public void error() {
        dismissLoading();
        mAdapter.setEmptyView(getErrorView((ViewGroup) mRecyclerView.getParent(), R.string.patrol_get_data_error));
    }

    public void refreshUI(List<PatrolItemEntity> itemEntities) {
        mItemEntities.clear();
        if (itemEntities != null && itemEntities.size() > 0) {
            mItemEntities.addAll(itemEntities);
        } else {
            mAdapter.setEmptyView(getNoDataView((ViewGroup) mRecyclerView.getParent()));
        }
        showTitleMenu();
        mRecyclerView.scrollToPosition(0);
        mAdapter.notifyDataSetChanged();
        dismissLoading();

        if (mItemEntities.get(0).getContent().equals("车站工况") && !TextUtils.isEmpty(mItemEntities.get(0).getSelect())){
            String itemChoice= mItemEntities.get(0).getSelect()+"";
           if (itemChoice.equals("空调")){
               mRbRight.setChecked(true);
           }else {
               mRbLeft.setChecked(true);
           }
            mAdapter.setNewData(extracted());
        }

    }

    private void showTitleMenu() {
        String time = TimeUtils.millis2String(System.currentTimeMillis(), DateUtils.SIMPLE_DATE_FORMAT_ALL);
        PatrolEquEntity equEntity = mEntities.get(mDevicePosition);
        if (equEntity.getEqId() == PatrolDbService.COMPREHENSIVE_EQU_ID) {
            setTitle(R.string.patrol_task_spot_content);
            String projectName = SPUtils.getInstance(SPKey.SP_MODEL).getString(SPKey.PROJECT_NAME,"");
            mWaterMark = projectName + "\r\n" + mSpotName + "-" + getString(R.string.patrol_task_spot_content) + "\r\n" + time;
            PatrolSpotDao db = new PatrolSpotDao();
            String taskName = db.getSpot(mSpotId).getTaskName().trim()+"";
            if (taskName.equals("空调设备状况")){
                mLlDeviceSwitch.setVisibility(View.VISIBLE);
                mChoiceTitle.setText("车站工况");
            }else {
                mLlDeviceSwitch.setVisibility(View.GONE);
            }
        } else {
            String projectName = SPUtils.getInstance(SPKey.SP_MODEL).getString(SPKey.PROJECT_NAME,"");
            String title = StringUtils.formatString(equEntity.getName()) + (TextUtils.isEmpty(equEntity.getCode()) ? "" : "(" + equEntity.getCode() + ")");
            mWaterMark = projectName + "\r\n" + mSpotName + "-" + title + "\r\n" + time;
            setTitle(title);
            mLlDeviceSwitch.setVisibility(View.VISIBLE);
            mSwitch.setChecked(!equEntity.isDeviceStatus());
        }
    }

    /**
     * 处理radioButtom 在radioGroup中不可取消选中的问题
     * */
    private void checkRadioFun(View view, String a) {
        if (name.equals(a)) {
            mRgChoice.clearCheck();
            name = "";
        } else {
            name = a;
            mRgChoice.check(view.getId());
        }
        mAdapter.setNewData(extracted());
    }

    // 选项改变数据源重组
    private List<PatrolItemEntity> extracted() {
        mItemChoiceList = new ArrayList<>();
        if (mRbLeft.isChecked()){
            for (PatrolItemEntity item : mItemEntities) {
                if (item.getValidStatus() != null &&
                        (item.getValidStatus()==  PatrolConstant.EQU_STOP
                                || item.getValidStatus() == PatrolConstant.EQU_ALL)){
                    mItemChoiceList.add(item);
                }
            }
            itemChoice = 1;
            mItemEntities.get(0).setSelect("通风");
        }else if (mRbRight.isChecked()){
            for (PatrolItemEntity item : mItemEntities) {
                if (item.getValidStatus() != null &&
                        (item.getValidStatus()==  PatrolConstant.EQU_USE
                                || item.getValidStatus() == PatrolConstant.EQU_ALL)){
                    mItemChoiceList.add(item);
                }
            }
            itemChoice = 2;
            mItemEntities.get(0).setSelect("空调");
        }else {
            mItemChoiceList = mItemEntities;
            itemChoice = 1; //初始化
            mItemEntities.get(0).setSelect("");
        }

        return mItemChoiceList;
    }

    @Override
    public void onRightImageMenuClick(View view) {
        super.onRightImageMenuClick(view);
        /**
         * 注意事项
         * */
        if (view.getId() == R.id.patrol_attention_id){
            if (TextUtils.isEmpty(attention)){
                ToastUtils.showShort("无注意事项");
                return;
            }
            startForResult(PatrolPrecautionsFragment.getInstance(attention+""),-1);
        }
    }

    @Override
    public void onClick(final View v) {
        mBack = false;
        mClickLastOne = false;
        mTempPosition = mDevicePosition;
        final int id = v.getId();
        if (id == R.id.pre_btn) {
            mDevicePosition--;
            if (mDevicePosition < 0) {
                mDevicePosition = 0;
            }
        } else if (id == R.id.next_btn) {
            mDevicePosition++;
            if (mDevicePosition > mEntities.size() - 1) {
                mDevicePosition = mEntities.size() - 1;
                mClickLastOne = true;
            }
        }
        saveDataBefore();
//        showLoading();
//        //判断是否可以提交
//        if (canComplete){
//            saveDataBefore();
//        }


    }

    public void showOrderTimeDialog(String leftTime){
        //保存此页面数据到数据库
        FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
        builder.setTitle("提示");
        String messageFormat="完成任务最少需要%s分钟，为满足期限。（还剩%s分钟）";
        builder.setTip(String.format(messageFormat,mNeedTime,leftTime));
        builder.setCancelVisiable(false);
        builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
            @Override
            public void onClick(QMUIDialog dialog, View view) {
//                saveDataBefore();
                dialog.dismiss();
            }
        });
        builder.create(R.style.fmDefaultWarnDialog).show();
    }


    private void saveDataBefore() {
        final int position = getPresenter().haveMiss(mItemEntities,itemChoice);
        if (position == -1) {
            getPresenter().saveData2Db(mItemEntities, mEntities, mTempPosition, false, mClickLastOne, mChange, mBack);
        } else {
            FMWarnDialogBuilder builder = new FMWarnDialogBuilder(getContext());
            builder.setTitle(R.string.patrol_remind);
            builder.setSure(R.string.patrol_miss);
            builder.setCancel(R.string.patrol_check);
            builder.setTip(R.string.patrol_content_miss_tip);
            builder.addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                @Override
                public void onClick(QMUIDialog dialog, View view) {
                    dialog.dismiss();
                    getPresenter().saveData2Db(mItemEntities, mEntities, mTempPosition, true, mClickLastOne, mChange, mBack);
                }
            });
            builder.addOnBtnCancelClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                @Override
                public void onClick(QMUIDialog dialog, View view) {
                    dialog.dismiss();
                    mDevicePosition = mTempPosition;
                    mRecyclerView.scrollToPosition(position);
                }
            });
            builder.create(R.style.fmDefaultWarnDialog).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getItemList();
        mEntities.get(mDevicePosition).setDeviceStatus(!isChecked);
    }

    private void setBtn() {
        mTvNext.setText(getString(R.string.patrol_check_next) + "(" + (mDevicePosition + 2) + "/" + mEntities.size() + ")");
        mTvPre.setText(getString(R.string.patrol_check_pre) + "(" + mDevicePosition + "/" + mEntities.size() + ")");
        if (mDevicePosition == 0) {
            mTvPre.setVisibility(View.GONE);
            if (mEntities.size() == 1) {
                mTvNext.setText(R.string.patrol_finish_x2);
                canComplete=true;
            }
        } else if (mDevicePosition == mEntities.size() - 1) {
            canComplete=true;
            mTvNext.setText(R.string.patrol_finish_x2);
            mTvPre.setVisibility(View.VISIBLE);
        } else {
            mTvPre.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        mItemClickPosition = position;
        int id = view.getId();
        if (id == R.id.question_edit_iv) {
            PatrolItemEntity patrolItemEntity = mItemEntities.get(position);
            String exceptions = patrolItemEntity.getExceptions();
            String comment = patrolItemEntity.getComment();
            startForResult(PatrolExceptionFragment.getInstance(comment, exceptions), REQUEST_EXCEPTION);
        } else if (id == R.id.question_take_photo_iv) {
            List<String> itemList = new ArrayList<>();
            itemList.add(getString(R.string.patrol_camera));
            boolean patrolPhoto = SPUtils.getInstance(SPKey.SP_MODEL).getBoolean(SPKey.PATROL_SELECT_PHOTO, true);
            if(patrolPhoto) {
                itemList.add(getString(R.string.patrol_select_photo));
            }
            itemList.add(getString(R.string.patrol_cancel));
            new BottomTextListSheetBuilder(getContext())
                    .setShowTitle(true)
                    .setTitle(R.string.patrol_select_photo_title)
                    .addArrayItem(itemList)
                    .setOnSheetItemClickListener(PatrolItemFragment.this)
                    .build()
                    .show();
        }else if (id == R.id.question_report_iv){
            Router router = Router.getInstance();
            WorkorderService workorderService = (WorkorderService) router.getService(WorkorderService.class.getSimpleName());
            if (workorderService != null) {
                PatrolItemEntity patrolItemEntity = mItemEntities.get(position);
                String desc=StringUtils.formatString(patrolItemEntity.getContent()) + (TextUtils.isEmpty(patrolItemEntity.getUnit()) ? "" : "(" + patrolItemEntity.getUnit() + ")");
                Long eqId=patrolItemEntity.getEqId();
                BaseFragment workorderCreateFragment = workorderService.getWorkorderCreateFragment(-1,eqId,desc,mPatrolSpotEntity.getLocation(),mPatrolSpotEntity.getLocationName(),patrolItemEntity.getContentResultId());
                start(workorderCreateFragment);
            }
        }
    }

    @Override
    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
        List<PatrolPicEntity> picEntities = mItemEntities.get(mItemClickPosition).getPicEntities();
        if (position == 0) {
            if (picEntities.size() < MAX_PHOTO) {
                PictureSelectorManager.camera(PatrolItemFragment.this, PictureConfig.REQUEST_CAMERA, mWaterMark);
            } else {
                ToastUtils.showShort(String.format(Locale.getDefault(), getString(R.string.patrol_select_photo_at_most), MAX_PHOTO));
            }
        } else if (position == 1) {
            String mater = mWaterMark + getString(R.string.patrol_check_item_upload_pic);
            PictureSelectorManager.MultipleChoose(PatrolItemFragment.this, MAX_PHOTO, PictureConfig.CHOOSE_REQUEST, mater);
        }
        dialog.dismiss();
    }


    @Override
    public boolean onBackPressedSupport() {
        if (mChange) {
            mBack = true;
            saveDataBefore();
            return true;
        } else {
            return super.onBackPressedSupport();
        }
    }

    @Override
    public void leftBackListener() {
        if (mChange) {
            mBack = true;
            saveDataBefore();
        } else {
            super.leftBackListener();
        }
    }

    public void popResult() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("change", mChange);
        setFragmentResult(RESULT_OK, bundle);
        pop();
    }

    @Override
    public void change() {
        mChange = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                case PictureConfig.REQUEST_CAMERA:
                    mChange = true;
                    List<PatrolPicEntity> picEntities = mItemEntities.get(mItemClickPosition).getPicEntities();
                    if (picEntities == null) {
                        picEntities = new ArrayList<>();
                    }
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList != null) {
                        for (LocalMedia media : selectList) {
                            PatrolPicEntity picEntity = new PatrolPicEntity();
                            String path = "";
                            if (media.isCut() && !media.isCompressed()) {
                                path = media.getCutPath();
                            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                                path = media.getCompressPath();
                            } else {
                                path = media.getPath();
                            }
                            picEntity.setPath(path);
                            picEntities.add(picEntity);
                        }
                    }
                    mAdapter.notifyItemChanged(mItemClickPosition);
                    break;
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_EXCEPTION:
                String comment = data.getString(PatrolTransmitService.PATROL_ITEM_EXCEPTION);
                PatrolItemEntity patrolItemEntity = mItemEntities.get(mItemClickPosition);
                String mark = patrolItemEntity.getComment();
                if (!TextUtils.isEmpty(comment) && TextUtils.isEmpty(mark)) {
                    mChange = true;
                }

                if (TextUtils.isEmpty(comment) && !TextUtils.isEmpty(mark)) {
                    mChange = true;
                }

                if (!TextUtils.isEmpty(comment) && !TextUtils.isEmpty(mark) && !comment.equals(mark)) {
                    mChange = true;
                }
                patrolItemEntity.setComment(comment);
                mAdapter.notifyItemChanged(mItemClickPosition);
                break;
        }
    }

    public static PatrolItemFragment getInstance(Long spotId, ArrayList<PatrolEquEntity> entities, int position,String spotName) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PATROL_EQU_LIST, entities);
        bundle.putLong(PATROL_SPOT_ID, spotId);
        bundle.putInt(CLICK_POSITION, position);
        bundle.putString(PATROL_SPOT_NAME, spotName);
        PatrolItemFragment instance = new PatrolItemFragment();
        instance.setArguments(bundle);
        return instance;
    }
    public static PatrolItemFragment getInstance(Long spotId, ArrayList<PatrolEquEntity> entities, int position, String spotName, LocationBean locationBean, PatrolSpotEntity entity) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PATROL_EQU_LIST, entities);
        bundle.putLong(PATROL_SPOT_ID, spotId);
        bundle.putInt(CLICK_POSITION, position);
        bundle.putString(PATROL_SPOT_NAME, spotName);
        bundle.putParcelable(PATROL_LOCATION,locationBean);
        bundle.putParcelable(PATROL_SPOT,entity);
        PatrolItemFragment instance = new PatrolItemFragment();
        instance.setArguments(bundle);
        return instance;
    }
}
