package com.facilityone.wireless.construction.fragment


import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout

import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService
import com.facilityone.wireless.a.arch.ec.module.Page
import com.facilityone.wireless.a.arch.offline.objectbox.ObjectBoxHelper
import com.facilityone.wireless.a.arch.offline.objectbox.construction.ConstructionTemplate
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder.OnBtnClickListener
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.adapter.ElectronicLedgerAdapter
import com.facilityone.wireless.construction.module.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.facilityone.wireless.construction.presenter.ConstructionTemplatePresenter as CtPresenter
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import me.yokeyword.fragmentation.ISupportFragment
import java.util.ArrayList
import com.google.gson.GsonBuilder


/**
 * @Auther: karelie
 * @Date: 2021/8/17
 * @Infor: 维护工单通用列表
 */
class ConstructionTemplateFragment : BaseFragment<CtPresenter?>(),
    BaseQuickAdapter.OnItemClickListener, OnRefreshLoadMoreListener {
    private var mType: Int? = null
    private var mRecyclerView: RecyclerView? = null
    private var mRefreshLayout: SmartRefreshLayout? = null

    //    private val mList: List<MaintenanceListEnity>? = null
    private var localWoId: Long? = null //当前选中的Id
    private var mCode: String? = null
    private var mTemplateId: Long? = null
    private var mPage: Page? = null
    var mElAdapter: ElectronicLedgerAdapter? = null

    //当前模板任务数据
    var mTemplateModel: TemplateModel? = null
    var mStartTime: Long? = null
    private val REFRESH = 500001 // 界面刷新
    private val REFRESH_POP = 500009 // 跳回列表
    private var mFirstIndex:Int=-1//第一个未填写的索引


    inline fun <reified T> fromJson(json: String): T {
        return GsonUtils.fromJson(json, object : TypeToken<T>() {}.type)
    }


    inline fun <reified T> String.fromJsonStr(): T {
        return GsonUtils.fromJson(this, object : TypeToken<T>() {}.type)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        //        initOnClick();
//        getData();
    }

    private fun getData(woId: Long?, templateId: Long?) {
//        showLoading();
//        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = Page()
        }
        mPage!!.reset()
        //        getPresenter().getMaintenanceList(mType,mPage,null);
        mStartTime = System.currentTimeMillis()
        val woId = 0L
        val templateId = 0L
        presenter!!.getTemplateData(woId, templateId)
    }


    private fun initData() {
        val bundle = arguments
        if (bundle != null) {
            mType = bundle.getInt(LIST_TYPE, -1)
            localWoId = bundle.getLong(WOID, -1L)
            mTemplateId = bundle.getLong(TEMPLATE_ID)
        }
        setTitle("施工监护项目列表")
    }


    //刷新页面
    private fun onRefresh() {
//        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = Page()
        }
        mPage!!.reset()
    }

    fun noDataRefresh() {
//        mAdapter.setEmptyView(getNoDataView(mRefreshLayout));
    }

    private fun initView() {
        mRefreshLayout = findViewById(R.id.refreshLayout)
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        setRightTextButton("保存", R.id.maintenance_ele_uoload_menu_id)
        val datas: List<ConstructionService.ElectronicLedgerEntity> = ArrayList()
        mElAdapter = ElectronicLedgerAdapter(datas)
        mElAdapter!!.tempRecycleView = mRecyclerView
        mRecyclerView!!.setItemViewCacheSize(200)
        mRecyclerView!!.adapter = mElAdapter
        //根据本地是否存在缓存数据决定是否加载网络数据
        val localDataJson =
            ObjectBoxHelper.getLastConstructionData() ?: getData(localWoId, mTemplateId)
        if (localDataJson is ConstructionTemplate) {
            val localData = localDataJson.originContent!!.fromJsonStr<ElEntity>()
            val localTemplate = localDataJson.originResponse!!.fromJsonStr<TemplateModel>()
            //获取本地缓存的模板数据
            mTemplateModel = localTemplate
            //Gson泛型转换优化
            for (localDatum in localData) {
                if (localDatum.content !is String) {
                    val gson: Gson = GsonBuilder().enableComplexMapKeySerialization().create()
                    val jsonString = gson.toJson(localDatum.content)
                    val bean = jsonString.fromJsonStr<SelectorModel>()
                    localDatum.content = bean
                }
            }


            mElAdapter!!.setNewData(localData)
        }
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
    }

    override fun onRightTextMenuClick(view: View) {
        super.onRightTextMenuClick(view)
        val viewId = view.id
        if (viewId == R.id.maintenance_ele_uoload_menu_id) {
            val saveDataList = mElAdapter!!.data
            var isUpload = false
            saveInspection(saveDataList)
        }
    }

    override fun setLayout(): Any {
        return R.layout.fragment_electronic_ledger
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): CtPresenter {
        return CtPresenter()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {}
    override fun onRefresh(refreshLayout: RefreshLayout) {
        onRefresh()
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        val bean: SelectDataBean? = data.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK)
    }

    override fun onBackPressedSupport(): Boolean {
        popResult()
        return true
    }

    override fun leftBackListener() {
        popResult()
    }

    fun popResult() {
        val bundle = Bundle()
        setFragmentResult(REFRESH, bundle)
        pop()
    }

    fun popLast() {
        super.onBackPressedSupport()
        val bundle = Bundle()
        setFragmentResult(REFRESH_POP, bundle)
        pop()
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/11/29 10:38
     * @Description:保存抽检信息
     */
    private fun saveInspection(dataList: MutableList<ConstructionService.ElectronicLedgerEntity>) {
        LogUtils.d(GsonUtils.toJson(dataList))
        var canUpload = false
        var allClear = false
        var hasFilledSize = 0
        var firstEmpty=false
        var needfillSize = 0
        //任务列表
        val tasks: MutableList<UploadTask> = arrayListOf()
        //遍历模板任务列表
        for (itemTask in mTemplateModel!!.tasks!!) {
            //提交任务对象
            val tempTask =
                UploadTask(taskId = itemTask.taskId, taskName = itemTask.taskName, contents = null)
            //提交任务内容列表
            val tempTaskContents: MutableList<UploadTaskContent> = arrayListOf()
            //遍历
            for ((index,tempItem) in dataList.withIndex()) {
                //判断是否与当前任务id相同
                if (tempItem.taskId == tempTask.taskId) {
                    //判断是否已经填写内容或选择选项
                    if (!TextUtils.isEmpty(tempItem.value)) {
                        hasFilledSize++
                        //判断item类型
                        if (tempItem.type == ConstructionService.ElectronicLedgerEntity.TYPE_RADIO) {
                            val uploadTaskContent = UploadTaskContent(
                                tempItem.contentId,
                                (tempItem.content as SelectorModel).name!!,
                                inputValue = null,
                                selectValue = tempItem.value
                            )
                            tempTaskContents.add(uploadTaskContent)
                        } else if (tempItem.type == ConstructionService.ElectronicLedgerEntity.TYPE_EDIT) {
                            val uploadTaskContent = UploadTaskContent(
                                tempItem.contentId,
                                tempItem.content as String,
                                inputValue = tempItem.value,
                                selectValue = tempItem.value
                            )
                            tempTaskContents.add(uploadTaskContent)
                        }
                        canUpload = true
                    } else {
                        if (!firstEmpty){
                            mFirstIndex=index
                            firstEmpty=true
                        }

                        //判断item类型
                        if (tempItem.type == ConstructionService.ElectronicLedgerEntity.TYPE_RADIO) {
                            val uploadTaskContent = UploadTaskContent(
                                tempItem.contentId,
                                (tempItem.content as SelectorModel).name!!,
                                inputValue = null,
                                selectValue = null
                            )
                            tempTaskContents.add(uploadTaskContent)
                        } else if (tempItem.type == ConstructionService.ElectronicLedgerEntity.TYPE_EDIT) {
                            val uploadTaskContent = UploadTaskContent(
                                tempItem.contentId,
                                tempItem.content as String,
                                inputValue = null,
                                selectValue = null
                            )
                            tempTaskContents.add(uploadTaskContent)
                        }
                    }
                } else {
                    continue
                }


            }
            tempTask.contents = tempTaskContents
            tasks.add(tempTask)
        }
        val uploadTemplateData = UploadTemplateData()
        uploadTemplateData.woId = localWoId
        uploadTemplateData.templateId = mTemplateModel!!.templateId
        uploadTemplateData.tasks = tasks
        uploadTemplateData.startTime = mStartTime

        if (canUpload) {

            for (tempItem in dataList) {
                //获取需要填充item的数量
                if (tempItem.type == ConstructionService.ElectronicLedgerEntity.TYPE_RADIO
                    ||
                    tempItem.type == ConstructionService.ElectronicLedgerEntity.TYPE_EDIT
                ) {
                    needfillSize++
                }
            }


            //判断是否已经全部填充
            allClear = (hasFilledSize == needfillSize)
            if (allClear) {
//                confirmSamplePass(uploadTemplateData)

                saveTemplateData2Db(true, dataList, uploadTemplateData)
            } else {
                val builder = FMWarnDialogBuilder(activity)
                builder.setTitle("")
                builder.setSure("忽略")
                builder.setCancel("检查")
                builder.setTip("检测到当前存在漏检项,是否忽略")
                builder.addOnBtnSureClickListener(OnBtnClickListener { dialog, view ->
                    dialog.dismiss()
                    saveTemplateData2Db(false, dataList, uploadTemplateData)
                }).addOnBtnCancelClickListener { dialog, view ->
                    dialog.dismiss()
                    mRecyclerView?.scrollToPosition(mFirstIndex)
                }
                builder.create(R.style.fmDefaultWarnDialog).show()
            }

        } else {
            ToastUtils.showShort("请先完成抽检后进行提交")
        }

    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/11/29 14:30
     * @Description: 抽检提交通过确认
     */

    private fun confirmSamplePass(upload: UploadTemplateData) {
        upload.pass = false
        val builder = FMWarnDialogBuilder(activity)
        builder.setTitle("")
        builder.setSure(R.string.maintenance_submit_pass)
        builder.setCancel(R.string.maintenance_submit_unpass)
        builder.setTip(R.string.maintenance_task_submit_check)
        builder.addOnBtnSureClickListener(OnBtnClickListener { dialog, view ->
            dialog.dismiss()
            upload.pass = true
            LogUtils.d(GsonUtils.toJson(upload))
//            presenter!!.saveTemplateData(upload)
        }).addOnBtnCancelClickListener { dialog, view ->
            dialog.dismiss()
            upload.pass = false
            LogUtils.d(GsonUtils.toJson(upload))
//            presenter!!.saveTemplateData(upload)
        }
        builder.create(R.style.fmDefaultWarnDialog).show()
    }


    private fun saveTemplateData2Db(
        lastStatus: Boolean,
        origin: ElEntity,
        upload: UploadTemplateData
    ) {
        val boxInstance = ObjectBoxHelper.getConstructionBox()
        val lastData = ObjectBoxHelper.getLastConstructionData()
        //判断是否存在临时数据,否则重新创建暂存
        val uploadData = lastData?.apply {
            status=lastStatus
            originContent=origin.dataToJson()
            upLoadContent=upload.dataToJson()
            originResponse=mTemplateModel?.dataToJson()
        }?:ConstructionTemplate(0,lastStatus,upload.dataToJson(),origin.dataToJson(),mTemplateModel?.dataToJson())
        boxInstance.put(uploadData)
        popResult()

    }


    private fun Any.dataToJson(): String {
        return GsonUtils.toJson(this)
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/11/30 8:40
     * @Description:获取数据失败
     */
    fun onLoadTemplateFail() {
        ToastUtils.showShort("数据")
        pop()
    }
    /**
     * @Created by: kuuga
     * @Date: on 2021/11/26 14:46
     * @Description: 获取模板数据成功回调
     */
    fun onLoadTemplateSuccess(templateModel: TemplateModel) {


        val taskData: MutableList<ConstructionService.ElectronicLedgerEntity> = arrayListOf()
        //赋值给全局变量方便提交时操作
        mTemplateModel = templateModel
        if (templateModel.tasks != null && templateModel.tasks!!.isNotEmpty()) {

            for (item in templateModel.tasks!!) {
                //获取任务标题
                val headItem = ConstructionService.ElectronicLedgerEntity(1, item.taskName)
                taskData.add(headItem)
                //获取任务内容
                for (subItem in item.contents!!) {
                    //填充列表内容
                    val itemContent = chooseItemType(item.taskId!!, subItem)
                    if (itemContent != null) {
                        taskData.add(itemContent)
                    }
                }


            }
            mElAdapter!!.setNewData(taskData);

        }


    }
    /**
     * @Created by: kuuga
     * @Date: on 2021/11/29 14:58
     * @Description:提交成功回调
     */
    fun onSubmitTemplateSuccess(pass: Boolean) {
        if (!pass) {
            ToastUtils.showShort("提交成功")
            popLast()
        } else {
            ToastUtils.showShort("提交成功")
            popResult()
        }

    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/11/30 8:43
     * @Description: 提交失败回调
     */
    fun onSubmitTemplateFail() {
        ToastUtils.showShort("提交失败")
    }

    //选择列表项内容
    private fun chooseItemType(
        taskId: Long,
        taskContent: TaskContent
    ): ConstructionService.ElectronicLedgerEntity? {
        return when (taskContent.type) {
            TaskContent.CHOICE -> {
                ConstructionService.ElectronicLedgerEntity(
                    taskId,
                    taskContent.contentId,
                    2,
                    SelectorModel(taskContent.content, 0, taskContent.selectValues)
                )
            }
            TaskContent.INPUT -> {
                ConstructionService.ElectronicLedgerEntity(
                    taskId,
                    taskContent.contentId,
                    3,
                    taskContent.content
                )
            }
            else -> {
                null
            }

        }
    }

    companion object {
        private const val LIST_TYPE = "list_type"
        private const val WOID = "woid"
        private const val WOCODE = "wocode"
        private const val TEMPLATE_ID = "template_id"
        private const val MAINTENANCE_INFO = 4001
        private const val REQUEST_LOCATION = 20001
        const val FAULT_DEVICE = 4007
        const val TOOLS = 4008
        const val CHARGE = 4009
        const val STEP = 4010
        const val SPACE_LOCATION = 4011
        const val PAYMENT = 4012
        private const val REQUEST_REASON = 20007
        private const val REQUEST_INVALID = 20008
        private const val MAX_NUMBER = 3 //一行显示几个tag
        private val LIST_REFRESH = 10001

        @JvmStatic
        fun getInstance(
            type: Int?,
            woId: Long?,
            woCode: String,
            templateId: Long
        ): ConstructionTemplateFragment {
            val bundle = Bundle()
//            bundle.putInt(LIST_TYPE, type!!)
//            bundle.putLong(WOID, woId!!)
//            bundle.putString(WOCODE,woCode)
//            bundle.putLong(TEMPLATE_ID,templateId)
            val instance = ConstructionTemplateFragment()
            instance.arguments = bundle
            return instance
        }

        @JvmStatic
        fun getInstance(): ConstructionTemplateFragment {
            val bundle = Bundle()
//            bundle.putInt(LIST_TYPE, type!!)
//            bundle.putLong(WOID, woId!!)
//            bundle.putString(WOCODE,woCode)
//            bundle.putLong(TEMPLATE_ID,templateId)
            val instance = ConstructionTemplateFragment()
            instance.arguments = bundle
            return instance
        }
    }

}

typealias ElEntity = MutableList<ConstructionService.ElectronicLedgerEntity>