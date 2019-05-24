package com.iwinad.uploadpicture;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Android 6.0 上权限分为<b>正常</b>和<b>危险</b>级别
 * <ul>
 * <li>正常级别权限：开发者仅仅需要在AndroidManifext.xml上声明，那么应用就会被允许拥有该权限，如：android.permission.INTERNET</li>
 * <li>危险级别权限：开发者需要在AndroidManifext.xml上声明，并且在运行时进行申请，而且用户允许了，应用才会被允许拥有该权限，如：android.permission.WRITE_EXTERNAL_STORAGE</li>
 * </ul>
 * 有米的以下权限需要在Android6.0上被允许，有米广告sdk才能正常工作，开发者需要在调用有米的任何代码之前，提前让用户允许权限
 * <ul>
 * <li>必须申请的权限
 * <ul>
 * <li>android.permission.READ_PHONE_STATE</li>
 * <li>android.permission.WRITE_EXTERNAL_STORAGE</li>
 * </ul>
 * </li>
 * <li>可选申请的权限
 * <ul>
 * <li>android.permission.ACCESS_FINE_LOCATION</li>
 * </ul>
 * </li>
 * </ul>
 * <p>Android 6.0 权限申请助手</p>
 * Created by Alian on 16-1-12.
 */
public class PermissionHelper {

	private static final String TAG = "PermissionHelper";

	private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
	private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

	/**
	 * 小tips:这里的int数值不能太大，否则不会弹出请求权限提示，测试的时候,改到1000就不会弹出请求了
	 */
	public final static int ACCESS_CAMERA_CODE = 100;

	public final static int ACCESS_LOCATION_CODE = 101;

	public final static int READ_PHONE_STATE_CODE = 102;

	public final static int WRITE_EXTERNAL_STORAGE_CODE = 103;

	public final static int REQUEST_OPEN_APPLICATION_SETTINGS_CODE = 104;

	/**
	 * Android SDK 所需要向用户申请的权限列表
	 */
	private PermissionModel[] mPermissionModels = null;

	private Activity mActivity;

	private OnApplyPermissionListener mOnApplyPermissionListener;
	//需请求的权限
	private String permission;
	//需请求的权限码
	private int permissionCode;

	public PermissionHelper(Activity activity) {
		mActivity = activity;
	}

	public void setOnApplyPermissionListener(OnApplyPermissionListener onApplyPermissionListener) {
		mOnApplyPermissionListener = onApplyPermissionListener;
	}
	/**
	 * 判断是否允许打开通知栏
	 * @param context
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public static boolean isNotificationEnabled(Context context) {

		AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
		ApplicationInfo appInfo = context.getApplicationInfo();
		String pkg = context.getApplicationContext().getPackageName();
		int uid = appInfo.uid;

		Class appOpsClass = null;
    /* Context.APP_OPS_MANAGER */
		try {
			appOpsClass = Class.forName(AppOpsManager.class.getName());
			Method checkOpNoThrowMethod =
					appOpsClass.getMethod(
							CHECK_OP_NO_THROW,
							Integer.TYPE,
							Integer.TYPE,
							String.class
					);
			Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
			int value = (int) opPostNotificationValue.get(Integer.class);

			return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==AppOpsManager.MODE_ALLOWED);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 这里我们演示如何在Android 6.0+上运行时申请权限
	 */
	public void applyPermissions() {
		try {
			for (final PermissionModel model : mPermissionModels) {
				if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity, model.permission)) {
					ActivityCompat.requestPermissions(mActivity, new String[] { model.permission }, model.requestCode);
					return;
				}
			}
			if (mOnApplyPermissionListener != null) {
				mOnApplyPermissionListener.onAfterApplyAllPermission();
			}
		} catch (Throwable e) {
			Log.e(TAG, "", e);
		}
	}

	/**
	 * 对应Activity的 {@code onRequestPermissionsResult(...)} 方法
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		notifyPermission(grantResults);
	}
	/**
	 * 权限提醒
	 * @param grantResults
	 */
	private void notifyPermission(int[] grantResults){
		if(PackageManager.PERMISSION_GRANTED!=grantResults[0]){
			if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)){

			}
		}else{
			if (mOnApplyPermissionListener != null) {
				mOnApplyPermissionListener.onAfterApplyPermission();
			}
		}
	}
	/**
	 * 对应Activity的 {@code onActivityResult(...)} 方法
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_OPEN_APPLICATION_SETTINGS_CODE:
			if (isAllRequestedPermissionGranted(mPermissionModels)) {
				if (mOnApplyPermissionListener != null) {
					mOnApplyPermissionListener.onAfterApplyAllPermission();
				}
			} else {
				mActivity.finish();
			}
			break;
		}
	}
	/**
	 * 判断是否授权
	 * @return
	 */
	public boolean isRequestPermission(String permission){
		if (Build.VERSION.SDK_INT >= 23) {
			if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity, permission)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 申请单个权限
	 */
	public void applyPermission(String permission,int requestCode,OnApplyPermissionListener permissionListener) {
		this.permission = permission;
		this.permissionCode = requestCode;
		if(permissionListener!=null){
			mOnApplyPermissionListener = permissionListener;
		}
		try {
			if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity, permission)) {
				ActivityCompat.requestPermissions(mActivity, new String[] { permission }, requestCode);
				return;
			}
		} catch (Throwable e) {
			Log.e(TAG, "", e);
		}
	}
	/**
	 * 判断是否所有的权限都被授权了
	 * @return
	 */
	public boolean isAllRequestedPermissionGranted(PermissionModel[] permissionModels) {
		mPermissionModels = permissionModels;
		for (PermissionModel model : permissionModels) {
			if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity, model.permission)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 申请所有权限
	 * @param permissionListener
	 */
	public void applyAllPermission(PermissionModel[] permissionModels,OnApplyPermissionListener permissionListener) {
		this.mPermissionModels = permissionModels;
		if(permissionListener!=null){
			mOnApplyPermissionListener = permissionListener;
		}
		try {
			for(int i=0;i<permissionModels.length;i++){
				String permission = mPermissionModels[i].permission;
				if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity, permission)) {
					ActivityCompat.requestPermissions(mActivity, new String[] { permission }, mPermissionModels[i].requestCode);
				}
			}
			mOnApplyPermissionListener.onAfterApplyAllPermission();
		} catch (Throwable e) {
			Log.e(TAG, "", e);
		}
	}
	/**
	 * 打开应用设置界面
	 *
	 * @param requestCode 请求码
	 *
	 * @return
	 */
	private boolean openApplicationSettings(int requestCode) {
		try {
			Intent intent =
					new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + mActivity.getPackageName()));
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			// Android L 之后Activity的启动模式发生了一些变化
			// 如果用了下面的 Intent.FLAG_ACTIVITY_NEW_TASK ，并且是 startActivityForResult
			// 那么会在打开新的activity的时候就会立即回调 onActivityResult
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mActivity.startActivityForResult(intent, requestCode);
			return true;
		} catch (Throwable e) {
			Log.e(TAG, "", e);
		}
		return false;
	}
	/**
	 * 查找申请权限的解释短语
	 *
	 * @param permission 权限
	 *
	 * @return
	 */
	private String findPermissionExplain(String permission) {
		if (mPermissionModels != null) {
			for (PermissionModel model : mPermissionModels) {
				if (model != null && model.permission != null && model.permission.equals(permission)) {
					return model.explain;
				}
			}
		}
		return null;
	}

	/**
	 * 查找申请权限的名称
	 *
	 * @param permission 权限
	 *
	 * @return
	 */
	private String findPermissionName(String permission) {
		if (mPermissionModels != null) {
			for (PermissionModel model : mPermissionModels) {
				if (model != null && model.permission != null && model.permission.equals(permission)) {
					return model.name;
				}
			}
		}
		return null;
	}

	private static class PermissionModel {

		/**
		 * 权限名称
		 */
		public String name;

		/**
		 * 请求的权限
		 */
		public String permission;

		/**
		 * 解析为什么请求这个权限
		 */
		public String explain;

		/**
		 * 请求代码
		 */
		public int requestCode;

		public PermissionModel(String name, String permission, String explain, int requestCode) {
			this.name = name;
			this.permission = permission;
			this.explain = explain;
			this.requestCode = requestCode;
		}
	}
	/**
	 * 权限申请事件监听
	 */
	public interface OnApplyPermissionListener {
		/**
		 * 申请单个权限之后的逻辑
		 */
		void onAfterApplyPermission();

		/**
		 * 申请所有权限之后的逻辑
		 */
		void onAfterApplyAllPermission();
	}
}
