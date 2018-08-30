package hyunwook.co.kr.wifimodule;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import hyunwook.co.kr.wifimodule.wifi.WifiFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity /*implements EasyPermissions.PermissionCallbacks*/ {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] LOCATION =
            {Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int RC_WIFI_PERM = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (hasWifiPermission()) {
            gotoWifiFragment();
        /*} else {
            EasyPermissions.requestPermissions(this, "권한필요", RC_WIFI_PERM, LOCATION);
        }*/
    }
/*
    private boolean hasWifiPermission() {
        return EasyPermissions.hasPermissions(this, LOCATION);
    }*/

    @AfterPermissionGranted(RC_WIFI_PERM)
    private void gotoWifiFragment() {
        WifiFragment wifiFragment = (WifiFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        Log.d(TAG, "goTo ->" + wifiFragment);
        if (wifiFragment == null) {
            wifiFragment = WifiFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), wifiFragment, R.id.contentFrame);
        }
    }


   /* @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }
*/
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String yes = "yes";
            String no = "no";

            Toast.makeText(this, getString(R.string.permissionToast, hasWifiPermission() ? yes: no), Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
