package com.facilityone.wireless.a.arch.mvvm

import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.SizeUtils
import com.facilityone.wireless.a.arch.R
import com.facilityone.wireless.a.arch.utils.NoDoubleClickListener
import com.facilityone.wireless.basiclib.app.FM
import com.facilityone.wireless.basiclib.utils.StringUtils
import com.gyf.barlibrary.ImmersionBar
import com.joanzapata.iconify.widget.IconTextView
import com.lzy.okgo.OkGo
import com.qmuiteam.qmui.widget.QMUITopBar
import com.qmuiteam.qmui.widget.QMUITopBarLayout
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog.CustomBuilder
import me.yokeyword.fragmentation.SwipeBackLayout
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment

/**
 * @Created by: kuuga
 * @Date: on 2021/8/17 12:00
 * @Description:MVVM模板代码
 */
abstract class BaseDBFragment<T:ViewDataBinding,VM:BaseViewModel> : SwipeBackFragment(),BaseViewModel.Handlers{
    //视图资源ID
    @get:LayoutRes
    abstract val layoutRes: Int
    //视图绑定对象
    lateinit var mBinding:T
    //模型视图层对象
    abstract val viewModel: VM



    lateinit var mDialog: QMUITipDialog
    private var mTip: TextView? = null

    lateinit var mRootView: View
    var mImmersionBar: ImmersionBar?=null
    lateinit var mTopBarLayout: QMUITopBarLayout
    lateinit var mTopBar: QMUITopBar
    lateinit var mMoreView: IconTextView

   abstract val isUseDialog: Boolean

    /**
     * @Created by: kuuga
     * @Date: on 2021/8/17 12:05
     * @Description:数据监测对象
     */
    open fun observeData(){}


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       //创建视图绑定对象
       DataBindingUtil.inflate<T>(inflater, layoutRes, container, false).also {
            mBinding=it
            mRootView=mBinding.root
        }
        //设置可侧滑返回的区域大小
        swipeBackLayout.setEdgeLevel(SwipeBackLayout.EdgeLevel.MIN)
        return attachToSwipeBack(mRootView)
    }


    private fun initDialog() {
        val builder = QMUITipDialog.Builder(context)
        builder.setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
        val tip = FM.getApplication().resources.getString(R.string.dialog_request_loading)
        builder.setTipWord(tip)
        mDialog = builder.create()
        mDialog.setCancelable(false)
    }

    override fun showLoading(): QMUITipDialog {
        return showLoading(false)!!
    }

    fun showLoading(cancelable: Boolean): QMUITipDialog? {
        if (mDialog != null && !mDialog!!.isShowing) {
            mDialog!!.setCancelable(cancelable)
            mDialog!!.show()
        }
        return mDialog
    }

    override fun dismissLoading(): QMUITipDialog {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        }
        return mDialog!!
    }

    fun initProgressBarLoading(): QMUITipDialog {
        val builder = CustomBuilder(context)
        builder.setContent(R.layout.dialog_progress_loading)
        val qmuiTipDialog = builder.create()
        mTip = qmuiTipDialog.findViewById<View>(R.id.tv_tip) as TextView
        return qmuiTipDialog
    }

    fun setTipView(tip: String?) {
        if (mTip != null) {
            mTip!!.text = StringUtils.formatString(tip)
        }
    }



    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    override fun onDestroyView() {
        //清除未完成的网络请求
        OkGo.getInstance().cancelTag(this)


//        dismissLoading()
        super.onDestroyView()
    }

    //加载view
    fun getLoadingView(viewGroup: ViewGroup?): View {
        return getLoadingView(viewGroup, R.string.loading)
    }

    fun getLoadingView(viewGroup: ViewGroup?, msg: String?): View {
        val inflate = LayoutInflater.from(context).inflate(R.layout.loading_view, viewGroup, false)
        if (!TextUtils.isEmpty(msg)) {
            val msgTv = inflate.findViewById<View>(R.id.tv_msg) as TextView
            msgTv.text = msg
        }
        return inflate
    }

    fun getLoadingView(viewGroup: ViewGroup?, @StringRes msg: Int): View {
        return getLoadingView(viewGroup, resources.getString(msg))
    }

    //出错view
    fun getErrorView(viewGroup: ViewGroup?): View {
        return getErrorView(viewGroup, R.string.empty_network_error)
    }

    fun getErrorView(viewGroup: ViewGroup?, msg: String?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.error_view, viewGroup, false)
        view.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(view: View) {
                onNoDataOrErrorClick(view)
            }
        })
        if (!TextUtils.isEmpty(msg)) {
            val msgTv = view.findViewById<View>(R.id.tv_msg) as TextView
            msgTv.text = msg
        }
        return view
    }

    fun getErrorView(viewGroup: ViewGroup?, @StringRes msg: Int): View {
        return getErrorView(viewGroup, resources.getString(msg))
    }

    //无数据view
    fun getNoDataView(viewGroup: ViewGroup?): View {
        return getNoDataView(viewGroup, R.string.empty_no_data)
    }

    fun getNoDataView(viewGroup: ViewGroup?, msg: String?, @DrawableRes drawable: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.empty_view, viewGroup, false)
        view.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(view: View) {
                onNoDataOrErrorClick(view)
            }
        })
        if (!TextUtils.isEmpty(msg)) {
            val msgTv = view.findViewById<View>(R.id.tv_msg) as TextView
            msgTv.text = msg
        }
        if (drawable != -1) {
            val emptyView = view.findViewById<View>(R.id.empty_view_iv) as ImageView
            emptyView.setBackgroundResource(drawable)
        }
        return view
    }

    fun getNoDataView(
        viewGroup: ViewGroup?,
        @StringRes msg: Int,
        @DrawableRes drawable: Int
    ): View {
        return getNoDataView(viewGroup, resources.getString(msg), drawable)
    }

    fun getNoDataView(viewGroup: ViewGroup?, @StringRes msg: Int): View {
        return getNoDataView(viewGroup, resources.getString(msg), -1)
    }

    /**
     * 无数据或者出错的时候点击处理事件
     */
    fun onNoDataOrErrorClick(view: View?) {}
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setParallaxOffset(0.5f)
    }

    //页面视图初始化
    open fun initView(savedInstanceState: Bundle?, view: View) {
        val titleBar = view.findViewById<View>(setTitleBar())
        if (titleBar != null) {
            ImmersionBar.setTitleBar(_mActivity, titleBar)
        }
        val statusBarView = view.findViewById<View>(setStatusBarView())
        if (statusBarView != null) {
            ImmersionBar.setStatusBarView(_mActivity, statusBarView)
        }
        initToolbar(view)
        //初始化弹窗组件
        if (isUseDialog!!) {
            initDialog()
        }
    }

    //页面数据初始化
    open fun initData(savedInstanceState: Bundle?) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onViewCreated(view, savedInstanceState)
        initData(savedInstanceState)
        initView(savedInstanceState,view)

    }




    protected open fun leftBackEnabled(): Boolean {
        return true
    }

    protected open fun isImmersionBarEnabled(): Boolean {
        return false
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mImmersionBar != null) mImmersionBar!!.destroy()
    }
    override fun onSupportVisible() {
        super.onSupportVisible()
        //如果要在Fragment单独使用沉浸式，请在onSupportVisible实现沉浸式
        if (isImmersionBarEnabled()) {
            mImmersionBar = ImmersionBar.with(this)
            mImmersionBar!!.navigationBarWithKitkatEnable(false).init()
        }
    }

    protected open fun setTitleBar(): Int {
        return 0
    }

    protected open fun setStatusBarView(): Int {
        return 0
    }

    protected open fun initToolbar(view: View) {
        val titleBar = view.findViewById<View>(setTitleBar())
        if (titleBar != null) {
            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            if (titleBar is QMUITopBarLayout) {
                mTopBarLayout = titleBar
                if (leftBackEnabled()) {
                    mTopBarLayout.addLeftView(getLeftBackView(), R.id.topbar_left_back_id, lp)
                }
            } else if (titleBar is QMUITopBar) {
                mTopBar = titleBar
                if (leftBackEnabled()) {
                    mTopBar.addLeftView(getLeftBackView(), R.id.topbar_left_back_id, lp)
                }
            }
        }
    }

    open fun getLeftBackView(): View? {
        val view =
            LayoutInflater.from(context).inflate(R.layout.fm_topbar_back, null) as IconTextView
        view.setOnClickListener { leftBackListener() }
        return view
    }

    open fun leftBackListener() {
        pop()
    }

    open fun setTitle(resId: Int): TextView? {
        return setTitle(context!!.getString(resId))
    }

    open fun setTitle(title: String?): TextView? {
        var textView: TextView? = null
        if (mTopBarLayout != null) {
            textView = mTopBarLayout.setTitle(title)
            return textView
        } else if (mTopBar != null) {
            textView = mTopBar.setTitle(title)
        }
        return textView
    }

    open fun setMoreMenu() {
        if (mMoreView == null) {
            mMoreView =
                LayoutInflater.from(context).inflate(R.layout.fm_topbar_more, null) as IconTextView
            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            mMoreView.layoutParams = lp
            if (mTopBarLayout != null) {
                mTopBarLayout.addRightView(mMoreView, R.id.topbar_right_more_id)
            } else if (mTopBar != null) {
                mTopBar.addRightView(mMoreView, R.id.topbar_right_more_id)
            }
            mMoreView.setOnClickListener(object : NoDoubleClickListener() {
                override fun onNoDoubleClick(view: View) {
                    onMoreMenuClick(view)
                }
            })
        }
    }

    open fun hideShowMoreMenu(show: Boolean) {
        if (mMoreView != null) {
            mMoreView.visibility = if (show) View.VISIBLE else View.GONE
        }
    }


    open fun setMoreMenuVisible(visible: Boolean) {
        if (mMoreView != null) {
            mMoreView.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    open fun removeRightView() {
        if (mTopBarLayout != null) {
            mTopBarLayout.removeAllRightViews()
        } else if (mTopBar != null) {
            mTopBar.removeAllRightViews()
        }
    }

    open fun setRightIcon(
        @StringRes textId: Int,
        @IdRes id: Int,
        listener: NoDoubleClickListener?
    ) {
        setRightIcon(textId, id, R.dimen.topbar_back_size, listener)
    }

    open fun setRightIcon(
        @StringRes textId: Int,
        @IdRes id: Int,
        @DimenRes textSize: Int,
        listener: NoDoubleClickListener?
    ) {
        val icon =
            LayoutInflater.from(context).inflate(R.layout.fm_topbar_more, null) as IconTextView
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        icon.layoutParams = lp
        icon.setPadding(SizeUtils.dp2px(8f), 0, SizeUtils.dp2px(8f), 0)
        icon.setText(textId)
        icon.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(textSize))
        if (mTopBarLayout != null) {
            mTopBarLayout.addRightView(icon, id)
        } else if (mTopBar != null) {
            mTopBar.addRightView(icon, id)
        }
        icon.setOnClickListener(listener)
    }

    open fun setRightImageButton(@DrawableRes drawableResId: Int, @IdRes viewId: Int) {
        val imageView = ImageView(context)
        imageView.setBackgroundResource(drawableResId)
        val layoutParams = RelativeLayout.LayoutParams(SizeUtils.dp2px(20f), SizeUtils.dp2px(20f))
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        layoutParams.setMargins(SizeUtils.dp2px(10f), 0, SizeUtils.dp2px(10f), 0)
        imageView.layoutParams = layoutParams
        if (mTopBarLayout != null) {
            mTopBarLayout.addRightView(imageView, viewId)
        } else if (mTopBar != null) {
            mTopBar.addRightView(imageView, viewId)
        }
        imageView.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(view: View) {
                onRightImageMenuClick(view)
            }
        })
    }


    /**
     * 添加右边文本菜单
     *
     * @param text
     * @param id
     */
    open fun setRightTextButton(text: String?, @IdRes id: Int) {
        var button: Button? = null
        if (mTopBarLayout != null) {
            button = mTopBarLayout.addRightTextButton(text, id)
        } else if (mTopBar != null) {
            button = mTopBar.addRightTextButton(text, id)
        }
        if (button == null) {
            return
        }
        button.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(view: View) {
                onRightTextMenuClick(view)
            }
        })
    }

    open fun setRightTextButton(@StringRes textId: Int, @IdRes id: Int) {
        val text = context!!.resources.getString(textId)
        setRightTextButton(text, id)
    }

    open fun onMoreMenuClick(view: View?) {}

    open fun onRightTextMenuClick(view: View?) {}

    open fun onRightImageMenuClick(view: View?) {}

    open fun setSubTitle(resId: Int) {
        setSubTitle(context!!.getString(resId))
    }

    open fun setSubTitle(title: String?) {
        if (mTopBarLayout != null) {
            mTopBarLayout.setSubTitle(title)
        } else if (mTopBar != null) {
            mTopBar.setSubTitle(title)
        }
    }

}