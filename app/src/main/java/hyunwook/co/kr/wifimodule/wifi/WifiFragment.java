package hyunwook.co.kr.wifimodule.wifi;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import hyunwook.co.kr.wifimodule.ConnectingReceiver;
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
              getActivity().finish();
            //}
        });
        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        int netType = (activeNetwork == null) ? -1 : activeNetwork.getType();
        String ssidState;
        if (netType == cm.TYPE_WIFI) {
            ssidState = wm.getConnectionInfo().getSSID();
            Log.d(TAG, "ssidstate ->" + ssidState.toString());
            TextView titleView = view.findViewById(R.id.title_text);
            titleView.setText(ssidState.toString());
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

    @Override
    public void connect(ScanResult device, String password) {
        showProgress(true);
    /*    Intent intent = new Intent(mContext, ConnectingReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);*/

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", device.SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);


        int netId = mManager.addNetwork(wifiConfig);
        Log.d(TAG, "connect ====>" + netId);
        mManager.disconnect();
        mManager.enableNetwork(netId, true);
        mManager.reconnect();
    }

    private WifiContract.Presenter mPresenter;
    @Override
    public void setPresenter(WifiContract.Presenter presenter) {
        mPresenter= presenter;
    }





}
