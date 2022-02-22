package com.facilityone.wireless.workorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridImageAdapter;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.offline.util.PatrolQrcodeUtils;
import com.facilityone.wireless.a.arch.utils.PictureSelectorManager;
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.basiclib.widget.FullyGridLayoutManager;
import com.facilityone.wireless.workorder.R;
import com.facilityone.wireless.workorder.module.WorkorderService;
import com.facilityone.wireless.workorder.presenter.WorkorderStepUpdatePresenter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.androidannotations.annotations.Bean;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:工单-维护步骤更新
 * Date: 2018/9/25 4:06 PM
 */
public class WorkorderStepUpdateFragment extends BaseFragment<WorkorderStepUpdatePresenter> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, BottomTextListSheetBuilder.OnSheetItemClickListener {

    private CustomContentItemView mEtWorkTeam;
    private CustomContentItemView mEtStep;
    private EditNumberView mEtDesc;
    private RadioGroup mRadioGroup;
    private RecyclerView mPhotoRv;
    private RadioButton mNormal; //正常按钮
    private RadioButton mAbNormal; //异常按钮
    private LinearLayout mLlPrecautions; //注意事项
    private EditText mEtMaintenanceResult; //输入结果
    private EditText mEquipmentNumber; //设备数量
    private LinearLayout mEqNumber; //设备数量布局

    private EditNumberView mEtStepWarning;
    private TextView mTvStepWarning;


    private static final String WORKORDER_ID = "workorder_id";
    private static final String WORKORDER_STEP = "workorder_tool";
    private static final String COUNT_ACCORD = "count_accord";
    private static final String ATTENTION = "attention";

    private static final int MAX_PHOTO = 8;
    //图片
    private List<LocalMedia> mSelectList;
    private GridImageAdapter mGridImageAdapter;
    private Long mWoId;
    private WorkorderService.StepsBean mStepsBean;
    private WorkorderService.WorkorderStepUpdateReq mRequest;
    private String name = "";
    private Boolean selectStatus; //选中状态
    private String maintenanceResult; //维护结果
    private String numberInput ;//设备输入数量
    private Boolean needInput;//是否需要打开输入结果的输入
    private Boolean haveRemark = false;//备注是否是必填项
    private Boolean needCountAccord; //是否需要输入设备数量
    private String attention; //注意事项


    @Override
    public WorkorderStepUpdatePresenter createPresenter() {
        return new WorkorderStepUpdatePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_workorder_step_update;
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
        initRecyclerView();
        initOnClick();
    }

    private void initOnClick() {
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            mRadioGroup.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId()== R.id.rb_nomal){
                        checkRadioFun(view,"normal");
                    }else if (view.getId()== R.id.rb_abnormal){
                        checkRadioFun(view,"abnormal");
                    }
                }
            });
        }
        mLlPrecautions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startForResult(PrecautionsFragment.getInstance(attention+""),-1);
            }
        });
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mWoId = arguments.getLong(WORKORDER_ID);
            mStepsBean = arguments.getParcelable(WORKORDER_STEP);
            needCountAccord = arguments.getBoolean(COUNT_ACCORD,false); //是否需要上传设备数量
            attention = arguments.getString(ATTENTION); //注意事项

            if (mStepsBean == null){
                return;
            }

            if (mStepsBean.accordText != null){
                needInput = mStepsBean.accordText;  //needInput == true ? 输入文本 : 选择正常异常
            }


        }

    }

    /**
     * 处理radioButtom 在radioGroup中不可取消选中的问题
     * */
    private void checkRadioFun(View view, String a) {
        if (name.equals(a)) {
            mRadioGroup.clearCheck();
            name = "";
        } else {
            name = a;
            mRadioGroup.check(view.getId());
        }

    }

    private void initView() {
        if (mStepsBean != null) {
            setTitle(getString(R.string.workorder_step) + mStepsBean.sort);
        } else {
            setTitle(R.string.workorder_menu_step);
        }
        setRightTextButton(R.string.workorder_save, R.id.workorder_step_save_menu_id);
        mEtWorkTeam = findViewById(R.id.et_work_team_step);
        mEtStep = findViewById(R.id.step_civ);
        mEtDesc = findViewById(R.id.desc_step_env);
        mRadioGroup = findViewById(R.id.maintenance_result);
        mPhotoRv = findViewById(R.id.rv_photo);
        mNormal = findViewById(R.id.rb_nomal);
        mAbNormal = findViewById(R.id.rb_abnormal);
        mLlPrecautions = findViewById(R.id.ll_precautions);
        mEtMaintenanceResult = findViewById(R.id.et_maintenance_result);
        mEtStep.setTextSigleLine(false);
        mEtStep.setTextGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        mEquipmentNumber = findViewById(R.id.et_step_eqNumber);
        mEqNumber = findViewById(R.id.ll_eq_number);

        mEtStepWarning = findViewById(R.id.etStepWarning);
        mEtStepWarning.canInput(false);
        mEtStepWarning.setInputDisp(false);
        mTvStepWarning = findViewById(R.id.tvStepWarning);

        if (mStepsBean != null) {
            mEtWorkTeam.setTipText(StringUtils.formatString(mStepsBean.workTeamName));
            mEtStep.setTipText(StringUtils.formatString(mStepsBean.step).replaceAll("-","\n"));
        }

        if (needInput){
            mRadioGroup.setVisibility(View.GONE);
            mEtMaintenanceResult.setVisibility(View.VISIBLE);
        }else {
            mRadioGroup.setVisibility(View.VISIBLE);
            mEtMaintenanceResult.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(attention)){
            mEtStepWarning.setDesc(attention);
            mEtStepWarning.setVisibility(View.VISIBLE);
            mTvStepWarning.setVisibility(View.VISIBLE);
        }else {
            mEtStepWarning.setVisibility(View.GONE);
            mTvStepWarning.setVisibility(View.GONE);
            mEtStepWarning.setDesc("无");
        }

        if (needCountAccord){
            mEqNumber.setVisibility(View.VISIBLE);
        }else {
            mEqNumber.setVisibility(View.GONE);
        }

        if (mStepsBean != null && mStepsBean.finished != null){
            if (mStepsBean.finished) {
                mNormal.setChecked(true);
            } else {
                mAbNormal.setChecked(true);
            }
        }else {
            mStepsBean.finished = null;
        }

        if (mStepsBean != null && mStepsBean.eqNumber != null){
            mEquipmentNumber.setText(mStepsBean.eqNumber+"");
        }

        if (!TextUtils.isEmpty(mStepsBean.step)){
            if (mStepsBean.step.equals("电梯检验合格证")){
                mNormal.setText("有效");
                mAbNormal.setText("无效");
            }else {
                mNormal.setText("正常");
                mAbNormal.setText("异常");
            }
        }

        if (needInput && mStepsBean != null && mStepsBean.enterText !=null){
            mEtMaintenanceResult.setText(mStepsBean.enterText+"");
        }

        if (mStepsBean.comment !=null){
            mEtDesc.setDesc(mStepsBean.comment+"");
        }

    }

    private void initRecyclerView() {
        mSelectList = new ArrayList<>();
        mGridImageAdapter = new GridImageAdapter(mSelectList, false, true, MAX_PHOTO);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getContext(),
                FullyGridLayoutManager.SPAN_COUNT,
                GridLayoutManager.VERTICAL,
                false);
        mPhotoRv.setLayoutManager(manager);
        mPhotoRv.setAdapter(mGridImageAdapter);
        mGridImageAdapter.setOnItemChildClickListener(this);
        mGridImageAdapter.setOnItemClickListener(this);
        if (mStepsBean.photos.size()>0){
            for (String photo : mStepsBean.photos) {
                LocalMedia pic = new LocalMedia();
                pic.setPath(FM.getApiHost()+photo+"");
                pic.setPictureType("local");
                mSelectList.add(pic);
            }
            mGridImageAdapter.setNewData(mSelectList);
            List<String> newPicture = new ArrayList<>();
            for (String photo : mStepsBean.photos) {
                newPicture.add(PatrolQrcodeUtils.getPicturePath(photo));
            }
            mStepsBean.photos = newPicture;
        }
    }

    @Override
    public void onRightTextMenuClick(View view) {
        mRequest = new WorkorderService.WorkorderStepUpdateReq();
        mRequest.woId = mWoId;
        mRequest.stepId = mStepsBean == null ? null : mStepsBean.stepId;
        mRequest.comment = mEtDesc.getDesc().toString();
        mStepsBean.comment = mRequest.comment;
        mRequest.photos = mStepsBean.photos;
        if (mAbNormal.isChecked()){
            haveRemark = true;
            mRequest.finished = false;
        }

        if (mNormal.isChecked()){
            haveRemark = false;
            mRequest.finished = true;
        }

        if (!mAbNormal.isChecked() && !mNormal.isChecked()){
            mRequest.finished = null;
        }

        if (needInput && TextUtils.isEmpty(mEtMaintenanceResult.getText()+"")){
            ToastUtils.showShort("请输入维护结果");
            return;
        }

        if (needInput){
            mRequest.enterText = mEtMaintenanceResult.getText()+"";
        }

        if (needCountAccord &&TextUtils.isEmpty(mEquipmentNumber.getText()+"")){
            ToastUtils.showShort("请输入设备数量");
            return;
        }
        if (needCountAccord){
            mRequest.eqNumber = Integer.parseInt(mEquipmentNumber.getText()+"");
        }

        if (!needInput && haveRemark && TextUtils.isEmpty(mEtDesc.getDesc()+"")){
            ToastUtils.showShort("请输入备注");
            return;
        }

        if (mSelectList.size() > 0) {
            showLoading();
            getPresenter().uploadFile(mSelectList);
        } else {
            showLoading();
            getPresenter().updateStep();
        }
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (view.getId() == R.id.iv_photo3) {
            new BottomTextListSheetBuilder(getContext())
                    .setShowTitle(true)
                    .setTitle(R.string.workorder_select_photo_title)
                    .addItem(R.string.workorder_select_camera)
                    .addItem(R.string.workorder_select_photo)
                    .addItem(R.string.workorder_cancel)
                    .setOnSheetItemClickListener(WorkorderStepUpdateFragment.this)
                    .build()
                    .show();
        } else if (view.getId() == R.id.ll_del) {
            new FMWarnDialogBuilder(getContext()).setIconVisible(false)
                    .setSureBluBg(true)
                    .setTitle(R.string.workorder_tip_title)
                    .setSure(R.string.workorder_confirm)
                    .setTip(R.string.workorder_delete_picture)
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
                                }
                            }
                            tempAdapter.remove(position);
                            if (item.getPictureType().equals("local")){
                                mStepsBean.photos.remove(position);
                            }


//                            if (!"".equals(path)) {
//                                FileUtils.deleteFile(path);
//                            }
                        }
                    }).create(R.style.fmDefaultWarnDialog).show();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PictureSelector.create(WorkorderStepUpdateFragment.this)
                .themeStyle(R.style.picture_fm_style)
                .openExternalPreview(position, mSelectList);
    }

    @Override
    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
        if (position == 0) {
            if (mSelectList.size() < MAX_PHOTO) {
                PictureSelectorManager.camera(WorkorderStepUpdateFragment.this, PictureConfig.REQUEST_CAMERA);
            } else {
                ToastUtils.showShort(String.format(Locale.getDefault(), getString(R.string.workorder_select_photo_at_most), MAX_PHOTO));
            }
        } else if (position == 1) {
            PictureSelectorManager.MultipleChoose(WorkorderStepUpdateFragment.this, MAX_PHOTO, mSelectList, PictureConfig.CHOOSE_REQUEST);
        }
        dialog.dismiss();
    }

    public WorkorderService.WorkorderStepUpdateReq getRequest() {
        return mRequest;
    }

    public void setPhoto(List<String> photo) {
        if (mStepsBean != null && photo != null) {
            if (mStepsBean.photos != null) {
                mStepsBean.photos.addAll(photo);
            } else {
                mStepsBean.photos = photo;
            }
        }
        mRequest.photos = mStepsBean.photos;

    }

    public void setBundle() {
        setFragmentResult(RESULT_OK, null);
        pop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    mSelectList.clear();
                    mSelectList.addAll(selectList);
                    mGridImageAdapter.replaceData(mSelectList);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    List<LocalMedia> selectCamera = PictureSelector.obtainMultipleResult(data);
                    mSelectList.addAll(selectCamera);
                    mGridImageAdapter.replaceData(mSelectList);
                    break;
            }
        }
    }

    public static WorkorderStepUpdateFragment getInstance(WorkorderService.StepsBean step
            , Long woId,boolean countAccord,String attention) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(WORKORDER_STEP, step);
        bundle.putLong(WORKORDER_ID, woId);
        bundle.putBoolean(COUNT_ACCORD, countAccord);
        bundle.putString(ATTENTION,attention);
        WorkorderStepUpdateFragment fragment = new WorkorderStepUpdateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
