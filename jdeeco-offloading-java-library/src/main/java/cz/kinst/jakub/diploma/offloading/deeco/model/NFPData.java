package cz.kinst.jakub.diploma.offloading.deeco.model;

import java.io.Serializable;

/**
 * Created by jakubkinst on 21/01/15.
 */
public abstract class NFPData implements Serializable {
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {

        this.timestamp = timestamp;
    }
}
