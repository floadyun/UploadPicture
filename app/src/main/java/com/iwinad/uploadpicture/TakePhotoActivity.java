package com.iwinad.uploadpicture;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.util.FileUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/5/30
 * @description:
 */
public class TakePhotoActivity extends AppBaseActivity {

    @BindView(R.id.main_image)
    ImageView mainImage;

    private static final int TAKE_PHOTO = 1;

    /**
     * 权限数组
     */
    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String imagePath;

    private Uri imgUri;

    private int picIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_layout);
        ButterKnife.bind(this);

        checkPermissions(permissions, 1, new PermissionsResultListener() {
            @Override
            public void onSuccessful(int[] results) {

            }
            @Override
            public void onFailure() {
                showToastText("请开启应用所需权限");
            }
        });

        EventBus.getDefault().register(this);
    }

    /**
     * 退出
     */
    @OnClick(R.id.exit_btn)void exitApp(){
        finishSelf();
    }
    /**
     * 拍照
     */
    @OnClick(R.id.take_photo_btn)void takePhoto(){
        imagePath = Environment.getExternalStorageDirectory()+"/upload/"+System.currentTimeMillis()+".png";
        FileUtil.createfile(imagePath);
        //储存拍照图片file
        File outputImage = new File(imagePath);

        if (outputImage.exists()){
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        if (Build.VERSION.SDK_INT>=24){
            //7.0以上新增的方法 共享文件 FileProvider是一种特殊的内容提供者
            // 第二个参数为对应filepaths.xml中provider（内容提供者的）的name
            imgUri = FileProvider
                    .getUriForFile(this,"com.iwinad.uploadpicture.fileprovider",outputImage);
        }else {
            imgUri = Uri.fromFile(outputImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }
    /**
     * 打开系统设置
     */
    @OnClick(R.id.system_setting_btn)void openSetting(){
        SettingDialogFragment settingFragment = new SettingDialogFragment();
        settingFragment.show(getSupportFragmentManager(),"SettingDialogFragment");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    if(resultCode == RESULT_OK) {
                        uploadImage(imagePath);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 上传图片
     */
    private void uploadImage(final String imagePath){
        showProgressDlg("上传图片...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String serverIp = PreferenceUtil.getPreference(TakePhotoActivity.this).getStringPreference(PreferenceUtil.DEFAULT_IP,"192.168.1.10");
                String serverPort = PreferenceUtil.getPreference(TakePhotoActivity.this).getStringPreference(PreferenceUtil.DEFAULT_PORT,"21");
                String userName = PreferenceUtil.getPreference(TakePhotoActivity.this).getStringPreference(PreferenceUtil.REMOTE_USER,"");
                String password = PreferenceUtil.getPreference(TakePhotoActivity.this).getStringPreference(PreferenceUtil.REMOTE_PASSWORD,"");
                String remotePath = PreferenceUtil.getPreference(TakePhotoActivity.this).getStringPreference(PreferenceUtil.REMOTE_FILE_PAHT,"C:/ftpRoot");
                FtpUtil.connectFtp(picIndex,serverIp, serverPort, userName, password,remotePath,imagePath);
                picIndex++;
            }
        }).start();
    }
    @Subscribe
    public void onEventMainThread(String event){
        stopProgressDlg();
        if(event.equals("1")){//成功

        }else if(event.equals("0")){
            showToastText("图片上传失败");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
