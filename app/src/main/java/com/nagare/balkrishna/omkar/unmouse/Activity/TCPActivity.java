package com.nagare.balkrishna.omkar.unmouse.Activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.nagare.balkrishna.omkar.unmouse.Entity.CommunicationType;
import com.nagare.balkrishna.omkar.unmouse.Entity.TCPPayload;
import com.nagare.balkrishna.omkar.unmouse.R;
import com.nagare.balkrishna.omkar.unmouse.TCP.TCPThread;
import com.nagare.balkrishna.omkar.unmouse.View.TouchImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class TCPActivity extends AppCompatActivity {

    private static final String TAG = "TCPActivity";
    private static final String DEVICE_ADDRESS = "bluetoothDeviceAddress";
    private static final String COMMUNICATION_TYPE = "communicationType";
    private static final int WIFI_SOCKET_PORT = 10000;

    private CommunicationType currentCommunicationType = CommunicationType.NONE;

    // Well known SPP UUID
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String mBluetoothDeviceAddress = null;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private ServerSocket wifiServerSocket = null;
    private Socket clientWifiSocket = null;
    private boolean clientConnectedSuccessfully = false;

    private int mWidth;
    private int mHeight;
    private TouchImageView mImageView;
    private Button mExtraActionsButton;
    private View guideline = null;

    private boolean isPopUpForExtraActionsExpanded = false;
    private boolean isPopUpExpanded = false;
    private String messageString = null;
    private TCPPayload.KeyType mKeyEvent = TCPPayload.KeyType.NONE;

    private TCPThread tcpThread = null;
    private PopupWindow popupExtraActionsWindow = null;
    private ProgressDialog progressDialog = null;
    private boolean isSpinnerShown = false;
    private ImageView forgroundImageView = null;

    private boolean doubleBackToExitPressedOnce = false;
    private int backPressedCountForKeyboard = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_tcp);

        setUpUI();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String type = extras.getString(COMMUNICATION_TYPE);
            if (type.equals(CommunicationType.BLUETOOTH.getVal())) {

                mBluetoothDeviceAddress = extras.getString(DEVICE_ADDRESS);
                startBluetoothTCPThreads();

            } else if (type.equals(CommunicationType.WIFI.getVal())) {
                startWifiTCPThreads();
            }
        }

    }

    private void setUpUI() {

        guideline = (View) findViewById(R.id.hgl_tcp_bluetooth);
        mImageView = (TouchImageView) findViewById(R.id.pc_screen);
        forgroundImageView = (ImageView) findViewById(R.id.foreground_layer);
        mExtraActionsButton = (Button) findViewById(R.id.extra_actions_button);

        mExtraActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPopUpForExtraActionsExpanded) {
                    showPopupForExtraActions();
                    isPopUpForExtraActionsExpanded = true;
                } else {
                    hidePopupForExtraActions();
                    isPopUpForExtraActionsExpanded = false;
                }
            }
        });

        progressDialog = new ProgressDialog(TCPActivity.this);
        progressDialog.setTitle("Wait!!");
        progressDialog.setMessage("connection is being established...");
        progressDialog.setCancelable(false);

    }

    public TCPPayload.KeyType getmKeyEvent() {
        return mKeyEvent;
    }

    public void setmKeyEvent(TCPPayload.KeyType mKeyEvent) {
        this.mKeyEvent = mKeyEvent;
    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    private void hidePopupForExtraActions() {

        if (popupExtraActionsWindow != null) {
            popupExtraActionsWindow.dismiss();
            mExtraActionsButton.startAnimation(AnimationUtils.loadAnimation(TCPActivity.this, R.anim.rotate_anticlockwise));
            mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_add_circle));
        }

    }

    private void showPopupForExtraActions() {
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupExtraActionsView = layoutInflater.inflate(R.layout.popup_extra_actions, null);
        popupExtraActionsView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        popupExtraActionsWindow = new PopupWindow(
                popupExtraActionsView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final Button keyboardButton = (Button) popupExtraActionsView.findViewById(R.id.keyboard_button);
        final Button mouseButton = (Button) popupExtraActionsView.findViewById(R.id.mouse_button);
        final Button pptButton = (Button) popupExtraActionsView.findViewById(R.id.presentation_button);


        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showKeyboardPopup(keyboardButton, mouseButton, pptButton);

            }
        });

        mouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMousePopup(keyboardButton, mouseButton, pptButton);

            }
        });

        pptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPPTPopup(keyboardButton, mouseButton, pptButton);

            }
        });

        mExtraActionsButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise));
        mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_cancel));
        int relativeWidth = (popupExtraActionsView.getMeasuredWidth() / -2) - (mExtraActionsButton.getMeasuredWidth() / -2);
        int relativeHeight = -popupExtraActionsView.getMeasuredHeight() - mExtraActionsButton.getMeasuredHeight();
        popupExtraActionsWindow.showAsDropDown(mExtraActionsButton, relativeWidth, relativeHeight);
    }

    private void showPPTPopup(final Button keyboardButton, final Button mouseButton, final Button pptButton) {

        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(this);
        mAlertDialogBuilder.setTitle("PPT mode");
        mAlertDialogBuilder.setMessage("Make sure your presentation is open in current window of your PC.");
        mAlertDialogBuilder.setCancelable(false);

        mAlertDialogBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        if (!isPopUpExpanded) {
                            LayoutInflater layoutInflater =
                                    (LayoutInflater) getBaseContext()
                                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupPPTView = layoutInflater.inflate(R.layout.popup_ppt, null);
                            popupPPTView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                            final PopupWindow popupPPTWindow = new PopupWindow(
                                    popupPPTView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            setupPPTPopupUI(popupPPTView, popupPPTWindow, mouseButton, keyboardButton);

                            int relativeWidth = (popupPPTView.getMeasuredWidth() / -2) - (mExtraActionsButton.getMeasuredWidth() / -2);
                            int relativeHeight = -popupPPTView.getMeasuredHeight() - pptButton.getMeasuredHeight() - mExtraActionsButton.getMeasuredHeight();
                            popupPPTWindow.showAsDropDown(mExtraActionsButton, relativeWidth, relativeHeight);

                            keyboardButton.setEnabled(false);
                            mouseButton.setEnabled(false);
                            mExtraActionsButton.setEnabled(false);
                            mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_cancel_disabled));
                            isPopUpExpanded = true;
                        }


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

    private void setupPPTPopupUI(View popupPPTView, final PopupWindow popupPPTWindow, final Button mouseButton, final Button keyboardButton) {

        final Button startPPTButton = (Button) popupPPTView.findViewById(R.id.start_stop_ppt_button);
        Button exitPPTButton = (Button) popupPPTView.findViewById(R.id.exit_ppt_button);
        Button forwardPPTButton = (Button) popupPPTView.findViewById(R.id.forward_ppt_button);
        Button backwardPPTButton = (Button) popupPPTView.findViewById(R.id.backward_ppt_button);

        startPPTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.START_PPT);
                startPPTButton.setBackground(getResources().getDrawable(R.drawable.ic_action_pause_circle_filled));
            }
        });

        exitPPTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ESC);
                popupPPTWindow.dismiss();
                isPopUpExpanded = false;
                mouseButton.setEnabled(true);
                keyboardButton.setEnabled(true);
                mExtraActionsButton.setEnabled(true);
                mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_cancel));

            }
        });

        forwardPPTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ARROW_RIGHT);
            }
        });

        backwardPPTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ARROW_LEFT);
            }
        });

    }

    private void showMousePopup(Button keyboardButton, Button mouseButton, Button pptButton) {

        if (!isPopUpExpanded) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupMouseActionsView = layoutInflater.inflate(R.layout.popup_mouse_actions, null);
            popupMouseActionsView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            final PopupWindow popupMouseActionsWindow = new PopupWindow(
                    popupMouseActionsView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            setupMouseActionsPopupUI(popupMouseActionsView, popupMouseActionsWindow, pptButton, keyboardButton);

            int relativeWidth = (popupMouseActionsView.getMeasuredWidth() / -2) - (mExtraActionsButton.getMeasuredWidth() / -2);
            int relativeHeight = -popupMouseActionsView.getMeasuredHeight() - mouseButton.getMeasuredHeight() - mExtraActionsButton.getMeasuredHeight();
            popupMouseActionsWindow.showAsDropDown(mExtraActionsButton, relativeWidth, relativeHeight);

            keyboardButton.setEnabled(false);
            pptButton.setEnabled(false);
            mExtraActionsButton.setEnabled(false);
            mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_cancel_disabled));
            isPopUpExpanded = true;
        }

    }

    private void setupMouseActionsPopupUI(View popupMouseActionsView, final PopupWindow popupMouseActionsWindow, final Button pptButton, final Button keyboardButton) {

        Button mouseRightButton = (Button) popupMouseActionsView.findViewById(R.id.mouse_right_button);
        final Button mouseMiddleButton = (Button) popupMouseActionsView.findViewById(R.id.mouse_middle_button);
        Button mouseCloseButton = (Button) popupMouseActionsView.findViewById(R.id.mouse_close_button);

        mouseRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setClickType(TCPPayload.ClickType.RIGHT_CLICK);
            }
        });

        mouseMiddleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setClickType(TCPPayload.ClickType.MIDDLE_CLICK);
                mouseMiddleButton.setBackground(getResources().getDrawable(R.drawable.ic_action_mouse_middle_click));
            }
        });

        mouseMiddleButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mImageView.setClickType(TCPPayload.ClickType.MIDDLE_CLICK_LONG);
                mouseMiddleButton.setBackground(getResources().getDrawable(R.drawable.ic_action_mouse_middle_click_hold));
                Toast.makeText(TCPActivity.this, "scroll direction switched", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mouseCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupMouseActionsWindow.dismiss();
                isPopUpExpanded = false;
                pptButton.setEnabled(true);
                keyboardButton.setEnabled(true);
                mExtraActionsButton.setEnabled(true);
                mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_cancel));

            }
        });

    }

    private void showKeyboardPopup(Button keyboardButton, Button mouseButton, Button pptButton) {

        if (!isPopUpExpanded) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupSendMessageView = layoutInflater.inflate(R.layout.popup_send_message, null);
            popupSendMessageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            final PopupWindow popupSendMessageWindow = new PopupWindow(
                    popupSendMessageView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            setupSendMessagePopupUI(popupSendMessageView, popupSendMessageWindow, mouseButton, pptButton);

            int relativeWidth = (popupSendMessageView.getMeasuredWidth() / -2) - (mExtraActionsButton.getMeasuredWidth() / -2);
            int relativeHeight = -popupSendMessageView.getMeasuredHeight() - keyboardButton.getMeasuredHeight() - mExtraActionsButton.getMeasuredHeight();
            popupSendMessageWindow.setFocusable(true);
            popupSendMessageWindow.getContentView().setFocusableInTouchMode(true);
            popupSendMessageWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        Log.d(TAG, "onKey: back pressed");
                        if (backPressedCountForKeyboard >= 2) {
                            popupSendMessageWindow.dismiss();
                            closeResources();
                            finish();
                            return true;
                        }

                        backPressedCountForKeyboard++;
                        if (backPressedCountForKeyboard % 2 == 1) {
                            Toast.makeText(TCPActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();
                        }

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                backPressedCountForKeyboard = 0;
                            }
                        }, 2000);

                        return true;
                    }
                    return false;
                }
            });
            popupSendMessageWindow.update();
            popupSendMessageWindow.showAsDropDown(mExtraActionsButton, relativeWidth, relativeHeight);

            pptButton.setEnabled(false);
            mouseButton.setEnabled(false);
            mExtraActionsButton.setEnabled(false);
            mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_cancel_disabled));
            forgroundImageView.setVisibility(View.VISIBLE);
            isPopUpExpanded = true;
        }

    }

    private void setupSendMessagePopupUI(View popupSendMessageView, final PopupWindow popupSendMessageWindow, final Button mouseButton, final Button pptButton) {

        Button closeButton = (Button) popupSendMessageView.findViewById(R.id.close_popup);
        Button sendButton = (Button) popupSendMessageView.findViewById(R.id.send_message);
        Button escapeButton = (Button) popupSendMessageView.findViewById(R.id.escape_button);
        Button tabButton = (Button) popupSendMessageView.findViewById(R.id.tab_button);
        Button insertButton = (Button) popupSendMessageView.findViewById(R.id.insert_button);
        Button deleteButton = (Button) popupSendMessageView.findViewById(R.id.delete_button);
        Button homeButton = (Button) popupSendMessageView.findViewById(R.id.home_button);
        Button endButton = (Button) popupSendMessageView.findViewById(R.id.end_button);
        Button backspaceButton = (Button) popupSendMessageView.findViewById(R.id.backspace_button);
        Button capslockButton = (Button) popupSendMessageView.findViewById(R.id.capslock_button);
        final Button shiftButton = (Button) popupSendMessageView.findViewById(R.id.shift_button);
        Button enterButton = (Button) popupSendMessageView.findViewById(R.id.enter_button);
        Button windowsButton = (Button) popupSendMessageView.findViewById(R.id.windows_button);
        final Button controlButton = (Button) popupSendMessageView.findViewById(R.id.control_button);
        final Button altButton = (Button) popupSendMessageView.findViewById(R.id.alt_button);
        Button spacebarButton = (Button) popupSendMessageView.findViewById(R.id.spacebar_button);
        Button cutButton = (Button) popupSendMessageView.findViewById(R.id.cut_button);
        Button copyButton = (Button) popupSendMessageView.findViewById(R.id.copy_button);
        Button pasteButton = (Button) popupSendMessageView.findViewById(R.id.paste_button);
        Button arrowUpButton = (Button) popupSendMessageView.findViewById(R.id.arrow_up_button);
        Button arrowLeftButton = (Button) popupSendMessageView.findViewById(R.id.arrow_left_button);
        Button arrowRightButton = (Button) popupSendMessageView.findViewById(R.id.arrow_right_button);
        Button arrowDownButton = (Button) popupSendMessageView.findViewById(R.id.arrow_down_button);
        final EditText messageText = (EditText) popupSendMessageView.findViewById(R.id.message_string);

        sendButton.setPaintFlags(sendButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        closeButton.setPaintFlags(closeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = messageText.getText().toString();

                if (text.isEmpty()) {
                    Toast.makeText(TCPActivity.this, "Enter some text first", Toast.LENGTH_SHORT).show();
                } else {
                    TCPActivity.this.setMessageString(text);
                    messageText.setText("");
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSendMessageWindow.dismiss();
                forgroundImageView.setVisibility(View.GONE);
                isPopUpExpanded = false;
                mouseButton.setEnabled(true);
                pptButton.setEnabled(true);
                mExtraActionsButton.setEnabled(true);
                mExtraActionsButton.setBackground(getResources().getDrawable(R.drawable.ic_action_cancel));
            }
        });

        escapeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ESC);
            }
        });

        tabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.TAB);
            }
        });

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.INSERT);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.DELETE);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.HOME);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.END);
            }
        });

        backspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.BACKSPACE);
            }
        });

        capslockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.CAPS_LOCK);
            }
        });

        shiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.SHIFT);
                shiftButton.setTextColor(Color.BLACK);
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ENTER);
            }
        });

        windowsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.WINDOW);
            }
        });

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.CTRL);
                controlButton.setTextColor(Color.BLACK);
            }
        });

        altButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                altButton.setTextColor(Color.BLACK);
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ALT);
            }
        });

        spacebarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.SPACE);
            }
        });

        cutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.CUT);
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.COPY);
            }
        });

        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.PASTE);
            }
        });

        arrowUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ARROW_UP);
            }
        });

        arrowLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ARROW_LEFT);
            }
        });

        arrowRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ARROW_RIGHT);
            }
        });

        arrowDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ARROW_DOWN);
            }
        });

        controlButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.CTRL_LONG);
                controlButton.setTextColor(Color.BLUE);
                Toast.makeText(TCPActivity.this, "Click again to release long press", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        altButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.ALT_LONG);
                altButton.setTextColor(Color.BLUE);
                Toast.makeText(TCPActivity.this, "Click again to release long press", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        shiftButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TCPActivity.this.setmKeyEvent(TCPPayload.KeyType.SHIFT_LONG);
                shiftButton.setTextColor(Color.BLUE);
                Toast.makeText(TCPActivity.this, "Click again to release long press", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void startBluetoothTCPThreads() {

        Thread startBluetoothConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                btAdapter = BluetoothAdapter.getDefaultAdapter();

                Log.d(TAG, "Attempting client connect...");

                // Set up a pointer to the remote node using it's address.
                BluetoothDevice device = btAdapter.getRemoteDevice(mBluetoothDeviceAddress);

                // Two things are needed to make a connection:
                //   A MAC address, which we got above.
                //   A Service ID or UUID.  In this case we are using the
                //     UUID for SPP.
                try {
                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Discovery is resource intensive.  Make sure it isn't going on
                // when you attempt to connect and pass your message.
                btAdapter.cancelDiscovery();

                // Establish the connection.  This will block until it connects.
                try {
                    btSocket.connect();
                    Log.d(TAG, "\n...Connection established and data link opened...");
                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }

                // Create a data stream so we can talk to server.
                Log.d(TAG, "\n...Sending message to server...");

                try {
                    OutputStream outStream = btSocket.getOutputStream();
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));

                    while (mWidth == 0 && mHeight == 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    tcpThread = new TCPThread(bReader, outStream, TCPActivity.this);
                    tcpThread.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        startBluetoothConnectionThread.start();

        startCheckTCPThreadStatusThread();

    }


    private void startWifiTCPThreads() {

        Log.d(TAG, "startWifiTCPThreads: started");
        Thread startWifiConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    wifiServerSocket = new ServerSocket(WIFI_SOCKET_PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (wifiServerSocket != null) {
                    try {

                        Thread checkConnectionThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(!clientConnectedSuccessfully){
                                    try {
                                        wifiServerSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        checkConnectionThread.start();
                        clientWifiSocket = wifiServerSocket.accept();
                        clientConnectedSuccessfully = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            wifiServerSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }else{
                    Log.d(TAG, "run: wifiServerSocket is null");
                }

                if(clientWifiSocket != null){

                    try {
                        OutputStream outStream = clientWifiSocket.getOutputStream();
                        BufferedReader bReader = new BufferedReader(new InputStreamReader(clientWifiSocket.getInputStream()));

                        while (mWidth == 0 && mHeight == 0) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        tcpThread = new TCPThread(bReader, outStream, TCPActivity.this);
                        tcpThread.start();
                        Log.d(TAG, "run: tcpthread started");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    Log.d(TAG, "run: clientWifiSocket is null");
                    closeApp("Connection could not be established!", "Make sure PC is connected to hotpot");
                }

            }
        });

        startWifiConnectionThread.start();

        startCheckTCPThreadStatusThread();
    }

    private void closeApp(String titleString, String messageString) {

        if(tcpThread != null) {
            tcpThread.stopThread();
            try {
                tcpThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tcpThread = null;
        }

        if(btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(wifiServerSocket != null){
            try {
                wifiServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(clientWifiSocket != null){
            try {
                clientWifiSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(TCPActivity.this);
        mAlertDialogBuilder.setTitle(titleString);
        mAlertDialogBuilder.setMessage(messageString);
        mAlertDialogBuilder.setCancelable(false);

        mAlertDialogBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAlertDialogBuilder.create().show();
            }
        });

    }

    private void startCheckTCPThreadStatusThread() {

        Thread checkTCPThreadStatusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (tcpThread != null) {
                        if (tcpThread.isExceptionOccured()) {

                            closeApp("Connection Interrupted", "Please check the app " +
                                    "running on your PC. Make sure it is running properly.");

                        } else {

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        });
        checkTCPThreadStatusThread.start();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mWidth = mImageView.getWidth();
        mHeight = mImageView.getHeight();
//        Log.d(TAG, "onWindowFocusChanged: width = " + mWidth + ", height = " + mHeight);
        if (!isSpinnerShown && progressDialog != null) {
            progressDialog.show();
            isSpinnerShown = true;
        }
    }

    public void dissmissSpinner() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            closeResources();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void closeResources() {

        if (popupExtraActionsWindow != null) {
            if (popupExtraActionsWindow.isShowing()) {
                popupExtraActionsWindow.dismiss();
            }
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (tcpThread != null) {
            tcpThread.stopThread();
            try {
                tcpThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tcpThread = null;
        }

        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (btAdapter != null) {
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
            }
            btAdapter = null;
        }

        if(wifiServerSocket != null){
            try {
                wifiServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(clientWifiSocket != null){
            try {
                clientWifiSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
