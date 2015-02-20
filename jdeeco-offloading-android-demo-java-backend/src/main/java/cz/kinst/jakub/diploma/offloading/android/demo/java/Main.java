package cz.kinst.jakub.diploma.offloading.android.demo.java;

import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.logger.JavaLogProvider;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.udpbroadcast.JavaUDPBroadcast;

public class Main {
    private static final String HELLO_URI = "/hello";
    private static OffloadingManager mOffloadingManager;
    private static HelloBackendImpl mHelloResource;

    public static void main(String[] args){

        Logger.setProvider(new JavaLogProvider());
        try {
            mOffloadingManager = OffloadingManager.createInstance(new JavaUDPBroadcast(), "hello");

            mHelloResource = new HelloBackendImpl(HELLO_URI);
            mOffloadingManager.attachBackend(mHelloResource, HelloBackend.class);
            mOffloadingManager.init(OffloadingManager.MODE_ONLY_BACKEND);
            mOffloadingManager.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
