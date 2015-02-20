package cz.kinst.jakub.diploma.offloading.events;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
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
