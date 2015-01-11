package cz.kinst.jakub.offloading;

import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by jakubkinst on 07/01/15.
 */
public abstract class OffloadingResourceImpl extends ServerResource {


    private String mPath;

    public OffloadingResourceImpl() {
    }

    public OffloadingResourceImpl(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public static byte[] getFileContent(Representation fileRepresentation) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        fileRepresentation.write(outputStream);
        return outputStream.toByteArray();
    }
}
