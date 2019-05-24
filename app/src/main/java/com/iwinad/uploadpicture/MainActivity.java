package com.iwinad.uploadpicture;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.File;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppBaseActivity {
    /**
     * ip地址
     */
    @BindView(R.id.server_ip_text)
    EditText server_ip_text;
    /**
     * 端口号
     */
    @BindView(R.id.server_port_text)
    EditText server_port_text;
    /**
     * 用户名
     */
    @BindView(R.id.user_name_text)
    EditText user_name_text;
    /**
     * 远程登录密码
     */
    @BindView(R.id.user_pass_text)
    EditText user_pass_text;
    /**
     * 远程文件路径
     */
    @BindView(R.id.remote_file_text)
    EditText remote_file_text;

    private String ipString,portString,userString,passString,fileString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ipString = PreferenceUtil.getPreference(this).getStringPreference(PreferenceUtil.DEFAULT_IP,"192.168.1.10");
        portString = PreferenceUtil.getPreference(this).getStringPreference(PreferenceUtil.DEFAULT_PORT,"21");
        userString = PreferenceUtil.getPreference(this).getStringPreference(PreferenceUtil.REMOTE_USER,"");
        passString = PreferenceUtil.getPreference(this).getStringPreference(PreferenceUtil.REMOTE_PASSWORD,"");
        fileString = PreferenceUtil.getPreference(this).getStringPreference(PreferenceUtil.REMOTE_FILE_PAHT,"/Users/admin/Desktop/Temp/test/");

        server_ip_text.setText(ipString);
        server_port_text.setText(portString);
        user_name_text.setText(userString);
        user_pass_text.setText(passString);
        remote_file_text.setText(fileString);

        EventBus.getDefault().register(this);
    }
    @OnClick(R.id.select_picture_btn)void selectPicture(){
        PreferenceUtil.getPreference(this).setStringPreference(PreferenceUtil.DEFAULT_IP,server_ip_text.getText().toString());
        PreferenceUtil.getPreference(this).setStringPreference(PreferenceUtil.DEFAULT_PORT,server_port_text.getText().toString());
        PreferenceUtil.getPreference(this).setStringPreference(PreferenceUtil.REMOTE_USER,user_name_text.getText().toString());
        PreferenceUtil.getPreference(this).setStringPreference(PreferenceUtil.REMOTE_PASSWORD,user_pass_text.getText().toString());
        PreferenceUtil.getPreference(this).setStringPreference(PreferenceUtil.REMOTE_FILE_PAHT,remote_file_text.getText().toString());

//        MediaUtil.selectPicture(this, PictureMimeType.ofImage());
    }
    List<LocalMedia> selectList;
    private int pictureSize = 0;
    private int pictureIndex = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    pictureSize = selectList.size();
                    showProgressDlg("上传图片...");
                    new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i=0;i<selectList.size();i++){
                                    File upladFile = new File(selectList.get(i).getPath());
                                    FtpUtil.connectFtp(i,server_ip_text.getText().toString(), server_port_text.getText().toString(), user_name_text.getText().toString(), user_pass_text.getText().toString(),remote_file_text.getText().toString(),upladFile.getPath());}
                                }
                    }).start();
                    break;
            }
        }
    }
    /**
     * 图片上传完成回调
     */
    @Subscribe
    public  void onEventMainThread(String code){
        pictureIndex++;
        if(pictureIndex==pictureSize){
            stopProgressDlg();
            pictureIndex = 0;
        }
        if(code.equals("0")){
            showToastText("第"+pictureIndex+"张图片上传失败.");
            stopProgressDlg();
        }else if(code.equals("-1")){
            showToastText("ftp服务器连接失败.");
            stopProgressDlg();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        FtpUtil.dissConnect();
    }
}
