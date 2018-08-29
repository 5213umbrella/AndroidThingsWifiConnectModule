package hyunwook.co.kr.wifimodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import hyunwook.co.kr.wifimodule.wifi.WifiFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gotoWifiFragment();
    }

    private void gotoWifiFragment() {
        WifiFragment wifiFragment = (WifiFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        Log.d(TAG, "goTo ->" + wifiFragment);
        if (wifiFragment != null) {
            wifiFragment = WifiFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), wifiFragment, R.id.contentFrame);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
