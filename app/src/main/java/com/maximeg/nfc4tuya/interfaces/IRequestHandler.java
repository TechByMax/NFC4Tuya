package com.maximeg.nfc4tuya.interfaces;

import com.maximeg.nfc4tuya.enums.RequestEnum;

public interface IRequestHandler {
    void onRequestCompleted(Object result, RequestEnum request);
    void onRequestError(String message, RequestEnum request);
}
