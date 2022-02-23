package com.facilityone.wireless.workorder
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.facilityone.wireless.a.arch.base.BaseActivity
import com.facilityone.wireless.a.arch.ec.utils.SPKey
import com.facilityone.wireless.a.arch.offline.util.PatrolQrcodeUtils
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.workorder.adapter.WorkOrderSpaceAdapter
import com.facilityone.wireless.workorder.databinding.ActivityWorkordernfcBinding
import com.facilityone.wireless.workorder.module.WorkorderConstant
import com.facilityone.wireless.workorder.module.WorkorderService
import com.facilityone.wireless.workorder.presenter.WorkOrderNFCPresenter
import com.joanzapata.iconify.widget.IconTextView
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

/**
 * @Creator:Karelie
 * @Data: 2021/11/16
 * @TIME: 15:09
 * @Introduce: 新位置界面（为解决NFC问题定制）
**/
class WorkOrderNfcList : BaseActivity<WorkOrderNFCPresenter>() {
    lateinit var binding: ActivityWorkordernfcBinding
    private var mNfcAdapter: NfcAdapter? = null
    private var mNfcPendingIntent: PendingIntent? = null
    private var mLocationsBeen: List<WorkorderService.PmSpaceBean>? = null
    val WORKORDER_LOCATION = "workorder_location"
    private val CAN_OPT = "can_opt"
    val WORKORDER_ID = "workorder_id"
    val WORKORDER_STATUS = "workorder_status"
    private var mAdapter: WorkOrderSpaceAdapter? = null
    private var mCanOpt = false
    private var workOrderId:Long? = null
    var transaction: FragmentTransaction? = null
    var localWoId : Long? = null //当前工单Id
    var localSpaceId : Long? = null //当前工单位置Id
    var workOrderStatus : Int? = null //当前工单状态
    var nfcTag : String? = null //点击位置后标记当前点位
    private val REFRESH = 500001 // 界面刷新
    var stepEmid :Long?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_workordernfc);
        initData()
        initNfc()
        initView()
        initOnClick()
    }

    private fun initOnClick() {

    }

    override fun createPresenter(): WorkOrderNFCPresenter {
        return WorkOrderNFCPresenter()
    }

    private fun initData() {
        val manager = supportFragmentManager
        transaction= manager.beginTransaction()
        if (intent != null){
            mLocationsBeen = ArrayList()
            mLocationsBeen = intent.getParcelableArrayListExtra(WORKORDER_LOCATION)
            mCanOpt = intent.getBooleanExtra(CAN_OPT,false)
            workOrderId = intent.getLongExtra(WORKORDER_ID,-1L)
            workOrderStatus = intent.getIntExtra(WORKORDER_STATUS,-1)
        }
        mAdapter = WorkOrderSpaceAdapter()
        binding.recyclerView.setLayoutManager(LinearLayoutManager(this))
        binding.recyclerView.adapter = mAdapter
        if (mLocationsBeen != null){
            mAdapter!!.replaceData(mLocationsBeen!!)
        }

        mAdapter!!.setOnItemChildClickListener{ baseQuickAdapter, view, i -> kotlin.run {
            if(mCanOpt){
                if (workOrderStatus == WorkorderConstant.WORK_STATUS_PROCESS){
                    if (!mCanOpt){
                        ToastUtils.showShort("请确认是否接单。")
                    }

                    localSpaceId = mLocationsBeen?.get(i)?.spotId
                    //NFC点位上随机值
                    nfcTag =PatrolQrcodeUtils.parseSpotCode(mLocationsBeen?.get(i)?.nfcTag).trim()
                    stepEmid = mLocationsBeen?.get(i)?.emId
                    val personId : Long = FM.getEmId()
                    if (!personId.equals(stepEmid)){
                        ToastUtils.showShort("这不是您的点位，请重新选择。")
                        nfcTag = null
                        stepEmid = null
                    }else{
                        ToastUtils.showShort("请将手机贴近NFC标签")
                    }

                }
            }

        } }


        /**
        * 右侧添加按钮 四运需求暂不需要
        * */
//        if (mCanOpt){
//            binding.uiTopbar.addRightTextButton(R.string.workorder_add_menu, R.id.workorder_space_add_menu_id)
//                .setOnClickListener {
//                    binding.flWorkOrder.visibility = View.VISIBLE
//                    var fg = WorkorderSpaceAddFragment.getInstance(null, 8, mLocationsBeen)
//                    transaction?.replace(binding.flWorkOrder.id,fg)
//                }
//        }
    }

    private fun initView() {
        binding.uiTopbar.setTitle("空间位置(NFC)")
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        binding.uiTopbar.addLeftView(
            getLeftBackView(),
            com.facilityone.wireless.a.arch.R.id.topbar_left_back_id,
            lp
        )

        QMUIStatusBarHelper.translucent(this, Color.BLACK)
    }

    private fun initNfc() {

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            ToastUtils.showShort("该设备不支持NFC")
            return
        }
        mNfcPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

    }

    override fun onNewIntent(intent: Intent) {
        if (intent == null) {
            ToastUtils.showShort("该手机不支持NFC")
            return
        }

        if (workOrderStatus == WorkorderConstant.WORK_STATUS_NONE){
            return
        }
        setIntent(intent)
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            println("NFC信息如下")
        }
    }

    private fun getLeftBackView(): View? {
        val view =
            LayoutInflater.from(this).inflate(R.layout.fm_topbar_back, null) as IconTextView
        view.setOnClickListener {
            finish()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (mNfcAdapter != null) {
            enableNdefExchangeMode()
        }
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val messages = getNdefMessages(intent)
            if (messages != null && messages.size > 0) {
                val ndefRecords = messages[0]!!.records
                if (ndefRecords != null && ndefRecords.size > 0) {
                    val payload = ndefRecords[0].payload
                    setNoteBody(String(payload))
                    intent = Intent() // Consume this intent.
                }
            }
        }
    }

    private fun setNoteBody(body: String?) {
        // 用户是否登录
        val logon = SPUtils.getInstance(SPKey.SP_MODEL).getBoolean(SPKey.HAVE_LOGON, false)
        if (!logon) {
//            showShort(R.string.patrol_not_login_tip);
            finish()
            return
        }

        if(!mCanOpt){
            ToastUtils.showShort("请确认是否接单。")
            return
        }

        // 应用是否打开
        val activityList = ActivityUtils.getActivityList()
        if (activityList == null || activityList.size < 2) {
//            showShort(R.string.patrol_not_open_app_tip);
            finish()
            return
        }
        if (body == "" || body == null) {
            ToastUtils.showShort("NFC格式校验失败,请匹配正确的设备")
            finish()
            return
        }
        val code = PatrolQrcodeUtils.parseSpotCode(body).trim()
        val codeType = PatrolQrcodeUtils.parseSpotCodeType(body).trim()

        if (!codeType.equals("PATROL")){
            ToastUtils.showShort("NFC格式校验失败,请匹配正确的设备");
//            this.finish();
            return;
        }

        if (!TextUtils.isEmpty(code)) {
            if (workOrderId == null){
                ToastUtils.showShort("数据异常，请联系相关人员")
                return
            }

            if(localSpaceId==null){
                ToastUtils.showShort("请点击匹配的位置")
                return
            }

            if (!code.equals(nfcTag)){
                ToastUtils.showShort("请选择正确的NFC标签")
                return
            }
            showLoading()
            presenter.addSignIn(workOrderId!!,localSpaceId!!)
        } else {
            ToastUtils.showShort("设备异常，请匹配正确的设备")
            finish()
        }

    }

    fun getNdefMessages(intent: Intent): Array<NdefMessage?>? {
        // Parse the intent
        var msgs: Array<NdefMessage?>? = null
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null) {
                msgs = arrayOfNulls(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            } else {
                // Unknown tag type
                val empty = byteArrayOf()
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty)
                val msg = NdefMessage(
                    arrayOf(
                        record
                    )
                )
                msgs = arrayOf(
                    msg
                )
            }
        } else {
//            Log.d(TAG, "Unknown intent.");
            finish()
        }
        return msgs
    }



    private fun enableNdefExchangeMode() {
        mNfcAdapter!!.enableForegroundDispatch(this, mNfcPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            mNfcAdapter!!.disableForegroundDispatch(this)
        }
    }



}