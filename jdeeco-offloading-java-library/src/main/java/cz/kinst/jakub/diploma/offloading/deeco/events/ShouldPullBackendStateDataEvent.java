package cz.kinst.jakub.diploma.offloading.deeco.events;

/**
 * Created by jakubkinst on 06/02/15.
 */
public class ShouldPullBackendStateDataEvent {
    private String backendId;
    private String backendAddress;

    public ShouldPullBackendStateDataEvent(String backendId, String backendAddress) {
        this.backendId = backendId;
        this.backendAddress = backendAddress;
    }

    public String getBackendId() {
        return backendId;
    }

    public String getBackendAddress() {
        return backendAddress;
    }
}
