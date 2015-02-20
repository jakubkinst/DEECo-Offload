package cz.kinst.jakub.diploma.offloading.listeners;

/**
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public interface OnBackendMoveListener {

    /**
     * Backend with backendId started moving from fromAddress to toAddress
     *
     * @param backendId   Backend ID
     * @param fromAddress
     * @param toAddress
     */
    public void onBackendMovingStarted(String backendId, String fromAddress, String toAddress);

    /**
     * Backend with backendId is done moving from fromAddress to toAddress
     *
     * @param backendId   Backend ID
     * @param fromAddress
     * @param toAddress
     */
    public void onBackendMovingDone(String backendId, String fromAddress, String toAddress);
}
