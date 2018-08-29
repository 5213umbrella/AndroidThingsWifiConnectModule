package hyunwook.co.kr.wifimodule.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import hyunwook.co.kr.wifimodule.listener.OnWifiListener;

/**
 * Created by hyunwook on 2018-08-29.
 */

public class WifiBroadcastReceiver extends BroadcastReceiver {

    OnWifiListener mOnWifiListener;

    static final String TAG = WifiBroadcastReceiver.class.getSimpleName();

    public WifiBroadcastReceiver(OnWifiListener onWifiListener) {
        mOnWifiListener = onWifiListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive....");
        String action = intent.getAction();

        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            mOnWifiListener.resultAvailable();
        }
    }
}
