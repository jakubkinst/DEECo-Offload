package cz.kinst.jakub.diploma.offloadableocr.java.offloading;

import java.io.Serializable;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class OCRResult implements Serializable {
    private String recognizedText;
    private long duration;
    private long timestamp;
    private String deviceIp;

    public OCRResult(String recognizedText, String deviceIp, long duration) {
        this.recognizedText = recognizedText;
        this.duration = duration;
        this.deviceIp = deviceIp;
        timestamp = System.currentTimeMillis();
    }

    public OCRResult(String recognizedText, String deviceIp) {
        this(recognizedText, deviceIp, -1);
    }

    public String getRecognizedText() {
        return recognizedText;
    }

    public long getDuration() {
        return duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDeviceIp() {
        return deviceIp;
    }
}
