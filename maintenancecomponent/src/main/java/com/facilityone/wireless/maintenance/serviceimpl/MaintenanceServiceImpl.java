package com.facilityone.wireless.maintenance.serviceimpl;

import android.os.Bundle;

import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.componentservice.maintenance.MaintenanceService;
import com.facilityone.wireless.maintenance.fragment.MaintenanceContentFragment;
import com.facilityone.wireless.maintenance.fragment.MaintenanceElectronicLedgerFragment;
import com.facilityone.wireless.maintenance.fragment.MaintenanceFragment;
import com.facilityone.wireless.maintenance.fragment.MaintenanceMenuFragment;
import com.facilityone.wireless.maintenance.presenter.MaintenanceMenuPresenter;

/**
 * Created by peter.peng on 2018/11/15.
 */

public class MaintenanceServiceImpl implements MaintenanceService {
    @Override
    public BaseFragment getFragment(Bundle bundle) {
        return MaintenanceMenuFragment.getInstance(bundle);
    }

    @Override
    public BaseFragment getPmInfo(Long pmId, Long todoId) {
        return MaintenanceContentFragment.getInstance(true, pmId, todoId);
    }

    @Override
    public BaseFragment getElectronicLedger() {
        return MaintenanceElectronicLedgerFragment.getInstance(1);
    }


}
