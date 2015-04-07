package cz.kinst.jakub.diploma.offloading.backend;

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
 * Helper class providing ability to send multiple files with arbitrary payload over the Restlet HTTP interface.
 * Uses Multipart Content-Type HTTP Header
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class MultipartHolder<E> {

	public static final String MULTIPART_FILE_PART = "file";
	private static final String MULTIPART_PARAMS_PART = "params";

	private List<FileItem> receivedFiles;
	private List<File> filesToSend;
	private MediaType fileMediaType;
	private E payload;


	/**
	 * Construct MultipartHolder from a list of files and a payload
	 *
	 * @param files         Files to upload
	 * @param fileMediaType MIME type of uploaded files
	 * @param payload       Custom serializable payload
	 */
	public MultipartHolder(List<File> files, MediaType fileMediaType, E payload) {
		this.filesToSend = files;
		this.payload = payload;
		this.fileMediaType = fileMediaType;
	}


	/**
	 * Construct MultipartHolder from incoming representation on the server side
	 *
	 * @param representation representation of HTTP request body
	 * @param payloadClass   class of the payload to deserialize
	 * @throws FileUploadException
	 */
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


	/**
	 * Static method used to read a file from {@link org.restlet.representation.Representation}
	 *
	 * @param valueRepresentation representation with file content
	 * @return file in form of byte array
	 * @throws IOException
	 */
	private static byte[] readFile(Representation valueRepresentation) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		valueRepresentation.write(outputStream);
		return outputStream.toByteArray();
	}


	/**
	 * Parse files from {@link org.restlet.representation.Representation}
	 *
	 * @param representation representation to parse
	 * @return List of Files
	 * @throws FileUploadException
	 */
	public static List<FileItem> getFiles(Representation representation) throws FileUploadException {
		RestletFileUpload upload = new RestletFileUpload(new DefaultFileItemFactory());
		return upload.parseRepresentation(representation);
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


	/**
	 * Build a representation that can be sent via Restlet HTTP interface
	 *
	 * @return
	 */
	public Representation getRepresentation() {
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
}
