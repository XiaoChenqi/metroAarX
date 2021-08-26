package com.facilityone.wireless.inventory.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener;
import com.facilityone.wireless.a.arch.widget.CustomContentItemView;
import com.facilityone.wireless.inventory.R;
import com.facilityone.wireless.inventory.adapter.InventoryPagerAdapter;
import com.facilityone.wireless.inventory.model.InventoryConstant;
import com.facilityone.wireless.inventory.model.InventorySelectDataBean;
import com.facilityone.wireless.inventory.model.MaterialService;
import com.facilityone.wireless.inventory.model.StorageService;
import com.facilityone.wireless.inventory.model.SupervisorService;
import com.facilityone.wireless.inventory.presenter.InventoryOutPresenter;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter.peng on 2018/12/3.
 * 出库界面
 */

public class InventoryOutFragment extends BaseFragment<InventoryOutPresenter> implements ViewPager.OnPageChangeListener {

    private static final int OUT_SELECT_MATERIAL_REQUEST_CODE = 6001;

    private QMUITabSegment mTabSegment;
    private QMUIViewPager mViewPager;

    private List<String> mTitleList;
    private List<BaseFragment> mFragmentList;
    private InventoryPagerAdapter mPagerAdapter;


    @Override
    public InventoryOutPresenter createPresenter() {
        return new InventoryOutPresenter();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_inventory_out;
    }

    @Override
    protected int setTitleBar() {
        return R.id.ui_topbar;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        setTitle(R.string.inventory_out_title);

        mTabSegment = findViewById(R.id.tabSegment);
        mViewPager = findViewById(R.id.viewPager);

        int normalColor = ContextCompat.getColor(getContext(), R.color.grey_6);
        int selectColor = ContextCompat.getColor(getContext(), R.color.green_1ab394);
        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectColor);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(InventoryReserveOutFragment.getInstance());
        mFragmentList.add(InventoryDirectOutFragment.getInstance());
        mTitleList = new ArrayList<>();
        mTitleList.add(getString(R.string.inventory_reserve_out));
        mTitleList.add(getString(R.string.inventory_direct_out));
        mPagerAdapter = new InventoryPagerAdapter(getChildFragmentManager(), mFragmentList, mTitleList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabSegment.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            setRightIcon(R.string.icon_scan, R.id.inventory_material_scan_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    //扫描物资二维码
                    if (mFragmentList == null || mFragmentList.size() == 0) {
                        return;
                    }
                    InventoryDirectOutFragment directOutFragment = (InventoryDirectOutFragment) mFragmentList.get(1);
                    long warehouseId = directOutFragment.getWarehouseId();
                    CustomContentItemView selectStorageTv = directOutFragment.getSelectStorageTv();
                    getPresenter().scanMaterialQRCode(warehouseId,selectStorageTv == null ? "" : selectStorageTv.getTipText());
                }
            });

            setRightIcon(R.string.icon_add, R.id.inventory_material_select_id, R.dimen.topbar_title_size, new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View view) {
                    if (mFragmentList == null || mFragmentList.size() == 0) {
                        return;
                    }
                    InventoryDirectOutFragment directOutFragment = (InventoryDirectOutFragment) mFragmentList.get(1);
                    if (!directOutFragment.isSelectStorage()) {
                        ToastUtils.showShort(R.string.inventory_storage_empty_hint);
                        return;
                    }
                    //选择物料
                    startForResult(InventorySelectDataFragment.getInstance(InventoryConstant.SELECT_MATERIAL_OUT, directOutFragment.getWarehouseId()), OUT_SELECT_MATERIAL_REQUEST_CODE);
                }
            });
        } else if (position == 0) {
            removeRightView();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (mFragmentList == null || mFragmentList.size() == 0) {
            return;
        }

        InventoryDirectOutFragment directOutFragment = (InventoryDirectOutFragment) mFragmentList.get(1);

        if(resultCode != RESULT_OK) {
            if(requestCode == InventoryDirectOutFragment.INVENTORY_DIRECT_OUT_QRCODE_REQUEST_CODE) {
                if(directOutFragment != null) {
                    directOutFragment.removeMaterial();
                }
            }
            return;
        }

        InventorySelectDataBean selectDataBean = null;
        if(requestCode != InventoryReserveOutFragment.INVENTORY_RESERVE_OUT_REQUEST_CODE ) {
            if(data == null) {
                return;
            }else {
                selectDataBean = data.getParcelable(InventorySelectDataFragment.SELECT_DATA);
            }
        }



        switch (requestCode) {
            case InventoryReserveOutFragment.INVENTORY_RESERVE_OUT_REQUEST_CODE://预定详情返回
                InventoryReserveOutFragment reserveOutFragment = (InventoryReserveOutFragment) mFragmentList.get(0);
                if(reserveOutFragment != null) {
                    reserveOutFragment.onRefresh();
                }
                break;
            case OUT_SELECT_MATERIAL_REQUEST_CODE://选择物资
                if(selectDataBean != null && directOutFragment != null) {
                    MaterialService.Material material = (MaterialService.Material) selectDataBean.target;
                    directOutFragment.addMaterial(material);
                }
                break;
            case InventoryDirectOutFragment.DIRECT_OUT_SELECT_STORAGE_REQUEST_CODE://选择仓库
                if(selectDataBean != null && directOutFragment != null) {
                    StorageService.Storage storage = (StorageService.Storage) selectDataBean.target;
                    directOutFragment.refreshStorage(storage);
                }
                break;
            case InventoryDirectOutFragment.DIRECT_OUT_SELECT_RECEIVING_PERSON_REQUEST_CODE://选择领用人
                if(selectDataBean != null && directOutFragment != null) {
                    UserService.UserInfoBean userInfo = (UserService.UserInfoBean) selectDataBean.target;
                    directOutFragment.refreshReceivingPerson(userInfo);
                }
                break;
            case InventoryDirectOutFragment.DIRECT_OUT_SELECT_SUPERVISOR_REQUEST_CODE://选择主管
                if(selectDataBean != null && directOutFragment != null) {
                    SupervisorService.Supervisor supervisor = (SupervisorService.Supervisor) selectDataBean.target;
                    directOutFragment.refreshSuperVisor(supervisor);
                }
                break;
            case InventoryDirectOutFragment.DIRECT_OUT_SELECT_ADMINISTRATOR_REQUEST_CODE://选择仓库管理员
                if(selectDataBean != null && directOutFragment != null) {
                    StorageService.Administrator administrator = new StorageService.Administrator();
                    administrator.administratorId = selectDataBean.id;
                    administrator.name = selectDataBean.name;
                    List<StorageService.Administrator> administratorList = (List<StorageService.Administrator>) selectDataBean.target;
                    directOutFragment.refreshAdministrator(administrator,administratorList);
                }
                break;
            case InventoryDirectOutFragment.INVENTORY_DIRECT_OUT_QRCODE_REQUEST_CODE:
            case InventoryDirectOutFragment.INVENTORY_DIRECT_OUT_REQUEST_CODE:
                MaterialService.MaterialInfo materialInfo =  data.getParcelable(MaterialBatchFragment.DATA_MATERIAL);
                if(directOutFragment != null) {
                    directOutFragment.refreshMaterial(materialInfo);
                }
                break;
        }
    }

    public static InventoryOutFragment getInstance() {
        InventoryOutFragment fragment = new InventoryOutFragment();
        return fragment;
    }

    public InventoryDirectOutFragment getDirectOutFragment() {
        if (mFragmentList == null || mFragmentList.size() == 0) {
            return null;
        }
        InventoryDirectOutFragment directOutFragment = (InventoryDirectOutFragment) mFragmentList.get(1);
        return directOutFragment;
    }
}
