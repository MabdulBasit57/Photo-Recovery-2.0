package com.decentapps.supre.photorecovery.datarecovery.utils;

public class MyProperties {
    private static MyProperties mInstance;
    public boolean AppInForeground = false;
    public String AppVersion = "1.2";
    public String BaseURL = "https://ai.narmx.com";
    public boolean IsPassThrowStartClass = false;
    public boolean JaCnf = false;
    public boolean NotRateThisTime = false;
    public String Store = "0";

    protected MyProperties() {
    }

    public static synchronized MyProperties getInstance() {
        MyProperties myProperties;
        synchronized (MyProperties.class) {
            if (mInstance == null) {
                mInstance = new MyProperties();
            }
            myProperties = mInstance;
        }
        return myProperties;
    }
}
