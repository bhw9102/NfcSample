package com.nolgong.nfcsample;

import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.acs.smartcard.Features;
import com.acs.smartcard.Reader;
import com.nolgong.nfcsample.nfc.CloseTask;
import com.nolgong.nfcsample.nfc.NfcHandler;
import com.nolgong.nfcsample.nfc.OpenTask;
import com.nolgong.nfcsample.nfc.TransmitParams;
import com.nolgong.nfcsample.nfc.TransmitTask;
import com.nolgong.nfcsample.nfc.Value;

public class BaseApplication extends Application {
    public static BaseApplication INSTANCE = null;
    public NfcHandler nfcHandler = null;

    //nfc 리더기 장치 연결과 관련된 변수
    public Features features = new Features();
    public UsbManager usbManager;
    public Reader reader;
    public PendingIntent permissionIntent;

    //nfc 리더기를 활용하기 위해 전역적으로 쓰이는 변수
    public String command;
    public int commandCnt;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = BaseApplication.this;
        nfcHandler = new NfcHandler();

        command = Value.COMMAND_FIRST;
        commandCnt = 0;

        onCreateNfc();
    }

    public String getCommandByCnt(){
        if(commandCnt == 0){
            commandCnt++;
            command = Value.COMMAND_SECOND;
        } else if(commandCnt == 1){
            commandCnt++;
            command = Value.COMMAND_THIRD;
        }

        return command;
    }

    private void onCreateNfc(){
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        reader = new Reader(usbManager);
        reader.setOnStateChangeListener(stateChangeListener);

        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Value.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Value.ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(receiver, filter);
    }

    private Reader.OnStateChangeListener stateChangeListener = new Reader.OnStateChangeListener() {
        @Override
        public void onStateChange(int slotNum, int prevState, int currState) {
            if(prevState < Reader.CARD_UNKNOWN || prevState > Reader.CARD_SPECIFIC){
                prevState = Reader.CARD_UNKNOWN;
            }
            if(currState < Reader.CARD_UNKNOWN || currState > Reader.CARD_SPECIFIC){
                currState = Reader.CARD_UNKNOWN;
            }
            if(currState == 2){
                executeTransmitTask();
            }
        }
    };

    private void executeTransmitTask(){
        commandCnt = 0;
        command = Value.COMMAND_FIRST;

        new Thread(new Runnable() {
            @Override
            public void run() {
                TransmitParams params = new TransmitParams();
                params.slotNum = 0;
                params.controlCode = Reader.IOCTL_CCID_ESCAPE;
                params.commandString = Value.COMMAND_FIRST;

                new TransmitTask(reader, nfcHandler.msgPostBox).execute(params);
            }
        }).start();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Value.ACTION_USB_PERMISSION.equals(action)){
                actOpen(intent);
            } else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
                actClose(intent);
            }
        }
    };

    private void actOpen(Intent intent){
        synchronized (this){
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if(!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                return;
            }
            if(device == null){
                return;
            }
            new OpenTask(reader).execute(device);
        }
    }

    private void actClose(Intent intent){
        synchronized (this){
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if(device == null){
                return;
            }
            if(!device.equals(reader.getDevice())){
                return;
            }
            new CloseTask(reader).execute();
        }
    }
}
