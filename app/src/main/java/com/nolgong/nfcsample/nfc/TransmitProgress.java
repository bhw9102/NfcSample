package com.nolgong.nfcsample.nfc;

public class TransmitProgress {
    public int controlCode;
    public byte[] command;
    public int commandLength;
    public byte[] response;
    public int responseLength;
    public Exception e;
}
