package com.nagare.balkrishna.omkar.unmouse.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nagare.balkrishna.omkar.unmouse.Activity.BluetoothDeviceSelectActivity;
import com.nagare.balkrishna.omkar.unmouse.R;

import java.util.List;

/**
 * Created by OMKARNAGARE on 2/25/2017.
 */

public class BluetoothDeviceListAdapter extends ArrayAdapter<BluetoothDeviceModel> {


    private List<BluetoothDeviceModel> dataSet;
    Context mContext;
    private int layout;
    private static final String TAG = "BluetoothDeviceListAdap";
    private BluetoothDeviceSelectActivity parentActivity = null;


    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
        public TextView deviceName;
    }

    public BluetoothDeviceListAdapter(List<BluetoothDeviceModel> data, Context context, int layout, BluetoothDeviceSelectActivity parentActivity) {
        super(context, layout, data);
        this.dataSet = data;
        this.mContext=context;
        this.layout = layout;
        this.parentActivity = parentActivity;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final BluetoothDeviceModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(layout, parent, false);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.deviceName.setText(dataModel.getBluetoothDeviceName());
        // Return the completed view to render on screen
        return convertView;
    }



}
