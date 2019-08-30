package com.nolgong.nfcsample.nfc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.acs.smartcard.Reader;
import com.nolgong.nfcsample.BaseApplication;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TransmitTask extends AsyncTask<TransmitParams, TransmitProgress, Void> {

    private Reader reader;
    private int slotNum = -1;

    private Messenger msgToActivity;

    public TransmitTask(Reader reader, Messenger msgToActivity) {
        this.reader = reader;
        this.msgToActivity = msgToActivity;
    }

    @Override
    protected Void doInBackground(TransmitParams... transmitParams) {
        TransmitProgress progress = null;
        byte[] command = null;
        byte[] response = null;
        int responseLength = 0;
        int foundIndex = 0;
        int startIndex = 0;

        slotNum = transmitParams[0].slotNum;
        do {
            // Find carriage return
            foundIndex = transmitParams[0].commandString.indexOf('\n', startIndex);
            if(foundIndex >= 0) {
                command = Tool.toByteArray(transmitParams[0].commandString.substring(startIndex, foundIndex));
            } else {
                command = Tool.toByteArray(transmitParams[0].commandString.substring(startIndex));
            }

            // Set next start index
            startIndex = foundIndex + 1;

            response = new byte[300];
            progress = new TransmitProgress();
            progress.controlCode = transmitParams[0].controlCode;

            try {
                if (transmitParams[0].controlCode < 0) {

                    // Transmit APDU
                    responseLength = reader.transmit(transmitParams[0].slotNum,
                            command, command.length, response,
                            response.length);

                } else {
                    // Transmit control command
                    responseLength = reader.control(transmitParams[0].slotNum,
                            transmitParams[0].controlCode, command, command.length,
                            response, response.length);
                }

                progress.command = command;
                progress.commandLength = command.length;
                progress.response = response;
                progress.responseLength = responseLength;
                progress.e = null;
            } catch (Exception e){
                progress.command = null;
                progress.commandLength = 0;
                progress.response = null;
                progress.responseLength = 0;
                progress.e = e;
            }
            publishProgress(progress);

        } while (foundIndex >= 0);

        return null;
    }

    @Override
    protected void onProgressUpdate(TransmitProgress... values) {
        if(values[0].e != null){

        } else {
            ArrayList<Byte> ndefValue = readNdefValue(values[0].response);
            if(ndefValue == null){
                return ;
            }
            String value = buildNfcText(ndefValue);
            sendMsgToActivity(value);
        }
    }

    private ArrayList<Byte> readNdefValue(byte[] apdu){
        if(apdu == null){
            return null;
        }
        byte b = -112;
        if(apdu[16] != b){
            return null;
        }
        return buildNdefMessage(apdu);
    }

    private ArrayList<Byte> buildNdefMessage(byte[] apdu){
        int tlvTag = -1;
        int tlvLength = -1;
        ArrayList<Byte> ndefMessage = new ArrayList<>();
        for(int i=0;i<16;i++){
            if(tlvTag == -1){
                tlvTag = apdu[i];
                continue;
            } else if (tlvLength == -1){
                tlvLength = apdu[i];
                continue;
            }

            if(tlvTag == 1){
                i += tlvLength -1;
                tlvTag = -1;
                tlvLength = -1;
                continue;
            } else if (tlvTag == -2){
                return null;
            }

            if(ndefMessage.size() < tlvLength){
                ndefMessage.add(apdu[i]);
            } else if(tlvLength == 0){
                tlvTag = -1;
                tlvLength = -1;
                i--;
            } else {
                doNdefFlagFalse();
                return null;
            }
        }
        return ndefMessage;
    }

    private void doNdefFlagFalse(){
        final TransmitParams params = new TransmitParams();
        params.slotNum = slotNum;
        params.controlCode = Reader.IOCTL_CCID_ESCAPE;
        params.commandString = BaseApplication.INSTANCE.getCommandByCnt();

        new Thread(new Runnable() {
            @Override
            public void run() {
                new TransmitTask(reader, msgToActivity).execute(params);
            }
        }).start();
    }

    private String buildNfcText(ArrayList<Byte> ndefMessage){
        String str = "";
        byte[] result_item = new byte[ndefMessage.size()];
        for (int k = 0; k < ndefMessage.size(); k++) {
            result_item[k] = ndefMessage.get(k);
        }

        int index = 1;
        byte type_length = result_item[index++];
        byte payload_length = result_item[index++];
        byte[] type = new byte[type_length];
        for (int i = 0; i < type_length; i++) {
            type[i] = result_item[index++];
        }
        if (type_length == 1 && type[0] == 84) {
            byte language_length = (byte) (result_item[index++] & 15);
            byte[] language_code = new byte[language_length];
            for (int i = 0; i < language_length; i++) {
                language_code[i] = result_item[index++];
            }
            byte[] payload = new byte[payload_length - language_length - 1];
            for (int i = 0; index < result_item.length; i++, index++) {
                payload[i] = result_item[index];
            }
            try {
                if ((language_length & 128) == 0) {
                    str = new String(payload, "UTF-8");
                } else {
                    str = new String(payload, "UTF-16");
                }
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        return str;
    }

    private void sendMsgToActivity(String value){
        if(msgToActivity == null){
            return;
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putString(Value.NFC_MESSAGE, value);
            Message msg = Message.obtain(null, Value.SEND_TO_ACTIVITY_FROM_NFC);
            msg.setData(bundle);
            msgToActivity.send(msg);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }
}
