package com.facilityone.wireless.maintenance.fragment


import com.facilityone.wireless.a.arch.mvp.BaseFragment
import com.facilityone.wireless.maintenance.presenter.MaintenanceELPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.facilityone.wireless.maintenance.model.MaintenanceEnity.MaintenanceListEnity
import com.facilityone.wireless.maintenance.adapter.ElectronicLedgerAdapter
import android.os.Bundle
import android.text.TextUtils
import com.facilityone.wireless.maintenance.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService
import com.facilityone.wireless.a.arch.ec.module.Page
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder
import com.facilityone.wireless.a.arch.widget.FMWarnDialogBuilder.OnBtnClickListener
import com.facilityone.wireless.maintenance.model.*
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import me.yokeyword.fragmentation.ISupportFragment
import java.util.ArrayList

/**
 * @Auther: karelie
 * @Date: 2021/8/17
 * @Infor: 维护工单通用列表
 */
class MaintenanceElectronicLedgerFragment : BaseFragment<MaintenanceELPresenter?>(),
    BaseQuickAdapter.OnItemClickListener, OnRefreshLoadMoreListener {
    private var mType: Int? = null
    private var mRecyclerView: RecyclerView? = null
    private var mRefreshLayout: SmartRefreshLayout? = null
    private val mList: List<MaintenanceListEnity>? = null
    private var localWoId: Long? = null //当前选中的Id
    private var mCode:String?=null
    private var mPage: Page? = null
    var mElAdapter: ElectronicLedgerAdapter? = null
    //当前模板任务数据
    var mTemplateModel:TemplateModel?=null
    var mStartTime:Long?=null
    private val REFRESH = 500001 // 界面刷新
    private val REFRESH_POP = 500009 // 跳回列表

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        //        initOnClick();
//        getData();
    }

    private fun getData(woId: Long?) {
//        showLoading();
//        mAdapter.setEmptyView(getLoadingView(mRefreshLayout));
        if (mPage == null) {
            mPage = Page()
        }
        mPage!!.reset()
        //        getPresenter().getMaintenanceList(mType,mPage,null);
        mStartTime=System.currentTimeMillis()
        presenter!!.getTemplateData(woId)
    }

    private fun initOnClick() {}
    private fun initData() {
        val bundle = arguments
        if (bundle != null) {
            mType = bundle.getInt(LIST_TYPE, -1)
            localWoId = bundle.getLong(WOID, -1L)
            mCode=bundle.getString(WOCODE,"电子台账")
        }
        setTitle(mCode)
    }

    fun refreshSuccessUI(ms: List<MaintenanceListEnity>) {
        dismissLoading()
        for (m in ms) {
            m.choice = 0
        }
        //        mAdapter.replaceData(ms);
//        if (ms != null) {
//            localWoId = ms.get(0).code; //默认设置匹配Id为列表第一个元素的Id
//        }
//        mAdapter.notifyDataSetChanged();
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
        setRightTextButton("提交", R.id.maintenance_ele_uoload_menu_id)
        val datas: List<MaintenanceEnity.ElectronicLedgerEntity> = ArrayList()
        mElAdapter = ElectronicLedgerAdapter(datas)
        mElAdapter!!.tempRecycleView=mRecyclerView
        mRecyclerView!!.setItemViewCacheSize(200)
        mRecyclerView!!.adapter = mElAdapter
        getData(localWoId)
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
//        val workorderItemBean = (adapter as MaintenanceListAdapter).data[position]!!
//        //        localWoId = workorderItemBean.code;
//        if (workorderItemBean.choice == MaintenanceConstant.CHOICE_NO) {
//            val status = workorderItemBean.status
//            val woId = workorderItemBean.woId
//            val code = workorderItemBean.code
//            val router = Router.getInstance()
//            val workorderService = router.getService(
//                WorkorderService::class.java.simpleName
//            ) as WorkorderService
//            if (workorderService != null) {
//                val fragment: BaseFragment<*>
//                fragment = if (mType == MaintenanceConstant.FIVE) {
//                    workorderService.getWorkorderInfoFragment(status, code, woId, true, true)
//                } else {
//                    workorderService.getWorkorderInfoFragment(status, code, woId, true)
//                }
//                startForResult(fragment, MAINTENANCE_INFO)
//            }
//        }
    }

    override fun onRightTextMenuClick(view: View) {
        super.onRightTextMenuClick(view)
        val viewId = view.id
        if (viewId == R.id.maintenance_ele_uoload_menu_id) {
            val saveDataList = mElAdapter!!.data
            var isUpload = false
            saveInspection(saveDataList)
//            for (temp in list) {
//                if (temp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO) {
//                    if (TextUtils.isEmpty(temp.value)) {
//                        ToastUtils.showLong("有类目未选择")
//                        isUpload = false
//                        break
//                    }
//                } else if (temp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO_SUB) {
//                    if (TextUtils.isEmpty(temp.value) || TextUtils.isEmpty(temp.subValue)) {
//                        ToastUtils.showLong("有类目未选择")
//                        isUpload = false
//                        break
//                    }
//                }
//                isUpload = true
//            }
//            if (isUpload) {
//                val checkModels: MutableList<CheckModel> = ArrayList()
//                for (titleItem in list) {
//                    if (titleItem.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_HEADER) {
//                        val tempItem = CheckModel()
//                        tempItem.title = titleItem.content.toString()
//                        //同一组对象数组
//                        val checkContentModels: MutableList<CheckContentModel> = ArrayList()
//                        for (innerTemp in list) {
//                            if (innerTemp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_HEADER || innerTemp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT) {
//                            } else if (innerTemp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_SUB_HEADER) {
//                                if (innerTemp.parent == titleItem.parent) {
//                                    val tempContent = CheckContentModel()
//                                    tempContent.name = innerTemp.content.toString()
//                                    checkContentModels.add(tempContent)
//                                }
//                            } else {
//                                val (name, value, state, sub, pid, tips) = innerTemp.content as SelectorModel
//                                if (pid == titleItem.parent as Long) {
//                                    //单行文本+单选组
//                                    if (innerTemp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO) {
//                                        val tempContent = CheckContentModel()
//                                        tempContent.name = name
//                                        if (value == 0) {
//                                            tempContent.value = "正常"
//                                            tempContent.otherValue = "异常"
//                                        } else {
//                                            tempContent.value = "携带"
//                                            tempContent.otherValue = "未携带"
//                                        }
//                                        if (state == 0) {
//                                            tempContent.selectedValue = tempContent.value
//                                        } else if (state == 1) {
//                                            tempContent.selectedValue = tempContent.otherValue
//                                        }
//                                        checkContentModels.add(tempContent)
//                                        if (!TextUtils.isEmpty(tips)) {
//                                            val tipsContent = CheckContentModel()
//                                            tipsContent.name = tips
//                                            checkContentModels.add(tipsContent)
//                                        }
//                                        //单行文本+单选组+子单选组
//                                    } else if (innerTemp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO_SUB) {
//                                        val tempContent = CheckContentModel()
//                                        tempContent.name = name + "sameLine_是否合格有效："
//                                        tempContent.value = "携带sameLine_是"
//                                        tempContent.otherValue = "未携带sameLine_否"
//                                        if (state == 0) {
//                                            if (sub!!.state == 0) {
//                                                tempContent.selectedValue = "携带sameLine_是"
//                                            } else if (sub.state == 1) {
//                                                tempContent.selectedValue = "携带sameLine_否"
//                                            }
//                                        } else {
//                                            if (sub!!.state == 0) {
//                                                tempContent.selectedValue = "未携带sameLine_是"
//                                            } else if (sub.state == 1) {
//                                                tempContent.selectedValue = "未携带sameLine_否"
//                                            }
//                                        }
//                                        checkContentModels.add(tempContent)
//                                        //单行文本判断
//                                    } else if (innerTemp.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_SUB_HEADER) {
//                                        val tempContent = CheckContentModel()
//                                        tempContent.name = name
//                                    }
//                                }
//                            }
//                        }
//                        tempItem.contents = checkContentModels
//                        checkModels.add(tempItem)
//                        //输入区域判断
//                    } else if (titleItem.type == MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT) {
//                        val tempItem = CheckModel()
//                        tempItem.remark = titleItem.value
//                        checkModels.add(tempItem)
//                    }
//                }
//                presenter!!.pushAccountCheck(AccountCheck(checkModels))
//
////                pop();
//            }
        }
    }

    override fun setLayout(): Any {
        return R.layout.fragment_maintenance_electronic_ledger
    }

    override fun setTitleBar(): Int {
        return R.id.ui_topbar
    }

    override fun createPresenter(): MaintenanceELPresenter {
        return MaintenanceELPresenter()
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

    fun popLast(){
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
    private fun saveInspection(dataList: MutableList<MaintenanceEnity.ElectronicLedgerEntity>){
        var canUpload=false
        var allClear=false
        var hasFilledSize=0
        var needfillSize=0
        //任务列表
        val tasks:MutableList<UploadTask> = arrayListOf()
        //遍历模板任务列表
        for (itemTask in mTemplateModel!!.tasks!!) {
            //提交任务对象
            val tempTask=UploadTask(taskId = itemTask.taskId,taskName = itemTask.taskName,contents = null)
            //提交任务内容列表
            val tempTaskContents:MutableList<UploadTaskContent> = arrayListOf()
            //遍历
            for (tempItem in dataList){
                //判断是否与当前任务id相同
                if (tempItem.taskId==tempTask.taskId){
                    //判断是否已经填写内容或选择选项
                    if (!TextUtils.isEmpty(tempItem.value)){
                        hasFilledSize++
                        //判断item类型
                        if (tempItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO){
                            val uploadTaskContent=UploadTaskContent(
                                tempItem.contentId,
                                (tempItem.content as SelectorModel).name!!,
                                inputValue = null,
                                selectValue = tempItem.value)
                            tempTaskContents.add(uploadTaskContent)
                        }else if (tempItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT){
                            val uploadTaskContent=UploadTaskContent(
                                tempItem.contentId,
                                tempItem.content as String,
                                inputValue = tempItem.value,
                                selectValue = null)
                            tempTaskContents.add(uploadTaskContent)
                        }
                        canUpload=true
                    }else{
                        //判断item类型
                        if (tempItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO){
                            val uploadTaskContent=UploadTaskContent(
                                tempItem.contentId,
                                (tempItem.content as SelectorModel).name!!,
                                inputValue = null,
                                selectValue = null)
                            tempTaskContents.add(uploadTaskContent)
                        }
                        else if( tempItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT){
                            val uploadTaskContent=UploadTaskContent(
                                tempItem.contentId,
                                tempItem.content as String,
                                inputValue = null,
                                selectValue = null)
                            tempTaskContents.add(uploadTaskContent)
                        }
                    }
                }else{
                    continue
                }


            }
            tempTask.contents=tempTaskContents
            tasks.add(tempTask)
        }
        val uploadTemplateData=UploadTemplateData()
        uploadTemplateData.woId=localWoId
        uploadTemplateData.templateId= mTemplateModel!!.templateId
        uploadTemplateData.tasks=tasks
        uploadTemplateData.startTime=mStartTime

        if (canUpload){

            for (tempItem in dataList){
                //获取需要填充item的数量
                if (tempItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_RADIO
                    ||
                    tempItem.type==MaintenanceEnity.ElectronicLedgerEntity.TYPE_EDIT){
                    needfillSize++
                }
            }


            //判断是否已经全部填充
            allClear=(hasFilledSize==needfillSize)
            if (allClear){
                confirmSamplePass(uploadTemplateData)
            }else{
                val builder = FMWarnDialogBuilder(activity)
                builder.setTitle("")
                builder.setSure(R.string.maintenance_submit_ok)
                builder.setCancel(R.string.maintenance_submit_cancel)
                builder.setTip(R.string.maintenance_task_submit_missing)
                builder.addOnBtnSureClickListener(OnBtnClickListener { dialog, view ->
                    dialog.dismiss()
                    confirmSamplePass(uploadTemplateData)
                })
                builder.create(R.style.fmDefaultWarnDialog).show()
            }

        }else{
            ToastUtils.showShort("请先完成抽检后进行提交")
        }

    }




    /**
     * @Created by: kuuga
     * @Date: on 2021/11/29 14:30
     * @Description: 抽检提交通过确认
     */

    private fun confirmSamplePass(upload:UploadTemplateData){
        upload.pass=false
        val builder = FMWarnDialogBuilder(activity)
        builder.setTitle("")
        builder.setSure(R.string.maintenance_submit_pass)
        builder.setCancel(R.string.maintenance_submit_unpass)
        builder.setTip(R.string.maintenance_task_submit_check)
        builder.addOnBtnSureClickListener(OnBtnClickListener { dialog, view ->
            dialog.dismiss()
            upload.pass=true
            LogUtils.d(GsonUtils.toJson(upload))
            presenter!!.saveTemplateData(upload)
        }).addOnBtnCancelClickListener { dialog, view ->
            dialog.dismiss()
            upload.pass=false
            LogUtils.d(GsonUtils.toJson(upload))
            presenter!!.saveTemplateData(upload)
        }
        builder.create(R.style.fmDefaultWarnDialog).show()
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/11/30 8:40
     * @Description:获取数据失败
     */
    fun onLoadTemplateFail(){
        ToastUtils.showShort("数据")
        pop()
    }
    /**
     * @Created by: kuuga
     * @Date: on 2021/11/26 14:46
     * @Description: 获取模板数据成功回调
     */
    fun onLoadTemplateSuccess(templateModel: TemplateModel) {


        val taskData: MutableList<MaintenanceEnity.ElectronicLedgerEntity> = arrayListOf()
        //赋值给全局变量方便提交时操作
        mTemplateModel=templateModel
        if (templateModel.tasks != null && templateModel.tasks!!.isNotEmpty()) {

            for (item in templateModel.tasks!!) {
                //获取任务标题
                val headItem = MaintenanceEnity.ElectronicLedgerEntity(1, item.taskName)
                taskData.add(headItem)
                //获取任务内容
                for (subItem in item.contents!!) {
                    //填充列表内容
                    val itemContent = chooseItemType(item.taskId!!,subItem)
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
    fun onSubmitTemplateSuccess(pass : Boolean){
        if (!pass){
            ToastUtils.showShort("提交成功")
            popLast()
        }else{
            ToastUtils.showShort("提交成功")
            popResult()
        }

    }

    /**
     * @Created by: kuuga
     * @Date: on 2021/11/30 8:43
     * @Description: 提交失败回调
     */
    fun onSubmitTemplateFail(){
        ToastUtils.showShort("提交失败")
    }

    //选择列表项内容
    private fun chooseItemType(taskId:Long,taskContent: TaskContent): MaintenanceEnity.ElectronicLedgerEntity?{
        return when(taskContent.type){
            TaskContent.CHOICE->{
                MaintenanceEnity.ElectronicLedgerEntity(taskId,taskContent.contentId,2, SelectorModel(taskContent.content,0, taskContent.selectValues))
            }
            TaskContent.INPUT->{
                MaintenanceEnity.ElectronicLedgerEntity(taskId,taskContent.contentId,3,taskContent.content)
            }
            else->{
                null
            }

        }
    }

    companion object {
        private const val LIST_TYPE = "list_type"
        private const val WOID = "woid"
        private const val WOCODE="wocode"
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
        @JvmStatic
        fun getInstance(type: Int?, woId: Long?,woCode:String): MaintenanceElectronicLedgerFragment {
            val bundle = Bundle()
            bundle.putInt(LIST_TYPE, type!!)
            bundle.putLong(WOID, woId!!)
            bundle.putString(WOCODE,woCode)
            val instance = MaintenanceElectronicLedgerFragment()
            instance.arguments = bundle
            return instance
        }
    }

}