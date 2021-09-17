package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.DatePickUtils;
import com.facilityone.wireless.a.arch.utils.ViewUtil;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.basiclib.utils.DateUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.model.BatchService;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.presenter.BatchModifyPresenter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by peter.peng on 2018/11/29.
 * 批次修改界面
 */

public class BatchModifyFragment extends BaseFragment<BatchModifyPresenter> implements View.OnClickListener {
    private static final String INVENTORY_ID = "inventory_id";
    private static final String INVENTORY_PRICE = "inventory_price";
    public static final String DATA_BATCH = "data_batch";
    private static final int SELECT_PROVIDER_REQUEST_CODE = 3001;

    private CustomContentItemView mProviderEt;
//    private ImageView mSelectProviderIv;
    private CustomContentItemView mSelectDueDateTv;
    private CustomContentItemView mPriceEt;
    private CustomContentItemView mNumberEt;

    private long mInventoryId;//库存id
    private String mPrice;
    private Calendar mDueCalendar = Calendar.getInstance();
    private BatchService.Batch mBatch;

    @Override
    public BatchModifyPresenter createPresenter() {
        return new BatchModifyPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_batch_modify;
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
        if (bundle != null) {
            mInventoryId = bundle.getLong(INVENTORY_ID, -1);
            mBatch = bundle.getParcelable(DATA_BATCH);
            mPrice = bundle.getString(INVENTORY_PRICE,"");
        }

        if (mInventoryId == -1) {
            ToastUtils.showShort(R.string.inventory_material_code_exception);
            pop();
            return;
        }
    }

    private void initView() {
        if(mBatch != null) {
            setTitle(R.string.inventory_edit_batches_title);
        }else {
            setTitle(R.string.inventory_add_batches_title);
        }

        setRightTextButton(R.string.inventory_save,R.id.inventory_batch_save_id);
        mProviderEt = findViewById(R.id.batch_modify_provider_et);
//        mSelectProviderIv = findViewById(R.id.batch_modify_select_provider_iv);
        mSelectDueDateTv = findViewById(R.id.batch_modify_select_due_date_tv);
        mPriceEt = findViewById(R.id.batch_modify_price_et);
        mNumberEt = findViewById(R.id.batch_modify_number_et);

        mPriceEt.getInputEt().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mNumberEt.getInputEt().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ViewUtil.setNumberPoint(mPriceEt.getInputEt(), 2);
        ViewUtil.setNumberPoint(mNumberEt.getInputEt(), 2);

        if (mBatch != null) {
            mProviderEt.setInputText(StringUtils.formatString(mBatch.providerName));
            mSelectDueDateTv.setTipText(mBatch.dueDate == null ? "" : TimeUtils.date2String(new Date(mBatch.dueDate), DateUtils.SIMPLE_DATE_FORMAT_YMD));
            mPriceEt.setInputText(StringUtils.formatStringCost(mBatch.price));
            mNumberEt.setInputText(mBatch.number == null ? "" : StringUtils.formatFloatCost(mBatch.number));
        }else {
            mPriceEt.setInputText(StringUtils.formatStringCost(mPrice));
            mBatch = new BatchService.Batch();
        }

        mProviderEt.setOnClickListener(this);
        mSelectDueDateTv.setOnClickListener(this);
        mProviderEt.getInputEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();
                if (!(!TextUtils.isEmpty(name)
                        && !TextUtils.isEmpty(mBatch.providerName)
                        && name.equals(mBatch.providerName))) {
                    mBatch.providerId = null;
                }
            }
        });

    }

    @Override
    public void onRightTextMenuClick(View view) {
        super.onRightTextMenuClick(view);
        if(view.getId() == R.id.inventory_batch_save_id) {
            if(isValidValue()) {
                //保存批次信息并返回上一界面
                saveBatchInfo();
            }

        }
    }

    /**
     * 判断界面的输入值是否有效
     * @return
     */
    private boolean isValidValue() {
        if (TextUtils.isEmpty(mProviderEt.getInputText())) {
            ToastUtils.showShort(R.string.inventory_material_create_provider_name_empty_hint);
            return false;
        }

        if (TextUtils.isEmpty(mSelectDueDateTv.getTipText())){
            ToastUtils.showShort(R.string.inventory_select_expiration_time_hint);
            return false;
        }

        if (TextUtils.isEmpty(mPriceEt.getInputText())) {
            ToastUtils.showShort(R.string.inventory_material_create_price_empty_hint);
            return false;
        }

        if(TextUtils.isEmpty(mNumberEt.getInputText())) {
            ToastUtils.showShort(R.string.inventory_material_input_number_empty_hint);
            return false;
        }
        return true;
    }


    /**
     * 保存批次信息并返回上一界面
     */
    private void saveBatchInfo() {
        mBatch.providerName = mProviderEt.getInputText().toString().trim();
        mBatch.price = StringUtils.formatStringCost(mPriceEt.getInputText().toString().trim());
        mBatch.number = TextUtils.isEmpty(mNumberEt.getInputText()) ? null : Float.parseFloat(mNumberEt.getInputText().toString().trim());

        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_BATCH,mBatch);
        setFragmentResult(RESULT_OK,bundle);
        pop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.batch_modify_provider_et) {
            startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_PROVIDER, mInventoryId), SELECT_PROVIDER_REQUEST_CODE);
        } else if (v.getId() == R.id.batch_modify_select_due_date_tv) {
            selectTime();
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        InventorySelectDataBean selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);

        switch (requestCode) {
            case SELECT_PROVIDER_REQUEST_CODE:
                mProviderEt.setInputText(StringUtils.formatString(selectDataBean.name));
                mBatch.providerId = selectDataBean.id;
                mBatch.providerName = selectDataBean.name;
                break;
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
                mDueCalendar.set(Calendar.HOUR_OF_DAY,0);
                mDueCalendar.set(Calendar.MINUTE,0);
                mDueCalendar.set(Calendar.SECOND,0);
                mDueCalendar.set(Calendar.MILLISECOND,0);
                mBatch.dueDate = mDueCalendar.getTimeInMillis();
                mSelectDueDateTv.setTipText(TimeUtils.date2String(date, DateUtils.SIMPLE_DATE_FORMAT_YMD));
            }
        });
    }

    public static BatchModifyFragment getInstance(long inventoryId,String price) {
        BatchModifyFragment fragment = new BatchModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(INVENTORY_ID, inventoryId);
        bundle.putString(INVENTORY_PRICE,price);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static BatchModifyFragment getInstance(BatchService.Batch batch, long inventoryId) {
        BatchModifyFragment fragment = new BatchModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(INVENTORY_ID, inventoryId);
        bundle.putParcelable(DATA_BATCH, batch);
        fragment.setArguments(bundle);
        return fragment;
    }
}
