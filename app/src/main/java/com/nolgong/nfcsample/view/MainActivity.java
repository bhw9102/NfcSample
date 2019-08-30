package com.nolgong.nfcsample.view;

import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nolgong.nfcsample.BaseApplication;
import com.nolgong.nfcsample.R;
import com.nolgong.nfcsample.nfc.Value;

public class MainActivity extends AppCompatActivity implements Handler.Callback{

    private Spinner spinnerNfc;
    private ArrayAdapter<String> adapterNfc;
    private TextView textViewLog;
    private Messenger messenger = new Messenger(new Handler(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_find_nfc).setOnClickListener(buttonFindNfc);
        findViewById(R.id.button_connect_nfc).setOnClickListener(buttonConnectNfc);
        textViewLog = findViewById(R.id.textview_log);

        onCreateSpinner();

        BaseApplication.INSTANCE.nfcHandler.setMsgToActivity(messenger);
    }

    /**
     * 스피너와 스피터의 어뎁터 세팅
     */
    private void onCreateSpinner(){
        adapterNfc = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        spinnerNfc = findViewById(R.id.spinner_nfc);
        spinnerNfc.setAdapter(adapterNfc);
    }

    /**
     * usb 로 연결된 NFC 리더기를 조회하는 버튼 리스너
     */
    View.OnClickListener buttonFindNfc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapterNfc.clear();
            for(UsbDevice device : BaseApplication.INSTANCE.usbManager.getDeviceList().values()){
                if(BaseApplication.INSTANCE.reader.isSupported(device)){
                    adapterNfc.add(device.getDeviceName());
                }
            }
        }
    };

    /**
     * NFC 리더기 목록에서 선택된 대상과 연결하는 버튼 리스터
     */
    View.OnClickListener buttonConnectNfc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String deviceName = (String) spinnerNfc.getSelectedItem();
            if(deviceName == null){
                return;
            }
            for(UsbDevice device : BaseApplication.INSTANCE.usbManager.getDeviceList().values()){
                if(deviceName.equals(device.getDeviceName())){
                    BaseApplication.INSTANCE.usbManager.requestPermission(device, BaseApplication.INSTANCE.permissionIntent);
                    break;
                }
            }
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case Value.SEND_TO_ACTIVITY_FROM_NFC:
                String nfcValue = msg.getData().getString(Value.NFC_MESSAGE);
                textViewLog.append("\n" + nfcValue);
                break;
            default:

        }
        return false;
    }
}
