package com.iwinad.uploadpicture;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.base.lib.baseui.AppBaseActivity;

import butterknife.ButterKnife;

/**
 * Created by yixiaofei on 2017/3/16 0016.
 * 弹窗基类,背景不透明
 */
public abstract class BaseDialogFragment extends DialogFragment {

    protected Dialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initView();
        return dialog;
    }
    protected abstract void initView();

    protected void setDialogView(int layoutId){
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //设置Content前设定
        dialog.setContentView(layoutId);
        dialog.setCanceledOnTouchOutside(false); //外部点击取消

        ButterKnife.bind(this,dialog);
    }
    protected void setDialogView(View layoutView){
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //设置Content前设定
        dialog.setContentView(layoutView);
        dialog.setCanceledOnTouchOutside(false); //外部点击取消

        ButterKnife.bind(this,dialog);
    }
    protected void setViewWindow(){
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);                       //从下方弹出
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //宽度填满
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;  //高度自适应
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(getActivity().getResources().getColor(R.color.half_transparent));
        // 设置SelectPicPopupWindow弹出窗体的背景
        win.setBackgroundDrawable(dw);
        win.setAttributes(lp);
    }

    /**
     * 设置全屏
     */
    public void setFullScreen(){
        Window win = dialog.getWindow();
        win.setGravity(Gravity.CENTER);                       //从下方弹出
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //宽度填满
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;  //高度自适应
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(getActivity().getResources().getColor(R.color.half_transparent));
        // 设置SelectPicPopupWindow弹出窗体的背景
        win.setBackgroundDrawable(dw);
        win.setAttributes(lp);
    }
    /**
     * 显示提示框
     * @param message
     */
    public void showProgress(String message){
        ((AppBaseActivity)getActivity()).showProgressDlg(message);
    }
    /**
     * 停止提示进度
     */
    public void stopProgress(){
        ((AppBaseActivity)getActivity()).stopProgressDlg();
    }

    public void showShare(String shareUrl, String shareTitle, String imageUrl, String description, int defaultImage){
        ((AppBaseActivity)getActivity()).showShare(shareUrl,shareTitle,imageUrl,description,defaultImage);
    }
}
