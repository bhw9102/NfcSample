package com.nolgong.nfcsample.nfc;

import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;

import com.acs.smartcard.Reader;

public class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

    private Reader reader;

    public OpenTask(Reader reader) {
        this.reader = reader;
    }

    @Override
    protected Exception doInBackground(UsbDevice... usbDevices) {
        Exception result = null;

        try {
            reader.open(usbDevices[0]);
        } catch (Exception e) {
            result = e;
        }

        return result;
    }

    @Override
    protected void onPostExecute(Exception e) {
        if(e != null){

        } else {

        }
    }


}
