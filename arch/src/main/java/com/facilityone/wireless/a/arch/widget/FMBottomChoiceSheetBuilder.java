package com.facilityone.wireless.a.arch.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
  * @Auther: karelie
  * @Date: 2021/8/16
  * @Infor: 选择型底部弹窗
  */
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.blankj.utilcode.util.ToastUtils;
import com.facilityone.wireless.a.arch.R;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

public class FMBottomChoiceSheetBuilder implements View.OnClickListener{
    public Context mContext;
    private QMUIBottomSheet mDialog;
    private EditText mDescEt;
    private TextView mCountTv;

    private int mMaxNumber = 200;

    private int selectionStart;
    private int selectionEnd;
    private CharSequence temp;
    private TextView mTitle;
    private TextView mTipTv;
    private Button mBtn;
    private Button mLeftBtn;
    private Button mRightBtn;
    private LinearLayout mLLTwoBtn;
    private String showTip;
    private boolean singleNeedInput;//单个按钮的时候是否需要输入内容才可以保存
    private boolean twoNeedLeftInput;//两个按钮的时候是否需要输入内容才可以保存
    private boolean twoNeedRightInput;//两个按钮的时候是否需要输入内容才可以保存

    public QMUIBottomSheet build() {
        mDialog = new QMUIBottomSheet(mContext);
        View contentView = buildViews();
        mDialog.setContentView(contentView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return mDialog;
    }

    public FMBottomChoiceSheetBuilder(Context context) {
        this.mContext = context;
        showTip = "请选择作废原因";
        singleNeedInput = true;
        twoNeedLeftInput = true;
        twoNeedRightInput = true;
    }

    private View buildViews() {
        View wrapperView = View.inflate(mContext, getContentViewLayoutId(), null);
        buildViews(wrapperView);
        mDescEt = (EditText) wrapperView.findViewById(R.id.et_input);
        mCountTv = (TextView) wrapperView.findViewById(R.id.tv_count);
        mTitle = (TextView) wrapperView.findViewById(R.id.tv_title);
        mTipTv = (TextView) wrapperView.findViewById(R.id.tv_tip);
        mBtn = (Button) wrapperView.findViewById(R.id.btn_save);
        mLeftBtn = (Button) wrapperView.findViewById(R.id.btn_left);
        mRightBtn = (Button) wrapperView.findViewById(R.id.btn_right);
        mLLTwoBtn = (LinearLayout) wrapperView.findViewById(R.id.ll_two_btn);
        
        if(mBtn != null) {
            mBtn.setOnClickListener(this);
        }
        if(mLeftBtn != null) {
            mLeftBtn.setOnClickListener(this);
        }
        if(mRightBtn != null) {
            mRightBtn.setOnClickListener(this);
        }
        return wrapperView;
    }

    public int getContentViewLayoutId() {
        return R.layout.fm_bottom_choice_dialog;
    }

    public void buildViews(View wrapperView){

    }


    public FMBottomChoiceSheetBuilder setTitle(String title) {
        if(mTitle != null) {
            mTitle.setText(title);
        }
        return this;
    }

    public FMBottomChoiceSheetBuilder setTitle(@StringRes int title) {
        setTitle(mContext.getResources().getString(title));
        return this;
    }

    public FMBottomChoiceSheetBuilder setTip(String tip) {
        if(mTipTv != null) {
            mTipTv.setText(tip);
        }
        return this;
    }



    public FMBottomChoiceSheetBuilder setTip(@StringRes int tip) {
        setTip(mContext.getResources().getString(tip));
        return this;
    }

    public FMBottomChoiceSheetBuilder setShowTip(boolean showTip){
        if(mTipTv != null) {
            mTipTv.setVisibility(showTip ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public FMBottomChoiceSheetBuilder setDesc(String desc) {
        mDescEt.setText(desc);
        return this;
    }

    public FMBottomChoiceSheetBuilder setDesc(@StringRes int desc) {
        setDesc(mContext.getResources().getString(desc));
        return this;
    }

    public FMBottomChoiceSheetBuilder setDescHint(String desc) {
        mDescEt.setHint(desc);
        return this;
    }

    public FMBottomChoiceSheetBuilder setDescHint(@StringRes int desc) {
        setDescHint(mContext.getResources().getString(desc));
        return this;
    }

    public FMBottomChoiceSheetBuilder setBtnText(String btn) {
        if(mBtn != null) {
            mBtn.setText(btn);
        }
        return this;
    }

    public FMBottomChoiceSheetBuilder setBtnText(@StringRes int btn) {
        setBtnText(mContext.getResources().getString(btn));
        return this;
    }

    public FMBottomChoiceSheetBuilder setLeftBtnText(String btn) {
        if(mLeftBtn != null) {
            mLeftBtn.setText(btn);
        }
        return this;
    }

    public FMBottomChoiceSheetBuilder setLeftBtnText(@StringRes int btn) {
        setLeftBtnText(mContext.getResources().getString(btn));
        return this;
    }

    public FMBottomChoiceSheetBuilder setRightBtnText(String btn) {
        if(mRightBtn != null) {
            mRightBtn.setText(btn);
        }
        return this;
    }

    public FMBottomChoiceSheetBuilder setRightBtnText(@StringRes int btn) {
        setRightBtnText(mContext.getResources().getString(btn));
        return this;
    }

    public FMBottomChoiceSheetBuilder setSingleNeedInput(boolean singleNeedInput) {
        this.singleNeedInput = singleNeedInput;
        return this;
    }

    public FMBottomChoiceSheetBuilder setTwoBtnLeftInput(boolean twoNeedInput) {
        this.twoNeedLeftInput = twoNeedInput;
        return this;
    }

    public FMBottomChoiceSheetBuilder setTwoBtnRightInput(boolean twoNeedInput) {
        this.twoNeedRightInput = twoNeedInput;
        return this;
    }

    public FMBottomChoiceSheetBuilder setSingleBtnBg(@DrawableRes int resID) {
        getSingleBtn().setBackgroundResource(resID);
        return this;
    }

    public EditText getDescEt() {
        return mDescEt;
    }

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



    public void setShowTip(String showTip) {
        this.showTip = showTip;
    }

    @Override
    public void onClick(View v) {
        if (mOnSaveInputListener != null && mDescEt != null) {
            String input = mDescEt.getText().toString();
            if (v.getId() == R.id.btn_save) {
                if (singleNeedInput && TextUtils.isEmpty(input)) {
                    ToastUtils.showShort(showTip);
                    return;
                }
                mOnSaveInputListener.onSaveClick(mDialog, input);

            } else if (v.getId() == R.id.btn_left) {
                if (twoNeedLeftInput && TextUtils.isEmpty(input)) {
                    ToastUtils.showShort(showTip);
                    return;
                }
                mOnSaveInputListener.onLeftClick(mDialog, input);
            } else if (v.getId() == R.id.btn_right) {
                if (twoNeedRightInput && TextUtils.isEmpty(input)) {
                    ToastUtils.showShort(showTip);
                    return;
                }
                mOnSaveInputListener.onRightClick(mDialog, input);
            }
        }
    }


    private FMBottomChoiceSheetBuilder.OnInputBtnClickListener mOnSaveInputListener;


    public void setOnSaveInputListener(FMBottomChoiceSheetBuilder.OnInputBtnClickListener onSaveInputListener) {
        mOnSaveInputListener = onSaveInputListener;
    }


    public interface OnInputBtnClickListener {
        void onSaveClick(QMUIBottomSheet dialog, String input);

        void onLeftClick(QMUIBottomSheet dialog, String input);

        void onRightClick(QMUIBottomSheet dialog, String input);
    }
}
