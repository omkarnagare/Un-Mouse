package com.nagare.balkrishna.omkar.unmouse.View;

/**
 * Created by OMKARNAGARE on 2/25/2017.
 */

public class BluetoothDeviceModel {
    private String bluetoothDeviceName;

    private String bluetoothDeviceAddress;

    public String getBluetoothDeviceAddress() {
        return bluetoothDeviceAddress;
    }

    public void setBluetoothDeviceAddress(String bluetoothDeviceAddress) {
        this.bluetoothDeviceAddress = bluetoothDeviceAddress;
    }

    public String getBluetoothDeviceName() {
        return bluetoothDeviceName;
    }

    public void setBluetoothDeviceName(String bluetoothDeviceName) {
        this.bluetoothDeviceName = bluetoothDeviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BluetoothDeviceModel that = (BluetoothDeviceModel) o;

        return bluetoothDeviceAddress != null ? bluetoothDeviceAddress.equals(that.bluetoothDeviceAddress) : that.bluetoothDeviceAddress == null;

    }

    @Override
    public int hashCode() {
        return bluetoothDeviceAddress != null ? bluetoothDeviceAddress.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BluetoothDeviceModel{" +
                "bluetoothDeviceAddress='" + bluetoothDeviceAddress + '\'' +
                ", bluetoothDeviceName='" + bluetoothDeviceName + '\'' +
                '}';
    }
}
