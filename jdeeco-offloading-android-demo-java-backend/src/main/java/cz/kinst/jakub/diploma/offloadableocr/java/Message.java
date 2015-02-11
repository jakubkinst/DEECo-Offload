package cz.kinst.jakub.diploma.offloadableocr.java;

/**
* Created by jakubkinst on 07/01/15.
*/
class Message {
    public String message;
    public long timestamp;

    Message(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
