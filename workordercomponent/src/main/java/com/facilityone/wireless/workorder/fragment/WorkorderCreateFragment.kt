package com.facilityone.wireless.workorder.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.facilityone.wireless.a.arch.ec.adapter.GridImageAdapter
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService
import com.facilityone.wireless.a.arch.ec.module.LocationBean
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean
import com.facilityone.wireless.a.arch.ec.module.UserService.UserInfoBean
import com.facilityone.wireless.a.arch.ec.selectdata.SelectDataFragment
import com.facilityone.wireless.a.arch.ec.utils.SPKey
import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.a.arch.utils.FMFileUtils
import com.facilityone.wireless.a.arch.utils.PictureSelectorManager
import com.facilityone.wireless.a.arch.widget.BottomTextListSheetBuilder
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder.OnBtnClickListener
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.basiclib.utils.GsonUtils
import com.facilityone.wireless.basiclib.utils.StringUtils
import com.facilityone.wireless.basiclib.widget.FullyGridLayoutManager
import com.facilityone.wireless.componentservice.workorder.WorkorderService
import com.facilityone.wireless.workorder.R
import com.facilityone.wireless.workorder.adapter.WorkorderCreateDeviceAdapter
import com.facilityone.wireless.workorder.databinding.FragmentWorkorderCreateBinding
import com.facilityone.wireless.workorder.module.WorkorderCreateService
import com.facilityone.wireless.workorder.module.WorkorderCreateService.WorkorderCreateReq
import com.facilityone.wireless.workorder.presenter.WorkorderCreatePresenter
import com.github.mikephil.charting.formatter.IFillFormatter
import com.gyf.barlibrary.ImmersionBar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import me.yokeyword.fragmentation.SwipeBackLayout
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:创建工单
 * Date: 2018/7/4 上午9:20
 */

class WorkorderCreateFragment : BaseFragment<WorkorderCreatePresenter?>(), View.OnClickListener,
    BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener,
    BottomTextListSheetBuilder.OnSheetItemClickListener {

    lateinit var binding:FragmentWorkorderCreateBinding


    //图片
    private var mSelectList: MutableList<LocalMedia>? = null
    private var mGridImageAdapter: GridImageAdapter? = null
    public var request: WorkorderCreateReq? = null
    var newrequest: WorkorderCreateService.newOrderCreate? = null


    //故障设备
    private var mDevices: MutableList<SelectDataBean>? = null
    private var mDevicesOnline: ArrayList<SelectDataBean>? = null
    private var mDeviceOnlineData:SelectDataBean ?= null
    private var list:ArrayList<com.facilityone.wireless.workorder.module.WorkorderService.WorkOrderEquipmentsBean> ?=null
    private var mDeviceAdapter: WorkorderCreateDeviceAdapter? = null
    private var mFromType = 0
    private var mEquipmentId: Long = 0
    private var mEquipmentFullName: String? = null
    private var mOtherLocationBean: LocationBean? = null
    private var mOtherLocationName: String? = null
    private var mOtherMedia: MutableList<LocalMedia>? = null
    private var mItemId: Long? = null
    private var mLocalMedia //拍照或选择
            : MutableList<LocalMedia>? = null

    //需求
    private var mDemandId: Long? = null
    private var mDesc: String? = null
    private var mPhone: String? = null
    private var mPeople: String? = null
    private var mDepSelectData //部门选择数据
            : SelectDataBean? = null
    private var mServiceTypeSelectData //服务类型选择数据
            : SelectDataBean? = null
    private var mWaterMark = false
    private var newOrder = false
    private var reqId : Long? = null
    private var mOtherDepartmentId:Long?=null
    private var patrolDetailId:Long?=null
    lateinit var deviceList: List<com.facilityone.wireless.workorder.module.WorkorderService.WorkOrderEquipmentsBean>
    override fun createPresenter(): WorkorderCreatePresenter {
        return WorkorderCreatePresenter()
    }

    override fun setLayout(): Any {
        return R.layout.fragment_workorder_create
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initRecyclerView()
        presenter!!.getUserInfo()
        initData()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mRootView = if (setLayout() is Int) {
//            inflater.inflate(setLayout() as Int, container, false)
            binding= FragmentWorkorderCreateBinding.inflate(inflater,container,false)
            binding.root
        } else if (setLayout() is View) {
            setLayout() as View
        } else {
            throw ClassCastException("type of setLayout() must be layout resId or view")
        }
        //设置可侧滑返回的区域大小
        swipeBackLayout.setEdgeLevel(SwipeBackLayout.EdgeLevel.MIN)

        return attachToSwipeBack(mRootView)


    }



    private fun initView() {
        var title = "创建工单"
        val bundle = arguments
        deviceList = ArrayList()
        newrequest = WorkorderCreateService.newOrderCreate()
        mDeviceOnlineData = SelectDataBean()
        binding.civContact.inputText =SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.EM_NAME)
        binding.civContact.canInput(false)
        if (bundle != null) {
            mFromType = bundle.getInt(FROM_TYPE, -1)
            mWaterMark = bundle.getBoolean(WATER_MARK, false)
            mEquipmentId = bundle.getLong(EQUIPMENT_ID, -1L)
            mEquipmentFullName = bundle.getString(EQUIPMENT_STR_ID)
            mItemId = bundle.getLong(ITEM_ID, -1L)
            mDemandId = bundle.getLong(DEMAND_ID, -1L)
            mOtherLocationBean = bundle.getParcelable(LOCATION_INFO)
            mOtherLocationName = bundle.getString(LOCATION_NAME, "")
            mPeople = bundle.getString(ORDER_PEOPLE, "")
            mPhone = bundle.getString(ORDER_PHONE, "")
            mDesc = bundle.getString(ORDER_DESC, "")
            mOtherDepartmentId = bundle.getLong(DEPARTMENT_ID,-1L)
            mOtherMedia = bundle.getParcelableArrayList(PIC_INFO)
            if (bundle.getLong(PATROL_DETAIL_ID) != null ){
                if (bundle.getLong(PATROL_DETAIL_ID) != 0L){
                    patrolDetailId = bundle.getLong(PATROL_DETAIL_ID)
                }
            }


            if ((mFromType == WorkorderService.CREATE_ORDER_BY_OTHER
                        || mFromType == WorkorderService.CREATE_ORDER_BY_PATROL_QUERY_REPAIR)
            ) {
                title = getString(R.string.workorder_report_fault)
            }
            if (mItemId == -1L) {
                mItemId = null
            }
            if (mDemandId == -1L) {
                mDemandId = null
            }
            newOrder = bundle.getBoolean(IS_NEWORDER,false);

            if (newOrder){
                reqId = bundle.getLong(ORDER_REQID)
                binding.civTel.inputText = mPhone
                binding.civContact.inputText = bundle.getString(ORDER_PEOPLE)
                if(mDesc==null || mDesc.equals("")){
                    binding.envDesc.desc = " "
                }else{
                    binding.envDesc.desc = mDesc
                }

                if (bundle.getString(DEPARTMENT_NAME)==null||bundle.getString(DEPARTMENT_NAME).equals("")){
                    binding.civDep.tipText = " "
                }else{
                    binding.civDep.tipText = bundle.getString(DEPARTMENT_NAME)


                }

                if(bundle.getString(LOCATION_NAME)==null||bundle.getString(LOCATION_NAME).equals("")){
                    binding.civLocation.tipText = " "
                }else{
                    binding.civLocation.tipText = bundle.getString(LOCATION_NAME)
                }

                mDevicesOnline = ArrayList()
                list = ArrayList()
                if (bundle.getParcelableArrayList<com.facilityone.wireless.workorder.module.WorkorderService.WorkOrderEquipmentsBean>(DEVICE_LIST) != null){
                    list = bundle.getParcelableArrayList(DEVICE_LIST)
//                    list.addAll(bundle.getParcelableArray(DEVICE_LIST) as ArrayList<com.facilityone.wireless.workorder.module.WorkorderService.WorkOrderEquipmentsBean>)
                }

                if (list!!.size>0){
                    for (data : com.facilityone.wireless.workorder.module.WorkorderService.WorkOrderEquipmentsBean in list!!){
                        mDeviceOnlineData!!.name = data.equipmentName
                        mDeviceOnlineData!!.fullName = data.equipmentCode
                        mDeviceOnlineData!!.desc = data.location
                        mDevicesOnline!!.add(mDeviceOnlineData!!)
                    }
                }

                when(bundle.getInt(WORKORDER_TYPE)){
                    0-> binding.civWorkorderType.tipText = "纠正性维护"
                    1-> binding.civWorkorderType.tipText = "自检"
                }
                binding.civPriority.tipText = bundle.getString(PRIORITY)
                binding.civServiceType.tipText = bundle.getString(SERVICETYPE)
                /**
                 * 新派单不可输入
                 * */
                binding.envDesc.setInputDisp(false)
                binding.envDesc.canInput(false)

                binding.civTel.canInput(false)
                binding.ivAddMenu.visibility = View.GONE
                binding.rvPhoto.visibility = View.GONE
                binding.civDep.showIcon(false)
                binding.civLocation.showIcon(false)
                /**
                 *  bundle.putLong(ORDER_REQID,data.reqId)
                bundle.putLong(ORDER_SERVICEID,data.serviceTypeId)
                bundle.putLong(ORDER_FLOWID,data.processId)
                bundle.putLong(ORDER_PRIORITYID,data.priorityId)
                 * */
                newrequest!!.woId = bundle.getLong(ORDER_REQID)
                newrequest!!.serviceTypeId = bundle.getLong(ORDER_SERVICEID)
                newrequest!!.flowId = bundle.getLong(ORDER_FLOWID)
                newrequest!!.priorityId = bundle.getLong(ORDER_PRIORITYID)
                newrequest!!.orderType = bundle.getInt(WORKORDER_TYPE)

            }

        }
        /**
         * 四运
         * */
        if (newOrder){
            setTitle("新派工单")
            binding.civServiceType.setOnClickListener(this)
            binding.civWorkorderType.setOnClickListener(this)
            binding.civPriority.setOnClickListener(this)
            binding.ivAddMenu.setOnClickListener(this)
            binding.civWorkorderType.tipText=getString(R.string.workorder_report_self)
        }else{
            setTitle(title)
            binding.civDep.setOnClickListener(this)
            binding.civLocation.setOnClickListener(this)
            binding.civServiceType.setOnClickListener(this)
            binding.civWorkorderType.setOnClickListener(this)
            binding.civPriority.setOnClickListener(this)
            binding.ivAddMenu.setOnClickListener(this)
            binding.civWorkorderType.tipText=getString(R.string.workorder_report_self)
        }

        setRightTextButton(R.string.workorder_submit, R.id.workorder_upload_menu_id)

    }

    private fun initRecyclerView() {
        mSelectList = ArrayList()
        mLocalMedia = ArrayList()
        if (mOtherMedia != null && mOtherMedia!!.size > 0) {
           (mSelectList as ArrayList<LocalMedia>).addAll(mOtherMedia!!)
        }
        mGridImageAdapter = GridImageAdapter(mSelectList, false, true, MAX_PHOTO)
        val manager = FullyGridLayoutManager(
            context,
            FullyGridLayoutManager.SPAN_COUNT,
            GridLayoutManager.VERTICAL,
            false
        )
        binding.rvPhoto.layoutManager = manager
        binding.rvPhoto.adapter = mGridImageAdapter
        mGridImageAdapter!!.setOnItemChildClickListener(this)
        mGridImageAdapter!!.setOnItemClickListener(this)
        mDevices = ArrayList()
        mDeviceAdapter = WorkorderCreateDeviceAdapter(mDevices)
        binding.deviceRecyclerView.layoutManager = LinearLayoutManager(context)
        mDeviceAdapter!!.canSwip = !newOrder
        binding.deviceRecyclerView.adapter = mDeviceAdapter
        if(newOrder && mDevicesOnline != null){
            mDevicesOnline?.let { mDeviceAdapter!!.replaceData(it) }
            binding.deviceTv.visibility=View.VISIBLE
            binding.deviceRecyclerView.visibility=View.VISIBLE
            mDeviceAdapter!!.notifyDataSetChanged()
        }

        mDeviceAdapter!!.setOnItemClick(object : WorkorderCreateDeviceAdapter.OnItemClick {
            override fun onBtnDelete(device: SelectDataBean, position: Int) {
                FMWarnDialogBuilder(context).setIconVisible(false)
                    .setSureBluBg(true)
                    .setTitle(R.string.workorder_tip_title)
                    .setSure(R.string.workorder_confirm)
                    .setTip(R.string.workorder_delete_device)
                    .addOnBtnSureClickListener(OnBtnClickListener { dialog, view ->
                        (mDevices as ArrayList<SelectDataBean>).removeAt(position)
                        mDeviceAdapter!!.notifyDataSetChanged()
                        if ((mDevices as ArrayList<SelectDataBean>).size == 0) {
                            binding.deviceRecyclerView.visibility = View.GONE
                            binding.deviceTv.visibility = View.GONE
                        }
                        dialog.dismiss()
                    }).create(R.style.fmDefaultWarnDialog).show()
            }
        })
    }

    private fun initData() {
        if (mEquipmentId != -1L && mEquipmentId != 0L) {
            presenter!!.getEquipmentFromDB(mEquipmentId)
        }
        if (!TextUtils.isEmpty(mEquipmentFullName)) {
            presenter!!.getEquipmentFromDB(mEquipmentFullName)
        }
        request = WorkorderCreateReq()
        request!!.woType = 0L //工单类型默认自检
        if (mDemandId != null) {
            request!!.woType = 0L //需求报障默认为纠正性计划维护
            request!!.reqId = mDemandId
            binding.civWorkorderType.tipText = getString(R.string.workorder_report_correct)
        }
        request!!.patrolItemDetailId = mItemId //巡检报障
        if (!TextUtils.isEmpty(mDesc)) {
            binding.envDesc.descEt.setText(mDesc)
        }
        if (!TextUtils.isEmpty(mOtherLocationName) && mOtherLocationBean != null) {
            binding.civLocation.tipText = mOtherLocationName
            request!!.location = mOtherLocationBean
        }
        if (mOtherDepartmentId!=null && mOtherDepartmentId != -1L){
            request!!.organizationId=mOtherDepartmentId
        }
    }

    fun getUserInfoSuccess(userInfo: String?) {
        var userBean: UserInfoBean? = null
        if (!TextUtils.isEmpty(userInfo)) {
            userBean = GsonUtils.fromJson(userInfo, UserInfoBean::class.java)
        }
        if (userBean != null) {
            binding.civContact.inputText = if (userBean.name == null) "" else userBean.name
        }

        if (TextUtils.isEmpty(mPeople)) {
            if (userBean != null) {
                binding.civLocation.inputText = if (userBean.name == null) "" else userBean.name
            }
        } else {
            binding.civLocation.inputText = mPeople
        }
        if (TextUtils.isEmpty(mPhone)) {
            if (userBean != null) {
                binding.civTel.inputText = if (userBean.phone == null) "" else userBean.phone
            }
        } else {
            binding.civTel.inputText = mPhone
        }
    }

    fun refreshDevice(device: SelectDataBean?) {
        if (device != null) {
            if (mFromType != WorkorderService.CREATE_ORDER_BY_PATROL_QUERY_REPAIR) {
                binding.civLocation.tipText =
                    StringUtils.formatString(device.desc)
                request!!.location = device.location
            }
            mDevices!!.add(device)
            binding.deviceTv.visibility = View.VISIBLE
            binding.deviceRecyclerView.visibility = View.VISIBLE
            mDeviceAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onRightTextMenuClick(view: View) {
        if (prioritySelect()) {
            return
        }
        if (TextUtils.isEmpty(binding.civServiceType.tipText)) {
            ToastUtils.showShort(R.string.workorder_stype_hint)
            return
        }
        if (TextUtils.isEmpty(binding.civTel.inputText)) {
            ToastUtils.showShort(R.string.workorder_phone_hint)
            return
        }
        if (TextUtils.isEmpty(binding.civPriority.tipText)&& !newOrder) {
            ToastUtils.showShort(R.string.workorder_priority_hint)
            return
        }
        showLoading()
        if (mDevices != null && mDevices!!.size > 0) {
            request!!.equipmentIds = ArrayList()
            for (device: SelectDataBean in mDevices!!) {
                request!!.equipmentIds.add(device.id)
            }
        }
        request!!.userId = FM.getEmId()
        request!!.name = binding.civContact.inputText
        request!!.phone = binding.civTel.inputText
        request!!.scDescription = binding.envDesc.desc
        if (patrolDetailId != null){
            request!!.patrolItemDetailId = patrolDetailId
        }
        val temp: MutableList<LocalMedia> = ArrayList()
        for (localMedia: LocalMedia in mSelectList!!) {
            if (TextUtils.isEmpty(localMedia.src)) {
                temp.add(localMedia)
            }
        }
        if (mOtherMedia != null && mOtherMedia!!.size > 0) {
            if (request!!.pictures == null) {
                request!!.pictures = ArrayList()
            }
            for (localMedia: LocalMedia in mOtherMedia!!) {
                request!!.pictures.add(localMedia.src)
            }
        }
        if(newOrder){
            presenter!!.newOrderCreare(newrequest)
        }else{
            if (temp.size > 0) {
                presenter!!.uploadFile(temp, mFromType)
            } else {
                presenter!!.createWorkorder(mFromType)
            }
        }

    }

    override fun onClick(view: View) {
        if (view.id == R.id.civ_dep) {
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_DEP),
                REQUEST_DEP
            )
        } else if (view.id == R.id.civ_location) {
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_LOCATION),
                REQUEST_LOCATION
            )
        } else if (view.id == R.id.civ_service_type) {
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_SERVICE_TYPE),
                REQUEST_SERVICE_TYPE
            )
        } else if (view.id == R.id.civ_workorder_type) {
            startForResult(
                SelectDataFragment.getInstance(ISelectDataService.DATA_TYPE_WORKORDER_TYPE),
                REQUEST_WORKORDER_TYPE
            )
        } else if (view.id == R.id.iv_add_menu) {
            if (TextUtils.isEmpty(binding.civLocation.tipText)) {
                ToastUtils.showShort(R.string.workorder_position_select_hint)
                return
            }
            startForResult(
                SelectDataFragment.getInstance(
                    ISelectDataService.DATA_TYPE_EQU, request!!.location, binding.civLocation.tipText
                ), REQUEST_EQU
            )
        } else if (view.id == R.id.civ_priority) {
            if (prioritySelect()) {
                return
            }
            startForResult(
                SelectDataFragment.getInstance(
                    ISelectDataService.DATA_TYPE_FLOW_PRIORITY,
                    mDepSelectData,
                    mServiceTypeSelectData,
                    request!!.woType,
                    request!!.location
                ), REQUEST_PRIORITY
            )
        }
    }

    private fun prioritySelect(): Boolean {
        if (TextUtils.isEmpty(binding.civLocation.tipText)) {
            ToastUtils.showShort(R.string.workorder_position_select_hint)
            return true
        }
        if (TextUtils.isEmpty(binding.civWorkorderType.tipText)&&!newOrder) {
            ToastUtils.showShort(R.string.workorder_type_hint)
            return true
        }
        if (TextUtils.isEmpty(binding.civServiceType.tipText)&&!newOrder) {
            ToastUtils.showShort(R.string.workorder_stype_hint)
            return true
        }
        return false
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (view.id == R.id.iv_photo3) {
            BottomTextListSheetBuilder(context)
                .setShowTitle(true)
                .setTitle(getString(R.string.workorder_select_photo_title))
                .addItem(R.string.workorder_select_camera)
                .addItem(R.string.workorder_select_photo)
                .addItem(R.string.workorder_cancel)
                .setOnSheetItemClickListener(this@WorkorderCreateFragment)
                .build()
                .show()
        } else if (view.id == R.id.ll_del) {
            FMWarnDialogBuilder(context).setIconVisible(false)
                .setSureBluBg(true)
                .setTitle(R.string.workorder_tip_title)
                .setSure(R.string.workorder_confirm)
                .setTip(R.string.workorder_delete_picture)
                .addOnBtnSureClickListener(object : OnBtnClickListener {
                    override fun onClick(dialog: QMUIDialog, view: View) {
                        dialog.dismiss()
                        val tempAdapter = adapter as GridImageAdapter
                        val path = ""
                        val item = tempAdapter.getItem(position)
                        val localMedia = tempAdapter.data[position]
                        if (mOtherMedia != null && mOtherMedia!!.contains(localMedia)) {
                            mOtherMedia!!.remove(localMedia)
                        }
                        if (mLocalMedia != null && mLocalMedia!!.contains(localMedia)) {
                            mLocalMedia!!.remove(localMedia)
                        }
                        tempAdapter.remove(position)
                        //                            if (item != null && !TextUtils.isEmpty(item.getSrc())) {
//                                //Src不为空，说明图片是从其他地方传过来的
//                                tempAdapter.remove(position);
//                            } else {
//                                //src为空，说明图片是本页面添加的，有可能压缩或裁切
//                                if (item != null) {
//                                    if (item.isCut() && !item.isCompressed()) {
//                                        path = item.getCutPath();
//                                    } else if (item.isCompressed() || (item.isCut() && item.isCompressed())) {
//                                        path = item.getCompressPath();
//                                    }
//                                }
//                                tempAdapter.remove(position);
////                                if (!"".equals(path)) {
////                                    FileUtils.deleteFile(path);
////                                }
//                            }
                    }
                }).create(R.style.fmDefaultWarnDialog).show()
        }
    }

    override fun onClick(dialog: QMUIBottomSheet, itemView: View, position: Int, tag: String) {
        val projectName = SPUtils.getInstance(SPKey.SP_MODEL).getString(SPKey.PROJECT_NAME, "")
        var inputText: String? = projectName + "\r\n" + binding.civLocation.tipText
        if (!mWaterMark) {
            inputText = null
        }
        if (position == 0) {
            if (mSelectList!!.size < MAX_PHOTO) {
                PictureSelectorManager.camera(
                    this@WorkorderCreateFragment,
                    PictureConfig.REQUEST_CAMERA,
                    inputText
                )
            } else {
                ToastUtils.showShort(
                    String.format(
                        Locale.getDefault(),
                        getString(R.string.workorder_select_photo_at_most),
                        MAX_PHOTO
                    )
                )
            }
        } else if (position == 1) {
            PictureSelectorManager.MultipleChoose(
                this@WorkorderCreateFragment,
                MAX_PHOTO,
                mLocalMedia,
                PictureConfig.CHOOSE_REQUEST,
                inputText
            )
        }
        dialog.dismiss()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        PictureSelector.create(this@WorkorderCreateFragment)
            .themeStyle(R.style.picture_fm_style)
            .openExternalPreview(position, mSelectList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    mSelectList!!.clear()
                    mLocalMedia!!.clear()
                    if (mOtherMedia != null && mOtherMedia!!.size > 0) {
                        mSelectList!!.addAll(mOtherMedia!!)
                    }
                    mLocalMedia!!.addAll(selectList)
                    mSelectList!!.addAll(selectList)
                    mGridImageAdapter!!.replaceData((mSelectList)!!)
                }
                PictureConfig.REQUEST_CAMERA -> {
                    val selectCamera = PictureSelector.obtainMultipleResult(data)
                    mSelectList!!.addAll(selectCamera)
                    mLocalMedia!!.addAll(selectCamera)
                    mGridImageAdapter!!.replaceData((mSelectList)!!)
                }
            }
        }
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)


        if (data!=null&&resultCode != RESULT_OK) {
            return
        }
        val bean: SelectDataBean? = data?.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK)
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (bean == null) {
                    binding.civLocation.tipText = ""
                    request!!.location = null
                } else {
                    binding.civLocation.tipText = StringUtils.formatString(bean.fullName)
                    bean.location.floorId = null
                    bean.location.roomId = null
                    request!!.location = bean.location
                    LogUtils.d(
                        ("location :" + request!!.location.roomId
                                + "--" + request!!.location.floorId
                                + "--" + request!!.location.buildingId
                                + "--" + request!!.location.siteId)
                    )
                }
                mDevices!!.clear()
                binding.deviceRecyclerView.visibility = View.GONE
                binding.deviceTv.visibility = View.GONE
                mDeviceAdapter!!.notifyDataSetChanged()
                if (mDeviceAdapter!!.data.size == 0) {
                    binding.deviceTv.visibility = View.GONE
                }
                setNullPriority()
            }
            REQUEST_DEP -> {
                if (bean == null) {
                    mDepSelectData = null
                    binding.civDep.tipText = ""
                    binding.civDep.showClearIocn(false)
                    request!!.organizationId = null
                } else {
                    mDepSelectData = bean
                    binding.civDep.tipText = StringUtils.formatString(bean.fullName)
                    binding.civDep.showClearIocn(!TextUtils.isEmpty(bean.fullName))
                    request!!.organizationId = bean.id
                    LogUtils.d("dep :" + request!!.organizationId)
                }
                setNullPriority()
            }
            REQUEST_SERVICE_TYPE -> {
                if (bean == null) {
                    mServiceTypeSelectData = null
                    binding.civServiceType.tipText=""
                    request!!.serviceTypeId = null
                    newrequest!!.serviceTypeId = null
                } else {
                    mServiceTypeSelectData = bean
                    binding.civServiceType.tipText = StringUtils.formatString(bean.fullName)
                    request!!.serviceTypeId = bean.id
                    newrequest!!.serviceTypeId = bean.id
                    LogUtils.d("service type :" + request!!.serviceTypeId)
                }
                setNullPriority()
            }
            REQUEST_WORKORDER_TYPE -> if (bean != null) {
                binding.civWorkorderType.tipText = StringUtils.formatString(bean.fullName)
                request!!.woType = bean.id
                newrequest!!.orderType = bean.id.toInt()
                LogUtils.d("wo type :" + request!!.woType)
                setNullPriority()
            }
            REQUEST_PRIORITY -> if (bean != null) {
                binding.civPriority.tipText = StringUtils.formatString(bean.fullName)
                request!!.priorityId = bean.id
                newrequest!!.priorityId = bean.id
                newrequest!!.flowId = bean.parentId
                request!!.processId = bean.parentId //流程id
                LogUtils.d("priority type :" + request!!.priorityId)
            } else {
                setNullPriority()
            }
            REQUEST_EQU -> if (bean != null) {
                for (device: SelectDataBean in mDevices!!) {
                    if ((device.id == bean.id)) {
                        ToastUtils.showShort(R.string.workorder_equipment_exist)
                        return
                    }
                }
                mDevices!!.clear()
                mDevices!!.add(bean)
                binding.deviceTv.visibility=View.VISIBLE
                binding.deviceRecyclerView.visibility=View.VISIBLE
                mDeviceAdapter!!.notifyDataSetChanged()
                LogUtils.d("device id :" + bean.id)
            }
        }
    }

    private fun setNullPriority() {
        binding.civPriority.tipText=""
        request!!.priorityId = null
        request!!.processId = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //删除所有照片文件
        FileUtils.deleteAllInDir(FMFileUtils.getPicPath())
    }

    companion object {
        private val EQUIPMENT_ID = "equipment_id"
        private val EQUIPMENT_STR_ID = "equipment_str_id" //设备编码
        private val FROM_TYPE = "from_type"
        private val LOCATION_NAME = "location_name"
        private val LOCATION_INFO = "location_info"
        private val PIC_INFO = "pic_info"
        private val ITEM_ID = "item_id"
        private val ORDER_PEOPLE = "order_people"
        private val ORDER_PHONE = "order_phone"
        private val ORDER_DESC = "order_desc"
        private val DEMAND_ID = "demand_id"
        private val WATER_MARK = "water_mark"
        private val MAX_PHOTO = 1000
        private val REQUEST_LOCATION = 20001
        private val REQUEST_DEP = 20002
        private val REQUEST_SERVICE_TYPE = 20003
        private val REQUEST_WORKORDER_TYPE = 20004
        private val REQUEST_PRIORITY = 20005
        private val REQUEST_EQU = 20006
        private val IS_NEWORDER = "isNewOrder"
        private val DEPARTMENT_NAME = "departname"
        private val DEPARTMENT_ID="departId"
        private val ORDERTYPE = "ordertype"
        private val SERVICETYPE = "servicetype"
        private val PRIORITY = "priority"
        private val WORKORDER_TYPE = "workoder_type"
        private val ORDER_REQID = "reqId"
        private val ORDER_SERVICEID = "serviceId"
        private val ORDER_FLOWID = "flowId"
        private val ORDER_PRIORITYID = "priorityId"
        private val DEVICE_LIST = "device_list"
        private val PATROL_DETAIL_ID = "partail_detail_id"

        @JvmStatic
        val instance: WorkorderCreateFragment
            get() {
                val bundle = Bundle()
                bundle.putBoolean(WATER_MARK, true)
                val fragment = WorkorderCreateFragment()
                fragment.arguments = bundle
                return fragment
            }

        @JvmStatic
        fun getInstance(fromType: Int, equipmentId: Long): WorkorderCreateFragment {
            val fragment = WorkorderCreateFragment()
            val bundle = Bundle()
            bundle.putInt(FROM_TYPE, fromType)
            bundle.putLong(EQUIPMENT_ID, equipmentId)
            bundle.putBoolean(WATER_MARK, true)
            fragment.arguments = bundle
            return fragment
        }
        @JvmStatic
        fun getInstance(fromType: Int, equipmentId: Long,desc: String,locationBean: LocationBean,locationName: String,patrolDetailId : Long): WorkorderCreateFragment {
            val fragment = WorkorderCreateFragment()
            val bundle = Bundle()
            bundle.putInt(FROM_TYPE, fromType)
            bundle.putLong(EQUIPMENT_ID, equipmentId)
            bundle.putBoolean(WATER_MARK, true)
            bundle.putString(ORDER_DESC, StringUtils.formatString(desc))
            bundle.putParcelable(LOCATION_INFO, locationBean)
            bundle.putString(LOCATION_NAME,locationName)
            val userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO)
            val infoBean = com.blankj.utilcode.util.GsonUtils.fromJson(
                userInfo,
                UserInfoBean::class.java
            )
            if (infoBean.organizationName!=null&&infoBean.organizationId!=null){
                bundle.putString(DEPARTMENT_ID, infoBean.organizationId)
                bundle.putString(DEPARTMENT_NAME, infoBean.organizationName)
            }
            if (patrolDetailId != null){
                bundle.putLong(PATROL_DETAIL_ID,patrolDetailId)
            }

            fragment.arguments = bundle
            return fragment
        }


        @JvmStatic
        fun getInstance(
            fromType: Int, equipmentId: Long, locationName: String?,
            locationBean: LocationBean?,
            localMedias: List<LocalMedia?>?,
            itemId: Long?, desc: String?,
            demandId: Long?, phone: String?, people: String?
        ): BaseFragment<*> {
            return getInstance(
                fromType,
                equipmentId,
                locationName,
                locationBean,
                localMedias,
                itemId,
                desc,
                demandId,
                phone,
                people,
                true
            )
        }
        @JvmStatic
        fun getInstance(
            fromType: Int, equipmentId: Long, locationName: String?,
            locationBean: LocationBean?,
            localMedias: List<LocalMedia?>?,
            itemId: Long?, desc: String?,
            demandId: Long?, phone: String?, people: String?, waterMark: Boolean
        ): BaseFragment<*> {
            var locationBean = locationBean
            var localMedias = localMedias
            var itemId = itemId
            var demandId = demandId
            demandId = demandId ?: -1L
            itemId = itemId ?: -1L
            localMedias = localMedias ?: ArrayList()
            locationBean = locationBean ?: LocationBean()
            val fragment = WorkorderCreateFragment()
            val bundle = Bundle()
            bundle.putInt(FROM_TYPE, fromType)
            bundle.putLong(EQUIPMENT_ID, equipmentId)
            bundle.putLong(ITEM_ID, itemId)
            bundle.putLong(DEMAND_ID, demandId)
            bundle.putBoolean(WATER_MARK, waterMark)
            bundle.putString(LOCATION_NAME, StringUtils.formatString(locationName))
            bundle.putString(ORDER_DESC, StringUtils.formatString(desc))
            bundle.putString(ORDER_PHONE, StringUtils.formatString(phone))
            bundle.putString(ORDER_PEOPLE, StringUtils.formatString(people))
            bundle.putParcelable(LOCATION_INFO, locationBean)
            bundle.putParcelableArrayList(PIC_INFO, localMedias as ArrayList<out Parcelable?>?)
            fragment.arguments = bundle
            return fragment
        }
        @JvmStatic
        fun getInstance(fromType: Int, equipmentId: String): WorkorderCreateFragment {
            val fragment = WorkorderCreateFragment()
            val bundle = Bundle()
            bundle.putInt(FROM_TYPE, fromType)
            bundle.putString(EQUIPMENT_STR_ID, equipmentId)
            bundle.putBoolean(WATER_MARK, true)
            fragment.arguments = bundle
            return fragment
        }
        @JvmStatic
        fun getInstance(data : WorkorderCreateService.newOrderCreateReq):BaseFragment<*>{
            val fragment = WorkorderCreateFragment()
            val bundle = Bundle()
            bundle.putBoolean(IS_NEWORDER,true)
            bundle.putString(ORDER_DESC,data.scDescription)
            bundle.putString(LOCATION_NAME,data.nameAll.loactionName)
            bundle.putString(ORDER_PEOPLE,data.name+"")
            bundle.putString(ORDER_PHONE,data.phone+"")
            bundle.putString(DEPARTMENT_NAME,data.nameAll.departmentName)
            bundle.putString(ORDERTYPE,data.nameAll.orderType)
            bundle.putString(SERVICETYPE,data.nameAll.serviceType)
            bundle.putString(PRIORITY,data.nameAll.priority)
            bundle.putInt(WORKORDER_TYPE,data.woType)
            if (data.reqId != null){
                bundle.putLong(ORDER_REQID,data.reqId)
            }

            if (data.serviceTypeId != null){
                bundle.putLong(ORDER_SERVICEID,data.serviceTypeId)
            }
            if (data.processId != null){
                bundle.putLong(ORDER_FLOWID,data.processId)
            }

            if (data.priorityId != null){
                bundle.putLong(ORDER_PRIORITYID,data.priorityId)
            }

            if(data.equipmentSystemName != null){
                bundle.putParcelableArrayList(DEVICE_LIST,data.equipmentSystemName as java.util.ArrayList<out Parcelable>)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}

private operator fun Boolean.invoke(b: Boolean) {

}
