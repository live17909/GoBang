package com.xuf.www.gobang.view.dialog;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.peak.salut.SalutDevice;
import com.xuf.www.gobang.R;
import com.xuf.www.gobang.eventbus.BusProvider;
import com.xuf.www.gobang.eventbus.ConnectPeerEvent;
import com.xuf.www.gobang.eventbus.WifiCancelPeerEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenov0 on 2015/12/27.
 */
public class PeersDialog extends BaseDialog {

    public static final String TAG = "PeersDialog";

    private ListView mListView;
    private DeviceAdapter mAdapter;
    private List<SalutDevice> mSalutDevices = new ArrayList<>();
    private List<BluetoothDevice> mBlueToothDevices = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_peer_list, container, false);

        mListView = (ListView)view.findViewById(R.id.lv_peers);
        mAdapter = new DeviceAdapter();
        mListView.setAdapter(mAdapter);

        ButtonRectangle cancel = (ButtonRectangle)view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new WifiCancelPeerEvent());
            }
        });

        return view;
    }

    public void updatePeers(List<SalutDevice> data){
        mAdapter.setSalutDevices(data);
    }

    public void updateBlueToothPeers(List<BluetoothDevice> bluetoothDevices){
        mAdapter.setBlueToothDevices(bluetoothDevices);
    }

    private class DeviceAdapter extends BaseAdapter{
        private boolean mIsSalutDevice;

        public void setSalutDevices(List<SalutDevice> data){
            mSalutDevices.clear();
            mSalutDevices.addAll(data);
            mIsSalutDevice = true;
            notifyDataSetChanged();
        }

        public void setBlueToothDevices(List<BluetoothDevice> blueToothDevices){
            mBlueToothDevices.clear();
            mBlueToothDevices.addAll(blueToothDevices);
            mIsSalutDevice = false;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mIsSalutDevice ? mSalutDevices.size() : mBlueToothDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mIsSalutDevice ? mSalutDevices.get(position) : mBlueToothDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_device, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            String device = mIsSalutDevice ? mSalutDevices.get(position).deviceName : mBlueToothDevices.get(position).getName();
            holder = (ViewHolder)convertView.getTag();
            holder.device.setText(device);
            holder.device.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsSalutDevice){
                        BusProvider.getInstance().post(new ConnectPeerEvent(mSalutDevices.get(position), null));
                    } else {
                        BusProvider.getInstance().post(new ConnectPeerEvent(null, mBlueToothDevices.get(position)));
                    }
                }
            });

            return convertView;
        }

        private class ViewHolder{
            public TextView device;

            public ViewHolder(View view){
                device = (TextView)view.findViewById(R.id.tv_device);
            }
        }
    }
}
