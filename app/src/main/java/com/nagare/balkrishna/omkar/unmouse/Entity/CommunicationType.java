package com.nagare.balkrishna.omkar.unmouse.Entity;

/**
 * Created by OMKARNAGARE on 3/5/2017.
 */

public enum CommunicationType {
    BLUETOOTH("bluetooth"),
    WIFI("wifi"),
    NONE("none");

    private String val;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    private CommunicationType(String val){
        this.val = val;
    }
}
