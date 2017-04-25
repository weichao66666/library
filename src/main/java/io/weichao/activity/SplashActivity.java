package io.weichao.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;

import io.weichao.library.R;
import io.weichao.util.PermissionUtil;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Created by WEI CHAO on 2017/3/27.
 */
public class SplashActivity extends BaseFragmentActivity {
    /**
     * 请求码
     */
    private static final int REQUEST_CODE = 0;
    /**
     * 所需的全部权限
     */
    private static final String[] PERMISSIONS = new String[]{CAMERA, READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fresco
        Fresco.initialize(this);

        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionUtil.isLackPermissions(this, PERMISSIONS)) {
            //　Android 版本为6.0+，并且缺少权限, 进入权限配置页面
            PermissionActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
        } else {
            onResumeContinue();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionActivity.PERMISSION_DENIED) {
            finish();
        } else {
            onResumeContinue();
        }
    }

    public void onResumeContinue() {
//        startActivity(new Intent(this, MainActivity.class));
//        finish();
    }
}
