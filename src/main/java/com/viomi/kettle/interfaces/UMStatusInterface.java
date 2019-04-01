package com.viomi.kettle.interfaces;

/**
 * Created by young2 on 2016/6/4.
 */
public interface UMStatusInterface {

    void onStatusDataReceive(byte[] data);
    void isOnlineChange(boolean isOnline);

}
