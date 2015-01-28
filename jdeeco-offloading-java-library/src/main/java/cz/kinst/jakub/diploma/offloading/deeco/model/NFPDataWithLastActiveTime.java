package cz.kinst.jakub.diploma.offloading.deeco.model;

import java.io.Serializable;

/**
 * Created by jakubkinst on 28/01/15.
 */
public class NFPDataWithLastActiveTime implements Serializable{
    private long lastActiveTime;
    private NFPData nfpData;

    public NFPDataWithLastActiveTime(NFPData nfpData, long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
        this.nfpData = nfpData;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public NFPData getNfpData() {
        return nfpData;
    }
}
