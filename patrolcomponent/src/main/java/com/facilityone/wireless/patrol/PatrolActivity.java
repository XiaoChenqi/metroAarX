package com.facilityone.wireless.patrol;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.offline.util.PatrolQrcodeUtils;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.patrol.fragment.PatrolMenuFragment;
import com.facilityone.wireless.patrol.fragment.PatrolScanFragment;
import com.luojilab.router.facade.annotation.RouteNode;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:巡检首页
 * Date: 2018/7/3 下午4:07
 */

@RouteNode(path = "/patrolHome", desc = "巡检首页")
public class PatrolActivity extends BaseFragmentActivity implements EmptyFragment.OnGoFragmentListener {


    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    private EmptyFragment mInstance;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    @Override
    protected int getContextViewId() {
        return R.id.patrol_main_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_PATROL);
        mInstance.setOnGoFragmentListener(this);
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        MetroUtils.getParamFromMetro(this);
//        initNfc();
    }

    private void initNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
//            showShort(R.string.patrol_not_support_nfc);
            return;
        }
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null)
        {
            ToastUtils.showShort("该手机不支持NFC");
            return;
        }
        setIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            System.out.println("NFC信息如下");
            ///
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            enableNdefExchangeMode();
        }
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            if (messages != null && messages.length > 0) {
                NdefRecord[] ndefRecords = messages[0].getRecords();
                if (ndefRecords != null && ndefRecords.length > 0) {
                    byte[] payload = ndefRecords[0].getPayload();
                    setNoteBody(new String(payload));
                    setIntent(new Intent()); // Consume this intent.
                }
            }
        }
    }

    private void setNoteBody(String body) {
        // 用户是否登录
        boolean logon = SPUtils.getInstance(SPKey.SP_MODEL).getBoolean(SPKey.HAVE_LOGON, false);
        if (!logon) {
//            showShort(R.string.patrol_not_login_tip);
            this.finish();
            return;
        }

        // 应用是否打开
        List<Activity> activityList = ActivityUtils.getActivityList();
        if (activityList == null || activityList.size() < 2) {
//            showShort(R.string.patrol_not_open_app_tip);
            this.finish();
            return;
        }

        if (body.equals("") || body==null){
            ToastUtils.showShort("NFC格式校验失败,请匹配正确的设备");
            this.finish();
            return;
        }

        String code = PatrolQrcodeUtils.parseSpotCode(body);
        String codeType = PatrolQrcodeUtils.parseSpotCodeType(body);

//        if (!codeType.equals("PATROL")){
//            ToastUtils.showShort("NFC格式校验失败,请匹配正确的设备");
//            this.finish();
//            return;
//        }

        if (!TextUtils.isEmpty(code)) {
            new FMWarnDialogBuilder(this).setIconVisible(false)
                    .setSureBluBg(true)
                    .setTitle("NFC读取")
                    .setSure("巡检")
                    .setCancel("工单")
                    .setTip("请选择NFC对接位置")
                    .addOnBtnSureClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, View view) {
//                            startForResult(PatrolScanFragment.getInstance(code+"",true), REQUEST_SPOT);
                            dialog.dismiss();
                        }
                    }).addOnBtnCancelClickListener(new FMWarnDialogBuilder.OnBtnClickListener() {
                @Override
                public void onClick(QMUIDialog dialog, View view) {
                    dialog.dismiss();
                    finish();
                    ToastUtils.showShort("暂未开放");
                }
            }).create(R.style.fmDefaultWarnDialog).show();

        } else {
            ToastUtils.showShort("设备异常，请匹配正确的设备");
            finish();
        }
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
            }
        } else {
//            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
//        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, null, null);
    }

    @Override
    public void goFragment(Bundle bundle) {
        mInstance.startWithPop(PatrolMenuFragment.getInstance(bundle));
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                this.finish();
            } else {
                TOUCH_TIME = System.currentTimeMillis();
                ToastUtils.showShort(R.string.patrol_press_exit_again);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public Object createPresenter() {
        return null;
    }
}
