package cz.kinst.jakub.diploma.offloading;

/**
 * Created by jakubkinst on 06/02/15.
 */
public interface OnBackendMoveListener {
    public void onBackendMovingStarted(String backendId, String fromAddress, String toAddress);

    public void onBackendMovingDone(String backendId, String fromAddress, String toAddress);
}
