package com.facilityone.wireless.inventory.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridImageAdapter;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.DatePickUtils;
import com.facilityone.wireless.a.arch.utils.FMFileUtils;
import com.facilityone.wireless.a.arch.utils.PictureSelectorManager;
import com.facilityone.wireless.a.arch.utils.ViewUtil;
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.a.arch.widget.EditNumberView;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.basiclib.widget.FullyGridLayoutManager;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.presenter.InventoryCreatePresenter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.facilityone.wireless.inventory.model.InventoryConstant.SELECT_PROVIDER;

/**
 * Created by peter.peng on 2018/11/26.
 * 新建物资界面
 */

public class InventoryCreateFragment extends BaseFragment<InventoryCreatePresenter> implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    private static final int MAX_PHOTO = 8;
    private static final int CREATE_SELECT_STORAGE_REQUEST_CODE = 1001;
    private static final int CREATE_SELECT_PROVIDER_REQUEST_CODE = 1002;

    private CustomContentItemView mSelectStorageTv;//选择仓库
    private CustomContentItemView mShelvesEt;//货架
    private CustomContentItemView mMaterialNameEt;//物资名称
    private CustomContentItemView mMaterialCodeEt;//物资编码
    private CustomContentItemView mMaterialUnitEt;//单位
    private CustomContentItemView mMaterialBrandEt;//品牌
    private CustomContentItemView mMaterialModelEt;//型号
    private CustomContentItemView mRatifiedPriceEt;//核定价格
    private CustomContentItemView mMinimumStockEt;//最低库存量
    private CustomContentItemView mInitialNumberEt;//初始数量
    private CustomContentItemView mExpirationNumberEt;//提醒提前天数
    private LinearLayout mHideLl;//需要隐藏的布局
    private CustomContentItemView mMaterialProviderEt;//供应商
    private ImageView mSelectProviderIv;//选择供应商
    private CustomContentItemView mMaterialCostEt;//单价
    private CustomContentItemView mSelectDueDateTv;//过期时间
    private EditNumberView mDescEnv;//备注
    private RecyclerView mPhotoRv;
    private Button mSaveBtn;

    private GridImageAdapter mGridImageAdapter;
    private List<LocalMedia> mPhotoList;

    private MaterialService.MaterialCreateRequst mMaterialCreateRequst;

    private Calendar mDueCalendar = Calendar.getInstance();//过期时间
    private float mIinitNum = -1;//初始数量
    private Integer mExpirationNumber = null; //提醒提前天数

    @Override
    public InventoryCreatePresenter createPresenter() {
        return new InventoryCreatePresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_create;
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
    }


    private void initData() {
        mMaterialCreateRequst = new MaterialService.MaterialCreateRequst();
    }

    private void initView() {
        setTitle(R.string.inventory_create_title);

        mSelectStorageTv = findViewById(R.id.civ_inventory_Storage);
        mShelvesEt = findViewById(R.id.civ_inventory_create_shelves);
        mMaterialNameEt = findViewById(R.id.civ_inventory_create_material_name);
        mMaterialCodeEt = findViewById(R.id.civ_inventory_create_material_code);
        mMaterialUnitEt = findViewById(R.id.civ_inventory_create_material_unit);
        mMaterialBrandEt = findViewById(R.id.civ_inventory_create_material_brand);
        mMaterialModelEt = findViewById(R.id.civ_inventory_create_material_model);
        mRatifiedPriceEt = findViewById(R.id.civ_inventory_create_material_ratified_price);
        mMinimumStockEt = findViewById(R.id.civ_inventory_create_material_minimum_stock);
        mInitialNumberEt = findViewById(R.id.civ_inventory_create_material_initial_number);
        mHideLl = findViewById(R.id.inventory_create_hide_ll);
        mMaterialProviderEt = findViewById(R.id.civ_inventory_create_material_provider);
        mSelectProviderIv = findViewById(R.id.inventory_create_select_provider_iv);
        mMaterialCostEt = findViewById(R.id.civ_inventory_create_material_cost);
        mSelectDueDateTv = findViewById(R.id.civ_inventory_create_select_due_date);
        mExpirationNumberEt = findViewById(R.id.civ_inventory_create_select_expiration);
        mDescEnv = findViewById(R.id.inventory_create_material_desc_env);
        mPhotoRv = findViewById(R.id.inventory_create_material_photos_rv);
        mSaveBtn = findViewById(R.id.inventory_create_material_save_btn);

        //控制输入框输入的小数点位数
        mMaterialCostEt.getInputEt().setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        mRatifiedPriceEt.getInputEt().setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        mMinimumStockEt.getInputEt().setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        mInitialNumberEt.getInputEt().setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        //提醒提前天数 正整数
        mExpirationNumberEt.getInputEt().setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_NORMAL);
        ViewUtil.setNumberPoint(mRatifiedPriceEt.getInputEt(), 2);
        ViewUtil.setNumberPoint(mMinimumStockEt.getInputEt(), 2);
        ViewUtil.setNumberPoint(mInitialNumberEt.getInputEt(), 2);
        ViewUtil.setNumberPoint(mMaterialCostEt.getInputEt(), 2);

        FullyGridLayoutManager gridLayoutManager = new FullyGridLayoutManager(getContext(), FullyGridLayoutManager.SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        mPhotoRv.setLayoutManager(gridLayoutManager);
        mPhotoList = new ArrayList<>();
        mGridImageAdapter = new GridImageAdapter(mPhotoList, false, true, MAX_PHOTO);
        mPhotoRv.setAdapter(mGridImageAdapter);
        mGridImageAdapter.setOnItemChildClickListener(this);
        mGridImageAdapter.setOnItemClickListener(this);

        //设置选择仓库点击事件
        mSelectStorageTv.setOnClickListener(this);
        //设置保存按钮点击事件
        mSaveBtn.setOnClickListener(this);
        //设置选择供应商点击事件
        mSelectProviderIv.setOnClickListener(this);
        //设置选择过期时间点击事件
        mSelectDueDateTv.setOnClickListener(this);

        mInitialNumberEt.getInputEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strNum = s.toString().trim();
                if (TextUtils.isEmpty(strNum)) {
                    mHideLl.setVisibility(View.GONE);
                    mInitialNumberEt.showDashLine(false);
                }

                if (!TextUtils.isEmpty(strNum) ) {
                    if (strNum.startsWith(".")){
                        ToastUtils.showShort("数量填写异常");
                        return;
                    }
                    mIinitNum = Float.parseFloat(strNum);
                    if (mIinitNum > 0) {
                        mHideLl.setVisibility(View.VISIBLE);
                        mInitialNumberEt.showDashLine(true);
                    } else {
                        mIinitNum = -1; //默认值
                        mHideLl.setVisibility(View.GONE);
                        mInitialNumberEt.showDashLine(false);
                    }
                }else {
                    mIinitNum = -1; //默认值
                }
            }
        });

        mMaterialProviderEt.getInputEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMaterialCreateRequst != null) {
                    String name = s.toString();
                    if (!(!TextUtils.isEmpty(name)
                            && !TextUtils.isEmpty(mMaterialCreateRequst.providerName)
                            && name.equals(mMaterialCreateRequst.providerName))) {
                        mMaterialCreateRequst.providerId = null;
                    }
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.civ_inventory_Storage) {
            Long emId = FM.getEmId();
            if(emId != null) {
                startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_STORAGE, emId), CREATE_SELECT_STORAGE_REQUEST_CODE);
            }
        } else if (v.getId() == R.id.inventory_create_material_save_btn) {
            saveMaterialInfo();
        } else if (v.getId() == R.id.inventory_create_select_provider_iv) {
            startForResult(InventorySelectDataFragment.getInstance(SELECT_PROVIDER), CREATE_SELECT_PROVIDER_REQUEST_CODE);
        } else if (v.getId() == R.id.civ_inventory_create_select_due_date) {
            selectTime();
        }
    }

    /**
     * 联网保存物资信息
     */
    private void saveMaterialInfo() {
        //判断界面的输入值是否有效
        if (isValidValue()) {
            long warehouseId = -1;
            if (mMaterialCreateRequst.warehouseId != null) {
                warehouseId = mMaterialCreateRequst.warehouseId;
            }
            String code = mMaterialCodeEt.getInputText().trim();
            //联网判断当前仓库中该物资是否已经存在
            getPresenter().MaterialExist(warehouseId, code);
        }
    }

    /**
     * 判断界面的输入值是否有效
     * @return
     */
    private boolean isValidValue() {
        if (TextUtils.isEmpty(mSelectStorageTv.getTipText())) {
            ToastUtils.showShort(R.string.inventory_storage_empty_hint);
            return false;
        }

        if (TextUtils.isEmpty(mMaterialNameEt.getInputText())) {
            ToastUtils.showShort(R.string.inventory_create_material_name_empty_hint);
            return false;
        } else if (mMaterialNameEt.getInputText().length() < 2) {
            ToastUtils.showShort(R.string.inventory_material_name_length_hint);
            return false;
        }

        if (TextUtils.isEmpty(mMaterialCodeEt.getInputText())) {
            ToastUtils.showShort(R.string.inventory_create_code_empty_hint);
            return false;
        } else if (mMaterialCodeEt.getInputText().length() < 2) {
            ToastUtils.showShort(R.string.inventory_material_code_length_hint);
            return false;
        }

        if (TextUtils.isEmpty(mMaterialUnitEt.getInputText())) {
            ToastUtils.showShort(R.string.inventory_create_unit_empty_hint);
            return false;
        }

        if (mIinitNum > 0) {
            if (TextUtils.isEmpty(mMaterialProviderEt.getInputText())) {
                ToastUtils.showShort(R.string.inventory_create_provider_name_empty_hint);
                return false;
            }

            if (TextUtils.isEmpty(mMaterialCostEt.getInputText())) {
                ToastUtils.showShort(R.string.inventory_create_price_empty_hint);
                return false;
            }
            //过期时间必填
            if (TextUtils.isEmpty(mSelectDueDateTv.getTipText())){
                ToastUtils.showShort(R.string.inventory_material_create_due_date_select_hint);
                return false;
            }
        }

        return true;
    }

    /*
    获取物资信息
     */
    public void getMaterialInfo() {
        mMaterialCreateRequst.shelve = mShelvesEt.getInputText().trim();
        mMaterialCreateRequst.name = mMaterialNameEt.getInputText().trim();
        mMaterialCreateRequst.code = mMaterialCodeEt.getInputText().trim();
        mMaterialCreateRequst.unit = mMaterialUnitEt.getInputText().trim();
        mMaterialCreateRequst.brand = mMaterialBrandEt.getInputText().trim();
        mMaterialCreateRequst.model = mMaterialModelEt.getInputText().trim();
        mMaterialCreateRequst.checkPrice = TextUtils.isEmpty(mRatifiedPriceEt.getInputText()) ? null : Float.parseFloat(mRatifiedPriceEt.getInputText().trim());
        mMaterialCreateRequst.minNumber = TextUtils.isEmpty(mMinimumStockEt.getInputText()) ? null : Float.parseFloat(mMinimumStockEt.getInputText().trim());
        mMaterialCreateRequst.initialNumber = TextUtils.isEmpty(mInitialNumberEt.getInputText()) ? null : Float.parseFloat(mInitialNumberEt.getInputText().trim());
        mMaterialCreateRequst.providerName = mMaterialProviderEt.getInputText().trim();
        mMaterialCreateRequst.price = TextUtils.isEmpty(mMaterialCostEt.getInputText()) ? null : Float.parseFloat(mMaterialCostEt.getInputText().trim());
        mMaterialCreateRequst.desc = mDescEnv.getDesc().trim();
        if (!TextUtils.isEmpty(mExpirationNumberEt.getInputText())){
            mMaterialCreateRequst.remindAhead = Integer.parseInt(mExpirationNumberEt.getInputText());
        }


        //联网保存物资信息(上传图片)
        if (mPhotoList != null && mPhotoList.size() > 0) {
            getPresenter().uploadFile(mPhotoList);
        } else {
            getPresenter().saveMaterialInfo();
        }
    }

    /**
     * 选择过期时间
     */
    private void selectTime() {
        KeyboardUtils.hideSoftInput(getActivity());
        DatePickUtils.pickDateDefaultYMD(getActivity(), mDueCalendar, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mDueCalendar.setTime(date);
                mMaterialCreateRequst.dueDate = mDueCalendar.getTimeInMillis();
                mSelectDueDateTv.setTipText(TimeUtils.date2String(date, DateUtils.SIMPLE_DATE_FORMAT_YMD));
            }
        });
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);

        switch (requestCode) {
            case CREATE_SELECT_STORAGE_REQUEST_CODE:
                mSelectStorageTv.setTipText(StringUtils.formatString(selectDataBean.name));
                mMaterialCreateRequst.warehouseId = selectDataBean.id;
                break;
            case CREATE_SELECT_PROVIDER_REQUEST_CODE:
                mMaterialProviderEt.setInputText(StringUtils.formatString(selectDataBean.name));
                mMaterialCreateRequst.providerId = selectDataBean.id;
                mMaterialCreateRequst.providerName = selectDataBean.name;
                break;
        }
    }

    /**
     * 当点击图片的删除按钮时回调
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (view.getId() == R.id.iv_photo3) {
            new BottomTextListSheetBuilder(getContext())
                    .setShowTitle(true)
                    .setTitle(R.string.inventory_select_photo_title)
                    .addItem(R.string.inventory_take_photo)
                    .addItem(R.string.inventory_select_photo)
                    .addItem(R.string.inventory_cancel)
                    .setOnSheetItemClickListener(new BottomTextListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                            dialog.dismiss();
                            if (tag.equals(getString(R.string.inventory_take_photo))) {
                                if (mPhotoList.size() < MAX_PHOTO) {
                                    PictureSelectorManager.camera(InventoryCreateFragment.this, PictureConfig.REQUEST_CAMERA);
                                } else {
                                    ToastUtils.showShort(String.format(Locale.getDefault(), getString(R.string.inventory_select_photo_at_most), MAX_PHOTO));
                                }
                            } else if (tag.equals(getString(R.string.inventory_select_photo))) {
                                PictureSelectorManager.MultipleChoose(InventoryCreateFragment.this, MAX_PHOTO, mPhotoList, PictureConfig.CHOOSE_REQUEST);
                            }
                        }
                    })
                    .build()
                    .show();
        } else if (view.getId() == R.id.ll_del) {
            new FMWarnDialogBuilder(getContext())
                    .setIconVisible(false)
                    .setSureBluBg(true)
                    .setTitle(R.string.inventory_remind)
                    .setSure(R.string.inventory_sure)
                    .setTip(R.string.inventory_sure_delete_photo)
                    .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, View view) {
                            dialog.dismiss();
                            GridImageAdapter imageAdapter = (GridImageAdapter) adapter;
                            LocalMedia media = imageAdapter.getData().get(position);
                            String path = "";
                            if (media != null) {
                                if (media.isCut() && !media.isCompressed()) {
                                    path = media.getCutPath();
                                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                                    path = media.getCompressPath();
                                }
                            }

                            imageAdapter.remove(position);

//                            if (!TextUtils.isEmpty(path)) {
//                                FileUtils.deleteFile(path);
//                            }
                        }
                    })
                    .create(R.style.fmDefaultWarnDialog)
                    .show();
        }
    }

    /**
     * 当点击图片的每一项时回调
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //预览图片
        PictureSelector.create(InventoryCreateFragment.this)
                .themeStyle(R.style.picture_fm_style)
                .openExternalPreview(position, mPhotoList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PictureConfig.REQUEST_CAMERA://拍照后返回
                List<LocalMedia> selectCamera = PictureSelector.obtainMultipleResult(data);
                if (selectCamera != null && selectCamera.size() > 0) {
                    mPhotoList.addAll(selectCamera);
                }
                mGridImageAdapter.notifyDataSetChanged();
                break;
            case PictureConfig.CHOOSE_REQUEST://从相册中选择图片后返回
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() > 0) {
                    mPhotoList.clear();
                    mPhotoList.addAll(selectList);
                }
                mGridImageAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //删除所有照片文件
        FileUtils.deleteAllInDir(FMFileUtils.getPicPath());
    }

    public static InventoryCreateFragment getInstance() {
        InventoryCreateFragment fragment = new InventoryCreateFragment();
        return fragment;
    }

    public MaterialService.MaterialCreateRequst getMaterialCreateRequst() {
        return mMaterialCreateRequst;
    }
}
