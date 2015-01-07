package cz.kinst.jakub.diploma.deecooffload;


import java.util.Date;

public class HelloResourceImpl extends ResourceImpl implements HelloResource {
    public HelloResourceImpl() {
    }

    public HelloResourceImpl(String uri, int port) throws Exception {
        super(uri, port);
    }

    @Override
    public Message getHello(String name) {
        return new Message("Hello to " + name + " from " + android.os.Build.MODEL + "!", new Date().getTime());
    }

}
