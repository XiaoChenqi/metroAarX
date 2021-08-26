package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facilityone.wireless.a.arch.ec.adapter.GridImageAdapter;
import com.facilityone.wireless.a.arch.ec.module.AttachmentBean;
import com.facilityone.wireless.a.arch.ec.module.Page;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.UrlUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.basiclib.widget.FullyGridLayoutManager;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.InventoryAttachmentAdapter;
import com.facilityone.wireless.inventory.adapter.MaterialRecordAdapter;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.presenter.MaterialInfoPresenter;
import com.joanzapata.iconify.widget.IconTextView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/11/28.
 * 物资批次更改界面
 */

public class MaterialInfoFragment extends BaseFragment<MaterialInfoPresenter> implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    private static final String INVENTORY_ID = "inventory_id";
    private static final String WAREHOUSE_ID = "warehouse_id";
    private static final String MATERIAL_CODE = "material_code";
    private static final String FROM_SCAN = "from_scan";
    public static final int INFO_INVENTORY_IN_REQUEST_CODE = 1201;
    public static final int INFO_INVENTORY_OUT_REQUEST_CODE = 1202;
    public static final int INFO_INVENTORY_MOVE_REQUEST_CODE = 1203;
    public static final int INFO_INVENTORY_CHECK_REQUEST_CODE = 1204;

    private LinearLayout mMaterialInfoTitleLl;//物资信息标题
    private LinearLayout mMaterialInfoLl;//物资信息隐藏部分
    private LinearLayout mPhotoLl;//物资信息图片布局
    private LinearLayout mAttachmentLl;//物资信息附件
    private IconTextView mExpandItv;//标题扩展图标
    private TextView mCodeTv;//物资编码
    private TextView mNameTv;//物资名称
    private TextView mStorageNameTv;//仓库名称
    private TextView mBrandTv;//品牌
    private TextView mShelvesTv;//货架
    private TextView mUnitTv;//单位
    private TextView mModelTv;//型号
    private TextView mPriceTv;//核定价格
    private TextView mMinNumberTv;//最低库存量
    private TextView mTotalNumberTv;//账面数量
    private TextView mReservedNumberTv;//已预订数量
    private TextView mDescTv;//备注
    private RecyclerView mPhotoRv;//图片
    private RecyclerView mAttachmentRv;//附件

    private LinearLayout mReserveCountLl;//标题预定数量
    private TextView mReserveCountTv;//标题预定数量

    private LinearLayout mMaterialRecordLl;//当前物资记录布局
    private RecyclerView mMaterialRecordRv;//当前物资记录
    private LinearLayout mCheckAllLl;//查看全部布局


    private MaterialRecordAdapter mMaterialRecordAdapter;//当前物资记录适配器
    private List<MaterialService.MaterialRecord> mMaterialRecordList;//物资记录列表
    private List<MaterialService.MaterialRecord> mTempRecordList;//物资记录列表

    private ArrayList<LocalMedia> mImageList;//图片列表
    private ArrayList<LocalMedia> mTempImageList;//图片列表
    private GridImageAdapter mGridImageAdapter;//图片适配器

    private InventoryAttachmentAdapter mAttachmentAdapter;//附件适配器
    private List<AttachmentBean> mAttachmentList;//附件列表

    private long mInventoryId = -1;//库存id
    private long mWarehouseId = -1;//仓库id
    private String mCode;//物资编号
    private boolean isFromScan = false;//是否来自扫描
    private StorageService.Storage mStorage;
    private Page mPage;
    private MaterialService.MaterialInfo mMaterialInfo;//物资详情
    private List<StorageService.Storage> mStorageList;//所有仓库列表，用于判断是否有权限


    @Override
    public MaterialInfoPresenter createPresenter() {
        return new MaterialInfoPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_material_info;
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
        getMaterialInfo();
    }


    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mInventoryId = bundle.getLong(INVENTORY_ID, -1);
            isFromScan = bundle.getBoolean(FROM_SCAN, false);
            mWarehouseId = bundle.getLong(WAREHOUSE_ID, -1);
            mCode = bundle.getString(MATERIAL_CODE, "");
        }

        mStorageList = new ArrayList<>();
    }

    private void initView() {
        setTitle(R.string.inventory_material_detail_title);
        mMaterialInfoTitleLl = findViewById(R.id.material_info_title_ll);
        mMaterialInfoLl = findViewById(R.id.material_info_ll);
        mPhotoLl = findViewById(R.id.material_info_photo_ll);
        mAttachmentLl = findViewById(R.id.material_info_attachment_ll);
        mExpandItv = findViewById(R.id.material_info_expand_itv);
        mCodeTv = findViewById(R.id.material_info_code_tv);
        mNameTv = findViewById(R.id.material_info_name_tv);
        mStorageNameTv = findViewById(R.id.material_info_storage_name_tv);
        mBrandTv = findViewById(R.id.material_info_brand_tv);
        mShelvesTv = findViewById(R.id.material_info_shelves_tv);
        mUnitTv = findViewById(R.id.material_info_unit_tv);
        mModelTv = findViewById(R.id.material_info_model_tv);
        mPriceTv = findViewById(R.id.material_info_price_tv);
        mMinNumberTv = findViewById(R.id.material_info_min_number_tv);
        mTotalNumberTv = findViewById(R.id.material_info_total_number_tv);
        mReservedNumberTv = findViewById(R.id.material_info_reserved_number_tv);
        mDescTv = findViewById(R.id.material_info_desc_tv);
        mPhotoRv = findViewById(R.id.material_info_photo_rv);
        mAttachmentRv = findViewById(R.id.material_info_attachment_tv);

        mReserveCountLl = findViewById(R.id.material_info_reserve_count_ll);
        mReserveCountTv = findViewById(R.id.material_info_reserve_count_tv);

        mReserveCountLl.setVisibility(View.GONE);

        mMaterialRecordLl = findViewById(R.id.material_info_record_ll);
        mMaterialRecordRv = findViewById(R.id.material_info_record_rv);
        mCheckAllLl = findViewById(R.id.material_info_record_check_all_ll);

        mAttachmentRv.setNestedScrollingEnabled(false);
        mPhotoRv.setNestedScrollingEnabled(false);
        mMaterialRecordRv.setNestedScrollingEnabled(false);

        if(isFromScan) {
            setMoreMenu();
        }else {
            setMenuVisibility(false);
        }


        mImageList = new ArrayList<>();
        mTempImageList = new ArrayList<>();
        mGridImageAdapter = new GridImageAdapter(mImageList, true);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getContext(),
                FullyGridLayoutManager.SPAN_COUNT,
                GridLayoutManager.VERTICAL,
                false);
        mPhotoRv.setLayoutManager(manager);
        mPhotoRv.setAdapter(mGridImageAdapter);
        mGridImageAdapter.setOnItemClickListener(this);

        mAttachmentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAttachmentList = new ArrayList<>();
        mAttachmentAdapter = new InventoryAttachmentAdapter(mAttachmentList);
        mAttachmentRv.setAdapter(mAttachmentAdapter);
        mAttachmentAdapter.setOnItemClickListener(this);

        mMaterialRecordRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mMaterialRecordList = new ArrayList<>();
        mTempRecordList = new ArrayList<>();
        mMaterialRecordAdapter = new MaterialRecordAdapter(mMaterialRecordList);
        mMaterialRecordRv.setAdapter(mMaterialRecordAdapter);

        mMaterialInfoTitleLl.setOnClickListener(this);
        mCheckAllLl.setOnClickListener(this);
    }

    @Override
    public void onMoreMenuClick(View view) {
        super.onMoreMenuClick(view);
        getPresenter().onMoreMenuClick(mMaterialInfo);
    }

    /**
     * 联网获取物资信息
     */
    private void getMaterialInfo() {
        if(isFromScan) {
            getPresenter().getMaterialInfoByQRCode(mWarehouseId,mCode);
        }else {
            getPresenter().getMaterialInfoById(mInventoryId);
        }
        if (mPage == null) {
            mPage = new Page();
        }
        mPage.reset();
        MaterialService.MaterialRecordListRequest request = new MaterialService.MaterialRecordListRequest();
        request.page = mPage;
        if (isFromScan) {
            request.code = mCode;
            request.warehouseId = mWarehouseId;
            getPresenter().getMaterialRecordListData(request, false);
        } else {
            request.inventoryId = mInventoryId;
            getPresenter().getMaterialRecordListData(request, true);
        }
    }


    /**
     * 联网获取物资信息成功
     *
     * @param data
     */
    public void getMaterialInfoSuccess(MaterialService.MaterialInfo data) {
        mMaterialInfo = data;
        mInventoryId = mMaterialInfo.inventoryId == null ? -1 : mMaterialInfo.inventoryId;
        refreshMaterialInfo();

    }

    /**
     * 刷新显示物资信息
     */
    private void refreshMaterialInfo() {
        if (mMaterialInfo == null) {
            return;
        }

        mTempImageList.clear();
        mImageList.clear();
        mAttachmentList.clear();

        mCodeTv.setText(StringUtils.formatString(mMaterialInfo.code));
        mNameTv.setText(StringUtils.formatString(mMaterialInfo.name));
        mStorageNameTv.setText(StringUtils.formatString(mMaterialInfo.warehouseName));
        mBrandTv.setText(StringUtils.formatString(mMaterialInfo.brand));
        mShelvesTv.setText(StringUtils.formatString(mMaterialInfo.shelves));
        mUnitTv.setText(StringUtils.formatString(mMaterialInfo.unit));
        mModelTv.setText(StringUtils.formatString(mMaterialInfo.model));
        mPriceTv.setText(StringUtils.formatString(mMaterialInfo.price));
        mMinNumberTv.setText(mMaterialInfo.minNumber == null ? "0.00" : StringUtils.formatFloatCost(mMaterialInfo.minNumber));
        mTotalNumberTv.setText(mMaterialInfo.totalNumber == null ? "0.00" : StringUtils.formatFloatCost(mMaterialInfo.totalNumber));
        mReservedNumberTv.setText(mMaterialInfo.reservedNumber == null ? "0.00" : StringUtils.formatFloatCost(mMaterialInfo.reservedNumber));
        mDescTv.setText(StringUtils.formatString(mMaterialInfo.desc));
        if (mMaterialInfo.pictures != null && mMaterialInfo.pictures.size() > 0) {
            mPhotoLl.setVisibility(View.VISIBLE);

            for (String pictureId : mMaterialInfo.pictures) {
                LocalMedia media = new LocalMedia();
                media.setPath(UrlUtils.getImagePath(pictureId));
                media.setDuration(mMaterialInfo.pictures.size());
                media.setPictureType(PictureMimeType.JPEG);
                mTempImageList.add(media);
            }
            if (mTempImageList.size() > FullyGridLayoutManager.SPAN_COUNT) {
                List<LocalMedia> localMedias = mTempImageList.subList(0, FullyGridLayoutManager.SPAN_COUNT);
                mImageList.addAll(localMedias);
            } else {
                if (mTempImageList.size() == FullyGridLayoutManager.SPAN_COUNT) {
                    LocalMedia localMedia = mTempImageList.get(FullyGridLayoutManager.SPAN_COUNT - 1);
                    localMedia.setDuration(-1l);
                }
                mImageList.addAll(mTempImageList);
            }
            mGridImageAdapter.notifyDataSetChanged();
        } else {
            mPhotoLl.setVisibility(View.GONE);
        }
        if (mMaterialInfo.attachment != null && mMaterialInfo.attachment.size() > 0) {
            mAttachmentLl.setVisibility(View.VISIBLE);
            mAttachmentList.addAll(mMaterialInfo.attachment);
        } else {
            mAttachmentLl.setVisibility(View.GONE);
        }

    }

    /**
     * 联网获取物资信息失败
     */
    public void getMaterialInfoError() {

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.material_info_title_ll) {
            Boolean tag = (Boolean) v.getTag();
            if (tag == null) {
                tag = false;
            }

            tag = !tag;
            mMaterialInfoLl.setVisibility(tag ? View.VISIBLE : View.GONE);
            mExpandItv.setText(tag ? R.string.icon_arrow_up : R.string.icon_arrow_down);
            v.setTag(tag);
        } else if (v.getId() == R.id.material_info_record_check_all_ll) {//查看全部
            start(MaterialRecordListFragment.getInstance(mInventoryId, mMaterialInfo.name));
        }
    }


    public List<StorageService.Storage> getStorageList() {
        if (mStorageList == null) {
            mStorageList = new ArrayList<>();
        }
        return mStorageList;
    }


    /**
     * 联网获取物资记录成功后回调
     *
     * @param contents
     */
    public void getMaterialRecordListDataSuccess(List<MaterialService.MaterialRecord> contents) {
        mTempRecordList.clear();
        mMaterialRecordList.clear();
        if (contents != null && contents.size() > 0) {
            mMaterialRecordLl.setVisibility(View.VISIBLE);
            mTempRecordList.addAll(contents);
            if (mTempRecordList.size() < 2) {
                mMaterialRecordList.addAll(mTempRecordList);
                mCheckAllLl.setVisibility(View.GONE);
            } else if (mTempRecordList.size() == 2) {
                mMaterialRecordList.addAll(mTempRecordList);
                mCheckAllLl.setVisibility(View.GONE);
            } else {
                mMaterialRecordList.add(mTempRecordList.get(0));
                mMaterialRecordList.add(mTempRecordList.get(1));
                mCheckAllLl.setVisibility(View.VISIBLE);
            }
            mMaterialRecordAdapter.notifyDataSetChanged();
        } else {
            mMaterialRecordLl.setVisibility(View.GONE);
        }
    }

    /**
     * 联网获取物资记录失败后回调
     */
    public void getMaterialRecordListDataError() {
        mMaterialRecordLl.setVisibility(View.GONE);
    }

    /**
     * 当点击图片列表的item时回调此方法
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == mGridImageAdapter) {
            //预览图片
            PictureSelector.create(this)
                    .themeStyle(R.style.picture_fm_style)
                    .openExternalPreview(position, mTempImageList);
        } else if (adapter == mAttachmentAdapter) {
            AttachmentBean attachmentBean = ((InventoryAttachmentAdapter) adapter).getData().get(position);
            getPresenter().openAttachment(UrlUtils.getAttachmentPath(attachmentBean.src), attachmentBean.name, getContext());
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case INFO_INVENTORY_IN_REQUEST_CODE :
            case INFO_INVENTORY_OUT_REQUEST_CODE :
            case INFO_INVENTORY_MOVE_REQUEST_CODE :
            case INFO_INVENTORY_CHECK_REQUEST_CODE :
                getMaterialInfo();
                break;
        }
    }

    public void setStorage(StorageService.Storage mStorage) {
        this.mStorage = mStorage;
    }

    public StorageService.Storage getStorage() {
        return mStorage;
    }

    public static MaterialInfoFragment getInstance(long inventoryId) {
        MaterialInfoFragment fragment = new MaterialInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(INVENTORY_ID, inventoryId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static MaterialInfoFragment getInstance(String code, long warehouseId, boolean scan) {
        MaterialInfoFragment fragment = new MaterialInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MATERIAL_CODE, code);
        bundle.putLong(WAREHOUSE_ID, warehouseId);
        bundle.putBoolean(FROM_SCAN, scan);
        fragment.setArguments(bundle);
        return fragment;
    }


}
