package cz.kinst.jakub.diploma.offloading.android.demo.java;


import org.restlet.representation.Representation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

import cz.kinst.jakub.diploma.offloading.model.NFPData;
import cz.kinst.jakub.diploma.offloading.model.SingleValueNFPData;
import cz.kinst.jakub.diploma.offloading.backend.BackendPerformanceProvider;
import cz.kinst.jakub.diploma.offloading.backend.MultipartHolder;
import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackendImpl;

public class HelloBackendImpl extends OffloadableBackendImpl implements HelloBackend {

    public HelloBackendImpl() {
    }

    public HelloBackendImpl(String path) {
        super(path, new BackendPerformanceProvider() {
            @Override
            public NFPData checkPerformance() {
                return new SingleValueNFPData(10);
            }

            @Override
            public String findOptimalAlternative(Map<String, NFPData> alternatives) {
                String bestAlternative = null;
                float max = Float.MIN_VALUE;
                for (String key : alternatives.keySet()) {
                    if (bestAlternative == null)
                        bestAlternative = key;
                    SingleValueNFPData nfpData = (SingleValueNFPData) alternatives.get(key);
                    if (nfpData.getPerformance() > max) {
                        max = nfpData.getPerformance();
                        bestAlternative = key;
                    }
                }
                return bestAlternative;
            }
        });
    }

    @Override
    public Message getHello(String name) {
        int count = getStateData().getInt("hello_counter", 0) + 1;
        getStateData().putInt("hello_counter", count);
        return new Message("Hello no. " + count + " to " + name + " from Desktop!", new Date().getTime());
    }

    @Override
    public Message getHi(String name) {
        return new Message("Hi to " + name + " from Desktop!", new Date().getTime());
    }

    @Override
    public Message testFile(Representation representation) {
        getContext().getAttributes().put("testVal", "XXX");
        try {
            MultipartHolder<Message> multipartHolder = new MultipartHolder<>(representation, Message.class);
            byte[] file = multipartHolder.getReceivedFiles().get(0).get();

            File outputFile = new File("received.jpg");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            bos.write(file);
            bos.flush();
            bos.close();
            return new Message("Received file of size" + file.length + " with param " + multipartHolder.getPayload().message + " at Desktop!", new Date().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
