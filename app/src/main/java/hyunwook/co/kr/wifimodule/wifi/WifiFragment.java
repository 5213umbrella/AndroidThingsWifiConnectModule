package hyunwook.co.kr.wifimodule.wifi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import hyunwook.co.kr.wifimodule.R;
import hyunwook.co.kr.wifimodule.adapter.WifiAdapter;
import hyunwook.co.kr.wifimodule.listener.OnListFragmentInteractionListener;
import hyunwook.co.kr.wifimodule.listener.OnWifiListener;

/**
 * Created by hyunwook on 2018-08-29.
 */

public class WifiFragment extends Fragment implements WifiContract.View, OnWifiListener, OnListFragmentInteractionListener {

    private final IntentFilter intentFilter = new IntentFilter();
    WifiManager mManager;
    List<ScanResult> wifiList;

    private ProgressBar progressBar;

    private RecyclerView mRecyclerView;

    private Context mContext;
    WifiAdapter adapter;

    WifiBroadcastReceiver receiver;
    static final String TAG = WifiFragment.class.getSimpleName();

    int netId;
    public static WifiFragment newInstance() {
        return new WifiFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION); //스캔 탐지.

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_list, container, false);

      /*  ActionBar mActionBar = getActivity().getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(mContext);

        View mcus*/

        mContext = getActivity();

        mRecyclerView = view.findViewById(R.id.list);

        progressBar = view.findViewById(R.id.progressBar);


        Button btnScan = view.findViewById(R.id.scanBtn);
        btnScan.setOnClickListener(view1 -> startScan());
        Button btnFinish = view.findViewById(R.id.finishBtn);
        btnFinish.setOnClickListener(view2 -> {
            //@Override
            //public void onClick(View view) {
            completeDialog(view2);
            //}
        });
        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(true);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        int netType = (activeNetwork == null) ? -1 : activeNetwork.getType();
        String ssidState;
        if (netType == cm.TYPE_WIFI) {
            ssidState = wm.getConnectionInfo().getSSID();
            Log.d(TAG, "ssidstate ->" + ssidState.toString());
            TextView titleView = view.findViewById(R.id.title_text);
            titleView.setText(ssidState.toString() + "--"+ getLocalIpAddress() +"--" + netId);
        }

    /*    Button btnFinish = view.findViewById(R.id.finishBtn);
        btnFinish.setOnClickListener(view2 -> {
         *//*   ComponentName componentName = getActivity().getPackageManager().getLaunchIntentForPackage("com.galarzaa.androidthings.samples").getComponent();
            Intent intent = IntentCompat.makeRestartActivityTask(componentName);
            startActivity(intent);*//*
            //와이파이 모듈 앱 종료
           getActivity().moveTaskToBack(true);
            getActivity().finish();
            android.os.Process.killProcess(android.os.Process.myPid());

           *//* //메인 라즈베리파이 출퇴근 앱 재실행
            PackageManager pm = getContext().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("com.galarzaa.androidthings.samples");
            ComponentName cm = intent.getComponent();*//*

     *//*       Intent mainIntent = Intent.makeRestartActivityTask(cm);
            getContext().startActivity(mainIntent);
            System.exit(0);*//*
        });*/

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager.setWifiEnabled(true);

        return view;

    }

    public void completeDialog(View v) {
        Log.d(TAG, "completeDialog -----");

        final Dialog dialog = new Dialog(v.getContext());
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        dialog.setContentView(R.layout.dialog_complete);
        dialog.setTitle("Wi-Fi");

        dialog.findViewById(R.id.ok).setOnClickListener(l -> {
            Log.d(TAG, "ok ---------------");
            try {
                Runtime.getRuntime().exec(new String[]{"reboot"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        dialog.findViewById(R.id.exit).setOnClickListener(e -> {
            Log.d(TAG, "exit --------------");
            getActivity().finish();
        });

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            }

            return "";
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        receiver = new WifiBroadcastReceiver(this);
        mContext.registerReceiver(receiver, intentFilter);

        startScan();


    }
    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(receiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void resultAvailable() {
        Log.d(TAG, "resultAvailable ======");

    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }
    @Override
    public void startScan() {
        if (mManager.isWifiEnabled()) {
            //wifi 이용가능
            showProgress(true);

            fetchDevices();
            showWifiDevices();
            showProgress(false);
        } else {
            Log.d(TAG, "unable network");
        }
    }

    @Override
    public void showProgress(boolean visibility) {
        if (progressBar != null) {
            progressBar.setVisibility((visibility) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void fetchDevices() {
        mManager.startScan();
        wifiList = mManager.getScanResults();

        Log.d(TAG, "wifi List size check ===>" + wifiList.size());
    }

    @Override
    public void showWifiDevices() {
        adapter = new WifiAdapter(wifiList, this);
        mRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

//        showProgress(false);
    }

    //해당 ssid 클릭
    @Override
    public void onListFragmentInteraction(ScanResult device) {
        Log.d(TAG, "onList fragment interaction -->" + device.SSID);

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);

        //set up the password input
        //추후에최초 입력 후 저장.
        final EditText input = new EditText(mContext);
        input.setHint("패스워드를 입력해주세요..");
        input.setHintTextColor(getResources().getColor(R.color.white));
        input.setWidth(70);
        input.setGravity(Gravity.CENTER);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        String wifiDetail = device.SSID + " 연결하시겠습니까 ?";

        builder.setTitle("Wi-Fi 연결")
                .setMessage(wifiDetail)
                .setPositiveButton(mContext.getString(R.string.btn_connect), (dialog, which) -> {
                    connect(device, input.getText().toString());
                })
                .setIcon(R.drawable.ios)
                .show();
    }
    public void connectToWifi(String ssid, String password){
        try{
            WifiManager wifiManager = (WifiManager) super.getContext().getSystemService(android.content.Context.WIFI_SERVICE);
            WifiConfiguration wc = new WifiConfiguration();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            wc.SSID = "\""+ssid+"\"";
            wc.preSharedKey = "\""+password+"\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiManager.setWifiEnabled(true);
            int netId = wifiManager.addNetwork(wc);
            Log.d(TAG, "wcSSID1 -> " + wc.SSID);
            if (netId == -1) {
                Log.d(TAG, "wcSSID -> " + wc.SSID);
                netId = getExistingNetworkId(wc.SSID);

                Log.d(TAG, "netId -->" + netId);
            }
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int getExistingNetworkId(String SSID) {
        WifiManager wifiManager = (WifiManager) super.getContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (existingConfig.SSID.equals(SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }
    /**
     * 09-07 09:39:44.593 302-382/system_process E/WifiConfigManager: Cannot find network with networkId -1 or configKey "KT_GiGA_2G_iosystem"NONE
     UID 10114 does not have permission to update configuration "KT_GiGA_2G_iosystem"NONE
     Failed to add/update network KT_GiGA_2G_iosystem
     Looking up network with invalid networkId -1
     * @param device
     * @param password
     */
    @Override
    public void connect(ScanResult device, String password) {
        showProgress(true);

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + device.SSID + "\"";

       /* conf.wepKeys[0] = "\"" + password + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);*/

       conf.preSharedKey = "\"" + password + "\"";

       WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
       wifiManager.addNetwork(conf);

       List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
       for (WifiConfiguration i : list) {
           Log.d(TAG, "start scan ");
           if (i.SSID != null && i.SSID.equals("\"" + device.SSID + "\"")) {
               Log.d(TAG, "search finish i=>" + i.SSID + "--" + i.networkId);
               wifiManager.disconnect();
               wifiManager.enableNetwork(i.networkId, true);
               wifiManager.reconnect();

               netId = i.networkId;

               SystemClock.sleep(3000);
               PackageManager pm = getActivity().getPackageManager();
               Intent sintent = pm.getLaunchIntentForPackage(getActivity().getPackageName());
               ComponentName scm = sintent.getComponent();

               Intent mainIntent = Intent.makeRestartActivityTask(scm);
               startActivity(mainIntent);
               System.exit(0);
               break;

           }
       }



     /*   Intent intent = new Intent(mContext, ConnectingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("SSID", device.SSID);
        intent.putExtra("password", password);
        startActivity(intent);
*/

        //connectToWifi(device.SSID, password);

        ////        전화를 액세스 포인트에 연결하는 방법
        //        /**
        //         * 최초 연결 이였던 와이파이 8
        //         */
        //        WifiConfiguration wifiConfig = new WifiConfiguration();
        //        wifiConfig.SSID = String.format("\"%s\"", device.SSID);
        //        wifiConfig.preSharedKey = String.format("\"%s\"", password);
        //
        //        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        ////remember id
        //        int netId = wifiManager.addNetwork(wifiConfig);
        //        wifiManager.disconnect();
        //        wifiManager.enableNetwork(netId, true);
        //        wifiManager.reconnect();
        //Add i harness ==========================
  /*      try {

            Log.d("rht", "Item clicked, SSID " + device.SSID + " Security : " + device.capabilities);

            String networkSSID = device.SSID;
            String networkPass = "";

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;

            if (device.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (device.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";

            } else {
                Log.v("rht", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }

            WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);

            Log.v("rht", "Add result " + networkId);
            if (networkId == -1) {
                for (WifiConfiguration tmp : wifiManager.getConfiguredNetworks())
                    if (tmp.SSID.equals( "\""+device.SSID+"\""))
                    {
                        Log.d(TAG, "networkId -> " +networkId);
                        networkId = tmp.networkId;
                        wifiManager.enableNetwork(networkId, true);
                    }
            }
            else {
                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                        Log.v("rht", "WifiConfiguration SSID " + i.SSID);

                        boolean isDisconnected = wifiManager.disconnect();
                        Log.v("rht", "isDisconnected : " + isDisconnected);

                        boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                        Log.v("rht", "isEnabled : " + isEnabled);

                        boolean isReconnected = wifiManager.reconnect();
                        Log.v("rht", "isReconnected : " + isReconnected);

                        break;
                    }
                }
            }


//            getActivity().registerReceiver(progressFinish, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
     /*   //Method to connect to WIFI Network
        //        public boolean connectTo(String networkSSID, String key) {
        WifiConfiguration config = new WifiConfiguration();
        WifiInfo info = mManager.getConnectionInfo(); //get WifiInfo
        int id = info.getNetworkId(); //get id of currently connected network

        config.SSID = "\"" + device.SSID + "\"";
        if (password.isEmpty()) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //            config.preSharedKey = "\"" + password + "\"";

        int netID = mManager.addNetwork(config);

        Log.d(TAG, "netId ---> " + netID);
        //            int tempConfigId = getExistingNetworkId(config.SSID);

        int tempConfigId = mManager.addNetwork(config);
        if (tempConfigId != -1) {
            netID = tempConfigId;
        }

        mManager.disconnect();
        mManager.disableNetwork(id); //disable current network
        mManager.enableNetwork(netID, true);
        mManager.reconnect();

        if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M))
                || ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                && !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M))) {

            Log.d(TAG, "Oreo");
            final ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder;
            builder = new NetworkRequest.Builder();
            //set the transport type do WIFI
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

            manager.requestNetwork(builder.build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.d(TAG, "onAvailable ->" + network.toString());
                        manager.bindProcessToNetwork(network);
                    } else {
                        ConnectivityManager.setProcessDefaultNetwork(network);
                    }
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    manager.unregisterNetworkCallback(this);
                    PackageManager pm = getActivity().getPackageManager();
                    Intent sintent = pm.getLaunchIntentForPackage(getActivity().getPackageName());
                    ComponentName scm = sintent.getComponent();

                    Intent mainIntent = Intent.makeRestartActivityTask(scm);
                    startActivity(mainIntent);
                    System.exit(0);

                }
            });
        }*/
    }
      /*  WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", device.SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);


        int netId = mManager.addNetwork(wifiConfig);
        Log.d(TAG, "connect ====>" + netId);
        mManager.disconnect();
        mManager.enableNetwork(netId, true);
        mManager.reconnect();

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
*/
        /**
         * Cannot find network with networkId -1 or configKey "조현욱"NONE
         UID 10110 does not have permission to update configuration "조현욱"NONE
         Failed to add/update network 조현욱
         *//*
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            String ssid;

            ssid = mManager.getConnectionInfo().getSSID();
            Log.d(TAG, "progressFinish -->" + ssid);

            PackageManager pm = getActivity().getPackageManager();
            Intent sintent = pm.getLaunchIntentForPackage(getActivity().getPackageName());
            ComponentName scm = sintent.getComponent();

            Intent mainIntent = Intent.makeRestartActivityTask(scm);
            startActivity(mainIntent);
            System.exit(0);
        }*/



    private WifiContract.Presenter mPresenter;
    @Override
    public void setPresenter(WifiContract.Presenter presenter) {
        mPresenter= presenter;
    }



    private BroadcastReceiver progressFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "progressFinish receiver");
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            /**
             * Cannot find network with networkId -1 or configKey "조현욱"NONE
             UID 10110 does not have permission to update configuration "조현욱"NONE
             Failed to add/update network 조현욱
             */
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                String ssid;

                ssid = mManager.getConnectionInfo().getSSID();
//                Log.d(TAG, "wm connect ssid -->" +ssid + "want wifi ->" + device);
//                if (ssid == device) {
                    //연결하고자하는 와이파이랑 현재 연결된 와이파이가 같을 경우.
                    //Log.d(TAG,/**/ "connected wifi...");

                    //                ComponentName componentName = getPackageManager().getLaunchIntentForPackage("com.galarzaa.androidthings.samples").getComponent();
                    //                Intent sintent = IntentCompat.makeRestartActivityTask(componentName);
              /*  startActivity(sintent);
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());*/

           /*     Intent resIntent = new Intent();
                resIntent.putExtra("result", "ok");
                ConnectingActivity.this.setResult(Activity.RESULT_OK, resIntent);*/
                    //SystemClock.sleep(5000);
                Log.d(TAG, "progressFinish -->" + ssid);

                    PackageManager pm = getActivity().getPackageManager();
                    Intent sintent = pm.getLaunchIntentForPackage(getActivity().getPackageName());
                    ComponentName scm = sintent.getComponent();

                    Intent mainIntent = Intent.makeRestartActivityTask(scm);
                    startActivity(mainIntent);
                    System.exit(0);
                }
                //finish();
            }

    };

}
