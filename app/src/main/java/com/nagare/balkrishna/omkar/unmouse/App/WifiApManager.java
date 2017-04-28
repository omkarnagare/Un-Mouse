package com.nagare.balkrishna.omkar.unmouse.App;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

public
class WifiApManager
{
    private static String password = "";
    private static String ssid     = "";

    private static final String TAG = "WifiApManager";

    private static WifiConfiguration wificonfiguration;

    /**
     * Sets password.
     *
     * @param password the password
     */
    public static
    void setPassword(String password)
    {
        WifiApManager.password = password;
    }

    /**
     * Sets ssid.
     *
     * @param ssid the ssid
     */
    public static
    void setSsid(String ssid)
    {
        WifiApManager.ssid = ssid;
    }

    /**
     * Config ap state boolean. Toggles wifi hotspot on or off
     *
     * @param context the context
     *
     * @return the boolean
     */
    public static
    boolean configApState(Context context, boolean enable)
    {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try
        {
            // if WiFi is on, turn it off
            if (enable)
            {
                wifimanager.setWifiEnabled(false);
            }

            if(ssid.equals("") || ssid.isEmpty()){
                Log.d(TAG, "configApState: Empty ssid");
                Method  wifiApConfigurationMethod = wifimanager.getClass().getMethod("getWifiApConfiguration",null);
                wificonfiguration = (WifiConfiguration)wifiApConfigurationMethod.invoke(wifimanager, null);
            }else {
                wificonfiguration = new WifiConfiguration();
                wificonfiguration.SSID = ssid;
                if (password == "") {
                    wificonfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    wificonfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                    wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                } else {
                    wificonfiguration.preSharedKey = password;
                    wificonfiguration.hiddenSSID = true;
                    wificonfiguration.status = WifiConfiguration.Status.ENABLED;
                    wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    wificonfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wificonfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wificonfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    wificonfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                    wificonfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wificonfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                }
                wifimanager.updateNetwork(wificonfiguration);
            }

            Method method = wifimanager.getClass()
                                       .getMethod("setWifiApEnabled",
                                                  WifiConfiguration.class,
                                                  boolean.class);
            method.invoke(wifimanager,
                          wificonfiguration,
                          enable);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * checks whether wifi hotspot on or off
     *
     * @param context the context
     *
     * @return the boolean
     */

    public static
    boolean isApOn(Context context)
    {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try
        {
            Method method = wifimanager.getClass()
                                       .getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored)
        {
            ignored.printStackTrace();
        }
        return false;
    }

    public static
    boolean getApState(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");

            int apState = (Integer) method.invoke(wifiManager);

            Log.d(TAG, "getApState: "+ apState);
            if (WifiManager.WIFI_STATE_ENABLED == apState % 10) {
                return true;
            }

        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
        return false;
    }
} // end of class
