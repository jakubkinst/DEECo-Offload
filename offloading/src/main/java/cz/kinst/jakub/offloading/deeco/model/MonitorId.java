package cz.kinst.jakub.offloading.deeco.model;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class MonitorId {
    public String appComponentId;
    public String deviceIp;

    public MonitorId(String appComponentId, String deviceIp) {
        this.appComponentId = appComponentId;
        this.deviceIp = deviceIp;
    }
}
