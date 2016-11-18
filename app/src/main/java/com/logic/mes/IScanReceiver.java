package com.logic.mes;

public interface IScanReceiver {
    void scanReceive(String res, int scanCode);

    void scanError();
}
