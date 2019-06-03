package com.iwinad.uploadpicture;

import android.text.TextUtils;
import android.widget.EditText;

import com.base.lib.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/5/30
 * @description:
 */
public class SettingDialogFragment extends BaseDialogFragment{

    @BindView(R.id.server_ip_text)
    EditText ipText;
    @BindView(R.id.server_port_text)
    EditText portText;
    @BindView(R.id.server_user_text)
    EditText userText;
    @BindView(R.id.server_password_text)
    EditText passwordText;
    @BindView(R.id.server_path_text)
    EditText pathText;

    @Override
    protected void initView() {
        setDialogView(R.layout.dialog_setting_layout);

        ipText.setText(PreferenceUtil.getPreference(getContext()).getStringPreference(PreferenceUtil.DEFAULT_IP,ipText.getText().toString()));
        portText.setText(PreferenceUtil.getPreference(getContext()).getStringPreference(PreferenceUtil.DEFAULT_PORT,portText.getText().toString()));
        userText.setText(PreferenceUtil.getPreference(getContext()).getStringPreference(PreferenceUtil.REMOTE_USER,userText.getText().toString()));
        passwordText.setText(PreferenceUtil.getPreference(getContext()).getStringPreference(PreferenceUtil.REMOTE_PASSWORD,passwordText.getText().toString()));
        pathText.setText(PreferenceUtil.getPreference(getContext()).getStringPreference(PreferenceUtil.REMOTE_FILE_PAHT,pathText.getText().toString()));
    }
    /**
     * 关闭
     */
    @OnClick({R.id.close_layout,R.id.setting_cancel_layout})void close(){
        dismiss();
    }
    /**
     * 确认
     */
    @OnClick(R.id.setting_sure_layout)void sure(){
        String ipStr = ipText.getText().toString();
        String portStr = portText.getText().toString();
        String userStr = userText.getText().toString();
        String passwordStr = passwordText.getText().toString();
        String pathStr = pathText.getText().toString();

        if(!TextUtils.isEmpty(ipStr)&&!TextUtils.isEmpty(portStr)||!TextUtils.isEmpty(userStr)||!TextUtils.isEmpty(passwordStr)&&!TextUtils.isEmpty(pathStr)){
            PreferenceUtil.getPreference(getContext()).setStringPreference(PreferenceUtil.DEFAULT_IP,ipStr);
            PreferenceUtil.getPreference(getContext()).setStringPreference(PreferenceUtil.DEFAULT_PORT,portStr);
            PreferenceUtil.getPreference(getContext()).setStringPreference(PreferenceUtil.REMOTE_USER,userStr);
            PreferenceUtil.getPreference(getContext()).setStringPreference(PreferenceUtil.REMOTE_PASSWORD,passwordStr);
            PreferenceUtil.getPreference(getContext()).setStringPreference(PreferenceUtil.REMOTE_FILE_PAHT,pathStr);
            ToastUtil.showToast(getContext(),"配置成功");
            dismiss();
        }else {
            ToastUtil.showToast(getContext(),"请输入相应配置信息");
        }
    }
}
