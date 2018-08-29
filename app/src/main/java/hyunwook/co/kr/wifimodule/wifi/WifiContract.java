package hyunwook.co.kr.wifimodule.wifi;

import android.net.wifi.ScanResult;

import hyunwook.co.kr.wifimodule.BasePresenter;
import hyunwook.co.kr.wifimodule.BaseView;

/**
 * Created by hyunwook on 2018-08-29.
 */

public interface WifiContract {

    interface View extends BaseView<Presenter> {
        void showProgress(boolean visibility);
        void fetchDevices();
        void startScan();
        void showWifiDevices();
        void connect(ScanResult device, String password);
    }

    interface Presenter extends BasePresenter {
        //presenter...
    }
}
