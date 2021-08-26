package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.ViewUtil;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.presenter.MaterialAddPresenter;

/**
 * Created by peter.peng on 2018/12/7.
 * 物资预定物料添加界面
 */

public class MaterialAddFragment extends BaseFragment<MaterialAddPresenter> implements View.OnClickListener {
    private static final String FROM_TYPE = "from_type";
    private static final String WAREHOUSE_ID = "warehouse_id";
    public static final String MATERIAL_INFO = "material_info";
    private static final int ADD_SELECT_MATERIAL_REQUEST_CODE = 10001;

    private CustomContentItemView mMaterialCodeTv;//编码
    private CustomContentItemView mMaterialNameTv;//名称
    private CustomContentItemView mBrandTv;//品牌
    private CustomContentItemView mModelTv;//型号
    private CustomContentItemView mRealNumberTv;//有效数量
    private CustomContentItemView mPriceTv;//费用（单价）
    private CustomContentItemView mReserveNumberEt;//预定数量

    private int mType;
    private long mWarehouseId = -1;//仓库id
    private MaterialService.MaterialInfo mMaterialInfo;

    @Override
    public MaterialAddPresenter createPresenter() {
        return new MaterialAddPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_material_add;
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
        Bundle bundle = getArguments();
        if(bundle != null) {
            mType = bundle.getInt(FROM_TYPE,-1);
            mWarehouseId = bundle.getLong(WAREHOUSE_ID,-1);
            mMaterialInfo = bundle.getParcelable(MATERIAL_INFO);
        }
    }

    private void initView() {
        String title = "";
        String menu = "";
        switch (mType) {
            case InventoryConstant.TYPE_MATERIAL_ADD :
                title = getString(R.string.inventory_add_material_title);
                menu = getString(R.string.inventory_add);
                break;
            case InventoryConstant.TYPE_MATERIAL_MODIFY:
                title = getString(R.string.inventory_edit_material_title);
                menu = getString(R.string.inventory_save);
                break;
        }
        setTitle(title);
        setRightTextButton(menu,R.id.inventory_material_save_id);

        mMaterialCodeTv = findViewById(R.id.material_add_code_tv);
        mMaterialNameTv = findViewById(R.id.material_add_name_tv);
        mBrandTv = findViewById(R.id.material_add_brand_tv);
        mModelTv = findViewById(R.id.material_add_model_tv);
        mRealNumberTv = findViewById(R.id.material_add_real_number_tv);
        mPriceTv = findViewById(R.id.material_add_price_tv);
        mReserveNumberEt = findViewById(R.id.material_add_reserve_number_et);
        mReserveNumberEt.getInputEt().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ViewUtil.setNumberPoint(mReserveNumberEt.getInputEt(),2);

        mMaterialCodeTv.setOnClickListener(this);
        mMaterialNameTv.setOnClickListener(this);

        switch (mType) {
            case InventoryConstant.TYPE_MATERIAL_ADD :
                mMaterialCodeTv.setClickable(true);
                mMaterialNameTv.setClickable(true);
                break;
            case InventoryConstant.TYPE_MATERIAL_MODIFY:
                mMaterialCodeTv.setClickable(false);
                mMaterialNameTv.setClickable(false);
                refreshView();
                break;
        }
    }

    @Override
    public void onRightTextMenuClick(View view) {
        super.onRightTextMenuClick(view);
        if (view.getId() == R.id.inventory_material_save_id) {
            if(mMaterialInfo == null) {
                ToastUtils.showShort(R.string.inventory_material_describe_material_hint);
                return;
            }
        }
        if(TextUtils.isEmpty(mReserveNumberEt.getInputText())) {
            ToastUtils.showShort(R.string.inventory_input_reserve_amount);
            return;
        }

        float reserveNumber = 0;
        try {
             reserveNumber= Float.parseFloat(mReserveNumberEt.getInputText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(reserveNumber <= 0) {
            ToastUtils.showShort(R.string.inventory_reserve_empty_hint);
            return;
        }

        if(reserveNumber > mMaterialInfo.realNumber) {
            ToastUtils.showShort(R.string.inventory_material_no_amount);
            return;
        }

        mMaterialInfo.number = reserveNumber;
        Bundle bundle = new Bundle();
        bundle.putParcelable(MATERIAL_INFO,mMaterialInfo);
        setFragmentResult(RESULT_OK,bundle);
        pop();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.material_add_code_tv || v.getId() == R.id.material_add_name_tv) {//选择物料
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_MATERIAL_RESERVE,mWarehouseId),ADD_SELECT_MATERIAL_REQUEST_CODE);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK || data == null) {
            return;
        }

        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);
        switch (requestCode) {
            case ADD_SELECT_MATERIAL_REQUEST_CODE :
                if(selectDataBean != null) {
                    MaterialService.Material material = (MaterialService.Material) selectDataBean.target;
                    if(material != null) {
                        mMaterialInfo = getPresenter().Material2MaterInfo(material);
                        refreshView();
                    }
                }
                break;
        }
    }

    /**
     * 刷新视图
     */
    private void refreshView() {
        if(mMaterialInfo != null) {
            mMaterialCodeTv.setTipText(StringUtils.formatString(mMaterialInfo.code));
            mMaterialNameTv.setTipText(StringUtils.formatString(mMaterialInfo.name));
            mBrandTv.setTipText(StringUtils.formatString(mMaterialInfo.brand));
            mModelTv.setTipText(StringUtils.formatString(mMaterialInfo.model));
            if(mMaterialInfo.realNumber == null) {
                mMaterialInfo.realNumber = 0f;
            }
            mRealNumberTv.setTipText(StringUtils.formatFloatCost(mMaterialInfo.realNumber));
            mPriceTv.setTipText(StringUtils.formatString(mMaterialInfo.price));
            if(mMaterialInfo.number != null) {
                mReserveNumberEt.setInputText(StringUtils.formatFloatCost(mMaterialInfo.number));
            }
        }
    }


    public static MaterialAddFragment getInstance(int type,long warehouseId){
        MaterialAddFragment fragment = new MaterialAddFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE,type);
        bundle.putLong(WAREHOUSE_ID,warehouseId);
        fragment.setArguments(bundle);
        return fragment;
    }
    public static MaterialAddFragment getInstance(int type, MaterialService.MaterialInfo materialInfo){
        MaterialAddFragment fragment = new MaterialAddFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM_TYPE,type);
        bundle.putParcelable(MATERIAL_INFO,materialInfo);
        fragment.setArguments(bundle);
        return fragment;
    }
}
