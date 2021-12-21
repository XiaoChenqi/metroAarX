package com.facilityone.wireless.demand.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cktim.camera2library.Camera2Config;
import com.facilityone.wireless.a.arch.ec.adapter.AudioAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridImageAdapter;
import com.facilityone.wireless.a.arch.ec.audio.AudioPlayConnection;
import com.facilityone.wireless.a.arch.ec.audio.AudioPlayManager;
import com.facilityone.wireless.a.arch.ec.audio.AudioPlayService;
import com.facilityone.wireless.a.arch.ec.module.CommonUrl;
import com.facilityone.wireless.a.arch.ec.module.ConstantMeida;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.LocationUtils;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.dao.EquDao;
import com.facilityone.wireless.a.arch.utils.FMFileUtils;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.a.arch.utils.PictureSelectorManager;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.a.arch.widget.FMBottomAudioSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMBottomGridSheetBuilder;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.facilityone.wireless.basiclib.utils.PermissionHelper;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.basiclib.video.SimplePlayer;
import com.facilityone.wireless.basiclib.widget.FullyGridLayoutManager;
import com.facilityone.wireless.demand.R;
import com.facilityone.wireless.demand.adapter.DeviceInforAdapter;
import com.facilityone.wireless.demand.module.DemandConstant;
import com.facilityone.wireless.demand.module.DemandCreateService;
import com.facilityone.wireless.demand.module.DemandService;
import com.facilityone.wireless.demand.presenter.DemandCreatePresenter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:创建需求
 * Date: 2018/6/21 下午4:07
 */

/**
 * Author：Karelie
 * Email:
 * description:快速报障界面
 * Date: 2021/8/4 10:55
 */
public class DemandCreateFragment extends BaseFragment<DemandCreatePresenter> implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, PermissionHelper.OnPermissionGrantedListener, PermissionHelper.OnPermissionDeniedListener, FMBottomAudioSheetBuilder.OnAudioFinishListener, AudioAdapter.onRemoveAudioListener {
    private CustomContentItemView mTypeCiv;
    private CustomContentItemView mContactCiv;
    private CustomContentItemView mTelCiv;
    private EditNumberView mNumberView;
    private ImageView mMenuIv;
    private RecyclerView mPhotoRv;
    private TextView mPicTitleTv;
    private RecyclerView mVideoRv;
    private TextView mVideoTitleTv;
    private RecyclerView mAudioRv;
    private TextView mAudioTitleTv;

    private static final int MAX_PHOTO = 1000;
    private static final int REQUEST_DEMAND_TYPE = 5009;
    private static final int REQUEST_DEMAND_LOCATION = 5010;
    private static final int REQUEST_DEMAND_DEVICE = 5011;
    //图片
    private List<LocalMedia> mSelectList;
    private GridImageAdapter mGridImageAdapter;
    //视频
    private List<LocalMedia> mVideoSelectList;
    private GridImageAdapter mVideoGridImageAdapter;
    //音频
    private List<LocalMedia> mAudioSelectList;
    private AudioAdapter mAudioAdapter;

    private DemandCreateService.DemandCreateReq request;
    private DemandCreateService.CompleteDeviceReq requestComplete;
    private QMUIBottomSheet mAudioDialog;

    /**
     * 上海四运
     * */
    private CustomContentItemView mCivLocation;
    private LinearLayout mChooseDevice; //选择设备布局
    private RecyclerView mDeviceList; //设备列表
    private DeviceInforAdapter mAdapter;
    private List<DemandService.DeviceInforListEnity> deviceList;
    private DemandService.DeviceInforListEnity deviceData;
    private Boolean isCompeteDemand = false;
    private List<String> deviceIdList;
    private Long locationId; //位置Id
    private Long deviceId = -1L; //设备Id
    private LocationBean mLocationData; // 位置对象
    private LocationBean mLocationForDevice; // 选择设备需将部分位置去除
    private String mLocationName; //位置名称
    private String mDesc;//故障描述

    private Boolean isPatrol; // 是否是巡检快速报障
    //最后一次签到记录
    private String locationNameLast;
    private LocationBean locationBeanLast;

    private Long contentId; //巡检Id
    private String deviceName;
    private String deviceCode;
    private Boolean hasDevice = false; //判断快速报障是否带设备


    @Override
    public DemandCreatePresenter createPresenter() {
        return new DemandCreatePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_demand_create;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getPresenter().getUserInfo();
        initRecyclerView();
        initData();
        initAudioPlayService();
        initOnClick();
    }

    private void initOnClick() {
        mCivLocation.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (deviceIdList.size() > 0 ){
                    showDeleteDeviceDialog("是否确定删除不是当前位置的设备？");
                }else {
                    startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_LOCATION), REQUEST_DEMAND_LOCATION);
                }

            }
        });

        mChooseDevice.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!mCivLocation.getTipText().equals("")){
                    mLocationForDevice = new LocationBean();
                     /**
                      * @Auther: karelie
                      * @Date: 2021/8/30
                      * 只需要这些数据选择位置即可
                      */
                    mLocationForDevice.buildingId = mLocationData.buildingId;
                    mLocationForDevice.siteId = mLocationData.siteId;
                    startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_EQU,mLocationForDevice,mLocationName), REQUEST_DEMAND_DEVICE);
                }else {
                    startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_EQU_ALL), REQUEST_DEMAND_DEVICE);
                }

            }
        });

    }

    public void RefreshSigon(DemandCreateService.AttendanceResp data){
        //判断最后一条记录数据不为空且为签到状态
        if (data != null && data.signStatus){
            mCivLocation.setTipText(data.locationName+"");
            mLocationData = data.location;
        }
    }

    private void initAudioPlayService() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), AudioPlayService.class);
        AudioPlayConnection audioPlayConnection = new AudioPlayConnection();
        getActivity().bindService(intent, audioPlayConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        setTitle(R.string.demand_create_title);
        setRightTextButton(R.string.demand_submit, R.id.demand_create_upload_id);
        mTypeCiv = findViewById(R.id.civ_type);
        mContactCiv = findViewById(R.id.civ_contact);
        mTelCiv = findViewById(R.id.civ_tel);
        mMenuIv = findViewById(R.id.iv_add_menu);

        mPhotoRv = findViewById(R.id.rv_photo);
        mPicTitleTv = findViewById(R.id.tv_pic);

        mVideoRv = findViewById(R.id.rv_video);
        mVideoTitleTv = findViewById(R.id.tv_video);

        mAudioRv = findViewById(R.id.rv_audio);
        mAudioTitleTv = findViewById(R.id.tv_audio);
        mNumberView = findViewById(R.id.env_desc);

        mMenuIv.setOnClickListener(this);
        mTypeCiv.setOnClickListener(this);

        mTelCiv.getInputEt().setInputType(InputType.TYPE_CLASS_PHONE);
        mContactCiv.canInput(false);

        mContactCiv.setInputText(SPUtils.getInstance(SPKey.SP_MODEL).getString(SPKey.USERNAME));

        InputFilter inputFilter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence charSequence, int start, int end, Spanned dest, int dstart, int dend) {
                String regex = "^[0-9\\-]+$";
                boolean isPhone = Pattern.matches(regex, charSequence.toString());
                if (!isPhone) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        mTelCiv.getInputEt().setFilters(new InputFilter[]{ inputFilter });


        /**
         * 四运
         * Coder:Karelie
         * */
        mCivLocation = findViewById(R.id.civ_location);
        mChooseDevice = findViewById(R.id.demand_create_choose_device_all);
        mDeviceList = findViewById(R.id.demand_create__device_list);

    }

    private void initRecyclerView() {
        mGridImageAdapter = new GridImageAdapter(mSelectList, false);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getContext(),
                FullyGridLayoutManager.SPAN_COUNT,
                GridLayoutManager.VERTICAL,
                false);
        mPhotoRv.setLayoutManager(manager);
        mPhotoRv.setAdapter(mGridImageAdapter);
        mGridImageAdapter.setOnItemChildClickListener(this);
        mGridImageAdapter.setOnItemClickListener(this);

        mVideoSelectList = new ArrayList<>();
        mVideoGridImageAdapter = new GridImageAdapter(mVideoSelectList, false);
        FullyGridLayoutManager audioManager = new FullyGridLayoutManager(getContext(),
                FullyGridLayoutManager.SPAN_COUNT,
                GridLayoutManager.VERTICAL,
                false);
        mVideoGridImageAdapter.setOnItemChildClickListener(this);
        mVideoGridImageAdapter.setOnItemClickListener(this);
        mVideoRv.setLayoutManager(audioManager);
        mVideoRv.setAdapter(mVideoGridImageAdapter);

        mAudioSelectList = new ArrayList<>();
        mAudioAdapter = new AudioAdapter(mAudioSelectList, getContext());
        mAudioAdapter.setOnRemoveAudioListener(this);
        mAudioRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAudioRv.setAdapter(mAudioAdapter);

        deviceList = new ArrayList<>();
        mDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DeviceInforAdapter(deviceList);
        mDeviceList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
    }

    private void initData() {
        Bundle bundle = getArguments();
        setTitle(bundle.getString("title")+"");
        request = new DemandCreateService.DemandCreateReq();
        requestComplete = new DemandCreateService.CompleteDeviceReq();
        isCompeteDemand = bundle.getBoolean("IsComplete",false);
        if (bundle.getString("locationName","") != null){
            mLocationName=bundle.getString("locationName","");
        }

        if (bundle.getParcelable("location")!= null){
            mLocationData=bundle.getParcelable("location");
        }

        mDesc=bundle.getString("desc","");
        contentId = bundle.getLong("contentId",-1);
        deviceName = bundle.getString("deviceName")+"";
        deviceId = bundle.getLong("deviceId",-1);
        deviceCode = bundle.getString("deviceCode")+"";
        deviceIdList = new ArrayList<>();

        if (deviceId != null && deviceId != -1 && deviceId != 0){
            deviceData = new DemandService.DeviceInforListEnity();
            deviceData.deviceName = deviceName;
            deviceData.deviceNumber = deviceCode;
            //根据设备和位置列表联查名称
            EquDao equDao = new EquDao();
            SelectDataBean device = equDao.queryEquById(deviceId);
            deviceData.deviceLocation = LocationUtils.getStrLocation(device.getLocation());
            deviceList.add(deviceData);
            mAdapter.replaceData(deviceList);
            deviceIdList = new ArrayList<>();
            deviceIdList.add(deviceId+"");
        }


        ArrayList<LocalMedia> imgs=bundle.getParcelableArrayList("images");
        if (imgs!=null){
            mSelectList=new ArrayList<>();
            mSelectList.addAll(imgs);
            mGridImageAdapter.replaceData(mSelectList);
            showOrHidePhoto();
        }
        List<String> phIds = new ArrayList<>();
        if (mSelectList != null){
            for (LocalMedia data : mSelectList) {
                if (data.getSrc() != null){
                    String a = data.getSrc();
                    String substring = a.substring(a.lastIndexOf("/") + 1);
                    phIds.add(substring);
                }
            }
        }

        request.photoIds = phIds;

        mNumberView.setDesc(mDesc);
        if (!TextUtils.isEmpty(mLocationName)){
            mCivLocation.setTipText(mLocationName);
        }

        isPatrol = bundle.getBoolean("isPatrol",false);
        String toJson = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);


    }

    public void refreshUserInfo(String userInfo) {
        if (!TextUtils.isEmpty(userInfo)) {
            UserService.UserInfoBean userBean = GsonUtils.fromJson(userInfo, UserService.UserInfoBean.class);
            if (userBean != null) {
                mContactCiv.setInputText(userBean.name == null ? "" : userBean.name);
                mTelCiv.setInputText(userBean.phone == null ? "" : userBean.phone);
                if (userBean.type == DemandConstant.OUT_SOURCING_CODE ){
                    getPresenter().getLastAttendance();//获取最后一次签到记录判断签入状态而后将数据写入界面
                }else if (userBean.type == DemandConstant.STATION_CODE){
                    mLocationName = LocationUtils.getStrLocation(userBean.location);
                    mCivLocation.setTipText(mLocationName);
                    mLocationData = userBean.location;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.civ_type) {
            startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_SERVICE_TYPE), REQUEST_DEMAND_TYPE);
        } else if (v.getId() == R.id.iv_add_menu) {
            showBottomMenu();
        }
    }

    public void showDeleteDeviceDialog(String tip){
        new FMWarnDialogBuilder(getContext()).setIconVisible(false)
                .setSureBluBg(true)
                .setTitle(R.string.demand_remind)
                .setSure(R.string.demand_sure)
                .setTip(tip)
                .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, View view) {
                        dialog.dismiss();
                        mAdapter.remove(0);
                        mAdapter.notifyDataSetChanged();
                        mNumberView.setDesc("");
                        deviceIdList.clear();
                        startForResult(SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_LOCATION), REQUEST_DEMAND_LOCATION);
                    }
                }).create(R.style.fmDefaultWarnDialog).show();
    }

    @Override
    public void onRightTextMenuClick(View view) {
        //验证参数
        if (validateParams()) {
            request.requester = mContactCiv.getInputText();
            request.contact = mTelCiv.getInputText();
            request.desc = mNumberView.getDesc();
            showLoading();
            if (!isCompeteDemand){
                request.equipment = deviceIdList;
                request.location = mLocationData;
            }else {
                requestComplete.reqId = 0l;
                requestComplete.desc = mNumberView.getDesc();
//                request.location = mLocationData;
                requestComplete.equipment = deviceIdList;
                requestComplete.photoIds = request.photoIds;
                requestComplete.audioIds = request.audioIds;
                requestComplete.videoIds = request.videoIds;
            }
            if (contentId != null && contentId != -1){
                request.resultId = contentId;
            }
            if (mSelectList != null){
                if (mSelectList.size() > 0) {
                    getPresenter().uploadFile(mSelectList, ConstantMeida.IMAGE);
                } else if (mVideoSelectList.size() > 0) {
                    getPresenter().uploadFile(mVideoSelectList, CommonUrl.UPLOAD_VIDEO_URL, ConstantMeida.VIDEO);
                } else if (mAudioSelectList.size() > 0) {
                    getPresenter().uploadFile(mAudioSelectList, CommonUrl.UPLOAD_VOICE_URL, ConstantMeida.AUDIO);
                }else {
                    getPresenter().createDemand();
                }
            }else {
                getPresenter().createDemand();
            }

        }
    }

    private boolean validateParams() {
        if (TextUtils.isEmpty(mContactCiv.getInputText())) {
            ToastUtils.showShort(R.string.demand_input_person);
            return false;
        }
        if (TextUtils.isEmpty(mTelCiv.getInputText())) {
            ToastUtils.showShort(R.string.demand_input_phone);
            return false;
        }
        if (TextUtils.isEmpty(mTelCiv.getInputText()) || RegexUtils.isTel(mTelCiv.getInputText())) {
            ToastUtils.showShort(R.string.demand_error_mobile_number);
            return false;
        }

        if (TextUtils.isEmpty(mTypeCiv.getTipText())) {
            ToastUtils.showShort(R.string.demand_select_demand_type);
            return false;
        }

        if (TextUtils.isEmpty(mNumberView.getDesc())) {
            ToastUtils.showShort(R.string.demand_input_desc);
            return false;
        }

        if (TextUtils.isEmpty(mCivLocation.getTipText())){
            ToastUtils.showShort("请选择位置");
            return false;
        }

        return true;
    }

    public DemandCreateService.DemandCreateReq getRequest() {
        return request;
    }

    public DemandCreateService.CompleteDeviceReq getCompleteRequest() {
        return requestComplete;
    }

    private void showBottomMenu() {
        final int TAG_MENU_CHOOSE_PIC = 0;
        final int TAG_MENU_CAMERA_PIC = 1;
        final int TAG_MENU_VIDEO = 2;
        final int TAG_MENU_AUDIO = 3;
        FMBottomGridSheetBuilder builder = new FMBottomGridSheetBuilder(getActivity());
        builder.addItem(R.drawable.icon_more_choose_pic_selector, getString(R.string.demand_picture), TAG_MENU_CHOOSE_PIC, FMBottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.icon_more_camera_pic_selector, getString(R.string.demand_take_a_picture), TAG_MENU_CAMERA_PIC, FMBottomGridSheetBuilder.FIRST_LINE)
//                .addItem(R.drawable.icon_more_audio_selector, getString(R.string.demand_audio), TAG_MENU_AUDIO, FMBottomGridSheetBuilder.FIRST_LINE)
//                .addItem(R.drawable.icon_more_video_selector, getString(R.string.demand_video), TAG_MENU_VIDEO, FMBottomGridSheetBuilder.FIRST_LINE)
                .setIsShowButton(false)
                .setOnSheetItemClickListener(new FMBottomGridSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView) {
                        dialog.dismiss();
                        String projectName = SPUtils.getInstance(SPKey.SP_MODEL).getString(SPKey.PROJECT_NAME, "");
                        int tag = (int) itemView.getTag();
                        switch (tag) {
                            case TAG_MENU_CHOOSE_PIC:
                                PictureSelectorManager.MultipleChoose(DemandCreateFragment.this, MAX_PHOTO, mSelectList, PictureConfig.CHOOSE_REQUEST, projectName);
                                break;
                            case TAG_MENU_CAMERA_PIC:
                                if (mSelectList.size() < MAX_PHOTO) {
                                    PictureSelectorManager.camera(DemandCreateFragment.this, PictureConfig.REQUEST_CAMERA, projectName);
                                } else {
                                    ToastUtils.showShort(String.format(Locale.getDefault(), getString(R.string.demand_select_photo_at_most), MAX_PHOTO));
                                }
                                break;
                            case TAG_MENU_VIDEO:
                                PictureSelectorManager.cameraVideo(DemandCreateFragment.this, PictureSelectorManager.REQUEST_CAMERA_VIDEO);
                                break;
                            case TAG_MENU_AUDIO:
                                PermissionHelper.requestMicrophone(DemandCreateFragment.this, DemandCreateFragment.this);
                                break;
                        }
                    }
                }).build().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    mSelectList = new ArrayList<>();
                    mSelectList.addAll(selectList);
                    mGridImageAdapter.replaceData(mSelectList);
                    showOrHidePhoto();
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    List<LocalMedia> selectCamera = PictureSelector.obtainMultipleResult(data);
                    mSelectList.addAll(selectCamera);
                    mGridImageAdapter.replaceData(mSelectList);
                    showOrHidePhoto();
                    break;
                case PictureSelectorManager.REQUEST_CAMERA_VIDEO:
                    try {
                        String path = data.getStringExtra(Camera2Config.INTENT_PATH_SAVE_VIDEO);
                        LocalMedia media = new LocalMedia();
                        media.setPath(path);
                        String videoType = PictureMimeType.createVideoType(path);
                        media.setPictureType(videoType);
                        int duration = PictureMimeType.getLocalVideoDuration(path);
                        media.setDuration(duration);
                        mVideoSelectList.add(media);
                        mVideoGridImageAdapter.replaceData(mVideoSelectList);
                        showOrHideVideo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void showOrHidePhoto() {
        mPicTitleTv.setVisibility(mSelectList.size() == 0 ? View.GONE : View.VISIBLE);
        mPhotoRv.setVisibility(mSelectList.size() == 0 ? View.GONE : View.VISIBLE);
    }

    private void showOrHideVideo() {
        mVideoRv.setVisibility(mVideoSelectList.size() == 0 ? View.GONE : View.VISIBLE);
        mVideoTitleTv.setVisibility(mVideoSelectList.size() == 0 ? View.GONE : View.VISIBLE);
    }

    private void showOrHideAudio() {
        mAudioRv.setVisibility(mAudioSelectList.size() == 0 ? View.GONE : View.VISIBLE);
        mAudioTitleTv.setVisibility(mAudioSelectList.size() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mGridImageAdapter) {
            PictureSelector.create(DemandCreateFragment.this)
                    .themeStyle(R.style.picture_fm_style)
                    .openExternalPreview(position, mSelectList);
        } else {
            // 预览视频
            LocalMedia localMedia = mVideoSelectList.get(position);
            SimplePlayer.startActivity(this, localMedia.getPath());
        }
    }

    public List<LocalMedia> getVideoSelectList() {
        if (mVideoSelectList == null) {
            return new ArrayList<>();
        }
        return mVideoSelectList;
    }

    public List<LocalMedia> getAudioSelectList() {
        if (mAudioSelectList == null) {
            return new ArrayList<>();
        }
        return mAudioSelectList;
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (adapter == mAdapter){
            final String tip = "是否确定删除该设备？";
            Button btn = view.findViewById(R.id.btn_delete_device_item);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FMWarnDialogBuilder(getContext()).setIconVisible(false)
                            .setSureBluBg(true)
                            .setTitle(R.string.demand_remind)
                            .setSure(R.string.demand_sure)
                            .setTip(tip)
                            .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, View view) {
                                    dialog.dismiss();
                                    mAdapter.remove(0);
                                    mAdapter.notifyDataSetChanged();
                                    mNumberView.setDesc("");
                                    deviceIdList.clear();
                                }
                            }).create(R.style.fmDefaultWarnDialog).show();
                }
            });

        }else {
            String tip = getString(R.string.demand_sure_delete_video);
            if (adapter == mGridImageAdapter) {
                tip = getString(R.string.demand_sure_delete_photo);
            }
            new FMWarnDialogBuilder(getContext()).setIconVisible(false)
                    .setSureBluBg(true)
                    .setTitle(R.string.demand_remind)
                    .setSure(R.string.demand_sure)
                    .setTip(tip)
                    .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, View view) {
                            dialog.dismiss();
                            GridImageAdapter tempAdapter = (GridImageAdapter) adapter;
                            String path = "";
                            LocalMedia item = tempAdapter.getItem(position);
                            if (item != null) {
                                if (item.isCut() && !item.isCompressed()) {
                                    path = item.getCutPath();
                                } else if (item.isCompressed() || (item.isCut() && item.isCompressed())) {
                                    path = item.getCompressPath();
                                } else {
                                    if (tempAdapter == mVideoGridImageAdapter) {
                                        path = item.getPath();
                                    }
                                }
                            }
                            mSelectList.remove(position);
                            if (request.photoIds != null){
                                if (position <= request.photoIds.size()-1){
                                    request.photoIds.remove(position);
                                }
                            }

                            tempAdapter.remove(position);
                            if (tempAdapter == mGridImageAdapter) {
                                showOrHidePhoto();
                            } else {
                                showOrHideVideo();
                            }
                        }
                    }).create(R.style.fmDefaultWarnDialog).show();
        }

    }

    @Override
    public void onSave(LocalMedia media) {
        mAudioSelectList.add(media);
        mAudioAdapter.notifyDataSetChanged();
        showOrHideAudio();
    }

    @Override
    public void onPermissionGranted() {
        FMBottomAudioSheetBuilder audioSheetBuilder = new FMBottomAudioSheetBuilder(getContext());
        mAudioDialog = audioSheetBuilder.build();
        mAudioDialog.show();
        audioSheetBuilder.setOnAudioFinishListener(this);
    }

    @Override
    public void onPermissionDenied() {
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (mAudioDialog != null && mAudioDialog.isShowing()) {
            LogUtils.d("非正常录音");
            mAudioDialog.cancel();
        }

    }

    @Override
    public void onRemove(int position) {
        showOrHideAudio();
    }

    @Override
    public void onAudioClick(ImageView imageView, String path, int position) {
        if (AudioPlayManager.getAudioPlayService() != null) {
            AudioPlayManager.getAudioPlayService().setImageView(imageView);
            AudioPlayManager.getAudioPlayService().stopPlayVoiceAnimation();
            AudioPlayManager.getAudioPlayService().play(path);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK) {
            return;
        }
        SelectDataBean bean = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK);
        switch (requestCode) {
            case REQUEST_DEMAND_TYPE: //选择服务类型
                if (bean == null) {
                    mTypeCiv.setTipText("");
                    request.serviceTypeId = null;
                } else {
                    mTypeCiv.setTipText(StringUtils.formatString(bean.getFullName()));
                    request.serviceTypeId = bean.getId();
                    LogUtils.d("demand type :" + request.serviceTypeId);
                }
                break;
            case REQUEST_DEMAND_LOCATION: //选择位置
                if (bean != null) {
                    mCivLocation.setTipText(bean.getFullName()+"");
                    locationId = bean.getId();
                    mLocationData = bean.getLocation();
//                    mLocationData.floorId = null;
//                    mLocationData.roomId = null;
                    mLocationName = bean.getFullName()+"";
                } else {
                    mCivLocation.setTipText("");
                    locationId = null;
                    mLocationName = "";
                    mLocationData = null;
                }
                break;
            case REQUEST_DEMAND_DEVICE: //选择设备
                if (bean == null){
                    return;
                }else {
                    deviceList = new ArrayList<>();
                    deviceIdList = new ArrayList<>();
                    deviceData = new DemandService.DeviceInforListEnity();
                    deviceData.deviceLocation  = LocationUtils.getStrLocation(bean.getLocation())+"";
                    deviceData.deviceName = bean.getName()+"";
                    deviceData.deviceNumber = bean.getFullName()+"";
                    deviceList.add(deviceData);
                    mAdapter.replaceData(deviceList);
                    String desc = mNumberView.getDesc();
//                    mNumberView.setDesc(bean.getName()+bean.getFullName()+desc);
                    deviceIdList.add(bean.getId()+"");
                    if (bean.getLocation() != null &&  mCivLocation.getTipText() == null){
                        mLocationData = bean.getLocation();
                        mLocationName = bean.getLocationName()+"";
                    }

                    if (mCivLocation.getTipText().equals("") || mCivLocation.getTipText() == null){
                        mCivLocation.setTipText(LocationUtils.getStrLocation(bean.getLocation())+"");
                    }

                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (AudioPlayManager.getAudioPlayService() != null) {
            if (AudioPlayManager.getAudioPlayService().isPlaying()) {
                AudioPlayManager.getAudioPlayService().stopPlaying();
            }
            AudioPlayManager.getAudioPlayService().stopPlayVoiceAnimation();
            AudioPlayManager.getAudioPlayService().quit();
        }
        //删除所有音频文件
        FileUtils.deleteAllInDir(FMFileUtils.getAudioPath());
        //删除所有视频文件
        FileUtils.deleteAllInDir(FMFileUtils.getVideoPath());
        //删除所有照片文件
        FileUtils.deleteAllInDir(FMFileUtils.getPicPath());
    }

    public static DemandCreateFragment getInstance() {
        return new DemandCreateFragment();
    }

    public static DemandCreateFragment getInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        DemandCreateFragment instance = new DemandCreateFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static DemandCreateFragment getInstance(Integer type, Long reqId,String title,boolean isComplete) {
        Bundle bundle = new Bundle();
        bundle.putInt("Asd", type);
        bundle.putLong("Asd", reqId);
        bundle.putString("title", title);
        bundle.putBoolean("IsComplete",isComplete);
        DemandCreateFragment instance = new DemandCreateFragment();
        instance.setArguments(bundle);
        return instance;
    }

    public static DemandCreateFragment getInstance(Long reqId,String title,String locationName,LocationBean locationBean,String desc,boolean isComplete,List<LocalMedia> imageIds,boolean isPatrol,Long contentId,String deviceName,Long deviceId,String code) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("location",locationBean);
        bundle.putString("locationName",locationName);
        bundle.putString("desc",desc);
        bundle.putString("title", title);
        bundle.putBoolean("isPatrol",isPatrol);
        bundle.putString("deviceName",deviceName);
        if (deviceId != null){
            bundle.putLong("deviceId",deviceId);
        }else {
            bundle.putLong("deviceId",-1);
        }
        bundle.putString("deviceCode",code);

        if (imageIds!=null){
            ArrayList<LocalMedia> medias = new ArrayList<>(imageIds);
            bundle.putParcelableArrayList("images",medias);
        }
        if (contentId != null){
            bundle.putLong("contentId",contentId);
        }
        bundle.putBoolean("IsComplete",isComplete);
        DemandCreateFragment instance = new DemandCreateFragment();
        instance.setArguments(bundle);
        return instance;
    }
}
