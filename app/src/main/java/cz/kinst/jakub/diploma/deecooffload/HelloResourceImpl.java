package cz.kinst.jakub.diploma.deecooffload;


import java.util.Date;

import cz.kinst.jakub.offloading.OffloadingResourceImpl;

public class HelloResourceImpl extends OffloadingResourceImpl implements HelloResource {
    public HelloResourceImpl() {
    }

    public HelloResourceImpl(String path) {
        super(path);
    }

    @Override
    public Message getHello(String name) {
        return new Message("Hello to " + name + " from " + android.os.Build.MODEL + "!", new Date().getTime());
    }

    @Override
    public Message getHi(String name) {
        return new Message("Hi to " + name + " from " + android.os.Build.MODEL + "!", new Date().getTime());
    }

}
