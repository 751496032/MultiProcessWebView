// ICallbackFromMainToWebInterface.aidl
package com.hongYi.h5container;

// Declare any non-default types here with import statements

interface ICallbackFromMainToWebInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

     void handleWebCallback(String jsCallbackNameKey, String result);

     void unregisterWebCallback(String jsCallbackNameKey);
}
