package cz.kinst.jakub.diploma.offloading.android.demo;


import android.os.Build;
import android.os.Environment;

import org.restlet.representation.Representation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

import cz.kinst.jakub.diploma.offloading.deeco.model.NFPData;
import cz.kinst.jakub.diploma.offloading.deeco.model.SimpleValueNFPData;
import cz.kinst.jakub.diploma.offloading.resource.MultipartHolder;
import cz.kinst.jakub.diploma.offloading.resource.OffloadingResourceImpl;
import cz.kinst.jakub.diploma.offloading.resource.ResourcePerformanceChecker;

public class HelloResourceImpl extends OffloadingResourceImpl implements HelloResource {
    public HelloResourceImpl() {
    }

    public HelloResourceImpl(String path) {
        super(path, new ResourcePerformanceChecker() {
            @Override
            public NFPData checkPerformance() {
                return new SimpleValueNFPData(Build.MODEL.length());
            }

            @Override
            public String findOptimalAlternative(Map<String, NFPData> alternatives) {
                String bestAlternative = null;
                float max = Float.MIN_VALUE;
                for (String key : alternatives.keySet()) {
                    if (bestAlternative == null)
                        bestAlternative = key;
                    SimpleValueNFPData nfpData = (SimpleValueNFPData) alternatives.get(key);
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
        return new Message("Hello to " + name + " from " + android.os.Build.MODEL + "!", new Date().getTime());
    }

    @Override
    public Message getHi(String name) {
        return new Message("Hi to " + name + " from " + android.os.Build.MODEL + "!", new Date().getTime());
    }

    @Override
    public Message testFile(Representation representation) {
        try {
            MultipartHolder<Message> multipartHolder = new MultipartHolder<>(representation, Message.class);
            byte[] file = multipartHolder.getReceivedFiles().get(0).get();

            File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "received.jpg");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            bos.write(file);
            bos.flush();
            bos.close();
            return new Message("Received file of size" + file.length + " with param " + multipartHolder.getPayload().message + " at " + android.os.Build.MODEL + "!", new Date().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
