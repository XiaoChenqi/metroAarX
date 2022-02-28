package com.facilityone.wireless.patrol;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.offline.util.PatrolQrcodeUtils;
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder;
import com.facilityone.wireless.componentservice.patrol.PatrolService;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.patrol.fragment.NfcFragment;
import com.facilityone.wireless.patrol.fragment.PatrolScanFragment;
import com.facilityone.wireless.patrol.fragment.PatrolSpotFragment;
import com.luojilab.component.componentlib.router.Router;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.List;

import static com.blankj.utilcode.util.ToastUtils.showShort;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/11/16 6:07 PM
 */
public class NfcRedTagActivity extends BaseFragmentActivity {
    private NfcFragment mNfcFragment;
    private static final int REQUEST_SPOT = 20002;
    @Override
    public Object createPresenter() {
        return null;
    }

    @Override
    protected int getContextViewId() {
        return R.id.patrol_nfc_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        mNfcFragment = new NfcFragment();
        return mNfcFragment;
    }

    private static final String TAG = "stickynotes";
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNdefExchangeFilters;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            showShort(R.string.patrol_not_support_nfc);
            return;
        }
        mNfcAdapter.setNdefPushMessage(null, this);
        // Handle all of our received NFC intents in this activity.
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT);


        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
        }
        mNdefExchangeFilters = new IntentFilter[]{ ndefDetected };
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mResumed = true;
        // Sticky notes received from Android
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
        if (mNfcAdapter != null) {
            enableNdefExchangeMode();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode

    }

    private void setNoteBody(String body) {
        // 用户是否登录
        boolean logon = SPUtils.getInstance(SPKey.SP_MODEL).getBoolean(SPKey.HAVE_LOGON, false);
        if (!logon) {
            showShort(R.string.patrol_not_login_tip);
            this.finish();
            return;
        }



        /**请勿覆盖,*/
        // 应用是否打开
        List<Activity> activityList = ActivityUtils.getActivityList();
        if (activityList == null) {
            showShort(R.string.patrol_not_open_app_tip);
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

        if (!codeType.equals("PATROL")){
            ToastUtils.showShort("NFC格式校验失败,请匹配正确的设备");
            this.finish();
            return;
        }

        if (!TextUtils.isEmpty(code)) {
            startForResult(PatrolScanFragment.getInstance(code+"",true), REQUEST_SPOT);
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
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
//        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
        mNfcAdapter.enableForegroundDispatch(this,mNfcPendingIntent,null,null);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }


}
