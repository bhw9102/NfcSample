package com.nolgong.nfcsample.nfc;

public class Value {
    public static String COMMAND_FIRST = "ff b0 00 04 10";
    public static String COMMAND_SECOND = "ff b0 00 08 10";
    public static String COMMAND_THIRD = "ff b0 00 0c 10";

    public static String NFC_MESSAGE = "nfc message";
    public static final int SEND_TO_ACTIVITY_FROM_NFC = 1;

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final String[] powerActionStrings = { "Power Down", "Cold Reset", "Warm Reset" };

    public static final String[] stateStrings = { "Unknown", "Absent",
            "Present", "Swallowed", "Powered", "Negotiable", "Specific" };

    public static final String[] featureStrings = { "FEATURE_UNKNOWN",
            "FEATURE_VERIFY_PIN_START", "FEATURE_VERIFY_PIN_FINISH",
            "FEATURE_MODIFY_PIN_START", "FEATURE_MODIFY_PIN_FINISH",
            "FEATURE_GET_KEY_PRESSED", "FEATURE_VERIFY_PIN_DIRECT",
            "FEATURE_MODIFY_PIN_DIRECT", "FEATURE_MCT_READER_DIRECT",
            "FEATURE_MCT_UNIVERSAL", "FEATURE_IFD_PIN_PROPERTIES",
            "FEATURE_ABORT", "FEATURE_SET_SPE_MESSAGE",
            "FEATURE_VERIFY_PIN_DIRECT_APP_ID",
            "FEATURE_MODIFY_PIN_DIRECT_APP_ID", "FEATURE_WRITE_DISPLAY",
            "FEATURE_GET_KEY", "FEATURE_IFD_DISPLAY_PROPERTIES",
            "FEATURE_GET_TLV_PROPERTIES", "FEATURE_CCID_ESC_COMMAND" };

    public static final String[] propertyStrings = { "Unknown", "wLcdLayout",
            "bEntryValidationCondition", "bTimeOut2", "wLcdMaxCharacters",
            "wLcdMaxLines", "bMinPINSize", "bMaxPINSize", "sFirmwareID",
            "bPPDUSupport", "dwMaxAPDUDataSize", "wIdVendor", "wIdProduct" };
}
