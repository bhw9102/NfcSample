package com.nolgong.nfcsample.nfc;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class NfcHandler implements Handler.Callback {
    public final Messenger msgPostBox = new Messenger(new Handler(this));
    private Messenger msgToActivity = null;


    public void setMsgToActivity(Messenger msgToActivity) {
        this.msgToActivity = msgToActivity;
    }

    @Override
    public boolean handleMessage(Message message) {
        sendEchoMsgToActivity(message);
        return false;
    }

    private void sendEchoMsgToActivity(Message message) {
        if (msgToActivity == null) {
            return;
        }
        try {
            Message msg = Message.obtain(message);
            msgToActivity.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
