package cz.kinst.jakub.offloading.resource;

import com.google.gson.Gson;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakubkinst on 11/01/15.
 */
public class MultipartHolder<E> {

    public static final String MULTIPART_FILE_PART = "file";
    private static final String MULTIPART_PARAMS_PART = "params";

    private List<FileItem> receivedFiles;
    private List<File> filesToSend;
    private MediaType fileMediaType;
    private E payload;

    public MultipartHolder(List<File> files, MediaType fileMediaType, E payload) {
        this.filesToSend = files;
        this.payload = payload;
        this.fileMediaType = fileMediaType;
    }

    public MultipartHolder(Representation representation, Class<E> payloadClass) throws FileUploadException {
        List<FileItem> files = MultipartHolder.getFiles(representation);
        receivedFiles = new ArrayList<>();
        for (FileItem file : files) {
            if (file.getFieldName().startsWith(MULTIPART_FILE_PART)) {
                receivedFiles.add(file);
            } else if (file.getFieldName().equals(MULTIPART_PARAMS_PART)) {
                String json = file.getString();
                this.payload = new Gson().fromJson(json, payloadClass);
            }
        }
    }

    private static byte[] readFile(Representation valueRepresentation) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        valueRepresentation.write(outputStream);
        return outputStream.toByteArray();
    }

    public List<File> getFilesToSend() {
        return filesToSend;
    }

    public List<FileItem> getReceivedFiles() {
        return receivedFiles;
    }

    public E getPayload() {
        return payload;
    }

    public MediaType getFileMediaType() {
        return fileMediaType;
    }

    public FormDataSet getForm() {

        FormDataSet form = new FormDataSet();
        form.setMultipart(true);
        int i = 0;
        for (File file : getFilesToSend()) {
            Representation fileRepresentation = new FileRepresentation(file, getFileMediaType());
            form.getEntries().add(new FormData(MULTIPART_FILE_PART + "_" + i++, fileRepresentation));
        }

        try {
            String json = new Gson().toJson(getPayload());
            File tmpFile = File.createTempFile("params", ".tmp");
            PrintWriter fos = new PrintWriter(tmpFile);
            fos.print(json);
            fos.close();
            form.getEntries().add(new FormData(MULTIPART_PARAMS_PART, new FileRepresentation(tmpFile, MediaType.TEXT_ALL)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return form;
    }

    public static List<FileItem> getFiles(Representation representation) throws FileUploadException {
        RestletFileUpload upload = new RestletFileUpload(new DefaultFileItemFactory());
        return upload.parseRepresentation(representation);
    }
}
