package hyunwook.co.kr.wifimodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 18-09-04
 * Wifi 연결 중일 때 액티비티
 */
public class ConnectingActivity extends Activity {

    static final String TAG = ConnectingActivity.class.getSimpleName();

    protected TextView mCount;
    private CountDownTimer countDownTimer;

    private final long start = 30 * 1000; //2분
    private final long interval = 1 * 1000;
    WifiManager mManager;
    String device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connecting_dialog);
        Log.d(TAG, "connecting Receiver onCreate");

        mManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Intent sIntent = getIntent();
        device = sIntent.getStringExtra("SSID");
        String password = sIntent.getStringExtra("password");

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", device);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);

        Log.d(TAG, "device and password ->" + device + "//"+ password);

        int netId = mManager.addNetwork(wifiConfig);

        mManager.disconnect();
        mManager.enableNetwork(netId, true);
        mManager.reconnect();

        mCount = (TextView) this.findViewById(R.id.countdown);
        countDownTimer = new MyCountDownTimer(start, interval);

        mCount.setText(mCount.getText() + String.valueOf(start / 1000));
        countDownTimer.start();

        registerReceiver(progressFinish, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }


        @Override
        public void onFinish() {
            mCount.setText("0");
            View view = getLayoutInflater().inflate(R.layout.connecting_error_dialog, null);
            TextView txtTitle = (TextView) view.findViewById(R.id.title);
            txtTitle.setTextSize(40);
            txtTitle.setTextColor(Color.RED);
            txtTitle.setText("무선 연결 실패");

            TextView message = (TextView) view.findViewById(R.id.message);
            message.setTextSize(30);
            message.setText("WI-FI를 연결 할 수 없습니다. \n\n 공유기를 확인하세요.");

            AlertDialog.Builder builder = new AlertDialog.Builder(ConnectingActivity.this);
            builder.setView(view)
                    .setCancelable(false)
                    .setPositiveButton("확 인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    moveTaskToBack(true);
                                    finish();
//                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            });
            final AlertDialog alertDialog = builder.create();
//            final AlertDialog alert = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                    btnPositive.setTextSize(25);
                }
            });
            alertDialog.show();

        }

        @Override
        public void onTick(long millisUntilFinished) {
            mCount.setText("" + millisUntilFinished / 1000);
        }
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
                Log.d(TAG, "wm connect ssid -->" +ssid + "want wifi ->" + device);
                if (ssid == device) {
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

                    PackageManager pm = getPackageManager();
                    Intent sintent = pm.getLaunchIntentForPackage(getPackageName());
                    ComponentName scm = sintent.getComponent();

                    Intent mainIntent = Intent.makeRestartActivityTask(scm);
                    startActivity(mainIntent);
                    System.exit(0);
                }
                //finish();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(progressFinish);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}

