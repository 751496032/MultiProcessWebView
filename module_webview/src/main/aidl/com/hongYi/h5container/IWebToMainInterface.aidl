// IWebToMainInterface.aidl
package com.hongYi.h5container;

// Declare any non-default types here with import statements
import com.hongYi.h5container.ICallbackFromMainToWebInterface;

interface IWebToMainInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

 void handleWebCommand(String commandName, String params , in ICallbackFromMainToWebInterface callback);

}
