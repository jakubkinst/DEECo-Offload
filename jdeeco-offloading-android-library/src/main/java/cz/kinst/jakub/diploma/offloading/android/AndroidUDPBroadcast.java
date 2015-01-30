package cz.kinst.jakub.diploma.offloading.android;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcastConfig;


/**
 * Created by jakubkinst on 12/11/14.
 */
public class AndroidUDPBroadcast extends UDPBroadcast {

    private final Context mContext;

    public AndroidUDPBroadcast(Context context) {
        mContext = context;
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public final String getMyIpAddress() {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);

    }

    @Override
    public final InetAddress getBroadcastAddress() {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        try {
            return InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected final void logDebug(String message) {
        if (UDPBroadcastConfig.DEBUG_MODE)
            Log.d(UDPBroadcastConfig.LOG_TAG, message);
    }

    @Override
    protected final void logError(String message) {
        Log.e(UDPBroadcastConfig.LOG_TAG, message);
    }

    @Override
    protected final void logInfo(String message) {
        Log.i(UDPBroadcastConfig.LOG_TAG, message);
    }

}
