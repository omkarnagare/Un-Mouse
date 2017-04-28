package com.nagare.balkrishna.omkar.unmouse.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public
class BluetoothInfo
{

    @Id
    private Long bluetoothInfoId;

    private String bluetoothName;

    @Index(unique = true)
    private String bluetoothAddress;

    @Generated(hash = 288659075)
    public BluetoothInfo(Long bluetoothInfoId, String bluetoothName,
            String bluetoothAddress) {
        this.bluetoothInfoId = bluetoothInfoId;
        this.bluetoothName = bluetoothName;
        this.bluetoothAddress = bluetoothAddress;
    }

    @Generated(hash = 1947195037)
    public BluetoothInfo() {
    }

    public
    Long getBluetoothInfoId()
    {
        return bluetoothInfoId;
    }

    public
    void setBluetoothInfoId(Long bluetoothInfoId)
    {
        this.bluetoothInfoId = bluetoothInfoId;
    }

    public
    String getBluetoothName()
    {
        return bluetoothName;
    }

    public
    void setBluetoothName(String bluetoothName)
    {
        this.bluetoothName = bluetoothName;
    }

    public
    String getBluetoothAddress()
    {
        return bluetoothAddress;
    }

    public
    void setBluetoothAddress(String bluetoothAddress)
    {
        this.bluetoothAddress = bluetoothAddress;
    }

    @Override
    public
    String toString()
    {
        return "BluetoothInfo{" +
                "bluetoothInfoId=" + bluetoothInfoId +
                ", bluetoothName='" + bluetoothName + '\'' +
                ", bluetoothAddress='" + bluetoothAddress + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BluetoothInfo that = (BluetoothInfo) o;

        return bluetoothAddress.equals(that.bluetoothAddress);

    }

    @Override
    public int hashCode() {
        return bluetoothAddress.hashCode();
    }
}
