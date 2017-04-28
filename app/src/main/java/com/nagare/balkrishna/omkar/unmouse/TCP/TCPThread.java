package com.nagare.balkrishna.omkar.unmouse.TCP;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.nagare.balkrishna.omkar.unmouse.Activity.TCPActivity;
import com.nagare.balkrishna.omkar.unmouse.Entity.Base64;
import com.nagare.balkrishna.omkar.unmouse.Entity.TCPPayload;
import com.nagare.balkrishna.omkar.unmouse.R;
import com.nagare.balkrishna.omkar.unmouse.View.TouchImageView;

import java.io.BufferedReader;
import java.io.OutputStream;

public
class TCPThread extends Thread
{
    private OutputStream   outStream   = null;
    private BufferedReader brReader = null;

    private TouchImageView mImageView  = null;
    private Activity currentActivity = null;

    private String messageStr = null;
    Gson mGson = new Gson();
    private boolean isRunning = false;
    private int mWidth;
    private int mHeight;

    private boolean isExceptionOccured = false;
    private boolean isSpinnerDissmissed = false;

    public void stopThread(){
        isRunning = false;
        outStream = null;
        brReader = null;
    }

    public TCPThread(BufferedReader brReader, OutputStream outStream, Activity currentActivity){

        this.brReader = brReader;
        this.outStream = outStream;
        this.isRunning = true;
        this.currentActivity = currentActivity;
        mImageView = (TouchImageView) currentActivity.findViewById(R.id.pc_screen);
        mWidth = mImageView.getWidth();
        mHeight = mImageView.getHeight();

    }

    public
    boolean isExceptionOccured()
    {
        return isExceptionOccured;
    }

    public
    void setExceptionOccured(boolean exceptionOccured)
    {
        isExceptionOccured = exceptionOccured;
    }

    @Override
    public
    void run()
    {
        while (isRunning){

            TCPPayload tcpPayload = new TCPPayload();
            tcpPayload.setClickEvent(mImageView.getClickType());
            tcpPayload.setCoordinateX(mImageView.getClickPositionX());
            tcpPayload.setCoordinateY(mImageView.getClickPositionY());
            tcpPayload.setScreenHeight(mHeight);
            tcpPayload.setScreenWidth(mWidth);

            if(currentActivity instanceof TCPActivity) {
                messageStr = ((TCPActivity) currentActivity).getMessageString();
                ((TCPActivity) currentActivity).setMessageString(null);

                tcpPayload.setKeyEvent(((TCPActivity) currentActivity).getmKeyEvent());
                ((TCPActivity) currentActivity).setmKeyEvent(TCPPayload.KeyType.NONE);
            }
            if (messageStr != null){
                tcpPayload.setMessage(messageStr);
            }else{
                tcpPayload.setMessage(null);
            }
            messageStr = null;

            mImageView.setClickType(TCPPayload.ClickType.NONE);

            String message = mGson.toJson(tcpPayload) + "\n";

            byte[] msgBuffer = message.getBytes();
            try
            {
                outStream.write(msgBuffer);
                outStream.flush();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                isExceptionOccured = true;
                isRunning = false;
            }

            StringBuilder received = new StringBuilder();
            String str = null;

            try
            {
                while (!(str = brReader.readLine()).equals("complete")) {
                    received.append(str + "\r\n");
                }
                if(!isSpinnerDissmissed) {
                    if (currentActivity instanceof TCPActivity) {
                        ((TCPActivity) currentActivity).dissmissSpinner();
                        isSpinnerDissmissed = true;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                isExceptionOccured = true;
                isRunning = false;
            }

            if (received.length() != 0 && isRunning) {
                byte[]       image  = Base64.getMimeDecoder().decode(received.substring(0, received.length() - 2));
                final Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                received = null;
                currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, mWidth,
                                                                            mHeight, false));
                    }
                });
            }

        }
    }
}
