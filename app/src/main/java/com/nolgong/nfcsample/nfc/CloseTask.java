package com.nolgong.nfcsample.nfc;

import android.os.AsyncTask;

import com.acs.smartcard.Reader;

public class CloseTask extends AsyncTask<Void, Void, Void> {

    private Reader reader;

    public CloseTask(Reader reader) {
        this.reader = reader;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        reader.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
