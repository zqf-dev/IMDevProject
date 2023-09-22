package com.zqf.imdevproject.network;

public class Net {
    private static Net netReq;

    public Net() {
    }

    public static Net newInstance() {
        if (netReq == null) {
            synchronized (Net.class) {
                netReq = new Net();
            }
        }

        return netReq;
    }
}
