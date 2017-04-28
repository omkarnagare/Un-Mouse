package com.nagare.balkrishna.omkar.unmouse.Entity;

/**
 * Created by OMKARNAGARE on 2/26/2017.
 */

public class TCPPayload {

    private ClickType clickEvent;

    private KeyType keyEvent;

    private int screenWidth;

    private int screenHeight;

    private float coordinateX;

    private float coordinateY;

    private String message;

    public ClickType getClickEvent() {
        return clickEvent;
    }

    public void setClickEvent(ClickType clickEvent) {
        this.clickEvent = clickEvent;
    }

    public float getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(float coordinateX) {
        this.coordinateX = coordinateX;
    }

    public float getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(float coordinateY) {
        this.coordinateY = coordinateY;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public KeyType getKeyEvent() {
        return keyEvent;
    }

    public void setKeyEvent(KeyType keyEvent) {
        this.keyEvent = keyEvent;
    }

    public enum ClickType{
        SINGLE_CLICK(10),
        DOUBLE_CLICK(20),
        MIDDLE_CLICK(30),
        MIDDLE_CLICK_LONG(40),
        RIGHT_CLICK(50),
        NONE(30);

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        ClickType(int value){
            this.value=value;
        }

    }

    public enum KeyType{
        CTRL(10),
        CTRL_LONG(20),
        SPACE(30),
        SHIFT(40),
        SHIFT_LONG(50),
        TAB(60),
        CAPS_LOCK(70),
        ENTER(80),
        ESC(90),
        DELETE(100),
        INSERT(110),
        BACKSPACE(120),
        ALT(130),
        ALT_LONG(140),
        WINDOW(150),
        HOME(160),
        END(170),
        ARROW_UP(180),
        ARROW_DOWN(190),
        ARROW_LEFT(200),
        ARROW_RIGHT(210),
        START_PPT(900),
        COPY(910),
        CUT(920),
        PASTE(930),
        NONE(0);

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        KeyType(int value){
            this.value=value;
        }

    }

}
