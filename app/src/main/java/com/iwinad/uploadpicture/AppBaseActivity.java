package com.iwinad.uploadpicture;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import com.kaopiz.kprogresshud.KProgressHUD;

/*
 *
 * @copyright : 深圳市创冠新媒体网络传媒有限公司版权所有
 *
 * @author :yixiaofei
 *
 * @version :1.0
 *
 * @creation date: 2017/5/22 0022
 *
 * @description:所有activity基类
 *
 * @update date :
 */

public class AppBaseActivity extends AppCompatActivity {

    protected String pageName;
    /**
     * 进度提示宽
     */
    public KProgressHUD kProgressHUD;

    private PermissionHelper permissionHelper;

    protected Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
    }
    /**
     * 权限请求
     * @param permission
     * @param permissionCode
     * @param applyPermissionListener
     */
    public boolean applyPermission(String permission, int permissionCode, PermissionHelper.OnApplyPermissionListener applyPermissionListener){
        permissionHelper = new PermissionHelper(this);
        if(!permissionHelper.isRequestPermission(permission)){
            permissionHelper.applyPermission(permission, permissionCode,applyPermissionListener);
            return false;
        }
        return true;
    }
    /**
     * 信息提示
     * @param text
     */
    protected void showToastText(final String text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                AbToastUtil.showToast(getApplicationContext(),text);
            }
        });
    }
    protected void showToastText(int resId){
        AbToastUtil.showToast(getApplicationContext(),resId);
    }

    public void showProgressDlg(String title, String strMessage) {
        if (null == kProgressHUD&&!isFinishing()) {
            kProgressHUD  =KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            if(!title.isEmpty()){
                kProgressHUD.setLabel(title);
            }
            if(!strMessage.isEmpty()){
                kProgressHUD.setDetailsLabel(strMessage);
            }
        }
    }

    /**
     * 启动进度条
     * @param title
     */
    public void showProgressDlg(String title) {
        try {
            showProgressDlg(title,"");
        }catch (Exception e){

        }
    }
    /**
     *启动进度条
     *进度条显示的信息
     * 当前的activity
     */
    public void showProgressDlg() {
        try {
            showProgressDlg("","");
        }catch (Exception e){

        }
    }

    /**
     * 动态设置progress进度
     * @param text
     */
    public  void setProressText(String text){
        if (null != kProgressHUD&&!isFinishing()) {
            kProgressHUD.setLabel(text);
        }
    }
    /**
     * 结束进度条
     */
    public  void stopProgressDlg() {
        if (null != kProgressHUD) {
            kProgressHUD.dismiss();
            kProgressHUD = null;
        }
    }
    /***
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        dismissKeyboard();
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /**
     * 隐藏键盘
     */
    protected void dismissKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        try {
            if(null!=this.getCurrentFocus().getWindowToken()){
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e){

        }
    }
    @Override
    public void onBackPressed() {
        finishSelf();
    }
    protected void finishSelf(){
        dismissKeyboard();
        finish();
    }
}
