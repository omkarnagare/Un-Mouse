package com.nagare.balkrishna.omkar.unmouse.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        public TextView deviceAddress;
        public Button deleteDevice;
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

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            if (layout == R.layout.bluetooth_device){
                convertView = inflater.inflate(R.layout.bluetooth_device, parent, false);
            }else{
                convertView = inflater.inflate(R.layout.bluetooth_saved_device, parent, false);
                viewHolder.deleteDevice = (Button) convertView.findViewById(R.id.delete_entry);

                viewHolder.deleteDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final BluetoothDeviceModel bluetoothDeviceModel = dataSet.get(position);

                        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(mContext);
                        mAlertDialogBuilder.setTitle("Are your sure?");
                        mAlertDialogBuilder.setMessage("Should we proceed deleting saved device " +
                                "with following details \nName :"+ bluetoothDeviceModel.getBluetoothDeviceName()+
                                "\nAddress :"+ bluetoothDeviceModel.getBluetoothDeviceAddress());
                        mAlertDialogBuilder.setCancelable(false);

                        mAlertDialogBuilder.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dataSet.remove(position);
                                        notifyDataSetChanged();
                                        parentActivity.deleteDeviceFromDatabase(bluetoothDeviceModel);

                                        dialog.cancel();
                                    }
                                });

                        mAlertDialogBuilder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        mAlertDialogBuilder.create().show();

                    }
                });

            }
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.deviceName.setText(dataModel.getBluetoothDeviceName());
        viewHolder.deviceAddress.setText(dataModel.getBluetoothDeviceAddress());
        // Return the completed view to render on screen
        return convertView;
    }



}
