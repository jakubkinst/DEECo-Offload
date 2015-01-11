package cz.kinst.jakub.diploma.deecooffload;


import android.os.Environment;

import org.restlet.representation.Representation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @Override
    public Message testFile(Representation fileRepresentation) {
        byte[] file = null;
        try {
            file = getFileContent(fileRepresentation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "received.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            bos.write(file);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Message("Received file " + file.length + " at " + android.os.Build.MODEL + "!", new Date().getTime());
    }

}
