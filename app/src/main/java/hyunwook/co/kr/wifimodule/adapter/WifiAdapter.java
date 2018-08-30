package hyunwook.co.kr.wifimodule.adapter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hyunwook.co.kr.wifimodule.R;
import hyunwook.co.kr.wifimodule.listener.OnListFragmentInteractionListener;

/**
 * Created by hyunwook on 2018-08-29.
 *
 * Wifi Scan Adapter.
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    static final String TAG = WifiAdapter.class.getSimpleName();

    private final List<ScanResult> mValues;
    private final OnListFragmentInteractionListener mListener;

    public WifiAdapter(List<ScanResult> items, OnListFragmentInteractionListener listener) {
        Log.d(TAG, "Wifi Adapter ->" + items.size());
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_wifi, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.wifiName.setText(holder.mItem.SSID);
//        holder.wifiImage.setImageResource(holder.m);

        int wifiSignalStrength = WifiManager.calculateSignalLevel(holder.mItem.level, 5);
        Log.d(TAG, "holder prequency ->" + holder.mItem.level + "Name " + holder.mItem.SSID + " signal -->"  + wifiSignalStrength);

        if (wifiSignalStrength == 4) {
            holder.wifiImage.setImageLevel(4);
        } else if (wifiSignalStrength == 3) {
            holder.wifiImage.setImageLevel(3);
        } else if (wifiSignalStrength == 2) {
            holder.wifiImage.setImageLevel(2);
        } else if (wifiSignalStrength == 1) {
            holder.wifiImage.setImageLevel(1);
        } else if (wifiSignalStrength == 0) {
            holder.wifiImage.setImageLevel(0);
        }

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView wifiName;
        private final ImageView wifiImage;

        private ScanResult mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            wifiName = view.findViewById(R.id.wifi_name);
            wifiImage = view.findViewById(R.id.wifi_icon);
            wifiImage.setImageResource(R.drawable.levellist);

        }

        @Override
        public String toString() {
            return super.toString() + " ' " + wifiName.getText() + "'";
        }

    }
}
