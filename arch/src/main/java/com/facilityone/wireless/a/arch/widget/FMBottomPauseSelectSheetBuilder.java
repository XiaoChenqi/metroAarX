package com.facilityone.wireless.a.arch.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.R;
import com.facilityone.wireless.a.arch.ec.module.ISelectDataService;
import com.facilityone.wireless.a.arch.ec.module.ReasonResponseBean;
import com.facilityone.wireless.a.arch.ec.module.SelectDataBean;
import com.facilityone.wireless.basiclib.utils.DataUtils;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Created by: kuuga
 * @Date: on 2021/8/13 12:04
 * @Description: 选择dialog
 */
public class FMBottomPauseSelectSheetBuilder implements View.OnClickListener {

    public Context mContext;
    private QMUIBottomSheet mDialog;
    private EditText mDescSelect;
    private TextView mCountTv;

    private int mMaxNumber = 200;
    private final static String RESULT_REASON="pause_reason";

    private int selectionStart;
    private int selectionEnd;
    private CharSequence temp;
    private TextView mTitle;
    private TextView mTipTv;
    private Button mBtn;
    private Button mLeftBtn;
    private Button mRightBtn;
    private LinearLayout mLLTwoBtn;
    public CustomContentItemView mCivTime;//暂停结束时间选择
    private String showTip;
    private boolean singleNeedInput;//单个按钮的时候是否需要输入内容才可以保存
    private boolean twoNeedLeftInput;//两个按钮的时候是否需要输入内容才可以保存
    private boolean twoNeedRightInput;//两个按钮的时候是否需要输入内容才可以保存

    private SelectDataBean reasonBean;



    public FMBottomPauseSelectSheetBuilder(Context context) {
        this.mContext = context;
        showTip = context.getString(R.string.arch_work_content_hint);
        singleNeedInput = true;
        twoNeedLeftInput = true;
        twoNeedRightInput = true;
    }

    public QMUIBottomSheet build() {
        mDialog = new QMUIBottomSheet(mContext);
        View contentView = buildViews();
        mDialog.setContentView(contentView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return mDialog;
    }

    private void showTimePicker(){
        //设置开始时间和结束时间
        Calendar endDate=Calendar.getInstance();
        Calendar startDate=Calendar.getInstance();
        endDate.set(2100,11,31,23,59);


        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(mContext, (date, v) -> {//选中事件回调
            mCivTime.setTipText(getTime(date));
            Long time=TimeUtils.date2Millis(date);
            mCivTime.setTag(time);
        })
        .setDate(startDate)
        .setRangDate(startDate,endDate)
        .setType(new boolean[]{true, true, true, true, true, false})
        .isDialog(true).build();
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }

        pvTime.show();
    }

    private View buildViews() {
        View wrapperView = View.inflate(mContext, getContentViewLayoutId(), null);
        buildViews(wrapperView);
//        mDescEt = (EditText) wrapperView.findViewById(R.id.et_input);
        mDescSelect=(EditText) wrapperView.findViewById(R.id.et_select_input);
        mCountTv = (TextView) wrapperView.findViewById(R.id.tv_count);
        mTitle = (TextView) wrapperView.findViewById(R.id.tv_title);
        mTipTv = (TextView) wrapperView.findViewById(R.id.tv_tip);
        mBtn = (Button) wrapperView.findViewById(R.id.btn_save);
        mLeftBtn = (Button) wrapperView.findViewById(R.id.btn_left);
        mRightBtn = (Button) wrapperView.findViewById(R.id.btn_right);
        mLLTwoBtn = (LinearLayout) wrapperView.findViewById(R.id.ll_two_btn);
        mCivTime=(CustomContentItemView)wrapperView.findViewById(R.id.civ_time);

        mCivTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

//        if(mDescEt != null) {
//            mDescEt.addTextChangedListener(this);
//        }
        mDescSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong("点击了原因选择");
            }
        });

        if(mBtn != null) {
            mBtn.setOnClickListener(this);
        }
        if(mLeftBtn != null) {
            mLeftBtn.setOnClickListener(this);
        }
        if(mRightBtn != null) {
            mRightBtn.setOnClickListener(this);
        }
        setInputNumber(mMaxNumber);
        return wrapperView;
    }

    public int getContentViewLayoutId() {
        return R.layout.fm_bottom_sheet_pause_select;
    }

    public void buildViews(View wrapperView){

    }



    private FMBottomPauseSelectSheetBuilder setInputNumber(int value) {
        mCountTv.setText(String.format(Locale.getDefault(), mContext.getString(R.string.arch_char_no_more), DataUtils.getNumberValue(value)));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setTitle(String title) {
        if(mTitle != null) {
            mTitle.setText(title);
        }
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setTitle(@StringRes int title) {
        setTitle(mContext.getResources().getString(title));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setTip(String tip) {
        if(mTipTv != null) {
            mTipTv.setText(tip);
        }
        return this;
    }



    public FMBottomPauseSelectSheetBuilder setTip(@StringRes int tip) {
        setTip(mContext.getResources().getString(tip));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setShowTip(boolean showTip){
        if(mTipTv != null) {
            mTipTv.setVisibility(showTip ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setDesc(String desc) {
        mDescSelect.setText(desc);
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setDesc(@StringRes int desc) {
        setDesc(mContext.getResources().getString(desc));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setDescHint(String desc) {
        mDescSelect.setHint(desc);
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setDescHint(@StringRes int desc) {
        setDescHint(mContext.getResources().getString(desc));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setBtnText(String btn) {
        if(mBtn != null) {
            mBtn.setText(btn);
        }
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setBtnText(@StringRes int btn) {
        setBtnText(mContext.getResources().getString(btn));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setLeftBtnText(String btn) {
        if(mLeftBtn != null) {
            mLeftBtn.setText(btn);
        }
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setLeftBtnText(@StringRes int btn) {
        setLeftBtnText(mContext.getResources().getString(btn));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setRightBtnText(String btn) {
        if(mRightBtn != null) {
            mRightBtn.setText(btn);
        }
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setRightBtnText(@StringRes int btn) {
        setRightBtnText(mContext.getResources().getString(btn));
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setSingleNeedInput(boolean singleNeedInput) {
        this.singleNeedInput = singleNeedInput;
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setTwoBtnLeftInput(boolean twoNeedInput) {
        this.twoNeedLeftInput = twoNeedInput;
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setTwoBtnRightInput(boolean twoNeedInput) {
        this.twoNeedRightInput = twoNeedInput;
        return this;
    }

    public FMBottomPauseSelectSheetBuilder setSingleBtnBg(@DrawableRes int resID) {
        getSingleBtn().setBackgroundResource(resID);
        return this;
    }

//    public EditText getDescEt() {
//        return mDescEt;
//    }

    public TextView getTitle() {
        return mTitle;
    }

    public Button getSingleBtn() {
        return mBtn;
    }

    public Button getLeftBtn() {
        return mLeftBtn;
    }

    public Button getRightBtn() {
        return mRightBtn;
    }

    public LinearLayout getLLTwoBtn() {
        return mLLTwoBtn;
    }

    public EditText getDescSelect(){
        return mDescSelect;
    }

    public void setReasonData(Bundle bundle){
        SelectDataBean reason=bundle.getParcelable(ISelectDataService.SELECT_OFFLINE_DATA_BACK);
        this.reasonBean=reason;
        if (reasonBean!=null){
            mDescSelect.setText(reasonBean.getFullName());
        }

    }




    public CustomContentItemView getCivTime(){
        return mCivTime;
    }

    public void setShowTip(String showTip) {
        this.showTip = showTip;
    }

    public FMBottomPauseSelectSheetBuilder setMaxNumber(int maxNumber) {
        this.mMaxNumber = maxNumber;
        return this;
    }

    @Override
    public void onClick(View v) {



      if (mOnPaseInputListener!=null&&mDescSelect!=null){
            String input = mDescSelect.getText().toString();
            Long time= (Long) mCivTime.getTag();
            if (v.getId() == R.id.btn_left) {
                if (twoNeedLeftInput && TextUtils.isEmpty(input)) {
                    ToastUtils.showShort(showTip);
                    return;
                }
                mOnPaseInputListener.onLeftClick(mDialog, reasonBean,time);
            } else if (v.getId() == R.id.btn_right) {
                if (twoNeedRightInput && TextUtils.isEmpty(input)) {
                    ToastUtils.showShort(showTip);
                    return;
                }
                mOnPaseInputListener.onRightClick(mDialog,reasonBean,time);
            }
        }
    }


    private OnInputBtnClickListener mOnSaveInputListener;


    public void setOnSaveInputListener(OnInputBtnClickListener onSaveInputListener) {
        mOnSaveInputListener = onSaveInputListener;
    }


    public interface OnInputBtnClickListener {
        void onSaveClick(QMUIBottomSheet dialog, String input);

        void onLeftClick(QMUIBottomSheet dialog, String input);

        void onRightClick(QMUIBottomSheet dialog, String input);
    }


    //暂停事件监听器
    private OnPauseInputBtnClickListener mOnPaseInputListener;
    public void setOnPauseInputListener(OnPauseInputBtnClickListener onPauseInputListener){
        mOnPaseInputListener=onPauseInputListener;
    }

    public interface OnPauseInputBtnClickListener{
        void onLeftClick(QMUIBottomSheet dialog, SelectDataBean reasonBean, Long time);

        void onRightClick(QMUIBottomSheet dialog, SelectDataBean reasonBean,Long time);
    }


    //时间格式化
    @SuppressLint("SimpleDateFormat")
    private String getTime(Date date) {
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }



}
