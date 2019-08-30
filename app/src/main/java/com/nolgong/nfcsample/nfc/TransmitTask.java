package com.nolgong.nfcsample.nfc;

import android.os.AsyncTask;

import com.acs.smartcard.Reader;

public class TransmitTask extends AsyncTask<TransmitParams, TransmitProgress, Void> {

    private Reader reader;
    private int slotNum = -1;

    public TransmitTask(Reader reader) {
        this.reader = reader;
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

        }
    }
}
