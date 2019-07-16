package com.nagare.balkrishna.omkar.unmouse.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public
class BluetoothInfo
{

    @Id
    private Long bluetoothInfoId;

    private String bluetoothName;

    @Generated(hash = 329670955)
    public BluetoothInfo(Long bluetoothInfoId, String bluetoothName) {
        this.bluetoothInfoId = bluetoothInfoId;
        this.bluetoothName = bluetoothName;
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

    @Override
    public
    String toString()
    {
        return "BluetoothInfo{" +
                "bluetoothInfoId=" + bluetoothInfoId +
                ", bluetoothName='" + bluetoothName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BluetoothInfo that = (BluetoothInfo) o;

        return bluetoothName.equals(that.bluetoothName);

    }

    @Override
    public int hashCode() {
        return bluetoothName.hashCode();
    }
}
