package com.nolgong.nfcsample.nfc;

import java.util.ArrayList;

public class Tool {

    public static String readNfcValue(byte[] apdu){
        if(apdu == null){
            return null;
        }
        if(apdu[16] != -122){
            return null;
        }
        buildNdefMessage(apdu);
    }

    public static ArrayList<Byte> buildNdefMessage(byte[] apdu){
        int tlvTag = -1;
        int tlvLength = -1;
        ArrayList<Byte> ndefMessage = new ArrayList<>();
        boolean ndefFlag = false;
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
                ndefFlag = true;
                break;
            }
        }
        return ndefMessage;
    }

    /**
     * Converts the HEX string to byte array.
     *
     * @param hexString
     *            the HEX string.
     * @return the byte array.
     */
    public static byte[] toByteArray(String hexString) {
        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {
            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {
                if (first) {
                    byteArray[len] = (byte) (value << 4);
                } else {
                    byteArray[len] |= value;
                    len++;
                }
                first = !first;
            }
        }
        return byteArray;
    }

    /**
     * Converts the integer to HEX string.
     *
     * @param i
     *            the integer.
     * @return the HEX string.
     */
    public static String toHexString(int i) {
        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        return hexString.toUpperCase();
    }

    /**
     * Converts the byte array to HEX string.
     *
     * @param buffer
     *            the buffer.
     * @return the HEX string.
     */
    public static String toHexString(byte[] buffer) {
        String bufferString = "";

        for (int i = 0; i < buffer.length; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase() + " ";
        }
        return bufferString;
    }
}
