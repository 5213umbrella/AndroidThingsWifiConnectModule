package hyunwook.co.kr.wifimodule.wifi;

public class WifiPresenter implements WifiContract.Presenter {

    private WifiContract.View mView;

    public WifiPresenter(WifiContract.View view) {
        mView = view;
    }

    @Override
    public void start() {

    }
}
