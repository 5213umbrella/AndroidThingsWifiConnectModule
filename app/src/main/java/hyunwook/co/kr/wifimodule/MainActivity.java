package hyunwook.co.kr.wifimodule;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import hyunwook.co.kr.wifimodule.wifi.WifiFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private String[] PERMISSION_STORAGE = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Log.d("dddd", "permission grant");

                //agency = new Agency();
                //agency.settingCamera();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한을 거절 한다면 \n현장 동영상 기능을 이용할 수 없습니다. \n\n[설정] -> [권한]을 클릭하셔서 \n모든 권한을 허용해주십시오.")
                .setPermissions(PERMISSION_STORAGE)
                .check();
        gotoWifiFragment();
    }

    private void gotoWifiFragment() {
        WifiFragment wifiFragment = (WifiFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        Log.d(TAG, "goTo ->" + wifiFragment);
        if (wifiFragment == null) {
            wifiFragment = WifiFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), wifiFragment, R.id.contentFrame);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
